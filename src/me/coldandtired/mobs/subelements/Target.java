package me.coldandtired.mobs.subelements;

import javax.xml.xpath.XPath;

import me.coldandtired.mobs.elements.Param_base;
import me.coldandtired.mobs.enums.Mobs_const;
import org.w3c.dom.Element;

public class Target extends Param_base
{	
	private Mobs_const target_type;
	
	public Target(XPath xpath, Element el)
	{	
		super(xpath, el);
		target_type = Mobs_const.valueOf(el.getLocalName().toUpperCase());
		String s = "name";
		if (el.hasAttribute(s)) params.put(Mobs_const.NAME, el.getAttribute(s));
		s = "location";
		if (el.hasAttribute(s)) params.put(Mobs_const.BLOCK, set_area(el.getAttribute(s)));
		s = "radius";
		if (el.hasAttribute(s)) params.put(Mobs_const.RADIUS, set_area(el.getAttribute(s)));
		s = "safe_radius";
		if (el.hasAttribute(s)) params.put(Mobs_const.SAFE_RADIUS, set_area(el.getAttribute(s)));	
	}

	public Mobs_const getTarget_type() 
	{		
		return target_type;
	}
	
	private int[] set_area(String s)
	{
		if (s == null || s.equalsIgnoreCase("")) return null;
		String[] orig = s.split(":");
		int[] temp = new int[3];
		temp[0] = Integer.parseInt(orig[0]);
		temp[1] = Integer.parseInt(orig[1]);
		temp[2] = Integer.parseInt(orig[2]);
		return temp;
	}
}