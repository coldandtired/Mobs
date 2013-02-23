package me.coldandtired.mobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import me.coldandtired.mobs.Enums.EventType;
import me.coldandtired.mobs.Enums.MParam;
import me.coldandtired.mobs.api.Data;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
	private boolean allow_debug;	
	private BukkitListener bukkit_listener = new BukkitListener();
	private Map<String, Timer> timers;
	private Set<EventType> prechecks;
	private boolean disabled_timer = false;
	
	@Override
	public void onEnable()
	{	
		error("This is a new version of the plugin and the old config files won't work anymore!");
		error("See for a guide to the new and improved config!");//TODO link
		xpath = XPathFactory.newInstance().newXPath();
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
		
		if (getServer().getPluginManager().getPlugin("Vault") != null)
		{
	        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) economy = economyProvider.getProvider();
		}
		
		try 
		{
		    new Metrics(this).start();
		} 
		catch (IOException e) 
		{
		    error("Something went wrong with Metrics - it will be disabled.");
		}
		
		getServer().getPluginManager().registerEvents(bukkit_listener, this);
	}
		
	/** Checks if the running version is the newest available (works with release versions only) */
	void versionCheck()
	{
		//TODO activate
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
		
		timers = null;
		prechecks = new HashSet<EventType>();
		
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
			Data.removeData(w, MParam.IGNORED_WORLD);
			if (temp != null && temp.contains(w.getName())) Data.putData(w, MParam.IGNORED_WORLD);
		}
		
		allow_debug = getConfig().getBoolean("allow_debug", false);		
		
		fillTimers();
		fillPrechecks();
	}
	
	private void fillTimers()
	{
		FileConfiguration config = getConfig();
		if (config.contains("timers"))
		{
			timers = new HashMap<String, Timer>();
			for (String s : config.getConfigurationSection("timers").getKeys(false))
			{
				String w = config.getString("timers." + s + ".world");
				if (w == null)
				{
					error("The timer called " + s + " is missing the world value!");
					continue;
				}
				
				if (Bukkit.getWorld(w) == null)
				{
					error("The timer called " + s + " has an unknown world!");
					continue;
				}
				
				int interval = config.getInt("timers." + s + ".interval", 300);
				timers.put(s, new Timer(s, interval, w));
			}
		}
	}
	
	/** Fills a set of events that will have their values set before the actions are performed */
	private void fillPrechecks()
	{
		FileConfiguration config = getConfig();
		
		if (config.getBoolean("check_burns_before_actions", false)) prechecks.add(EventType.BURNS);
		if (config.getBoolean("check_changes_block_before_actions", false)) prechecks.add(EventType.CHANGES_BLOCK);
		if (config.getBoolean("check_creates_portal_before_actions", false)) prechecks.add(EventType.CREATES_PORTAL);
		if (config.getBoolean("check_damaged_before_actions", false)) prechecks.add(EventType.DAMAGED);
		if (config.getBoolean("check_dies_before_actions", false)) prechecks.add(EventType.DIES);
		if (config.getBoolean("check_player_dies_before_actions", false)) prechecks.add(EventType.PLAYER_DIES);
		if (config.getBoolean("check_dyed_before_actions", false)) prechecks.add(EventType.DYED);
		if (config.getBoolean("check_evolves_before_actions", false)) prechecks.add(EventType.EVOLVES);
		if (config.getBoolean("check_explodes_before_actions", false)) prechecks.add(EventType.EXPLODES);
		if (config.getBoolean("check_grows_wool_before_actions", false)) prechecks.add(EventType.GROWS_WOOL);
		if (config.getBoolean("check_heals_before_actions", false)) prechecks.add(EventType.HEALS);
		if (config.getBoolean("check_picks_before_actions", false)) prechecks.add(EventType.PICKS_UP_ITEM);
		if (config.getBoolean("check_sheared_before_actions", false)) prechecks.add(EventType.SHEARED);
		if (config.getBoolean("check_splits_before_actions", false)) prechecks.add(EventType.SPLITS);
		if (config.getBoolean("check_tamed_before_actions", false)) prechecks.add(EventType.TAMED);
		if (config.getBoolean("check_targets_before_actions", false)) prechecks.add(EventType.TARGETS);
		if (config.getBoolean("check_teleports_before_actions", false)) prechecks.add(EventType.TELEPORTS);
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
		else if (cmd.getName().equalsIgnoreCase("timers"))
		{
			if (args.length == 0)
			{
				sender.sendMessage("Timers are " + (disabled_timer ? "paused" : "running"));
				return true;
			}
			if (timers == null)
			{
				sender.sendMessage("There are no timers set!");
				sender.sendMessage("Timers are " + (disabled_timer ? "paused" : "running"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("enable"))
			{
				if (args.length == 1)
				{
					for (Timer t : timers.values())
					{
						t.setEnabled(true);
					}
					sender.sendMessage("Enabled all timers!");
					return true;
				}
				else if (args.length == 2)
				{
					timers.get(args[1]).setEnabled(true);
					sender.sendMessage("Enabled timer " + args[1] + "!");
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("disable"))
			{
				if (args.length == 1)
				{
					for (Timer t : timers.values())
					{
						t.setEnabled(false);
					}
					sender.sendMessage("Disabled all timers!");
					return true;
				}
				else if (args.length == 2)
				{
					timers.get(args[1]).setEnabled(false);
					sender.sendMessage("Disabled timer " + args[1] + "!");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("activate"))
			{
				if (args.length == 1)
				{					
					sender.sendMessage("Activating all timers!");
					for (Timer t : timers.values())
					{
						t.activate();
					}
					return true;
				}
				else if (args.length == 2)
				{
					sender.sendMessage("Activating timer " + args[1] + "!");
					timers.get(args[1]).activate();
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("unpause"))
			{
				if (args.length == 1)
				{
					disabled_timer = false;
					sender.sendMessage("Timers are now running!");
					return true;
				}
				return false;
			}
			else if (args[0].equalsIgnoreCase("pause"))
			{
				if (args.length == 1)
				{
					disabled_timer = true;
					sender.sendMessage("Timers are now paused!");
					return true;
				}
				return false;
			}
			else if (args[0].equalsIgnoreCase("check"))
			{
				if (args.length == 1)
				{
					for (Timer t : timers.values())
					{
						sender.sendMessage(t.check());
					}
					sender.sendMessage("Timers are " + (disabled_timer ? "paused" : "running"));
					return true;
				}
				else if (args.length == 2)
				{
					Timer t = timers.get(args[1]);
					
					sender.sendMessage(t.check());
					sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("set_interval"))
			{
				if (args.length == 1) return false;
				int i = Integer.parseInt(args[1]);
				if (args.length == 2)
				{
					for (Timer t : timers.values())
					{
						t.setInterval(i);
					}
					sender.sendMessage("All timer intervals set to " + i + "!");
					return true;
				}
				else if (args.length == 3)
				{
					Timer t = timers.get(args[2]);
					t.setInterval(i);
					sender.sendMessage("Timer " + t.getName() + " interval set to " + i + "!");
					return true;
				}
			}
			return false;
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
		timers = null;
		prechecks = null;
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
	
	public static boolean canDebug()
	{
		return getPlugin().allow_debug;
	}
	
	public static boolean checkBefore(EventType et)
	{
		return getPlugin().prechecks.contains(et);
	}
	
	public static boolean isSpoutEnabled()
	{//TODO move
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