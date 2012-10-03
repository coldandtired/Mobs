package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;

public class Damaged_listener extends Base_listener
{
	public Damaged_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void damaged(EntityTargetLivingEntityEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		performActions(Mobs_event.TARGETS, le, event);
	}
}