package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.PigZapEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_param;

public class Evolves_listener extends Base_listener
{
	public Evolves_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void creeper_evolves(CreeperPowerEvent event)
	{
		performActions(Mobs_event.EVOLVES, event.getEntity(), event);
		Map<String, Object> data = getData(event.getEntity());
		if (data != null && data.containsKey(Mobs_param.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void pig_evolves(PigZapEvent event)
	{
		performActions(Mobs_event.EVOLVES, event.getEntity(), event);
		Map<String, Object> data = getData(event.getEntity());
		if (data != null && data.containsKey(Mobs_param.NO_EVOLVE)) event.setCancelled(true);
	}
}