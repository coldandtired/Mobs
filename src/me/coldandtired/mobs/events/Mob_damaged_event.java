package me.coldandtired.mobs.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class Mob_damaged_event extends Base_event
{
	private final LivingEntity entity;
	private EntityDamageEvent orig_event;
	
	public Mob_damaged_event(LivingEntity le, EntityDamageEvent orig_event)
	{
		entity = le;
		this.orig_event = orig_event;
	}
	
	public LivingEntity getEntity()
	{
		return entity;
	}

	public EntityDamageEvent getOrig_event()
	{
		return orig_event;
	}
}