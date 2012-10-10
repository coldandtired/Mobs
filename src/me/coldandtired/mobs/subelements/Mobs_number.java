package me.coldandtired.mobs.subelements;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mobs_number 
{
	private String value;
	private String type = "abs";
	private String type2 = "abs";
	
	public Mobs_number(String value)
	{
		this.value = value;
		if (value.endsWith("%"))
		{
			type = "percent";
			value = value.replace("%", "");
		}
		
		if (value.startsWith("+"))
		{
			type2 = "add";
			value = value.substring(1);
		}
		else if (value.startsWith("-"))
		{
			type2 = "dec";
			value = value.substring(1);
		}
	}
	
	private int getValue()
	{
		if (!value.contains(",")) return Integer.parseInt(value);

		List<Integer> temp = new ArrayList<Integer>();
		for (String s : value.split(","))
		{
			if (s.contains("to"))
			{
				String[] temp2 = s.split("to");
				int low = Math.min(Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]));
				int high = Math.max(Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]));
				for (int i = low; i <= high; i++) temp.add(i);
			}
			else temp.add(Integer.parseInt(s));
		}		
		
		return temp.get(new Random().nextInt(temp.size()));
	}
	
	public int getAbsolute_value(int orig)
	{
		if (value == null || value.equalsIgnoreCase("")) return 0;
		int i = getValue();
		
		if (type.equalsIgnoreCase("abs"))
		{	
			if (type2.equalsIgnoreCase("add")) return orig + i;
			else if (type2.equalsIgnoreCase("dec")) return orig - i;
			else return i;
		}
		else
		{
			// percent
			if (type2.equalsIgnoreCase("add")) return ((orig * i) / 100) + orig;
			else if (type2.equalsIgnoreCase("dec")) return orig - ((orig * i) / 100);
			else return (orig * i) / 100;
		}
	}
}