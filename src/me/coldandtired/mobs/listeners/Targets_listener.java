package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;

public class Targets_listener extends Base_listener
{
	public Targets_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void targets(EntityCombustEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		performActions(Mobs_event.TARGETS, le, event);
		//targeted event!
		Map<String, Object> data = getData(le);
		if (data != null && data.containsKey(Mobs_const.FRIENDLY)) event.setCancelled(true);
	}
}