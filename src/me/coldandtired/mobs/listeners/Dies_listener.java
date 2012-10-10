package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Dies_listener extends Base_listener
{	
	private boolean active;
	
	public Dies_listener(List<Outcome> outcomes, boolean active)
	{
		super(outcomes);
		this.active = active;
	}

	@EventHandler
	public void dies(EntityDeathEvent event)
	{
		if (active) performActions(Mobs_event.DIES, event.getEntity(), event);
		Map<String, Object> data = getData(event.getEntity());
		if (data != null)
		{
			if (data.containsKey(Mobs_const.CLEAR_DROPS.toString())) event.getDrops().clear();
			if (data.containsKey(Mobs_const.CLEAR_EXP.toString())) event.setDroppedExp(0);
		}
	}
	
	@EventHandler
	public void player_dies(PlayerDeathEvent event)
	{
		if (active) performActions(Mobs_event.DIES, event.getEntity(), event);
		Map<String, Object> data = getData(event.getEntity());
		if (data != null)
		{
			if (data.containsKey(Mobs_const.CLEAR_DROPS.toString())) event.getDrops().clear();
			if (data.containsKey(Mobs_const.CLEAR_EXP.toString())) event.setDroppedExp(0);
		}
	}
}