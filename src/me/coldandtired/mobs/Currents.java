package me.coldandtired.mobs;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import me.coldandtired.mobs.enums.MSubactions;
import me.coldandtired.mobs.subelements.Item_drop;

public class Currents
{
	private String value;
	private Integer amount;
	private List<MSubactions> subactions;
	private List<Item_drop> items;
	private List<String> mobs;
	private List<String> messages;
	private List<String> effects;
	
	public void fill(String value, Integer amount, List<MSubactions> subactions, List<Item_drop> items,
			List<String> mobs, List<String> messages, List<String> effects)
	{
		this.value = value;
		this.amount = amount;
		this.subactions = subactions;
		this.items = items;
		this.mobs = mobs;
		this.messages = messages;
		this.effects = effects;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public boolean getBool_value()
	{
		return value.equalsIgnoreCase("random") ? new Random().nextBoolean() : Boolean.parseBoolean(value);
	}
	
	public Integer getAmount(Integer orig)
	{
		return amount != null ? amount : value == null ? orig : Integer.parseInt(value);
	}
	
	public List<MSubactions> getSubactions()
	{
		return subactions;
	}
	
	public List<Item_drop> getItems()
	{
		return items;
	}
	
	public List<String> getMobs()
	{
		return mobs;
	}
	
	public List<String> getEffects()
	{
		return effects;
	}
	
	public World getWorld(Object o)
	{
		return o instanceof Location ? ((Location)o).getWorld() : ((LivingEntity)o).getWorld();
	}
	
	public List<String> getMessages()
	{
		return messages;
	}
}