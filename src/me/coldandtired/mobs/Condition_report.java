package me.coldandtired.mobs;

public class Condition_report 
{
	private String name;
	private String check_value;
	private String actual_value;
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setCheck_value(String value)
	{
		check_value = value;
	}
	
	public void setActual_value(Object value)
	{
		actual_value = "" + value;
	}
	
	public String getName()
	{
		return "  " + name;
	}
	
	public String getCheck_value()
	{
		return "    needed - " + check_value;
	}
	
	public String getActual_value()
	{
		return "    got - " + actual_value;
	}
}