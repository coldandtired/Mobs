package me.coldandtired.mobs.elements;

import java.util.Map;
import java.util.Random;
import java.util.SortedMap;

public class Alternatives 
{
	private int total;
	private SortedMap<Integer, Object> choices;
	
	public Alternatives(int total, SortedMap<Integer, Object> choices)
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