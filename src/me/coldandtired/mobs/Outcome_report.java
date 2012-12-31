package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.List;

public class Outcome_report 
{
	private boolean passed = false;
	private List<Condition_report> failed_conds = new ArrayList<Condition_report>();
	private List<Condition_report> passed_conds = new ArrayList<Condition_report>();
	private List<Action_report> failed_actions = new ArrayList<Action_report>();
	private List<Action_report> performed_actions = new ArrayList<Action_report>();
	
	public void setPassed()
	{
		passed = true;
	}
	
	public void addAction(Action_report ar)
	{
		if (ar.isSuccess()) performed_actions.add(ar); else failed_actions.add(ar);
	}
	
	public List<Action_report> getPerformed_actions()
	{
		return performed_actions;
	}
	
	public List<Action_report> getFailed_actions()
	{
		return failed_actions;
	}
	
	public void addPassed_condition(Condition_report cond)
	{
		passed_conds.add(cond);
	}
	
	public void addFailed_condition(Condition_report cond)
	{
		failed_conds.add(cond);
	}

	public boolean getPassed()
	{
		return passed;
	}
	
	public List<Condition_report> getPassed_conds()
	{
		return passed_conds;
	}
	
	public List<Condition_report> getFailed_conds()
	{
		return failed_conds;
	}
}