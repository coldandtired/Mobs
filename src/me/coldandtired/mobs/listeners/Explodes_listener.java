package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;

public class Explodes_listener extends Base_listener
{
	public Explodes_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void explodes(EntityExplodeEvent event)
	{
		Entity entity = event.getEntity();		
		if (entity == null) return;		
		if (entity instanceof Fireball) entity = ((Fireball)entity).getShooter();
		if (!(entity instanceof LivingEntity)) return;

		LivingEntity le = (LivingEntity)entity;
		
		performActions(Mobs_event.EXPLODES, le, event);
		
		Map<String, Object> data = getData(le);
		if (data != null && data.containsKey(Mobs_const.FRIENDLY)) event.setCancelled(true);
		
		if (event.isCancelled()) return;
		else
		{			
			if (data.containsKey(Mobs_const.NO_DESTROY_BLOCKS)) event.blockList().clear();
			else
			{
				if (data.containsKey(Mobs_const.EXPLOSION_SIZE))
				{
					int size = (Integer)data.get(Mobs_const.EXPLOSION_SIZE);
					event.setCancelled(true);
					Location loc = event.getLocation();
					if (data.containsKey(Mobs_const.FIERY_EXPLOSION)) loc.getWorld().createExplosion(loc, size, true);
					else loc.getWorld().createExplosion(loc, size);
				}
			}
		}
	}
}