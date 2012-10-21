package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

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
		
		performActions(MEvent.SHEARED, le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_SHEARED)) event.setCancelled(true);
	}
}