
package me.coldandtired.mobs.subelements;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Config_element;
import me.coldandtired.mobs.elements.Text_value;
import me.coldandtired.mobs.enums.MTarget;

import org.w3c.dom.Element;

public class Target extends Config_element
{
	private MTarget target_type;
	private Text_value player;
	private Text_value area_name;
	private Text_value x;
	private Text_value y;
	private Text_value z;
	private Text_value mob;
	private Text_value amount;
	
	public Target(Element element, Config_element parent) throws XPathExpressionException
	{	
		super(element, parent);
		target_type = MTarget.valueOf(element.getLocalName().toUpperCase());
		Element el;
		switch (target_type)
		{
			case PLAYER:
				player = new Text_value(element);
				break;
			case AROUND:
			case BLOCK:
				el = (Element)Mobs.getXPath().evaluate("x", element, XPathConstants.NODE);
				if (el != null) x = new Text_value(el);
				el = (Element)Mobs.getXPath().evaluate("y", element, XPathConstants.NODE);
				if (el != null) y = new Text_value(el);
				el = (Element)Mobs.getXPath().evaluate("z", element, XPathConstants.NODE);
				if (el != null) z = new Text_value(el);
				break;
			case AREA:
				if (element.getChildNodes().getLength() == 1) area_name = new Text_value(element);
				break;				
		}
		
		el = (Element)Mobs.getXPath().evaluate("amount", element, XPathConstants.NODE);
		if (el != null) amount = new Text_value(el);
		
		el = (Element)Mobs.getXPath().evaluate("mob", element, XPathConstants.NODE);
		if (el != null) mob = new Text_value(el);		
	}
	
	public MTarget getTarget_type()
	{
		return target_type;
	}

	public String getArea_name()
	{
		if (area_name == null) return null;
		return area_name.getValue();
	}
	
	public Text_value getX()
	{
		return x;
	}
	
	public Text_value getY()
	{
		return y;
	}
	
	public Text_value getZ()
	{
		return z;
	}
	
	public String getPlayer()
	{
		if (player == null) return null;
		return player.getValue();
	}
		
	public String[] getMob()
	{
		if (mob == null) return null;
		return mob.getValue().toUpperCase().split(":");
	}
	
	public int getAmount(int orig)
	{
		if (amount == null) return orig;
		return amount.getInt_value(orig);			
	}
}