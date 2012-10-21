package me.coldandtired.mobs.elements;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MAction;
import me.coldandtired.mobs.enums.MTarget;
import me.coldandtired.mobs.subelements.Item_drop;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Action
{
	private MAction action_type;
	private Alternatives targets;
	private Alternatives items;
	private Text_value mob;
	private Text_value amount;
	private boolean locked = false;
	private Text_value value;
	private Text_value message;
	
	public Action(Element element) throws XPathExpressionException 
	{
		action_type = MAction.valueOf(element.getLocalName().toUpperCase());
		
		NodeList list = (NodeList)Mobs.getXPath().evaluate("value", element, XPathConstants.NODESET);
	
		if (element.getChildNodes().getLength() == 1 || list.getLength() > 0)
		{
			value = new Text_value(element);
			//return;
		}
		
		if (element.hasAttribute("lock")) locked = Boolean.parseBoolean(element.getAttribute("lock"));	
		
		Element el = (Element)Mobs.getXPath().evaluate("amount", element, XPathConstants.NODE);
		if (el != null) amount = new Text_value(el);
			
		el = (Element)Mobs.getXPath().evaluate("message", element, XPathConstants.NODE);
		if (el != null) message = new Text_value(el);
			
		el = (Element)Mobs.getXPath().evaluate("mob", element, XPathConstants.NODE);
		if (el != null) mob = new Text_value(el);
			
		el = (Element)Mobs.getXPath().evaluate("target", element, XPathConstants.NODE);
		if (el != null) fillTargets(el);	
		
		fillItems(element);	
	}
	
	private void fillTargets(Element element) throws XPathExpressionException
	{
		NodeList list = (NodeList)Mobs.getXPath().evaluate(MTarget.getXpath(), element, XPathConstants.NODESET);		
		if (list.getLength() > 0)
		{
			Map<Integer, Object> temp = new HashMap<Integer, Object>();
			int count = 0;
			for (int i = 0; i < list.getLength(); i ++)
			{
				Element el = (Element)list.item(i);
				int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
				count += ratio;
				if (list.getLength() == 1) count = 1;						
				temp.put(count, new Target(el));	
			}
			targets = new Alternatives(count, temp);
		}
	}
	
	private void fillItems(Element element) throws XPathExpressionException
	{
		NodeList list = (NodeList)Mobs.getXPath().evaluate("item", element, XPathConstants.NODESET);		
		if (list.getLength() > 0)
		{
			Map<Integer, Object> temp = new HashMap<Integer, Object>();
			int count = 0;
			for (int i = 0; i < list.getLength(); i ++)
			{
				Element el = (Element)list.item(i);
				int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
				count += ratio;
				if (list.getLength() == 1) count = 1;						
				temp.put(count, new Item_drop(el));	
			}
			items = new Alternatives(count, temp);
		}
	}
	
	/*private void fillMisc(Param p, Element element)
	{
		NodeList list;
		Map<Integer, Object> temp;
		int count;
		try
		{			
			list = (NodeList)Mobs.getXPath().evaluate("power | duration | time", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, el.getTextContent());	
				}
				p.addParam(MParam.NUMBER, new Alternatives(count, temp));
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}*/
	
	public MAction getAction_type() 
	{
		return action_type;
	}

	public Target getTarget()
	{
		if (targets == null) return null;
		return (Target)targets.get_alternative();
	}
	
	public World getWorld(LivingEntity le)
	{
		Target t = getTarget();
		return t == null ? le.getWorld() : t.getWorld(le);
	}
	
	public Item_drop getItem()
	{
		if (items == null) return null;
		return (Item_drop)items.get_alternative();
	}
	
	public String[] getMob()
	{
		if (mob == null) return null;
		return mob.getValue().toUpperCase().split(":");
	}
	
	public Text_value getPure_amount()
	{
		return amount;
	}
	
	public int getAmount(int orig)
	{
		if (amount == null) return orig;
		return amount.getInt_value(orig);			
	}
	
	public String getValue()
	{
		if (value == null) return null;
		return value.getValue();
	}
	
	public Integer getInt_value(int orig)
	{
		Text_value tv = value == null ? amount : value;
		if (tv == null) return orig;
		return tv.getInt_value(orig);
	}	
	
	public boolean isLocked()
	{
		return locked;
	}
	
	public String getMessage()
	{
		if (message == null) return null;
		return message.getValue();
	}
}