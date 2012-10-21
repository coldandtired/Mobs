package me.coldandtired.mobs.listeners;

import java.util.List;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Dies_listener extends Base_listener
{		
	public Dies_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	@EventHandler
	public void dies(EntityDeathEvent event)
	{
		performActions(MEvent.DIES, event.getEntity(), event);
		if (Data.hasData(event.getEntity(), MParam.CLEAR_DROPS)) event.getDrops().clear();
		if (Data.hasData(event.getEntity(), MParam.CLEAR_EXP)) event.setDroppedExp(0);
	}
	
	@EventHandler
	public void player_dies(PlayerDeathEvent event)
	{
		performActions(MEvent.DIES, event.getEntity(), event);
		if (Data.hasData(event.getEntity(), MParam.CLEAR_DROPS)) event.getDrops().clear();
		if (Data.hasData(event.getEntity(), MParam.CLEAR_EXP)) event.setDroppedExp(0);
	}
}