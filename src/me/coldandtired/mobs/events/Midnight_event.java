package me.coldandtired.mobs.events;

import org.bukkit.World;

public class Midnight_event extends Base_event
{
private World world;
	
	public Midnight_event(World world)
	{
		this.world = world;
	}
	
	public World getWorld()
	{
		return world;
	}
}