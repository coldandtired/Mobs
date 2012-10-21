package me.coldandtired.mobs.events;

import org.bukkit.World;

public class Dusk_event extends Base_event
{
private World world;
	
	public Dusk_event(World world)
	{
		this.world = world;
	}
	
	public World getWorld()
	{
		return world;
	}
}