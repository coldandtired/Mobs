package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
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
	
	private boolean matchesLivingEntity(ConditionType ct, EventValues ev, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ev, ct, conditions.get(ct), ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le == null)
		{
			callConditionEvent(ev, ct, conditions.get(ct), ReasonType.NO_MOB, false);
			return false;
		}
		
		switch (ct)
		{
			case IF_ADULT: return matchesAdult(ct, ev, le);		
			case IF_AGE: return matchesAge(ct, ev, le);			
			case IF_ANGRY: return matchesAngry(ct, ev, le);
			case IF_CUSTOM_FLAG_1:
			case IF_CUSTOM_FLAG_2:
			case IF_CUSTOM_FLAG_3:
			case IF_CUSTOM_FLAG_4:
			case IF_CUSTOM_FLAG_5:
			case IF_CUSTOM_FLAG_6:
			case IF_CUSTOM_FLAG_7:
			case IF_CUSTOM_FLAG_8:
			case IF_CUSTOM_FLAG_9:
			case IF_CUSTOM_FLAG_10: return matchesCustomFlag(ct, ev, le);

			case IF_CUSTOM_INT_1:
			case IF_CUSTOM_INT_2:
			case IF_CUSTOM_INT_3:
			case IF_CUSTOM_INT_4:
			case IF_CUSTOM_INT_5:
			case IF_CUSTOM_INT_6:
			case IF_CUSTOM_INT_7:
			case IF_CUSTOM_INT_8:
			case IF_CUSTOM_INT_9:
			case IF_CUSTOM_INT_10: return matchesCustomInt(ct, ev, le);
					
			case IF_CUSTOM_STRING_1:
			case IF_CUSTOM_STRING_2:
			case IF_CUSTOM_STRING_3:
			case IF_CUSTOM_STRING_4:
			case IF_CUSTOM_STRING_5:
			case IF_CUSTOM_STRING_6:
			case IF_CUSTOM_STRING_7:
			case IF_CUSTOM_STRING_8:
			case IF_CUSTOM_STRING_9:
			case IF_CUSTOM_STRING_10: return matchesCustomString(ct, ev, le);
				
			case IF_DEATH_CAUSE:
			case IF_NOT_DEATH_CAUSE: return matchesDeathCause(ct, ev, le);
				
			case IF_KILLED_BY_PLAYER: return matchesKilledByPlayer(ct, ev, le);
			
			case IF_MOB:
			case IF_NOT_MOB: return matchesMob(ct, ev, le);
				
			case IF_NAME:
			case IF_NOT_NAME: return matchesName(ct, ev, le);
				
			case IF_NOT_CUSTOM_STRING_1:
			case IF_NOT_CUSTOM_STRING_2:
			case IF_NOT_CUSTOM_STRING_3:
			case IF_NOT_CUSTOM_STRING_4:
			case IF_NOT_CUSTOM_STRING_5:
			case IF_NOT_CUSTOM_STRING_6:
			case IF_NOT_CUSTOM_STRING_7:
			case IF_NOT_CUSTOM_STRING_8:
			case IF_NOT_CUSTOM_STRING_9:
			case IF_NOT_CUSTOM_STRING_10: return matchesNotCustomString(ct, ev, le);
				
			case IF_OCELOT:
			case IF_NOT_OCELOT: return matchesOcelot(ct, ev, le);
				
			case IF_OWNER:
			case IF_NOT_OWNER: return matchesOwner(ct, ev, le);
			
			case IF_PLAYER_HAS_PERMISSION:
			case IF_NOT_PLAYER_HAS_PERMISSION: return matchesPlayerHasPermission(ct, ev, le);
				
			case IF_PLAYER_IS_OP: return matchesPlayerIsOp(ct, ev,le);
			
			case IF_POWERED: return matchesPowered(ct, ev, le);
			
			case IF_SADDLED: return matchesSaddled(ct, ev, le);
				
			case IF_SHEARED: return matchesSheared(ct, ev, le);
				
			case IF_SPAWN_REASON:
			case IF_NOT_SPAWN_REASON: return matchesSpawnReason(ct,ev, le);
				
			case IF_TAMED: return matchesTamed(ct, ev, le);
				
			case IF_VILLAGER:
			case IF_NOT_VILLAGER: return matchesVillager(ct, ev, le);
				
			case IF_WOOL:
			case IF_NOT_WOOL: return matchesWool(ct, ev, le);
		}
		
		return false;
	}
	
	private boolean matchesLocation(ConditionType ct, EventValues ev, Object o)
	{
		Location loc;
		if (ev.getLivingEntity() != null) loc = ev.getLivingEntity().getLocation();
		if (o != null)
		{
			if (o instanceof Location) loc = (Location)o;
			else if (o instanceof LivingEntity) loc = ((LivingEntity)o).getLocation();
			else
			{
				callConditionEvent(ev, ct, conditions.get(ct), ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (loc == null)
		{
			callConditionEvent(ev, ct, conditions.get(ct), ReasonType.NO_LOCATION, false);
			return false;
		}
		
		switch (ct)
		{
			case IF_AREA: return matchesArea(ct, ev, loc);		
			case IF_BIOME:
			case IF_NOT_BIOME: return matchesBiome(ct, ev, loc);		
			case IF_BLOCK_LIGHT_LEVEL: return matchesBlockLightLevel(ct, ev, loc);		
			case IF_LIGHT_LEVEL: return matchesLightLevel(ct, ev, loc);
			case IF_SKY_LIGHT_LEVEL: return matchesSkyLightLevel(ct, ev, loc);		
			case IF_X: return matchesX(ct, ev, loc);		
			case IF_Y: return matchesY(ct, ev, loc);		
			case IF_Z: return matchesZ(ct, ev, loc);
		}
	}
	
	private boolean matchesWorld(ConditionType ct, EventValues ev, Object o)
	{
		
	}
	
	public boolean passes(EventValues ev)
	{//TODO remove
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
			switch (ct)
			{
				case IF_ADULT:
				case IF_AGE:
				case IF_ANGRY:
				case IF_CUSTOM_FLAG_1:
				case IF_CUSTOM_FLAG_2:
				case IF_CUSTOM_FLAG_3:
				case IF_CUSTOM_FLAG_4:
				case IF_CUSTOM_FLAG_5:
				case IF_CUSTOM_FLAG_6:
				case IF_CUSTOM_FLAG_7:
				case IF_CUSTOM_FLAG_8:
				case IF_CUSTOM_FLAG_9:
				case IF_CUSTOM_FLAG_10:
				case IF_CUSTOM_INT_1:
				case IF_CUSTOM_INT_2:
				case IF_CUSTOM_INT_3:
				case IF_CUSTOM_INT_4:
				case IF_CUSTOM_INT_5:
				case IF_CUSTOM_INT_6:
				case IF_CUSTOM_INT_7:
				case IF_CUSTOM_INT_8:
				case IF_CUSTOM_INT_9:
				case IF_CUSTOM_INT_10:
				case IF_CUSTOM_STRING_1:
				case IF_CUSTOM_STRING_2:
				case IF_CUSTOM_STRING_3:
				case IF_CUSTOM_STRING_4:
				case IF_CUSTOM_STRING_5:
				case IF_CUSTOM_STRING_6:
				case IF_CUSTOM_STRING_7:
				case IF_CUSTOM_STRING_8:
				case IF_CUSTOM_STRING_9:
				case IF_CUSTOM_STRING_10:
				case IF_DEATH_CAUSE:
				case IF_NOT_DEATH_CAUSE:
				case IF_KILLED_BY_PLAYER:
				case IF_MOB:
				case IF_NOT_MOB:
				case IF_NAME:
				case IF_NOT_NAME:
				case IF_NOT_CUSTOM_STRING_1:
				case IF_NOT_CUSTOM_STRING_2:
				case IF_NOT_CUSTOM_STRING_3:
				case IF_NOT_CUSTOM_STRING_4:
				case IF_NOT_CUSTOM_STRING_5:
				case IF_NOT_CUSTOM_STRING_6:
				case IF_NOT_CUSTOM_STRING_7:
				case IF_NOT_CUSTOM_STRING_8:
				case IF_NOT_CUSTOM_STRING_9:
				case IF_NOT_CUSTOM_STRING_10:
				case IF_OCELOT:
				case IF_NOT_OCELOT:
				case IF_OWNER:
				case IF_NOT_OWNER:
				case IF_PLAYER_HAS_PERMISSION:
				case IF_NOT_PLAYER_HAS_PERMISSION:
				case IF_PLAYER_IS_OP:
				case IF_POWERED:
				case IF_SADDLED:
				case IF_SHEARED:
				case IF_SPAWN_REASON:
				case IF_NOT_SPAWN_REASON:
				case IF_TAMED:
				case IF_VILLAGER:
				case IF_NOT_VILLAGER:
				case IF_WOOL:
				case IF_NOT_WOOL: if (!matchesLivingEntity(ct, ev, o)) return false;
					break;
					
				case IF_AREA:				
				case IF_BIOME:
				case IF_NOT_BIOME:					
				case IF_BLOCK_LIGHT_LEVEL:					
				case IF_LIGHT_LEVEL:
				case IF_SKY_LIGHT_LEVEL:					
				case IF_X:					
				case IF_Y: 					
				case IF_Z: if (!matchesLocation(ct, ev, o)) return false;
					break;
			}
		}
		
		for (ConditionType ct : conditions.keySet())
		{
			String s = conditions.get(ct);
			switch (ct)
			{					
				
					
				case IF_LUNAR_PHASE: if (!matchesLunarPhase(ct, s, ev, o)) return false;
					break;
									
				case IF_PERCENT: if (!matchesPercent(ct, s, ev, o)) return false;
					break;
					
				case IF_IN_WORLD:
				case IF_NOT_IN_WORLD: if (!matchesInWorld(ct, s, ev, o, ct.equals(ConditionType.IF_NOT_IN_WORLD))) return false;
					break;
					
				case IF_ON_SERVER:
				case IF_NOT_ON_SERVER: if (!matchesOnServer(ct, s, ev, o, ct.equals(ConditionType.IF_NOT_ON_SERVER))) return false;
					break;
					
				case IF_PROJECTILE:
				case IF_NOT_PROJECTILE: if (!matchesProjectile(ct, s, ev, o, ct.equals(ConditionType.IF_NOT_PROJECTILE))) return false;
					break;
					
				case IF_RAINING: if (!matchesRaining(ct, getBool(s), ev)) return false;
					break;
					
				case IF_SERVER_PLAYER_COUNT: if (!matchesServerPlayerCount(ct, s, ev, o)) return false;
					break;
					
				
					
				case IF_THUNDERING: if (!matchesThundering(ct, getBool(s), ev)) return false;
					break;
					
				case IF_WORLD:
				case IF_NOT_WORLD: if (!matchesWorld(ct, s, ev, o, ct.equals(ConditionType.IF_NOT_WORLD))) return false;
					break;
					
				case IF_WORLD_TIME: if (!matchesWorldTime(ct, s, ev, o)) return false;
					break;
					
				case IF_WORLD_TYPE:
				case IF_NOT_WORLD_TYPE: if (!matchesWorldType(ct, s, ev, o, ct.equals(ConditionType.IF_NOT_WORLD_TYPE))) return false;
					break;
			}
		}
		return true;
	}
	
	private boolean matchesAdult(ConditionType ct, EventValues ev, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
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
	
	private boolean matchesAge(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Ageable)
		{
			Integer i = ((Ageable)le).getAge();
			boolean b = matchesInt(i, needed);
			callConditionEvent(ev, ct, needed, i, b);
			return b;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NOT_AN_AGEABLE_MOB, false);
			return false;
		}
	}
	
	private boolean matchesAngry(ConditionType ct, EventValues ev, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
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
	
	private boolean matchesArea(ConditionType ct, String needed, EventValues ev, Object o)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
		
		//Area area = Mobs.extra_events.getArea(s);
		
		/*for (String value : values)
		{
			String s = value.contains(":") ? "" : loc.getWorld().getName() + ":";
			s += value;
			cr.setCheck_value(s);
			Area area = Mobs.extra_events.getArea(s);
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
	
	private boolean matchesCustomFlag(ConditionType ct, EventValues ev, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
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
	
	private boolean matchesCustomInt(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		SubactionType st = SubactionType.valueOf(ct.toString().substring(3));
		if (Data.hasData(le, st))
		{
			Integer i = (Integer)Data.getData(le, st);
			boolean b = matchesInt(i, needed);
			callConditionEvent(ev, ct, needed, i, b);
			return b;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NO_METADATA, false);
			return false;
		}
	}
	
	private boolean matchesCustomString(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
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
		
	private boolean matchesKilledByPlayer(ConditionType ct, EventValues ev, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le.isDead())
		{
			boolean b = le.getKiller() != null;
			callConditionEvent(ev, ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NOT_DEAD, false);
			return false;
		}
	}
	
	private boolean matchesLunarPhase(ConditionType ct, String needed, EventValues ev, Object o)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			long i = (w.getFullTime()/24000) % 8;
			boolean b = matchesInt((int)i, needed);
			callConditionEvent(ev, ct, needed, i, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
	}
	
	private boolean matchesMob(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		String s = le.getType().toString();
		boolean b = matchesString(s, needed);
		if (ct.equals(ConditionType.IF_NOT_MOB)) b = !b;
		callConditionEvent(ev, ct, needed, s, b);
		return b;
	}

	private boolean matchesDeathCause(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le.isDead())
		{
			String s = le.getLastDamageCause().toString();	
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_DEATH_CAUSE)) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NOT_DEAD, false);
			return false;
		}
	}
	
	private boolean matchesInWorld(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			boolean b = false;
			for (Player p : w.getPlayers())
			{
				String s = p.getName();
				if (matchesString(s, needed))
				{
					b = true;
					break;
				}
			}
			
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, w.getName() + "'s players", b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
	}
	
	private boolean matchesName(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Player)
		{
			String s = ((Player)le).getName();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_NAME)) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		else if (!Data.hasData(le, SubactionType.NAME)) return false;
		
		String s = (String)Data.getData(le, SubactionType.NAME);
		boolean b = matchesString(s, needed);
		if (ct.equals(ConditionType.IF_NOT_NAME)) b = !b;
		callConditionEvent(ev, ct, needed, s, b);
		return b;
	}
		
	private boolean matchesNotCustomString(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
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
	
	private boolean matchesOcelot(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Ocelot)
		{
			String s = ((Ocelot)le).getCatType().name();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_OCELOT)) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NO_TARGET, false);
			return false;
		}
	}
	
	private boolean matchesOnServer(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{
		boolean b = false;
		for (Player p : Bukkit.getOnlinePlayers())
		{
			String s = p.getName();
			if (matchesString(s, needed))
			{
				b = true;
				break;
			}
		}
		
		if (reversed) b = !b;
		callConditionEvent(ev, ct, needed, "Server players", b);
		return b;
	}
	
	private boolean matchesOwner(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Tameable)
		{
			String s = ((Tameable)le).getOwner().getName();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_OWNER)) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NOT_A_TAMEABLE_MOB, false);
			return false;
		}
	}
	
	private boolean matchesPercent(ConditionType ct, String needed, EventValues ev, Object o)
	{
		int i = new Random().nextInt(101);
		boolean b = i >= Integer.valueOf(needed);
		callConditionEvent(ev, ct, needed, i, b);
		return b;
	}
	
	private boolean matchesPlayerIsOp(ConditionType ct, EventValues ev, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le instanceof Player)
		{
			boolean b = ((Player)le).isOp();
			callConditionEvent(ev, ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NOT_A_PLAYER, false);
			return false;
		}
	}
	
	private boolean matchesPlayerHasPermission(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Player)
		{
			boolean b = ((Player)le).hasPermission(needed);
			if (ct.equals(ConditionType.IF_NOT_PLAYER_HAS_PERMISSION)) b = !b;
			callConditionEvent(ev, ct, needed, ((Player)le).getName() + "'s permissions", b);
			return b;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NOT_A_PLAYER, false);
			return false;
		}
	}
	
	private boolean matchesPowered(ConditionType ct, EventValues ev, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
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
	
	private boolean matchesProjectile(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{
		Projectile p = ev.getProjectile();
		
		if (p != null) 
		{
			String s = p.getType().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_PROJECTILE, false);
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
	
	private boolean matchesSaddled(ConditionType ct, EventValues ev, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
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
	
	private boolean matchesServerPlayerCount(ConditionType ct, String needed, EventValues ev, Object o)
	{
		int i = Bukkit.getOnlinePlayers().length;
		boolean b = matchesInt(i, needed);
		callConditionEvent(ev, ct, needed, i, b);
		return b;
	}
	
	private boolean matchesSheared(ConditionType ct, EventValues ev, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
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
	
	private boolean matchesSpawnReason(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (Data.hasData(le, SubactionType.SPAWN_REASON))
		{
			String s = (String)Data.getData(le, SubactionType.SPAWN_REASON);
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_SPAWN_REASON)) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NO_METADATA, false);
			return false;
		}
	}
	
	private boolean matchesStandinOn(ConditionType ct, EventValues ev, LivingEntity le)
	{//TODO do
		return true;
	}
	
	private boolean matchesTamed(ConditionType ct, EventValues ev, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
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
	
	private boolean matchesVillager(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Villager)
		{
			String s = ((Villager)le).getProfession().name();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_VILLAGER)) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NOT_A_VILLAGER, false);
			return false;
		}
	}
	
	private boolean matchesWool(ConditionType ct, EventValues ev, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Sheep)
		{
			String s = ((Sheep)le).getColor().name();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_WOOL)) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ev, ct, needed, ReasonType.NOT_A_SHEEP, false);
			return false;
		}
	}
	
	private boolean matchesWorld(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			String s = w.getName();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
	}
	
	private boolean matchesWorldTime(ConditionType ct, String needed, EventValues ev, Object o)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			long i = w.getTime();
			boolean b = matchesInt((int)i, needed);
			callConditionEvent(ev, ct, needed, i, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
	}
	
	private boolean matchesWorldType(ConditionType ct, String needed, EventValues ev, Object o, boolean reversed)
	{
		World w = ev.getWorld();
		if (w != null)
		{
			String s = w.getEnvironment().toString();
			boolean b = matchesString(s, needed);
			if (reversed) b = !b;
			callConditionEvent(ev, ct, needed, s, b);
			return b;
		}
		callConditionEvent(ev, ct, needed, ReasonType.NO_WORLD, false);
		return false;
	}
	
// Utils	
	
	private boolean matchesString(String orig, String needed)
	{
		if (orig == null) return false;
		List<String> temp = Arrays.asList(needed.replace(" ", "").toUpperCase().split(","));
		return temp.contains(orig.toUpperCase());
	}
	
	private boolean matchesInt(Integer orig, String needed)
	{
		if (orig == null) return false;

		needed = needed.replace(" ", "").toUpperCase();

		for (String s :needed.split(","))
		{
			if (s.startsWith("ABOVE"))
			{
				if (Integer.parseInt(s.replace("ABOVE", "")) < orig) return true;
			}
			else if (s.startsWith("BELOW"))
			{
				if (Integer.parseInt(s.replace("BELOW", "")) > orig) return true;
			}
			else if (s.contains("TO"))
			{
				String[]temp = s.split("TO");
				if (orig >= Math.min(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])) &&
					orig <= Math.max(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]))) return true;
			}
			else if (Integer.parseInt(s) == orig) return true;
		}
		return false;
	}
	
	private Object getConditionsTarget(EventValues ev)
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

	private List<LivingEntity> getRelevantMobs(List<Entity> orig, String m)
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