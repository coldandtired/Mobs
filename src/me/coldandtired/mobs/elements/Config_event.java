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
	
	private Config_event(NodeList list, MEvent event_name, boolean debug) throws XPathExpressionException 
	{
		super((Element)list.item(0).getParentNode(), null);
		this.event_name = event_name;
		debug_event = debug;
				
		outcomes = new ArrayList<Outcome>();
		for (int i = 0; i < list.getLength(); i++) outcomes.add(new Outcome((Element)list.item(i), this));
		for (Outcome o : outcomes) if (o.getActions() == null || o.getActions().size() == 0)
		{
			outcomes = null;
			return;
		}
	}
	
	public static Config_event get(MEvent event_name, Set<String> debug) throws XPathExpressionException
	{
		File f = new File(Mobs.getInstance().getDataFolder(), event_name.toString().toLowerCase() + ".txt");
		if (!f.exists()) return null;
		
		NodeList list = (NodeList)Mobs.getXPath().evaluate("event/outcome", new InputSource(f.getPath()), XPathConstants.NODESET);
		if (list.getLength() == 0) return null;
		
		Config_event ce = new Config_event(list, event_name, debug.contains(event_name.toString().toLowerCase()));
		if (ce.outcomes == null) return null;
		return ce;
	}
	
	public void performActions(LivingEntity le, Projectile projectile, Event orig_event)
	{
		Event_report er = new Event_report(event_name);
		for (Outcome o : outcomes)
		{
			boolean perform = o.passedConditions_check(er, le, projectile, orig_event, false);
			if (perform)
			{
				if (!o.performActions(le, event_name, orig_event)) break;

			}
		}
		if (debug_event) Mobs.getInstance().debug(er);
	}
	
	public List<Outcome> getOutcomes()
	{
		return outcomes;
	}
}