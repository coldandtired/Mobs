package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.events.Mob_approached_event;

public class Approached_listener extends Base_listener 
{
	public Approached_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void approached(Mob_approached_event event)
	{
		performActions(Mobs_event.APPROACHED, event.getEntity(), event);
	}
}