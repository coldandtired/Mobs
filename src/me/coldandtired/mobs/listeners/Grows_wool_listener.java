package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.SheepRegrowWoolEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Grows_wool_listener extends Base_listener
{
	public Grows_wool_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void grows_wool(SheepRegrowWoolEvent event)
	{
		performActions(MEvent.GROWS_WOOL, event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_GROW_WOOL)) event.setCancelled(true);
	}
}