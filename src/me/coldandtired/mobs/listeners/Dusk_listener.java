package me.coldandtired.mobs.listeners;

import java.util.List;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.events.Dusk_event;

import org.bukkit.event.EventHandler;

public class Dusk_listener extends Base_listener
{
	public Dusk_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	@EventHandler
	public void dusk(Dusk_event event)
	{
		performActions(Mobs_event.DUSK, null, event);
	}
}