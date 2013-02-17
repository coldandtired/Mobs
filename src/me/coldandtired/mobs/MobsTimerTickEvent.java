package me.coldandtired.mobs;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsTimerTickEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private Timer timer;
	
	public MobsTimerTickEvent(Timer timer)
	{
		this.timer = timer;
	}
	
	public Timer getTimer()
	{
		return timer;
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