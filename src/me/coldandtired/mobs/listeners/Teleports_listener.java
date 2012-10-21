package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTeleportEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Teleports_listener extends Base_listener
{
	public Teleports_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void mob_teleports(EntityTeleportEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		performActions(MEvent.TELEPORTS, le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_TELEPORT)) event.setCancelled(true);
	}
}