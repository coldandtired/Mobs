package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.SlimeSplitEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;

public class Splits_listener extends Base_listener
{
	public Splits_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	@EventHandler
	public void mob_splits(SlimeSplitEvent event)
	{
		performActions(Mobs_event.SPLITS, event.getEntity(), event);
		Map<String, Object> data = getData(event.getEntity());
		if (data != null && data.containsKey(Mobs_const.SPLIT_INTO))
		{
			int i = (Integer)data.get(Mobs_const.SPLIT_INTO);
			if (i == 0) event.setCancelled(true);
			else event.setCount(i);
		}
	}
}