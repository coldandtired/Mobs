package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.Outcome_report;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
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
	private Outcome_report or;
	private List<Condition_group> conditions = new ArrayList<Condition_group>();
	
	public Outcome_report getOutcome_report()
	{
		return or;
	}
	
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

		
		NodeList list = (NodeList)Mobs.getXPath().evaluate("condition_group", element, XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++)
		{
			Element el = (Element)Mobs.getXPath().evaluate("conditions", list.item(i), XPathConstants.NODE);
			Condition_group c = Condition_group.get(el, this);			
			if (c != null) conditions.add(c);
		}
		if (conditions.size() == 0) conditions = null;	
	}
	
	public boolean passedConditions_check(LivingEntity le, Projectile projectile, Event orig_event, boolean override) 
	{
		or = new Outcome_report();
		if (!override && (!isAffected(le) || !canTick() || !enabled)) return false;
		if (conditions == null)
		{
			or.setPassed();
			return true;
		}
		
		for (Condition_group c : conditions)
		{			
			if (c.passedConditions_check(or, le, projectile, orig_event))
			{
				or.setPassed();
				return true;
			}
		}
		return false;
	}
	
	public boolean performActions(LivingEntity le, Event orig_event) 
	{
		if (action_groups == null) return false;
		boolean b = false;
		for (Action_group ag : getAction_groups())
		{
			if (ag.performActions(or, le, orig_event)) b = true;
		}
		return b;
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
		if (le == null) return true;
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
	
	public List<Condition_group> getConditions()
	{
		return conditions;
	}
	
	public int getInterval()
	{
		return interval;
	}
}