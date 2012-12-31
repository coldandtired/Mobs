package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.Outcome_report;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Action_group extends Config_element
{
	private List<Action> actions;

	private Action_group(Element element, Config_element parent) throws XPathExpressionException 
	{
		super(element, parent);
		NodeList list = (NodeList)Mobs.getXPath().evaluate("action", element, XPathConstants.NODESET);
		actions = new ArrayList<Action>();
		for (int i = 0; i < list.getLength(); i++)
		{
			Element el = (Element)list.item(i);
			actions.add(new Action(el, this));
		}
	}
	
	public static Action_group get(Element element, Config_element parent) throws XPathExpressionException
	{
		Action_group a = new Action_group(element, parent);
		if (a.actions.size() > 0) return a;
		return null;
	}
	
	public boolean performActions(Outcome_report or, LivingEntity le, Event orig_event)
	{
		for (Action a : actions) a.perform(or, le, orig_event);
		return false;
	}
}