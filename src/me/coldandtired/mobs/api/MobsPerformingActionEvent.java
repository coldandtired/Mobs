package me.coldandtired.mobs.api;

import me.coldandtired.mobs.MobsEvent;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsPerformingActionEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private final String attempting;
	private final MobsEvent outcome;

	public MobsPerformingActionEvent(String attempting, MobsEvent outcome)
	{
		this.attempting = attempting;
		this.outcome = outcome;
	}
	
	public String getAttempting()
	{
		return attempting;
	}
	
	public MobsEvent getOutcome()
	{
		return outcome;
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