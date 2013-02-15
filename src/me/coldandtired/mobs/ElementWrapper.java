package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Enums.ElementType;
import me.coldandtired.mobs.api.MobsFailedConditionEvent;
import me.coldandtired.mobs.api.MobsPassedConditionEvent;
import me.coldandtired.mobs.Enums.ConditionType;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Wolf;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ElementWrapper
{
	Object value;
	private int total;
	private int low = 1;
	private int high = 1;
	public String list_type = "ratio";
	private boolean filled = true;
		
	@SuppressWarnings("unchecked")
	public ElementWrapper(Element element, ElementType et, MobsElement me) throws XPathExpressionException
	{
		String name = et.toString().toLowerCase();
		if (element.hasAttribute(name))
		{
			value = element.getAttribute(name);
			return;
		}
		else
		{
			Element el = (Element)Mobs.getXPath().evaluate(name, element, XPathConstants.NODE);
			if (el != null)
			{
				NodeList list = (NodeList)Mobs.getXPath().evaluate("entry", el, XPathConstants.NODESET);
				high = list.getLength();
				
				// use is only for actions and conditions
				if (el.hasAttribute("use") && (et.equals(ElementType.ACTION) || et.equals(ElementType.CONDITION)))
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
						if (use.equalsIgnoreCase("ALL"))
						{
							list_type = "all";
							low = high;
						}
						else
						{
							low = Integer.parseInt(use);
							high = low;
						}
					}
				}
				value = new TreeMap<Integer, MobsElement>();
				
				int count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element li = (Element)list.item(i);
					int ratio = list.getLength() > 1 && li.hasAttribute("ratio") ? Integer.parseInt(li.getAttribute("ratio")) : 1;
					count += ratio;	
					MobsElement me2 = new MobsElement(li, me);
					if (!et.equals(ElementType.CONDITION) && me2.hasConditions()) list_type = "conds";
					((TreeMap<Integer, MobsElement>)value).put(count, me2);
				}
				total = count;
				return;
			} 
			filled = false;
		}
	}
	
	public boolean isFilled()
	{
		return filled;
	}
	
	public String getValue()
	{
		return (String)value;
	}
	
	public boolean isString()
	{
		return value instanceof String;
	}
	
	/*@SuppressWarnings("unchecked")
	public String getString(ElementType et, MobsElement orig, EventValues bv, boolean check)
	{
		if (isString())
		{
			if (!check) return getValue();
			if (passesConditions(orig, bv)) return getValue();
			return null;
		}
		
		SortedMap<Integer, MobsElement> map = (TreeMap<Integer, MobsElement>)value;
		if (list_type.equalsIgnoreCase("ratio"))
		{	
			int i = low > -1 ? new Random().nextInt((high - low) + 1) + low : high;
			return map.get(i).getString(et, bv, check);
		}
		else
		{
			for (MobsElement me : map.values())
			{
				if (!check) return me.getString(et, bv, check);
				if (passesConditions(me, bv)) return me.getString(et, bv, check);
			}
		}
		return null;
	}*/
	
	@SuppressWarnings("unchecked")
	public MobsElement getCurrentElement(ElementType et, MobsElement orig, EventValues ev)
	{
		if (isString())
		{
			if (passesConditions(orig, ev)) return orig;
			return null;
		}
		
		SortedMap<Integer, MobsElement> map = (TreeMap<Integer, MobsElement>)value;
		if (list_type.equalsIgnoreCase("ratio"))
		{	
			int i = low > -1 ? new Random().nextInt((high - low) + 1) + low : high;
			return map.get(i);
		}
		else
		{
			for (MobsElement me : map.values())
			{
				if (passesConditions(me, ev)) return me.getCurrentElement(et, ev);
			}
		}
		return null;
	}
	
	
	
	public List<MobsElement> getActions(MobsElement orig, EventValues bukkit_values)
	{//TODO fix
		List<MobsElement> temp = new ArrayList<MobsElement>();
		
		if (isString())
		{
			if (passesConditions(orig, bukkit_values)) temp.add(orig);
			return temp;
		}
		
		@SuppressWarnings("unchecked")
		SortedMap<Integer, MobsElement> map = (TreeMap<Integer, MobsElement>)value;
		
		if (list_type.equalsIgnoreCase("ratio"))
		{
			int t = new Random().nextInt(total) + 1;
			for (Integer i : (map).keySet()) if (i >= t)
			{
				if (passesConditions(map.get(i), bukkit_values)) temp.addAll(map.get(i).getActions(bukkit_values));
				return temp;
			}
		}
		else if (list_type.equalsIgnoreCase("all"))
		{
			for (MobsElement mv : map.values()) if (passesConditions(mv, bukkit_values)) temp.addAll(mv.getActions(bukkit_values));
			return temp;
		}
		else
		{
			for (MobsElement mee : map.values()) if (passesConditions(mee, bukkit_values)) temp.addAll(mee.getActions(bukkit_values));
			int i = low > -1 ? new Random().nextInt((high - low) + 1) + low : high;	
			if (high != temp.size()) Collections.shuffle(temp);
			if (i < temp.size()) temp = temp.subList(0, i);
			return temp;
		}
		return temp;
	}
	
	public List<MobsElement> getConditions(MobsElement orig, EventValues bukkit_values)
	{
		List<MobsElement> temp = new ArrayList<MobsElement>();
		
		if (isString())
		{
			temp.add(orig);
			return temp;
		}
		
		@SuppressWarnings("unchecked")
		SortedMap<Integer, MobsElement> map = (TreeMap<Integer, MobsElement>)value;
		
		temp.addAll(map.values());
		return temp;
	}
	
	private boolean passesConditions(MobsElement orig, EventValues ev)
	{	//TODO check
		if (!orig.hasConditions()) return true;				
		
		ElementWrapper ew = orig.getWrapper(ElementType.CONDITION);
		Mobs.log(ew.list_type);
		
		for (MobsElement me : orig.getConditions(ev))
		{
			ElementWrapper ew2 = me.getWrapper(ElementType.CONDITION);
			if (ew2.isString())
			{
				boolean b = checkCondition(me, ew2.getValue(), ev);
				if (!b)
				{
					if (ew.list_type.equalsIgnoreCase("all")) return false;
				}
				else if (!ew.list_type.equalsIgnoreCase("all")) return true;
			}
			else 
			{
				for (MobsElement me2 : me.getConditions(ev))
				{
					ElementWrapper ew3 = me2.getWrapper(ElementType.CONDITION);
					if (ew3.isString())
					{
						boolean b = checkCondition(me2, ew3.getValue(), ev);
						if (!b)
						{
							if (ew2.list_type.equalsIgnoreCase("all")) return false;
						}
						else if (!ew2.list_type.equalsIgnoreCase("all")) return true;
					}
				}
			}			
		}
		
		return false;
	}
	
	private boolean checkCondition(MobsElement orig, String cond, EventValues bv)
	{
		switch (ConditionType.valueOf(cond.toUpperCase()))
		{
			case ADULT: return checkAdult(orig, bv, false);
			case ANGRY: return checkAngry(orig, bv, false);
			case AREA: return checkArea(orig, bv, false);
			case RAINING: return checkRaining(orig, bv, false);
			case THUNDERING: return checkThundering(orig, bv, false);
			case NOT_ADULT: return checkAdult(orig, bv, true);
			case NOT_ANGRY: return checkAngry(orig, bv, true);
			case NOT_AREA: return checkArea(orig, bv, true);
			case NOT_RAINING: return checkRaining(orig, bv, true);
			case NOT_THUNDERING: return checkThundering(orig, bv, true);
		}
		return false;
	}
	
	private boolean checkAdult(MobsElement orig, EventValues bv, boolean reversed)
	{
		String name = reversed ? "not_adult" : "adult";
		
		LivingEntity le = bv.getLivingEntity();
		if (le != null)
		{
			if (le instanceof Ageable)
			{
				boolean b = ((Ageable)le).isAdult();
				if (reversed) b = !b;
				callConditionEvent(bv, name, null, null, b);
				return b;
			}
			else
			{
				callConditionEvent(bv, name, null, "Not an ageable mob", false);
				return false;
			}
		}
		callConditionEvent(bv, name, null, "No mob", false);
		return false;
	}
	
	private boolean checkAngry(MobsElement orig, EventValues bv, boolean reversed)
	{
		String name = reversed ? "not_angry" : "angry";
		
		LivingEntity le = bv.getLivingEntity();
		if (le != null)
		{
			if (le instanceof Wolf)
			{
				boolean b = ((Wolf)le).isAngry();
				if (reversed) b = !b;
				callConditionEvent(bv, name, null, null, b);
				return b;
			}
			else if (le instanceof PigZombie)
			{
				boolean b = ((PigZombie)le).isAngry();
				if (reversed) b = !b;
				callConditionEvent(bv, name, null, null, b);
				return b;
			}
			else
			{
				callConditionEvent(bv, name, null, "Not an angerable mob", false);
				return false;
			}
		}
		callConditionEvent(bv, name, null, "No mob", false);
		return false;
	}
	
	private boolean checkArea(MobsElement orig, EventValues bv, boolean reversed)
	{
		/*String name = reversed ? "not_area" : "area";
		String area = orig.getString(ElementType.AREA_NAME, null);
		World w = getWorld(orig, bv);
		if (w != null)
		{
			
		}
		callConditionEvent(bv, name, null, "No world found", false);*/
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
	
	private boolean checkRaining(MobsElement orig, EventValues bv, boolean reversed)
	{
		String name = reversed ? "not_raining" : "raining";
		
		World w = getWorld(orig, bv);
		if (w != null)
		{
			boolean b = w.hasStorm();
			if (reversed) b = !b;
			callConditionEvent(bv, name, null, null, b);
			return b;
		}
		callConditionEvent(bv, name, null, "No world found", false);
		return false;
	}
	
	private boolean checkThundering(MobsElement orig, EventValues bv, boolean reversed)
	{
		String name = reversed ? "not_thundering" : "thundering";
		
		World w = getWorld(orig, bv);
		if (w != null)
		{
			boolean b = w.hasStorm();
			if (reversed) b = !b;
			callConditionEvent(bv, name, null, null, b);
			return b;
		}
		callConditionEvent(bv, name, null, "No world found", false);
		return false;
	}
	
	
	private World getWorld(MobsElement me, EventValues bukkit_values)
	{
		/*String w = me.getContainingElement(ElementType.WORLD).getString(ElementType.WORLD, bukkit_values);
		
		if (w != null) return Bukkit.getWorld(w);
		
		LivingEntity le = bukkit_values.getLivingEntity();
		if (le != null) return le.getWorld();*/
		
		return null;
	}
	
	/** Calls an event when a condition is checked */
	private void callConditionEvent(EventValues bukkit_values, String condition_type, String condition_value, String actual_value, boolean passed)
	{
		if (!Mobs.allow_debug) return;
		
		if (passed) Bukkit.getServer().getPluginManager().callEvent(new MobsPassedConditionEvent(bukkit_values, condition_type, condition_value));
		else Bukkit.getServer().getPluginManager().callEvent(new MobsFailedConditionEvent(bukkit_values, condition_type, condition_value));
	}
}