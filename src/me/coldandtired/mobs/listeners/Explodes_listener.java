package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

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
		
		performActions(MEvent.EXPLODES, le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.FRIENDLY)) event.setCancelled(true);
		
		if (event.isCancelled()) return;
		else
		{			
			if (Data.hasData(le, MParam.NO_DESTROY_BLOCKS)) event.blockList().clear();
			else
			{
				if (Data.hasData(le, MParam.EXPLOSION_SIZE))
				{
					Integer size = (Integer)Data.getData(le, MParam.EXPLOSION_SIZE);
					if (size == null) return;
					
					event.setCancelled(true);
					Location loc = event.getLocation();
					if (Data.hasData(le, MParam.FIERY_EXPLOSION)) loc.getWorld().createExplosion(loc, size, true);
					else loc.getWorld().createExplosion(loc, size);
				}
			}
		}
	}
}