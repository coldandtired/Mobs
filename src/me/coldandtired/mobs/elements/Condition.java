package me.coldandtired.mobs.elements;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MCcondition;
import me.coldandtired.mobs.enums.MTarget;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Condition extends Param
{
	private MCcondition condition_type;
	private String value;
	private Alternatives targets;
	private String mob;
	private String amount;
	private Text_value world;
	
	public Condition(Element element) throws XPathExpressionException 
	{
		condition_type = MCcondition.valueOf(element.getLocalName().toUpperCase());
		NodeList list = (NodeList)Mobs.getXPath().evaluate("value", element, XPathConstants.NODESET);
		
		if (element.getChildNodes().getLength() == 1 || list.getLength() > 0)
		{
			value = element.getTextContent();
			//return;
		}
		
		Element el = (Element)Mobs.getXPath().evaluate("amount", element, XPathConstants.NODE);
		if (el != null) amount = el.getTextContent();
			
		//el = (Element)Mobs.getXPath().evaluate("message", element, XPathConstants.NODE);
		//if (el != null) message = new Text_value(el);
			
		el = (Element)Mobs.getXPath().evaluate("mob", element, XPathConstants.NODE);
		if (el != null) mob = el.getTextContent();
		
		el = (Element)Mobs.getXPath().evaluate("target/world | world", element, XPathConstants.NODE);
		if (el != null) world = new Text_value(el);
		
		el = (Element)Mobs.getXPath().evaluate("target", element, XPathConstants.NODE);
		if (el != null) fillTargets(el, world);
	}
	
	private void fillTargets(Element element, Text_value world) throws XPathExpressionException
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
				temp.put(count, new Target(el, world));	
			}
			targets = new Alternatives(count, temp);
		}
	}
	
	public World getWorld(LivingEntity le)
	{
		if (world == null) return le.getWorld();
		return Bukkit.getWorld(world.getValue());
	}

	public Target getTarget()
	{
		if (targets == null) return null;
		return (Target)targets.get_alternative();
	}
	
	public String[] getMob()
	{
		if (mob == null) return null;
		return mob.toUpperCase().split(":");
	}
	
	public boolean matches(int orig)
	{
		String temp = amount == null ? value : amount;
		if (temp == null) return false;
		
		temp = temp.replace(" ", "").toUpperCase();
		
		for (String s :temp.split(","))
		{
			if (s.startsWith("ABOVE"))
			{
				if (Integer.parseInt(s.replace("ABOVE", "")) < orig) return true;
			}
			else if (s.startsWith("BELOW"))
			{
				if (Integer.parseInt(s.replace("BELOW", "")) > orig) return true;
			}
			else if (s.contains("TO"))
			{
				String[]temp2 = s.split("TO");
				if (orig >= Math.min(Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1])) &&
					orig <= Math.max(Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]))) return true;
			}
			else if (Integer.parseInt(s) == orig) return true;
		}
		return false;
	}

	public boolean matches(String orig)
	{
		if (value == null) return false;
		for (String s : value.split(","))
		{
			if (s.trim().equalsIgnoreCase(orig)) return true;
		}
		return false;
	}
	
	public MCcondition getCondition_type() 
	{
		return condition_type;
	}

	public String getValue()
	{
		return value;
	}
}