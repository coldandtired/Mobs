package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs_element.Element_types;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Element_wrapper
{
	Object value;
	private int total;
	private int low = 1;
	private int high = 1;
	public String list_type = "ratio";
	private boolean filled = true;
	public enum Conditions
	{
		ADULT, 
		ANGRY,
		AREA,
		//AREA_MOB_COUNT,
		//ATTACKER_TYPE
		BIOME,
		BLOCK_LIGHT_LEVEL,
		CHUNK_MOB_COUNT,
		CUSTOM_INT_1,
		CUSTOM_INT_2,
		CUSTOM_INT_3,
		CUSTOM_INT_4,
		CUSTOM_INT_5,
		CUSTOM_INT_6,
		CUSTOM_INT_7,
		CUSTOM_INT_8,
		CUSTOM_INT_9,
		CUSTOM_INT_10,
		CUSTOM_STRING_1,
		CUSTOM_STRING_2,
		CUSTOM_STRING_3,
		CUSTOM_STRING_4,
		CUSTOM_STRING_5,
		CUSTOM_STRING_6,
		CUSTOM_STRING_7,
		CUSTOM_STRING_8,
		CUSTOM_STRING_9,
		CUSTOM_STRING_10,
		//CAN_BREED,
		DEATH_CAUSE,
		CUSTOM_FLAG_1,
		CUSTOM_FLAG_2,
		CUSTOM_FLAG_3,
		CUSTOM_FLAG_4,
		CUSTOM_FLAG_5,
		CUSTOM_FLAG_6,
		CUSTOM_FLAG_7,
		CUSTOM_FLAG_8,
		CUSTOM_FLAG_9,
		CUSTOM_FLAG_10,
		KILLED_BY_PLAYER,
		//KILLER_NAME,
		LIGHT_LEVEL,
		//LOCAL_TIME,
		LUNAR_PHASE,
		//MOB_AGE,
		NAME,
		NOT_ADULT,
		NOT_ANGRY,
		NOT_AREA,
		NOT_BIOME,
		NOT_CUSTOM_STRING_1,
		NOT_CUSTOM_STRING_2,
		NOT_CUSTOM_STRING_3,
		NOT_CUSTOM_STRING_4,
		NOT_CUSTOM_STRING_5,
		NOT_CUSTOM_STRING_6,
		NOT_CUSTOM_STRING_7,
		NOT_CUSTOM_STRING_8,
		NOT_CUSTOM_STRING_9,
		NOT_CUSTOM_STRING_10,
		NOT_DEATH_CAUSE,
		NOT_CUSTOM_FLAG_1,
		NOT_CUSTOM_FLAG_2,
		NOT_CUSTOM_FLAG_3,
		NOT_CUSTOM_FLAG_4,
		NOT_CUSTOM_FLAG_5,
		NOT_CUSTOM_FLAG_6,
		NOT_CUSTOM_FLAG_7,
		NOT_CUSTOM_FLAG_8,
		NOT_CUSTOM_FLAG_9,
		NOT_CUSTOM_FLAG_10,
		NOT_KILLED_BY_PLAYER,
		NOT_NAME,
		NOT_OCELOT_TYPE,
		NOT_OWNER,
		NOT_PLAYER_HAS_PERMISSION,
		NOT_PLAYER_IS_OP,
		NOT_POWERED,
		NOT_PROJECTILE,
		NOT_PROJECTILE_TYPE,
		NOT_RAINING,
		NOT_SADDLED,
		NOT_SHEARED,
		NOT_SPAWN_REASON,
		NOT_TAMED,
		NOT_THUNDERING,
		NOT_VILLAGER_TYPE,
		NOT_WORLD_NAME,
		OCELOT_TYPE,
		OWNER,
		PLAYER_HAS_PERMISSION,
		PLAYER_IS_OP,
		PROJECTILE,
		PROJECTILE_TYPE,
		//MOB_NOT_STANDING_ON,
		//MOB_STANDING_ON,
		//OCELOT_TYPE,
		//ONLINE_PLAYER_COUNT,
		//PERCENT,
		//PLAYER_HOLDING,
		//PLAYER_ITEM,
		//PLAYER_MONEY,
		//PLAYER_PERMISSION,
		//PLAYER_WEARING,
		POWERED,
		RAINING,
		//REGION_MOB_COUNT,
		//REMAINING_LIFETIME,
		SADDLED,
		SHEARED,
		SPAWN_REASON,
		SKY_LIGHT_LEVEL,
		TAMED,
		THUNDERING,
		//time conditions
		//WOOL_COLORS,
		VILLAGER_TYPE,
		WORLD_MOB_COUNT,
		WORLD_NAME, 
		//WORLD_PLAYER,
		WORLD_TIME,
		//WORLD_TYPE,
		X,
		Y,
		Z; }
	
	@SuppressWarnings("unchecked")
	public Element_wrapper(Element element, Element_types et, Mobs_element me) throws XPathExpressionException
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
				if (el.hasAttribute("use") && (et.equals(Element_types.ACTION_TYPE) || et.equals(Element_types.CONDITION_TYPE)))
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
				value = new TreeMap<Integer, Mobs_element>();
				
				int count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element li = (Element)list.item(i);
					int ratio = list.getLength() > 1 && li.hasAttribute("ratio") ? Integer.parseInt(li.getAttribute("ratio")) : 1;
					count += ratio;	
					Mobs_element me2 = new Mobs_element(li, me);
					if (!et.equals(Element_types.CONDITION_TYPE) && me2.hasConditions()) list_type = "conds";
					((TreeMap<Integer, Mobs_element>)value).put(count, me2);
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
	
	@SuppressWarnings("unchecked")
	public String getString(Element_types et, Mobs_element orig, Bukkit_values bukkit_values)
	{
		if (isString())
		{
			if (!et.equals(Element_types.CONDITION_TYPE) && passesConditions(orig, bukkit_values)) return getValue();
			else return getValue();
		}
		
		SortedMap<Integer, Mobs_element> map = (TreeMap<Integer, Mobs_element>)value;
		if (list_type.equalsIgnoreCase("ratio"))
		{	
			int i = low > -1 ? new Random().nextInt((high - low) + 1) + low : high;
			return map.get(i).getString(et, bukkit_values);
		}
		else
		{
			for (Mobs_element me : map.values())
			{
				if (passesConditions(me, bukkit_values)) return me.getString(et, bukkit_values);
			}
		}
		return "other";
	}
	
	public List<Mobs_element> getActions(Mobs_element orig, Bukkit_values bukkit_values)
	{
		List<Mobs_element> temp = new ArrayList<Mobs_element>();
		
		if (isString())
		{
			if (passesConditions(orig, bukkit_values)) temp.add(orig);
			return temp;
		}
		
		@SuppressWarnings("unchecked")
		SortedMap<Integer, Mobs_element> map = (TreeMap<Integer, Mobs_element>)value;
		
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
			for (Mobs_element mv : map.values()) if (passesConditions(mv, bukkit_values)) temp.addAll(mv.getActions(bukkit_values));
			return temp;
		}
		else
		{
			for (Mobs_element mee : map.values()) if (passesConditions(mee, bukkit_values)) temp.addAll(mee.getActions(bukkit_values));
			Mobs.error(list_type + ", " + temp.size());
			Mobs.error("low = " + low + ", high = " + high);
			int i = low > -1 ? new Random().nextInt((high - low) + 1) + low : high;	
			Mobs.error("i = " + i);
			if (high != temp.size()) Collections.shuffle(temp);
			if (i < temp.size()) temp = temp.subList(0, i);
			return temp;
		}
		return temp;
	}
	
	public List<Mobs_element> getConditions(Mobs_element orig, Bukkit_values bukkit_values)
	{
		List<Mobs_element> temp = new ArrayList<Mobs_element>();
		
		if (isString())
		{
			temp.add(orig);
			return temp;
		}
		
		@SuppressWarnings("unchecked")
		SortedMap<Integer, Mobs_element> map = (TreeMap<Integer, Mobs_element>)value;
		
		temp.addAll(map.values());
		return temp;
	}
	
	private boolean passesConditions(Mobs_element orig, Bukkit_values bukkit_values)
	{
		if (!orig.hasConditions())
		{
			orig.setPassed();
			return true;
		}
		
		if (orig.hasPassed()) return true;
		
		Element_wrapper ew = orig.getWrapper(Element_types.CONDITION_TYPE);
		Mobs.log(ew.list_type);
		
		for (Mobs_element me : orig.getConditions(bukkit_values))
		{
			Element_wrapper ew2 = me.getWrapper(Element_types.CONDITION_TYPE);
			if (ew2.isString())
			{
				boolean b = checkCondition(me, ew2.getValue(), bukkit_values);
				if (!b)
				{
					if (ew.list_type.equalsIgnoreCase("all")) return false;
				}
				else if (!ew.list_type.equalsIgnoreCase("all")) return true;
			}
			else 
			{
				for (Mobs_element me2 : me.getConditions(bukkit_values))
				{
					Element_wrapper ew3 = me2.getWrapper(Element_types.CONDITION_TYPE);
					if (ew3.isString())
					{
						boolean b = checkCondition(me2, ew3.getValue(), bukkit_values);
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
	
	private boolean checkCondition(Mobs_element orig, String cond, Bukkit_values bukkit_values)
	{
		switch (Conditions.valueOf(cond.toUpperCase()))
		{
			case RAINING: return bukkit_values.getLivingEntity().getWorld().hasStorm();
			case THUNDERING: return bukkit_values.getLivingEntity().getWorld().isThundering();
		}
		return true;
	}
	
	/*public Mobs_element getContainer()
	{		
		int t = new Random().nextInt(total) + 1;
		for (Integer i : (map).keySet()) if (i >= t)
		{
			return (map.get(i));
		}
		
		return null;
	}*/
}