package me.coldandtired.mobs.listeners;

import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Targets_listener extends Base_listener
{
	public Targets_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void targets(EntityTargetLivingEntityEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		performActions(MEvent.TARGETS, le, event);
		if (event.isCancelled()) return;
		//targetd event!
		if (Data.hasData(le, MParam.FRIENDLY)) event.setCancelled(true);
	}
}