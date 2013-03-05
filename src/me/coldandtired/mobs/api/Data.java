package me.coldandtired.mobs.api;

import java.util.HashMap;
import java.util.Map;
import me.coldandtired.mobs.Mobs;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;

public class Data 
{
	@SuppressWarnings("unchecked")
	public static boolean hasData(Metadatable m, Object param)
	{
		if (m.hasMetadata("mobs_data"))
		{
			return ((Map<String, Object>)m.getMetadata("mobs_data").get(0).value()).containsKey(param.toString());
		} else return false;
	}
	
	@SuppressWarnings("unchecked")
	public static void putData(Metadatable m, Object param)
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
			m.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getPlugin(), data));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void putData(Metadatable m, Object param, Object value)
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
			m.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getPlugin(), data));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void removeData(Metadatable m, Object param)
	{
		if (m.hasMetadata("mobs_data"))
		{
			((Map<String, Object>)m.getMetadata("mobs_data").get(0).value()).remove(param.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object getData(Metadatable m, Object param)
	{
		if (!m.hasMetadata("mobs_data")) return null;
		
		return	((Map<String, Object>)m.getMetadata("mobs_data").get(0).value()).get(param.toString());		
	}
	
	public static void clearData(Metadatable m)
	{
		m.removeMetadata("mobs_data", Mobs.getPlugin());
	}
}