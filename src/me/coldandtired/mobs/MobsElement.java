package me.coldandtired.mobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;


import me.coldandtired.mobs.Enums.ElementType;

import org.w3c.dom.Element;

public class MobsElement
{
	private MobsElement parent;
	
	private Map<ElementType, ElementWrapper> wrappers = new HashMap<ElementType, ElementWrapper>();
	
	public boolean hasConditions()
	{
		return wrappers.containsKey(ElementType.CONDITION);
	}
	
	public MobsElement(Element element, MobsElement parent) throws XPathExpressionException
	{
		this.parent = parent;
		for (ElementType et : ElementType.values())
		{
			ElementWrapper ew = new ElementWrapper(element, et, this);
			if (ew.isFilled()) wrappers.put(et, ew);
		}
	}
	
	ElementWrapper getWrapper(ElementType et)
	{
		return wrappers.get(et);
	}
	
	public List<MobsElement> getActions(EventValues ev) 
	{
		return wrappers.get(ElementType.ACTION).getActions(this, ev);		
	}
	
	public List<MobsElement> getConditions(EventValues bukkit_values) 
	{
		return wrappers.get(ElementType.CONDITION).getConditions(this, bukkit_values);		
	}
	
	/*public boolean isBubbledFrom(MobsElement me)
	{
		if (!Mobs.allow_bubbling || me.equals(this)) return false;
		
		while (!me.equals(this) && me.parent != null) me = me.parent;
		return me.equals(this);
	}
	
	public boolean isLocked()
	{return false;
		//String s = getElement_value(ElementType.LOCKED);
		//return s == null ? true : Boolean.parseBoolean(s);
	}*/

	public MobsElement getCurrentElement(ElementType et, EventValues ev)
	{
		MobsElement me = getContainingElement(et);
		if (me != null && me.wrappers.containsKey(et))
		{
			return me.wrappers.get(et).getCurrentElement(et, me, ev);
		}
		return null;
	}
	
	MobsElement getContainingElement(ElementType et)
	{//TODO remove bubbling?
		if (!Mobs.allow_bubbling)
		{
			if (wrappers.containsKey(et)) return this; else return null;
		}
		
		MobsElement mv = this;
		
		while (mv != null && !mv.wrappers.containsKey(et)) mv = mv.parent;
		if (mv == null) return null;
		
		return mv;
	}	
	
	String getString(ElementType et)
	{
		return getWrapper(et).getValue();
	}
}