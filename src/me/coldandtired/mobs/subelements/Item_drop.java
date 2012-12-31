package me.coldandtired.mobs.subelements;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Alternatives;
import me.coldandtired.mobs.elements.Config_element;

import org.bukkit.inventory.ItemStack;
import org.w3c.dom.Element;

public class Item_drop extends Config_element
{
	private Object ids;
	private Object datas;
	private boolean match_id = false;
	private boolean match_data = false;
	//private boolean match_amount = false;
//	private boolean match_enchantments = false;
	
	public Item_drop(Element element, Config_element parent) throws XPathExpressionException 
	{
		super(element, parent);
		
		ids = fillInts((Element)Mobs.getXPath().evaluate("ids", element, XPathConstants.NODE));
		datas = fillInts((Element)Mobs.getXPath().evaluate("datas", element, XPathConstants.NODE));
		if (ids != null) match_id = true;
		if (datas != null) match_data = true;
		//if (Mobs.getXPath().evaluate("match_enchantments", element, XPathConstants.NODE) != null) match_enchantments = true;
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getIds(int orig_id)
	{
		if (ids == null)
		{
			List<Integer> temp = new ArrayList<Integer>();
			temp.add(orig_id);
			return temp;
		}
		
		if (ids instanceof Alternatives)
		{
			List<Integer> temp = new ArrayList<Integer>();
			temp.add((Integer)((Alternatives)ids).getAlternative());
			return temp;
		}
		else return (List<Integer>)ids;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getDatas(int orig_data)
	{
		if (datas == null)
		{
			List<Integer> temp = new ArrayList<Integer>();
			temp.add(orig_data);
			return temp;
		}
		
		if (datas instanceof Alternatives)
		{
			List<Integer> temp = new ArrayList<Integer>();
			temp.add((Integer)((Alternatives)datas).getAlternative());
			return temp;
		}
		else return (List<Integer>)datas;
	}
		
	public List<ItemStack> getItemstacks(int orig_id, int orig_data)
	{
		List<ItemStack> temp = new ArrayList<ItemStack>();
		Integer amount = getAmount();
		if (amount == null) amount = 1;
		for (int i : getIds(orig_id))
		{
			for (int d : getDatas(orig_data))
			{
				temp.add(new ItemStack(i, amount, (short)d));
			}
		}
		return temp;
	}
	
	public boolean matches(ItemStack orig, ItemStack stack)
	{
		if (orig == null) return false;
		if (match_id && orig.getType() != stack.getType()) return false;
		if (match_data && orig.getData().getData() != stack.getData().getData()) return false;
	//	if (match_amount && orig.getAmount() != stack.getAmount()) return false;
		return true;
	}
}