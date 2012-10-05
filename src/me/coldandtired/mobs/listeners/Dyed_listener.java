package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.SheepDyeWoolEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;

public class Dyed_listener extends Base_listener
{
	public Dyed_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void dyed(SheepDyeWoolEvent event)
	{
		performActions(Mobs_event.DYED, event.getEntity(), event);
		Map<String, Object> data = getData(event.getEntity());
		if (data != null && data.containsKey(Mobs_const.NO_DYED)) event.setCancelled(true);
	}
}