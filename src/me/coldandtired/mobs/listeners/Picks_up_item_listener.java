package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.event.player.PlayerPickupItemEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.enums.MEvent;

public class Picks_up_item_listener extends Base_listener
{
	public Picks_up_item_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	public void picks_up_item(PlayerPickupItemEvent event)
	{
		performActions(MEvent.PICKS_UP_ITEM, event.getPlayer(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getPlayer(), MParam.NO_PICK_UP_ITEMS)) event.setCancelled(true);
	}
}