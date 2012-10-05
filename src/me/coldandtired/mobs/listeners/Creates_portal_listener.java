package me.coldandtired.mobs.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCreatePortalEvent;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_const;

public class Creates_portal_listener extends Base_listener
{
	public Creates_portal_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void mob_creates_portal(EntityCreatePortalEvent event)
	{
		performActions(Mobs_event.CREATES_PORTAL, event.getEntity(), event);
		Map<String, Object> data = getData(event.getEntity());
		if (data != null && data.containsKey(Mobs_const.NO_CREATE_PORTALS)) event.setCancelled(true);
	}
}