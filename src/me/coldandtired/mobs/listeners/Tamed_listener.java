package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTameEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Tamed_listener extends Base_listener
{
	public Tamed_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void mob_tamed(EntityTameEvent event)
	{
		performActions(MEvent.TAMED, event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_TAMED)) event.setCancelled(true);
	}
}