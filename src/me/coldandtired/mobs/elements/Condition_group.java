package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Condition_report;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.Outcome_report;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Condition_group extends Config_element
{
	private List<Condition> conditions = new ArrayList<Condition>();
	
	private Condition_group(Element element, Config_element parent) throws XPathExpressionException 
	{
		super(element, parent);
		NodeList list = (NodeList)Mobs.getXPath().evaluate("condition", element, XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++)
		{
			conditions.add(new Condition((Element)list.item(i), this));
		}
	}
	
	public static Condition_group get(Element element, Config_element parent) throws XPathExpressionException
	{
		Condition_group c = new Condition_group(element, parent);
		if (c.conditions.size() == 0) return null;
		return c;
	}
	
	public boolean passedConditions_check(Outcome_report or, LivingEntity live, Projectile projectile, Event orig_event)
	{
		for (Condition c : conditions)
		{
			Condition_report cr = new Condition_report();
			if (c.passes(cr, live, projectile, orig_event)) or.addPassed_condition(cr);
			else
			{
				or.addFailed_condition(cr);
				return false;
			}
		}
		
		return true;
	}
	
	public List<Condition> getConditions()
	{
		return conditions;
	}

}