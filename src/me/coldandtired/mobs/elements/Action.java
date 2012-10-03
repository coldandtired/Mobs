package me.coldandtired.mobs.elements;

import javax.xml.xpath.XPath;
import me.coldandtired.mobs.enums.Mobs_action;
import org.w3c.dom.Element;

public class Action extends Param_base
{
	private Mobs_action action_type;
	
	public Action(XPath xpath, Element el) 
	{
		super(xpath, el);
		action_type = Mobs_action.valueOf(el.getLocalName().toUpperCase());		
	}
	
	public Mobs_action getAction_type() 
	{
		return action_type;
	}
}