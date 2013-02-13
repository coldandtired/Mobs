package me.coldandtired.mobs;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import me.coldandtired.mobs.Enums.MParam;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;

public class Data 
{
	@SuppressWarnings("unchecked")
	public static boolean hasData(Metadatable m, MParam param)
	{
		if (m.hasMetadata("mobs_data"))
		{
			return ((Map<String, Object>)m.getMetadata("mobs_data").get(0).value()).containsKey(param.toString());
		} else return false;
	}
	
	@SuppressWarnings("unchecked")
	public static void putData(Metadatable m, MParam param)
	{
		Map<String, Object> data;
		if (m.hasMetadata("mobs_data"))
		{
			data = (Map<String, Object>)m.getMetadata("mobs_data").get(0).value();
			data.put(param.toString(), null);
		}
		else 
		{
			data = new HashMap<String, Object>();
			data.put(param.toString(), null);
			m.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void putData(Metadatable m, MParam param, Object value)
	{
		Map<String, Object> data;
		if (m.hasMetadata("mobs_data"))
		{
			data = (Map<String, Object>)m.getMetadata("mobs_data").get(0).value();
			data.put(param.toString(), value);
		}
		else 
		{
			data = new HashMap<String, Object>();
			data.put(param.toString(), value);
			m.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void removeData(Metadatable m, MParam param)
	{
		if (m.hasMetadata("mobs_data"))
		{
			((Map<String, Object>)m.getMetadata("mobs_data").get(0).value()).remove(param.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object getData(Metadatable m, MParam param)
	{
		if (!m.hasMetadata("mobs_data")) return null;
		
		return	((Map<String, Object>)m.getMetadata("mobs_data").get(0).value()).get(param.toString());		
	}
	
	public static void toggleData(Metadatable m, MParam param)
	{
		if (hasData(m, param)) removeData(m, param); else putData(m, param);
	}
	
	public static void putRandom_data(Metadatable m, MParam param)
	{
		if (new Random().nextBoolean()) removeData(m, param); else putData(m, param);
	}

	public static void clearData(Metadatable m)
	{
		m.removeMetadata("mobs_data", Mobs.getInstance());
	}
	
	public static int adjustInt(Metadatable m, MParam param, int orig)
	{
		String ad = (String)getData(m, param);
		if (ad == null) return orig;
		
		String[] value = ad.split(":");
		
		String s = value[0].replace(" ", "");
		int a = 0;
		if (s.contains("to"))
		{
			String[] temp = s.split("to");
			a = Math.min(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
			int b = Math.max(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
			a = new Random().nextInt((b - a) + 1) + a;
		}
		else a = Integer.parseInt(s);
		
		if (value.length == 1) return a;
		
		if (value[1].contains("%"))
		{
			if (value[1].contains("+")) return ((orig * a) / 100) + orig;
			else if (value[1].contains("-")) return orig - ((orig * a) / 100);
			else return (orig * a) / 100;
		}
		else
		{
			if (value[1].contains("+")) return orig + a;
			else if (value[1].contains("-")) return orig - a;
		}
		return orig;
	}
}