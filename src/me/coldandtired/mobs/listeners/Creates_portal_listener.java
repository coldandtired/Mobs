package me.coldandtired.mobs.listeners;

import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCreatePortalEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Creates_portal_listener extends Base_listener
{	
	public Creates_portal_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void mob_creates_portal(EntityCreatePortalEvent event)
	{
		performActions(MEvent.CREATES_PORTAL, event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_CREATE_PORTALS)) event.setCancelled(true);
	}
}