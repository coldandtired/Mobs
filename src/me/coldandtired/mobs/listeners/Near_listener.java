package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.events.Mob_near_event;

public class Near_listener extends Base_listener
{
	public Near_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void near(Mob_near_event event)
	{
		performActions(MEvent.NEAR, event.getEntity(), event);
	}
}