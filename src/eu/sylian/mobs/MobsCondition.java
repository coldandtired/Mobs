package eu.sylian.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.w3c.dom.Element;

import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;

import eu.sylian.mobs.Enums.ConditionType;
import eu.sylian.mobs.Enums.ReasonType;
import eu.sylian.mobs.Enums.SubactionType;
import eu.sylian.mobs.Enums.TargetType;
import eu.sylian.mobs.api.Data;
import eu.sylian.mobs.api.MobsConditionEvent;
import eu.sylian.extraevents.Area;
import eu.sylian.extraevents.LivingEntityBlockEvent;
import eu.sylian.extraevents.LivingEntityDamageEvent;
import eu.sylian.extraevents.LivingEntityLeaveAreaEvent;
import eu.sylian.extraevents.PlayerApproachLivingEntityEvent;
import eu.sylian.extraevents.PlayerCommandEvent;
import eu.sylian.extraevents.PlayerLeaveAreaEvent;
import eu.sylian.extraevents.PlayerLeaveLivingEntityEvent;
import eu.sylian.extraevents.PlayerNearLivingEntityEvent;
import eu.sylian.extraevents.Timer;

public class MobsCondition
{
	private EventValues ev;
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
	
	private boolean matchesLivingEntity(ConditionType ct, Object o)
	{
		LivingEntity le = ev.getLivingEntity();
		if (o != null)
		{
			if (o instanceof LivingEntity) le = (LivingEntity)o;
			else
			{
				callConditionEvent(ct, conditions.get(ct), ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (le == null)
		{
			callConditionEvent(ct, conditions.get(ct), ReasonType.NO_MOB, false);
			return false;
		}
		
		switch (ct)
		{
			case IF_ADULT: return matchesAdult(ct, le);		
			case IF_AGE: return matchesAge(ct, le);			
			case IF_ANGRY: return matchesAngry(ct, le);

			case IF_BOOTS:
			case IF_NOT_BOOTS: return matchesBoots(ct, le);
			
			case IF_CARRYING:
			case IF_NOT_CARRYING: return matchesCarrying(ct, le);

			case IF_CHESTPLATE:
			case IF_NOT_CHESTPLATE: return matchesChestplate(ct, le);
			
			case IF_CHUNK_COUNT: return matchesChunkCount(ct, le);
			
			case IF_CUSTOM_FLAG_1:
			case IF_CUSTOM_FLAG_2:
			case IF_CUSTOM_FLAG_3:
			case IF_CUSTOM_FLAG_4:
			case IF_CUSTOM_FLAG_5:
			case IF_CUSTOM_FLAG_6:
			case IF_CUSTOM_FLAG_7:
			case IF_CUSTOM_FLAG_8:
			case IF_CUSTOM_FLAG_9:
			case IF_CUSTOM_FLAG_10: return matchesCustomFlag(ct, le);

			case IF_CUSTOM_INT_1:
			case IF_CUSTOM_INT_2:
			case IF_CUSTOM_INT_3:
			case IF_CUSTOM_INT_4:
			case IF_CUSTOM_INT_5:
			case IF_CUSTOM_INT_6:
			case IF_CUSTOM_INT_7:
			case IF_CUSTOM_INT_8:
			case IF_CUSTOM_INT_9:
			case IF_CUSTOM_INT_10: return matchesCustomInt(ct, le);
					
			case IF_CUSTOM_STRING_1:
			case IF_CUSTOM_STRING_2:
			case IF_CUSTOM_STRING_3:
			case IF_CUSTOM_STRING_4:
			case IF_CUSTOM_STRING_5:
			case IF_CUSTOM_STRING_6:
			case IF_CUSTOM_STRING_7:
			case IF_CUSTOM_STRING_8:
			case IF_CUSTOM_STRING_9:
			case IF_CUSTOM_STRING_10: return matchesCustomString(ct, le);
				
			case IF_DEATH_CAUSE:
			case IF_NOT_DEATH_CAUSE: return matchesDeathCause(ct, le);
				
			case IF_HELMET:
			case IF_NOT_HELMET: return matchesHelmet(ct, le);
			
			case IF_HOLDING:
			case IF_NOT_HOLDING: return matchesHolding(ct, le);
				
			case IF_CURRENT_INV_SLOT: return matchesCurrentInvSlot(ct, le);
			
			case IF_KILLED_BY_PLAYER: return matchesKilledByPlayer(ct, le);

			case IF_LEGGINGS:
			case IF_NOT_LEGGINGS: return matchesLeggings(ct, le);
			
			case IF_MOB:
			case IF_NOT_MOB: return matchesMob(ct, le);
				
			case IF_MONEY: return matchesMoney(ct, le);
			
			case IF_NAME:
			case IF_NOT_NAME: return matchesName(ct, le);
				
			case IF_NAME_IS_VISIBLE: return matchesNameIsVisible(ct, le);
			
			case IF_NOT_CUSTOM_STRING_1:
			case IF_NOT_CUSTOM_STRING_2:
			case IF_NOT_CUSTOM_STRING_3:
			case IF_NOT_CUSTOM_STRING_4:
			case IF_NOT_CUSTOM_STRING_5:
			case IF_NOT_CUSTOM_STRING_6:
			case IF_NOT_CUSTOM_STRING_7:
			case IF_NOT_CUSTOM_STRING_8:
			case IF_NOT_CUSTOM_STRING_9:
			case IF_NOT_CUSTOM_STRING_10: return matchesNotCustomString(ct, le);
				
			case IF_OCELOT:
			case IF_NOT_OCELOT: return matchesOcelot(ct, le);
				
			case IF_OWNER:
			case IF_NOT_OWNER: return matchesOwner(ct, le);
			
			case IF_PLAYER_HAS_PERMISSION:
			case IF_NOT_PLAYER_HAS_PERMISSION: return matchesPlayerHasPermission(ct, le);
				
			case IF_PLAYER_IS_OP: return matchesPlayerIsOp(ct, le);
			
			case IF_POWERED: return matchesPowered(ct, le);
			
			case IF_REMAINING_LIFETIME: return matchesRemainingLifetime(ct, le);
			
			case IF_SADDLED: return matchesSaddled(ct, le);
				
			case IF_SHEARED: return matchesSheared(ct, le);
				
			case IF_SKELETON_IS_WITHER: return matchesSkeletonType(ct, le);
			
			case IF_SPAWN_REASON:
			case IF_NOT_SPAWN_REASON: return matchesSpawnReason(ct, le);

			case IF_STANDING_ON:
			case IF_NOT_STANDING_ON: return matchesStandingOn(ct, le);
			
			case IF_TAMED: return matchesTamed(ct, le);
				
			case IF_VILLAGER:
			case IF_NOT_VILLAGER: return matchesVillager(ct, le);
				
			case IF_WEARING:
			case IF_NOT_WEARING: return matchesWearing(ct, le);
			
			case IF_WOOL:
			case IF_NOT_WOOL: return matchesWool(ct, le);
			
			case IF_ZOMBIE_IS_VILLAGER: return matchesZombieVillager(ct, le);
		}
		
		return false;
	}
	
	private boolean matchesBlock(ConditionType ct, Object o)
	{
		Block block = null;
		if (ev.getLivingEntity() != null) block = ev.getLivingEntity().getLocation().getBlock();
		if (o != null)
		{
			if (o instanceof Block) block = (Block)o;
			else if (o instanceof LivingEntity) block = ((LivingEntity)o).getLocation().getBlock();
			else
			{
				callConditionEvent(ct, conditions.get(ct), ReasonType.NO_TARGET, false);
				return false;
			}
		}
		
		if (block == null)
		{
			callConditionEvent(ct, conditions.get(ct), ReasonType.NO_LOCATION, false);
			return false;
		}

		switch (ct)
		{
			case IF_AREA: return matchesArea(ct, block);
			case IF_BLOCK:
			case IF_NOT_BLOCK: return matchesBlockType(ct, block);
			case IF_BIOME:
			case IF_NOT_BIOME: return matchesBiome(ct, block);		
			case IF_BLOCK_LIGHT_LEVEL: return matchesBlockLightLevel(ct, block);		
			case IF_BLOCKS_FROM_SPAWN: return matchesBlocksFromSpawn(ct, block);
			case IF_LIGHT_LEVEL: return matchesLightLevel(ct, block);
			case IF_SKY_LIGHT_LEVEL: return matchesSkyLightLevel(ct, block);		
			case IF_X: return matchesX(ct, block);		
			case IF_Y: return matchesY(ct, block);		
			case IF_Z: return matchesZ(ct, block);
		}
		
		return false;
	}
		
	public boolean passes(EventValues ev)
	{
		Object o = null;
		if (conditions.containsKey(ConditionType.CONDITION_TARGET))
		{
			o = getConditionsTarget(ev);
			if (o == null)
			{
				callConditionEvent(null, null, ReasonType.NO_TARGET, false);
				return false;
			}
		}	
		
		this.ev = ev;
		
		for (ConditionType ct : conditions.keySet())
		{
			switch (ct)
			{
				case IF_ADULT:
				case IF_AGE:
				case IF_ANGRY:
				case IF_BOOTS:
				case IF_NOT_BOOTS:
				case IF_CARRYING:
				case IF_NOT_CARRYING:
				case IF_CHESTPLATE:
				case IF_NOT_CHESTPLATE:
				case IF_CHUNK_COUNT:
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
				case IF_HELMET:
				case IF_NOT_HELMET:
				case IF_HOLDING:
				case IF_NOT_HOLDING:
				case IF_KILLED_BY_PLAYER:
				case IF_LEGGINGS:
				case IF_NOT_LEGGINGS:
				case IF_MOB:
				case IF_NOT_MOB:
				case IF_MONEY:
				case IF_NAME:
				case IF_NAME_IS_VISIBLE:
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
				case IF_REMAINING_LIFETIME:
				case IF_SADDLED:
				case IF_SHEARED:
				case IF_SKELETON_IS_WITHER:
				case IF_SPAWN_REASON:
				case IF_STANDING_ON:
				case IF_NOT_STANDING_ON:
				case IF_NOT_SPAWN_REASON:
				case IF_TAMED:
				case IF_VILLAGER:
				case IF_NOT_VILLAGER:
				case IF_WEARING:
				case IF_NOT_WEARING:
				case IF_WOOL:
				case IF_NOT_WOOL: 
				case IF_ZOMBIE_IS_VILLAGER: if (!matchesLivingEntity(ct, o)) return false;
					break;
					
				case IF_AREA:
				case IF_BLOCK:
				case IF_NOT_BLOCK:
				case IF_NOT_AREA:
				case IF_BIOME:
				case IF_NOT_BIOME:					
				case IF_BLOCK_LIGHT_LEVEL:
				case IF_BLOCKS_FROM_SPAWN:
				case IF_LIGHT_LEVEL:
				case IF_SKY_LIGHT_LEVEL:					
				case IF_X:					
				case IF_Y: 					
				case IF_Z: if (!matchesBlock(ct, o)) return false;
					break;
			}
		}
		
		for (ConditionType ct : conditions.keySet())
		{
			switch (ct)
			{
				case IF_AREA_COUNT: if (!matchesAreaCount(ct)) return false;
					break;
				case IF_COMMAND:
				case IF_NOT_COMMAND: if (!matchesCommand(ct)) return false;			
					break;		
				case IF_COMMAND_ARGS: if (!matchesCommandArgs(ct)) return false;
					break;
				case IF_DATE: if (!matchesDate(ct)) return false;
					break;
				case IF_DAY: if (!matchesDay(ct)) return false;
					break;
				case IF_DAY_OF_YEAR: if (!matchesDayOfYear(ct)) return false;
					break;
				case IF_HOUR: if (!matchesHour(ct)) return false;
					break;
				case IF_LUNAR_PHASE: if (!matchesLunarPhase(ct)) return false;
					break;									
				case IF_PERCENT: if (!matchesPercent(ct)) return false;
					break;	
				case IF_MINUTE: if (!matchesMinute(ct)) return false;
					break;	
				case IF_MONTH: if (!matchesMonth(ct)) return false;
					break;			
				case IF_IN_WORLD:
				case IF_NOT_IN_WORLD: if (!matchesInWorld(ct)) return false;
					break;					
				case IF_ON_SERVER:
				case IF_NOT_ON_SERVER: if (!matchesOnServer(ct)) return false;
					break;
					
				case IF_PROJECTILE:
				case IF_NOT_PROJECTILE: if (!matchesProjectile(ct)) return false;
					break;
					
				case IF_RAINING: if (!matchesRaining(ct)) return false;
					break;	
				case IF_SECOND: if (!matchesSecond(ct)) return false;
					break;
				case IF_SERVER_PLAYER_COUNT: if (!matchesServerPlayerCount(ct)) return false;
					break;
				case IF_THUNDERING: if (!matchesThundering(ct)) return false;
					break;
					
				case IF_TIMER:
				case IF_NOT_TIMER: if (!matchesTimer(ct)) return false;
					break;
					
				case IF_WEEK: if (!matchesWeek(ct)) return false;
					break;
				case IF_WEEK_OF_MONTH: if (!matchesWeekOfMonth(ct)) return false;
					break;				
				case IF_WORLD:
				case IF_NOT_WORLD: if (!matchesWorld(ct)) return false;
					break;
				case IF_WORLD_COUNT: if (!matchesWorldCount(ct)) return false;
				case IF_WORLD_TIME: if (!matchesWorldTime(ct)) return false;
					break;					
				case IF_WORLD_TYPE:
				case IF_NOT_WORLD_TYPE: if (!matchesWorldType(ct)) return false;
					break;
				case IF_YEAR: if (!matchesYear(ct)) return false;
					break;
			}
		}
		return true;
	}
	
	private boolean matchesAdult(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le instanceof Ageable)
		{
			boolean b = ((Ageable)le).isAdult();
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else if (le instanceof Zombie)
		{
			boolean b = !((Zombie)le).isBaby();
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_AN_AGEABLE_MOB, false);
			return false;
		}
	}
	
	private boolean matchesAge(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Ageable)
		{
			Integer i = ((Ageable)le).getAge();
			boolean b = matchesInt(i, needed);
			callConditionEvent(ct, needed, i, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_AN_AGEABLE_MOB, false);
			return false;
		}
	}
	
	private boolean matchesAngry(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le instanceof Wolf)
		{
			boolean b = ((Wolf)le).isAngry();
			callConditionEvent(ct, needed, b, b == needed);
			return b;
		}
		else if (le instanceof PigZombie)
		{
			boolean b = ((PigZombie)le).isAngry();
			callConditionEvent(ct, needed, b, b == needed);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_AN_ANGERABLE_MOB, false);
			return false;
		}
	}
	
	private boolean matchesArea(ConditionType ct, Block block)
	{
		String needed = block.getWorld().getName() + ":" + conditions.get(ct);

		Area area = Mobs.getExtraEvents().getArea(needed);
		boolean b = false;
		
		if (ev.getOrigEvent() instanceof PlayerLeaveAreaEvent)
		{
			b = ((PlayerLeaveAreaEvent)ev.getOrigEvent()).getArea() == area;
		}
		else if (ev.getOrigEvent() instanceof LivingEntityLeaveAreaEvent)
		{
			b = ((LivingEntityLeaveAreaEvent)ev.getOrigEvent()).getArea() == area;
		}
		else b = area != null && area.isInArea(block.getLocation());	
		
		if (ct.equals(ConditionType.IF_NOT_AREA)) b = !b;
		callConditionEvent(ct, needed, "", b);
		return b;
	}
	
	private boolean matchesAreaCount(ConditionType ct)
	{		
		String s = conditions.get(ConditionType.CONDITION_TARGET_AREA);
		Area area = null;
		if (s == null)
		{
			if (ev.getLivingEntity() == null) return false;
			
			for (Area a : Mobs.getExtraEvents().getAreas())
			{
				if (a.isInArea(ev.getLivingEntity().getLocation()))
				{
					area = a;
					break;
				}
			}
		}
		
		s = ev.getWorld().getName() + ":" + s;
		if (area == null) area = Mobs.getExtraEvents().getArea(s);
		
		if (area == null)
		{
			callConditionEvent(ct, s, ReasonType.NO_AREA, false);
			return false;
		}
		String[] mob = conditions.get(ConditionType.IF_AREA_COUNT_MOB).replace(" ", "").split(":");
		String needed = conditions.get(ct);
		int i = 0;
		
		for (LivingEntity le : getRelevantMobs(ev.getWorld().getEntities(), mob[0], mob.length > 1 ? mob[1] : null))
		{
			if (area.isInArea(le.getLocation())) i++;
		}
		
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed + ", " + s, i, b);
		return b;
	}
	
	private boolean matchesBiome(ConditionType ct, Block block)
	{
		String needed = conditions.get(ct);
		
		String s = null;
	    if (Bukkit.getPluginManager().isPluginEnabled("TerrainControl"))
	    {
	    	LocalWorld lw = TerrainControl.getWorld(ev.getWorld().getName());
	    	if (lw != null)
	    	{
	    		s = lw.getBiome(block.getX(), block.getZ()).getName();
	    	}
	    }

	    if (s == null) block.getBiome().name();
		boolean b = matchesString(s, needed);
		if (ct.equals(ConditionType.IF_NOT_BIOME)) b = !b;
		callConditionEvent(ct, needed, s, b);
		return b;
	}
	
	private boolean matchesBlockLightLevel(ConditionType ct, Block block)
	{
		String needed = conditions.get(ct);
		
		int i = block.getLightFromBlocks();
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesBlocksFromSpawn(ConditionType ct, Block block)
	{
		String needed = conditions.get(ct);
		
		int i = (int)block.getLocation().distance(block.getWorld().getSpawnLocation());
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesBlockType(ConditionType ct, Block block)
	{
		String needed = conditions.get(ct);
		
		boolean b = matchesBlock(block, needed);
		callConditionEvent(ct, needed, block.toString(), b);
		return b;
	}
		
	private boolean matchesBoots(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		ItemStack is = le.getEquipment().getBoots();
		boolean b = matchesItem(is, needed);
		if (ct.equals(ConditionType.IF_NOT_BOOTS)) b = !b;
		callConditionEvent(ct, needed, is.toString(), b);
		return b;
	}
	
	private boolean matchesCarrying(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Player)
		{		
			for (ItemStack is : ((Player)le).getInventory().getContents())
			{
				boolean b = matchesItem(is, needed);
				if (b)
				{
					if (ct.equals(ConditionType.IF_NOT_CARRYING)) b = !b;
					callConditionEvent(ct, needed, is.toString(), b);
					return b;
				}
			}
			return ct.equals(ConditionType.IF_NOT_CARRYING) ? true : false;			
		}
		
		callConditionEvent(ct, needed, ReasonType.NOT_A_PLAYER, false);
		return false;
	}
	
	private boolean matchesChestplate(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		ItemStack is = le.getEquipment().getChestplate();
		boolean b = matchesItem(is, needed);
		if (ct.equals(ConditionType.IF_NOT_CHESTPLATE)) b = !b;
		callConditionEvent(ct, needed, is.toString(), b);
		return b;
	}
	
	private boolean matchesChunkCount(ConditionType ct, LivingEntity le)
	{
		String[] mob = conditions.get(ConditionType.IF_CHUNK_COUNT_MOB).replace(" ", "").split(":");
		String needed = conditions.get(ct);
		
		int i = getRelevantMobs(Arrays.asList(le.getLocation().getChunk().getEntities()), mob[0], mob.length > 1 ? mob[1] : null).size();
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD_TYPE)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesCommand(ConditionType ct)
	{
		String needed = conditions.get(ct);
		if (!(ev.getOrigEvent() instanceof PlayerCommandEvent)) return false;
		
		String s = ((PlayerCommandEvent)ev.getOrigEvent()).getCommand();
		boolean b = matchesString(s, needed);
		if (ct.equals(ConditionType.IF_NOT_COMMAND)) b = !b;
		callConditionEvent(ct, needed, s, b);
		return b;
	}
	
	private boolean matchesCommandArgs(ConditionType ct)
	{
		String needed = conditions.get(ct);
		if (!(ev.getOrigEvent() instanceof PlayerCommandEvent)) return false;
		
		List<String> temp = ((PlayerCommandEvent)ev.getOrigEvent()).getArgs();
		int i = temp == null ? 0 : temp.size();
		
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesCurrentInvSlot(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Player)
		{		
			int i = ((Player)le).getInventory().getHeldItemSlot();
			boolean b = matchesInt(i, needed);
			callConditionEvent(ct, needed, i, b);
			return b;		
		}
		
		callConditionEvent(ct, needed, ReasonType.NOT_A_PLAYER, false);
		return false;
	}
	
	private boolean matchesCustomFlag(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		SubactionType st = SubactionType.valueOf(ct.toString().substring(3));
		if (Data.hasData(le, st))
		{
			boolean b = (Boolean)Data.getData(le, st);
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NO_METADATA, false);
			return false;
		}
	}
	
	private boolean matchesCustomInt(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		SubactionType st = SubactionType.valueOf(ct.toString().substring(3));
		if (Data.hasData(le, st))
		{
			Integer i = (Integer)Data.getData(le, st);
			boolean b = matchesInt(i, needed);
			callConditionEvent(ct, needed, i, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NO_METADATA, false);
			return false;
		}
	}
	
	private boolean matchesCustomString(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		SubactionType st = SubactionType.valueOf(ct.toString().substring(3));
		if (Data.hasData(le, st))
		{
			String s = (String)Data.getData(le, st);
			boolean b = matchesString(s, needed);
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NO_METADATA, false);
			return false;
		}
	}
		
	private boolean matchesDate(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Calendar.getInstance().get(Calendar.DATE);
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesDay(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesDayOfYear(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesHelmet(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		ItemStack is = le.getEquipment().getHelmet();
		boolean b = matchesItem(is, needed);
		if (ct.equals(ConditionType.IF_NOT_HELMET)) b = !b;
		callConditionEvent(ct, needed, is.toString(), b);
		return b;
	}
	
	private boolean matchesHolding(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		ItemStack is = le.getEquipment().getItemInHand();
		boolean b = matchesItem(is, needed);
		if (ct.equals(ConditionType.IF_NOT_HOLDING)) b = !b;
		callConditionEvent(ct, needed, is.toString(), b);
		return b;
	}
	
	private boolean matchesHour(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Calendar.getInstance().get(Calendar.HOUR);
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesKilledByPlayer(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le.isDead())
		{
			boolean b = le.getKiller() != null;
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_DEAD, false);
			return false;
		}
	}
	
	private boolean matchesLeggings(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		ItemStack is = le.getEquipment().getLeggings();
		boolean b = matchesItem(is, needed);
		if (ct.equals(ConditionType.IF_NOT_LEGGINGS)) b = !b;
		callConditionEvent(ct, needed, is.toString(), b);
		return b;
	}
	
	private boolean matchesLightLevel(ConditionType ct, Block block)
	{
		String needed = conditions.get(ct);
		
		int i = block.getLightLevel();
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesLunarPhase(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		long i = (ev.getWorld().getFullTime()/24000) % 8;
		boolean b = matchesInt((int)i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesMob(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		String s = le.getType().toString();
		boolean b = matchesString(s, needed);
		if (ct.equals(ConditionType.IF_NOT_MOB)) b = !b;
		callConditionEvent(ct, needed, s, b);
		return b;
	}

	private boolean matchesMonth(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Calendar.getInstance().get(Calendar.MONTH);
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesDeathCause(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le.isDead())
		{
			String s = le.getLastDamageCause().toString();	
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_DEATH_CAUSE)) b = !b;
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_DEAD, false);
			return false;
		}
	}
	
	private boolean matchesInWorld(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		World w = ev.getWorld();
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
		
		if (ct.equals(ConditionType.IF_NOT_IN_WORLD)) b = !b;
		callConditionEvent(ct, needed, w.getName() + "'s players", b);
		return b;
	}
	
	private boolean matchesMinute(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Calendar.getInstance().get(Calendar.MINUTE);
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesMoney(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Player)
		{
			if (Mobs.getEconomy() != null)
			{
				int i = (int)Mobs.getEconomy().getBalance(((Player)le).getName());				
				boolean b = matchesInt(i, needed);
				callConditionEvent(ct, needed, i, b);
				return b;
			}
			else
			{
				callConditionEvent(ct, needed, ReasonType.NO_VAULT, false);
				return false;
			}
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_PLAYER, false);
			return false;
		}
	}
	
	private boolean matchesName(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Player)
		{
			String s = ((Player)le).getName();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_NAME)) b = !b;
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		
		String s = le.getCustomName();
		boolean b = matchesString(s, needed);
		if (ct.equals(ConditionType.IF_NOT_NAME)) b = !b;
		callConditionEvent(ct, needed, s, b);
		return b;
	}
	
	private boolean matchesNameIsVisible(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		boolean b = le.isCustomNameVisible();
		callConditionEvent(ct, needed, b, b == needed);
		return b == needed;
	}	
		
	private boolean matchesNotCustomString(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		SubactionType st = SubactionType.valueOf(ct.toString().substring(3));
		if (Data.hasData(le, st))
		{
			String s = (String)Data.getData(le, st);
			boolean b = !matchesString(s, needed);
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NO_METADATA, false);
			return false;
		}
	}
	
	private boolean matchesOcelot(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Ocelot)
		{
			String s = ((Ocelot)le).getCatType().name();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_OCELOT)) b = !b;
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NO_TARGET, false);
			return false;
		}
	}
	
	private boolean matchesOnServer(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
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
		
		if (ct.equals(ConditionType.IF_NOT_ON_SERVER)) b = !b;
		callConditionEvent(ct, needed, "Server players", b);
		return b;
	}
	
	private boolean matchesOwner(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Tameable)
		{
			String s = ((Tameable)le).getOwner().getName();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_OWNER)) b = !b;
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_TAMEABLE_MOB, false);
			return false;
		}
	}
	
	private boolean matchesPercent(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = new Random().nextInt(101);
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesPlayerIsOp(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le instanceof Player)
		{
			boolean b = ((Player)le).isOp();
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_PLAYER, false);
			return false;
		}
	}
	
	private boolean matchesPlayerHasPermission(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Player)
		{
			boolean b = ((Player)le).hasPermission(needed);
			if (ct.equals(ConditionType.IF_NOT_PLAYER_HAS_PERMISSION)) b = !b;
			callConditionEvent(ct, needed, ((Player)le).getName() + "'s permissions", b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_PLAYER, false);
			return false;
		}
	}
	
	private boolean matchesPowered(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le instanceof Creeper)
		{
			boolean b = ((Creeper)le).isPowered();
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_CREEPER, false);
			return false;
		}
	}
	
	private boolean matchesProjectile(ConditionType ct)
	{
		String needed = conditions.get(ct);		
		Projectile p = ev.getProjectile();
		
		if (p != null) 
		{
			String s = p.getType().toString();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_PROJECTILE)) b = !b;
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		callConditionEvent(ct, needed, ReasonType.NO_PROJECTILE, false);
		return false;
	}
	
	private boolean matchesRaining(ConditionType ct)
	{
		boolean needed = getBool(conditions.get(ct));
		
		boolean b = ev.getWorld().hasStorm();
		callConditionEvent(ct, needed, "" + b, b == needed);
		return b == needed;
	}
	
	private boolean matchesRemainingLifetime(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		SubactionType st = SubactionType.MAX_LIFE;
		if (Data.hasData(le, st))
		{
			Integer i = (Integer)Data.getData(le, st);
			boolean b = matchesInt(i, needed);
			callConditionEvent(ct, needed, i, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NO_METADATA, false);
			return false;
		}
	}
	
	private boolean matchesSaddled(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le instanceof Pig)
		{
			boolean b = ((Pig)le).hasSaddle();
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_PIG, false);
			return false;
		}
	}
	
	private boolean matchesSecond(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Calendar.getInstance().get(Calendar.SECOND);
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesServerPlayerCount(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Bukkit.getOnlinePlayers().length;
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesSheared(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le instanceof Sheep)
		{
			boolean b = ((Sheep)le).isAdult();
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_SHEEP, false);
			return false;
		}
	}
	
	private boolean matchesSkeletonType(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le instanceof Skeleton)
		{
			boolean b = ((Skeleton)le).getSkeletonType().equals(SkeletonType.WITHER);
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_SKELETON, false);
			return false;
		}
	}
	
	private boolean matchesSkyLightLevel(ConditionType ct, Block block)
	{
		String needed = conditions.get(ct);
		
		int i = block.getLightFromSky();
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesSpawnReason(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (Data.hasData(le, SubactionType.SPAWN_REASON))
		{
			String s = (String)Data.getData(le, SubactionType.SPAWN_REASON);
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_SPAWN_REASON)) b = !b;
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NO_METADATA, false);
			return false;
		}
	}
	
	private boolean matchesStandingOn(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		Block block = le.getLocation().getBlock().getRelative(BlockFace.DOWN);
		
		boolean b = matchesBlock(block, needed);
		callConditionEvent(ct, needed, block.toString(), b);
		return b;
	}
	
	private boolean matchesTamed(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le instanceof Tameable)
		{
			boolean b = ((Tameable)le).isTamed();
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_TAMEABLE_MOB, false);
			return false;
		}
	}
	
	private boolean matchesThundering(ConditionType ct)
	{
		boolean needed = getBool(conditions.get(ct));
		
		boolean b = ev.getWorld().isThundering();
		callConditionEvent(ct, needed, "" + b, b == needed);
		return b == needed;
	}
	
	private boolean matchesTimer(ConditionType ct)
	{
		String needed = conditions.get(ct);		
		Timer t = ev.getTimer();
		
		if (t != null) 
		{
			String s = t.getName();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_TIMER)) b = !b;
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		callConditionEvent(ct, needed, ReasonType.NO_TIMER, false);
		return false;
	}
	
	private boolean matchesVillager(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Villager)
		{
			String s = ((Villager)le).getProfession().name();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_VILLAGER)) b = !b;
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_VILLAGER, false);
			return false;
		}
	}
	
	private boolean matchesWearing(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Player)
		{		
			Player p = (Player)le;
			ItemStack is = p.getInventory().getBoots();
			boolean b = matchesItem(is, needed);
			
			if (!b)
			{
				is = p.getInventory().getChestplate();
				b = matchesItem(is, needed);
			}
			
			if (!b)
			{
				is = p.getInventory().getHelmet();
				b = matchesItem(is, needed);
			}
			
			if (!b)
			{
				is = p.getInventory().getLeggings();
				b = matchesItem(is, needed);
			}

			if (ct.equals(ConditionType.IF_NOT_WEARING)) b = !b;
			callConditionEvent(ct, needed, p.getName() + "'s armour", b);
			return b;
		}
		
		callConditionEvent(ct, needed, ReasonType.NOT_A_PLAYER, false);
		return false;
	}
		
	private boolean matchesWeek(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesWeekOfMonth(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesWool(ConditionType ct, LivingEntity le)
	{
		String needed = conditions.get(ct);
		
		if (le instanceof Sheep)
		{
			String s = ((Sheep)le).getColor().name();
			boolean b = matchesString(s, needed);
			if (ct.equals(ConditionType.IF_NOT_WOOL)) b = !b;
			callConditionEvent(ct, needed, s, b);
			return b;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_SHEEP, false);
			return false;
		}
	}
	
	private boolean matchesWorld(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		String s = ev.getWorld().getName();
		boolean b = matchesString(s, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, s, b);
		return b;
	}
	
	private boolean matchesWorldCount(ConditionType ct)
	{
		String[] mob = conditions.get(ConditionType.IF_WORLD_COUNT_MOB).replace(" ", "").split(":");
		String needed = conditions.get(ct);
		
		int i = getRelevantMobs(ev.getWorld().getEntities(), mob[0], mob.length > 1 ? mob[1] : null).size();
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD_TYPE)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesWorldTime(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		long i = ev.getWorld().getTime();
		boolean b = matchesInt((int)i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesWorldType(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		String s = ev.getWorld().getEnvironment().toString();
		boolean b = matchesString(s, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD_TYPE)) b = !b;
		callConditionEvent(ct, needed, s, b);
		return b;
	}
	
	private boolean matchesX(ConditionType ct, Block block)
	{
		String needed = conditions.get(ct);
		
		int i = block.getX();
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesY(ConditionType ct, Block block)
	{
		String needed = conditions.get(ct);
		
		int i = block.getY();
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesYear(ConditionType ct)
	{
		String needed = conditions.get(ct);
		
		int i = Calendar.getInstance().get(Calendar.YEAR);
		boolean b = matchesInt(i, needed);
		if (ct.equals(ConditionType.IF_NOT_WORLD)) b = !b;
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesZ(ConditionType ct, Block block)
	{
		String needed = conditions.get(ct);
		
		int i = block.getZ();
		boolean b = matchesInt(i, needed);
		callConditionEvent(ct, needed, i, b);
		return b;
	}
	
	private boolean matchesZombieVillager(ConditionType ct, LivingEntity le)
	{
		boolean needed = getBool(conditions.get(ct));
		
		if (le instanceof Zombie)
		{
			boolean b = ((Zombie)le).isVillager();
			callConditionEvent(ct, needed, b, b == needed);
			return b == needed;
		}
		else
		{
			callConditionEvent(ct, needed, ReasonType.NOT_A_ZOMBIE, false);
			return false;
		}
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
	
	@SuppressWarnings("deprecation")
	private boolean matchesBlock(Block orig, String needed)
	{
		needed = needed.replace(" ", "").toUpperCase();
		int id = orig.getTypeId();
		int data = orig.getData();
		for (String s :needed.split(","))
		{
			if (s.contains(":"))
			{
				String[] temp = s.split(":");
				if (Integer.valueOf(temp[0]) == id && Integer.valueOf(temp[1]) == data) return true;
			}
			else if (Integer.valueOf(s) == id) return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	private boolean matchesItem(ItemStack orig, String needed)
	{
		if (orig == null) return false;
		
		needed = needed.replace(" ", "").toUpperCase();
		int id = orig.getTypeId();
		int data = orig.getData().getData();
		for (String s :needed.split(","))
		{
			if (s.contains(":"))
			{
				String[] temp = s.split(":");
				if (Enums.isMaterial(temp[0]))
				{
					return Material.valueOf(temp[0]).equals(orig.getType()) && Integer.valueOf(temp[1]) == data;
				}
				if (Integer.valueOf(temp[0]) == id && Integer.valueOf(temp[1]) == data) return true;
			}
			else
			{
				if (Enums.isMaterial(s)) return Material.valueOf(s).equals(orig.getType());
				if (Integer.valueOf(s) == id) return true;
			}
		}
		
		return false;
	}
	
	/** Returns either a LivingEntity or a Block, or null */
	private Object getConditionsTarget(EventValues ev)
	{		
		String condition_target = conditions.get(ConditionType.CONDITION_TARGET);
		
		String name = "";
		if (condition_target.contains(":"))
		{
			name = condition_target.split(":")[1];
			condition_target = condition_target.split(":")[0];
		}
		
		if (condition_target.startsWith("CLOSEST_"))
		{
			LivingEntity orig = ev.getLivingEntity();
			if (orig == null) return null;
			
			List<LivingEntity> mobs = getRelevantMobs(orig.getNearbyEntities(50, 10, 50), condition_target.replace("CLOSEST_", ""), name);
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
			List<LivingEntity> mobs = getRelevantMobs(w.getEntities(), condition_target.replace("RANDOM_", ""), name);
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
				if (conditions.containsKey(ConditionType.CONDITION_TARGET_BLOCK))
				{
					String[] temp = conditions.get(ConditionType.CONDITION_TARGET_BLOCK).replace(" ", "").split(":");
					return w.getBlockAt(Integer.valueOf(temp[0]), Integer.valueOf(temp[1]), Integer.valueOf(temp[2]));
				}
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
				if (name == null) return null;
				else return Bukkit.getPlayer(name);
				
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
	{
		s = s.toUpperCase();
		if (s == null) return false;
		if (s.equalsIgnoreCase("yes")) return true;
		if (s.equalsIgnoreCase("no")) return false;
		
		return Boolean.valueOf(s);
	}

	private List<LivingEntity> getRelevantMobs(List<Entity> orig, String m, String name)
	{		
		List<String> temp = m.equalsIgnoreCase("") ? null : Arrays.asList(m.replace(" ", "").toUpperCase().split(","));
		List<LivingEntity> mobs = new ArrayList<LivingEntity>();
		
		for (Entity e : orig)
		{
			if (!(e instanceof LivingEntity)) continue;
			
			if (temp != null && !temp.contains(e.getType().toString())) continue;
			
			if (name != null)
			{
				String s = e instanceof Player ? ((Player)e).getName() : (String)Data.getData(e, SubactionType.NAME);
				if (s == null || !s.equalsIgnoreCase(name)) continue;
			}
			mobs.add((LivingEntity)e);
		}
		return mobs;
	}
		
	/** Calls an event when a condition is checked */
	private void callConditionEvent(ConditionType ct, Object needed, Object got, boolean passed)
	{
		if (!Mobs.canDebug()) return;
		
		Bukkit.getServer().getPluginManager().callEvent(new MobsConditionEvent(ct.toString(), "" + needed, "" + got, passed, ev));
	}
}