package me.coldandtired.mobs.subelements;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Text_value;
import org.bukkit.inventory.ItemStack;
import org.w3c.dom.Element;

public class Item_drop
{
	private Text_value id;
	private Text_value data;
	private Text_value amount;
	private boolean match_id = false;
	private boolean match_data = false;
	private boolean match_amount = false;
//	private boolean match_enchantments = false;
	
	public Item_drop(Element element) throws XPathExpressionException 
	{
		Element el = (Element)Mobs.getXPath().evaluate("id", element, XPathConstants.NODE);
		if (el != null)
		{
			id = new Text_value(el);
			match_id = true;
		}
			
		el = (Element)Mobs.getXPath().evaluate("data", element, XPathConstants.NODE);
		if (el != null)
		{
			data = new Text_value(el);
			match_data = true;
		}
		
		el = (Element)Mobs.getXPath().evaluate("amount", element, XPathConstants.NODE);
		if (el != null)
		{
			amount = new Text_value(el);
			match_amount = true;
		}
		
		//if (Mobs.getXPath().evaluate("match_enchantments", element, XPathConstants.NODE) != null) match_enchantments = true;
	}
	
	public int getId()
	{
		if (id == null) return 0;
		return id.getInt_value(0);
	}
	
	public short getData()
	{
		if (data == null) return 0;
		return (short) data.getInt_value(0);
	}
	
	public int getAmount()
	{
		if (amount == null) return 1;
		return amount.getInt_value(1);
	}
	
	public boolean matches(ItemStack orig, ItemStack stack)
	{
		if (orig == null) return false;
		if (match_id && orig.getType() != stack.getType()) return false;
		if (match_data && orig.getData().getData() != stack.getData().getData()) return false;
		if (match_amount && orig.getAmount() != stack.getAmount()) return false;
		
		return true;
	}
}