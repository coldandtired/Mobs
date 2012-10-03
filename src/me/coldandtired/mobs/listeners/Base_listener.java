package me.coldandtired.mobs.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class Base_listener implements Listener 
{
	private List<Outcome> outcomes;
	
	public Base_listener(List<Outcome> outcomes)
	{
		this.outcomes = outcomes;
	}
	
	List<Outcome> getRelevant_outcomes(LivingEntity le)
	{
		List<Outcome> temp = new ArrayList<Outcome>();
		for (Outcome o : outcomes) if (o.isAffected(le)) temp.add(o);
		return temp;
	}
	
	void performActions(Mobs_event event, LivingEntity le, Event orig_event)
	{
		Mobs.getInstance().getEvent_manager().start_actions(getRelevant_outcomes(le), event, le, orig_event, true);
	}

	@SuppressWarnings("unchecked")
	Map<String, Object> getData(LivingEntity le)
	{
		if (le.hasMetadata("mobs_data")) return (Map<String, Object>) le.getMetadata("mobs_data").get(0).value();
		return null;
	}
}