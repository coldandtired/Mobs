package me.coldandtired.mobs.subelements;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Param;
import me.coldandtired.mobs.elements.Text_value;
import me.coldandtired.mobs.enums.MTarget;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.w3c.dom.Element;

public class Target extends Param
{
	private MTarget target_type;
	private Text_value player;
	private Area area;
	private Text_value area_name;
	private Text_value x;
	private Text_value y;
	private Text_value z;
	private Text_value mob;
	private Text_value amount;
	private Text_value world;
	
	public Target(Element element) throws XPathExpressionException
	{	
		target_type = MTarget.valueOf(element.getLocalName().toUpperCase());
		Element el;
		switch (target_type)
		{
			case PLAYER:
				player = new Text_value(element);
				break;
			case AROUND:
			case BLOCK:
				el = (Element)Mobs.getXPath().evaluate("x", element, XPathConstants.NODE);
				if (el != null) x = new Text_value(el);
				el = (Element)Mobs.getXPath().evaluate("y", element, XPathConstants.NODE);
				if (el != null) y = new Text_value(el);
				el = (Element)Mobs.getXPath().evaluate("z", element, XPathConstants.NODE);
				if (el != null) z = new Text_value(el);
				break;
			case AREA:
				if (element.getChildNodes().getLength() == 0) area_name = new Text_value(element);
				else area = new Area(element); 
				break;				
		}
		
		el = (Element)Mobs.getXPath().evaluate("amount", element, XPathConstants.NODE);
		if (el != null) amount = new Text_value(el);
		
		el = (Element)Mobs.getXPath().evaluate("mob", element, XPathConstants.NODE);
		if (el != null) mob = new Text_value(el);
		
		el = (Element)Mobs.getXPath().evaluate("world", element, XPathConstants.NODE);
		if (el != null) world = new Text_value(el);
	}
	
	public MTarget getTarget_type()
	{
		return target_type;
	}

	public String getArea_name()
	{
		if (area_name == null) return null;
		return area_name.getValue();
	}

	public Area getArea()
	{
		return area;
	}
	
	public String getX()
	{
		if (x == null) return null;
		return x.getValue();
	}
	
	public String getY()
	{
		if (y == null) return null;
		return x.getValue();
	}
	
	public String getZ()
	{
		if (z == null) return null;
		return z.getValue();
	}
	
	public String getPlayer()
	{
		if (player == null) return null;
		return player.getValue();
	}
	
	public World getWorld(LivingEntity le)
	{
		if (world == null) return le.getWorld();
		return Bukkit.getWorld(world.getValue());
	}
	
	public String[] getMob()
	{
		if (mob == null) return null;
		return mob.getValue().toUpperCase().split(":");
	}
	
	public int getAmount(int orig)
	{
		if (amount == null) return orig;
		return amount.getInt_value(orig);			
	}
}