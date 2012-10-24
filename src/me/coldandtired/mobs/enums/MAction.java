package me.coldandtired.mobs.enums;

public enum MAction 
{
	BROADCAST, 
	CANCEL_EVENT,
	CLEAR_DATA,
	CLEAR_DROPS,
	CLEAR_EXP,
	CLEAR_ITEMS,
	CLOSE_DOOR,
	CLOSE_GATE,
	CLOSE_TRAPDOOR,
	CONTINUE,
	DAMAGE,
	DESTROY_BLOCK,
	DROP_EXP,
	DROP_ITEM,  
	EXPLOSION,
	FIERY_EXPLOSION,
	GIVE_ITEM,
	//HEAL(amount)
	KILL,
	LIGHTNING,
	LIGHTNING_EFFECT,
	LOG,
	OPEN_DOOR,
	OPEN_GATE,
	OPEN_TRAPDOOR,
	PLAY_BLAZE_EFFECT,
	PLAY_BOW_EFFECT,
	PLAY_CLICK1_EFFECT,
	PLAY_CLICK2_EFFECT,
	PLAY_DOOR_EFFECT,
	PLAY_ENDER_EFFECT,
	PLAY_EXTINGUISH_EFFECT,
	PLAY_GHAST1_EFFECT,
	PLAY_GHAST2_EFFECT,
	PLAY_FLAMES_EFFECT,
	PLAY_POTION_EFFECT,
	PLAY_SMOKE_EFFECT,
	PLAY_STEP_EFFECT,
	PLAY_ZOMBIE1_EFFECT,
	PLAY_ZOMBIE2_EFFECT,
	PLAY_ZOMBIE3_EFFECT,
	PRESS_BUTTON,
	PULL_LEVER,
	PUSH_LEVER,
	RAIN,
	REMOVE,
	
	REMOVE_CUSTOM_FLAG_1,
	REMOVE_CUSTOM_FLAG_2,
	REMOVE_CUSTOM_FLAG_3,
	REMOVE_CUSTOM_FLAG_4,
	REMOVE_CUSTOM_FLAG_5,
	REMOVE_CUSTOM_FLAG_6,
	REMOVE_CUSTOM_FLAG_7,
	REMOVE_CUSTOM_FLAG_8,
	REMOVE_CUSTOM_FLAG_9,
	REMOVE_CUSTOM_FLAG_10,
	REMOVE_CUSTOM_INT_1,
	REMOVE_CUSTOM_INT_2,
	REMOVE_CUSTOM_INT_3,
	REMOVE_CUSTOM_INT_4,
	REMOVE_CUSTOM_INT_5,
	REMOVE_CUSTOM_INT_6,
	REMOVE_CUSTOM_INT_7,
	REMOVE_CUSTOM_INT_8,
	REMOVE_CUSTOM_INT_9,
	REMOVE_CUSTOM_INT_10,
	REMOVE_CUSTOM_STRING_1,
	REMOVE_CUSTOM_STRING_2,
	REMOVE_CUSTOM_STRING_3,
	REMOVE_CUSTOM_STRING_4,
	REMOVE_CUSTOM_STRING_5,
	REMOVE_CUSTOM_STRING_6,
	REMOVE_CUSTOM_STRING_7,
	REMOVE_CUSTOM_STRING_8,
	REMOVE_CUSTOM_STRING_9,
	REMOVE_CUSTOM_STRING_10,
	
	REMOVE_ITEM,
	REMOVE_MAX_LIFE,
	RESTORE_SKIN,
	SEND_MESSAGE,
	SET_ADULT_NO,
	SET_ADULT_RANDOM,
	SET_ADULT_YES,
	SET_ANGRY_NO,
	SET_ANGRY_RANDOM,
	SET_ANGRY_YES,
	SET_ATTACK_POWER,
	SET_BLOCK,
	SET_CAN_BE_DYED_NO,
	SET_CAN_BE_DYED_RANDOM,
	SET_CAN_BE_DYED_YES,
	SET_CAN_BE_SADDLED_NO,
	SET_CAN_BE_SADDLED_RANDOM,
	SET_CAN_BE_SADDLED_YES,
	SET_CAN_BE_SHEARED_NO,
	SET_CAN_BE_SHEARED_RANDOM,
	SET_CAN_BE_SHEARED_YES,
	SET_CAN_BE_TAMED_NO,
	SET_CAN_BE_TAMED_RANDOM,
	SET_CAN_BE_TAMED_YES,
	//SET_CAN_BREED,
	SET_CAN_BURN_NO,
	SET_CAN_BURN_RANDOM,
	SET_CAN_BURN_YES,
	SET_CAN_CREATE_PORTALS_NO,
	SET_CAN_CREATE_PORTALS_RANDOM,
	SET_CAN_CREATE_PORTALS_YES,
	SET_CAN_DESTROY_BLOCKS_NO,
	SET_CAN_DESTROY_BLOCKS_RANDOM,
	SET_CAN_DESTROY_BLOCKS_YES,
	SET_CAN_EVOLVE_NO,
	SET_CAN_EVOLVE_RANDOM,
	SET_CAN_EVOLVE_YES,
	SET_CAN_GRAZE_NO,
	SET_CAN_GRAZE_RANDOM,
	SET_CAN_GRAZE_YES,
	SET_CAN_GROW_WOOL_NO,
	SET_CAN_GROW_WOOL_RANDOM,
	SET_CAN_GROW_WOOL_YES,
	SET_CAN_HEAL_NO,
	SET_CAN_HEAL_RANDOM,
	SET_CAN_HEAL_YES,
	SET_CAN_MOVE_BLOCKS_NO,
	SET_CAN_MOVE_BLOCKS_RANDOM,
	SET_CAN_MOVE_BLOCKS_YES,
	SET_CAN_OVERHEAL_NO,
	SET_CAN_OVERHEAL_RANDOM,
	SET_CAN_OVERHEAL_YES,	
	SET_CAN_PICK_UP_ITEMS_NO,
	SET_CAN_PICK_UP_ITEMS_RANDOM,
	SET_CAN_PICK_UP_ITEMS_YES,
	SET_CAN_TELEPORT_NO,
	SET_CAN_TELEPORT_RANDOM,
	SET_CAN_TELEPORT_YES,	
	
	SET_CUSTOM_FLAG_1,
	SET_CUSTOM_FLAG_2,
	SET_CUSTOM_FLAG_3,
	SET_CUSTOM_FLAG_4,
	SET_CUSTOM_FLAG_5,
	SET_CUSTOM_FLAG_6,
	SET_CUSTOM_FLAG_7,
	SET_CUSTOM_FLAG_8,
	SET_CUSTOM_FLAG_9,
	SET_CUSTOM_FLAG_10,
	SET_CUSTOM_INT_1,
	SET_CUSTOM_INT_2,
	SET_CUSTOM_INT_3,
	SET_CUSTOM_INT_4,
	SET_CUSTOM_INT_5,
	SET_CUSTOM_INT_6,
	SET_CUSTOM_INT_7,
	SET_CUSTOM_INT_8,
	SET_CUSTOM_INT_9,
	SET_CUSTOM_INT_10,
	SET_CUSTOM_STRING_1,
	SET_CUSTOM_STRING_2,
	SET_CUSTOM_STRING_3,
	SET_CUSTOM_STRING_4,
	SET_CUSTOM_STRING_5,
	SET_CUSTOM_STRING_6,
	SET_CUSTOM_STRING_7,
	SET_CUSTOM_STRING_8,
	SET_CUSTOM_STRING_9,
	SET_CUSTOM_STRING_10,	
	
	SET_DAMAGE_TAKEN_FROM_BLOCK_EXPLOSION,
	SET_DAMAGE_TAKEN_FROM_CONTACT,
	SET_DAMAGE_TAKEN_FROM_CUSTOM,
	SET_DAMAGE_TAKEN_FROM_DROWNING,
	SET_DAMAGE_TAKEN_FROM_ATTACK,
	SET_DAMAGE_TAKEN_FROM_ENTITY_EXPLOSION,
	SET_DAMAGE_TAKEN_FROM_FALL,
	SET_DAMAGE_TAKEN_FROM_FIRE,
	SET_DAMAGE_TAKEN_FROM_FIRE_TICK,
	SET_DAMAGE_TAKEN_FROM_LAVA,
	SET_DAMAGE_TAKEN_FROM_LIGHTNING,
	SET_DAMAGE_TAKEN_FROM_MAGIC,
	SET_DAMAGE_TAKEN_FROM_MELTING,
	SET_DAMAGE_TAKEN_FROM_POISON,
	SET_DAMAGE_TAKEN_FROM_PROJECTILE,
	SET_DAMAGE_TAKEN_FROM_STARVATION,
	SET_DAMAGE_TAKEN_FROM_SUFFOCATION,
	SET_DAMAGE_TAKEN_FROM_SUICIDE,
	SET_DAMAGE_TAKEN_FROM_VOID,	
	SET_EXP,
	SET_EXPLOSION_SIZE,
	SET_FIERY_EXPLOSION_NO,
	SET_FIERY_EXPLOSION_RANDOM,
	SET_FIERY_EXPLOSION_YES,
	SET_FRIENDLY_NO,
	SET_FRIENDLY_RANDOM,
	SET_FRIENDLY_YES,
	SET_HP,
	SET_LEVEL,
	SET_MAX_HP,
	SET_MAX_LIFE,
	SET_MONEY,
	SET_NAME,
	SET_OWNER,
	SET_POWERED_NO,
	SET_POWERED_RANDOM,
	SET_POWERED_YES,
	SET_SADDLED_NO,
	SET_SADDLED_RANDOM,
	SET_SADDLED_YES,
	SET_SHEARED_NO,
	SET_SHEARED_RANDOM,
	SET_SHEARED_YES,
	SET_SKIN,
	SET_TAMED_NO,
	SET_TAMED_RANDOM,
	SET_TAMED_YES,
	SET_TIME,
	SET_TITLE,
	SET_WOOL_BLACK,
	SET_WOOL_BLUE,
	SET_WOOL_BROWN,
	SET_WOOL_CYAN,
	SET_WOOL_GRAY,
	SET_WOOL_GREEN,
	SET_WOOL_LIGHT_BLUE,
	SET_WOOL_LIME,
	SET_WOOL_MAGENTA,
	SET_WOOL_ORANGE,
	SET_WOOL_PINK,
	SET_WOOL_PURPLE,
	SET_WOOL_RANDOM,
	SET_WOOL_RED,
	SET_WOOL_SILVER,
	SET_WOOL_WHITE,
	SET_WOOL_YELLOW,
	SPAWN_MOB,
	STORM,
	SUN,
	TOGGLE_ADULT,
	TOGGLE_ANGRY,
	TOGGLE_CAN_BE_DYED,
	TOGGLE_CAN_BE_SADDLED,
	TOGGLE_CAN_BE_SHEARED,
	TOGGLE_CAN_BE_TAMED,
	TOGGLE_CAN_BURN,
	TOGGLE_CAN_CREATE_PORTALS,
	TOGGLE_CAN_DESTROY_BLOCKS,
	TOGGLE_CAN_EVOLVE,
	TOGGLE_CAN_GRAZE,
	TOGGLE_CAN_GROW_WOOL,
	TOGGLE_CAN_HEAL,
	TOGGLE_CAN_MOVE_BLOCKS,
	TOGGLE_CAN_OVERHEAL,
	TOGGLE_CAN_PICK_UP_ITEMS,
	TOGGLE_CAN_TELEPORT,
	TOGGLE_DOOR,
	TOGGLE_FIERY_EXPLOSION,
	TOGGLE_FRIENDLY,
	TOGGLE_GATE,
	TOGGLE_LEVER,
	TOGGLE_POWERED,
	TOGGLE_SADDLED,
	TOGGLE_SHEARED,
	TOGGLE_TAMED,
	TOGGLE_TRAPDOOR;
}