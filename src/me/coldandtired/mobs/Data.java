package me.coldandtired.mobs;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import me.coldandtired.mobs.enums.MParam;

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
}