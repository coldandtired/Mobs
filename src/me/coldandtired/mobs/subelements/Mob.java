package me.coldandtired.mobs.subelements;

import me.coldandtired.mobs.elements.Text_value;

import org.bukkit.entity.EntityType;
import org.w3c.dom.Element;

public class Mob 
{
	private EntityType entity_type;
	private Text_value name;
	
	public Mob(Element el)
	{
		entity_type = EntityType.valueOf(el.getLocalName().toUpperCase());
		if (el.hasChildNodes())	name = new Text_value(el);
	}
	
	public EntityType getType()
	{
		return entity_type;
	}
	
	public String getName()
	{
		if (name == null) return null;
		
		return name.getValue();
	}
}