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
	private List<String> blocks = new ArrayList<String>();
	private String world;
	private int x_start;
	private int x_end;
	private int y_start;
	private int y_end;
	private int z_start;
	private int z_end;
	
	public Area(Element el, String world_name)
	{
		world = world_name;	
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
		
		for (int x = x_start; x <= x_end; x++)
		{
			if (x <= x_start - x_safe || x >= x_start + x_safe)
			{
				for (int z = z_start; z <= z_end; z++)
				{
					if (z <= z_start - z_safe || z >= z_start + z_safe)
					{
						for (int y = y_start; y <= y_end; y++)
						{
							if ((y <= y_start - y_safe || y >= y_start + y_safe) && y < max) blocks.add("" + x + ":" + y + ":" + z);						
						}					
					}
				}
			}
		}		
	}
	
	public Area(ProtectedRegion pr, String world_name)
	{
		world = world_name;
		BlockVector bv = pr.getMinimumPoint();
		x_start = bv.getBlockX();
		y_start = bv.getBlockY();
		z_start = bv.getBlockZ();
		bv = pr.getMaximumPoint();
		x_end = bv.getBlockX();
		y_end = bv.getBlockY();
		z_end = bv.getBlockZ();
		int max_height = Bukkit.getWorld(world).getMaxHeight();
		
		for (int x = x_start; x <= x_end; x++)
		{
			for (int z = z_start; z <= z_end; z++)
			{
				for (int y = y_start; y <= y_end; y++)
				{
					if (y < max_height) blocks.add("" + x + ":" + y + ":" + z);
				}
			}
		}
	}
	
	public Location getLocation( )
	{
		String[] ss = blocks.get(new Random().nextInt(blocks.size())).split(":");
		return Bukkit.getWorld(world).getBlockAt(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2])).getLocation();
	}
	
	public boolean containsLocation(Location loc)
	{
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		return x >= x_start && x <= x_end && y >= y_start && y <= y_end && z >= z_start && z <= z_end;
	}
}