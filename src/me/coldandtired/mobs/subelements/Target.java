
package me.coldandtired.mobs.subelements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.extra_events.Area;
import me.coldandtired.extra_events.LivingEntityBlockEvent;
import me.coldandtired.extra_events.LivingEntityDamageEvent;
import me.coldandtired.extra_events.PlayerApproachLivingEntityEvent;
import me.coldandtired.extra_events.PlayerLeaveLivingEntityEvent;
import me.coldandtired.extra_events.PlayerNearLivingEntityEvent;
import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Config_element;
import me.coldandtired.mobs.elements.Text_value;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.enums.MTarget;

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
import org.w3c.dom.Element;

public class Target extends Config_element
{
	private MTarget target_type;
	private Text_value player;
	private Text_value area_name;
	private Text_value x;
	private Text_value y;
	private Text_value z;
	private Text_value mob;
	private Text_value amount;
	
	public Target(Element element, Config_element parent) throws XPathExpressionException
	{	
		super(element, parent);
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
				if (element.getChildNodes().getLength() == 1) area_name = new Text_value(element);
				break;				
		}
		
		el = (Element)Mobs.getXPath().evaluate("amount", element, XPathConstants.NODE);
		if (el != null) amount = new Text_value(el);
		
		el = (Element)Mobs.getXPath().evaluate("mob", element, XPathConstants.NODE);
		if (el != null) mob = new Text_value(el);		
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
	
	public Text_value getX()
	{
		return x;
	}
	
	public Text_value getY()
	{
		return y;
	}
	
	public Text_value getZ()
	{
		return z;
	}
	
	public String getPlayer()
	{
		if (player == null) return null;
		return player.getValue();
	}
		
	public String[] getMob()
	{
		if (mob == null) return null;
		return mob.getValue().toUpperCase().split(":");
	}
	
	public List<Object> getTargeted_objects(LivingEntity le, Event orig_event)
	{
		List<Object> targets = new ArrayList<Object>();

		switch (target_type)
		{
			case AUX_MOB:
				if (orig_event instanceof EntityDamageByEntityEvent)
				{
					Entity ee = ((EntityDamageByEntityEvent)orig_event).getDamager();
					if (ee instanceof LivingEntity) targets.add(ee);
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
				targets.add(Bukkit.getPlayer(getPlayer()));
				/*for (String s : getPlayer())
					targets.add(Bukkit.getPlayer(s));*/
				break;
			case NEAREST:
				for (LivingEntity l : getNearest(le.getNearbyEntities(50, 10, 50), le))
					targets.add(l);
				break;
			case RANDOM:
				for (LivingEntity l : getNearby(le.getWorld().getEntities()))
					targets.add(l);
				break;
			case BLOCK:
				World w = getWorld(le);
				targets.add(new Location(w, getX().getInt_value(0), getY().getInt_value(0), getZ().getInt_value(0)));
				/*
				for (int x : getX())
				{
					for (int y : getY())
					{
						if (y > w.getMaxHeight()) y = w.getMaxHeight();
						for (int z : getZ())
						{
							targets.add(new Location(w, x, y, z));
						}
					}
				}
				*/
				break;
			default:
				if (le != null) targets.add(le);
				break;
		}
		return targets;
	}
	
	private List<LivingEntity> getNearest(List<Entity> entities, LivingEntity le)
	{
		List<LivingEntity> targets = filter(entities, getMob());
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
		int count = getAmount(1);
		for (int i = 0; i < count; i++)
		{
			if (mobs.size() <= i) return targets;
			targets.add((LivingEntity)mobs.get(i).getEntity());
		}
		return targets;
	}
	
	public static List<LivingEntity> filter(List<Entity> entities, String[] mob)
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
	
	public List<LivingEntity> getNearby(List<Entity> entities)
	{	
		List<LivingEntity> targets = filter(entities, getMob());
		
		Collections.shuffle(targets);
		int count = getAmount(1);
		if (count > targets.size()) count = targets.size();
		return targets.subList(0, count);
	}
	
	public List<LivingEntity> getTargets(LivingEntity le, Event orig_event)
	{
		List<LivingEntity> targets = new ArrayList<LivingEntity>();

		switch (getTarget_type())
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
				targets.add(Bukkit.getPlayer(getPlayer()));
				break;
			case NEAREST:
				targets = getNearest(le.getNearbyEntities(50, 10, 50), le);
				break;
			case RANDOM:
				targets = getNearby(le.getWorld().getEntities());
				break;
			default:
				if (le != null) targets.add(le);
				break;
		}
		return targets;
	}	
	
	public Object getSingle_target(LivingEntity le, Event orig_event)
	{
		switch (target_type)
		{
			case AUX_MOB:
				if (orig_event instanceof EntityDamageByEntityEvent)
				{
					Entity ee = ((EntityDamageByEntityEvent)orig_event).getDamager();
					if (ee instanceof LivingEntity) return ee;
				}
				else if (orig_event instanceof PlayerApproachLivingEntityEvent)
					return ((PlayerApproachLivingEntityEvent)orig_event).getPlayer();
				else if (orig_event instanceof PlayerLeaveLivingEntityEvent)
					return ((PlayerLeaveLivingEntityEvent)orig_event).getPlayer();
				else if (orig_event instanceof PlayerNearLivingEntityEvent)
					return ((PlayerNearLivingEntityEvent)orig_event).getPlayer();
				else if (orig_event instanceof LivingEntityBlockEvent)
					return ((LivingEntityBlockEvent)orig_event).getAttacker();
				else if (orig_event instanceof LivingEntityDamageEvent)
					return ((LivingEntityDamageEvent)orig_event).getAttacker();
				else if (orig_event instanceof EntityTargetLivingEntityEvent)
					return ((EntityTargetLivingEntityEvent)orig_event).getTarget();
				else if (orig_event instanceof EntityTameEvent)
					return (LivingEntity) ((EntityTameEvent)orig_event).getOwner();
				else if (orig_event instanceof PlayerShearEntityEvent)
					return ((PlayerShearEntityEvent)orig_event).getPlayer();
				else if (orig_event instanceof EntityDeathEvent)
					return ((EntityDeathEvent)orig_event).getEntity().getKiller();
				else if (orig_event instanceof PlayerDeathEvent)
					return ((PlayerDeathEvent)orig_event).getEntity().getKiller();
			case BLOCK: return new Location(getWorld(le), getX().getInt_value(0), getY().getInt_value(0), getZ().getInt_value(0));
			case PLAYER: return Bukkit.getPlayer(getPlayer());
			case NEAREST:
				List<LivingEntity> list = getNearest(le.getNearbyEntities(50, 10, 50), le);
				if (list.size() > 0) return list.get(0); else return le;
			case RANDOM:
				list =  getNearby(le.getWorld().getEntities());
				if (list.size() > 0) return list.get(0); else return le;
			default: return le;
		}
	}
	
	public List<Location> getLocations(Config_element oe, LivingEntity le, Event orig_event)
	{
		List<Location> locs = new ArrayList<Location>();
		World w = oe.getWorld(le);
		if (w == null  && !getTarget_type().equals(MTarget.AREA)) return locs;
		// no world, can't continue
		
		switch (getTarget_type())
		{
			case AREA:
				Area area = null;
				String s = getArea_name();
				if (s != null)
				{
					String s2 = s.contains(":") ? "" : le.getWorld().getName() + ":";
					s2 += s;
					area = Mobs.extra_events.getArea(s2);
					if (area == null)
					{
						Mobs.warn("No area called " + s2 + " found!");
						return locs;
					}
					locs.add(area.getRandom_location());
					return locs;
				}
				break;
			case BLOCK:
				locs.add(w.getBlockAt(Integer.parseInt(getX().getValue()), Integer.parseInt(getY().getValue()), 
						Integer.parseInt(getZ().getValue())).getLocation());
				break;
			case AROUND:
				List<LivingEntity> targets = getNearby(w.getEntities());
				for (LivingEntity l : targets) locs.add(getOffset(this, l.getLocation()));
				break;
			default:
				for (LivingEntity l : getTargets(le, orig_event)) locs.add(l.getLocation());
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
	
	public int getAmount(int orig)
	{
		if (amount == null) return orig;
		return amount.getInt_value(orig);			
	}
}