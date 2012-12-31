package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Currents;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MSubactions;
import me.coldandtired.mobs.subelements.Item_drop;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Config_element 
{
	protected Object action_groups;
	private Object targets;
	private Text_value world;
	protected Config_element parent; 
	private Object values;
	private Object amounts;
	protected Object mobs;
	private Object subactions;
	private Object items;
	private Currents currents; 
	//private Object names;
	private Object messages;
//	private String ce_name;
	private Object names;
	private Object xs;
	private Object ys;
	private Object zs;
	private Object effects;
	
	public Config_element(Element element, Config_element parent) throws XPathExpressionException
	{		
		if (element == null) return;
		//ce_name = element.getLocalName();
		this.parent = parent;

		names = fillStrings((Element)Mobs.getXPath().evaluate("names", element, XPathConstants.NODE));
		effects = fillStrings((Element)Mobs.getXPath().evaluate("effects", element, XPathConstants.NODE));
		xs = fillInts((Element)Mobs.getXPath().evaluate("xs", element, XPathConstants.NODE));
		ys = fillInts((Element)Mobs.getXPath().evaluate("ys", element, XPathConstants.NODE));
		zs = fillInts((Element)Mobs.getXPath().evaluate("zs", element, XPathConstants.NODE));
		fillSubactions((Element)Mobs.getXPath().evaluate("subactions", element, XPathConstants.NODE));
		fillAction_groups((Element)Mobs.getXPath().evaluate("action_group", element, XPathConstants.NODE));
		fillValues((Element)Mobs.getXPath().evaluate("values", element, XPathConstants.NODE));
		fillMobs((Element)Mobs.getXPath().evaluate("mobs", element, XPathConstants.NODE));
		fillAmounts((Element)Mobs.getXPath().evaluate("amounts", element, XPathConstants.NODE));
		fillTargets((Element)Mobs.getXPath().evaluate("targets", element, XPathConstants.NODE));		
		fillItems((Element)Mobs.getXPath().evaluate("items", element, XPathConstants.NODE));		
		messages = fillStrings((Element)Mobs.getXPath().evaluate("messages", element, XPathConstants.NODE));
		/*Element el = (Element)Mobs.getXPath().evaluate("target/world | world", element, XPathConstants.NODE);
		if (el != null) world = new Text_value(el);*/	
	}

	protected Object fillInts(Element group) throws XPathExpressionException
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
						temp.put(count, Integer.parseInt(el.getTextContent()));	
					}
					return new Alternatives(count, temp, false);
				}
				else
				{
					List<Integer> temp = new ArrayList<Integer>();
					for (int i = 0; i < list.getLength(); i ++)
					{
						temp.add(Integer.parseInt(list.item(i).getTextContent()));
					}
					return temp;
				}
			}
		}
		return null;
	}
	
	protected void fillAction_groups(Element group) throws XPathExpressionException
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
					for (int i = 0; i < list.getLength(); i++)
					{
						Element el = (Element)list.item(i);
						
						int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
						count += ratio;
						if (list.getLength() == 1) count = 1;
						el = (Element)Mobs.getXPath().evaluate("actions", el, XPathConstants.NODE);
						Action_group a = Action_group.get(el, this);			
						if (a != null) temp.put(count, a);
					}
					if (temp.size() > 0) action_groups = new Alternatives(count, temp, false);
				}
				else
				{
					List<Action_group> temp = new ArrayList<Action_group>();
					for (int i = 0; i < list.getLength(); i ++)
					{
						Action_group a = Action_group.get((Element)list.item(i), this);
						temp.add(a);
					}
					action_groups = temp;
				}
			}
		}
	}

	protected void fillMobs(Element group) throws XPathExpressionException
	{
		if (group != null)
		{
			NodeList list = (NodeList)Mobs.getXPath().evaluate("*", group, XPathConstants.NODESET);
			if (list.getLength() > 0)
			{
				if (list.getLength() > 1)
				{
					SortedMap<Integer, Object> temp = new TreeMap<Integer, Object>();
					int count = 0;
					for (int i = 0; i < list.getLength(); i ++)
					{
						Element el = (Element)list.item(i);
						int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
						count += ratio;
						if (list.getLength() == 1) count = 1;						
						temp.put(count, el.getTextContent());	
					}
					mobs = new Alternatives(count, temp, false);
				}
				else mobs = list.item(0).getTextContent().toUpperCase();
			}
		}
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
					targets = new Alternatives(count, temp, false);
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

	protected void fillAmounts(Element group) throws XPathExpressionException
	{
		if (group != null)
		{
			NodeList list = (NodeList)Mobs.getXPath().evaluate("*", group, XPathConstants.NODESET);
			if (list.getLength() > 0)
			{
				if (list.getLength() > 1)
				{
					SortedMap<Integer, Object> temp = new TreeMap<Integer, Object>();
					int count = 0;
					for (int i = 0; i < list.getLength(); i ++)
					{
						Element el = (Element)list.item(i);
						int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
						count += ratio;
						if (list.getLength() == 1) count = 1;						
						temp.put(count, el.getTextContent());	
					}
					amounts = new Alternatives(count, temp, false);
				}
				else amounts = list.item(0).getTextContent().toUpperCase();
			}
		}
	}

	protected void fillValues(Element group) throws XPathExpressionException
	{
		if (group != null)
		{
			NodeList list = (NodeList)Mobs.getXPath().evaluate("*", group, XPathConstants.NODESET);
			if (list.getLength() > 0)
			{
				if (list.getLength() > 1)
				{
					SortedMap<Integer, Object> temp = new TreeMap<Integer, Object>();
					int count = 0;
					for (int i = 0; i < list.getLength(); i ++)
					{
						Element el = (Element)list.item(i);
						int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
						count += ratio;
						if (list.getLength() == 1) count = 1;						
						temp.put(count, el.getTextContent());	
					}
					values = new Alternatives(count, temp, false);
				}
				else values = list.item(0).getTextContent().toUpperCase();
			}
		}
	}

	protected void fillItems(Element group) throws XPathExpressionException
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
						temp.put(count, new Item_drop(el, this));	
					}
					items = new Alternatives(count, temp, false);
				}
				else
				{
					List<Item_drop> temp = new ArrayList<Item_drop>();
					for (int i = 0; i < list.getLength(); i ++)
					{
						temp.add(new Item_drop((Element)list.item(i), this));
					}
					items = temp;
				}
			}
		}
	}
	
	protected Object fillStrings(Element group) throws XPathExpressionException
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
						temp.put(count, el.getTextContent());	
					}
					return new Alternatives(count, temp, false);
				}
				else
				{
					List<String> temp = new ArrayList<String>();
					for (int i = 0; i < list.getLength(); i ++)
					{
						temp.add(list.item(i).getTextContent());
					}
					return temp;
				}
			}
		}
		return null;
	}
	
	protected void fillSubactions(Element group) throws XPathExpressionException
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
						temp.put(count, MSubactions.valueOf(el.getTextContent().toUpperCase()));	
					}
					subactions = new Alternatives(count, temp, false);
				}
				else
				{
					List<MSubactions> temp = new ArrayList<MSubactions>();
					for (int i = 0; i < list.getLength(); i ++)
					{
						temp.add(MSubactions.valueOf(list.item(i).getTextContent().toUpperCase()));
					}
					subactions = temp;
				}
			}
		}
	}	
	
	protected List<String> fillString_list(String name, Element element) throws XPathExpressionException
	{
		List<String> temp = null;
		NodeList list = (NodeList)Mobs.getXPath().evaluate(name + "/*", element, XPathConstants.NODESET);
		if (list.getLength() > 0)
		{
			temp = new ArrayList<String>();
			for (int i = 0; i < list.getLength(); i++)
			{
				temp.add(list.item(i).getTextContent().toUpperCase());
			}
		}
		return temp;
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
	
	@SuppressWarnings("unchecked")
	protected List<Action_group> getAction_groups()
	{
		if (action_groups == null) return null;
				
		if (action_groups instanceof List<?>) return (List<Action_group>)action_groups;
		else
		{
			List<Action_group> temp = new ArrayList<Action_group>();
			temp.add((Action_group)((Alternatives)action_groups).getAlternative());
			return temp;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<MSubactions> getSubactions()
	{
		if (subactions != null)
		{
			if (subactions instanceof List<?>) return (List<MSubactions>)subactions;
			List<MSubactions> temp = new ArrayList<MSubactions>();
			temp.add((MSubactions)((Alternatives)subactions).getAlternative());
			return temp;
		}
		if (parent != null) return parent.getSubactions();
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected List<String> getMobs()
	{
		if (mobs != null)
		{
			if (mobs instanceof List<?>) return (List<String>)mobs;
			List<String> temp = new ArrayList<String>();
			temp.add(((Alternatives)mobs).getString_alternative());
			return temp;
		}
		if (parent != null) return parent.getMobs();
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getX()
	{
		if (xs instanceof Alternatives)
		{
			List<Integer> temp = new ArrayList<Integer>();
			temp.add((Integer)((Alternatives)xs).getAlternative());
			return temp;
		}
		else return (List<Integer>)xs;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getY()
	{
		if (ys instanceof Alternatives)
		{
			List<Integer> temp = new ArrayList<Integer>();
			temp.add((Integer)((Alternatives)ys).getAlternative());
			return temp;
		}
		else return (List<Integer>)ys;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getZ()
	{
		if (zs instanceof Alternatives)
		{
			List<Integer> temp = new ArrayList<Integer>();
			temp.add((Integer)((Alternatives)zs).getAlternative());
			return temp;
		}
		else return (List<Integer>)zs;
	}
	
	@SuppressWarnings("unchecked")
	protected List<String> getPlayer()
	{
		if (names != null)
		{
			if (names instanceof List<?>) return (List<String>)names;
			List<String> temp = new ArrayList<String>();
			temp.add((String)((Alternatives)names).getAlternative());
			return temp;
		}
		if (parent != null) return parent.getPlayer();
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected List<String> getEffects()
	{
		if (effects != null)
		{
			if (effects instanceof List<?>) return (List<String>)effects;
			List<String> temp = new ArrayList<String>();
			temp.add((String)((Alternatives)effects).getAlternative());
			return temp;
		}
		if (parent != null) return parent.getEffects();
		return null;
	}
	
	protected Currents getCurrents()
	{
		if (currents == null) currents = new Currents();
		currents.fill(getValue(), getAmount(), getSubactions(), getItems(), getMobs(), getMessages(), getEffects());
		return currents;		
	}
	
	@SuppressWarnings("unchecked")
	protected List<Item_drop> getItems()
	{
		if (items != null)
		{
			if (items instanceof List<?>) return (List<Item_drop>)items;
			List<Item_drop> temp = new ArrayList<Item_drop>();
			temp.add((Item_drop)((Alternatives)items).getAlternative());
			return temp;
		}
		if (parent != null) return parent.getItems();
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected List<String> getMessages()
	{
		if (messages != null)
		{
			if (messages instanceof List<?>) return (List<String>)messages;
			List<String> temp = new ArrayList<String>();
			temp.add((String)((Alternatives)messages).getAlternative());
			return temp;
		}
		if (parent != null) return parent.getMessages();
		return null;
	}
	
	protected String getValue()
	{
		if (values != null)
		{
			if (values instanceof String) return (String)values;
			else return (String)((Alternatives)values).getAlternative();
		}
		if (parent != null) return parent.getValue();
		return null;
	}
	
	protected Integer getAmount()
	{
		if (amounts != null)
		{
			if (amounts instanceof String) return Integer.parseInt((String)amounts);
			else return Integer.parseInt((String)((Alternatives)amounts).getAlternative());
		}
		if (parent != null) return parent.getAmount();
		return null;
	}
	
	protected boolean isValue_locked()
	{
		if (values instanceof Alternatives)
		{
			return ((Alternatives)values).isLocked();
		}
		return false;
	}
	
	protected World getWorld(LivingEntity le)
	{
		World w = null;
		if (world != null) return Bukkit.getWorld(world.getValue());
		if (parent != null) w = parent.getWorld(le);
		if (w != null) return w;
		if (le != null) return le.getWorld();
		return null;
	}
}