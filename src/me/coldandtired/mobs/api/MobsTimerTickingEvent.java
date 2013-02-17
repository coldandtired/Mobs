package me.coldandtired.mobs.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsTimerTickingEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private String name;
	private int interval;
	private String world;
	
	public MobsTimerTickingEvent(String name, int interval, String world)
	{
		this.name = name;
		this.interval = interval;
		this.world = world;
	}	
	
	public String getTimerName()
	{
		return name;
	}
	
	public int getTimerInterval()
	{
		return interval;
	}
		
	public String getTimerWorld()
	{
		return world;
	}
	
	@Override
	public boolean isCancelled() 
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) 
	{
		this.cancelled = cancelled;
	}
	
	@Override
	public HandlerList getHandlers() 
	{
		return handlers;
	}

	public static HandlerList getHandlerList() 
	{
	    return handlers;
	}
}