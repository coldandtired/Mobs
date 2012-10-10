package me.coldandtired.mobs.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Action;
import me.coldandtired.mobs.enums.Mobs_const;
import me.coldandtired.mobs.subelements.Area;
import me.coldandtired.mobs.subelements.Mobs_number;
import me.coldandtired.mobs.subelements.Nearby_mob;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Target_manager 
{
	private Map<String, Area> areas = new HashMap<String, Area>();
	
	public Target_manager(NodeList list)
	{
		importAreas(list);
	}
	
	Collection<Area> getAreas()
	{
		return areas.values();
	}
				
	@SuppressWarnings("unchecked")
	private List<LivingEntity> getNearest(List<Entity> entities, Target t, LivingEntity le)
	{
		List<Nearby_mob> mobs = new ArrayList<Nearby_mob>();
		Location loc = le.getLocation();
		String[] mob = t.hasParam(Mobs_const.MOB) ? ((String)(t.getAlternative(Mobs_const.MOB))).split(":") : null;

		for (Entity e : entities)
		{
			if (!(e instanceof LivingEntity)) continue;
			if (mob != null)
			{
				if (!e.getType().equals(EntityType.valueOf(mob[0]))) continue;
				if (mob.length == 2)
				{
					if (!e.hasMetadata("mobs_data")) continue;
					Map<String, Object> temp = (Map<String, Object>)e.getMetadata("mobs_data").get(0).value();
						
					String s = (String)temp.get("NAME");
					if (s == null || !s.equalsIgnoreCase(mob[1])) continue;
				}
			}
			mobs.add(new Nearby_mob(e, e.getLocation().distance(loc)));
		}
		
		Collections.sort(mobs, new Comparator<Nearby_mob>() 
		{
		    public int compare(Nearby_mob m1, Nearby_mob m2)
		    {
		        return m1.getDistance().compareTo(m2.getDistance());
		    }
		});
		
		List<LivingEntity> targets = new ArrayList<LivingEntity>();
		Mobs_number number = t.getMobs_number();
		int count = number == null ? 1 : number.getAbsolute_value(1);
		for (int i = 0; i < count; i++)
		{
			if (mobs.size() <= i) return targets;
			targets.add((LivingEntity)mobs.get(i).getEntity());
		}
		return targets;
	}
	
	private List<LivingEntity> getNearby(List<Entity> entities, Target t)
	{		
		String[] mob = t.hasParam(Mobs_const.MOB) ? ((String)(t.getAlternative(Mobs_const.MOB))).split(":") : null;
		List<LivingEntity> targets = new ArrayList<LivingEntity>();
		for (Entity e : entities)
		{
			if (!(e instanceof LivingEntity)) continue;
			if (mob != null)
			{
				if (!e.getType().equals(EntityType.valueOf(mob[0]))) continue;
				if (mob.length == 2)
				{
					if (!e.hasMetadata("mobs_data")) continue;
					@SuppressWarnings("unchecked")
					Map<String, Object> temp = (Map<String, Object>)e.getMetadata("mobs_data").get(0).value();
						
					String s = (String)temp.get("NAME");
					if (s == null || !s.equalsIgnoreCase(mob[1])) continue;
				}
			}
			targets.add((LivingEntity)e);
		}
		
		Collections.shuffle(targets);
		Mobs_number number = t.getMobs_number();
		int count = number == null ? targets.size() : number.getAbsolute_value(1);
		if (count > targets.size()) count = targets.size();
		return targets.subList(0, count);
	}
	
	public World getWorld(Action a, LivingEntity le)
	{
		if (a.hasParam(Mobs_const.WORLD)) return Bukkit.getWorld(a.getString_alt(Mobs_const.WORLD));
		else if (le != null) return le.getWorld();
		return null;
	}
		
	public List<LivingEntity> getTargets(Target t, LivingEntity le)
	{
		List<LivingEntity> targets = new ArrayList<LivingEntity>();
		if (t == null) return targets;
		
		switch (t.getTarget_type())
		{
			case PLAYER:
				targets.add(Bukkit.getPlayer(t.getString(Mobs_const.NAME)));
				break;
			case NEAREST:
				targets = getNearest(le.getNearbyEntities(50, 10, 50), t, le);
				break;
			case RANDOM:
				targets = getNearby(le.getWorld().getEntities(), t);
				break;
			case SELF:
				if (le != null) targets.add(le);
				break;
		}
		return targets;
	}
	
	public List<Location> getLocations(Action a, LivingEntity le)
	{
		List<Location> locs = new ArrayList<Location>();
		Target t = (Target)a.getAlternative(Mobs_const.TARGET);
		World w = getWorld(a, le);
		if (w == null) return locs;
		// no world, can't continue
		
		if (t == null)
		{
			if (le != null) locs.add(le.getLocation());
			return locs;
		}
		switch (t.getTarget_type())
		{
			case AREA:
				String s = t.getString(Mobs_const.NAME).toUpperCase();
				if (!s.contains(":")) s = w.getName().toUpperCase() + ":" + s;
				Area area = areas.get(s);
				if (area == null)
				{
					Mobs.warn("No area called " + s + " found!");
					return null;
				}
				else locs.add(area.getLocation());
			case BLOCK:
				int[] temp = t.getInt_array(Mobs_const.BLOCK);
				locs.add(w.getBlockAt(temp[0], temp[1], temp[2]).getLocation());
				break;
			case NEAR:
				List<LivingEntity> targets = getNearby(w.getEntities(), t);
				for (LivingEntity l : targets) locs.add(getOffset(t, l.getLocation()));
				break;
			default:
				for (LivingEntity l : getTargets(t, le)) locs.add(l.getLocation());
				break;
		}
		return locs;
	}

	private Location getOffset(Target t, Location loc)
	{
		Random r = new Random();
		int start = loc.getBlockX();
		int safe_radius = t.getInt(Mobs_const.X_SAFE_RADIUS, 0);
		if (!t.hasParam(Mobs_const.X_RADIUS))
		{
			if (safe_radius == 0) safe_radius = 1;
			if (r.nextBoolean()) loc.setX(start - safe_radius); else loc.setX(start + safe_radius);
		}
		else
		{
			int radius = t.getInt(Mobs_const.X_RADIUS, 1);
			List<Integer> choices = new ArrayList<Integer>();
			for (int i = start - radius; i <= start + radius; i++)
			{
				if (i < start - safe_radius || i > start + safe_radius) choices.add(i);
			}
			loc.setX(choices.get(r.nextInt(choices.size())));
		}
		
		start = loc.getBlockZ();
		safe_radius = t.getInt(Mobs_const.Z_SAFE_RADIUS, 0);
		if (!t.hasParam(Mobs_const.Z_RADIUS))
		{
			if (safe_radius == 0) safe_radius = 1;
			if (r.nextBoolean()) loc.setZ(start - safe_radius); else loc.setZ(start + safe_radius);
		}
		else
		{
			int radius = t.getInt(Mobs_const.Z_RADIUS, 1);
			List<Integer> choices = new ArrayList<Integer>();
			for (int i = start - radius; i <= start + radius; i++)
			{
				if (i < start - safe_radius || i > start + safe_radius) choices.add(i);
			}
			loc.setZ(choices.get(r.nextInt(choices.size())));
		}
		
		start = loc.getBlockY() + 1;
		safe_radius = t.getInt(Mobs_const.Y_SAFE_RADIUS, 0);
		int max = loc.getWorld().getMaxHeight();
		if (!t.hasParam(Mobs_const.Y_RADIUS))
		{
			if (safe_radius == 0) safe_radius = 1;
			int y = r.nextBoolean() ? start - safe_radius : start + safe_radius;
			if (y > max) loc.setY(max); else loc.setY(y);
		}
		else
		{
			int radius = t.getInt(Mobs_const.Y_RADIUS, 1);
			List<Integer> choices = new ArrayList<Integer>();
			for (int i = start - radius; i <= start + radius; i++)
			{
				if (i < start - safe_radius || i > start + safe_radius) choices.add(i);
			}
			int y = choices.get(r.nextInt(choices.size()));
			if (y > max) loc.setY(max); else loc.setY(y);
		}
		return loc;
	}
	
 	private void importAreas(NodeList list)
	{
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (p != null && p instanceof WorldGuardPlugin)
		{
			WorldGuardPlugin wgp = (WorldGuardPlugin)p;
			
			for (World w : Bukkit.getWorlds())
			{
				String world_name = w.getName().toUpperCase();
				for (String s : wgp.getRegionManager(w).getRegions().keySet())
				{
					areas.put(world_name + ":" + s.toUpperCase(), new Area(wgp.getRegionManager(w).getRegion(s), world_name));
				}
			}
		}
		
		if (list.getLength() == 0) return;
		for (int i = 0; i < list.getLength(); i++)
		{
			Element el = (Element)list.item(i);
			String world_name = el.getAttribute("world").toUpperCase();
			areas.put(world_name + ":" + el.getLocalName().toUpperCase(), new Area(el, world_name));
		}
	}
}