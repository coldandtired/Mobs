package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.SheepDyeWoolEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Dyed_listener extends Base_listener
{	
	public Dyed_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void dyed(SheepDyeWoolEvent event)
	{
		performActions(MEvent.DYED, event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_DYED)) event.setCancelled(true);
	}
}