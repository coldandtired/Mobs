package me.coldandtired.mobs.api;

import me.coldandtired.mobs.EventValues;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsConditionEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private final String name;
	private final String needed;
	private final String got;
	private boolean passed;
	private final EventValues ev;
	
	public MobsConditionEvent(String name, String needed, String got, boolean passed, EventValues ev)
	{
		this.name = name;
		this.needed = needed;
		this.got = got;
		this.passed = passed;
		this.ev = ev;
	}
	
	public String getConditionName()
	{
		return name;
	}
	
	public String getNeededValue()
	{
		return needed;
	}
	
	public String getActualValue()
	{
		return got;
	}
	
	public boolean hasPassed()
	{
		return passed;
	}
	
	public EventValues getEventValues()
	{
		return ev;
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