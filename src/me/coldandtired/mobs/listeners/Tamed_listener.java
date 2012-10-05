package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTameEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;

public class Tamed_listener extends Base_listener
{
	public Tamed_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void mob_tamed(EntityTameEvent event)
	{
		performActions(Mobs_event.TAMED, event.getEntity(), event);
		Map<String, Object> data = getData(event.getEntity());
		if (data != null && data.containsKey(Mobs_const.NO_TAMED)) event.setCancelled(true);
	}
}