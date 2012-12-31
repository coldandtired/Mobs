package me.coldandtired.mobs.elements;

import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;

import org.w3c.dom.Element;

public class Alternatives 
{
	private int total;
	private SortedMap<Integer, Object> choices;
	private boolean locked = false;	
	
	public Alternatives(int total, SortedMap<Integer, Object> choices, boolean locked)
	{
		this.total = total;
		this.choices = choices;
		this.locked = locked;
	}
	
	public boolean isLocked()
	{
		return locked;
	}
	
	public static Object get(String name, Element element) throws XPathExpressionException
	{
		Element el = (Element)Mobs.getXPath().evaluate(name, element, XPathConstants.NODE);
		if (el == null) return null;
		//boolean locked = el.hasAttribute("lock") ? Boolean.parseBoolean(el.getAttribute("lock")) : false;
		
		return null;
	}
	
	public Object getAlternative()
	{		
		int t = new Random().nextInt(total) + 1;
		for (Integer i : choices.keySet()) if (i >= t) return choices.get(i);
		return null;
	}
	
	public String getString_alternative()
	{
		return ((String)getAlternative()).toUpperCase();
	}
	
	public boolean getBoolean_alternative()
	{
		String s = getString_alternative();
		if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes")) return true;
		else if (s.equalsIgnoreCase("random")) return new Random().nextBoolean();
		else return false;
	}
	
	public Map<Integer, Object> getChoices()
	{
		return choices;
	}
}