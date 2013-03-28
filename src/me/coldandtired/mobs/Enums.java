package me.coldandtired.mobs;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class Enums
{
	public enum EventType
	{//ATTACKED,
		BLOCKS,
		BURNS,
		CHANGES_BLOCK,
		CREATES_PORTAL,
		DAMAGED,
		DAWN,
		DIES,
		DUSK,
		DYED,
		ENTERS_AREA,
		EVOLVES,
		EXPLODES,
		GROWS_WOOL,
		HEALS,
		HIT,
		HIT_BY_PROJECTILE,
		HOUR_CHANGE,
		IN_AREA,
		LEAVES,
		LEAVES_AREA,
		MIDDAY,
		MIDNIGHT,
		NIGHT,
		PICKS_UP_ITEM,
		PLAYER_APPROACHES,
		PLAYER_DIES,
		PLAYER_JOINS,
		PLAYER_NEAR,
		PLAYER_SPAWNS,
		PLAYER_TARGETED,
		SHEARED,
		SPAWNS,
		SPLITS,
		TAMED,
		TARGETS,
		TELEPORTS,
		TIMER
	}
	
	enum ElementType 
	{  
		ACTION,
		AMOUNT,
		AMOUNT_TYPE,
		CONDITION,
		DURATION,
		EFFECT,
		ENCHANTMENT, 
		ENCHANTMENT_LEVEL, 
		ITEM, 
		ITEM_DATA, 
		MESSAGE,
		MOB, 		
		MOB_NAME,
		SIZE,
		SOUND,
		SOUND_PITCH,
		SOUND_VOLUME,
		SUB, 
		TARGET,
		TARGET_AMOUNT,
		TARGET_NAME,
		TARGET_X_OFFSET,
		TARGET_Y_OFFSET,
		TARGET_Z_OFFSET,
		VALUE,
		X, 
		Y, 
		Z 
	}
	
	enum ActionType
	{
		//ACTIVATE_BUTTON,
		//TODO Bukkit bug
		BROADCAST,
		CANCEL_EVENT,
		CAUSE,
		DAMAGE,
		GIVE,
		KILL,
		LOG,
		REMOVE,
		RESET,
		SET,
		SPAWN,
		TELL
	}
	
	enum NumberType
	{
		ABSOLUTE,
		DEC,
		DEC_PERCENT,
		INC,
		INC_PERCENT,
		PERCENT,
	}
	
	enum SubactionType
	{
		ADULT,
		ALL_DATA,
		ALL_DROPS,
	    ALL_ITEMS,
		ANGRY,
		ATTACK_POWER,
		BLOCK,
		CUSTOM_DROPS,
		CUSTOM_FLAG_1,
		CUSTOM_FLAG_2,
		CUSTOM_FLAG_3,
		CUSTOM_FLAG_4,
		CUSTOM_FLAG_5,
		CUSTOM_FLAG_6,
		CUSTOM_FLAG_7,
		CUSTOM_FLAG_8,
		CUSTOM_FLAG_9,
		CUSTOM_FLAG_10,
		CUSTOM_INT_1,
		CUSTOM_INT_2,
		CUSTOM_INT_3,
		CUSTOM_INT_4,
		CUSTOM_INT_5,
		CUSTOM_INT_6,
		CUSTOM_INT_7,
		CUSTOM_INT_8,
		CUSTOM_INT_9,
		CUSTOM_INT_10,
		CUSTOM_STRING_1,
		CUSTOM_STRING_2,
		CUSTOM_STRING_3,
		CUSTOM_STRING_4,
		CUSTOM_STRING_5,
		CUSTOM_STRING_6,
		CUSTOM_STRING_7,
		CUSTOM_STRING_8,
		CUSTOM_STRING_9,
		CUSTOM_STRING_10,
		DAMAGE_FROM_BLOCK_EXPLOSION,
		DAMAGE_FROM_CONTACT, 
		DAMAGE_FROM_CUSTOM,
		DAMAGE_FROM_DROWNING,
		DAMAGE_FROM_ENTITY_ATTACK,
		DAMAGE_FROM_ENTITY_EXPLOSION,
		DAMAGE_FROM_FALL,
		DAMAGE_FROM_FALLING_BLOCK,
		DAMAGE_FROM_FIRE,
		DAMAGE_FROM_FIRE_TICK,
		DAMAGE_FROM_LAVA,
		DAMAGE_FROM_LIGHTNING,
		DAMAGE_FROM_MAGIC,
		DAMAGE_FROM_MELTING,
		DAMAGE_FROM_POISON,
		DAMAGE_FROM_PROJECTILE,
		DAMAGE_FROM_STARVATION,
		DAMAGE_FROM_SUFFOCATION,
		DAMAGE_FROM_SUICIDE,
		DAMAGE_FROM_VOID,
		DAMAGE_FROM_WITHER,
		DEFAULT_DROPS,
		DROPPED_EXP,		
		EFFECT,
	    EXP,
	    EXPLOSION,
		EXPLOSION_SIZE,
	    FIERY_EXPLOSION,
	    FRIENDLY,
	    HP,
	    ITEM,
	    LEVEL,
	    LIGHTNING,
	    LIGHTNING_EFFECT,
	    MAX_HP,   
	    MAX_LIFE,
	    MOB,
	    MONEY,
	    NAME,
	    NO_BURN,	
		NO_CREATE_PORTALS,
		NO_DEFAULT_DROPS,
		NO_DESTROY_BLOCKS,
		NO_DROPS,
		NO_DYED,
		NO_EVOLVE,		
		NO_FIERY_EXPLOSION,	
		NO_GRAZE,
		NO_GROW_WOOL,
		NO_HEAL,
		NO_MOVE_BLOCKS,
		NO_PICK_UP_ITEMS,
		NO_SADDLED,
		NO_SHEARING,
		NO_TAMING,
		NO_TELEPORT,    
	    OCELOT,
		OPEN,
		OWNER,
		POWERED,
		SADDLED,
		SHEARED,
		SIZE,
		SKELETON,
	    SKIN,
	    SOUND,
	    SPAWN_REASON,
	    SPLIT_INTO,
		TAMED,
		TIME,
		TITLE,
		VILLAGER,
		WEATHER,
		WOOL
    }
		
	enum TargetType
	{ 
		APPROACHED_PLAYER,
		AREA,
		AROUND,
		ATTACKER,
		BLOCK,
		CLOSEST,
		CLOSEST_BAT,
		CLOSEST_BLAZE,
		CLOSEST_CAVE_SPIDER,
		CLOSEST_CHICKEN,
		CLOSEST_COW,
		CLOSEST_CREEPER,
		CLOSEST_ENDER_DRAGON,
		CLOSEST_ENDERMAN,
		CLOSEST_GHAST,
		CLOSEST_GIANT,
		CLOSEST_GOLEM,
		CLOSEST_IRON_GOLEM,
		CLOSEST_MAGMA_CUBE,
		CLOSEST_MUSHROOM_COW,
		CLOSEST_OCELOT,
		CLOSEST_PIG,
		CLOSEST_PIG_ZOMBIE,
		CLOSEST_PLAYER,
		CLOSEST_SILVERFISH,
		CLOSEST_SHEEP,
		CLOSEST_SKELETON,
		CLOSEST_SLIME,
		CLOSEST_SNOWMAN,
		CLOSEST_SPIDER,
		CLOSEST_SQUID,
		CLOSEST_VILLAGER,
		CLOSEST_WITCH,
		CLOSEST_WITHER,
		CLOSEST_WOLF,
		CLOSEST_ZOMBIE,
		KILLER,
		LEFT_PLAYER,
		NEAR_PLAYER,
		PLAYER,
		RANDOM,
		RANDOM_BAT,
		RANDOM_BLAZE,
		RANDOM_CAVE_SPIDER,
		RANDOM_CHICKEN,
		RANDOM_COW,
		RANDOM_CREEPER,
		RANDOM_ENDER_DRAGON,
		RANDOM_ENDERMAN,
		RANDOM_GHAST,
		RANDOM_GIANT,
		RANDOM_GOLEM,
		RANDOM_IRON_GOLEM,
		RANDOM_MAGMA_CUBE,
		RANDOM_MUSHROOM_COW,
		RANDOM_OCELOT,
		RANDOM_PIG,
		RANDOM_PIG_ZOMBIE,
		RANDOM_PLAYER,
		RANDOM_SILVERFISH,
		RANDOM_SHEEP,
		RANDOM_SKELETON,
		RANDOM_SLIME,
		RANDOM_SNOWMAN,
		RANDOM_SPIDER,
		RANDOM_SQUID,
		RANDOM_VILLAGER,
		RANDOM_WITCH,
		RANDOM_WITHER,
		RANDOM_WOLF,
		RANDOM_ZOMBIE,
		OWNER,
		SELF,
		SHEARER,
		TAMER,
		TARGETED,
	}
	
	enum ValueType
	{
		FALSE,
		NO,
		RAINY,
		RANDOM,
		STORMY,
		SUNNY,
		TOGGLED,
		TRUE,
		YES
	}
	
	enum ConditionType
	{
		CONDITION_TARGET,
		CONDITION_TARGET_AREA,
		CONDITION_TARGET_BLOCK,
		IF_ADULT,
		IF_AGE,
		IF_ANGRY,
		IF_AREA,
		IF_AREA_COUNT,
		IF_BIOME,
		IF_BLOCK,
		IF_BLOCK_LIGHT_LEVEL,
		IF_BLOCKS_FROM_SPAWN,
		IF_CARRYING,
		IF_CHUNK_COUNT,
		IF_CUSTOM_FLAG_1,
		IF_CUSTOM_FLAG_2,
		IF_CUSTOM_FLAG_3,
		IF_CUSTOM_FLAG_4,
		IF_CUSTOM_FLAG_5,
		IF_CUSTOM_FLAG_6,
		IF_CUSTOM_FLAG_7,
		IF_CUSTOM_FLAG_8,
		IF_CUSTOM_FLAG_9,
		IF_CUSTOM_FLAG_10,
		IF_CUSTOM_INT_1,
		IF_CUSTOM_INT_2,
		IF_CUSTOM_INT_3,
		IF_CUSTOM_INT_4,
		IF_CUSTOM_INT_5,
		IF_CUSTOM_INT_6,
		IF_CUSTOM_INT_7,
		IF_CUSTOM_INT_8,
		IF_CUSTOM_INT_9,
		IF_CUSTOM_INT_10,
		IF_CUSTOM_STRING_1,
		IF_CUSTOM_STRING_2,
		IF_CUSTOM_STRING_3,
		IF_CUSTOM_STRING_4,
		IF_CUSTOM_STRING_5,
		IF_CUSTOM_STRING_6,
		IF_CUSTOM_STRING_7,
		IF_CUSTOM_STRING_8,
		IF_CUSTOM_STRING_9,
		IF_CUSTOM_STRING_10,
		IF_DATE,
		IF_DAY,
		IF_DAY_OF_YEAR,
		IF_DEATH_CAUSE,
		IF_HOLDING,
		IF_HOUR,
		IF_IN_WORLD,
		IF_KILLED_BY_PLAYER,
		IF_LIGHT_LEVEL,
		IF_LUNAR_PHASE,
		IF_MINUTE,
		IF_MOB,
		IF_MONEY,
		IF_MONTH,
		IF_NAME,
		IF_NOT_AREA,
		IF_NOT_BIOME,
		IF_NOT_BLOCK,
		IF_NOT_CARRYING,
		IF_NOT_CUSTOM_STRING_1,
		IF_NOT_CUSTOM_STRING_2,
		IF_NOT_CUSTOM_STRING_3,
		IF_NOT_CUSTOM_STRING_4,
		IF_NOT_CUSTOM_STRING_5,
		IF_NOT_CUSTOM_STRING_6,
		IF_NOT_CUSTOM_STRING_7,
		IF_NOT_CUSTOM_STRING_8,
		IF_NOT_CUSTOM_STRING_9,
		IF_NOT_CUSTOM_STRING_10,
		IF_NOT_DEATH_CAUSE,
		IF_NOT_HOLDING,
		IF_NOT_IN_WORLD,
		IF_NOT_MOB,
		IF_NOT_NAME,
		IF_NOT_OCELOT,
		IF_NOT_ON_SERVER,
		IF_NOT_OWNER,
		IF_NOT_PLAYER_HAS_PERMISSION,
		IF_NOT_PROJECTILE,
		IF_NOT_SPAWN_REASON,
		IF_NOT_STANDING_ON,
		IF_NOT_TIMER,
		IF_NOT_VILLAGER,
		IF_NOT_WEARING,
		IF_NOT_WOOL,
		IF_NOT_WORLD,
		IF_NOT_WORLD_TYPE,
		IF_OCELOT,
		IF_ON_SERVER,
		IF_OWNER,
		IF_PERCENT,
		IF_PLAYER_HAS_PERMISSION,
		IF_PLAYER_IS_OP,
		IF_POWERED,
		IF_PROJECTILE,
		IF_RAINING,
		IF_REMAINING_LIFETIME,
		IF_SADDLED,
		IF_SECOND,
		IF_SERVER_PLAYER_COUNT,
		IF_SHEARED,
		IF_SKY_LIGHT_LEVEL,
		IF_SPAWN_REASON,
		IF_STANDING_ON,
		IF_TAMED,
		IF_THUNDERING,
		IF_TIMER,
		IF_VILLAGER,
		IF_WEARING,
		IF_WEEK,
		IF_WEEK_OF_MONTH,
		IF_WOOL,
		IF_WORLD,
		IF_WORLD_COUNT,
		IF_WORLD_TIME,
		IF_WORLD_TYPE,
		IF_X,
		IF_Y,
		IF_YEAR,
		IF_Z
	}
	
	public enum ReasonType
	{
		BAD_AMOUNT,
		BAD_ITEM_ID,
		CANNOT_BE_OPENED,
		CANNOT_CANCEL_EVENT,
		//CHUNK_NOT_LOADED,
		NO_ACTION,
		NO_AREA,
		NO_EFFECT,
		NO_ITEM,
		NO_LOCATION,
		NO_MATCHING_TARGET,
		NO_MESSAGE,
		NO_METADATA,
		NO_MOB,
		NO_PLAYER,
		NO_PROJECTILE,
		NO_SOUND,
		NO_SPOUT,
		NO_SUBACTION,
		NO_TARGET,
		NO_TIMER,
		NO_VALUE,
		NO_VAULT,
		NO_X,
		NO_Y,
		NO_Z,
		NOT_A_BUTTON,
		NOT_A_CREEPER,
		NOT_A_PIG,
		NOT_A_PLAYER,
		NOT_A_SHEEP,
		NOT_A_TAMEABLE_MOB,
		NOT_A_VILLAGER,
		NOT_AN_AGEABLE_MOB,
		NOT_AN_ANGERABLE_MOB,
		NOT_AN_EVENT_WITH_AN_ATTACKER,
		NOT_DEAD,
		NOT_THE_APPROACHES_EVENT,
		NOT_THE_DIES_EVENT,
		NOT_THE_LEAVES_EVENT,
		NOT_THE_NEAR_EVENT,
		NOT_THE_SHEARS_EVENT,
		NOT_THE_TAMES_EVENT,
		NOT_THE_TARGETS_EVENT,
		Y_EXCEEDS_MAX_HEIGHT
	}

	public static boolean isActionType(String s)
	{
		for (ActionType at : ActionType.values())
		{
			if (at.toString().equalsIgnoreCase(s)) return true;
		}
		return false;
	}
	
	public static boolean isEnchantment(String s)
	{
		for (Enchantment e : Enchantment.values())
		{
			if (e.getName().equalsIgnoreCase(s)) return true;
		}
		
		return false;
	}
	
 	public static boolean isTargetType(String s)
	{
		for (TargetType tt : TargetType.values())
		{
			if (tt.toString().equalsIgnoreCase(s)) return true;
		}
		return false;
	}
	
	public static boolean isMaterial(String s)
	{
		for (Material m : Material.values())
		{
			if (m.toString().equalsIgnoreCase(s)) return true;
		}
		return false;
	}
	
//HP_PER_SIZE,
//INVINCIBILITY_TICKS,
}