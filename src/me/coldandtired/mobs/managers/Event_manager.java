package me.coldandtired.mobs.managers;

import java.util.List;
import java.util.Map;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Condition;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.enums.Mobs_param;
import me.coldandtired.mobs.subelements.Area;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;

public class Event_manager 
{
	public Event_manager()
	{
		
	}
	
	public void start_actions(List<Outcome> outcomes, Mobs_event event, LivingEntity le, Event orig_event, boolean single_outcome)
	{			
		Mobs.debug("------------------");
		Mobs.debug("Event - " + event.toString());
		Mobs.debug("Outcomes - " + outcomes.size());
		for (Outcome o : outcomes)
		{
			List<List<Condition>> conds = o.getConditions();
			int s = conds == null ? 0 : conds.size();
			Mobs.debug("Checking outcome, condition groups - " + s);
			boolean perform = true;
			if (conds != null)
			{
				for (List<Condition> cs : conds)
				{
					perform = true;
					Mobs.debug("Checking " + cs.size() + " conditions");
					for (Condition c : cs)
					{
						Mobs.debug("Checking " + c.getCondition_type().toString());
						if (!check_condition(c, le, orig_event))
						{
							perform = false;
							Mobs.debug("Failed");
							break;
						}
						Mobs.debug("Passed");
					}
					if (perform) break;
				}
			}
			else Mobs.debug("No conditions!");
			
			if (perform)
			{
				Mobs.getInstance().getAction_manager().performActions(o, event, le, orig_event);
				if (single_outcome) return;
			}
		}
		Mobs.debug("No matching conditions!");
		Mobs.debug("------------------");
	}	
	
	@SuppressWarnings("unchecked")
	boolean check_condition(Condition condition, LivingEntity le, Event event)
	{
		String value = condition.getString_param(Mobs_param.VALUE);
		Target t = condition.getTarget();
		if (t == null && le == null) return false;
		
		switch (condition.getCondition_type())
		{
			case ADULT:
				if (le instanceof Ageable) return ((Ageable)le).isAdult();
				break;
			case ANGRY:
				if (le instanceof Wolf) return ((Wolf)le).isAngry();
				else if (le instanceof PigZombie) return ((PigZombie)le).isAngry();
				break;
			case AREA:
				for (Area a : Mobs.getInstance().getTarget_manager().getAreas()) if (a.containsLocation(le.getLocation())) return true;
				break;
			case BIOME:
				for (String s : value.split(",")) if (le.getLocation().getBlock().getBiome().name().equalsIgnoreCase(s)) return true;
				break;
			case DEATH_CAUSE:
				if (!le.isDead()) return false;
				for (String s : value.split(",")) if (s.equalsIgnoreCase(le.getLastDamageCause().toString())) return true;
				break;
			case KILLED_BY_PLAYER:
				if (le.isDead()) return le.getKiller() instanceof Player;
				break;
			case LIGHT_LEVEL: return (condition.matchesValue(le.getLocation().getBlock().getLightLevel()));
			case NAME:
				if (le instanceof Player)
				{
					for (String s : value.split(",")) if (s.equalsIgnoreCase(((Player)le).getName())) return true;
				}
				else if (le.hasMetadata("mobs_data"))
				{
					String name = (String) ((Map<String, Object>)le.getMetadata("mobs_data").get(0).value()).get(Mobs_param.NAME.toString());
					if (name == null) return false;
					for (String s : value.split(",")) if (s.equalsIgnoreCase(name)) return true;
				}
				break;
			case RAINING:
				World w = getWorld(condition);
				return w == null ? false : w.hasStorm();
			case POWERED:
				if (le instanceof Creeper) return ((Creeper)le).isPowered();
				break;
			case SADDLED:
				if (le instanceof Pig) return ((Pig)le).hasSaddle();
				break;
			case SHEARED:
				if (le instanceof Sheep) return ((Sheep)le).isSheared();
				break;
			case SPAWN_REASON:
				if (le.hasMetadata("mobs_data"))
				{
					String spawn_reason = (String) ((Map<String, Object>)le.getMetadata("mobs_data").get(0).value()).get(Mobs_param.SPAWN_REASON.toString());
					if (spawn_reason == null) return false;
					for (String s : value.split(",")) if (s.equalsIgnoreCase(spawn_reason)) return true;
				}
				break;
			case TAMED:
				if (le instanceof Wolf) return ((Wolf)le).isTamed();
				break;
			case THUNDERING: 
				w = getWorld(condition);
				return w == null ? false : w.isThundering();
			case WORLD_NAME:
				w = getWorld(condition);
				if (w == null) return false;
				for (String s : value.split(",")) if (s.equalsIgnoreCase(w.getName())) return true;
				break;
			case WORLD_TIME: 
				w = getWorld(condition);
				if (w == null) return false;
				return (condition.matchesValue((int)w.getTime()));
			case X: return (condition.matchesValue(le.getLocation().getBlockX()));
			case Y: return (condition.matchesValue(le.getLocation().getBlockY()));
			case Z: return (condition.matchesValue(le.getLocation().getBlockZ()));
						
			case NOT_ADULT:
				if (le instanceof Ageable) return !((Ageable)le).isAdult();
				break;
			case NOT_ANGRY:
				if (le instanceof Wolf) return !((Wolf)le).isAngry();
				else if (le instanceof PigZombie) return !((PigZombie)le).isAngry();
				break;
			case NOT_AREA:
				for (Area a : Mobs.getInstance().getTarget_manager().getAreas()) if (a.containsLocation(le.getLocation())) return false;
				return true;				
			case NOT_BIOME:
				for (String s : value.split(",")) if (le.getLocation().getBlock().getBiome().name().equalsIgnoreCase(s)) return false;
				return true;
			case NOT_DEATH_CAUSE:
				if (!le.isDead()) return false;
				for (String s : value.split(",")) if (s.equalsIgnoreCase(le.getLastDamageCause().toString())) return false;
				return true;
			case NOT_KILLED_BY_PLAYER:
				if (le.isDead()) return !(le.getKiller() instanceof Player);
				break;
			case NOT_NAME:
				if (le instanceof Player)
				{
					for (String s : value.split(",")) if (s.equalsIgnoreCase(((Player)le).getName())) return false;
				}
				else if (le.hasMetadata("mobs_data"))
				{
					String name = (String) ((Map<String, Object>)le.getMetadata("mobs_data").get(0).value()).get(Mobs_param.NAME.toString());
					if (name == null) return true;
					for (String s : value.split(",")) if (s.equalsIgnoreCase(name)) return false;
				}
				break;
			case NOT_POWERED:
				if (le instanceof Creeper) return !((Creeper)le).isPowered();
				break;
			case NOT_RAINING: 
				w = getWorld(condition);
				return w == null ? false : !w.hasStorm();
			case NOT_SADDLED:
				if (le instanceof Pig) return !((Pig)le).hasSaddle();
				break;
			case NOT_SHEARED:
				if (le instanceof Sheep) return !((Sheep)le).isSheared();
				break;
			case NOT_SPAWN_REASON:
				if (le.hasMetadata("mobs_data"))
				{
					String spawn_reason = (String) ((Map<String, Object>)le.getMetadata("mobs_data").get(0).value()).get(Mobs_param.SPAWN_REASON.toString());
					if (spawn_reason == null) return true;
					for (String s : value.split(",")) if (s.equalsIgnoreCase(spawn_reason)) return false;
				}
				return true;
			case NOT_TAMED:
				if (le instanceof Wolf) return !((Wolf)le).isTamed();
				break;
			case NOT_THUNDERING: 
				w = getWorld(condition);
				return w == null ? false : !w.isThundering();
			case NOT_WORLD_NAME:
				w = getWorld(condition);
				if (w == null) return false;
				for (String s : value.split(",")) if (s.equalsIgnoreCase(w.getName())) return false;
				return true;
		}
		return false;
	}
	
	public World getWorld(Condition condition)
	{
		Target t = condition.getTarget();
		if (t == null) return null;
		
		switch (t.getTarget_type())
		{
			case WORLD: return Bukkit.getWorld(t.getString_param(Mobs_param.NAME));
		}
		return null;
	}
}