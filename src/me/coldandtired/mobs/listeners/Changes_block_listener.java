package me.coldandtired.mobs.listeners;

import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Changes_block_listener extends Base_listener
{
	public Changes_block_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void changes_block(EntityChangeBlockEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		performActions(MEvent.CHANGES_BLOCK, le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_MOVE_BLOCKS) || Data.hasData(le, MParam.NO_GRAZE)) event.setCancelled(true);
	}
}