package me.coldandtired.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_action;
import me.coldandtired.mobs.enums.Mobs_condition;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_subelement;
import me.coldandtired.mobs.listeners.Blocks_listener;
import me.coldandtired.mobs.listeners.Burns_listener;
import me.coldandtired.mobs.listeners.Changes_block_listener;
import me.coldandtired.mobs.listeners.Creates_portal_listener;
import me.coldandtired.mobs.listeners.Damaged_listener;
import me.coldandtired.mobs.listeners.Dies_listener;
import me.coldandtired.mobs.listeners.Dyed_listener;
import me.coldandtired.mobs.listeners.Evolves_listener;
import me.coldandtired.mobs.listeners.Explodes_listener;
import me.coldandtired.mobs.listeners.Grows_wool_listener;
import me.coldandtired.mobs.listeners.Heals_listener;
import me.coldandtired.mobs.listeners.Hit_listener;
import me.coldandtired.mobs.listeners.Sheared_listener;
import me.coldandtired.mobs.listeners.Spawns_listener;
import me.coldandtired.mobs.listeners.Splits_listener;
import me.coldandtired.mobs.listeners.Tamed_listener;
import me.coldandtired.mobs.listeners.Targets_listener;
import me.coldandtired.mobs.listeners.Teleports_listener;
import me.coldandtired.mobs.managers.Action_manager;
import me.coldandtired.mobs.managers.Event_manager;
import me.coldandtired.mobs.managers.Target_manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
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
	private static boolean disable_timer = false;
	
	@Override
	public void onEnable()
	{		
		instance = this;
		if (!load_config()) setEnabled(false);
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
		//if (!is_latest_version(xpath)) Utils.log("There's a newer version of Mobs available!");
		warn("This is an alpha release of the plugin.  Make sure you have backups!");
		File f = new File(getDataFolder(), "config.txt");
		if (f.exists())
		{
			try
			{
				InputSource input = new InputSource(f.getPath());
				Element el = (Element)xpath.evaluate("mobs/settings", input, XPathConstants.NODE);
				if (el != null)	
				{
					if (el.hasAttribute("log_level")) log_level = Integer.parseInt(el.getAttribute("log_level"));
					if (el.hasAttribute("disable_timer")) disable_timer = Boolean.parseBoolean(el.getAttribute("disable_timer"));
				}

				action_manager = new Action_manager();
				event_manager = new Event_manager();
				target_manager = new Target_manager((NodeList)xpath.evaluate("mobs/areas/*", input, XPathConstants.NODESET));
				setupListeners(xpath, input);
				if (!disable_timer)
				{
					NodeList list = (NodeList)xpath.evaluate("mobs/repeating_outcomes/outcome", input, XPathConstants.NODESET);
					if (list.getLength() > 0)
					{
						repeating_outcomes = new ArrayList<Outcome>();
						for (int i = 0; i < list.getLength(); i++) repeating_outcomes.add(new Outcome(xpath, (Element)list.item(i)));
					}
					getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() 
					{			 
						public void run() {timerTick();}
					}, 20, 20);
				}
				
			}
			catch (Exception e) {warn("Something went wrong loading the config - stopping!"); return false;}
		}
		else
		{
			log("No config file found - stopping!");
			// No data file			
			f = this.getDataFolder();
			if (!f.exists()) f.mkdir();
			return false;		
		}
		
		return true;
	}
	
	private void timerTick()
	{
		if (repeating_outcomes != null)
		{
			for (Outcome o : repeating_outcomes)
			{
				if (o.canTick()) event_manager.start_actions(repeating_outcomes, Mobs_event.AUTO, null, null, false);
			}
		}
	}
	
	private void setupListeners(XPath xpath, InputSource input)
	{
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
		if (cmd.getName().equalsIgnoreCase("spawn_mobs"))
		{
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("reload_mobs"))
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
		else if (cmd.getName().equalsIgnoreCase("check_names"))
		{
			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("actions"))
				{
					log("Implemented actions");
					log("----------------------");
					for (Mobs_action ma : Mobs_action.values()) log(ma.toString().toLowerCase());
					log("See ... for more info");
					return true;
				}
				else if (args[0].equalsIgnoreCase("conditions"))
				{
					log("Implemented conditions");
					log("----------------------");
					for (Mobs_condition mc : Mobs_condition.values()) log(mc.toString().toLowerCase());
					log("See ... for more info");
					return true;
				}
				else if (args[0].equalsIgnoreCase("events"))
				{
					log("Implemented events");
					log("----------------------");
					for (Mobs_event me : Mobs_event.values()) log(me.toString().toLowerCase());
					log("See ... for more info");
					return true;
				}
				else if (args[0].equalsIgnoreCase("targets"))
				{
					log("Implemented targets");
					log("----------------------");
					for (Mobs_subelement mt : Mobs_subelement.values()) log(mt.toString().toLowerCase());
					log("See ... for more info");
					return true;
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