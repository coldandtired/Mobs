package me.coldandtired.mobs.enums;

public enum MAction 
{
	BREAK,
	CANCEL_EVENT,
	CAUSE,
	CONTINUE,
	DAMAGE,
	DROP,
	GIVE,
	KILL,
	PLAY,
	REMOVE,
	SET,
	SPAWN,
	TOGGLE,
	WRITE,
	
	/* 
	//HEAL(amount)
	PRESS_BUTTON,
	//SET_CAN_BREED,
	SET_FIERY_EXPLOSION_NO,*/;

	
	public static String getXpath()
	{
		String s = "";
		for (MAction a : values()) s = s + " | " + a.toString().toLowerCase();
		return s.substring(3);
	}
}