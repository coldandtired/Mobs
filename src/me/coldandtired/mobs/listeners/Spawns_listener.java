package me.coldandtired.mobs.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_param;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Spawns_listener extends Base_listener
{	
	private boolean active;
	private String mob_name = null;
	private String spawn_reason = null;
	
	public Spawns_listener(List<Outcome> outcomes, boolean active)
	{
		super(outcomes);
		this.active = active;
	}

	public void setMob_name(String[] mob)
	{
		spawn_reason = mob[0];
		mob_name = mob.length == 2 ? mob[1] : null;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void spawns(CreatureSpawnEvent event)
	{
		LivingEntity le = event.getEntity();
		if (spawn_reason == null) spawn_reason = event.getSpawnReason().toString();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Mobs_param.SPAWN_REASON.toString(), spawn_reason);
		if (mob_name != null) data.put(Mobs_param.NAME.toString(), mob_name);
		event.getEntity().setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		if (active) performActions(Mobs_event.SPAWNS, le, event);		
	}
}