package me.coldandtired.mobs.elements;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.Mobs_condition;
import me.coldandtired.mobs.enums.Mobs_param;

import org.w3c.dom.Element;

public class Condition extends Param_base
{
	private Mobs_condition condition_type;
	
	public Condition(XPath xpath, Element el) 
	{
		super(xpath, el);
		condition_type = Mobs_condition.valueOf(el.getLocalName().toUpperCase());
	}
	
	public Mobs_condition getCondition_type() 
	{
		return condition_type;
	}

	public boolean matchesValue(int orig)
	{
		Mobs.log(orig);
		List<Integer> temp = new ArrayList<Integer>();
		for (String s : getString_param(Mobs_param.VALUE).split(","))
		{
			s = s.replace(" ", "");
			if (s.startsWith("above"))
			{
				s = s.replaceAll("above", "");
				if (orig > Integer.parseInt(s)) return true;
			}
			else if (s.startsWith("below"))
			{
				s = s.replaceAll("below", "");
				if (orig < Integer.parseInt(s)) return true;
			}
			else if (s.contains("to"))
			{
				String[] temp2 = s.split("to");
				int low = Math.min(Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]));
				int high = Math.max(Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]));
				for (int i = low; i <= high; i++) temp.add(i);
			}
			else temp.add(Integer.parseInt(s));
		}
		Mobs.log(temp.contains(orig));
		return temp.contains(orig);
	}
}