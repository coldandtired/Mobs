package me.coldandtired.mobs.enums;

public enum MCondition 
{
	ADULT, 
	ANGRY,
	AREA,
	//AREA_MOB_COUNT,
	//ATTACKER_TYPE
	BIOME,
	BLOCK_LIGHT_LEVEL,
	CHUNK_MOB_COUNT,
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
	//CAN_BREED,
	DEATH_CAUSE,
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
	KILLED_BY_PLAYER,
	//KILLER_NAME,
	LIGHT_LEVEL,
	//LOCAL_TIME,
	LUNAR_PHASE,
	//MOB_AGE,
	NAME,
	NOT_ADULT,
	NOT_ANGRY,
	NOT_AREA,
	NOT_BIOME,
	NOT_CUSTOM_STRING_1,
	NOT_CUSTOM_STRING_2,
	NOT_CUSTOM_STRING_3,
	NOT_CUSTOM_STRING_4,
	NOT_CUSTOM_STRING_5,
	NOT_CUSTOM_STRING_6,
	NOT_CUSTOM_STRING_7,
	NOT_CUSTOM_STRING_8,
	NOT_CUSTOM_STRING_9,
	NOT_CUSTOM_STRING_10,
	NOT_DEATH_CAUSE,
	NOT_CUSTOM_FLAG_1,
	NOT_CUSTOM_FLAG_2,
	NOT_CUSTOM_FLAG_3,
	NOT_CUSTOM_FLAG_4,
	NOT_CUSTOM_FLAG_5,
	NOT_CUSTOM_FLAG_6,
	NOT_CUSTOM_FLAG_7,
	NOT_CUSTOM_FLAG_8,
	NOT_CUSTOM_FLAG_9,
	NOT_CUSTOM_FLAG_10,
	NOT_KILLED_BY_PLAYER,
	NOT_NAME,
	NOT_OCELOT_TYPE,
	NOT_OWNER,
	NOT_PLAYER_HAS_PERMISSION,
	NOT_PLAYER_IS_OP,
	NOT_POWERED,
	NOT_RAINING,
	NOT_SADDLED,
	NOT_SHEARED,
	NOT_SPAWN_REASON,
	NOT_TAMED,
	NOT_THUNDERING,
	NOT_VILLAGER_TYPE,
	NOT_WORLD_NAME,
	OCELOT_TYPE,
	OWNER,
	PLAYER_HAS_PERMISSION,
	PLAYER_IS_OP,
	//MOB_NOT_STANDING_ON,
	//MOB_STANDING_ON,
	//OCELOT_TYPE,
	//ONLINE_PLAYER_COUNT,
	//PERCENT,
	//PLAYER_HOLDING,
	//PLAYER_ITEM,
	//PLAYER_MONEY,
	//PLAYER_PERMISSION,
	//PLAYER_WEARING,
	POWERED,
	RAINING,
	//REGION_MOB_COUNT,
	//REMAINING_LIFETIME,
	SADDLED,
	SHEARED,
	SPAWN_REASON,
	SKY_LIGHT_LEVEL,
	TAMED,
	THUNDERING,
	//time conditions
	//WOOL_COLORS,
	VILLAGER_TYPE,
	WORLD_MOB_COUNT,
	WORLD_NAME, 
	//WORLD_PLAYER,
	WORLD_TIME,
	//WORLD_TYPE,
	X,
	Y,
	Z;
	
	public static String getXpath()
	{
		String s = "";
		for (MCondition c : values()) s = s + " | " + c.toString().toLowerCase();
		return s.substring(3);
	}
}