package me.coldandtired.mobs.enums;

public enum MTarget 
{
	AREA,
	AROUND,
	BLOCK,
	NEAREST,
	PLAYER,
	RANDOM,
	SECONDARY_MOB,
	SELF;
	
	public static String getXpath()
	{
		String s = "";
		for (MTarget t : values()) s = s + " | " + t.toString().toLowerCase();
		return s.substring(3);
	}
}