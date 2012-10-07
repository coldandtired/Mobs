package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;


import org.bukkit.entity.LivingEntity;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Outcome 
{
	private int interval = 300;
	private int remaining;
	private String name = null;
	private boolean enabled = true;
	private List<List<Condition>> conditions = new ArrayList<List<Condition>>();
	private Alternatives actions;
	private List<String> affected_mobs = null;
	private List<String> unaffected_mobs = null;
	private List<String> affected_worlds = null;
	private List<String> unaffected_worlds = null;
	
	public Outcome(XPath xpath, Element element)
	{
		if (element.hasAttribute("interval")) interval = Integer.parseInt(element.getAttribute("interval"));
		if (element.hasAttribute("name")) name = element.getAttribute("name");
		if (element.hasAttribute("enabled")) enabled = Boolean.parseBoolean(element.getAttribute("enabled"));
		remaining = interval;
		if (element.hasAttribute("affected_mobs")) affected_mobs = Arrays.asList(element.getAttribute("affected_mobs").replace(" ", "").toUpperCase().split(","));
		if (element.hasAttribute("unaffected_mobs")) unaffected_mobs = Arrays.asList(element.getAttribute("unaffected_mobs").replace(" ", "").toUpperCase().split(","));
		if (element.hasAttribute("affected_worlds")) affected_worlds = Arrays.asList(element.getAttribute("affected_worlds").replace(" ", "").toUpperCase().split(","));
		if (element.hasAttribute("unaffected_worlds")) unaffected_worlds = Arrays.asList(element.getAttribute("unaffected_worlds").replace(" ", "").toUpperCase().split(","));
	
		try
		{
			NodeList list = (NodeList)xpath.evaluate("conditions", element, XPathConstants.NODESET);
			for (int i = 0; i < list.getLength(); i++)
			{
				List<Condition> temp = new ArrayList<Condition>();
				NodeList list2 = (NodeList)xpath.evaluate("*", list.item(i), XPathConstants.NODESET);
				for (int j = 0; j < list2.getLength(); j++)
				{
					temp.add(new Condition(xpath, (Element)list2.item(j)));
				}
				conditions.add(temp);
			}
			
			Map<Integer, Object> temp = new HashMap<Integer, Object>();
			int count = 0;
			list = (NodeList)xpath.evaluate("actions", element, XPathConstants.NODESET);
			for (int i = 0; i < list.getLength(); i++)
			{
				Element el = (Element)list.item(i);
				
				int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
				count += ratio;
				if (list.getLength() == 1) count = 1;
				NodeList list2 = (NodeList)xpath.evaluate("*", el, XPathConstants.NODESET);
				List<Action> actions = new ArrayList<Action>();
				for (int j = 0; j < list2.getLength(); j++)
				{
					Element el2 = (Element)list2.item(j);
					actions.add(new Action(xpath, el2));
				}
				if(actions.size() > 0) temp.put(count, actions);
			}
			if (temp.size() > 0) actions = new Alternatives(count, temp);
		}
		catch (Exception e) {e.printStackTrace();}
		if (conditions.size() == 0) conditions = null;
	}
	
	public boolean canTick()
	{
		remaining--;
		if (remaining == 0) remaining = interval;
		return remaining == interval;
	}
	
	public boolean isAffected(LivingEntity le)
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
		if (name == null) return false;
		return name.equalsIgnoreCase(s);
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<List<Condition>> getConditions()
	{
		return conditions;
	}
	
	public int getInterval()
	{
		return interval;
	}
	
	@SuppressWarnings("unchecked")
	public List<Action> getActions()
	{
		if (actions == null) return null;
		return (List<Action>)actions.get_alternative();
	}
}