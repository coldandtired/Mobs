package me.coldandtired.mobs.elements;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import me.coldandtired.mobs.enums.Mobs_action;
import me.coldandtired.mobs.enums.Mobs_param;
import me.coldandtired.mobs.enums.Mobs_subelement;
import me.coldandtired.mobs.subelements.Item_drop;
import me.coldandtired.mobs.subelements.Mobs_number;
import me.coldandtired.mobs.subelements.Target;

public class Param_base 
{
	protected Map<Mobs_param, Object> params = new HashMap<Mobs_param, Object>();

	public Param_base(XPath xpath, Element el)
	{
		String s = "match_data";
		if (el.hasAttribute(s)) addParam(Mobs_param.MATCH_DATA);
		s = "match_amount";
		if (el.hasAttribute(s)) addParam(Mobs_param.MATCH_AMOUNT);
		s = "match_enchantments";
		if (el.hasAttribute(s)) addParam(Mobs_param.MATCH_ENCHANTMENTS);
		s = "world";
		if (el.hasAttribute(s)) params.put(Mobs_param.WORLD, el.getAttribute(s));
		s = "value";
		if (el.hasAttribute(s)) params.put(Mobs_param.VALUE, el.getAttribute(s));
		
		try
		{
			NodeList list = (NodeList)xpath.evaluate("*", el, XPathConstants.NODESET);
			if (list.getLength() > 0)
			{
				Map<Integer, Object> targets = new HashMap<Integer, Object>();
				int target_count = 0;
				Map<Integer, Object> messages = new HashMap<Integer, Object>();
				int message_count = 0;
				Map<Integer, Object> items = new HashMap<Integer, Object>();
				int item_count = 0;
				Map<Integer, Object> numbers = new HashMap<Integer, Object>();
				int number_count = 0;
				Map<Integer, Object> mobs = new HashMap<Integer, Object>();
				int mob_count = 0;
				
				for (int i = 0; i < list.getLength(); i++)
				{
					Element el2 = (Element)list.item(i);
					s = el2.getLocalName();
					int ratio = el2.hasAttribute("ratio") ? Integer.parseInt(el2.getAttribute("ratio")) : 1;
					
					try
					{
						Mobs_subelement sub = Mobs_subelement.valueOf(s.toUpperCase());
						switch (sub)
						{
							case AREA:
							case BLOCK:
							case MECHANISM:
							case PLAYER:
							case SELF:
							case WORLD:
								target_count += ratio;
								if (list.getLength() == 1) target_count = 1;						
								targets.put(target_count, new Target(xpath, el2));	
								continue;
								
							case MESSAGE:
								message_count += ratio;
								if (list.getLength() == 1) message_count = 1;						
								messages.put(message_count, el2.getAttribute("text"));		
								continue;
								
							case ITEM:
								item_count += ratio;
								if (list.getLength() == 1) item_count = 1;						
								items.put(item_count, new Item_drop(el2));		
								continue;
								
							case AMOUNT:
							case DURATION:
							case POWER:
							case TIME:
								number_count += ratio;
								if (list.getLength() == 1) number_count = 1;						
								numbers.put(number_count, new Mobs_number(el2));	
								continue;
						}
					}
					catch (Exception e)
					{
						if (el.getLocalName().equalsIgnoreCase(Mobs_action.SPAWN_MOB.toString()))
						{
							mob_count += ratio;
							if (list.getLength() == 1) mob_count = 1;
							mobs.put(mob_count, el2.getLocalName().toUpperCase() + ":" + el2.getAttribute("name"));
						}
					}
				}
				if (targets.size() > 0) params.put(Mobs_param.TARGET, new Alternatives(target_count, targets));
				if (messages.size() > 0) params.put(Mobs_param.MESSAGE, new Alternatives(message_count, messages));
				if (items.size() > 0) params.put(Mobs_param.ITEM, new Alternatives(item_count, items));
				if (numbers.size() > 0) params.put(Mobs_param.NUMBER, new Alternatives(number_count, numbers));
				if (mobs.size() > 0) params.put(Mobs_param.MOB, new Alternatives(mob_count, mobs));
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public String getString_param(Mobs_param param)
	{
		return (String)params.get(param);
	}
	
	public Integer getInt_param(Mobs_param param)
	{
		if (!params.containsKey(param)) return null;
		return (Integer)params.get(param);
	}
	
	public Boolean getBoolean_param(Mobs_param param)
	{
		// used in item matching
		if (!params.containsKey(param)) return null;
		return (Boolean)params.get(param);
	}
	
	public int[] getInt_array(Mobs_param param)
	{
		// used in block targets
		return (int[])params.get(param);
	}

	public Object getAlternative(Mobs_param param)
	{
		if (!params.containsKey(param)) return null;
		return ((Alternatives)params.get(param)).get_alternative();
	}
	
	public Mobs_number getMobs_number()
	{
		Object o = getAlternative(Mobs_param.NUMBER);
		if (o != null) return (Mobs_number)o;
		return null;
	}
	
	public void addParam(Mobs_param param, Object o)
	{
		params.put(param, o);
	}

	public void addParam(Mobs_param param)
	{
		params.put(param, null);
	}
	
	public boolean hasParam(Mobs_param param)
	{
		return params.containsKey(param);
	}
	
	public void removeParam(Mobs_param param)
	{
		params.remove(param);
	}

	public Target getTarget()
	{
		if (!params.containsKey(Mobs_param.TARGET)) return null;
		Target t = (Target)getAlternative(Mobs_param.TARGET);
		return t.getTarget_type() == Mobs_subelement.SELF ? null : t;
	}
}