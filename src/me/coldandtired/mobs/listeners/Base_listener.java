package me.coldandtired.mobs.listeners;

import java.util.ArrayList;
import java.util.List;

import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.managers.Event_manager;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class Base_listener implements Listener 
{
	private List<Outcome> outcomes;
	
	public Base_listener(List<Outcome> outcomes)
	{
		if (outcomes.size() == 0) outcomes = null;
		this.outcomes = outcomes;
	}
	
	List<Outcome> getRelevant_outcomes(LivingEntity le)
	{
		List<Outcome> temp = new ArrayList<Outcome>();
		for (Outcome o : outcomes) if (o.isAffected(le)) temp.add(o);
		return temp;
	}
	
	void performActions(MEvent event, LivingEntity le, Event orig_event)
	{
		if (outcomes == null) return;
		Event_manager.get().start_actions(getRelevant_outcomes(le), event, le, orig_event, true, null);
	}
}