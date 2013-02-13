package me.coldandtired.mobs.events;

import me.coldandtired.mobs.EventValues;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsPassedConditionEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	public MobsPassedConditionEvent(EventValues bukkit_values, String condition_type, String condition_value)
	{
		// TODO Auto-generated constructor stub
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