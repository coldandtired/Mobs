package me.coldandtired.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import me.coldandtired.extra_events.LivingEntityBlockEvent;
import me.coldandtired.extra_events.LivingEntityDamageEvent;
import me.coldandtired.extra_events.PlayerApproachLivingEntityEvent;
import me.coldandtired.extra_events.PlayerLeaveLivingEntityEvent;
import me.coldandtired.extra_events.PlayerNearLivingEntityEvent;
import me.coldandtired.mobs.Event_listener.MEvents;
import me.coldandtired.mobs.Mobs_element.MAttributes;
import me.coldandtired.mobs.events.MobsFailedActionEvent;
import me.coldandtired.mobs.events.MobsPerformingActionEvent;

public class Mobs_event
{	
	private Mobs_element values;
	private enum MAction_verbs { BREAK, CANCEL_EVENT, CAUSE, DAMAGE, GIVE, KILL, PLAY, 
		REMOVE, SET, SPAWN, TOGGLE, WRITE };
	private enum MTargets { AREA, AROUND, AUX_MOB, BLOCK, NEAREST, 	PLAYER,	RANDOM,	SELF };
	private boolean allow_debug;
	private String event_name;
	
	private Mobs_event(String event_name, Element element, boolean allow_debug) throws XPathExpressionException
	{
		values = new Mobs_element(element);
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
	public void performActions(LivingEntity le, Projectile projectile, Event orig_event)
	{
		for (Mobs_element me : values.getActions(le, projectile, orig_event))
		{
			for (String s : me.getValue(MAttributes.ACTION_TYPE))
			{
				for (Object target : getTargets(me, le, orig_event)) performAction(target, projectile, orig_event, s.toUpperCase());
			}
		}
	}
		
	/** Performs the action on the target (if valid) */
	private void performAction(Object target, Projectile projectile, Event orig_event, String action_type)
	{
		MAction_verbs action_verb = getAction_verb(action_type);
		if (action_verb != null) action_type = action_type.replaceFirst(action_verb.toString() + "_", "");
		
		switch (action_verb)
		{
			case CANCEL_EVENT: cancelEvent(target, orig_event);
				break;
		
			case KILL: killSomething(target);
				break;
				
			/*case REMOVE: clearSomething(o);
				break;
			
			case SET: setSomething(o);
				break;
			
			case TOGGLE: toggleSomething(o);
				break;*/
		}		
	}
	
	/** Cancels the original Bukkit event */
	private void cancelEvent(Object target, Event orig_event)
	{
		if (orig_event instanceof Cancellable)
		{		
			boolean b = allow_debug ? callPerformingActionEvent(target, "cancel_event", "", "").isCancelled() : true;
			
			if (b) ((Cancellable)orig_event).setCancelled(true);
		}
		else if (allow_debug) callFailedActionEvent(target, "cancel_event", "", "");
	}
	
	/** Kills a mob */
	private void killSomething(Object target)
	{
		if (target instanceof Location)
		{
			if (allow_debug) callFailedActionEvent(target, "kill", "", "");
			return;
		}
		
		boolean b = allow_debug ? callPerformingActionEvent(target, "kill", "", "").isCancelled() : true;
		if (b)
		{
			LivingEntity le = (LivingEntity)target;	
			le.setHealth(0);
		}
	}
	
// Utils
	
	/** Strips the verb from the whole action */
	private MAction_verbs getAction_verb(String action_type)
	{
		if (action_type.startsWith("BREAK")) return MAction_verbs.BREAK;
		if (action_type.startsWith("CANCEL_EVENT")) return MAction_verbs.CANCEL_EVENT;
		if (action_type.startsWith("CAUSE_")) return MAction_verbs.CAUSE;
		if (action_type.startsWith("DAMAGE")) return MAction_verbs.DAMAGE;
		if (action_type.startsWith("GIVE_")) return MAction_verbs.GIVE;
		if (action_type.startsWith("KILL")) return MAction_verbs.KILL;
		if (action_type.startsWith("PPLAY_")) return MAction_verbs.PLAY;
		if (action_type.startsWith("REMOVE_")) return MAction_verbs.REMOVE;
		if (action_type.startsWith("SET_")) return MAction_verbs.SET;
		if (action_type.startsWith("SPAWN_")) return MAction_verbs.SPAWN;
		if (action_type.startsWith("TOGGLE_")) return MAction_verbs.TOGGLE;
		if (action_type.startsWith("WRITE_")) return MAction_verbs.WRITE;
		return null;
	}
	
	/** Returns a list of objects (LivingEntity or Location) to have actions performed on */
	private List<Object> getTargets(Mobs_element me, LivingEntity le, Event orig_event)
	{
		List<Object> targets = new ArrayList<Object>();
		List<String> target_types = me.getValue(MAttributes.TARGET_TYPE);
		if (target_types == null)
		{
			if (le != null) targets.add(le);
			return targets;
		}
		
		for (String target : target_types)
		{
			MTargets target_type = MTargets.valueOf(target.toUpperCase());
			switch (target_type)
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
					for (String s : me.getValue(MAttributes.PLAYER)) targets.add(Bukkit.getPlayer(s));
					/*for (String s : getPlayer())
						targets.add(Bukkit.getPlayer(s));*/
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
					if (le != null) targets.add(le);
					break;
			}
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
	private void callFailedActionEvent(Object o, String action_verb, String action_type, String action_value)
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
		MobsFailedActionEvent mfae = new MobsFailedActionEvent(event_name, mob_type, l, action_verb, action_type, action_value);
		Bukkit.getServer().getPluginManager().callEvent(mfae);
	}
}