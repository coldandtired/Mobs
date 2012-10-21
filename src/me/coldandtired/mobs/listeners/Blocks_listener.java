package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.events.Mob_blocks_event;

public class Blocks_listener extends Base_listener
{
	public Blocks_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	@EventHandler
	public void blocks(Mob_blocks_event event)
	{
		performActions(MEvent.BLOCKS, event.getEntity(), event);
	}
}