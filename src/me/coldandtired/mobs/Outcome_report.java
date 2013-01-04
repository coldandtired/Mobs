package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.List;

public class Outcome_report 
{
	private boolean passed = false;
	private List<Condition_report> failed_conds = new ArrayList<Condition_report>();
	private List<Condition_report> passed_conds = new ArrayList<Condition_report>();
	
	public void setPassed()
	{
		passed = true;
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