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
	public enum Element_types { ACTION_TYPE, AFFECTED_MOBS, AFFECTED_WORLDS, AMOUNT, CONDITION_TYPE, CONDITION_VALUE, EFFECT, ENCHANTMENT_ID, ENCHANTMENT_LEVEL, ITEM_DATA, ITEM_ID, LOCKED, MESSAGE, MOB_NAME, MOB_TYPE, PLAYER, SUBACTION_TYPE, TARGET_TYPE, WORLD, X, Y, Z }
	public enum Subasssctions {ADULT,
		ANGRY,
		/*ATTACK_POWER,
		BLOCK,
		BROADCAST,
		CLOSED,
	    DAMAGE_TAKEN,
	    DATA,
	    DROPPED_EXP,
	    DROPPED_ITEMS,
	    EXP,
	    EXPLOSION,
	    EXPLOSION_SIZE,
	    FIERY_EXPLOSION,*/
	    FRIENDLY,
	    /*HP,
	    ITEM,
	    ITEMS,
	    LEVEL,
	    LIGHTNING,
	    LIGHTNING_EFFECT,
	    LOG,
	    MAX_HP,
	    MAX_LIFE,
	    MECHANISM,
	    MESSAGE,
	    MOB,
	    MONEY,
	    NAME,*/
	    NO_BURN,	
		NO_CREATE_PORTALS,
		NO_DESTROY_BLOCKS,
		NO_DYED,
		NO_EVOLVE,		
		NO_FIERY_EXPLOSION,	
		NO_GRAZE,
		NO_GROW_WOOL,
		NO_HEAL,
		NO_MOVE_BLOCKS,
		NO_PICK_UP_ITEMS,
		NO_SADDLED,
		NO_SHEARING,
		NO_TAMING,
		NO_TELEPORT,
	    /*OCELOT_TYPE,
	    OPEN,
	    OWNER,
	    POWERED,
		SADDLED,
		SHEARED,
		SKIN,
		TAMED,
		TIME,
		TITLE,
		VILLAGER_TYPE,
		WEATHER,
		WOOL_COLOR
		
		
		/*BROADCAST, 
		CANCEL_EVENT,
		DATA,
		DROPS,
		EXP,
		ITEMS,
		DOOR,
		GATE,
		TRAPDOOR,
		CONTINUE,
		DAMAGE,
		DESTROY_BLOCK,
		DROP_EXP,
		DROP_ITEM,  
		EXPLOSION,
		FIERY_EXPLOSION,
		GIVE_ITEM,
		//HEAL(amount)
		KILL,
		LIGHTNING,
		LIGHTNING_EFFECT,
		LOG,
		OPEN_DOOR,
		OPEN_GATE,
		OPEN_TRAPDOOR,
		PLAY_BLAZE_EFFECT,
		PLAY_BOW_EFFECT,
		PLAY_CLICK1_EFFECT,
		PLAY_CLICK2_EFFECT,
		PLAY_DOOR_EFFECT,
		PLAY_ENDER_EFFECT,
		PLAY_EXTINGUISH_EFFECT,
		PLAY_GHAST1_EFFECT,
		PLAY_GHAST2_EFFECT,
		PLAY_FLAMES_EFFECT,
		PLAY_POTION_EFFECT,
		PLAY_SMOKE_EFFECT,
		PLAY_STEP_EFFECT,
		PLAY_ZOMBIE1_EFFECT,
		PLAY_ZOMBIE2_EFFECT,
		PLAY_ZOMBIE3_EFFECT,
		PRESS_BUTTON,
		PULL_LEVER,
		PUSH_LEVER,
		RAIN,
		REMOVE,
		
		REMOVE_ITEM,
		REMOVE_MAX_LIFE,
		RESTORE_SKIN,
		SEND_MESSAGE,
		SET_ADULT
		SET_ANGRY
		SET_ATTACK_POWER,
		SET_BLOCK,
		SET_CAN_BE_DYED
		SET_CAN_BE_SADDLED
		SET_CAN_BE_SHEARED
		SET_CAN_BE_TAMED
		//SET_CAN_BREED,
		SET_CAN_BURN
		SET_CAN_CREATE_PORTALS
		SET_CAN_DESTROY_BLOCKS
		SET_CAN_EVOLVE
		SET_CAN_GRAZE
		SET_CAN_GROW_WOOL
		SET_CAN_HEAL
		SET_CAN_MOVE_BLOCKS	
		SET_CAN_PICK_UP_ITEMS
		SET_CAN_TELEPORT		
		
		SET_DAMAGE_TAKEN_FROM_BLOCK_EXPLOSION,
		SET_DAMAGE_TAKEN_FROM_CONTACT,
		SET_DAMAGE_TAKEN_FROM_CUSTOM,
		SET_DAMAGE_TAKEN_FROM_DROWNING,
		SET_DAMAGE_TAKEN_FROM_ATTACK,
		SET_DAMAGE_TAKEN_FROM_ENTITY_EXPLOSION,
		SET_DAMAGE_TAKEN_FROM_FALL,
		SET_DAMAGE_TAKEN_FROM_FIRE,
		SET_DAMAGE_TAKEN_FROM_FIRE_TICK,
		SET_DAMAGE_TAKEN_FROM_LAVA,
		SET_DAMAGE_TAKEN_FROM_LIGHTNING,
		SET_DAMAGE_TAKEN_FROM_MAGIC,
		SET_DAMAGE_TAKEN_FROM_MELTING,
		SET_DAMAGE_TAKEN_FROM_POISON,
		SET_DAMAGE_TAKEN_FROM_PROJECTILE,
		SET_DAMAGE_TAKEN_FROM_STARVATION,
		SET_DAMAGE_TAKEN_FROM_SUFFOCATION,
		SET_DAMAGE_TAKEN_FROM_SUICIDE,
		SET_DAMAGE_TAKEN_FROM_VOID,	
		SET_EXP,
		SET_EXPLOSION_SIZE,
		SET_FIERY_EXPLOSION
		SET_FRIENDLY
		SET_HP,
		SET_LEVEL,
		SET_MAX_HP,
		SET_MAX_LIFE,
		SET_MONEY,
		SET_NAME,
		SET_OCELOT_TYPE,
		SET_OWNER,
		SET_POWERED
		SET_SADDLED
		SET_SHEARED
		SET_SKIN,
		SET_TAMED
		SET_TIME,
		SET_TITLE,
		SET_VILLAGER_TYPE,
		SET_WOOL_BLACK,
		SET_WOOL_BLUE,
		SET_WOOL_BROWN,
		SET_WOOL_CYAN,
		SET_WOOL_GRAY,
		SET_WOOL_GREEN,
		SET_WOOL_LIGHT_BLUE,
		SET_WOOL_LIME,
		SET_WOOL_MAGENTA,
		SET_WOOL_ORANGE,
		SET_WOOL_PINK,
		SET_WOOL_PURPLE,
		SET_WOOL_RANDOM,
		SET_WOOL_RED,
		SET_WOOL_SILVER,
		SET_WOOL_WHITE,
		SET_WOOL_YELLOW,
		SPAWN_MOB,
		STORM,
		SUN,
		TOGGLE_DOOR,
		TOGGLE_GATE,
		TOGGLE_LEVER,
		TOGGLE_TRAPDOOR;*/}

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
	
	private Map<Element_types, Element_wrapper> wrappers = new HashMap<Element_types, Element_wrapper>();
	
	public Mobs_element(Element element) throws XPathExpressionException
	{
		//Mobs.log("localname = " + element.getLocalName());		
		for (Element_types a : Element_types.values()) fill(element, a);
	}
	
	private void fill(Element element, Element_types a) throws XPathExpressionException
	{
		String name = a.toString().toLowerCase();
		if (element.hasAttribute(name))
		{
		//	Mobs.log(name + " = string (" + element.getAttribute(name) + ")");
			//return element.getAttribute(name);
			wrappers.put(a, new Element_wrapper(element.getAttribute(name)));
			return;
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
						else
						{
							low = Integer.parseInt(use);
							high = low;
						}
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
				
				for (Mobs_element mv : map.values()) mv.setParent(this);
				
				wrappers.put(a, new Element_wrapper(map, count, low, high, list_type));
			}
		}
	}
	
	private void setParent(Mobs_element parent)
	{
		this.parent = parent;
	}
	
	public List<Mobs_element> getValues(Element_types a)
	{
		Mobs_element mv = this;
		
		while (mv != null && !mv.wrappers.containsKey(a)) mv = mv.parent;
		if (mv == null) return null;	
		
		return mv.wrappers.get(a).getMobs_values(mv);
	}
	
	public List<Mobs_element> getActions()
	{
		Mobs.log("in");
		List<Mobs_element> mes = getValues(Element_types.ACTION_TYPE);
		List<Mobs_element> temp = new ArrayList<Mobs_element>();
		for (Mobs_element me : mes)
		{
			Mobs.log(me.toString());
			temp.addAll(me.getValues(Element_types.ACTION_TYPE));			
		}
		return temp;
	}
	
// element getters
	
	private Mobs_element getContaining_element(Element_types et)
	{
		Mobs_element mv = this;
		
		while (mv != null && !mv.wrappers.containsKey(et)) mv = mv.parent;
		if (mv == null) return this;
		
		Element_wrapper wrapper = mv.wrappers.get(et);
		
		if (wrapper.getValue() instanceof String) return mv;
		
		return wrapper.getContainer();
	}
	
	public Mobs_element getAction_type()
	{
		return getContaining_element(Element_types.ACTION_TYPE);
	}	
	
	public Mobs_element getAmount()
	{
		return getContaining_element(Element_types.AMOUNT);
	}
	
	public Mobs_element getItem_data()
	{
		return getContaining_element(Element_types.ITEM_DATA);
	}
	
	public Mobs_element getItem_id()
	{
		return getContaining_element(Element_types.ITEM_ID);
	}
	
	public Mobs_element getMessage()
	{
		return getContaining_element(Element_types.MESSAGE);
	}
	
	public Mobs_element getMob_name()
	{
		return getContaining_element(Element_types.MOB_NAME);
	}
	
	public Mobs_element getMob_type()
	{
		return getContaining_element(Element_types.MOB_TYPE);
	}
	
	public Mobs_element getSubaction()
	{
		return getContaining_element(Element_types.SUBACTION_TYPE);
	}
	
	public Mobs_element getTarget_type()
	{
		return getContaining_element(Element_types.TARGET_TYPE);
	}
	

// value getters
	
	private String getElement_value(Element_types et)
	{
		Mobs_element mv = this;
		
		while (mv != null && !mv.wrappers.containsKey(et)) mv = mv.parent;
		if (mv == null) return null;					
		
		Object o = mv.wrappers.get(et).getValue();
		
		if (o instanceof String) return (String)o;
		
		return null;
	}
	
	public String getAction_type_value()
	{
		return getElement_value(Element_types.ACTION_TYPE);
	}
	
	public String getAmount_value()
	{
		return getElement_value(Element_types.AMOUNT);
	}
	
	public String getCondition_type_value()
	{
		if (wrappers.containsKey(Element_types.CONDITION_TYPE))
		{
			Object o = wrappers.get(Element_types.CONDITION_TYPE).getValue();
			
			if (o instanceof String) return (String)o;
		}
		return null;
	}
	
	public String getCondition_value_value()
	{
		return getElement_value(Element_types.CONDITION_VALUE);
	}
	
	public String getItem_data_value()
	{
		return getElement_value(Element_types.ITEM_DATA);
	}
	
	public String getItem_id_value()
	{
		return getElement_value(Element_types.ITEM_ID);
	}
	
	public String getMessage_value()
	{
		return getElement_value(Element_types.MESSAGE);
	}
	
	public String getMob_name_value()
	{
		return getElement_value(Element_types.MOB_NAME);
	}

	public String getMob_type_value()
	{
		return getElement_value(Element_types.MOB_TYPE);
	}		
	
	public String getPlayer_value()
	{
		return getElement_value(Element_types.PLAYER);
	}
	
	public String getSubaction_value()
	{
		return getElement_value(Element_types.SUBACTION_TYPE);
	}
	
	public String getTarget_type_value()
	{
		return getElement_value(Element_types.TARGET_TYPE);
	}

// rest
	
	public boolean isLocked()
	{
		String s = getElement_value(Element_types.LOCKED);
		return s == null ? true : Boolean.parseBoolean(s);
	}
	
	public boolean passesConditions(LivingEntity le, Projectile projectile, Event orig_event)
	{
		String cond = "";//getCondition_type_value();
		if (cond == null) return true;
		
		switch (Conditions.valueOf(cond.toUpperCase()))
		{
			case RAINING: return le.getWorld().hasStorm();
		}
		return false;
	}
}