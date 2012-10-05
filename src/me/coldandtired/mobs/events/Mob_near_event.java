package me.coldandtired.mobs.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Mob_near_event extends Base_event
{
	private LivingEntity entity;
	private Player player;
	
	public Mob_near_event(LivingEntity le, Player player)
	{
		entity = le;
		this.player = player;
	}
	
	public LivingEntity getEntity()
	{
		return entity;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}