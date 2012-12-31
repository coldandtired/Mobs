
package me.coldandtired.mobs.subelements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.extra_events.LivingEntityBlockEvent;
import me.coldandtired.extra_events.LivingEntityDamageEvent;
import me.coldandtired.extra_events.PlayerApproachLivingEntityEvent;
import me.coldandtired.extra_events.PlayerLeaveLivingEntityEvent;
import me.coldandtired.extra_events.PlayerNearLivingEntityEvent;
import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Alternatives;
import me.coldandtired.mobs.elements.Config_element;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.enums.MTarget;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
	
	public Target(Config_element parent) throws XPathExpressionException
	{
		super(null, parent);
		target_type = MTarget.SELF;
	}
	
	public Target(Element element, Config_element parent) throws XPathExpressionException
	{	
		super(element, parent);
		target_type = MTarget.valueOf(element.getAttribute("type").toUpperCase());
		
		/*Element el;
		switch (target_type)
		{
			//case PLAYER:
			//	player = new Text_value(element);
			//	break;
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
		if (el != null) amount = new Text_value(el);*/
		
		//el = (Element)Mobs.getXPath().evaluate("mob", element, XPathConstants.NODE);
		//if (el != null) mob = new Text_value(el);		
	}
	
	public MTarget getTarget_type()
	{
		return target_type;
	}
		
	public String[] getMob()
	{
		if (mobs == null) return null;
		return ((String)((Alternatives)mobs).getAlternative()).toUpperCase().split(":");
		//return mob.getValue().toUpperCase().split(":");
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
			case BLOCK: return new Location(getWorld(le), getX().get(0), getY().get(0), getZ().get(0));
			case PLAYER: return Bukkit.getPlayer(getPlayer().get(0));
			case NEAREST:
				List<LivingEntity> list = getNearest(le.getNearbyEntities(50, 10, 50), le);
				if (list.size() > 0) return list.get(0); else return le;
			case RANDOM:
				list =  getNearby(le.getWorld().getEntities());
				if (list.size() > 0) return list.get(0); else return le;
			default: return le;
		}
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
		Integer count = getAmount();
		if (count == null) count = 1;
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
		Integer count = getAmount();
		if (count == null) count = 1;
		if (count > targets.size()) count = targets.size();
		return targets.subList(0, count);
	}
	
	/*public List<Location> getLocations(LivingEntity le, Event orig_event)
	{
		List<Location> locs = new ArrayList<Location>();
		World w = getWorld(le);                  
		if (w == null  && !target_type.equals(MTarget.AREA)) return locs;
		// no world, can't continue

		switch (target_type)
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
				locs.add(w.getBlockAt(getX(), getY(), getZ()).getLocation());
				break;
			case AROUND:
				List<LivingEntity> targets = getNearby(w.getEntities());
				for (LivingEntity l : targets) locs.add(getOffset(l.getLocation()));
				break;
			default:
				//for (LivingEntity l : getTargets(le, orig_event)) locs.add(l.getLocation());
				break;
		}
		return locs;
	}*/
	
	/*private Location getOffset(Location loc)
	{
		Random r = new Random();
		
		int start = loc.getBlockX();
		int i = xs == null ? 1 : getX();
		if (r.nextBoolean()) loc.setX(start - i); else loc.setX(start + i);	
		
		start = loc.getBlockZ();
		i = zs == null ? 1 : getZ();
		if (r.nextBoolean()) loc.setZ(start - i); else loc.setZ(start + i);	
		
		start = loc.getBlockY();
		i = ys == null ? 1 : getY();
		if (r.nextBoolean()) i = (start - i); else i = (start + i);
		Block b = loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ());
		while (b.getType() != Material.AIR && b.getY() < loc.getWorld().getMaxHeight()) b = b.getRelative(BlockFace.UP);
		i = b.getY();
		if (i > loc.getWorld().getMaxHeight()) i = loc.getWorld().getMaxHeight();
		loc.setY(i);
		return loc;
	}*/
	
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
				for (String s : getPlayer())
					targets.add(Bukkit.getPlayer(s));
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
				break;
			default:
				if (le != null) targets.add(le);
				break;
		}
		return targets;
	}
}