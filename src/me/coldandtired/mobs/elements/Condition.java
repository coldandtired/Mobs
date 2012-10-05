package me.coldandtired.mobs.elements;

import javax.xml.xpath.XPath;
import me.coldandtired.mobs.enums.Mobs_condition;
import org.w3c.dom.Element;

public class Condition extends Param_base
{
	private Mobs_condition condition_type;
	
	public Condition(XPath xpath, Element el) 
	{
		super(xpath, el);
		condition_type = Mobs_condition.valueOf(el.getLocalName().toUpperCase());
	}
	
	public Mobs_condition getCondition_type() 
	{
		return condition_type;
	}
}