package me.coldandtired.mobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;
import me.coldandtired.mobs.events.Mob_approached_event;
import me.coldandtired.mobs.events.Mob_near_event;
import me.coldandtired.mobs.listeners.*;
import me.coldandtired.mobs.managers.*;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
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
	private Action_manager action_manager;
	private Event_manager event_manager;
	private Target_manager target_manager;
	private Spawns_listener spawns_listener;
	private List<Outcome> repeating_outcomes = null;
	private static int log_level = 1;
	private boolean approached = false;
	private boolean near = false;
	private boolean disabled_timer = false;
	
	@Override
	public void onEnable()
	{		
		instance = this;
		checkConfig();
		if (!load_config()) setEnabled(false);
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
	boolean is_latest_version(XPath xpath)
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
		XPath xpath = XPathFactory.newInstance().newXPath();
		if (!is_latest_version(xpath)) log("There's a newer version of Mobs available!");
		warn("This is a Beta version of the plugin.  Make sure you have backups!");
		File f = new File(getDataFolder(), "config.txt");
		InputSource input;
		try
		{
			input = new InputSource(f.getPath());
			Element el = (Element)xpath.evaluate("mobs/settings", input, XPathConstants.NODE);
			if (el != null)	
			{
				if (el.hasAttribute("log_level")) log_level = Integer.parseInt(el.getAttribute("log_level"));
			}
			action_manager = new Action_manager();
			event_manager = new Event_manager();
			target_manager = new Target_manager((NodeList)xpath.evaluate("mobs/areas/*", input, XPathConstants.NODESET));
			boolean needs_timer = setupListeners(xpath, input);
			NodeList list = getList(xpath, input, "repeating_outcomes");
			if (list.getLength() > 0)
			{
				repeating_outcomes = new ArrayList<Outcome>();
				for (int i = 0; i < list.getLength(); i++) repeating_outcomes.add(new Outcome(xpath, (Element)list.item(i)));
				needs_timer = true;
			} else disabled_timer = true;
			if (needs_timer)
			{
				getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() 
				{			 
					public void run() {timerTick();}
				}, 20, 20);
			}
			
		}
		catch (Exception e) {warn("Something went wrong loading the config - stopping!"); e.printStackTrace(); return false;}
		finally
		{
			input = null;
		}
		return true;
	}
	
	private void timerTick()
	{
		checkNearbyPlayers();
		// remaining life
		if (!disabled_timer && repeating_outcomes != null)
		{
			for (Outcome o : repeating_outcomes)
			{
				if (o.isEnabled() && o.canTick()) event_manager.start_actions(repeating_outcomes, Mobs_event.AUTO, null, null, false);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void checkNearbyPlayers()
	{
		for (World w : Bukkit.getWorlds())
		{
			for (Player p : w.getEntitiesByClass(Player.class))
			{
				List<Entity> all = p.getNearbyEntities(10, 4, 10);
				List<Entity> temp = new ArrayList<Entity>(all);
				if (approached)
				{
					if (p.hasMetadata("mobs_data"))
					{
						Map<String, Object> data = (Map<String, Object>) p.getMetadata("mobs_data").get(0).value();
						if (data.containsKey(Mobs_const.NEARBY_MOBS.toString()))
						{
							all.removeAll((List<Entity>) data.get(Mobs_const.NEARBY_MOBS.toString()));
							data.put(Mobs_const.NEARBY_MOBS.toString(), temp);						
						}
					}
					else
					{
						Map<String, Object> data = new HashMap<String, Object>();
						data.put(Mobs_const.NEARBY_MOBS.toString(), all);
						p.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data)); 
					}
					
					for (Entity e : all)
					{
						if (e instanceof LivingEntity)
						{
							getServer().getPluginManager().callEvent(new Mob_approached_event((LivingEntity)e, p));
						}
					}
				}
				
				if (near)
				{
					for (Entity e :temp)
					{
						if (e instanceof LivingEntity)
						{
							getServer().getPluginManager().callEvent(new Mob_near_event((LivingEntity)e, p));
						}
					}
				}
			}
		}
	}
	
	private boolean setupListeners(XPath xpath, InputSource input)
	{
		boolean needs_timer = false;
		PluginManager pm = getServer().getPluginManager();
		NodeList list = getList(xpath, input, "spawns");
		spawns_listener = new Spawns_listener(getEvent_outcomes(xpath, list), list.getLength() > 0);
		pm.registerEvents(spawns_listener, this);
		
		list = getList(xpath, input, "dies");
		if (list.getLength() > 0) pm.registerEvents(new Dies_listener(getEvent_outcomes(xpath, list)), this);
		
		boolean use_hit = false;
		boolean use_damaged = false;
		boolean use_blocks = false;
		list = getList(xpath, input, "damaged");
		if (list.getLength() > 0)
		{
			pm.registerEvents(new Damaged_listener(getEvent_outcomes(xpath, list)), this);
			use_damaged = true;
		}

		list = getList(xpath, input, "blocks");
		if (list.getLength() > 0)
		{
			pm.registerEvents(new Blocks_listener(getEvent_outcomes(xpath, list)), this);
			use_blocks = true;
		}				

		list = getList(xpath, input, "hit");
		if (list.getLength() > 0) use_hit = true;
				
		if (use_hit || use_damaged || use_blocks) pm.registerEvents(new Hit_listener(getEvent_outcomes(xpath, list), use_hit, use_damaged, use_blocks), this);
		
		list = getList(xpath, input, "burns");
		if (list.getLength() > 0) pm.registerEvents(new Burns_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "targets");
		if (list.getLength() > 0) pm.registerEvents(new Targets_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "changes_block");
		if (list.getLength() > 0) pm.registerEvents(new Changes_block_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "evolves");
		if (list.getLength() > 0) pm.registerEvents(new Evolves_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "teleports");
		if (list.getLength() > 0) pm.registerEvents(new Teleports_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "tamed");
		if (list.getLength() > 0) pm.registerEvents(new Tamed_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "splits");
		if (list.getLength() > 0) pm.registerEvents(new Splits_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "teleports");
		if (list.getLength() > 0) pm.registerEvents(new Teleports_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "tamed");
		if (list.getLength() > 0) pm.registerEvents(new Tamed_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "sheared");
		if (list.getLength() > 0) pm.registerEvents(new Sheared_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "dyed");
		if (list.getLength() > 0) pm.registerEvents(new Dyed_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "grows_wool");
		if (list.getLength() > 0) pm.registerEvents(new Grows_wool_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "creates_portal");
		if (list.getLength() > 0) pm.registerEvents(new Creates_portal_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "heals");
		if (list.getLength() > 0) pm.registerEvents(new Heals_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "explodes");
		if (list.getLength() > 0) pm.registerEvents(new Explodes_listener(getEvent_outcomes(xpath, list)), this);

		list = getList(xpath, input, "picks_up_item");
		if (list.getLength() > 0) pm.registerEvents(new Picks_up_item_listener(getEvent_outcomes(xpath, list)), this);
		
		list = getList(xpath, input, "approached");
		if (list.getLength() > 0) 
		{
			approached = true;
			pm.registerEvents(new Approached_listener(getEvent_outcomes(xpath, list)), this);
			needs_timer = true;
		} else approached = false;
		
		list = getList(xpath, input, "near");
		if (list.getLength() > 0)
		{
			near = true;
			pm.registerEvents(new Near_listener(getEvent_outcomes(xpath, list)), this);
			needs_timer = true;
		} else near = false;

		list = getList(xpath, input, "joins");
		if (list.getLength() > 0) pm.registerEvents(new Joins_listener(getEvent_outcomes(xpath, list)), this);
		
		return needs_timer;
	}
	
	private NodeList getList(XPath xpath, InputSource input, String s)
	{
		try
		{
			return (NodeList)xpath.evaluate("mobs/" + s + "/outcome", input, XPathConstants.NODESET);
		}
		catch (Exception e){return null;}
	}
	
	public List<Outcome> getEvent_outcomes(XPath xpath, NodeList list)
	{
		List<Outcome> temp = new ArrayList<Outcome>();
		for (int i = 0; i < list.getLength(); i++)
		{
			temp.add(new Outcome(xpath, (Element)list.item(i)));
		}
		return temp;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("reload_mobs"))
		{
			getServer().getScheduler().cancelTasks(this);
			repeating_outcomes = null;
			event_manager = null;
			target_manager = null;
			action_manager = null;
			spawns_listener = null;
			HandlerList.unregisterAll(this);
			load_config();
			sender.sendMessage("Config reloaded!");
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("repeating_outcomes"))
		{
			if (args.length == 0)
			{
				sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
				return true;
			}
			if (repeating_outcomes == null)
			{
				sender.sendMessage("There are no repeating outcomes!");
				sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
				return true;
			}
			if (args[0].equalsIgnoreCase("enable"))
			{
				if (args.length == 1)
				{
					for (Outcome o : repeating_outcomes)
					{
						o.setEnabled(true);
					}
					sender.sendMessage("Enabled all repeating outcomes!");
					return true;
				}
				else if (args.length == 2)
				{
					for (Outcome o : repeating_outcomes)
					{
						if (o.checkName(args[1]))
						{
							o.setEnabled(true);
							sender.sendMessage("Enabled repeating outcome " + args[1] + "!");
							return true;
						}
					}
				}
			}
			else if (args[0].equalsIgnoreCase("disable"))
			{
				if (args.length == 1)
				{
					for (Outcome o : repeating_outcomes)
					{
						o.setEnabled(false);
					}
					sender.sendMessage("Disabled all repeating outcomes!");
					return true;
				}
				else if (args.length == 2)
				{
					for (Outcome o : repeating_outcomes)
					{
						if (o.checkName(args[1]))
						{
							o.setEnabled(false);
							sender.sendMessage("Disabled repeating outcome " + args[1] + "!");
							return true;
						}
					}
				}
			}
			else if (args[0].equalsIgnoreCase("unpause"))
			{
				if (args.length == 1)
				{
					disabled_timer = false;
					sender.sendMessage("Repeating outcomes are now running!");
					return true;
				}
				return false;
			}
			else if (args[0].equalsIgnoreCase("pause"))
			{
				if (args.length == 1)
				{
					disabled_timer = true;
					sender.sendMessage("Repeating outcomes are now paused!");
					return true;
				}
				return false;
			}
			else if (args[0].equalsIgnoreCase("check"))
			{
				if (args.length == 1)
				{
					for (Outcome o : repeating_outcomes)
					{
						String s = o.getName() == null ? "No name set" : o.getName();
						sender.sendMessage("Outcome " + s + " is " + (o.isEnabled() ? "enabled, " : "disabled, ")
								+ o.getInterval() + " second interval (" + o.getRemaining() + " seconds left)");
					}
					sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
					return true;
				}
				else if (args.length == 2)
				{
					for (Outcome o : repeating_outcomes)
					{
						if (o.checkName(args[1]))
						{
							sender.sendMessage("Outcome " + o.getName() + " is " + (o.isEnabled() ? "enabled, " : "disabled, ")
									+ o.getInterval() + " second interval (" + o.getRemaining() + " seconds left)");
							sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
							return true;
						}
					}
				}
			}
			else if (args[0].equalsIgnoreCase("set_interval"))
			{
				if (args.length == 1) return false;
				int i = Integer.parseInt(args[1]);
				if (args.length == 2)
				{
					for (Outcome o : repeating_outcomes)
					{
						o.setInterval(i);
					}
					sender.sendMessage("All outcome intervals set to " + i + "!");
					return true;
				}
				else if (args.length == 3)
				{
					for (Outcome o : repeating_outcomes)
					{
						if (o.checkName(args[2]))
						{
							o.setInterval(i);
							sender.sendMessage("Outcome " + o.getName() + " interval set to " + i + "!");
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public Action_manager getAction_manager()
	{
		return action_manager;
	}
		
	public Event_manager getEvent_manager()
	{
		return event_manager;
	}
		
	public Target_manager getTarget_manager()
	{
		return target_manager;
	}
	
	public void setMob_name(String[] mob)
	{
		spawns_listener.setMob_name(mob);
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
		getServer().getScheduler().cancelTasks(this);
		repeating_outcomes = null;
		logger = null;
		instance = null;
		event_manager = null;
		target_manager = null;
		action_manager = null;
		spawns_listener = null;
	}

	public static void log(Object message)
	{
		logger.info(Ansi.ansi().fg(Ansi.Color.GREEN).toString() + "[Mobs] "
				+ message + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
	}
	
	/** log warning */
	public static void warn(Object message)
	{
		logger.warning(Ansi.ansi().fg(Ansi.Color.RED).toString() + "[Mobs] "
				+ message + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
	}
	
	/** debug info */
	public static void debug(Object message)
	{
		if (log_level > 2) logger.info(Ansi.ansi().fg(Ansi.Color.YELLOW).toString() 
				 + "[Mobs] " + message + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
	}
}