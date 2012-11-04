package me.coldandtired.mobs.managers;

import java.util.Arrays;
import java.util.List;
import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Condition;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;
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

public class Event_manager 
{
	private static Event_manager em;
	private Target_manager tm;
	
	private Event_manager()
	{
		// private for singleton
		tm = Target_manager.get();
	}
	
	public static Event_manager get()
	{
		if (em == null) em = new Event_manager();
		return em;
	}
	
	public void start_actions(List<Outcome> outcomes, MEvent event, LivingEntity le, Event orig_event, boolean single_outcome, String name)
	{		
		Mobs.debug("------------------");
		Mobs.debug("Event - " + event.toString());
		Mobs.debug("Outcomes - " + outcomes.size());
		for (Outcome o : outcomes)
		{
			if (name != null && !o.checkName(name)) continue;
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
				boolean b = Action_manager.get().performActions(o, event, le, orig_event);
				if (!b) single_outcome = false;
				if (single_outcome) return;
			}
		}
		Mobs.debug("No matching conditions!");
		Mobs.debug("------------------");
	}	
	
	boolean check_condition(Condition c, LivingEntity live, Event event)
	{		
		for (LivingEntity le : tm.getTargets(c.getTarget(), live))
		switch (c.getCondition_type())
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
				return c.matches(le.getLocation().getBlock().getBiome().name());
			case BLOCK_LIGHT_LEVEL: return c.matches(le.getLocation().getBlock().getLightFromBlocks());
			case CHUNK_MOB_COUNT:
				List<Location> list = Target_manager.get().getLocations(c.getTarget(), le);
				for (Location loc : list)
				{
					int i = Target_manager.get().filter(Arrays.asList(loc.getChunk().getEntities()), c.getMob()).size();
					return c.matches(i);
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
				return Data.hasData(le, MParam.valueOf(c.getCondition_type().toString()));	
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
				String s = (String)Data.getData(le, MParam.valueOf(c.getCondition_type().toString()));
				if (s != null) return c.matches(Integer.parseInt(s));
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
				s = (String)Data.getData(le, MParam.valueOf(c.getCondition_type().toString()));
				if (s != null) return c.matches(s);
				break;
			case DEATH_CAUSE:
				if (!le.isDead()) return false;
				return c.matches(le.getLastDamageCause().toString());			
			case KILLED_BY_PLAYER:
				if (le.isDead()) return le.getKiller() instanceof Player;
				break;
			case LIGHT_LEVEL: return c.matches(le.getLocation().getBlock().getLightLevel());
			case LUNAR_PHASE: 
				World w = c.getWorld(le);
				long days = w.getFullTime()/24000;
				long phase= days % 8;
				return c.matches((int)phase);
			case NAME:
				if (le instanceof Player)
				{
					return c.matches(((Player)le).getName());
				}
				else if (le.hasMetadata("mobs_data"))
				{
					String name = (String)Data.getData(le, MParam.NAME);
					if (name == null) return false;
					return c.matches(name);
				}
				break;
			case OWNER:
				if (le instanceof Tameable)
				{
					AnimalTamer tamer = ((Tameable)le).getOwner();
					if (tamer != null) return c.matches(tamer.getName());
				}
				break;
			case PLAYER_HAS_PERMISSION:
				if (!(le instanceof Player)) return false;
				for (String ss : c.getValue().split(","))
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
				w = c.getWorld(le);
				return w.hasStorm();
			case SADDLED:
				if (le instanceof Pig) return ((Pig)le).hasSaddle();
				break;
			case SHEARED:
				if (le instanceof Sheep) return ((Sheep)le).isSheared();
				break;
			case SKY_LIGHT_LEVEL: return c.matches(le.getLocation().getBlock().getLightFromSky());
			case SPAWN_REASON:
				String spawn_reason = (String)Data.getData(le, MParam.SPAWN_REASON);
				if (spawn_reason == null) return false;
				return c.matches(spawn_reason);
			case TAMED:
				if (le instanceof Wolf) return ((Wolf)le).isTamed();
				break;
			case THUNDERING: 
				w = c.getWorld(le);
				return w.isThundering();
			case WORLD_MOB_COUNT:
				list = Target_manager.get().getLocations(c.getTarget(), le);
				for (Location loc : list)
				{
					int i = Target_manager.get().filter(loc.getWorld().getEntities(), c.getMob()).size();
					return c.matches(i);
				}
				break;
			case WORLD_NAME:
				w = c.getWorld(le);
				return c.matches(w.getName());
			case WORLD_TIME: 
				w = c.getWorld(le);
				return c.matches((int)w.getTime());
			case X: return c.matches(le.getLocation().getBlockX());
			case Y: return c.matches(le.getLocation().getBlockY());
			case Z: return c.matches(le.getLocation().getBlockZ());
						
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
				return !c.matches(le.getLocation().getBlock().getBiome().name());
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
				return !Data.hasData(le, MParam.valueOf(c.getCondition_type().toString().substring(4)));
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
				s = (String)Data.getData(le, MParam.valueOf(c.getCondition_type().toString().substring(4)));
				if (s != null) return !c.matches(s);
				break;	
			case NOT_DEATH_CAUSE:
				if (!le.isDead()) return false;
				return !c.matches(le.getLastDamageCause().toString());
			case NOT_KILLED_BY_PLAYER:
				if (le.isDead()) return !(le.getKiller() instanceof Player);
				break;
			case NOT_NAME:
				if (le instanceof Player)
				{
					return !c.matches(((Player)le).getName());
				}
				else if (le.hasMetadata("mobs_data"))
				{
					String name = (String)Data.getData(le, MParam.NAME);
					if (name == null) return true;
					return !c.matches(name);
				}
				break;
			case NOT_OWNER:
				if (le instanceof Tameable)
				{
					AnimalTamer tamer = ((Tameable)le).getOwner();
					if (tamer != null) return !c.matches(tamer.getName());
				}
				return true;
			case NOT_PLAYER_HAS_PERMISSION:
				if (!(le instanceof Player)) return true;
				for (String ss : c.getValue().split(","))
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
				w = c.getWorld(le);
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
				return !c.matches(spawn_reason);
			case NOT_TAMED:
				if (le instanceof Wolf) return !((Wolf)le).isTamed();
				break;
			case NOT_THUNDERING: 
				w = c.getWorld(le);
				return !w.isThundering();
			case NOT_WORLD_NAME:
				w = c.getWorld(le);
				return !c.matches(w.getName());
		}
		return false;
	}
}