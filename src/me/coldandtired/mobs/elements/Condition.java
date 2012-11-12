package me.coldandtired.mobs.elements;

import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MCondition;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.managers.Target_manager;
import me.coldandtired.mobs.subelements.Area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.w3c.dom.Element;

public class Condition extends Config_element
{
	private MCondition condition_type;
	private String value;
	private String mob;
	private String amount;
	
	public Condition(Element element, Config_element parent) throws XPathExpressionException 
	{
		super(element, parent);
		condition_type = MCondition.valueOf(element.getLocalName().toUpperCase());
		Element el = (Element)Mobs.getXPath().evaluate("value", element, XPathConstants.NODE);
		
		if (element.getChildNodes().getLength() == 1) value = element.getTextContent();		
		else if (el != null) value = el.getTextContent();
		
		el = (Element)Mobs.getXPath().evaluate("amount", element, XPathConstants.NODE);
		if (el != null) amount = el.getTextContent();
			
		//el = (Element)Mobs.getXPath().evaluate("message", element, XPathConstants.NODE);
		//if (el != null) message = new Text_value(el);
			
		el = (Element)Mobs.getXPath().evaluate("mob", element, XPathConstants.NODE);
		if (el != null) mob = el.getTextContent();
	}

	public boolean passes(LivingEntity live, Event orig_event)
	{
		Mobs.debug("checking condition " + condition_type.toString());
		for (LivingEntity le : Target_manager.get().getTargets(getTarget(), live, orig_event))
		switch (getCondition_type())
		{
			case ADULT:
				if (le instanceof Ageable) return ((Ageable)le).isAdult();
				break;
			case ANGRY:
				if (le instanceof Wolf) return ((Wolf)le).isAngry();
				else if (le instanceof PigZombie) return ((PigZombie)le).isAngry();
				break;
			case AREA:
				for (Area a : Target_manager.get().getAreas()) if (a.containsLocation(le.getLocation())) return true;
				break;
			case BIOME:
				return matches(le.getLocation().getBlock().getBiome().name());
			case BLOCK_LIGHT_LEVEL: return matches(le.getLocation().getBlock().getLightFromBlocks());
			case CHUNK_MOB_COUNT:
				List<Location> list = Target_manager.get().getLocations(this, le, orig_event);
				for (Location loc : list)
				{
					int i = Target_manager.get().filter(Arrays.asList(loc.getChunk().getEntities()), getMob()).size();
					return matches(i);
				}
				break;
			case CUSTOM_FLAG_1:
			case CUSTOM_FLAG_2:
			case CUSTOM_FLAG_3:
			case CUSTOM_FLAG_4:
			case CUSTOM_FLAG_5:
			case CUSTOM_FLAG_6:
			case CUSTOM_FLAG_7:
			case CUSTOM_FLAG_8:
			case CUSTOM_FLAG_9:
			case CUSTOM_FLAG_10:
				return Data.hasData(le, MParam.valueOf(getCondition_type().toString()));	
			case CUSTOM_INT_1:
			case CUSTOM_INT_2:
			case CUSTOM_INT_3:
			case CUSTOM_INT_4:
			case CUSTOM_INT_5:
			case CUSTOM_INT_6:
			case CUSTOM_INT_7:
			case CUSTOM_INT_8:
			case CUSTOM_INT_9:
			case CUSTOM_INT_10:
				String s = (String)Data.getData(le, MParam.valueOf(getCondition_type().toString()));
				if (s != null) return matches(Integer.parseInt(s));
				break;
			case CUSTOM_STRING_1:
			case CUSTOM_STRING_2:
			case CUSTOM_STRING_3:
			case CUSTOM_STRING_4:
			case CUSTOM_STRING_5:
			case CUSTOM_STRING_6:
			case CUSTOM_STRING_7:
			case CUSTOM_STRING_8:
			case CUSTOM_STRING_9:
			case CUSTOM_STRING_10:
				s = (String)Data.getData(le, MParam.valueOf(getCondition_type().toString()));
				if (s != null) return matches(s);
				break;
			case DEATH_CAUSE:
				if (!le.isDead()) return false;
				return matches(le.getLastDamageCause().toString());			
			case KILLED_BY_PLAYER:
				if (le.isDead()) return le.getKiller() instanceof Player;
				break;
			case LIGHT_LEVEL: return matches(le.getLocation().getBlock().getLightLevel());
			case LUNAR_PHASE: 
				World w = getWorld(le);
				long days = w.getFullTime()/24000;
				long phase= days % 8;
				return matches((int)phase);
			case NAME:
				if (le instanceof Player)
				{
					return matches(((Player)le).getName());
				}
				else if (le.hasMetadata("mobs_data"))
				{
					String name = (String)Data.getData(le, MParam.NAME);
					if (name == null) return false;
					return matches(name);
				}
				break;
			case OWNER:
				if (le instanceof Tameable)
				{
					AnimalTamer tamer = ((Tameable)le).getOwner();
					if (tamer != null) return matches(tamer.getName());
				}
				break;
			case PLAYER_HAS_PERMISSION:
				if (!(le instanceof Player)) return false;
				for (String ss : getValue().split(","))
				{
					if (((Player)le).hasPermission(ss.trim())) return true;
				}
				break;
			case PLAYER_IS_OP:
				if (le instanceof Player) return ((Player)le).isOp();
				break;
			case POWERED:
				if (le instanceof Creeper) return ((Creeper)le).isPowered();
				break;
			case RAINING:
				w = getWorld(le);
				return w.hasStorm();
			case SADDLED:
				if (le instanceof Pig) return ((Pig)le).hasSaddle();
				break;
			case SHEARED:
				if (le instanceof Sheep) return ((Sheep)le).isSheared();
				break;
			case SKY_LIGHT_LEVEL: return matches(le.getLocation().getBlock().getLightFromSky());
			case SPAWN_REASON:
				String spawn_reason = (String)Data.getData(le, MParam.SPAWN_REASON);
				if (spawn_reason == null) return false;
				return matches(spawn_reason);
			case TAMED:
				if (le instanceof Wolf) return ((Wolf)le).isTamed();
				break;
			case THUNDERING: 
				w = getWorld(le);
				return w.isThundering();
			case WORLD_MOB_COUNT:
				int i = Target_manager.get().filter(getWorld(null).getEntities(), getMob()).size();
				return matches(i);
			case WORLD_NAME:
				w = getWorld(le);
				return matches(w.getName());
			case WORLD_TIME: 
				w = getWorld(le);
				return matches((int)w.getTime());
			case X: return matches(le.getLocation().getBlockX());
			case Y: return matches(le.getLocation().getBlockY());
			case Z: return matches(le.getLocation().getBlockZ());
						
			case NOT_ADULT:
				if (le instanceof Ageable) return !((Ageable)le).isAdult();
				break;
			case NOT_ANGRY:
				if (le instanceof Wolf) return !((Wolf)le).isAngry();
				else if (le instanceof PigZombie) return !((PigZombie)le).isAngry();
				break;
			case NOT_AREA:
				for (Area a : Target_manager.get().getAreas()) if (a.containsLocation(le.getLocation())) return false;
				return true;				
			case NOT_BIOME:
				return !matches(le.getLocation().getBlock().getBiome().name());
			case NOT_CUSTOM_FLAG_1:
			case NOT_CUSTOM_FLAG_2:
			case NOT_CUSTOM_FLAG_3:
			case NOT_CUSTOM_FLAG_4:
			case NOT_CUSTOM_FLAG_5:
			case NOT_CUSTOM_FLAG_6:
			case NOT_CUSTOM_FLAG_7:
			case NOT_CUSTOM_FLAG_8:
			case NOT_CUSTOM_FLAG_9:
			case NOT_CUSTOM_FLAG_10:
				return !Data.hasData(le, MParam.valueOf(getCondition_type().toString().substring(4)));
			case NOT_CUSTOM_STRING_1:
			case NOT_CUSTOM_STRING_2:
			case NOT_CUSTOM_STRING_3:
			case NOT_CUSTOM_STRING_4:
			case NOT_CUSTOM_STRING_5:
			case NOT_CUSTOM_STRING_6:
			case NOT_CUSTOM_STRING_7:
			case NOT_CUSTOM_STRING_8:
			case NOT_CUSTOM_STRING_9:
			case NOT_CUSTOM_STRING_10:
				s = (String)Data.getData(le, MParam.valueOf(getCondition_type().toString().substring(4)));
				if (s != null) return !matches(s);
				break;	
			case NOT_DEATH_CAUSE:
				if (!le.isDead()) return false;
				return !matches(le.getLastDamageCause().toString());
			case NOT_KILLED_BY_PLAYER:
				if (le.isDead()) return !(le.getKiller() instanceof Player);
				break;
			case NOT_NAME:
				if (le instanceof Player)
				{
					return !matches(((Player)le).getName());
				}
				else if (le.hasMetadata("mobs_data"))
				{
					String name = (String)Data.getData(le, MParam.NAME);
					if (name == null) return true;
					return !matches(name);
				}
				break;
			case NOT_OWNER:
				if (le instanceof Tameable)
				{
					AnimalTamer tamer = ((Tameable)le).getOwner();
					if (tamer != null) return !matches(tamer.getName());
				}
				return true;
			case NOT_PLAYER_HAS_PERMISSION:
				if (!(le instanceof Player)) return true;
				for (String ss : getValue().split(","))
				{
					if (((Player)le).hasPermission(ss.trim())) return false;
				}
				break;
			case NOT_PLAYER_IS_OP:
				if (le instanceof Player) return !((Player)le).isOp();
				break;
			case NOT_POWERED:
				if (le instanceof Creeper) return !((Creeper)le).isPowered();
				break;
			case NOT_RAINING: 
				w = getWorld(le);
				return !w.hasStorm();
			case NOT_SADDLED:
				if (le instanceof Pig) return !((Pig)le).hasSaddle();
				break;
			case NOT_SHEARED:
				if (le instanceof Sheep) return !((Sheep)le).isSheared();
				break;
			case NOT_SPAWN_REASON:
				spawn_reason = (String)Data.getData(le, MParam.SPAWN_REASON);
				if (spawn_reason == null) return true;
				return !matches(spawn_reason);
			case NOT_TAMED:
				if (le instanceof Wolf) return !((Wolf)le).isTamed();
				break;
			case NOT_THUNDERING: 
				w = getWorld(le);
				return !w.isThundering();
			case NOT_WORLD_NAME:
				w = getWorld(le);
				return !matches(w.getName());
		}
		return false;
	}
	
	public String[] getMob()
	{
		if (mob == null) return null;
		return mob.toUpperCase().split(":");
	}
	
	public boolean matches(int orig)
	{
		String temp = amount == null ? value : amount;
		if (temp == null) return false;
		
		temp = temp.replace(" ", "").toUpperCase();
		
		for (String s :temp.split(","))
		{
			if (s.startsWith("ABOVE"))
			{
				if (Integer.parseInt(s.replace("ABOVE", "")) < orig) return true;
			}
			else if (s.startsWith("BELOW"))
			{
				if (Integer.parseInt(s.replace("BELOW", "")) > orig) return true;
			}
			else if (s.contains("TO"))
			{
				String[]temp2 = s.split("TO");
				if (orig >= Math.min(Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1])) &&
					orig <= Math.max(Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]))) return true;
			}
			else if (Integer.parseInt(s) == orig) return true;
		}
		return false;
	}

	public boolean matches(String orig)
	{
		if (value == null) return false;
		for (String s : value.split(","))
		{
			if (s.trim().equalsIgnoreCase(orig)) return true;
		}
		return false;
	}
	
	public MCondition getCondition_type() 
	{
		return condition_type;
	}

	public String getValue()
	{
		return value;
	}
}