package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MobsEvent
{
	List<MobsOutcome> outcomes = new ArrayList<MobsOutcome>();
	
	public MobsEvent(String event_name, NodeList list) throws XPathExpressionException
	{
		for (int i = 0; i < list.getLength(); i++)
		{
			outcomes.add(new MobsOutcome(event_name, (Element)list.item(i)));
		}
	}
	
	public void performActions(EventValues ev)
	{
		for (MobsOutcome mo : outcomes) mo.performActions(ev);
	}
}