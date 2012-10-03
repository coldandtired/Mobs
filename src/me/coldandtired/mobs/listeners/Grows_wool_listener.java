package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.SheepRegrowWoolEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_param;

public class Grows_wool_listener extends Base_listener
{
	public Grows_wool_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void grows_wool(SheepRegrowWoolEvent event)
	{
		performActions(Mobs_event.GROWS_WOOL, event.getEntity(), event);
		Map<String, Object> data = getData(event.getEntity());
		if (data != null && data.containsKey(Mobs_param.NO_GROW_WOOL)) event.setCancelled(true);
	}
}