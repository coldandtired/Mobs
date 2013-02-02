package me.coldandtired.mobs.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsFailedConditionEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	public MobsFailedConditionEvent(String event_name, String mob_type,
			String l, String action_verb, String action_type,
			String action_value)
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