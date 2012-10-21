package me.coldandtired.mobs.events;

import org.bukkit.World;

public class Midday_event extends Base_event
{
private World world;
	
	public Midday_event(World world)
	{
		this.world = world;
	}
	
	public World getWorld()
	{
		return world;
	}
}