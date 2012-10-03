package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.events.Mob_blocks_event;
import me.coldandtired.mobs.events.Mob_damaged_event;

public class Hit_listener extends Base_listener
{	
	boolean use_hit;
	boolean use_blocks;
	boolean use_damaged;
	
	public Hit_listener(List<Outcome> outcomes, boolean use_hit, boolean use_damaged, boolean use_blocks) 
	{
		super(outcomes);
		this.use_blocks = use_blocks;
		this.use_hit = use_hit;
		this.use_damaged = use_damaged;
	}

	@EventHandler
	public void hit(EntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (use_hit) performActions(Mobs_event.HIT, le, event);
		
		if (le.getNoDamageTicks() > 10)
		{
			// no damage
			event.setCancelled(true);
			if (use_blocks) Mobs.getInstance().getServer().getPluginManager().callEvent(new Mob_blocks_event(le));
		}	
		else
		{
			// damaged
			if (use_damaged) Mobs.getInstance().getServer().getPluginManager().callEvent(new Mob_damaged_event(le));
		}
	}
}