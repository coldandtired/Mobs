package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.w3c.dom.Element;

import me.coldandtired.extra_events.LivingEntityBlockEvent;
import me.coldandtired.extra_events.LivingEntityDamageEvent;
import me.coldandtired.extra_events.PlayerApproachLivingEntityEvent;
import me.coldandtired.extra_events.PlayerLeaveLivingEntityEvent;
import me.coldandtired.extra_events.PlayerNearLivingEntityEvent;
import me.coldandtired.mobs.Enums.ConditionType;
import me.coldandtired.mobs.Enums.ReasonType;
import me.coldandtired.mobs.Enums.SubactionType;
import me.coldandtired.mobs.Enums.TargetType;
import me.coldandtired.mobs.api.Data;
import me.coldandtired.mobs.api.MobsConditionEvent;

public class MobsCondition
{
	//private String name;
	private Map<ConditionType, String> conditions = new HashMap<ConditionType, String>();
	
	private MobsCondition() {}
	
	public static MobsCondition fill(Element element)
	{
		MobsCondition mc = new MobsCondition();
		for (ConditionType ct : ConditionType.values())
		{   
			String s = ct.toString().toLowerCase();
			if (element.hasAttribute(s))
			{
				mc.conditions.put(ct, element.getAttribute(s));
			}
		}
		if (mc.conditions.size() > 0) return mc;
		
		return null;
	}
	
	public boolean passes(EventValues ev)
	{
		Mobs.log("checking...");
		
		Object o = null;
		if (conditions.containsKey(ConditionType.CONDITION_TARGET))
		{
			o = getConditionsTarget(ev);
			if (o == null)
			{
				callConditionEvent(ev, null, null, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		for (ConditionType ct : conditions.keySet())
		{
			String s = conditions.get(ct);
			switch (ct)
			{
				case IF_ADULT: if (!matchesAdult(ct, getBool(s), ev, o)) return false;
					break;
				case IF_ANGRY: if (!matchesAngry(ct, getBool(s), ev, o)) return false;
					break;
				case IF_AREA: if (!matchesArea(ct, s, ev,o)) return false;
					break;
					
				case IF_BIOME:
				case IF_NOT_BIOME: if (!matchesBiome(ct, s, ev, o, ct.equals(ConditionType.IF_NOT_BIOME))) return false;
					break;
					
				case IF_CUSTOM_FLAG_1:
				case IF_CUSTOM_FLAG_2:
				case IF_CUSTOM_FLAG_3:
				case IF_CUSTOM_FLAG_4:
				case IF_CUSTOM_FLAG_5:
				case IF_CUSTOM_FLAG_6:
				case IF_CUSTOM_FLAG_7:
				case IF_CUSTOM_FLAG_8:
				case IF_CUSTOM_FLAG_9:
				case IF_CUSTOM_FLAG_10: if (!matchesCustomFlag(ct, getBool(s), ev, o)) return false;
						break;
						
				case IF_CUSTOM_STRING_1:
				case IF_CUSTOM_STRING_2:
				case IF_CUSTOM_STRING_3:
				case IF_CUSTOM_STRING_4:
				case IF_CUSTOM_STRING_5:
				case IF_CUSTOM_STRING_6:
				case IF_CUSTOM_STRING_7:
				case IF_CUSTOM_STRING_8:
				case IF_CUSTOM_STRING_9:
				case IF_CUSTOM_STRING_10: if (!matchesCustomString(ct, s, ev, o)) return false;
					break;
						
				case IF_MOB:
				case IF_NOT_MOB: if (!matchesMob(ct, s, ev, o, ct.equals(ConditionType.IF_NOT_MOB))) return false;
					break;
					
				case IF_NAME:
				case IF_NOT_NAME: if (!matchesName(ct, s, ev, o, ct.equals(ConditionType.IF_NOT_NAME))) return false;
					break;
					
				case IF_NOT_CUSTOM_STRING_1:
				case IF_NOT_CUSTOM_STRING_2:
				case IF_NOT_CUSTOM_STRING_3:
				case IF_NOT_CUSTOM_STRING_4:
				case IF_NOT_CUSTOM_STRING_5:
				case IF_NOT_CUSTOM_STRING_6:
				case IF_NOT_CUSTOM_STRING_7:
				case IF_NOT_CUSTOM_STRING_8:
				case IF_NOT_CUSTOM_STRING_9:
				case IF_NOT_CUSTOM_STRING_10: if (!matchesNotCustomString(ct, s, ev, o)) return false;
					break;
					
				case IF_POWERED: if (!matchesPowered(ct, getBool(s), ev,o)) return false;
					break;
				case IF_RAINING: if (!matchesRaining(ct, getBool(s), ev)) return false;
					break;
				case IF_SADDLED: if (!matchesSaddled(ct, getBool(s), ev,o)) return false;
					break;
				case IF_SHEARED: if (!matchesSheared(ct, getBool(s), ev,o)) return false;
					break;
				case IF_TAMED: if (!matchesTamed(ct, getBool(s), ev,o)) return false;
					break;
				case IF_THUNDERING: if (!matchesThundering(ct, getBool(s), ev)) return false;
					break;
			}
		}
		return true;
	}
	
	private boolean matchesString(String orig, String needed)
	{
		if (orig == null) return false;
		List<String> temp = Arrays.asList(needed.replace(" ", "").toUpperCase().split(","));
		return temp.contains(orig.toUpperCase());
	}
	
	private boolean matchesAdult(ConditionType ct, boolean needed, EventValues ev, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null)
		{
			if (le instanceof Ageable)
			{
				boolean b = ((Ageable)le).isAdult();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_AN_AGEABLE_MOB, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesAngry(ConditionType ct, boolean needed, EventValues ev, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null)
		{
			if (le instanceof Wolf)
			{
				boolean b = ((Wolf)le).isAngry();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b;
			}
			else if (le instanceof PigZombie)
			{
				boolean b = ((PigZombie)le).isAngry();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_AN_ANGERABLE_MOB, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesArea(ConditionType ct, String needed, EventValues ev, Object o)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
		
		/*for (String value : values)
		{
			String s = value.contains(":") ? "" : loc.getWorld().getName() + ":";
			s += value;
			cr.setCheck_value(s);
			Area area = Mobs.extra_events.getArea(s);
			cr.setActual_value(get_string_from_loc(loc));
			if (area != null && area.isIn_area(loc)) return true;
		}
		break;*/		
	}
	
	private boolean matchesBiome(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{//TODO fix for custom biomes and location objects
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesCustomFlag(ConditionType ct, boolean needed, EventValues ev, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null)
		{
			SubactionType st = SubactionType.valueOf(ct.toString().substring(3));
			if (Data.hasData(le, st))
			{
				boolean b = (Boolean)Data.getData(le, st);
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_METADATA, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesCustomString(ConditionType ct, String needed, EventValues ev, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			SubactionType st = SubactionType.valueOf(ct.toString().substring(3));
			if (Data.hasData(le, st))
			{
				String s = (String)Data.getData(le, st);
				boolean b = matchesString(s, needed);
				callConditionEvent(ev, ct, needed, s, b);
				return b;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_METADATA, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesMob(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}

	private boolean matchesDeathCause(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{//TODO do
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesName(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			if (!(Data.hasData(le, SubactionType.NAME))) return false;
			String s = (String)Data.getData(le, SubactionType.NAME);
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
		
	private boolean matchesNotCustomString(ConditionType ct, String needed, EventValues ev, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			SubactionType st = SubactionType.valueOf(ct.toString().substring(3));
			if (Data.hasData(le, st))
			{
				String s = (String)Data.getData(le, st);
				boolean b = !matchesString(s, needed);
				callConditionEvent(ev, ct, needed, s, b);
				return b;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_METADATA, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesOcelotType(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{//TODO do
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesOwner(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{//TODO do
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesPowered(ConditionType ct, boolean needed, EventValues ev, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null)
		{
			if (le instanceof Creeper)
			{
				boolean b = ((Creeper)le).isPowered();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_A_CREEPER, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesProjectileType(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{//TODO do
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesRaining(ConditionType ct, boolean needed, EventValues ev)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			boolean b = w.hasStorm();
			callConditionEvent(ev, ct, needed, "" + b, b == needed);
			return b == needed;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
	}
	
	private boolean matchesSaddled(ConditionType ct, boolean needed, EventValues ev, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null)
		{
			if (le instanceof Pig)
			{
				boolean b = ((Pig)le).hasSaddle();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_A_PIG, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesSheared(ConditionType ct, boolean needed, EventValues ev, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null)
		{
			if (le instanceof Sheep)
			{
				boolean b = ((Sheep)le).isAdult();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_A_SHEEP, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesSpawnReason(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{//TODO do
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesStandinOn(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{//TODO do
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesTamed(ConditionType ct, boolean needed, EventValues ev, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null)
		{
			if (le instanceof Tameable)
			{
				boolean b = ((Tameable)le).isTamed();
				callConditionEvent(ev, ct, needed, b, b == needed);
				return b == needed;
			}
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NOT_A_TAMEABLE_MOB, false);
				return false;
			}
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesThundering(ConditionType ct, boolean needed, EventValues ev)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			boolean b = w.isThundering();
			callConditionEvent(ev, ct, needed, "" + b, b == needed);
			return b == needed;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
	}
	
	private boolean matchesVillagerType(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{//TODO do
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesWool(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{//TODO do
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	private boolean matchesWorld(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{//TODO do
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le != null) 
		{
			String s = le.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_MOB, false);
		return false;
	}
	
	public Object getConditionsTarget(EventValues ev)
	{		
		String condition_target = conditions.get(ConditionType.CONDITION_TARGET);
		
		if (condition_target.startsWith("CLOSEST_"))
		{
			LivingEntity orig = ev.getLivingEntity();
			if (orig == null) return null;
			
			List<LivingEntity> mobs = getRelevantMobs(orig.getNearbyEntities(50, 10, 50), condition_target.replace("CLOSEST_", ""));
			if (mobs.size() == 0) return null;	
			
			Location loc = orig.getLocation();
			List<NearbyMob> nearby_mobs = new ArrayList<NearbyMob>();
			for (LivingEntity le : mobs)
			{
				nearby_mobs.add(new NearbyMob(le, loc));
			}
	
			Collections.sort(nearby_mobs, new Comparator<NearbyMob>() 
			{
			    public int compare(NearbyMob m1, NearbyMob m2)
			    {
			        return m1.getDistance().compareTo(m2.getDistance());
			    }
			});
			
			return nearby_mobs.get(0).getLivingEntity();
		}
		else if (condition_target.startsWith("RANDOM_"))
		{
			World w = ev.getWorld();
			if (w == null) return null;
			List<LivingEntity> mobs = getRelevantMobs(w.getEntities(), condition_target.replace("RANDOM_", ""));
			if (mobs.size() == 0) return null;	
			
			Collections.shuffle(mobs);
			return mobs.get(0);
		}
		
		switch (TargetType.valueOf(condition_target))
		{			
			case APPROACHED_PLAYER:
			if (!(ev.getOrigEvent() instanceof PlayerApproachLivingEntityEvent)) return null;
			else return ev.getAuxMob();
			
			case ATTACKER:
			if (ev.getOrigEvent() instanceof EntityDamageByEntityEvent
					|| ev.getOrigEvent() instanceof LivingEntityBlockEvent
					|| ev.getOrigEvent() instanceof LivingEntityDamageEvent)
			{
				return ev.getAuxMob();
			}
			else return null;
			
			case BLOCK:
				World w = ev.getWorld();
				if (conditions.containsKey(ConditionType.CONDITION_TARGET_X) && 
						conditions.containsKey(ConditionType.CONDITION_TARGET_Y) &&
						conditions.containsKey(ConditionType.CONDITION_TARGET_Z)) 
					return new Location(w, Integer.valueOf(conditions.get(ConditionType.CONDITION_TARGET_X)), 
							Integer.valueOf(conditions.get(ConditionType.CONDITION_TARGET_Y)), 
							Integer.valueOf(conditions.get(ConditionType.CONDITION_TARGET_Z)));
				else return null;
				
			case CLOSEST:	
				LivingEntity orig = ev.getLivingEntity();
				if (orig == null) return null;	
				
				List<Entity> ents = orig.getNearbyEntities(50, 10, 50);
				if (ents.size() == 0) return null;	
				
				Location loc = orig.getLocation();
				List<NearbyMob> nearby_mobs = new ArrayList<NearbyMob>();
				for (Entity e : ents)
				{
					if (e instanceof LivingEntity) nearby_mobs.add(new NearbyMob((LivingEntity)e, loc));
				}
				
				if (nearby_mobs.size() == 0) return null;
				
				Collections.sort(nearby_mobs, new Comparator<NearbyMob>() 
				{
				    public int compare(NearbyMob m1, NearbyMob m2)
				    {
				        return m1.getDistance().compareTo(m2.getDistance());
				    }
				});
				
				return nearby_mobs.get(0).getLivingEntity();
				
			case KILLER:
				if (!(ev.getOrigEvent() instanceof EntityDeathEvent || ev.getOrigEvent() instanceof PlayerDeathEvent)) return null;
				else return ev.getAuxMob();
				
			case LEFT_PLAYER:
				if (!(ev.getOrigEvent() instanceof PlayerLeaveLivingEntityEvent)) return null;
				else return ev.getAuxMob();
				
			case NEAR_PLAYER:
				if (!(ev.getOrigEvent() instanceof PlayerNearLivingEntityEvent)) return null;
				else return ev.getAuxMob();
				
			case OWNER:
				if (!(ev.getLivingEntity() instanceof Tameable)) return null;
				else return ((Tameable)ev.getLivingEntity()).getOwner();
				
			case PLAYER:
				if (conditions.containsKey(ConditionType.CONDITION_TARGET_NAME)) return null;
				else return Bukkit.getPlayer(conditions.get(ConditionType.CONDITION_TARGET_NAME));
				
			case RANDOM:
				w = ev.getWorld();
				if (w == null) return null;
				List<LivingEntity> mobs = w.getLivingEntities();
				if (mobs.size() == 0) return null;			
								
				Collections.shuffle(mobs);
				return mobs.get(0);
				
			case SHEARER:
				if (!(ev.getOrigEvent() instanceof PlayerShearEntityEvent)) return null;
				else return ev.getAuxMob();
				
			case TAMER:
				if (!(ev.getOrigEvent() instanceof EntityTameEvent)) return null;
				else return ev.getAuxMob();
				
			case TARGETED:
				if (!(ev.getOrigEvent() instanceof EntityTargetLivingEntityEvent)) return null;
				else return ev.getAuxMob();
			
			default: return null;
		}
	}	
	
	private boolean getBool(String s)
	{//TODO all yes/no values through this!
		s = s.toUpperCase();
		if (s == null) return false;
		if (s.equalsIgnoreCase("yes")) return true;
		if (s.equalsIgnoreCase("no")) return false;
		
		return Boolean.valueOf(s);
	}
	
	//TODO target class?
	protected List<LivingEntity> getRelevantMobs(List<Entity> orig, String m)
	{		
		List<String> temp = Arrays.asList(m.replace(" ", "").split(","));
		List<LivingEntity> mobs = new ArrayList<LivingEntity>();
		
		for (Entity e : orig)
		{
			if (!temp.contains(e.getType().toString())) continue;
			if (conditions.containsKey(ConditionType.CONDITION_TARGET_NAME))
			{
				String s = e instanceof Player ? ((Player)e).getName() : (String)Data.getData(e, SubactionType.NAME);
				if (s == null || !s.equalsIgnoreCase(conditions.get(ConditionType.CONDITION_TARGET_NAME))) continue;
			}
			mobs.add((LivingEntity)e);
		}
		return mobs;
	}
	
	/** Calls an event when a condition is checked */
	private void callConditionEvent(EventValues ev, ConditionType ct, Object needed, Object got, boolean passed)
	{
		if (!Mobs.canDebug()) return;
		
		Bukkit.getServer().getPluginManager().callEvent(new MobsConditionEvent(ct.toString(), "" + needed, "" + got, passed, ev));
	}
}