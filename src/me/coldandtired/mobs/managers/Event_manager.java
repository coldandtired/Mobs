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
	
	private Event_manager()
	{
		// private for singleton
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
	
	boolean check_condition(Condition c, LivingEntity le, Event event)
	{
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
				return matchesString(c, le.getLocation().getBlock().getBiome().name());
			case CHUNK_MOB_COUNT:
				List<Location> list = Target_manager.get().getLocations(c.getTarget(), le);
				for (Location loc : list)
				{
					int i = Target_manager.get().filter(Arrays.asList(loc.getChunk().getEntities()), c.getMob()).size();
					return c.matches(i);
				}
				break;
			case DEATH_CAUSE:
				if (!le.isDead()) return false;
				return matchesString(c, le.getLastDamageCause().toString());
			case KILLED_BY_PLAYER:
				if (le.isDead()) return le.getKiller() instanceof Player;
				break;
			case LIGHT_LEVEL: return c.matches(le.getLocation().getBlock().getLightLevel());
			case NAME:
				if (le instanceof Player)
				{
					return matchesString(c, ((Player)le).getName());
				}
				else if (le.hasMetadata("mobs_data"))
				{
					String name = (String)Data.getData(le, MParam.NAME);
					if (name == null) return false;
					return matchesString(c, name);
				}
				break;
			case OWNER:
				if (le instanceof Tameable)
				{
					AnimalTamer tamer = ((Tameable)le).getOwner();
					if (tamer != null) return matchesString(c, tamer.getName());
				}
				break;
			case RAINING:
				World w = c.getWorld(le);
				return w.hasStorm();
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
					String spawn_reason = (String)Data.getData(le, MParam.SPAWN_REASON);
					if (spawn_reason == null) return false;
					return matchesString(c, spawn_reason);
				}
				break;
			case TAMED:
				if (le instanceof Wolf) return ((Wolf)le).isTamed();
				break;
			case THUNDERING: 
				w = c.getWorld(le);
				return w.isThundering();
			case WORLD_NAME:
				w = c.getWorld(le);
				return matchesString(c, w.getName());
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
				return !matchesString(c, le.getLocation().getBlock().getBiome().name());
			case NOT_DEATH_CAUSE:
				if (!le.isDead()) return false;
				return !matchesString(c, le.getLastDamageCause().toString());
			case NOT_KILLED_BY_PLAYER:
				if (le.isDead()) return !(le.getKiller() instanceof Player);
				break;
			case NOT_NAME:
				if (le instanceof Player)
				{
					return !matchesString(c, ((Player)le).getName());
				}
				else if (le.hasMetadata("mobs_data"))
				{
					String name = (String)Data.getData(le, MParam.NAME);
					if (name == null) return true;
					return !matchesString(c, name);
				}
				break;
			case NOT_OWNER:
				if (le instanceof Tameable)
				{
					AnimalTamer tamer = ((Tameable)le).getOwner();
					if (tamer != null) return !matchesString(c, tamer.getName());
				}
				return true;
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
				if (le.hasMetadata("mobs_data"))
				{
					String spawn_reason = (String)Data.getData(le, MParam.SPAWN_REASON);
					if (spawn_reason == null) return true;
					return !matchesString(c, spawn_reason);
				}
				return true;
			case NOT_TAMED:
				if (le instanceof Wolf) return !((Wolf)le).isTamed();
				break;
			case NOT_THUNDERING: 
				w = c.getWorld(le);
				return !w.isThundering();
			case NOT_WORLD_NAME:
				w = c.getWorld(le);
				return !matchesString(c, w.getName());
		}
		return false;
	}
	
	/*public boolean matchesInt(Condition c, int orig)
	{
		List<Integer> temp = new ArrayList<Integer>();
		for (String s : pm.getString(c, MParam.VALUE).split(","))
		{
			s = s.replace(" ", "");
			if (s.startsWith("above"))
			{
				s = s.replaceAll("above", "");
				if (orig > Integer.parseInt(s)) return true;
			}
			else if (s.startsWith("below"))
			{
				s = s.replaceAll("below", "");
				if (orig < Integer.parseInt(s)) return true;
			}
			else if (s.contains("to"))
			{
				String[] temp2 = s.split("to");
				int low = Math.min(Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]));
				int high = Math.max(Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]));
				for (int i = low; i <= high; i++) temp.add(i);
			}
			else temp.add(Integer.parseInt(s));
		}
		return temp.contains(orig);
	}*/

	public boolean matchesString(Condition c, String orig)
	{
		//String name = pm.getString(c, MParam.VALUE);
		//for (String s : name.split(",")) if (s.trim().equalsIgnoreCase(orig)) return true;
		return false;
	}
}