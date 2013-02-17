package me.coldandtired.mobs;

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
		
		if (timer == null) return null;
		
		return Bukkit.getWorld(timer.getWorld());
	}
}