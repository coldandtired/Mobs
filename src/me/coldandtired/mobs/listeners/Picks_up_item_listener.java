package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.event.player.PlayerPickupItemEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_const;
import me.coldandtired.mobs.enums.Mobs_event;

public class Picks_up_item_listener extends Base_listener
{
	public Picks_up_item_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	public void picks_up_item(PlayerPickupItemEvent event)
	{
		performActions(Mobs_event.PICKS_UP_ITEM, event.getPlayer(), event);
		Map<String, Object> data = getData(event.getPlayer());
		if (data != null && data.containsKey(Mobs_const.NO_PICK_UP_ITEMS)) event.setCancelled(true);
	}
}