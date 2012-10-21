package me.coldandtired.mobs.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Spawns_listener extends Base_listener
{	
	private String mob_name = null;
	private String spawn_reason = null;
	
	public Spawns_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}

	public void setMob_name(String name)
	{
		spawn_reason = "SPAWNED";
		mob_name = name;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void spawns(CreatureSpawnEvent event)
	{
		LivingEntity le = event.getEntity();
		if (spawn_reason == null) spawn_reason = event.getSpawnReason().toString();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(MParam.SPAWN_REASON.toString(), spawn_reason);
		if (mob_name != null) data.put(MParam.NAME.toString(), mob_name);
		le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		performActions(MEvent.SPAWNS, le, event);
		mob_name = null;
		spawn_reason = null;
	}
	
	@EventHandler
	public void player_respawns(PlayerRespawnEvent event)
	{
		Player p = event.getPlayer();
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, MParam.NAME, p.getName());
		performActions(MEvent.SPAWNS, p, event);	
	}
}