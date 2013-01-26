package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;

public class Numbers
{
	private int total;	
	private int low = 1;
	private int high = 1;
	private String list_type;
	
	public Numbers(int total, int low, int high, String list_type)
	{
		this.total = total;
		this.low = low;
		this.high = high;
		this.list_type = list_type;
	}
	
	public String getNumbers()
	{
		return "total = " + total + ", low = " + low + ", high = " + high;
	}
	
	public List<Mobs_element> getMobs_values(SortedMap<Integer, Mobs_element> map)
	{
		List<Mobs_element> temp = new ArrayList<Mobs_element>();
		
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
			int i = low > -1 ? new Random().nextInt((high - low) + 1) + low : high;	
			if (i >= temp.size()) return temp;
			
			return temp.subList(0, i);
		}		
		
		return null;
	}
}