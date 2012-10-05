package me.coldandtired.mobs.subelements;

import org.bukkit.entity.Entity;

public class Nearby_mob 
{
	private Entity le;
	private Double distance;
	
	public Nearby_mob(Entity le, double distance)
	{
		this.le = le;
		this.distance = distance;
	}
	
	public Entity getEntity()
	{
		return le;
	}
	
	public Double getDistance()
	{
		return distance;
	}
}