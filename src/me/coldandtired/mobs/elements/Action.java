package me.coldandtired.mobs.elements;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import me.coldandtired.mobs.enums.Mobs_action;
import me.coldandtired.mobs.enums.Mobs_const;
import me.coldandtired.mobs.enums.Mobs_target;
import me.coldandtired.mobs.subelements.Item_drop;
import me.coldandtired.mobs.subelements.Mobs_number;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.entity.EntityType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Action extends P
{
	private Mobs_action action_type;
			
	public Action(XPath xpath, Element element) 
	{
		action_type = Mobs_action.valueOf(element.getLocalName().toUpperCase());
		if (element.hasChildNodes() && element.getChildNodes().getLength() == 1) params.put(Mobs_const.NAME, element.getChildNodes().item(0).getTextContent());
		try
		{
			NodeList list = (NodeList)xpath.evaluate(Mobs_target.getXpath(), element, XPathConstants.NODESET);		
			Map<Integer, Object> temp;
			int count;
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, new Target(xpath, el));	
				}
				params.put(Mobs_const.TARGET, new Alternatives(count, temp));
			}
			
			list = (NodeList)xpath.evaluate("power | amount | duration | time", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, new Mobs_number(el.getTextContent()));	
				}
				params.put(Mobs_const.NUMBER, new Alternatives(count, temp));
			}
			
			list = (NodeList)xpath.evaluate("message", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, el.getTextContent());	
				}
				params.put(Mobs_const.MESSAGE, new Alternatives(count, temp));
			}
			
			list = (NodeList)xpath.evaluate("world", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, el.getTextContent());	
				}
				params.put(Mobs_const.WORLD, new Alternatives(count, temp));
			}
			
			list = (NodeList)xpath.evaluate("item", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, new Item_drop(el));	
				}
				params.put(Mobs_const.ITEM, new Alternatives(count, temp));
			}
			
			String s = null;
			for (EntityType et : EntityType.values()) s = s + " | " + et.toString().toLowerCase();
			s = s.substring(3);
					
			list = (NodeList)xpath.evaluate(s, element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, el.getLocalName().toUpperCase() + ":" + el.getTextContent());	
				}
				params.put(Mobs_const.MOB, new Alternatives(count, temp));
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public Mobs_action getAction_type() 
	{
		return action_type;
	}
}