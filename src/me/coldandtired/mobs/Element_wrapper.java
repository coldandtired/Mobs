package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Element_wrapper
{
	Object value;
	private int total;	
	private int low = 1;
	private int high = 1;
	private String list_type;
	
	public Element_wrapper(String name)
	{
		value = name;
	}
	
	public Object getValue()
	{
		return value;
	}
	
	public Element_wrapper(Map<Integer, Mobs_element> map, int total, int low, int high, String list_type)
	{
		value = map;
		this.total = total;
		this.low = low;
		this.high = high;
		this.list_type = list_type;
	}
	
	@SuppressWarnings("unchecked")
	public Mobs_element getContainer()
	{
		if (!(value instanceof Map<?, ?>)) return null;
		
		Map<Integer, Mobs_element> map = (Map<Integer, Mobs_element>)value;
		
		//for (Mobs_element me : map.values()) if ()
		
		int t = new Random().nextInt(total) + 1;
		for (Integer i : (map).keySet()) if (i >= t)
		{
			return (map.get(i));
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Mobs_element> getMobs_values(Mobs_element orig)
	{
		List<Mobs_element> temp = new ArrayList<Mobs_element>();
		
		if (value instanceof String)
		{
			temp.add(orig);
			return temp;
		}
		
		Map<Integer, Mobs_element> map = (Map<Integer, Mobs_element>)value;
		if (map.size() > 1 && list_type.equalsIgnoreCase("ratio"))
		{
			int t = new Random().nextInt(total) + 1;
			for (Integer i : (map).keySet()) if (i >= t)
			{
				temp.add(map.get(i));
				return temp;
			}
		}
		else
		{
			for (Mobs_element mv : map.values()) temp.add(mv);
			if (temp.size() == 1 || list_type.equalsIgnoreCase("all")) return temp;
			
			Collections.shuffle(temp);
			Mobs.error("low = " + low + ", high = " + high);
			int i = low > -1 ? new Random().nextInt((high - low) + 1) + low : high;	
			if (i >= temp.size()) return temp;
			
			return temp.subList(0, i);
		}		
		
		return null;
	}
}