package me.coldandtired.mobs.api;

import me.coldandtired.mobs.Enums.ReasonType;
import me.coldandtired.mobs.MobsEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsFailedActionEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private final String attempted;
	private final ReasonType reason;
	private final MobsEvent outcome;

	public MobsFailedActionEvent(String attempted, ReasonType reason, MobsEvent outcome)
	{
		this.attempted = attempted;
		this.reason = reason;
		this.outcome = outcome;
	}		

	public String getAttempted()
	{
		return attempted;
	}
	
	public ReasonType getReason()
	{
		return reason;
	}
	
	public MobsEvent getMobsOutcome()
	{
		return outcome;
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