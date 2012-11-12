package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MAction;
import me.coldandtired.mobs.enums.MEvent;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Actions extends Config_element
{
	private List<Action> actions;

	private Actions(Element element, Config_element parent) throws XPathExpressionException 
	{
		super(element, parent);
		NodeList list = (NodeList)Mobs.getXPath().evaluate(MAction.getXpath(), element, XPathConstants.NODESET);
		actions = new ArrayList<Action>();
		for (int i = 0; i < list.getLength(); i++)
		{
			Element el = (Element)list.item(i);
			actions.add(new Action(el, this));
		}
	}
	
	public static Actions get(Element element, Config_element parent) throws XPathExpressionException
	{
		Actions a = new Actions(element, parent);
		if (a.actions.size() > 0) return a;
		return null;
	}
	
	public boolean performActions(LivingEntity le, MEvent event, Event orig_event)
	{
		for (Action a : actions) a.perform(le, event, orig_event);
		return false;
	}
	
	public List<Action> getActions()
	{
		return actions;
	}
}