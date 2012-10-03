package me.coldandtired.mobs.elements;

import java.util.Map;
import java.util.Random;

public class Alternatives 
{
	private int total;
	private Map<Integer, Object> choices;
	
	public Alternatives(int total, Map<Integer, Object> choices)
	{
		this.total = total;
		this.choices = choices;
	}
	
	public Object get_alternative()
	{
		int t = new Random().nextInt(total) + 1;
		for (Integer i : choices.keySet()) if (i >= t) return choices.get(i);
		return null;
	}
	
	public Map<Integer, Object> getChoices()
	{
		return choices;
	}
}