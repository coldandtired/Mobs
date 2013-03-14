package me.coldandtired.mobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import me.coldandtired.extraevents.ExtraEvents;
import me.coldandtired.mobs.Enums.EventType;
import me.coldandtired.mobs.api.Data;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The main plugin class
 *
 * @version 1.4.7 b1
 * @author 	coldandtired
 */
public class Mobs extends JavaPlugin
{
	private XPath xpath;
	private Economy economy = null;
	private ExtraEvents extra_events;
	private boolean allow_debug;	
	private BukkitListener bukkit_listener = new BukkitListener();
	
	@Override
	public void onEnable()
	{	
		xpath = XPathFactory.newInstance().newXPath();
		checkVersion();
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		try 
		{
			loadConfig();
		} 
		catch (XPathExpressionException e)
		{
			setEnabled(false);
			e.printStackTrace();
			return;
		}
		
		PluginManager pm = getServer().getPluginManager();
		
		if (pm.getPlugin("Vault") != null)
		{
	        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) economy = economyProvider.getProvider();
		}
		
		extra_events = (ExtraEvents)pm.getPlugin("Extra events");
		
		try 
		{
		    new Metrics(this).start();
		} 
		catch (IOException e)
		{
		    error("Something went wrong with Metrics - it will be disabled.");
		}
		
		pm.registerEvents(bukkit_listener, this);
	}
		
	/** Checks if the running version is the newest available (works with release versions only) */
	private void checkVersion()
	{
		if (!getConfig().getBoolean("check_for_newer_version", true)) return;
		
		DocumentBuilder dbf;
		try 
		{
			dbf = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dbf.parse("http://dev.bukkit.org/server-mods/mobs/files.rss");
			String s = ((Element) xpath.evaluate("//item[1]/title", doc, XPathConstants.NODE)).getTextContent();
			if (!s.equalsIgnoreCase(getDescription().getVersion())) log("There's a more recent version available!");
		} 
		catch (Exception e) {}		
	}
	
	/** Loads the config file and splits it into the relevant objects */
	@SuppressWarnings("unchecked")
	void loadConfig() throws XPathExpressionException
	{		
		reloadConfig();
		bukkit_listener.fillEvents();
		
		if (getConfig().getBoolean("generate_templates"))
		{
			for (EventType event : EventType.values())
			{
				InputStream inputstream = null;
				OutputStream out = null;
				try
				{
					File f = new File(getDataFolder(), event.toString().toLowerCase() + ".txt");
					if (f.exists()) continue;
					
					inputstream = getClass().getClassLoader().getResourceAsStream("template.txt");
					if (inputstream == null) break;
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
		
		Set<String> temp = new HashSet<String>((Collection<? extends String>)getConfig().getList("worlds_to_ignore"));
		for (World w : Bukkit.getWorlds())
		{
			Data.removeData(w, "IGNORED_WORLD");
			if (temp != null && temp.contains(w.getName())) Data.putData(w, "IGNORED_WORLD");
		}
		
		allow_debug = getConfig().getBoolean("allow_debug", false);		
	}
		
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("reload_mobs"))
		{			
			try
			{
				loadConfig();
			}
			catch (XPathExpressionException e) {e.printStackTrace();}
			sender.sendMessage("Config reloaded!");
			return true;
		}
		return false;
	}
				
	public static void setMobName(String name)
	{
		getPlugin().bukkit_listener.setMobName(name);
	}
		
	/** clean up */
	@Override
	public void onDisable()
	{
		xpath = null;
		economy = null;
		bukkit_listener = null;
	}

// static getters	
	
	public static Mobs getPlugin()
	{
		return (Mobs)Bukkit.getServer().getPluginManager().getPlugin("Mobs");//instance;
	}
	
	public static XPath getXPath()
	{
		return getPlugin().xpath;
	}
	
	public static Economy getEconomy()
	{
		return getPlugin().economy;
	}
	
	public static ExtraEvents getExtraEvents()
	{
		return getPlugin().extra_events;
	}
	
	public static boolean canDebug()
	{
		return getPlugin().allow_debug;
	}
		
	public static boolean isSpoutEnabled()
	{
		return Bukkit.getServer().getPluginManager().isPluginEnabled("Spout");
	}

	public static void log(Object message)
	{
		Bukkit.getLogger().info(Ansi.ansi().fg(Ansi.Color.GREEN).toString() + "[Mobs] "
				+ message + Ansi.ansi().fg(Ansi.Color.DEFAULT).toString());
	}
	
	/** log error */
	public static void error(Object message)
	{
		Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED).toString() + "[Mobs] "
				+ message + Ansi.ansi().fg(Ansi.Color.DEFAULT).toString());
	}
}