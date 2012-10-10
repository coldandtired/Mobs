package me.coldandtired.mobs.enums;

public enum Mobs_target 
{
	AREA,
	BLOCK,
	NEAR,
	NEAREST,
	PLAYER,
	RANDOM,
	SELF;
	
	public static String getXpath()
	{
		String s = "";
		for (Mobs_target t : values()) s = s + " | " + t.toString().toLowerCase();
		return s.substring(3);
	}
}