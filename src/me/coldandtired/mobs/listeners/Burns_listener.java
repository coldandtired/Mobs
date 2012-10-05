package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;

public class Burns_listener extends Base_listener
{
	public Burns_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void burns(EntityCombustEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		performActions(Mobs_event.BURNS, le, event);
		Map<String, Object> data = getData(le);
		if (data != null && data.containsKey(Mobs_const.NO_BURN)) event.setCancelled(true);
	}
}