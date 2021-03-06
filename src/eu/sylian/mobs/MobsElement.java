package eu.sylian.mobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.sylian.mobs.Enums.ElementType;

public class MobsElement
{
	private MobsElement parent;
	private Map<ElementType, Object> values = new HashMap<ElementType, Object>();
	private List<List<MobsCondition>> conditions;
		
	public void setParent(MobsElement parent)
	{
		this.parent = parent;
	}
	
	public MobsElement(Element element, MobsElement parent, Map<String, MobsCondition> linked_conditions) throws XPathExpressionException
	{		
		this.parent = parent;
		for (ElementType et : ElementType.values())
		{
			String name = et.toString().toLowerCase();
			if (element.hasAttribute(name))
			{
				values.put(et, element.getAttribute(name));
				continue;
			}
			
			NodeList list = (NodeList)Mobs.getXPath().evaluate(name + "/entry", element, XPathConstants.NODESET);
			if (list.getLength() == 0) continue;	
			
			int count = 0;
			SortedMap<Integer, MobsElement> temp = new TreeMap<Integer, MobsElement>();
			for (int i = 0; i < list.getLength(); i++)
			{
				Element el = (Element)list.item(i);
				int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1; 
				count += ratio;
				temp.put(count, new MobsElement(el, this, linked_conditions));
			}
			values.put(et, temp);
		}
		conditions = fillConditions(element, linked_conditions);
	}
	
	@SuppressWarnings("unchecked")
	MobsElement getCurrentElement(ElementType et, EventValues ev)
	{
		MobsElement mv = this;
		
		while (mv != null && !mv.values.containsKey(et)) mv = mv.parent;
		if (mv == null) return null;
		
		if (mv.hasConditions() && !mv.passesConditions(ev)) return null;
		
		Object o = mv.values.get(et);
	
		if (o instanceof String)
		{
			if (mv.passesConditions(ev)) return mv;
			return null;
		}
		
		Map<Integer, MobsElement> map = (Map<Integer, MobsElement>)o;
		
		boolean conditional = false;
		for (MobsElement me : map.values())
		{
			if (me.hasConditions())
			{
				conditional = true;
				break;
			}
		}
	
		if (conditional)
		{
			for (MobsElement me : map.values())
			{
				if (me.passesConditions(ev)) return me;
			}
			return null;
		}
		
		int temp = map.size();
		
		if (temp > 1)
		{
			temp = 0;
			for (int i : map.keySet()) if (i > temp) temp = i;
			
			temp = new Random().nextInt(temp) + 1;
			for (int i : map.keySet())
			{
				if (temp <= i) return map.get(i);
			}
		}
		else
		for (int i : map.keySet())
		{
			return map.get(i);
		}
		
		return null;
	}
	
	String getString(ElementType et)
	{
		Object o = values.get(et);
		if (o != null) return (String)o;
		
		return null;
	}
	
// condition stuff	
	
	public boolean hasConditions()
	{
		return conditions != null;
	}
	
	List<List<MobsCondition>> fillConditions(Element element, Map<String, MobsCondition> linked_conditions)
	{		
		MobsCondition conditions = MobsCondition.fill(element);
		
		List<MobsCondition> list = new ArrayList<MobsCondition>();
		List<List<MobsCondition>> main_list = new ArrayList<List<MobsCondition>>();
		if (conditions != null)
		{
			list.add(conditions);
			main_list.add(list);
		}
		
		String s = getString(ElementType.CONDITION);
		if (s == null)
		{
			if (main_list.size() > 0) return main_list; else return null;
		}
			
		s = s.toUpperCase();
		
		if (linked_conditions != null)
		{
			String[] temp = s.replace(" ", "").split(",");
			for (String c : temp)
			{
				list = new ArrayList<MobsCondition>();
				String[] temp2 = c.split("\\+");
				for (String la : temp2)
				{
					MobsCondition mc = linked_conditions.get(la);
					if (mc == null) continue;
					
					list.add(mc);
				}
				main_list.add(list);
			}
		}
		
		return main_list;
	}
		
	public boolean passesConditions(EventValues ev)
	{		
		if (conditions == null || ev.alreadyPassed(this) || conditions.size() == 0) return true;

		ev.addCheckedElement(this);	
		
		for (List<MobsCondition> lmc : conditions)
		{
			boolean b = true;
			for (MobsCondition mc : lmc)
			{
				if (!mc.passes(ev)) b = false;
			}
			if (b) return true;
		}
		return false;
		
	}	
}