package me.coldandtired.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import me.coldandtired.extra_events.LivingEntityBlockEvent;
import me.coldandtired.extra_events.LivingEntityDamageEvent;
import me.coldandtired.extra_events.PlayerApproachLivingEntityEvent;
import me.coldandtired.extra_events.PlayerLeaveLivingEntityEvent;
import me.coldandtired.extra_events.PlayerNearLivingEntityEvent;
import me.coldandtired.mobs.Event_listener.MEvents;
import me.coldandtired.mobs.events.MobsFailedActionEvent;
import me.coldandtired.mobs.events.MobsPerformingActionEvent;

public class Mobs_event
{	
	private Mobs_element actions;
	private enum Action_types {
		//BREAK,
		CANCEL_EVENT,
		CAUSE,
		//DAMAGE,
		//GIVE,
		KILL,
		//PLAY, 
		//REMOVE,
		//SET,
		SPAWN,
		//TOGGLE,
		WRITE };
	public enum Subaction_types {
		//ADULT,
		//ANGRY,
		//ATTACK_POWER,
		//BLOCK,
		BROADCAST,
	//	CLOSED,
	 //   DAMAGE_TAKEN,
	//    DATA,
	//    DROPPED_EXP,
	//    DROPPED_ITEMS,
	    EXP,
	    EXPLOSION,
	//    EXPLOSION_SIZE,
	    FIERY_EXPLOSION,
	//    FRIENDLY,
	 //   HP,
	    ITEM,
	//    ITEMS,
	 //   LEVEL,
	    LIGHTNING,
	    LIGHTNING_EFFECT,
	    LOG,
	//    MAX_HP,
	//    MAX_LIFE,
	//    MECHANISM,
	    MESSAGE,
	    MOB,
	//    MONEY,
	//    NAME,
	//    NO_BURN,	
	//	NO_CREATE_PORTALS,
	//	NO_DESTROY_BLOCKS,
	//	NO_DYED,
	//	NO_EVOLVE,		
	//	NO_FIERY_EXPLOSION,	
	//	NO_GRAZE,
	//	NO_GROW_WOOL,
	//	NO_HEAL,
	//	NO_MOVE_BLOCKS,
	//	NO_PICK_UP_ITEMS,
	//	NO_SADDLED,
	//	NO_SHEARING,
	//	NO_TAMING,
	//	NO_TELEPORT,
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
	private enum Target_types { AREA, AROUND, AUX_MOB, BLOCK, NEAREST, 	PLAYER,	RANDOM,	SELF };
	private boolean allow_debug;
	private String event_name;
	
	private Mobs_event(String event_name, Element element, boolean allow_debug) throws XPathExpressionException
	{
		actions = new Mobs_element(element);
		this.event_name = event_name;
		this.allow_debug = allow_debug;
	}
	
	/** Returns a new Mobs_event if the relevant file exists */
	public static Mobs_event fill(MEvents event_name, boolean allow_debug) throws XPathExpressionException
	{
		File f = new File(Mobs.getInstance().getDataFolder(), event_name.toString().toLowerCase() + ".txt");
		if (!f.exists()) return null;
		
		return new Mobs_event(event_name.toString(), (Element)Mobs.getXPath().evaluate("event", new InputSource(f.getPath()), XPathConstants.NODE), allow_debug);
	}
	
	/** Performs all the actions on all the targets */
	public void performActions(Bukkit_values bukkit_values)
	{
		for (Mobs_element me : actions.getActions())
		{			
			switch (Action_types.valueOf(me.getAction_type_value().toUpperCase()))
			{
				case CANCEL_EVENT: cancelEvent(bukkit_values);
					break;
			
			//	case CAUSE: causeSomething(me, bukkit_values);
			//		break;
					/*case DAMAGE: damageMob(o);
					break;
				case DROP: dropSomething(o);
					break;
				case GIVE: giveSomething(o);
					break;*/
			//	case KILL: killSomething(bukkit_values);
			//		break;					
					
				/*case PLAY: playSomething(o);
					break;
				case REMOVE: removeSomething(o);
					break;
				case SET: setProperties(o);
					break;*/
				case SPAWN: spawnSomething(me, bukkit_values);
					break;
				/*case TOGGLE: toggleSomething(o);
					break;*/
					
				case WRITE: writeSomething(me, bukkit_values);
					break;						
//
			}	
		}
	}
	
	/** Breaks a block */
	/*private void breakBlock(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		List<Item_drop> items = currents.getItems();
		Action_report ar = new Action_report("BREAK", getString_from_loc(loc));
		if (items == null)
		{
			loc.getBlock().breakNaturally();
			ar.setValue("naturally");
		}
		else
		{
			ItemStack is = items.get(0).getItemstacks(0, 0).get(0);
			Mobs.log(items.get(0).toString());
			loc.getBlock().breakNaturally(is);
			ar.setValue(getString_from_item(is));
		}
		ar.setSuccess();
		report.addAction(ar);
	}*/
	
	/** Cancels the original Bukkit event */
	private void cancelEvent(Bukkit_values bukkit_values)
	{
		if (bukkit_values.getOrig_event() instanceof Cancellable)
		{		
			boolean b = allow_debug ? !callPerformingActionEvent(bukkit_values, "cancel_event", "", "").isCancelled() : true;
			
			if (b) ((Cancellable)bukkit_values.getOrig_event()).setCancelled(true);
		}
		else if (allow_debug) callFailedActionEvent(bukkit_values, "cancel_event", "", "", "Event cannot be cancelled");
	}
	
// Cause action_type
	
	/** Causes explosions, lightning, etc. */
	/*private void causeSomething(Mobs_element me, Bukkit_values bukkit_values)
	{
		List<String> temp = me.getElement_type(Element_types.SUBACTION_TYPE);
		if (temp == null)
		{
			if (allow_debug) callFailedActionEvent(bukkit_values, "cause", "", "", "No subaction type found");
			return;
		}
		
		for (String s : temp)
		{			
			switch (Subaction_types.valueOf(s.toUpperCase()))
			{
				case EXPLOSION: explode(me, bukkit_values);
					break;
				case FIERY_EXPLOSION: explodeFire(me, bukkit_values);
					break;
				case LIGHTNING: lightningStrike(bukkit_values);
					break;
				case LIGHTNING_EFFECT: lightningEffect(bukkit_values);
					break;
			}
		}
	}*/
	
	/** Causes an explosion */
	/*private void explode(Mobs_element me, Bukkit_values bukkit_values)
	{
		if (target == null)
		{
			if (allow_debug) callFailedActionEvent(target, "cause", "explosion", "", "No target found");
			return;
		}
		
		Location loc = target instanceof Location ? (Location)target : ((LivingEntity)target).getLocation();
		List<String> temp = me.getElement_type(Element_types.AMOUNT);

		if (temp == null ) loc.getWorld().createExplosion(loc, 0);
		else
		for (String s : temp)
		{
			int i = getNumber(s);
			boolean b = allow_debug ? !callPerformingActionEvent(target, "cause", "explosion", Integer.toString(i)).isCancelled() : true;
			if (b) loc.getWorld().createExplosion(loc, i);
		}
	}*/

	/** Causes an explosion which sets blocks on fire */
	/*private void explodeFire(Mobs_element me, Bukkit_values bukkit_values)
	{
		if (target == null)
		{
			if (allow_debug) callFailedActionEvent(target, "cause", "fiery_explosion", "", "No target found");
			return;
		}
		
		Location loc = target instanceof Location ? (Location)target : ((LivingEntity)target).getLocation();
		List<String> temp = me.getElement_type(Element_types.AMOUNT);

		if (temp == null ) loc.getWorld().createExplosion(loc, 0, true);
		else
		for (String s : temp)
		{
			int i = getNumber(s);
			boolean b = allow_debug ? !callPerformingActionEvent(target, "cause", "fiery_explosion", Integer.toString(i)).isCancelled() : true;
			if (b) loc.getWorld().createExplosion(loc, i, true);
		}
	}*/
	
	/** Strikes a location or mob with lightning */
	/*private void lightningStrike(Bukkit_values bukkit_values)
	{
		if (target == null)
		{
			if (allow_debug) callFailedActionEvent(target, "cause", "lightning", "", "No target found");
			return;
		}
		
		Location loc = target instanceof Location ? (Location)target : ((LivingEntity)target).getLocation();
		boolean b = allow_debug ? !callPerformingActionEvent(target, "cause", "lightning", "").isCancelled() : true;
		if (b) loc.getWorld().strikeLightning(loc);
	}*/

	/** Shows lightning without damaging a location or mob */
	/*private void lightningEffect(Bukkit_values bukkit_values)
	{
		if (target == null)
		{
			if (allow_debug) callFailedActionEvent(target, "cause", "lightning_effect", "", "No target found");
			return;
		}
		
		Location loc = target instanceof Location ? (Location)target : ((LivingEntity)target).getLocation();
		boolean b = allow_debug ? !callPerformingActionEvent(target, "cause", "lightning_effect", "").isCancelled() : true;
		if (b) loc.getWorld().strikeLightningEffect(loc);
	}*/
	
	/** Kills a mob */
	/*private void killSomething(Bukkit_values bukkit_values)
	{
		if (target == null || target instanceof Location)
		{
			if (allow_debug) callFailedActionEvent(target, "kill", "", "", "Target not a mob");
			return;
		}
		
		boolean b = allow_debug ? !callPerformingActionEvent(target, "kill", "", "").isCancelled() : true;
		if (b)
		{
			LivingEntity le = (LivingEntity)target;	
			le.setHealth(0);
		}
	}*/

// Spawn action_type
	
	/** Spawns a mob/item/exp_orb, etc. */
	private void spawnSomething(Mobs_element me, Bukkit_values bukkit_values)
	{
		Mobs_element temp = me.getSubaction();
		if (temp == null)
		{
			if (allow_debug) callFailedActionEvent(bukkit_values, "spawn", "", "", "No subaction type found");
			return;
		}
		
		switch (Subaction_types.valueOf(temp.getSubaction_value().toUpperCase()))
		{
			case EXP: spawnExp(me, bukkit_values);
				break;
			case ITEM: spawnItem(me, bukkit_values);
				break;
			case MOB: spawnMob(me, bukkit_values);
				break;
		}
	}

	/** Spawns an exp orb at a location */
	private void spawnExp(Mobs_element me, Bukkit_values bukkit_values)
	{
		Mobs_element temp = me.getAmount();
		int amount = getNumber(temp.getAmount_value(), 1);
		
		List<Object> targets = getTargets(temp, bukkit_values);
		if (targets == null)
		{
			if (allow_debug) callFailedActionEvent(null, "spawn", "exp", "", "No targets found");
			return;
		}
		
		boolean b = allow_debug ? !callPerformingActionEvent(bukkit_values, "spawn", "exp", "").isCancelled() : true;
		if (!b) return;
		
		for (Object o : targets)
		{
			Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
			ExperienceOrb orb = (ExperienceOrb)loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
			orb.setExperience(amount);
			if (!me.isLocked())
			{
				temp = me.getAmount();
				amount = getNumber(temp.getAmount_value(), 1);
			}
		}
	}
	
	/** Spawns an item at a location */
	private void spawnItem(Mobs_element me, Bukkit_values bukkit_values)
	{		
		Mobs_element temp = me.getItem_id();
		
		String id = temp.getItem_id_value();
		if (id == null)
		{
			if (allow_debug) callFailedActionEvent(bukkit_values, "spawn", "item", "", "No item_id found");
			return;
		}
		
		temp = temp.getItem_data();
		int data = getNumber(temp.getItem_data_value(), 0);		
		
		temp = temp.getAmount();
		int amount = getNumber(temp.getAmount_value(), 1);
		
		List<Object> targets = getTargets(temp, bukkit_values);
		if (targets == null)
		{
			if (allow_debug) callFailedActionEvent(null, "spawn", "item", "", "No targets found");
			return;
		}
		
		boolean b = allow_debug ? !callPerformingActionEvent(bukkit_values, "spawn", "item", "").isCancelled() : true;
		if (!b) return;
		
		for (Object o : targets)
		{
			Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
			loc.getWorld().dropItem(loc, new ItemStack(getNumber(id, 0), amount, (short)data));
			if (!me.isLocked())
			{
				temp = me.getItem_id();
				id = temp.getItem_id_value();
				
				temp = temp.getItem_data();
				data = getNumber(temp.getItem_data_value(), 0);
				
				temp = temp.getAmount();
				amount = getNumber(temp.getAmount_value(), 1);
			}
		}
	}
	
	/** Spawns a mob at a location */
	private void spawnMob(Mobs_element me, Bukkit_values bukkit_values)
	{
		Mobs_element temp = me.getMob_type();
		
		String mob_type = temp.getMob_type_value();
		if (mob_type == null)
		{
			if (allow_debug) callFailedActionEvent(bukkit_values, "spawn", "mob", "", "No mob_type found");
			return;
		}
		
		temp = temp.getMob_name();
		String mob_name = temp.getMob_name_value();		
		
		temp = temp.getAmount();
		int amount = getNumber(temp.getAmount_value(), 1);
		
		List<Object> targets = getTargets(temp, bukkit_values);
		if (targets == null)
		{
			if (allow_debug) callFailedActionEvent(null, "spawn", "mob", "", "No targets found");
			return;
		}
		
		boolean b = allow_debug ? !callPerformingActionEvent(bukkit_values, "spawn", "mob", "").isCancelled() : true;
		if (!b) return;
		
		for (int i = 0; i < amount; i++)
		{
			for (Object o : targets)
			{
				Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
				Mobs.getInstance().setMob_name(mob_name);
				loc.getWorld().spawnEntity(loc, EntityType.valueOf(mob_type.toUpperCase()));
				if (!me.isLocked())
				{
					temp = me.getMob_type();
					mob_type = temp.getMob_type_value();
					
					temp = temp.getMob_name();
					mob_name = temp.getMob_name_value();		
				}
			}
		}
	}
	
// Write action_type	
	
	/** Sends a message to the log/a player/the server */
	private void writeSomething(Mobs_element me, Bukkit_values bukkit_values)
	{
		Mobs_element temp = me.getSubaction();
		if (temp == null)
		{
			if (allow_debug) callFailedActionEvent(bukkit_values, "write", "", "", "No subaction type found");
			return;
		}
		
		switch (Subaction_types.valueOf(temp.getSubaction_value().toUpperCase()))
		{
		//	case BROADCAST: broadcastMessage(me);
		//		break;
			case LOG: logMessage(me, bukkit_values);
				break;
		//	case MESSAGE: sendMessage(me, target);
		//		break;
		}	
	}
	
	/** Sends a message to everyone on the server */
	/*private void broadcastMessage(Mobs_element me, Bukkit_values bukkit_values)
	{
		List<String> temp = me.getElement_type(Element_types.MESSAGE);
		if (temp == null)
		{
			if (allow_debug) callFailedActionEvent(null, "write", "", "", "No message found");
			return;
		}
		
		for (String s : temp)
		{
			boolean b = allow_debug ? !callPerformingActionEvent(null, "write", "broadcast", s).isCancelled() : true;
			if (b) Bukkit.getServer().broadcastMessage(s);
		}
	}*/
	
	/** Writes a message to the console */
	private void logMessage(Mobs_element me, Bukkit_values bukkit_values)
	{
		Mobs_element temp = me.getMessage();
		if (temp == null)
		{
			if (allow_debug) callFailedActionEvent(bukkit_values, "write", "log", "", "No message found");
			return;
		}
		
		String s = temp.getMessage_value();
		boolean b = allow_debug ? !callPerformingActionEvent(null, "write", "log", s).isCancelled() : true;
		if (b) Mobs.log(s);
	}
	
	/** Sends a message to one player */
	/*private void sendMessage(Mobs_element me, Bukkit_values bukkit_values)
	{
		if (!(target instanceof Player))
		{
			if (allow_debug) callFailedActionEvent(target, "write", "message", "", "Target isn't a player");
			return;
		}
		
		List<String> temp = me.getElement_type(Element_types.MESSAGE);
		if (temp == null)
		{
			if (allow_debug) callFailedActionEvent(target, "write", "message", "", "No message found");
			return;
		}
		
		for (String s : temp)
		{
			boolean b = allow_debug ? !callPerformingActionEvent(target, "write", "log", s).isCancelled() : true;
			if (b) ((Player)target).sendMessage(s);
		}
	}*/
	
// Utils
	
	/** Returns a randomized number */
	private int getNumber(String s, int orig)
	{
		if (s == null) return orig;
		
		s = s.replace(" ", "").toUpperCase();
		String[] temp = s.split(",");
		String s2 = temp[new Random().nextInt(temp.length)];
		if (s2.contains("TO"))
		{
			String[] temp2 = s2.split("TO");
			return new Random().nextInt(Integer.parseInt(temp2[1]) - Integer.parseInt(temp2[0])) +
					Integer.parseInt(temp2[0]);
		}
		else return Integer.parseInt(s2);
	}
	
	/** Returns a list of objects (LivingEntity or Location) to have actions performed on */
	private List<Object> getTargets(Mobs_element me, Bukkit_values bukkit_values)
	{
		List<Object> targets = new ArrayList<Object>();
		String target_type = me.getTarget_type().getTarget_type_value();
		if (target_type == null)
		{
			if (bukkit_values.getLivingEntity() != null) targets.add(bukkit_values.getLivingEntity());
			return targets;
		}
		
		Event orig_event = bukkit_values.getOrig_event();
		switch (Target_types.valueOf(target_type))
		{
			case AUX_MOB:
				if (orig_event instanceof EntityDamageByEntityEvent)
				{
					Entity ee = ((EntityDamageByEntityEvent)orig_event).getDamager();
					if (ee instanceof LivingEntity) targets.add(ee);
				}
				else if (orig_event instanceof PlayerApproachLivingEntityEvent)
					targets.add(((PlayerApproachLivingEntityEvent)orig_event).getPlayer());
				else if (orig_event instanceof PlayerLeaveLivingEntityEvent)
					targets.add(((PlayerLeaveLivingEntityEvent)orig_event).getPlayer());
				else if (orig_event instanceof PlayerNearLivingEntityEvent)
					targets.add(((PlayerNearLivingEntityEvent)orig_event).getPlayer());
				else if (orig_event instanceof LivingEntityBlockEvent)
					targets.add(((LivingEntityBlockEvent)orig_event).getAttacker());
				else if (orig_event instanceof LivingEntityDamageEvent)
					targets.add(((LivingEntityDamageEvent)orig_event).getAttacker());
				else if (orig_event instanceof EntityTargetLivingEntityEvent)
					targets.add(((EntityTargetLivingEntityEvent)orig_event).getTarget());
				else if (orig_event instanceof EntityTameEvent)
					targets.add((LivingEntity) ((EntityTameEvent)orig_event).getOwner());
				else if (orig_event instanceof PlayerShearEntityEvent)
					targets.add(((PlayerShearEntityEvent)orig_event).getPlayer());
				else if (orig_event instanceof EntityDeathEvent)
					targets.add(((EntityDeathEvent)orig_event).getEntity().getKiller());
				else if (orig_event instanceof PlayerDeathEvent)
					targets.add(((PlayerDeathEvent)orig_event).getEntity().getKiller());
			
				break;			
			case PLAYER:
				String s = me.getPlayer_value();
				if (s != null) targets.add(Bukkit.getPlayer(s));
				break;
			/*case NEAREST:
				for (LivingEntity l : getNearest(le.getNearbyEntities(50, 10, 50), le))
					targets.add(l);
				break;
			case RANDOM:
				for (LivingEntity l : getNearby(le.getWorld().getEntities()))
					targets.add(l);
				break;
			case BLOCK:
				World w = getWorld(le);
				targets.add(new Location(w, getX().getInt_value(0), getY().getInt_value(0), getZ().getInt_value(0)));
				/*
				for (int x : getX())
				{
					for (int y : getY())
					{
						if (y > w.getMaxHeight()) y = w.getMaxHeight();
						for (int z : getZ())
						{
							targets.add(new Location(w, x, y, z));
						}
					}
				}
				*/
			//	break;
			default:
				if (bukkit_values.getLivingEntity() != null) targets.add(bukkit_values.getLivingEntity());
				break;
		}
		return targets;
	}
	
	/** Calls an event when an action is about to be performed, with the possibility of cancelling */
	private MobsPerformingActionEvent callPerformingActionEvent(Object o, String action_verb, String action_type, String action_value)
	{
		String mob_type = null;
		Location loc;
		if (o instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity)o;
			loc = le.getLocation();
			mob_type = le.getType().toString();
		}
		else loc = (Location)o;
		String l = loc.getWorld().getName() + ", " + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
		MobsPerformingActionEvent mpae = new MobsPerformingActionEvent(event_name, mob_type, l, action_verb.toString(), action_type, action_value);
		Bukkit.getServer().getPluginManager().callEvent(mpae);
		return mpae;
	}
	
	/** Calls an event when an action fails (due to wrong type of mob, etc.) */
	private void callFailedActionEvent(Object o, String action_type, String subaction_type, String action_value, String failed_reason)
	{
		String mob_type = null;
		Location loc;
		if (o instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity)o;
			loc = le.getLocation();
			mob_type = le.getType().toString();
		}
		else loc = (Location)o;
		String l = loc.getWorld().getName() + ", " + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
		Bukkit.getServer().getPluginManager().callEvent(new MobsFailedActionEvent(event_name, mob_type, l, action_type, subaction_type, action_value));
	}
	
	/** Calls an event when an condition fails (due to wrong type of mob, etc.) */
	/*private void callFailedConditionEvent(Object o, String action_verb, String action_type, String action_value)
	{
		String mob_type = null;
		Location loc;
		if (o instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity)o;
			loc = le.getLocation();
			mob_type = le.getType().toString();
		}
		else loc = (Location)o;
		String l = loc.getWorld().getName() + ", " + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
		Bukkit.getServer().getPluginManager().callEvent(new MobsFailedConditionEvent(event_name, mob_type, l, action_verb, action_type, action_value));
	}*/
	
	/** Calls an event when an condition fails (due to wrong type of mob, etc.) */
	/*private void callPassedConditionEvent(Object o, String action_verb, String action_type, String action_value)
	{
		String mob_type = null;
		Location loc;
		if (o instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity)o;
			loc = le.getLocation();
			mob_type = le.getType().toString();
		}
		else loc = (Location)o;
		String l = loc.getWorld().getName() + ", " + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
		Bukkit.getServer().getPluginManager().callEvent(new MobsPassedConditionEvent(event_name, mob_type, l, action_verb, action_type, action_value));
	}*/
}