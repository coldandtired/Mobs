package me.coldandtired.mobs;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;

public class Bukkit_values
{
	LivingEntity le;
	Projectile projectile;
	Event orig_event;
	
	public Bukkit_values(LivingEntity le, Projectile projectile, Event orig_event)
	{
		this.le = le;
		this.projectile = projectile;
		this.orig_event = orig_event;
	}
	
	public LivingEntity getLivingEntity()
	{
		return le;
	}
	
	public Projectile getProjectile()
	{
		return projectile;
	}
	
	public Event getOrig_event()
	{
		return orig_event;
	}
}