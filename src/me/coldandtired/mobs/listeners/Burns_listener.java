package me.coldandtired.mobs.listeners;

import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Burns_listener extends Base_listener
{	
	public Burns_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void burns(EntityCombustEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		performActions(MEvent.BURNS, le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_BURN)) event.setCancelled(true);
	}
}