package eu.sylian.mobs;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class NearbyMob
{
	private LivingEntity le;
	private Double distance;
	
	public NearbyMob(LivingEntity le, Location loc)
	{
		this.le = le;
		distance = le.getLocation().distance(loc);
	}
	
	public LivingEntity getLivingEntity()
	{
		return le;
	}
	
	public Double getDistance()
	{
		return distance;
	}
}