package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MCondition;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Conditions extends Config_element
{
	private List<Condition> conditions = new ArrayList<Condition>();
	
	private Conditions(Element element, Config_element parent) throws XPathExpressionException 
	{
		super(element, parent);
		NodeList list = (NodeList)Mobs.getXPath().evaluate(MCondition.getXpath(), element, XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++)
		{
			conditions.add(new Condition((Element)list.item(i), this));
		}
	}
	
	public static Conditions get(Element element, Config_element parent) throws XPathExpressionException
	{
		Conditions c = new Conditions(element, parent);
		if (c.conditions.size() == 0) return null;
		return c;
	}
	
	public boolean passedConditions_check(LivingEntity live, Event orig_event)
	{
		for (Condition c : conditions) if (c.passes(live, orig_event))
		{
			Mobs.debug("Passed");
			return true;
		} else Mobs.debug("Failed");
		
		return false;
	}
	
	public List<Condition> getConditions()
	{
		return conditions;
	}

}