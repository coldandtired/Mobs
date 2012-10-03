package me.coldandtired.mobs.events;

import org.bukkit.entity.LivingEntity;

public class Mob_blocks_event extends Base_event
{
	private LivingEntity entity;
	
	public Mob_blocks_event(LivingEntity le)
	{
		entity = le;
	}
	
	public LivingEntity getEntity()
	{
		return entity;
	}
}