package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.events.Dawn_event;

public class Dawn_listener extends Base_listener
{
	public Dawn_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	@EventHandler
	public void dawn(Dawn_event event)
	{
		performActions(MEvent.DAWN, null, event);
	}
}