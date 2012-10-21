package me.coldandtired.mobs.listeners;

import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.PigZapEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Evolves_listener extends Base_listener
{	
	public Evolves_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void creeper_evolves(CreeperPowerEvent event)
	{
		performActions(MEvent.EVOLVES, event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void pig_evolves(PigZapEvent event)
	{
		performActions(MEvent.EVOLVES, event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_EVOLVE)) event.setCancelled(true);
	}
}