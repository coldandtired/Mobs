package me.coldandtired.mobs.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.coldandtired.mobs.Mobs;
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
import org.bukkit.entity.Player;
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
	
	public Location getLocation_from_target(Target t, LivingEntity le)
	{
		World w = getWorld(t);
		if (w == null && le != null) w = le.getWorld();
		if (t != null)
		{
			switch (t.getTarget_type())
			{
				case PLAYER:
					LivingEntity p = getTarget_entity(t, le);
					Location l = p.getLocation();
					if (w.getName().equalsIgnoreCase(l.getWorld().getName())) return l;
					break;
				case BLOCK:
					int[] temp = t.getInt_array(Mobs_const.BLOCK);
					return w.getBlockAt(temp[0], temp[1], temp[2]).getLocation();
				case AREA:
					String s = t.getString_param(Mobs_const.NAME).toUpperCase();
					if (!s.contains(":")) s = w.getName().toUpperCase() + ":" + s;
					Area a = areas.get(s);
					if (a == null)
					{
						Mobs.warn("No area called " + s + " found!");
						return null;
					}
					else return a.getLocation();					
			}
		}
		return le == null ? null : le.getLocation();
	}
	
	public LivingEntity getTarget_entity(Target t, LivingEntity le)
	{
		if (t == null) return le;
		
		switch (t.getTarget_type())
		{
			case PLAYER: return Bukkit.getPlayer(t.getString_param(Mobs_const.NAME));
		}
		return null;
	}
	
	public List<Player> getPlayers(Target t, LivingEntity le)
	{
		List<Player> players = null;
		if (t == null)
		{
			players = new ArrayList<Player>();
			players.add((Player)le);
			return players;
		}
		switch (t.getTarget_type())
		{
			case PLAYER:
				LivingEntity p = Mobs.getInstance().getTarget_manager().getTarget_entity(t, le);			
				if (p == null) return null;
				players = new ArrayList<Player>();
				players.add((Player)p);
				break;
			case NEAREST_PLAYER:
				List<Nearby_mob> mobs = Mobs.getInstance().getTarget_manager().getNearest(t, le, EntityType.PLAYER);
				if (mobs.size() == 0) return null;
				players = new ArrayList<Player>();
				Mobs_number number = t.getMobs_number();
				int count = number == null ? 1 : number.getAbsolute_value(0);
				for (int i = 0; i < count; i++)
				{
					if (mobs.size() <= i) return players;
					players.add((Player)mobs.get(i).getEntity());
				}
				break;
		}
		return players;
	}
	
	private List<Nearby_mob> getNearest(Target t, LivingEntity le, EntityType type)
	{		
		List<Entity> nearby = le.getNearbyEntities(50, 10, 50);
		List<Nearby_mob> mobs = new ArrayList<Nearby_mob>();
		Location loc = le.getLocation();
		for (Entity e : nearby) if (e.getType() == type) mobs.add(new Nearby_mob(e, e.getLocation().distance(loc)));
		
		Collections.sort(mobs, new Comparator<Nearby_mob>() 
		{
		    public int compare(Nearby_mob m1, Nearby_mob m2)
		    {
		        return m1.getDistance().compareTo(m2.getDistance());
		    }
		});
		return mobs;
	}
	
	public World getWorld(Target t)
	{
		if (t == null) return null;
		switch (t.getTarget_type())
		{
			case WORLD: return Bukkit.getWorld(t.getString_param(Mobs_const.NAME));
		}
		return null;
	}
	
	/*public Location get_location_from_arear(Target t, World w)
	{
		int[] b = t.getInt_array(Mob_param.BLOCK);
		int[] r = t.getInt_array(Mob_param.RADIUS);
		int[] s = t.getInt_array(Mob_param.SAFE_RADIUS);

		int safex = s != null ? s[0] : 0;
		int safez = s != null ? s[2] : 0;	
		int safey = s!= null ? s[1] : 0;
		int max = w.getMaxHeight();
				
		List<String> temp = new ArrayList<String>();
		
		for (int x = b[0] - r[0]; x <= b[0] + r[0]; x++)
		{
			if (x <= b[0] - safex || x >= b[0] + safex)
			{
				for (int z = b[2] - r[2]; z <= b[2] + r[2]; z++)
				{
					if (z <= b[2] - safez || z >= b[2] + safez)
					{
						for (int y = b[1] - r[1]; y <= b[1] + r[1]; y++)
						{
							if ((y <= b[1] - safey || y >= b[1] + safey) && y < max)
							{								
								temp.add("" + x + ":" + y + ":" + z);
							}
						}
					}
				}
			}
		}
		if (temp.size() == 0) return null;
		String[] ss = temp.get(new Random().nextInt(temp.size())).split(":");
		return w.getBlockAt(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2])).getLocation();
	}*/
	
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

/*ExecutorService pool = Executors.newFixedThreadPool(1);
Future<Location> test = pool.submit(new Callable<Location>()
{
	public Location call()
	{
		return get_location_from_area(t, w);
	}
});
try
{
	loc = test.get();
}
catch (Exception e){e.printStackTrace();}
break;*/