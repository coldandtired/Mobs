package me.coldandtired.mobs.subelements;

import java.util.Random;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Mobs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Area 
{
	private int x_start;
	private int x_length;
	private int x_offset = 0;
	private int y_start;
	private int y_length;
	private int y_offset = 0;
	private int z_start;
	private int z_length;
	private int z_offset = 0;
	private String world;
	
	public Area(Element element) throws DOMException, XPathExpressionException
	{
		Element el = (Element)Mobs.getXPath().evaluate("world", element, XPathConstants.NODE);
		if (el != null) world = el.getTextContent();
		String[] temp = ((Element)Mobs.getXPath().evaluate("x", element, XPathConstants.NODE)).getTextContent().split(":");
		if (temp.length == 2)
		{
			x_start = Integer.parseInt(temp[0]);
			x_length = Integer.parseInt(temp[1]) + 1;
			
			temp = ((Element)Mobs.getXPath().evaluate("y", element, XPathConstants.NODE)).getTextContent().split(":");
			y_start = Integer.parseInt(temp[0]);
			y_length = Integer.parseInt(temp[1]) + 1;
			
			temp = ((Element)Mobs.getXPath().evaluate("z", element, XPathConstants.NODE)).getTextContent().split(":");
			z_start = Integer.parseInt(temp[0]);
			z_length = Integer.parseInt(temp[1]) + 1;
		}
		else
		{
			//mid, radius, safe
			int i = Integer.parseInt(temp[1]);
			x_start = Integer.parseInt(temp[0]) - i;			
			x_offset = (Integer.parseInt(temp[2]) * 2) + 1;
			x_length = (i * 2) - x_offset + 1;

			temp = ((Element)Mobs.getXPath().evaluate("y", element, XPathConstants.NODE)).getTextContent().split(":");
			i = Integer.parseInt(temp[1]);
			y_start = Integer.parseInt(temp[0]) - i;			
			y_offset = (Integer.parseInt(temp[2]) * 2) + 1;
			y_length = (i * 2) - y_offset + 1;

			temp = ((Element)Mobs.getXPath().evaluate("z", element, XPathConstants.NODE)).getTextContent().split(":");
			i = Integer.parseInt(temp[1]);
			z_start = Integer.parseInt(temp[0]) - i;			
			z_offset = (Integer.parseInt(temp[2]) * 2) + 1;
			z_length = (i * 2) - z_offset + 1;
		}	
}
	
	public Area(ProtectedRegion pr, String world)
	{
		this.world = world;
		BlockVector bv = pr.getMinimumPoint();
		x_start = bv.getBlockX();
		y_start = bv.getBlockY();
		z_start = bv.getBlockZ();
		
		bv = pr.getMaximumPoint();
		x_length = bv.getBlockX() - x_start + 1;
		y_length = bv.getBlockY() - y_start + 1;
		z_length = bv.getBlockZ() - z_start + 1;
	}
	
	public World getWorld()
	{
		if (world == null) return null;
		return Bukkit.getWorld(world);
	}
	
	public Location getLocation(World world)
	{
		Random r = new Random();
		World w = world == null ? getWorld() : world;
		int x = 0;
		if (x_length > 0)
		{
			x = r.nextInt(x_length);
			if (x_offset > 0 && x >= (x_length / 2)) x += x_offset;
		}
		x += x_start;
		
		int y = 0;
		if (y_length > 0)
		{
			y = r.nextInt(y_length);
			if (y_offset > 0 && y >= (y_length / 2)) y += y_offset;
		}
		y += y_start;
		if (y > w.getMaxHeight()) y = w.getMaxHeight();
		
		int z = 0;
		if (z_length > 0)
		{
			z = r.nextInt(z_length);
			if (z_offset > 0 && z >= (z_length / 2)) z += z_offset;
		}
		z += z_start;
		return w.getBlockAt(x, y, z).getLocation();
	}
	
	public boolean containsLocation(Location loc)
	{
		int i = loc.getBlockX();
		if (i < x_start || i >= x_start + x_offset + x_length) return false;
		// outside external limit
		if (x_offset > 0)
		{
			int temp = (x_offset - 1) / 2;
			if (i > x_start + temp || i <= x_start + temp + x_offset) return false;
			//inside safe area
		}

		i = loc.getBlockY();
		if (i < y_start || i >= y_start + y_offset + y_length) return false;
		// outside external limit
		if (y_offset > 0)
		{
			int temp = (y_offset - 1) / 2;
			if (i > y_start + temp || i <= y_start + temp + y_offset) return false;
			//inside safe area
		}
		
		i = loc.getBlockZ();
		if (i < z_start || i >= z_start + z_offset + z_length) return false;
		// outside external limit
		if (z_offset > 0)
		{
			int temp = (z_offset - 1) / 2;
			if (i > z_start + temp || i <= z_start + temp + z_offset) return false;
			//inside safe area
		}
		return true;
	}
}