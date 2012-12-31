package me.coldandtired.mobs;

public class Action_report
{
	private String name;
	private String property;
	private String value;
	private boolean success = false; 
	
	public Action_report(String name, String property)
	{
		this.name = name;
		this.property = property;
	}
	
	public void setSuccess()
	{
		success = true;
	}
	
	public boolean isSuccess()
	{
		return success;
	}
	
	public void setValue(Object value)
	{
		this.value = "" + value;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getProperty()
	{
		String s = property == null ? "" : " " + property;
		String s2 = value == null ? "" : " " + value;
		return "    " + name + s + s2;
	}
}