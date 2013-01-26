package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;


import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Mobs_element
{
	private Mobs_element parent;
	public enum MAttributes { ACTION_TYPE, AFFECTED_MOBS, AFFECTED_WORLDS, AMOUNT, CONDITION_TYPE, CONDITION_VALUE, EFFECT, ENCHANTMENT_ID, ENCHANTMENT_LEVEL, ITEM_DATA, ITEM_ID, MESSAGE, MOB_NAME, MOB_TYPE, PLAYER, TARGET_TYPE, WORLD, X, Y, Z }
	
	private Map<MAttributes, Object> values = new HashMap<MAttributes, Object>();
	private Map<MAttributes, Numbers> numbers = new HashMap<MAttributes, Numbers>();
	
	public Mobs_element(Element element) throws XPathExpressionException
	{
		//Mobs.log("localname = " + element.getLocalName());
		
		for (MAttributes a : MAttributes.values())
		{
			Object o = fill(element, a);
			if (o != null) values.put(a, o);
		}
	}
	
	private Object fill(Element element, MAttributes a) throws XPathExpressionException
	{
		String name = a.toString().toLowerCase();
		if (element.hasAttribute(name))
		{
		//	Mobs.log(name + " = string (" + element.getAttribute(name) + ")");
			return element.getAttribute(name);
		}
		else
		{
			Element el = (Element)Mobs.getXPath().evaluate(name, element, XPathConstants.NODE);
			if (el != null)
			{
				//Mobs.log(name + " = sublist");
				//Mobs.warn("scanning " + el.getLocalName());
				SortedMap<Integer, Mobs_element> map = new TreeMap<Integer, Mobs_element>();
				NodeList list = (NodeList)Mobs.getXPath().evaluate("entry", el, XPathConstants.NODESET);
				int low = 1;
				int high = 1;
				String list_type = "ratio";
				if (el.hasAttribute("use"))
				{
					list_type = "list";
					String use = el.getAttribute("use").replace(" ", "").toUpperCase();
					if (use.contains("TO"))
					{
						String[] temp = use.split("TO");
						low = Integer.parseInt(temp[0]);
						high = temp[1].equalsIgnoreCase("ALL") ? list.getLength() : Integer.parseInt(temp[1]);
					}
					else
					{
					//	Mobs.log("use = " + use);
						if (use.equalsIgnoreCase("ALL")) list_type = "all";
						else low = Integer.parseInt(use);
					}
				}
				
				int count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element li = (Element)list.item(i);
					int ratio = li.hasAttribute("ratio") ? Integer.parseInt(li.getAttribute("ratio")) : 1;
					count += ratio;						
					map.put(count, new Mobs_element(li));
				}
				
				numbers.put(a, new Numbers(count, low, high, list_type));
				
				for (Mobs_element mv : map.values()) mv.setParent(this);
				
				return map;
			}
		}
		return null;
	}
	
	private void setParent(Mobs_element parent)
	{
		this.parent = parent;
	}
	
	@SuppressWarnings("unchecked")
	public List<Mobs_element> getValues(MAttributes a)
	{
		Mobs_element mv = this;
		
		while (mv != null && !mv.values.containsKey(a)) mv = mv.parent;
		if (mv == null) return null;	
		
		Object o = mv.values.get(a);
		
		if (o instanceof String)
		{
			List<Mobs_element> temp = new ArrayList<Mobs_element>();
			temp.add(mv);
			return temp;
		}
		else return numbers.get(a).getMobs_values((SortedMap<Integer, Mobs_element>)o);
	}
	
	public List<String> getValue(MAttributes a)
	{
		Mobs_element mv = this;
		
		while (mv != null && !mv.values.containsKey(a)) mv = mv.parent;
		if (mv == null) return null;					
		
		Object o = mv.values.get(a);
		
		List<String> temp = new ArrayList<String>();
		if (o instanceof String)
		{
			temp.add((String)o);
			return temp;
		}
		else
		{
			for (Mobs_element me : mv.getValues(a)) temp.add((String)me.values.get(a));
			return temp;
		}
	}
	
	public List<Mobs_element> getActions(LivingEntity le, Projectile projectile, Event orig_event)
	{
		List<Mobs_element> mes = getValues(MAttributes.ACTION_TYPE);
		List<Mobs_element> temp = new ArrayList<Mobs_element>();
		for (Mobs_element me : mes)
		{
			List<Mobs_element> mes2 = me.getValues(MAttributes.ACTION_TYPE);
			for (Mobs_element mee : mes2) mee.passesConditions(le, projectile, orig_event);
			temp.addAll(mes2);			
		}
		return temp;
	}
	
	public boolean passesConditions(LivingEntity le, Projectile projectile, Event orig_event)
	{
		for (String cond : getValue(MAttributes.CONDITION_TYPE))
		{
			Mobs.log(cond);
		}
		return false;
	}
}