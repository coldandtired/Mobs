package me.coldandtired.mobs.elements;

import java.util.HashMap;
import java.util.Map;

import me.coldandtired.mobs.enums.MParam;

public class Param 
{
	protected Map<MParam, Alternatives> params = new HashMap<MParam, Alternatives>();
	
	public void addParam(MParam param, Alternatives value)
	{
		params.put(param, value);
	}
	
	public Map<MParam, Alternatives> getParams()
	{
		return params;
	}
}