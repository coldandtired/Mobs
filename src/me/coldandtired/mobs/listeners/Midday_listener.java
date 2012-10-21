package me.coldandtired.mobs.listeners;

import java.util.List;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.events.Midday_event;

import org.bukkit.event.EventHandler;

public class Midday_listener extends Base_listener
{
	public Midday_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void midday(Midday_event event)
	{
		performActions(MEvent.MIDDAY, null, event);
	}
}