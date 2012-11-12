package me.coldandtired.mobs.enums;

public enum MTarget 
{
	AREA,
	AROUND,
	AUX_MOB,
	BLOCK,
	NEAREST,
	PLAYER,
	RANDOM,
	SELF;
	
	public static String getXpath()
	{
		String s = "";
		for (MTarget t : values()) s = s + " | " + t.toString().toLowerCase();
		return s.substring(3);
	}
}