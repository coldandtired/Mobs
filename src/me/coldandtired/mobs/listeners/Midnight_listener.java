package me.coldandtired.mobs.listeners;

import java.util.List;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.events.Midnight_event;

import org.bukkit.event.EventHandler;

public class Midnight_listener extends Base_listener
{
	public Midnight_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	@EventHandler
	public void midnight(Midnight_event event)
	{
		performActions(MEvent.MIDNIGHT, null, event);
	}
}