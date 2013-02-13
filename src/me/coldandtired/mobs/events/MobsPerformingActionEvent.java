package me.coldandtired.mobs.events;

import java.util.List;

import me.coldandtired.mobs.EventValues;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsPerformingActionEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private final String event_name;
	private final String action_name;
	private final String action_value;
	private final List<Object> targets;

	public MobsPerformingActionEvent(String event_name, EventValues ev, String action_name, String action_value, List<Object> targets)
	{
		this.event_name = event_name;
		this.action_name = action_name;
		this.action_value = action_value;
		this.targets = targets;
	}
	
	public String getEventName()
	{
		return event_name;
	}	
	
	public String getActionName()
	{
		return action_name;
	}
	
	public String getActionValue()
	{
		return action_value;
	}
	
	public List<Object> getTargets()
	{
		return targets;
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