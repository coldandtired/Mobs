package me.coldandtired.mobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;


import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.w3c.dom.Element;

public class Mobs_element
{
	private Mobs_element parent;
	private boolean passed = false;
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
	
	private Map<Element_types, Element_wrapper> wrappers = new HashMap<Element_types, Element_wrapper>();
	
	public boolean hasConditions()
	{
		return wrappers.containsKey(Element_types.CONDITION_TYPE);
	}
	
	public void setPassed()
	{
		passed = true;
	}
	
	public boolean hasPassed()
	{
		return passed;
	}
	
	public Mobs_element(Element element, Mobs_element parent) throws XPathExpressionException
	{
		//Mobs.log("localname = " + element.getLocalName());
		this.parent = parent;
		for (Element_types et : Element_types.values())
		{
			Element_wrapper ew = new Element_wrapper(element, et, this);
			if (ew.isFilled()) wrappers.put(et, ew);
		}
	}
	
	public Element_wrapper getWrapper(Element_types et)
	{
		return wrappers.get(et);
	}
	
	public List<Mobs_element> getActions(Bukkit_values bukkit_values) 
	{
		return wrappers.get(Element_types.ACTION_TYPE).getActions(this, bukkit_values);		
	}
	
	public List<Mobs_element> getConditions(Bukkit_values bukkit_values) 
	{
		return wrappers.get(Element_types.CONDITION_TYPE).getConditions(this, bukkit_values);		
	}
	
	public String getString(Element_types et, Bukkit_values bukkit_values)
	{
		return wrappers.get(et).getString(et, this, bukkit_values);
	}
	
// element getters
	
	private Mobs_element getContaining_element(Element_types et)
	{
		Mobs_element mv = this;
		
		while (mv != null && !mv.wrappers.containsKey(et)) mv = mv.parent;
		if (mv == null) return this;
		
		//Element_wrapper wrapper = mv.wrappers.get(et);
		
		//if (wrapper.getValue() instanceof String) 
			return mv;
		
		//return wrapper.getContainer();
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
		
		//Object o = mv.wrappers.get(et).getValue();
		
		//if (o instanceof String) return (String)o;
		
		return "nope";//o.toString();
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
		return getElement_value(Element_types.CONDITION_TYPE);
		/*if (wrappers.containsKey(Element_types.CONDITION_TYPE))
		{
			Object o = wrappers.get(Element_types.CONDITION_TYPE).getValue();
			
			if (o instanceof String) return (String)o;
		}
		return null;*/
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
		//String cond = getCondition_type_value();
		//if (cond == null) return true;
		
	//	switch (Conditions.valueOf(cond.toUpperCase()))
	//	{
	//		case RAINING: return le.getWorld().hasStorm();
	//	}
		return false;
	}
}