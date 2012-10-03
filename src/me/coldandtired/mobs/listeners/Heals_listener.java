package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_param;

public class Heals_listener extends Base_listener
{
	public Heals_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void heals(EntityRegainHealthEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();

		performActions(Mobs_event.HEALS, le, event);
		Map<String, Object> data = getData(le);
		if (data != null && data.containsKey(Mobs_param.NO_HEAL))
		{
			event.setCancelled(true);
			return;
		}

		//if (mob.hasParam("hp")) return;
		
		//int i = mob.getInt_param("hp") + event.getAmount();
		//if (mob.hasParam("can_overheal") && mob.getBoolean_param("can_overheal")) mob.setHp(i);
		//else mob.setHp(i > mob.getMax_hp() ? mob.getMax_hp() : i);
	}
}