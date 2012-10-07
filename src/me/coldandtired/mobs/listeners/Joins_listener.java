package me.coldandtired.mobs.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_const;
import me.coldandtired.mobs.enums.Mobs_event;

public class Joins_listener extends Base_listener 
{
	public Joins_listener(List<Outcome> outcomes)
	{
		super(outcomes);
	}
	
	@EventHandler
	public void joins(PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		if (!p.hasMetadata("mobs_data"))
		{
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(Mobs_const.SPAWN_REASON.toString(), "NATURAL");
			p.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		}
		performActions(Mobs_event.JOINS, p, event);
	}
}