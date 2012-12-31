package me.coldandtired.mobs.elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Event_report;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Config_event extends Config_element
{	
	private List<Outcome> outcomes;
	private MEvent event_name;	
	private boolean debug_event; 
	private Event_report er;
	private Set<String> mobs_to_debug;

	private Config_event(NodeList list, MEvent event_name, boolean debug, Set<String> mobs_to_debug) throws XPathExpressionException 
	{
		super((Element)list.item(0).getParentNode(), null);
		this.mobs_to_debug = mobs_to_debug;
		try
		{
			this.event_name = event_name;
			debug_event = debug;
					
			outcomes = new ArrayList<Outcome>();
			for (int i = 0; i < list.getLength(); i++) outcomes.add(new Outcome((Element)list.item(i), this));
			for (Outcome o : outcomes) if (o.getAction_groups() == null || o.getAction_groups().size() == 0)
			{
				outcomes = null;
				return;
			}
		}
		catch (Exception e)
		{
			Mobs.error("The " + event_name.toString().toLowerCase() + ".txt file is malformed!");
			e.printStackTrace();
		}
	}
	
	public static Config_event get(MEvent event_name, Set<String> debug, Set<String> mobs_to_debug) throws XPathExpressionException
	{
		File f = new File(Mobs.getInstance().getDataFolder(), event_name.toString().toLowerCase() + ".txt");
		if (!f.exists()) return null;
		
		NodeList list = (NodeList)Mobs.getXPath().evaluate("event/outcome", new InputSource(f.getPath()), XPathConstants.NODESET);
		if (list.getLength() == 0) return null;
		
		Config_event ce = new Config_event(list, event_name, debug.contains(event_name.toString().toLowerCase()), mobs_to_debug);
		if (ce.outcomes == null) return null;
		return ce;
	}
	
	public void performActions(LivingEntity le, Projectile projectile, Event orig_event) 
	{
		er = new Event_report(event_name);
		for (Outcome o : outcomes)
		{
			boolean perform = o.passedConditions_check(le, projectile, orig_event, false);
			er.addOutcome_report(o.getOutcome_report());
			if (perform)
			{
				if (!o.performActions(le, orig_event)) break;
			}
		}
		boolean b = le != null ? mobs_to_debug.contains(le.getType().toString().toLowerCase()) : false;
		if (debug_event && b) Mobs.getInstance().debug(er);
	}
	
	public List<Outcome> getOutcomes()
	{
		return outcomes;
	}
}