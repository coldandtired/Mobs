package me.coldandtired.mobs.elements;

import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;

import me.coldandtired.mobs.Mobs;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Text_value 
{
	private String simple_value;
	private Alternatives values; 
	
	public Text_value(Element element)
	{
		try
		{
			NodeList list = (NodeList)Mobs.getXPath().evaluate("value", element, XPathConstants.NODESET);
			if (list.getLength() == 0)
			{
				simple_value = element.getTextContent();
				return;
			}
			
			SortedMap<Integer, Object> temp = new TreeMap<Integer, Object>();
			int count = 0;
			for (int i = 0; i < list.getLength(); i++)
			{
				Element el = (Element)list.item(i);
				int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
				count += ratio;
				if (list.getLength() == 1) count = 1;
				temp.put(count, el.getTextContent());
			}
			//values = new Alternatives(count, temp);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public String getValue()
	{
		if (simple_value != null) return simple_value;
		return (String)values.getAlternative();
	}
	
	public int getInt_value(int orig)
	{
		String[] value = getValue().split(":");
		
		String s = value[0].replace(" ", "");
		int a = 0;
		if (s.contains("to"))
		{
			String[] temp = s.split("to");
			a = Math.min(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
			int b = Math.max(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
			a = new Random().nextInt((b - a) + 1) + a;
		}
		else a = Integer.parseInt(s);
		
		if (value.length == 1) return a;
		
		if (value[1].contains("%"))
		{
			if (value[1].contains("+")) return ((orig * a) / 100) + orig;
			else if (value[1].contains("-")) return orig - ((orig * a) / 100);
			else return (orig * a) / 100;
		}
		else
		{
			if (value[1].contains("+")) return orig + a;
			else if (value[1].contains("-")) return orig - a;
		}
		return orig;	
	}
}