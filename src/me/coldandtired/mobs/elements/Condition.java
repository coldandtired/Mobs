package me.coldandtired.mobs.elements;

import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.extra_events.Area;
import me.coldandtired.mobs.Condition_report;
import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MCondition;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
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

	public boolean passes(Condition_report cr, LivingEntity live, Projectile projectile, Event orig_event)
	{
		cr.setName(condition_type.toString());
		boolean b;
		List<Target> targets = getTargets();
		Object o = targets != null ? targets.get(0).getSingle_target(live, orig_event) : live;
		
		/*if (o instanceof Location)
		{
			Location loc = (Location)o;
			switch (condition_type)
			{
				case AREA:
					for (String value : values)
					{
						String s = value.contains(":") ? "" : loc.getWorld().getName() + ":";
						s += value;
						cr.setCheck_value(s);
						Area area = Mobs.extra_events.getArea(s);
						cr.setActual_value(get_string_from_loc(loc));
						if (area != null && area.isIn_area(loc)) return true;
					}
					break;
				case BIOME:
				case NOT_BIOME:	
					return matches(cr, loc.getBlock().getBiome().name(), MCondition.BIOME);
				case BLOCK_LIGHT_LEVEL: return matches(cr, loc.getBlock().getLightFromBlocks());
				case CHUNK_MOB_COUNT:
					if (mobs != null)
					{
						for (String m : mobs)
						{
							int i = Target.filter(Arrays.asList(loc.getChunk().getEntities()), m.toUpperCase().split(":")).size();
							if (matches(cr, i)) return true;
						}
						break;
					}
					else return matches(cr, Target.filter(Arrays.asList(loc.getChunk().getEntities()), null).size());
			
				case LIGHT_LEVEL: return matches(cr, loc.getBlock().getLightLevel());
				case LUNAR_PHASE: 
					long days = loc.getWorld().getFullTime()/24000;
					long phase= days % 8;
					return matches(cr, (int)phase);
				case RAINING:
				case NOT_RAINING:
					return matches(cr, loc.getWorld().hasStorm(), MCondition.RAINING);
				case SKY_LIGHT_LEVEL: return matches(cr, loc.getBlock().getLightFromSky());
				case THUNDERING:
				case NOT_THUNDERING:
					return matches(cr, loc.getWorld().isThundering(), MCondition.THUNDERING);
				case WORLD_MOB_COUNT:
					if (mobs != null)
					{
						for (String m : mobs)
						{
							int i = Target.filter(loc.getWorld().getEntities(), m.toUpperCase().split(":")).size();
							if (matches(cr, i)) return true;
						}
						break;
					}
					else return matches(cr, Target.filter(loc.getWorld().getEntities(), null).size());
				case WORLD_NAME:
				case NOT_WORLD_NAME:
					return matches(cr, loc.getWorld().getName(), MCondition.WORLD_NAME);
				case WORLD_TIME: 
					return matches(cr, (int)loc.getWorld().getTime());
				case X: return matches(cr, loc.getBlockX());
				case Y: return matches(cr, loc.getBlockY());
				case Z: return matches(cr, loc.getBlockZ());
				
				case NOT_AREA:
					for (String value : values)
					{
						String s = value.contains(":") ? "" : loc.getWorld().getName() + ":";
						s += value;
						Area a = Mobs.extra_events.getArea(s);
						if (a != null && a.isIn_area(loc)) return false;
					}
					return true;
			}
			return false;
		}
		
		else if (o instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity)o;		
			switch (condition_type)
			{
				case ADULT:
				case NOT_ADULT:
					if (le instanceof Ageable)
					{
						return matches(cr, ((Ageable)le).isAdult(), MCondition.ADULT);
					}
					break;
				case ANGRY:
				case NOT_ANGRY:
					if (le instanceof Wolf)
					{
						return matches(cr, ((Wolf)le).isAngry(), MCondition.ANGRY);
					}
					else if (le instanceof PigZombie)
					{
						return matches(cr, ((PigZombie)le).isAngry(), MCondition.ANGRY);
					}
					break;
				case AREA:
					for (String value : values)
					{
						String s = value.contains(":") ? "" : getWorld(le).getName() + ":";
						s += value;
						cr.setCheck_value(s);
						Area area = Mobs.extra_events.getArea(s);
						cr.setActual_value(get_string_from_loc(le.getLocation()));
						if (area != null && area.isIn_area(le.getLocation())) return true;
					}
					break;
				case BIOME:
				case NOT_BIOME:	
					return matches(cr, le.getLocation().getBlock().getBiome().name(), MCondition.BIOME);
				case BLOCK_LIGHT_LEVEL: return matches(cr, le.getLocation().getBlock().getLightFromBlocks());
				case CHUNK_MOB_COUNT:
					if (mobs != null)
					{
						for (String m : mobs)
						{
							int i = Target.filter(Arrays.asList(le.getLocation().getChunk().getEntities()), m.toUpperCase().split(":")).size();
							if (matches(cr, i)) return true;
						}
						break;
					}
					else return matches(cr, Target.filter(Arrays.asList(le.getLocation().getChunk().getEntities()), null).size());
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
					cr.setCheck_value("true");
					b = Data.hasData(le, MParam.valueOf(getCondition_type().toString()));
					cr.setActual_value(b);
					return b;
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
					if (s != null) return matches(cr, Integer.parseInt(s));
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
					if (s != null) return matches(cr, s);
					break;
				case DEATH_CAUSE:
				case NOT_DEATH_CAUSE:
					if (!le.isDead()) return false;
					return matches(cr, le.getLastDamageCause().toString(), MCondition.DEATH_CAUSE);			
				case KILLED_BY_PLAYER:
				case NOT_KILLED_BY_PLAYER:
					if (le.isDead()) return matches(cr, (le.getKiller() instanceof Player), MCondition.KILLED_BY_PLAYER);
					return false;
				case LIGHT_LEVEL: return matches(cr, le.getLocation().getBlock().getLightLevel());
				case LUNAR_PHASE: 
					World w = getWorld(le);
					long days = w.getFullTime()/24000;
					long phase= days % 8;
					return matches(cr, (int)phase);
				case NAME:
				case NOT_NAME:	
					if (le instanceof Player)
					{
						return matches(cr, ((Player)le).getName(), MCondition.NAME);
					}
					else if (le.hasMetadata("mobs_data"))
					{
						String name = (String)Data.getData(le, MParam.NAME);
						return matches(cr, name, MCondition.NAME);
					}
					cr.setCheck_value(values, condition_type.equals(MCondition.NAME));
					cr.setActual_value("no Mobs data");
					break;
				case OCELOT_TYPE:
				case NOT_OCELOT_TYPE:
					if (le instanceof Ocelot)
					{
						return matches(cr, ((Ocelot)le).getCatType().toString(), MCondition.OCELOT_TYPE);
					}
					cr.setActual_value("not an ocelot");
					break;
				case OWNER:
				case NOT_OWNER:
					if (le instanceof Tameable)
					{
						AnimalTamer tamer = ((Tameable)le).getOwner();
						if (tamer != null) return matches(cr, tamer.getName(), MCondition.OWNER);
					}
					break;
				case PLAYER_HAS_PERMISSION:
					if (!(le instanceof Player)) return false;
					for (String ss : values)
					{
						if (((Player)le).hasPermission(ss.toLowerCase())) return true;
					}
					break;
				case PLAYER_IS_OP:
				case NOT_PLAYER_IS_OP:
					if (le instanceof Player) return matches(cr, ((Player)le).isOp(), MCondition.PLAYER_IS_OP);
					break;
				case POWERED:
				case NOT_POWERED:
					if (le instanceof Creeper) return matches(cr, ((Creeper)le).isPowered(), MCondition.POWERED);
					break;
				case PROJECTILE:
				case NOT_PROJECTILE:
					return matches(cr, projectile != null, MCondition.PROJECTILE);
				case PROJECTILE_TYPE:
				case NOT_PROJECTILE_TYPE:
					if (projectile == null) break;
					return matches(cr, projectile.getType().toString(), MCondition.PROJECTILE_TYPE);
				case RAINING:
				case NOT_RAINING:
					w = getWorld(le);
					return matches(cr, w.hasStorm(), MCondition.RAINING);
				case SADDLED:
				case NOT_SADDLED:
					if (le instanceof Pig) return matches(cr, ((Pig)le).hasSaddle(), MCondition.SADDLED);
					break;
				case SHEARED:
				case NOT_SHEARED:
					if (le instanceof Sheep) return matches(cr, ((Sheep)le).isSheared(), MCondition.SHEARED);
					break;
				case SKY_LIGHT_LEVEL: return matches(cr, le.getLocation().getBlock().getLightFromSky());
				case SPAWN_REASON:
				case NOT_SPAWN_REASON:
					String spawn_reason = (String)Data.getData(le, MParam.SPAWN_REASON);
					if (spawn_reason == null) return false;
					return matches(cr, spawn_reason, MCondition.SPAWN_REASON);
				case TAMED:
				case NOT_TAMED:
					if (le instanceof Wolf) return matches(cr, ((Wolf)le).isTamed(), MCondition.TAMED);
					break;
				case THUNDERING:
				case NOT_THUNDERING:
					w = getWorld(le);
					return matches(cr, w.isThundering(), MCondition.THUNDERING);
				case VILLAGER_TYPE:
				case NOT_VILLAGER_TYPE:
					if (le instanceof Villager)
					{
						return matches(cr, ((Villager)le).getProfession().toString(), MCondition.VILLAGER_TYPE);
					}
					cr.setActual_value("not a villager");
					break;
				case WORLD_MOB_COUNT:
					if (mobs != null)
					{
						for (String m : mobs)
						{
							int i = Target.filter(getWorld(le).getEntities(), m.toUpperCase().split(":")).size();
							if (matches(cr, i)) return true;
						}
						break;
					}
					else return matches(cr, Target.filter(getWorld(le).getEntities(), null).size());
				case WORLD_NAME:
				case NOT_WORLD_NAME:
					w = getWorld(le);
					return matches(cr, w.getName(), MCondition.WORLD_NAME);
				case WORLD_TIME: 
					w = getWorld(le);
					return matches(cr, (int)w.getTime());
				case X: return matches(cr, le.getLocation().getBlockX());
				case Y: return matches(cr, le.getLocation().getBlockY());
				case Z: return matches(cr, le.getLocation().getBlockZ());
							
				case NOT_AREA:
					for (String value : values)
					{
						s = value.contains(":") ? "" : getWorld(le).getName() + ":";
						s += value;
						Area a = Mobs.extra_events.getArea(s);
						if (a == null) return true;
						if (a.isIn_area(le.getLocation())) return false;
					}
					return true;				
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
					if (s != null) return !matches(cr, s);
					break;	
				case NOT_PLAYER_HAS_PERMISSION:
					if (!(le instanceof Player)) return true;
					for (String ss : values)
					{
						if (((Player)le).hasPermission(ss.toLowerCase())) return false;
					}
					break;
			}
		}
		return false;*/
		
		if (o instanceof Location)
		{
			Location loc = (Location)o;
			switch (condition_type)
			{
				case AREA:
					String s = value.contains(":") ? "" : loc.getWorld().getName() + ":";
					s += value;
					cr.setCheck_value(s);
					Area area = Mobs.extra_events.getArea(s);
					if (area == null)
					{
						cr.setActual_value("no area with that name");
						return false;
					}
					cr.setActual_value(get_string_from_loc(loc));
					if (area.isIn_area(loc)) return true;
					break;
				case BIOME:
					cr.setCheck_value(value);
					s = loc.getBlock().getBiome().name();
					cr.setActual_value(s);
					return matches(cr, s);
				case BLOCK_LIGHT_LEVEL:
					cr.setCheck_value(value);
					cr.setActual_value("" + loc.getBlock().getLightFromBlocks());
					return matches(loc.getBlock().getLightFromBlocks());
				//case BIOME:
				//case NOT_BIOME:	
					//return matches(cr, loc.getBlock().getBiome().name(), MCondition.BIOME);
				//case BLOCK_LIGHT_LEVEL: return matches(cr, loc.getBlock().getLightFromBlocks());
				case CHUNK_MOB_COUNT:
					int i = Target.filter(Arrays.asList(loc.getChunk().getEntities()), getMob()).size();
					return matches(i);
				case LIGHT_LEVEL: return matches(loc.getBlock().getLightLevel());
				case LUNAR_PHASE: 
					long days = loc.getWorld().getFullTime()/24000;
					long phase= days % 8;
					return matches((int)phase);
				case RAINING:
					return loc.getWorld().hasStorm();
				case SKY_LIGHT_LEVEL: return matches(loc.getBlock().getLightFromSky());
				case THUNDERING: 
					return loc.getWorld().isThundering();
				case WORLD_MOB_COUNT:
					i = Target.filter(getWorld(null).getEntities(), getMob()).size();
					return matches(i);
				case WORLD_NAME:
					return matches(cr, loc.getWorld().getName());
				case WORLD_TIME: 
					return matches((int)loc.getWorld().getTime());
				case X: return matches(loc.getBlockX());
				case Y: return matches(loc.getBlockY());
				case Z: return matches(loc.getBlockZ());
				

				case NOT_AREA:
					s = value.contains(":") ? "" : loc.getWorld().getName() + ":";
					s += value;
					Area a = Mobs.extra_events.getArea(s);
					if (a == null) return true;
					if (a.isIn_area(loc)) return false;
					return true;				
				case NOT_BIOME:
					return !matches(cr, loc.getBlock().getBiome().name());

				case NOT_RAINING: 
					return !loc.getWorld().hasStorm();
				case NOT_THUNDERING: 
					return !loc.getWorld().isThundering();
				case NOT_WORLD_NAME:
					return !matches(cr, loc.getWorld().getName());
				/*case CHUNK_MOB_COUNT:
					if (mobs != null)
					{
						for (String m : mobs)
						{
							int i = Target.filter(Arrays.asList(loc.getChunk().getEntities()), m.toUpperCase().split(":")).size();
							if (matches(cr, i)) return true;
						}
						break;
					}
					else return matches(cr, Target.filter(Arrays.asList(loc.getChunk().getEntities()), null).size());
			
				case LIGHT_LEVEL: return matches(cr, loc.getBlock().getLightLevel());
				case LUNAR_PHASE: 
					long days = loc.getWorld().getFullTime()/24000;
					long phase= days % 8;
					return matches(cr, (int)phase);
				case RAINING:
				case NOT_RAINING:
					return matches(cr, loc.getWorld().hasStorm(), MCondition.RAINING);
				case SKY_LIGHT_LEVEL: return matches(cr, loc.getBlock().getLightFromSky());
				case THUNDERING:
				case NOT_THUNDERING:
					return matches(cr, loc.getWorld().isThundering(), MCondition.THUNDERING);
				case WORLD_MOB_COUNT:
					if (mobs != null)
					{
						for (String m : mobs)
						{
							int i = Target.filter(loc.getWorld().getEntities(), m.toUpperCase().split(":")).size();
							if (matches(cr, i)) return true;
						}
						break;
					}
					else return matches(cr, Target.filter(loc.getWorld().getEntities(), null).size());
				case WORLD_NAME:
				case NOT_WORLD_NAME:
					return matches(cr, loc.getWorld().getName(), MCondition.WORLD_NAME);
				case WORLD_TIME: 
					return matches(cr, (int)loc.getWorld().getTime());
				case X: return matches(cr, loc.getBlockX());
				case Y: return matches(cr, loc.getBlockY());
				case Z: return matches(cr, loc.getBlockZ());
				
				case NOT_AREA:
					for (String value : values)
					{
						String s = value.contains(":") ? "" : loc.getWorld().getName() + ":";
						s += value;
						Area a = Mobs.extra_events.getArea(s);
						if (a != null && a.isIn_area(loc)) return false;
					}
					return true;*/
			}
		}
		else if (o instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity)o;
			switch (condition_type)
			{
				case ADULT:
					cr.setCheck_value("true");
					if (le instanceof Ageable)
					{
						b = ((Ageable)le).isAdult();
						cr.setActual_value(b);
						return b;
					}
					cr.setActual_value("not an animal");
					break;
				case ANGRY:
					cr.setCheck_value("true");
					if (le instanceof Wolf)
					{
						b = ((Wolf)le).isAngry();
						cr.setActual_value(b);
						return b;
					}
					else if (le instanceof PigZombie)
					{
						b = ((PigZombie)le).isAngry();
						cr.setActual_value(b);
						return b;
					}
					cr.setActual_value("not a wolf/pig_zombie");
					break;
				case AREA:
					String s = value.contains(":") ? "" : getWorld(le).getName() + ":";
					s += value;
					cr.setCheck_value(s);
					Area area = Mobs.extra_events.getArea(s);
					if (area == null)
					{
						cr.setActual_value("no area with that name");
						return false;
					}
					cr.setActual_value(get_string_from_loc(le.getLocation()));
					if (area.isIn_area(le.getLocation())) return true;
					break;
				case BIOME:
					cr.setCheck_value(value);
					s = le.getLocation().getBlock().getBiome().name();
					cr.setActual_value(s);
					return matches(cr, s);
				case BLOCK_LIGHT_LEVEL:
					cr.setCheck_value(value);
					cr.setActual_value("" + le.getLocation().getBlock().getLightFromBlocks());
					return matches(le.getLocation().getBlock().getLightFromBlocks());
				case CHUNK_MOB_COUNT:
					int i = Target.filter(Arrays.asList(le.getLocation().getChunk().getEntities()), getMob()).size();
					return matches(i);
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
					cr.setCheck_value("true");
					b = Data.hasData(le, MParam.valueOf(getCondition_type().toString()));
					cr.setActual_value(b);
					return b;
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
					s = (String)Data.getData(le, MParam.valueOf(getCondition_type().toString()));
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
					if (s != null) return matches(cr, s);
					break;
				case DEATH_CAUSE:
					if (!le.isDead()) return false;
					return matches(cr, le.getLastDamageCause().toString());			
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
						return matches(cr, ((Player)le).getName());
					}
					else if (le.hasMetadata("mobs_data"))
					{
						String name = (String)Data.getData(le, MParam.NAME);
						if (name == null) return false;
						return matches(cr, name);
					}
					cr.setActual_value("no data");
					break;
				case OCELOT_TYPE:
					if (le instanceof Ocelot)
					{
						return matches(cr, ((Ocelot)le).getCatType().toString());
					}
					cr.setActual_value("not an ocelot");
					break;
				case OWNER:
					if (le instanceof Tameable)
					{
						AnimalTamer tamer = ((Tameable)le).getOwner();
						if (tamer != null) return matches(cr, tamer.getName());
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
					return matches(cr, spawn_reason);
				case TAMED:
					if (le instanceof Wolf) return ((Wolf)le).isTamed();
					break;
				case THUNDERING: 
					w = getWorld(le);
					return w.isThundering();
				case VILLAGER_TYPE:
					if (le instanceof Villager)
					{
						return matches(cr, ((Villager)le).getProfession().toString());
					}
					cr.setActual_value("not a villager");
					break;
				case WORLD_MOB_COUNT:
					i = Target.filter(getWorld(null).getEntities(), getMob()).size();
					return matches(i);
				case WORLD_NAME:
					w = getWorld(le);
					return matches(cr, w.getName());
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
					s = value.contains(":") ? "" : getWorld(le).getName() + ":";
					s += value;
					Area a = Mobs.extra_events.getArea(s);
					if (a == null) return true;
					if (a.isIn_area(le.getLocation())) return false;
					return true;				
				case NOT_BIOME:
					return !matches(cr, le.getLocation().getBlock().getBiome().name());
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
					if (s != null) return !matches(cr, s);
					break;	
				case NOT_DEATH_CAUSE:
					if (!le.isDead()) return false;
					return !matches(cr, le.getLastDamageCause().toString());
				case NOT_KILLED_BY_PLAYER:
					if (le.isDead()) return !(le.getKiller() instanceof Player);
					break;
				case NOT_NAME:
					if (le instanceof Player)
					{
						return !matches(cr, ((Player)le).getName());
					}
					else if (le.hasMetadata("mobs_data"))
					{
						String name = (String)Data.getData(le, MParam.NAME);
						if (name == null) return true;
						return !matches(cr, name);
					}
					break;
				case NOT_OCELOT_TYPE:
					if (le instanceof Ocelot)
					{
						return !matches(cr, ((Ocelot)le).getCatType().toString());
					}
					cr.setActual_value("not an ocelot");
					break;
				case NOT_OWNER:
					if (le instanceof Tameable)
					{
						AnimalTamer tamer = ((Tameable)le).getOwner();
						if (tamer != null) return !matches(cr, tamer.getName());
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
				case PROJECTILE:
					return projectile != null;
				case NOT_PROJECTILE:
					return projectile == null;
				case PROJECTILE_TYPE:
					if (projectile == null) break;
					return matches(cr, projectile.getType().toString());
				case NOT_PROJECTILE_TYPE:
					if (projectile == null) break;
					return !matches(cr, projectile.getType().toString());
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
					cr.setCheck_value(value);
					if (spawn_reason == null) return true;
					b = !matches(cr, spawn_reason);
					cr.setActual_value(spawn_reason);
					return b;
				case NOT_TAMED:
					if (le instanceof Wolf) return !((Wolf)le).isTamed();
					break;										
				case NOT_THUNDERING: 
					w = getWorld(le);
					return !w.isThundering();
				case NOT_VILLAGER_TYPE:
					if (le instanceof Villager)
					{
						return !matches(cr, ((Villager)le).getProfession().toString());
					}
					cr.setActual_value("not a villager");
					break;
				case NOT_WORLD_NAME:
					w = getWorld(le);
					return !matches(cr, w.getName());
			}
		}
		return false;
	}
	
	public String[] getMob()
	{
		if (mob == null) return null;
		return mob.toUpperCase().split(":");
	}
	
	public boolean matches(Condition_report cr, boolean orig, MCondition cond)
	{
		boolean b = condition_type.equals(cond);
		cr.setCheck_value(Boolean.toString(!b));
		cr.setActual_value(orig);
		return orig == b;
	}
	
	public boolean matches(Condition_report cr, String orig, MCondition cond)
	{
		boolean b = condition_type.equals(cond);
		cr.setCheck_value(value);
		cr.setActual_value(orig);
		if (value == null) return !b;
		return value.contains(orig) == b;
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

	public boolean matches(Condition_report cr, String orig)
	{
		cr.setCheck_value(value);
		cr.setActual_value(orig);
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
	
	private String get_string_from_loc(Location loc)
	{
		return loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ", " + loc.getWorld().getName();
	}
}