package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;


import me.coldandtired.mobs.Enums.ConditionType;
import me.coldandtired.mobs.Enums.ElementType;
import me.coldandtired.mobs.Enums.ReasonType;
import me.coldandtired.mobs.api.MobsConditionEvent;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MobsElement
{
	private MobsElement parent;
	private boolean use_all = false;
	private Map<ElementType, Object> values = new HashMap<ElementType, Object>();	
	private Map<ConditionType, String> conditions = new HashMap<ConditionType, String>();
	
	public boolean hasConditions()
	{
		return conditions != null;
	}
	
	public MobsElement(Element element, MobsElement parent) throws XPathExpressionException
	{//TODO one outcome? no affected? bring back continue?
		fillConditions(element);
		this.parent = parent;
		for (ElementType et : ElementType.values())
		{
			String name = et.toString().toLowerCase();
			if (element.hasAttribute(name))
			{
				values.put(et, element.getAttribute(name));
				continue;
			}
			
			if (et.equals(ElementType.MAIN))
			{
				Element u = (Element)Mobs.getXPath().evaluate(name, element, XPathConstants.NODE);
				if (u == null) continue;
				
				if (u.hasAttribute("use_all"))
				{
					use_all = getBool(u.getAttribute("use"));
				}
			}
			
			NodeList list = (NodeList)Mobs.getXPath().evaluate(name + "/entry", element, XPathConstants.NODESET);
			if (list.getLength() == 0) continue;
			
			boolean conditional = false;
			if (list.getLength() > 1 && !use_all)
			{
				for (int i = 0; i < list.getLength(); i++)
				{
					boolean b = isConditional((Element)list.item(i));
					if (b)
					{
						conditional = b;
						break;
					}
				}
			}
			else conditional = true;
			
			if (conditional)
			{
				List<MobsElement> temp = new ArrayList<MobsElement>();
				for (int i = 0; i < list.getLength(); i++)
				{
					temp.add(new MobsElement((Element)list.item(i), this));
				}
				values.put(et, temp);
				continue;
			}
			
			int count = 0;
			SortedMap<Integer, MobsElement> temp = new TreeMap<Integer, MobsElement>();
			for (int i = 0; i < list.getLength(); i++)
			{
				Element el = (Element)list.item(i);
				int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1; 
				count += ratio;
				temp.put(count, new MobsElement(el, this));
			}
			values.put(et, temp);
		}
	}
	
	private boolean isConditional(Element element)
	{
		for (ConditionType ct : ConditionType.values())
		{
			String s = ct.toString().toLowerCase();
			if (element.hasAttribute(s)) return true;
		}
		
		return false;
	}
	
	private void fillConditions(Element element)
	{		
		for (ConditionType ct : ConditionType.values())
		{
			String s = ct.toString().toLowerCase();
			if (element.hasAttribute(s))
			{
				conditions.put(ct, element.getAttribute(s));
			}
		}
		
		if (conditions.values().size() == 0) conditions = null;
	}
	
	@SuppressWarnings("unchecked")
	Object getActions(EventValues ev) 
	{
		MobsElement mv = this;
		
		while (mv != null && !mv.values.containsKey(ElementType.MAIN)) mv = mv.parent;
		if (mv == null) return null;
		
		Object o = mv.values.get(ElementType.MAIN);
		if (o instanceof String)
		{
			if (mv.passesConditions(false, ev)) return mv;
			return null;
		}
		
		if (o instanceof List<?>)
		{
			List<MobsElement> list = (List<MobsElement>)o;
			if (use_all) return list;
			
			for (MobsElement me : list)
			{
				if (me.passesConditions(false, ev)) return me;
			}
			
			return null;
		}
		
		Map<Integer, MobsElement> map = (Map<Integer, MobsElement>)o;
		int temp = 0;
		for (int i : map.keySet())
		{
			temp += i;
		}
		
		temp = new Random().nextInt(temp - 1) + 1;
		for (int i : map.keySet())
		{
			if (temp <= i) return map.get(i);
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	MobsElement getCurrentElement(ElementType et, EventValues ev)
	{
		MobsElement mv = this;
		
		while (mv != null && !mv.values.containsKey(et)) mv = mv.parent;
		if (mv == null) return null;
		
		Object o = mv.values.get(et);
		boolean b = mv == this;
		
		if (o instanceof String)
		{
			if (mv.passesConditions(b, ev)) 	return mv;
		}
		
		if (o instanceof List<?>)
		{
			List<MobsElement> list = (List<MobsElement>)o;
			for (MobsElement me : list)
			{
				if (me.passesConditions(b, ev)) return me;
			}
			
			return null;
		}
		
		Map<Integer, MobsElement> map = (Map<Integer, MobsElement>)o;
		int temp = 0;
		for (int i : map.keySet())
		{
			temp += i;
		}
		
		temp = new Random().nextInt(temp - 1) + 1;
		for (int i : map.keySet())
		{
			if (temp <= i) return map.get(i);
		}
		
		return null;
	}
	
	String getString(ElementType et)
	{
		return (String)values.get(et);
	}
	
// condition stuff	
	
	private boolean passesConditions(boolean b, EventValues ev)
	{//TODO different target? aux only?
		if (!hasConditions() || b) return true;
		
		for (ConditionType ct : conditions.keySet())
		{
			String s = conditions.get(ct);
			switch (ct)
			{
				case ADULT: if (!matchesAdult(ct, getBool(s), ev)) return false;
					break;
				case ANGRY: if (!matchesAngry(ct, getBool(s), ev)) return false;
					break;
				case AREA: if (!matchesArea(ct, s, ev)) return false;
					break;
				case POWERED: if (!matchesPowered(ct, getBool(s), ev)) return false;
					break;
				case RAINING: if (!matchesRaining(ct, getBool(s), ev)) return false;
					break;
				case SADDLED: if (!matchesSaddled(ct, getBool(s), ev)) return false;
					break;
				case SHEARED: if (!matchesSheared(ct, getBool(s), ev)) return false;
					break;
				case TAMED: if (!matchesTamed(ct, getBool(s), ev)) return false;
					break;
				case THUNDERING: if (!matchesThundering(ct, getBool(s), ev)) return false;
					break;
			}
		}
		return true;
	}
	
	private boolean matchesAdult(ConditionType ct, boolean needed, EventValues ev)
	{
		LivingEntity le = ev.getLivingEntity();
		if (le != null)
		{
			if (le instanceof Ageable)
			{
				boolean b = ((Ageable)le).isAdult();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_AN_AGEABLE_MOB, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesAngry(ConditionType ct, boolean needed, EventValues ev)
	{
		LivingEntity le = ev.getLivingEntity();
		if (le != null)
		{
			if (le instanceof Wolf)
			{
				boolean b = ((Wolf)le).isAngry();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b;
			}
			else if (le instanceof PigZombie)
			{
				boolean b = ((PigZombie)le).isAngry();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_AN_ANGERABLE_MOB, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesArea(ConditionType ct, String needed, EventValues ev)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
		
		/*for (String value : values)
		{
			String s = value.contains(":") ? "" : loc.getWorld().getName() + ":";
			s += value;
			cr.setCheck_value(s);
			Area area = Mobs.extra_events.getArea(s);
			cr.setActual_value(get_string_from_loc(loc));
			if (area != null && area.isIn_area(loc)) return true;
		}
		break;*/
		
	}
	
	private boolean matchesPowered(ConditionType ct, boolean needed, EventValues ev)
	{
		LivingEntity le = ev.getLivingEntity();
		if (le != null)
		{
			if (le instanceof Creeper)
			{
				boolean b = ((Creeper)le).isPowered();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_A_CREEPER, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesRaining(ConditionType ct, boolean needed, EventValues ev)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			boolean b = w.hasStorm();
			callConditionEvent(ev, ct, needed, "" + b, b == needed);
			return b == needed;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
	}
	
	private boolean matchesSaddled(ConditionType ct, boolean needed, EventValues ev)
	{
		LivingEntity le = ev.getLivingEntity();
		if (le != null)
		{
			if (le instanceof Pig)
			{
				boolean b = ((Pig)le).hasSaddle();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_A_PIG, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesSheared(ConditionType ct, boolean needed, EventValues ev)
	{
		LivingEntity le = ev.getLivingEntity();
		if (le != null)
		{
			if (le instanceof Sheep)
			{
				boolean b = ((Sheep)le).isAdult();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_A_SHEEP, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesTamed(ConditionType ct, boolean needed, EventValues ev)
	{
		LivingEntity le = ev.getLivingEntity();
		if (le != null)
		{
			if (le instanceof Tameable)
			{
				boolean b = ((Tameable)le).isTamed();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_A_TAMEABLE_MOB, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesThundering(ConditionType ct, boolean needed, EventValues ev)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			boolean b = w.isThundering();
			callConditionEvent(ev, ct, needed, "" + b, b == needed);
			return b == needed;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
	}
	
// Utils
	
	private boolean getBool(String s)
	{//TODO all yes/no values through this!
		s = s.toUpperCase();
		if (s == null) return false;
		if (s.equalsIgnoreCase("yes")) return true;
		if (s.equalsIgnoreCase("no")) return false;
		
		return Boolean.valueOf(s);
	}
	
	/** Calls an event when a condition is checked */
	private void callConditionEvent(EventValues ev, ConditionType ct, Object needed, Object got, boolean passed)
	{
		if (!Mobs.canDebug()) return;
		
		Bukkit.getServer().getPluginManager().callEvent(new MobsConditionEvent(ct.toString(), "" + needed, "" + got, passed, ev));
	}
}