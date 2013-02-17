package me.coldandtired.mobs;

import me.coldandtired.mobs.api.MobsTimerTickingEvent;

import org.bukkit.Bukkit;

public class Timer
{
	private String name;
	private int interval;
	private int remaining;
	private boolean enabled = true;
	private String world;
	
	public Timer(String name, int interval, String world)
	{
		this.name = name;
		this.interval = interval;
		remaining = interval;
		this.world = world;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getInterval()
	{
		return interval;
	}
	
	public void setInterval(int interval)
	{
		this.interval = interval;
	}
	
	public String getWorld()
	{
		return world;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public void tick()
	{
		remaining--;
		if (remaining < 1)
		{
			remaining = interval;
			if (Mobs.canDebug())
			{
				MobsTimerTickingEvent mtte = new MobsTimerTickingEvent(name, interval, world);
				Bukkit.getServer().getPluginManager().callEvent(mtte);
				
				if (mtte.isCancelled()) return;
			}
			
			activate();
		}
	}
	
	public void activate()
	{
		Bukkit.getServer().getPluginManager().callEvent(new MobsTimerTickEvent(this));
	}

	public String check()
	{
		return "Timer " + name + " is " + (enabled ? "enabled, " : "disabled, ")
				+ interval + " second interval (" + remaining + " seconds left)";
	}
}