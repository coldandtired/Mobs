package me.coldandtired.mobs;

import java.util.HashSet;
import java.util.Set;

import me.coldandtired.mobs.Enums.EventType;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;

public class EventValues
{
	private Event orig_event;
	private EventType mobs_event;
	private LivingEntity le;
	private LivingEntity aux_mob;
	private Projectile projectile;
	private Timer timer;
	private Set<Integer> checked_elements = new HashSet<Integer>();	
	
	public EventValues(Event orig_event, EventType mobs_event, LivingEntity le, LivingEntity aux_mob, Projectile projectile, Timer timer)
	{
		this.orig_event = orig_event;
		this.mobs_event = mobs_event;
		this.le = le;
		this.aux_mob = aux_mob;
		this.projectile = projectile;
		this.timer = timer;
	}
	
	public Event getOrigEvent()
	{
		return orig_event;
	}

	public EventType getMobsEvent()
	{
		return mobs_event;
	}
	
	public LivingEntity getLivingEntity()
	{
		return le;
	}
	
	public LivingEntity getAuxMob()
	{
		return aux_mob;
	}
	
	public Projectile getProjectile()
	{
		return projectile;
	}
	
	public World getWorld()
	{
		if (le != null) return le.getWorld();
		
		return Bukkit.getWorld(timer.getWorld());
	}

	public void addCheckedElement(MobsElement me)
	{
		checked_elements.add(me.hashCode());
	}
	
	public boolean alreadyPassed(MobsElement me)
	{
		return checked_elements.contains(me.hashCode());
	}
}