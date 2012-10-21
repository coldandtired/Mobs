package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.SlimeSplitEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Splits_listener extends Base_listener
{
	public Splits_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	@EventHandler
	public void mob_splits(SlimeSplitEvent event)
	{
		performActions(MEvent.SPLITS, event.getEntity(), event);
		Integer i = (Integer)Data.getData(event.getEntity(), MParam.SPLIT_INTO);
		if (i == null) return;
		if (i == 0) event.setCancelled(true); else event.setCount(i);
	}
}