package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.events.Mob_blocks_event;
import me.coldandtired.mobs.events.Mob_damaged_event;

public class Hit_listener extends Base_listener
{		
	public Hit_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void hit(EntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		performActions(MEvent.HIT, le, event);
		
		if (le.getNoDamageTicks() > 10)
		{
			// no damage
			event.setCancelled(true);
			Mobs.getInstance().getServer().getPluginManager().callEvent(new Mob_blocks_event(le));
		}	
		else
		{
			// damaged
			Mobs.getInstance().getServer().getPluginManager().callEvent(new Mob_damaged_event(le, event));
		}
	}
}