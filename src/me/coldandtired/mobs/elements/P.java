package me.coldandtired.mobs.elements;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import me.coldandtired.mobs.enums.Mobs_const;
import me.coldandtired.mobs.enums.Mobs_target;
import me.coldandtired.mobs.subelements.Mobs_number;
import me.coldandtired.mobs.subelements.Target;

public class P 
{
	protected Map<Mobs_const, Object> params = new HashMap<Mobs_const, Object>();
	
	protected int getRatio(Element el)
	{
		return el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
	}
	
	public String getString(Mobs_const param)
	{
		return (String)params.get(param);
	}
	
	public String getString_alt(Mobs_const param)
	{
		if (!params.containsKey(param)) return null;
		return (String)getAlternative(param);
	}
	
	public Object getObject(Mobs_const param)
	{
		return params.get(param);
	}
	
 	public Object getAlternative(Mobs_const param)
	{
		if (!params.containsKey(param)) return null;
		return ((Alternatives)params.get(param)).get_alternative();
	}
	
	public Target getTarget()
	{
		if (!params.containsKey(Mobs_const.TARGET)) return null;
		Target t = (Target)getAlternative(Mobs_const.TARGET);
		return t.getTarget_type() == Mobs_target.SELF ? null : t;
	}
	
	public int getInt(Mobs_const param, int i)
	{
		Object o = getAlternative(param);
		if (o == null) return i;
		return ((Mobs_number)o).getAbsolute_value(i);
	}
	
	public int[] getInt_array(Mobs_const param)
	{
		// used in block targets
		return (int[])params.get(param);
	}
	
	public Mobs_number getMobs_number()
	{
		Object o = getAlternative(Mobs_const.NUMBER);
		if (o != null) return (Mobs_number)o;
		return null;
	}
	
	public boolean hasParam(Mobs_const param)
	{
		return params.containsKey(param);
	}
}