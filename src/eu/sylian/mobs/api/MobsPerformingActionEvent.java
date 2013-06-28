package eu.sylian.mobs.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.sylian.mobs.EventValues;

public class MobsPerformingActionEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private final String attempting;
	private final EventValues ev;

	public MobsPerformingActionEvent(String attempting, EventValues ev)
	{
		this.attempting = attempting;
		this.ev = ev;
	}
	
	public String getAttempting()
	{
		return attempting;
	}
	
	public EventValues getEventValues()
	{
		return ev;
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