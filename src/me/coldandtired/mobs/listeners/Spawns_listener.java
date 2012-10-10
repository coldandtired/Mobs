package me.coldandtired.mobs.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void spawns(CreatureSpawnEvent event)
	{
		LivingEntity le = event.getEntity();
		if (spawn_reason == null) spawn_reason = event.getSpawnReason().toString();
		if (le.hasMetadata("mobs_data")) Mobs.log("has");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Mobs_const.SPAWN_REASON.toString(), spawn_reason);
		if (mob_name != null) data.put(Mobs_const.NAME.toString(), mob_name);
		le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		if (active) performActions(Mobs_event.SPAWNS, le, event);
	}
	
	@EventHandler
	public void player_respawns(PlayerRespawnEvent event)
	{
		LivingEntity le = event.getPlayer();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Mobs_const.SPAWN_REASON.toString(), "NATURAL");
		if (mob_name != null) data.put(Mobs_const.NAME.toString(), mob_name);
		le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		if (active) performActions(Mobs_event.SPAWNS, le, event);	
	}
}