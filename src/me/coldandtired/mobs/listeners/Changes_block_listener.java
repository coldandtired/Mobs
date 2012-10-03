package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_param;

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
		
		performActions(Mobs_event.CHANGES_BLOCK, le, event);
		Map<String, Object> data = getData(le);
		if (data != null && (data.containsKey(Mobs_param.NO_MOVE_BLOCKS) || data.containsKey(Mobs_param.NO_GRAZE))) event.setCancelled(true);
	}
}
