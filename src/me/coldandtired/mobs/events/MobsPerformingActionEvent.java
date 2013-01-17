package me.coldandtired.mobs.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsPerformingActionEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private final String event;
	private final String mob;
	private final String loc;
	private final String action_verb;
	private final String action_name;
	private final String action_value;

	public MobsPerformingActionEvent(String event, String mob, String loc, String action_verb, String action_name, String action_value)
	{
		this.event = event;
		this.mob = mob;
		this.loc = loc;
		this.action_verb = action_verb;
		this.action_name = action_name;
		this.action_value = action_value;
	}
	
	public String getEvent()
	{
		return event;
	}
	
	public String getMob()
	{
		return mob;
	}
	
	public String getLoc()
	{
		return loc;
	}
	
	public String getAction_verb()
	{
		return action_verb;
	}
	
	public String getAction_name()
	{
		return action_name;
	}
	
	public String getAction_value()
	{
		return action_value;
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