package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_param;

public class Sheared_listener extends Base_listener
{
	public Sheared_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void mob_sheared(PlayerShearEntityEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		performActions(Mobs_event.SHEARED, le, event);
		Map<String, Object> data = getData(le);
		if (data != null && data.containsKey(Mobs_param.NO_SHEARED)) event.setCancelled(true);
	}
}