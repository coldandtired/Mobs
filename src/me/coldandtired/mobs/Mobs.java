package me.coldandtired.mobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import me.coldandtired.extra_events.Extra_events;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.listeners.*;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * the main plugin class
 *
 * @version 1.0
 * @author 	coldandtired
 */
public class Mobs extends JavaPlugin
{
	private static Logger logger = Logger.getLogger("Minecraft");
	private static Mobs instance;
	public static Economy economy = null;
	public static Extra_events extra_events;
	private Event_listener event_listener;
	private static XPath xpath;
	private static boolean show_warnings;
	private boolean debug_mode;
	private boolean include_failed;
	
	@Override
	public void onEnable()
	{		
		instance = this;
		try 
		{
			if (!load_config()) setEnabled(false);
		} 
		catch (XPathExpressionException e)
		{
			setEnabled(false);
			e.printStackTrace();
		}
	}

	public static XPath getXPath()
	{
		return xpath;
	}
		
	/** checks if the running version is the newest available (works with release versions only) */
	boolean is_latest_version()
	{
		DocumentBuilder dbf;
		try 
		{
			dbf = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dbf.parse("http://dev.bukkit.org/server-mods/mobs/files.rss");
			String s = ((Element) xpath.evaluate("//item[1]/title", doc, XPathConstants.NODE)).getTextContent();
			return (s.equalsIgnoreCase(getDescription().getVersion()));
		} 
		catch (Exception e) {return true;}		
	}
	
	/** loads the config file and splits it into the relevant objects 
	 * @throws XPathExpressionException */
	@SuppressWarnings("unchecked")
	boolean load_config() throws XPathExpressionException
	{
		xpath = XPathFactory.newInstance().newXPath();
		error("This is a Beta version of the plugin.  Make sure you have backups!");
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);		 
		saveConfig();
		
		if (config.getBoolean("generate_templates"))
		{
			for (MEvent event : MEvent.values())
			{
				InputStream inputstream = null;
				OutputStream out = null;
				try
				{
					File f = new File(getDataFolder(), event.toString().toLowerCase() + ".txt");
					if (f.exists()) continue;
					
					inputstream = getClass().getClassLoader().getResourceAsStream("template.txt");
					if (inputstream == null) return false;
					out = new FileOutputStream(f);
					byte buf[] = new byte[1024];
					int len;
					while((len = inputstream.read(buf) )>0)
					out.write(buf, 0, len);
				}
				catch (Exception e) {e.printStackTrace();}
				finally
				{
					try
					{
						if (out != null) out.close();
						if (inputstream != null) inputstream.close();
					}
					catch (Exception e) {e.printStackTrace();}
				}
			}
		}
		
		show_warnings = config.getBoolean("show_warnings");
		debug_mode = config.getBoolean("debug.enabled");
		include_failed = config.getBoolean("debug.include_failed");
		
		Set<String> temp = new HashSet<String>((Collection<? extends String>)config.getList("worlds_to_ignore"));
		for (World w : Bukkit.getWorlds())
		{
			Data.removeData(w, MParam.IGNORED_WORLD);
			if (temp != null && temp.contains(w.getName())) Data.putData(w, MParam.IGNORED_WORLD);
		}
		
		//if (!disable_check && !is_latest_version()) log("There's a newer version of Mobs available!");
		
		if (getServer().getPluginManager().getPlugin("Vault") != null)
		{
	        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) economy = economyProvider.getProvider();
		}
		
		extra_events = (Extra_events)getServer().getPluginManager().getPlugin("Extra Events");
		
		try 
		{
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} 
		catch (IOException e) 
		{
		    error("Something went wrong with Metrics - it will be disabled.");
		}
		
		event_listener = new Event_listener(new HashSet<String>((Collection<? extends String>)config.getList("debug.events_to_debug")));
		getServer().getPluginManager().registerEvents(event_listener, this);		
		return true;
	}
		
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("reload_mobs"))
		{
			getServer().getScheduler().cancelTasks(this);
			event_listener = null;
			HandlerList.unregisterAll(this);
			try
			{
				load_config();
			}
			catch (XPathExpressionException e) {e.printStackTrace();}
			sender.sendMessage("Config reloaded!");
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("repeating_outcomes"))
		{
			return event_listener.adjustRepeating_outcomes(sender, args);
		}
		return false;
	}
				
	public void setMob_name(String name)
	{
		event_listener.setMob_name(name);
	}
	
	public static Mobs getInstance()
	{
		return instance;
	}
	
	/** clean up */
	@Override
	public void onDisable()
	{
		instance = null;
		event_listener = null;
		xpath = null;
		logger = null;
		economy = null;
	}
	
	public static boolean isSpout_enabled()
	{
		return Bukkit.getServer().getPluginManager().isPluginEnabled("Spout");
	}

	public void debug(Event_report report)
	{
		if (!debug_mode) return;
		
		logger.info("");
		debug(report.getName() + " - " + report.getOutcomes().size() + " outcome(s)");
		debug("-------------");
		int i = 1;
		for (Outcome_report or : report.getOutcomes())
		{
			String s = or.getPassed_conds().size() + or.getFailed_conds().size() == 0 ? " - no conditions" : "";
			debug("Outcome " + i + s);
			if (s.equalsIgnoreCase(""))
			{			
				debug("-------------");
				if (include_failed && or.getFailed_conds().size() > 0)
				{
					debug("Failed conditions", Ansi.Color.MAGENTA);
					for (Condition_report cr : or.getFailed_conds())
					{
						debug(cr.getName());
						debug(cr.getCheck_value());
						debug(cr.getActual_value());
					}
				}
				if (or.getPassed() && or.getPassed_conds().size() > 0)
				{
					debug("Passed conditions", Ansi.Color.CYAN);
					for (Condition_report cr : or.getPassed_conds())
					{
						debug(cr.getName());
						debug(cr.getCheck_value());
						debug(cr.getActual_value());
					}
				}	
			}
			debug("-------------");
		}
	}
	
	/** debug info */
	private void debug(Object message)
	{
		logger.info(Ansi.ansi().fg(Ansi.Color.YELLOW).toString() + message + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
	}
	
	/** debug info */
	private void debug(Object message, Color colour)
	{
		logger.info(Ansi.ansi().fg(colour).toString() + message + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
	}
	
	public static void log(Object message)
	{
		logger.info(Ansi.ansi().fg(Ansi.Color.GREEN).toString() + "[Mobs] "
				+ message + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
	}
	
	/** log error */
	public static void error(Object message)
	{
		logger.warning(Ansi.ansi().fg(Ansi.Color.RED).toString() + "[Mobs] "
				+ message + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
	}
	
	/** log warning */
	public static void warn(Object message)
	{
		if (!show_warnings) return;
		
		logger.warning(Ansi.ansi().fg(Ansi.Color.RED).toString() + "[Mobs] "
				+ message + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
	}
}