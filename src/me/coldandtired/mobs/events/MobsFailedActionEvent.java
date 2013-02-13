package me.coldandtired.mobs.events;

import me.coldandtired.mobs.Enums.FailedReason;
import me.coldandtired.mobs.MobsOutcome;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobsFailedActionEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private final MobsOutcome outcome;
	private final String attempted;
	private final FailedReason reason;

	public MobsFailedActionEvent(MobsOutcome outcome, String attempted, FailedReason reason)
	{
		this.outcome = outcome;
		this.attempted = attempted;
		this.reason = reason;
	}
	
	public MobsOutcome getMobsOutcome()
	{
		return outcome;
	}		

	public String getAttempted()
	{
		return attempted;
	}
	
	public FailedReason getReason()
	{
		return reason;
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