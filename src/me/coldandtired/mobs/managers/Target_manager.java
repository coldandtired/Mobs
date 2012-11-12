package me.coldandtired.mobs.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.xpath.XPath;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Config_element;
import me.coldandtired.mobs.elements.Text_value;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.enums.MTarget;
import me.coldandtired.extra_events.*;
import me.coldandtired.mobs.subelements.Area;
import me.coldandtired.mobs.subelements.Nearby_mob;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.plugin.Plugin;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Target_manager 
{
	private Map<String, Area> areas = new HashMap<String, Area>();
	private static Target_manager tm;
	
	private Target_manager()
	{
		// empty for singleton
	}
	
	public static Target_manager get()
	{
		if (tm == null) tm = new Target_manager();
		return tm;
	}
	
	public Collection<Area> getAreas()
	{
		return areas.values();
	}
				
	private List<LivingEntity> getNearest(List<Entity> entities, Target t, LivingEntity le)
	{
		List<LivingEntity> targets = filter(entities, t.getMob());
		Location loc = le.getLocation();
		List<Nearby_mob> mobs = new ArrayList<Nearby_mob>();
		for (LivingEntity l : targets)
		{
			mobs.add(new Nearby_mob(l, l.getLocation().distance(loc)));
		}
		
		Collections.sort(mobs, new Comparator<Nearby_mob>() 
		{
		    public int compare(Nearby_mob m1, Nearby_mob m2)
		    {
		        return m1.getDistance().compareTo(m2.getDistance());
		    }
		});
		
		targets = new ArrayList<LivingEntity>();
		int count = t.getAmount(1);
		for (int i = 0; i < count; i++)
		{
			if (mobs.size() <= i) return targets;
			targets.add((LivingEntity)mobs.get(i).getEntity());
		}
		return targets;
	}
	
	public List<LivingEntity> filter(List<Entity> entities, String[] mob)
	{
		List<LivingEntity> targets = new ArrayList<LivingEntity>();
		for (Entity e : entities)
		{
			if (!(e instanceof LivingEntity)) continue;
			if (mob != null)
			{
				if (!e.getType().equals(EntityType.valueOf(mob[0]))) continue;
				String name = mob.length > 1 ? mob[1] : null;
				if (name != null)
				{
					String s = e instanceof Player ? ((Player)e).getName() : (String)Data.getData(e, MParam.NAME);
					if (s == null || !s.equalsIgnoreCase(name)) continue;
				}
			}
			targets.add((LivingEntity)e);
		}
		return targets;
	}
	
	public List<LivingEntity> getNearby(List<Entity> entities, Target t)
	{	
		List<LivingEntity> targets = filter(entities, t.getMob());
		
		Collections.shuffle(targets);
		int count = t.getAmount(1);
		if (count > targets.size()) count = targets.size();
		return targets.subList(0, count);
	}
	
	public List<LivingEntity> getTargets(Target t, LivingEntity le, Event orig_event)
	{
		List<LivingEntity> targets = new ArrayList<LivingEntity>();
		if (t == null)
		{
			if (le != null) targets.add(le);
			return targets;
		}

		switch (t.getTarget_type())
		{
			case AUX_MOB:
				if (orig_event instanceof EntityDamageByEntityEvent)
				{
					Entity ee = ((EntityDamageByEntityEvent)orig_event).getDamager();
					if (ee instanceof LivingEntity) targets.add((LivingEntity)ee);
				}
				else if (orig_event instanceof PlayerApproachLivingEntityEvent)
					targets.add(((PlayerApproachLivingEntityEvent)orig_event).getPlayer());
				else if (orig_event instanceof PlayerLeaveLivingEntityEvent)
					targets.add(((PlayerLeaveLivingEntityEvent)orig_event).getPlayer());
				else if (orig_event instanceof PlayerNearLivingEntityEvent)
					targets.add(((PlayerNearLivingEntityEvent)orig_event).getPlayer());
				else if (orig_event instanceof LivingEntityBlockEvent)
					targets.add(((LivingEntityBlockEvent)orig_event).getAttacker());
				else if (orig_event instanceof LivingEntityDamageEvent)
					targets.add(((LivingEntityDamageEvent)orig_event).getAttacker());
				else if (orig_event instanceof EntityTargetLivingEntityEvent)
					targets.add(((EntityTargetLivingEntityEvent)orig_event).getTarget());
				else if (orig_event instanceof EntityTameEvent)
					targets.add((LivingEntity) ((EntityTameEvent)orig_event).getOwner());
				else if (orig_event instanceof PlayerShearEntityEvent)
					targets.add(((PlayerShearEntityEvent)orig_event).getPlayer());
				else if (orig_event instanceof EntityDeathEvent)
					targets.add(((EntityDeathEvent)orig_event).getEntity().getKiller());
				else if (orig_event instanceof PlayerDeathEvent)
					targets.add(((PlayerDeathEvent)orig_event).getEntity().getKiller());
			
				break;			
			case PLAYER:
				targets.add(Bukkit.getPlayer(t.getPlayer()));
				break;
			case NEAREST:
				targets = getNearest(le.getNearbyEntities(50, 10, 50), t, le);
				break;
			case RANDOM:
				targets = getNearby(le.getWorld().getEntities(), t);
				break;
			default:
				if (le != null) targets.add(le);
				break;
		}
		return targets;
	}
	
	
	
	public List<Location> getLocations(Config_element oe, LivingEntity le, Event orig_event)
	{
		List<Location> locs = new ArrayList<Location>();
		World w = oe.getWorld(le);
		Target t = oe.getTarget();
		if (w == null  && !t.getTarget_type().equals(MTarget.AREA)) return locs;
		// no world, can't continue
		
		if (t == null)
		{
			if (le != null) locs.add(le.getLocation());
			return locs;
		}
		switch (t.getTarget_type())
		{
			case AREA:
				Area area = null;
				String s = t.getArea_name();
				if (s != null)
				{
					area = areas.get(s.toUpperCase());
					if (area == null)
					{
						Mobs.warn("No area called " + s + " found!");
						return locs;
					}
					if (s.contains(":")) locs.add(area.getLocation(null));
					else locs.add(area.getLocation(w));
					return locs;
				}
				else area = t.getArea();
				locs.add(area.getLocation(w));
				break;
			case BLOCK:
				locs.add(w.getBlockAt(Integer.parseInt(t.getX().getValue()), Integer.parseInt(t.getY().getValue()), 
						Integer.parseInt(t.getZ().getValue())).getLocation());
				break;
			case AROUND:
				List<LivingEntity> targets = getNearby(w.getEntities(), t);
				for (LivingEntity l : targets) locs.add(getOffset(t, l.getLocation()));
				break;
			default:
				for (LivingEntity l : getTargets(t, le, orig_event)) locs.add(l.getLocation());
				break;
		}
		return locs;
	}

	private Location getOffset(Target t, Location loc)
	{
		Random r = new Random();
		
		int start = loc.getBlockX();
		Text_value tv = t.getX();
		int i = tv == null ? 1 : tv.getInt_value(start);
		if (r.nextBoolean()) loc.setX(start - i); else loc.setX(start + i);	
		
		start = loc.getBlockZ();
		tv = t.getZ();
		i = tv == null ? 1 : tv.getInt_value(start);
		if (r.nextBoolean()) loc.setZ(start - i); else loc.setZ(start + i);	
		
		start = loc.getBlockY();
		tv = t.getY();
		i = tv == null ? 1 : tv.getInt_value(start);
		if (r.nextBoolean()) i = (start - i); else i = (start + i);
		Block b = loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ());
		while (b.getType() != Material.AIR && b.getY() < loc.getWorld().getMaxHeight()) b = b.getRelative(BlockFace.UP);
		i = b.getY();
		if (i > loc.getWorld().getMaxHeight()) i = loc.getWorld().getMaxHeight();
		loc.setY(i);
		return loc;
	}
	
 	public void importAreas(XPath xpath, NodeList list)
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
					Area a = new Area(wgp.getRegionManager(w).getRegion(s), world_name);
					areas.put(world_name + ":" + s.toUpperCase(), a);
					if (!areas.containsKey(s)) areas.put(s.toUpperCase(), a);
				}
			}
		}
		
		if (list.getLength() == 0) return;
		for (int i = 0; i < list.getLength(); i++)
		{
			try
			{
				for (int j = 0; j < list.getLength(); j++)
				{
					Element el = (Element)list.item(j);
					Area a = new Area(el);
					areas.put(el.getLocalName().toUpperCase(), a);
					World w = a.getWorld();
					if (w != null) areas.put(w.getName().toUpperCase() + ":" + el.getLocalName().toUpperCase(), a);
				}
			}
			catch (Exception e) {e.printStackTrace();}
		}
	}
}