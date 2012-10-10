package me.coldandtired.mobs.elements;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import me.coldandtired.mobs.enums.Mobs_condition;
import me.coldandtired.mobs.enums.Mobs_const;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Condition extends P
{
	private Mobs_condition condition_type;
	
	public Condition(XPath xpath, Element element) 
	{
		condition_type = Mobs_condition.valueOf(element.getLocalName().toUpperCase());
		if (element.hasChildNodes() && element.getChildNodes().getLength() == 1) params.put(Mobs_const.VALUE, element.getChildNodes().item(0).getTextContent());
		try
		{
			NodeList list = (NodeList)xpath.evaluate("world", element, XPathConstants.NODESET);		
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
					temp.put(count, el.getTextContent());	
				}
				params.put(Mobs_const.WORLD, new Alternatives(count, temp));
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public Mobs_condition getCondition_type() 
	{
		return condition_type;
	}
}