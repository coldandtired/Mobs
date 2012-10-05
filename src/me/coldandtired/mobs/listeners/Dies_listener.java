package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class Dies_listener extends Base_listener
{	
	public Dies_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	@EventHandler
	public void dies(EntityDeathEvent event)
	{
		performActions(Mobs_event.DIES, event.getEntity(), event);
		Map<String, Object> data = getData(event.getEntity());
		if (data != null)
		{
			if (data.containsKey(Mobs_const.CLEAR_DROPS.toString())) event.getDrops().clear();
			if (data.containsKey(Mobs_const.CLEAR_EXP.toString())) event.setDroppedExp(0);
		}
	}
}