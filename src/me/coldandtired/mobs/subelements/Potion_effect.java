package me.coldandtired.mobs.subelements;

import org.w3c.dom.Element;

/** inherits name */
public class Potion_effect
{
	private String level;
	private String duration;
	
	public Potion_effect(Element el) 
	{
		if (el.hasAttribute("level")) level = el.getAttribute("level");
		if (el.hasAttribute("duration")) duration = el.getAttribute("duration");
	}	
	
	public String getLevel()
	{
		return level;
	}
	
	public void setLevel(String level)
	{
		this.level = level;
	}
	
	public String getDuration()
	{
		return duration;
	}
	
	public void setDuration(String duration)
	{
		this.duration = duration;
	}
}