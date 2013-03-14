package me.coldandtired.mobs.api;

import me.coldandtired.mobs.Enums.ReasonType;
import me.coldandtired.mobs.EventValues;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsFailedActionEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private final String attempted;
	private final ReasonType reason;
	private final EventValues ev;

	public MobsFailedActionEvent(String attempted, ReasonType reason, EventValues ev)
	{
		this.attempted = attempted;
		this.reason = reason;
		this.ev = ev;
	}		

	public String getAttempted()
	{
		return attempted;
	}
	
	public ReasonType getReason()
	{
		return reason;
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