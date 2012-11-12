package me.coldandtired.mobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.listeners.*;
import me.coldandtired.mobs.managers.*;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
	private Event_listener event_listener;
	private static XPath xpath;
	private static int log_level = 2;
	
	@Override
	public void onEnable()
	{		
		instance = this;
		checkConfig();
		if (!load_config()) setEnabled(false);
	}

	public static XPath getXPath()
	{
		return xpath;
	}
	
	private void checkConfig()
	{
		InputStream inputstream = null;
		OutputStream out = null;
		try
		{
			File f = getDataFolder();
			if (!f.exists()) f.mkdir();
			f = new File(getDataFolder(), "config.txt");
			if (f.exists()) f = new File(getDataFolder(), "example.txt");
			inputstream = getClass().getClassLoader().getResourceAsStream("config.txt");
			if (inputstream == null) return;
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
	
	/** loads the config file and splits it into the relevant objects */
	boolean load_config()
	{
		xpath = XPathFactory.newInstance().newXPath();
		error("This is a Beta version of the plugin.  Make sure you have backups!");
		File f = new File(getDataFolder(), "config.txt");
		InputSource input = null;
		try
		{
			input = new InputSource(f.getPath());
			Element main = (Element)xpath.evaluate("mobs", input, XPathConstants.NODE);
			loadSettings((Element)xpath.evaluate("settings", main, XPathConstants.NODE));
			
			Target_manager.get().importAreas(xpath, (NodeList)xpath.evaluate("mobs/areas/*", input, XPathConstants.NODESET));
			
			event_listener = new Event_listener(main);

			getServer().getPluginManager().registerEvents(event_listener, this);		
		}
		catch (Exception e) {error("Something went wrong loading the config - stopping!"); e.printStackTrace(); return false;}
		return true;
	}
	
	private void loadSettings(Element el)
	{
		//boolean disable_check = false;
		if (el != null)	
		{
			if (el.hasAttribute("log_level")) log_level = Integer.parseInt(el.getAttribute("log_level"));
			//if (el.hasAttribute("disable_update_check")) disable_check = Boolean.parseBoolean(el.getAttribute("disable_update_check"));
			if (el.hasAttribute("ignored_worlds"))
			{
				List<String> temp = Arrays.asList(el.getAttribute("ignored_worlds").replace(" ", "").split(","));
				for (World w : Bukkit.getWorlds())
				{
					Data.removeData(w, MParam.IGNORED_WORLD);
					if (temp.contains(w.getName())) Data.putData(w, MParam.IGNORED_WORLD);
				}
			}
		}
		
		if (getServer().getPluginManager().getPlugin("Vault") != null)
		{
	        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) economy = economyProvider.getProvider();
		}
		
		try 
		{
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} 
		catch (IOException e) 
		{
		    error("Something went wrong with Metrics - it will be disabled.");
		}
		
		//if (!disable_check && !is_latest_version()) log("There's a newer version of Mobs available!");
	}
		
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("reload_mobs"))
		{
			getServer().getScheduler().cancelTasks(this);
			event_listener = null;
			HandlerList.unregisterAll(this);
			checkConfig();
			load_config();
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
	
	public int getLog_level()
	{
		return log_level;
	}
	
	public static Mobs getInstance()
	{
		return instance;
	}
	
	/** clean up */
	@Override
	public void onDisable()
	{
		logger = null;
		instance = null;
		event_listener = null;
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
		if (log_level < 2) return;
		
		logger.warning(Ansi.ansi().fg(Ansi.Color.RED).toString() + "[Mobs] "
				+ message + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
	}
	
	/** debug info */
	public static void debug(Object message)
	{
		if (log_level > 2) logger.info(Ansi.ansi().fg(Ansi.Color.YELLOW).toString() 
				 + "[Mobs] " + message + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
	}

	public static boolean isSpout_enabled()
	{
		return Bukkit.getServer().getPluginManager().isPluginEnabled("Spout");
	}
}