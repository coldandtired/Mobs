package me.coldandtired.mobs.subelements;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.w3c.dom.Element;

/** inherits amount */
public class Item_drop
{
	private int item_id = 0;
	private String amount = "1";
	private short item_data = 0;
	
	public Item_drop(Element el) 
	{
		if (el.hasAttribute("id")) item_id = Integer.parseInt(el.getAttribute("id"));
		if (el.hasAttribute("amount")) amount = (el.getAttribute("amount"));
		if (el.hasAttribute("data")) item_data = Short.parseShort(el.getAttribute("data"));
	}
	
	public int getItem_id()
	{
		return item_id;
	}
	
	public void setItem_id(int item_id)
	{
		this.item_id = item_id;
	}
	
	public int getAmount() 
	{
		if (!amount.contains(",")) return Integer.parseInt(amount);

		List<Integer> temp = new ArrayList<Integer>();
		String[] temp1 = amount.split(",");		
		for (String s : temp1)
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

	public void setAmount(String amount) 
	{
		this.amount = amount;
	}

	public short getItem_data()
	{
		return item_data;
	}
	
	public void setItem_data(short item_data)
	{
		this.item_data = item_data;
	}
}