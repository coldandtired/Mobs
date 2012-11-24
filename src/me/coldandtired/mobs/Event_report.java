package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.List;

import me.coldandtired.mobs.enums.MEvent;

public class Event_report 
{
	private MEvent event_name;
	private List<Outcome_report> outcomes = new ArrayList<Outcome_report>();

	public Event_report(MEvent event)
	{
		event_name = event;
	}
	
	public void addOutcome_report(Outcome_report or)
	{
		outcomes.add(or);
	}

	public MEvent getName()
	{
		return event_name;
	}
	
	public List<Outcome_report> getOutcomes()
	{
		return outcomes;
	}
}