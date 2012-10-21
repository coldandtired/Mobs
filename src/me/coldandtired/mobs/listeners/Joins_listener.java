package me.coldandtired.mobs.listeners;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.enums.MEvent;

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
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, MParam.NAME, p.getName());
		performActions(MEvent.JOINS, p, event);
	}
}