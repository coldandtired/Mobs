package me.coldandtired.mobs.subelements;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import me.coldandtired.mobs.elements.Alternatives;
import me.coldandtired.mobs.elements.P;
import me.coldandtired.mobs.enums.Mobs_const;
import me.coldandtired.mobs.enums.Mobs_target;

import org.bukkit.entity.EntityType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Target extends P
{
	private Mobs_target target_type;
	
	public Target(XPath xpath, Element element)
	{	
		target_type = Mobs_target.valueOf(element.getLocalName().toUpperCase());
		if (target_type.equals(Mobs_target.PLAYER))	params.put(Mobs_const.NAME, element.getTextContent());
		
		else if (target_type.equals(Mobs_target.BLOCK))
		{
			int[] temp = {
					Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent()),
					Integer.parseInt(element.getElementsByTagName("y").item(0).getTextContent()),
					Integer.parseInt(element.getElementsByTagName("z").item(0).getTextContent())
					};
			params.put(Mobs_const.BLOCK, temp);
		}	
		else if (target_type.equals(Mobs_target.AREA)) params.put(Mobs_const.NAME, element.getTextContent());
		
		// subvalues
		try
		{
			NodeList list = (NodeList)xpath.evaluate("amount", element, XPathConstants.NODESET);		
			Map<Integer, Object> temp;
			int count;
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, new Mobs_number(el.getTextContent()));
				}
				params.put(Mobs_const.NUMBER, new Alternatives(count, temp));
			}
			
			String s = null;
			for (EntityType et : EntityType.values()) s = s + " | " + et.toString().toLowerCase();
			s = s.substring(3);
					
			list = (NodeList)xpath.evaluate(s, element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, el.getLocalName().toUpperCase() + ":" + el.getTextContent());	
				}
				params.put(Mobs_const.MOB, new Alternatives(count, temp));
			}			
			
			list = (NodeList)xpath.evaluate("x_radius", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, new Mobs_number(el.getTextContent()));
				}
				params.put(Mobs_const.X_RADIUS, new Alternatives(count, temp));
			}
			
			list = (NodeList)xpath.evaluate("x_safe_radius", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, new Mobs_number(el.getTextContent()));
				}
				params.put(Mobs_const.X_SAFE_RADIUS, new Alternatives(count, temp));
			}
			
			list = (NodeList)xpath.evaluate("y_radius", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, new Mobs_number(el.getTextContent()));
				}
				params.put(Mobs_const.Y_RADIUS, new Alternatives(count, temp));
			}
			
			list = (NodeList)xpath.evaluate("y_safe_radius", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, new Mobs_number(el.getTextContent()));
				}
				params.put(Mobs_const.Y_SAFE_RADIUS, new Alternatives(count, temp));
			}
			
			list = (NodeList)xpath.evaluate("z_radius", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, new Mobs_number(el.getTextContent()));
				}
				params.put(Mobs_const.Z_RADIUS, new Alternatives(count, temp));
			}
			
			list = (NodeList)xpath.evaluate("z_safe_radius", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = getRatio(el);
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, new Mobs_number(el.getTextContent()));
				}
				params.put(Mobs_const.Z_SAFE_RADIUS, new Alternatives(count, temp));
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public Mobs_target getTarget_type()
	{
		return target_type;
	}
}