package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Config_element 
{
	private Object targets;
	private Text_value world;
	private Config_element parent; 
	
	public Config_element(Element element, Config_element parent) throws XPathExpressionException
	{		
		this.parent = parent;
		Element el = (Element)Mobs.getXPath().evaluate("target/world | world", element, XPathConstants.NODE);
		if (el != null) world = new Text_value(el);
		
		el = (Element)Mobs.getXPath().evaluate("target", element, XPathConstants.NODE);
		if (el != null) fillTargets(el);	
	}
	
	protected void fillTargets(Element group) throws XPathExpressionException
	{
		if (group != null)
		{
			NodeList list = (NodeList)Mobs.getXPath().evaluate("*", group, XPathConstants.NODESET);
			if (list.getLength() > 0)
			{
				boolean b = group.hasAttribute("use_all") ? Boolean.parseBoolean(group.getAttribute("use_all")) : false;
				if (list.getLength() > 1 && !b)
				{					
					SortedMap<Integer, Object> temp = new TreeMap<Integer, Object>();
					int count = 0;
					for (int i = 0; i < list.getLength(); i ++)
					{
						Element el = (Element)list.item(i);
						int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
						count += ratio;
						if (list.getLength() == 1) count = 1;						
						temp.put(count, new Target(el, this));	
					}
					targets = new Alternatives(count, temp);
				}
				else
				{
					List<Target> temp = new ArrayList<Target>();
					for (int i = 0; i < list.getLength(); i ++)
					{
						temp.add(new Target((Element)list.item(i), this));
					}
					targets = temp;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<Target> getTargets()
	{
		List<Target> t = null;
		if (targets != null)
		{
			if (targets instanceof List<?>) return (List<Target>)targets;
			else
			{
				List<Target> temp = new ArrayList<Target>();
				temp.add((Target)((Alternatives)targets).getAlternative());
				return temp;
			}
		}
		if (parent != null) t = parent.getTargets();
		return t;
	}
	
	/*private void fillTargets(Element element) throws XPathExpressionException
	{
		NodeList list = (NodeList)Mobs.getXPath().evaluate(MTarget.getXpath(), element, XPathConstants.NODESET);		
		if (list.getLength() > 0)
		{
			SortedMap<Integer, Object> temp = new TreeMap<Integer, Object>();
			int count = 0;
			for (int i = 0; i < list.getLength(); i ++)
			{
				Element el = (Element)list.item(i);
				int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
				count += ratio;
				if (list.getLength() == 1) count = 1;						
				temp.put(count, new Target(el, parent));	
			}
			targets = new Alternatives(count, temp);
		}
	}
	
	public Target getTarget()
	{
		Target t = null;
		if (targets != null) return (Target)targets.get_alternative();
		if (parent != null) t = parent.getTarget();
		if (t != null) return t;
		return null;
	}*/
	
	public World getWorld(LivingEntity le)
	{
		World w = null;
		if (world != null) return Bukkit.getWorld(world.getValue());
		if (parent != null) w = parent.getWorld(le);
		if (w != null) return w;
		if (le != null) return le.getWorld();
		return null;
	}
}