package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Config_event extends Config_element
{	
	private List<Outcome> outcomes;
	private MEvent event_name;
	
	private Config_event(Element element, MEvent event_name) throws XPathExpressionException 
	{
		super(element, null);
		this.event_name = event_name;
		NodeList list = (NodeList)Mobs.getXPath().evaluate("outcome", element, XPathConstants.NODESET);
		if (list.getLength() == 0) return;
		
		outcomes = new ArrayList<Outcome>();
		for (int i = 0; i < list.getLength(); i++) outcomes.add(new Outcome((Element)list.item(i), this));
	}
	
	public static Config_event get(Element element, MEvent event_name) throws XPathExpressionException
	{
		Element el = (Element)Mobs.getXPath().evaluate(event_name.toString().toLowerCase(), element, XPathConstants.NODE);
		if (el == null) return null;
		Config_event ce = new Config_event(el, event_name);
		if (ce.outcomes == null) return null;
		return ce;
	}
	
	public void performActions(LivingEntity le, Event orig_event)
	{
		Mobs.debug("------------------");
		Mobs.debug("Event - " + event_name.toString());
		Mobs.debug("Outcomes - " + outcomes.size());
		for (Outcome o : outcomes)
		{
			boolean perform = o.passedConditions_check(le, orig_event, false);
			if (perform)
			{
				if (!o.performActions(le, event_name, orig_event)) break;
			}			
		}
		Mobs.debug(event_name.toString() + " finished");
		Mobs.debug("------------------");
	}
	
	public List<Outcome> getOutcomes()
	{
		return outcomes;
	}
}