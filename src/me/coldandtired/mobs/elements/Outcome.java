package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MEvent;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Outcome extends Config_element
{
	private int interval = -1;
	private int remaining;
	private String name = null;
	private boolean enabled = true;
	private List<String> affected_mobs = null;
	private List<String> unaffected_mobs = null;
	private List<String> affected_worlds = null;
	private List<String> unaffected_worlds = null;
	private Alternatives actions;
	private List<Conditions> conditions = new ArrayList<Conditions>();
	
	public Outcome(Element element, Config_element parent) throws XPathExpressionException 
	{
		super(element, parent);
		if (element.hasAttribute("interval")) interval = Integer.parseInt(element.getAttribute("interval"));
		if (element.hasAttribute("name")) name = element.getAttribute("name");
		if (element.hasAttribute("enabled")) enabled = Boolean.parseBoolean(element.getAttribute("enabled"));
		remaining = interval;
		if (element.hasAttribute("affected_mobs")) affected_mobs = Arrays.asList(element.getAttribute("affected_mobs").replace(" ", "").toUpperCase().split(","));
		if (element.hasAttribute("unaffected_mobs")) unaffected_mobs = Arrays.asList(element.getAttribute("unaffected_mobs").replace(" ", "").toUpperCase().split(","));
		if (element.hasAttribute("affected_worlds")) affected_worlds = Arrays.asList(element.getAttribute("affected_worlds").replace(" ", "").toUpperCase().split(","));
		if (element.hasAttribute("unaffected_worlds")) unaffected_worlds = Arrays.asList(element.getAttribute("unaffected_worlds").replace(" ", "").toUpperCase().split(","));
		
		SortedMap<Integer, Object> temp = new TreeMap<Integer, Object>();
		int count = 0;
		NodeList list = (NodeList)Mobs.getXPath().evaluate("actions", element, XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++)
		{
			Element el = (Element)list.item(i);
			
			int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
			count += ratio;
			if (list.getLength() == 1) count = 1;			
			Actions a = Actions.get(el, this);			
			if (a != null) temp.put(count, a);
		}
		if (temp.size() > 0) actions = new Alternatives(count, temp);
		
		list = (NodeList)Mobs.getXPath().evaluate("conditions", element, XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++)
		{
			Conditions c = Conditions.get((Element)list.item(i), this);			
			if (c != null) conditions.add(c);
		}
		if (conditions.size() == 0) conditions = null;	
	}
	
	public boolean passedConditions_check(LivingEntity le, Event orig_event, boolean override)
	{
		if (!override && (!isAffected(le) || !canTick() || !enabled)) return false;
		if (conditions == null)
		{
			Mobs.debug("No condition groups");
			return true;
		}
		
		Mobs.debug("Condition groups - " + conditions.size());
		Mobs.debug("------------------");
		for (Conditions c : conditions)
		{
			Mobs.debug("Checking condition group");
			if (c.passedConditions_check(le, orig_event)) return true;
		}
		Mobs.debug("All condition groups failed - not performing any actions");
		return false;
	}
	
	public boolean performActions(LivingEntity le, MEvent event, Event orig_event)
	{
		if (actions == null) return false;
		return ((Actions)actions.get_alternative()).performActions(le, event, orig_event);
	}
	
	private boolean canTick()
	{
		if (interval < 0) return true;
		remaining--;
		if (remaining < 1) remaining = interval;
		return remaining == interval;
	}
	
	private boolean isAffected(LivingEntity le)
	{ 
		if ((unaffected_mobs != null && unaffected_mobs.contains(le.getType().toString())) || 
				(unaffected_worlds != null && unaffected_worlds.contains(le.getWorld().getName().toUpperCase()))) return false;
		if (affected_mobs != null && !affected_mobs.contains(le.getType().toString())) return false;
		if (affected_worlds != null && !affected_worlds.contains(le.getWorld().getName().toUpperCase())) return false;
		return true;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setInterval(int interval)
	{
		this.interval = interval;
		remaining = interval;
	}
	
	public int getRemaining()
	{
		return remaining;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public boolean checkName(String s)
	{
		if (s == null) return true;
		if (name == null) return false;
		return name.equalsIgnoreCase(s);
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<Conditions> getConditions()
	{
		return conditions;
	}
	
	public int getInterval()
	{
		return interval;
	}
	
	public List<Action> getActions()
	{
		if (actions == null) return null;
		return ((Actions)actions.get_alternative()).getActions();
	}
}