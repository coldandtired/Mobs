package me.coldandtired.mobs.subelements;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.w3c.dom.Element;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Area 
{
	private List<Integer> xs = new ArrayList<Integer>();
	private List<Integer> ys = new ArrayList<Integer>();
	private List<Integer> zs = new ArrayList<Integer>();
	private String world;
	
	public Area(Element el, String world_name)
	{
		world = world_name;	
		int x_start;
		int x_end;
		int y_start;
		int y_end;
		int z_start;
		int z_end;
		int x_safe = 0;
		int y_safe = 0;
		int z_safe = 0;
		if (el.hasAttribute("from_x"))
		{
			x_start = Integer.parseInt(el.getAttribute("from_x"));
			x_end = Integer.parseInt(el.getAttribute("to_x"));
			y_start = Integer.parseInt(el.getAttribute("from_y"));
			y_end = Integer.parseInt(el.getAttribute("to_y"));
			z_start = Integer.parseInt(el.getAttribute("from_z"));
			z_end = Integer.parseInt(el.getAttribute("to_z"));
		}
		else
		{
			int temp = Integer.parseInt(el.getAttribute("radius_x"));
			x_start = Integer.parseInt(el.getAttribute("mid_x")) - temp;
			x_end = x_start + temp;
			if (el.hasAttribute("safe_radius_x")) x_safe = Integer.parseInt(el.getAttribute("safe_radius_x"));
			temp = Integer.parseInt(el.getAttribute("radius_y"));
			y_start = Integer.parseInt(el.getAttribute("mid_y")) - temp;
			y_end = y_start + temp;
			if (el.hasAttribute("safe_radius_y")) y_safe = Integer.parseInt(el.getAttribute("safe_radius_y"));
			temp = Integer.parseInt(el.getAttribute("radius_z"));
			z_start = Integer.parseInt(el.getAttribute("mid_z")) - temp;
			z_end = z_start + temp;
			if (el.hasAttribute("safe_radius_z")) z_safe = Integer.parseInt(el.getAttribute("safe_radius_z"));
		}
		
		int max = Bukkit.getWorld(world).getMaxHeight();
		
		for (int i = x_start; i <= x_end; i++) if (i <= x_start - x_safe || i >= x_start + x_safe) xs.add(i);
		for (int i = z_start; i <= z_end; i++) if (i <= z_start - z_safe || i >= z_start + z_safe) zs.add(i);
		for (int i = y_start; i <= y_end; i++) if ((i <= y_start - y_safe || i >= y_start + y_safe) && i <= max) ys.add(i);					
	}
	
	public Area(ProtectedRegion pr, String world_name)
	{
		world = world_name;
		BlockVector min = pr.getMinimumPoint();
		BlockVector max = pr.getMaximumPoint();
		int max_height = Bukkit.getWorld(world).getMaxHeight();
		
		for (int i = min.getBlockX(); i <= max.getBlockX(); i++) xs.add(i);
		for (int i = min.getBlockZ(); i <= max.getBlockZ(); i++) zs.add(i);
		for (int i = min.getBlockY(); i <= max.getBlockY(); i++) if (i <= max_height) ys.add(i);
	}
	
	public Location getLocation( )
	{
		Random r = new Random();
		int x = xs.get(r.nextInt(xs.size()));
		int y = ys.get(r.nextInt(ys.size()));
		int z = zs.get(r.nextInt(zs.size()));
		return Bukkit.getWorld(world).getBlockAt(x, y, z).getLocation();
	}
	
	public boolean containsLocation(Location loc)
	{
		return xs.contains(loc.getBlockX()) && ys.contains(loc.getBlockY()) && zs.contains(loc.getBlockZ());
	}
}