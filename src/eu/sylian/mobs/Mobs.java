package eu.sylian.mobs;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import eu.sylian.extraevents.ExtraEvents;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.sylian.mobs.Enums.EventType;
import eu.sylian.mobs.api.Data;

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
		
		extra_events = (ExtraEvents)pm.getPlugin("Extra Events");
		
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

        try (InputStream is = new URL("https://api.curseforge.com/servermods/files?projectIds=34954").openStream())
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            JSONArray array = (JSONArray) JSONValue.parse(rd.readLine());
            String s;
            if (array.size() > 0)
            {
                JSONObject latest = (JSONObject) array.get(array.size() - 1);
                s = (String)latest.get("gameVersion");
                if (!s.equalsIgnoreCase(getDescription().getVersion())) log("There's a more recent version available!");
            }
        }
        catch (Exception e)
        {
            error("Error checking version :(");
        }
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