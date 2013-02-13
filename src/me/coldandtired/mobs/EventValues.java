package me.coldandtired.mobs;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;

public class EventValues
{
	private LivingEntity le;
	private Projectile projectile;
	private Event orig_event;
	private String mobs_event;
	
	public EventValues(LivingEntity le, Projectile projectile, Event orig_event, String mobs_event)
	{
		this.le = le;
		this.projectile = projectile;
		this.orig_event = orig_event;
		this.mobs_event = mobs_event;
	}
	
	public LivingEntity getLivingEntity()
	{
		return le;
	}
	
	public Projectile getProjectile()
	{
		return projectile;
	}
	
	public Event getOrigEvent()
	{
		return orig_event;
	}

	public String getMobsEvent()
	{
		return mobs_event;
	}
}