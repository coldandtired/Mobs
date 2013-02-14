package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.xml.xpath.XPathExpressionException;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.EntitySkinType;
import org.w3c.dom.Element;
import me.coldandtired.extra_events.LivingEntityBlockEvent;
import me.coldandtired.extra_events.LivingEntityDamageEvent;
import me.coldandtired.extra_events.PlayerApproachLivingEntityEvent;
import me.coldandtired.extra_events.PlayerLeaveLivingEntityEvent;
import me.coldandtired.extra_events.PlayerNearLivingEntityEvent;
import me.coldandtired.mobs.events.MobsFailedActionEvent;
import me.coldandtired.mobs.events.MobsPerformingActionEvent;
import me.coldandtired.mobs.Enums.*;

public class MobsOutcome extends MobsElement
{	
	private String event_name;
	private EventValues ev;
	private MobsElement ce;
	private Object target;
	private List<String> affected_mobs;
	private List<String> affected_worlds;
	
	MobsOutcome(String event_name, Element element) throws XPathExpressionException
	{
		super(element, null);
		ce = this;
		if (element.hasAttribute("affected_mobs"))
		{
			affected_mobs = new ArrayList<String>();
			String s = element.getAttribute("affected_mobs").toUpperCase().replace(" ", "");
			affected_mobs = Arrays.asList(s.split(","));
		}

		if (element.hasAttribute("affected_worlds"))
		{
			affected_worlds = new ArrayList<String>();
			String s = element.getAttribute("affected_worlds").toUpperCase().replace(" ", "");
			affected_worlds = Arrays.asList(s.split(","));
		}
		this.event_name = event_name;
	}
	
	/** Performs all the actions on all the targets */
	public void performActions(EventValues ev)
	{
		this.ev = ev;
		if (!isAffected()) return;
		
		target = getMCTarget();
		
		List<MobsElement> actions = getActions();
		if (actions == null)
		{
			actionFailed(null, ReasonType.NO_ACTION);
			return;
		}
		
		for (MobsElement me : actions)
		{		
			ce = me;
			ActionType at = getAction();
			if (!at.equals(ActionType.CANCEL_EVENT) && target == null)
			{
				actionFailed(at.toString(), ReasonType.NO_TARGET);
				continue;
			}
			
			switch (at)
			{					
				case CANCEL_EVENT: cancelEvent();
					break;			
				case CAUSE: causeSomething();
					break;					
				case DAMAGE: damageSomething();
					break;					
				case GIVE: giveSomething();
					break;						
				case PLAY: playSomething();
					break;				
				case REMOVE: removeSomething();
					break;					
				case SET: setSomething();
					break;					
				case SPAWN: spawnSomething();
					break;					
				case WRITE: writeSomething();
					break;			
			}	
		}
	}
		
	/** Cancels the original Bukkit event */
	private void cancelEvent()
	{
		if (ev.getOrigEvent() instanceof Cancellable)
		{		
			if (isActionCancelled("cancel_event")) return;
			
			((Cancellable)ev.getOrigEvent()).setCancelled(true);
		}
		else actionFailed("cancel_event", ReasonType.CANNOT_CANCEL_EVENT);
	}
	
// Cause action
	
	/** Causes explosions, lightning, etc. */
	private void causeSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("cause", ReasonType.NO_SUBACTION);
			return;
		}
		
		switch (st)
		{
			case EXPLOSION: explode();
				break;
			case FIERY_EXPLOSION: explodeFire();
				break;
			case LIGHTNING: lightningStrike();
				break;
			case LIGHTNING_EFFECT: lightningEffect();
				break;
		}
	}
		
	/** Causes an explosion */
	private void explode()
	{		
		int size = getSize(1);
		
		if (isActionCancelled("cause explosion " + Integer.toString(size))) return; 
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("cause explosion " + size + ", " + getPrettyLoc(loc), ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			loc.getWorld().createExplosion(loc, size);
		}
	}

	/** Causes an explosion which sets blocks on fire */
	private void explodeFire()
	{
		int size = getSize(1);
		
		if (isActionCancelled("cause fiery_explosion " + Integer.toString(size))) return; 
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("cause fiery_explosion " + size + ", " + getPrettyLoc(loc), ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			loc.getWorld().createExplosion(loc, size, true);
		}
	}
	
	/** Strikes a location or mob with lightning */
	private void lightningStrike()
	{
		if (isActionCancelled("cause lightning")) return; 
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("cause lightning, " + getPrettyLoc(loc), ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			loc.getWorld().strikeLightning(loc);
		}
	}

	/** Shows lightning without damaging a location or mob */
	private void lightningEffect()
	{		
		if (isActionCancelled("cause lightning_effect")) return; 
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("cause lightning_effect, " + getPrettyLoc(loc), ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			loc.getWorld().strikeLightningEffect(loc);
		}
	}
	
// Damage action
	
	/** Damages a mob or breaks a block */
	private void damageSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("damage", ReasonType.NO_SUBACTION);
			return;
		}
		
		switch (st)
		{
			case BLOCK: breakBlock();
				break;
			case MOB: damageMob();
				break;
		}
	}
	
	/** Breaks a block */
	private void breakBlock()
	{				
		int id = getItemId();
		if (id == 0)
		{
			if (isActionCancelled("damage block")) return;
			
			for (Location loc : getLocations())
			{
				if (!loc.getChunk().isLoaded())
				{
					actionFailed("damage block, " + getPrettyLoc(loc), ReasonType.CHUNK_NOT_LOADED);
					continue;
				}
				loc.getBlock().breakNaturally();
			}
			return;
		}
		
		int data = getItemData();
		
		ItemStack is = new ItemStack(id, 1, (short)data);
		if (isActionCancelled("damage block, " + getPrettyItem(is))) return;
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("damage block, (" + getPrettyItem(is) + "), " + getPrettyLoc(loc), ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			loc.getBlock().breakNaturally(is);
		}
	}
	
	/** Damages a mob */
	private void damageMob()
	{
		int amount = getAmount(0);
		if (amount == 0)
		{
			actionFailed("damage mob", ReasonType.BAD_AMOUNT);
			return;		
		}
			
		if (isActionCancelled("damage mob " + amount)) return;
			
		for (Damageable d : getDamageables())
		{
			if (amount != -1 ) d.damage(amount); else d.setHealth(0);
			//TODO kill docs -1
		}
	}	
	
// Give action
	
	/** Sends an item to players */
	private void giveSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("give", ReasonType.NO_SUBACTION);
			return;
		}
		
		switch (st)
		{
			case EXP: giveExp();
				break;
			case ITEM: giveItem();
				break;
			case MONEY: giveMoney();
				break;
		}
	}
	
	/** Gives a player exp directly */
	private void giveExp()
	{
		int amount = getAmount(0);
		if (amount == 0)
		{
			actionFailed("give exp", ReasonType.BAD_AMOUNT);
			return;		
		}
		
		if (isActionCancelled("give exp " + amount)) return;
		
		for (Player p : getPlayers()) p.giveExp(amount);
	}
		
	/** Sends an item to players */
	private void giveItem()
	{
		int id = getItemId();
		if (id == 0)
		{
			actionFailed("give item", ReasonType.BAD_ITEM_ID);
			return;		
		}
		
		int data = getItemData();
		int amount = getAmount(1);		
		
		ItemStack is = new ItemStack(id, amount, (short)data);
		if (isActionCancelled("give item " + getPrettyItem(is))) return;
			
		for (Player p : getPlayers()) p.getInventory().addItem(is);
	}
	
	/** Gives a player money (needs Vault) */
	private void giveMoney()
	{
		if (Mobs.economy == null)
		{
			actionFailed("give money", ReasonType.NO_VAULT);
			return;
		}
		
		int amount = getAmount(0);
		if (amount == 0)
		{
			actionFailed("give money", ReasonType.BAD_AMOUNT);
			return;		
		}
		
		if (isActionCancelled("give money " + amount)) return;
		
		for (Player p : getPlayers()) Mobs.economy.depositPlayer(p.getName(), amount);
	}
	
// Play action
	
	/** Plays a visual effect or sound effect */
	private void playSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("play", ReasonType.NO_SUBACTION);
			return;
		}
		
		switch (st)
		{
			case EFFECT: playEffect();
				break;
			case SOUND: playSound();
				break;
		}
	}	
	
	/** Plays a visual or sound effect */
	private void playEffect()
	{
		Effect effect = getEffect();
		if (effect == null)
		{
			actionFailed("play effect", ReasonType.NO_EFFECT);
			return;
		}
		
		if (isActionCancelled("play effect " + effect)) return;
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("play effect " + effect, ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			loc.getWorld().playEffect(loc, effect, 10);
		}
	}
	
	/** Plays a sound */
	private void playSound()
	{
		Sound sound = getSound();
		if (sound == null)
		{
			actionFailed("play sound", ReasonType.NO_SOUND);
			return;
		}
		
		float volume = getSoundVolume();
		float pitch = getSoundPitch();
		
		if (isActionCancelled("play sound " + sound + "(" + volume + ", " + pitch + ")")) return;
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("play sound", ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			loc.getWorld().playSound(loc, sound, volume, pitch);
		}
	}

// Remove action

	/** Removes something from the world */
	private void removeSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("remove", ReasonType.NO_SUBACTION);
			return;
		}
		
		switch (st)
		{
			case ALL_DROPS: removeAllDrops();
				break;
			case DATA: removeData();
				break;
			case DROPPED_EXP: removeDroppedExp();
				break;
			case DROPPED_ITEMS: removeDroppedItems();
				break;
			case ITEM: removeItem();
				break;
			case ITEMS: removeInventory();
				break;
			case MOB: removeMob();
				break;
			case SKIN: removeSkin();
				break;
			default: removeProperty();
				break;
		}
	}
	
	/** Flags a mob to not drop anything on death */
	private void removeAllDrops()
	{
		//TODO meta stuff
	}
	
	/** Removes all data from a mob or block */
	private void removeData()
	{		
		if (isActionCancelled("remove data")) return;
		
		for (LivingEntity le : getEntities()) Data.clearData(le);
	}
	
	/** Flags a mob to not drop any exp on death */
	private void removeDroppedExp()
	{
		//TODO meta stuff
	}
	
	/** Flags a mob to not drop any items on death */
	private void removeDroppedItems()
	{
		//TODO meta stuff
	}
	
	/** Removes all matching items from a player's inventory */
	private void removeItem()
	{
		//TODO item stuff
	}
	
	/** Clears all items from a player */
	private void removeInventory()
	{
		if (isActionCancelled("remove items")) return;
		
		for (Player p : getPlayers()) p.getInventory().clear();
	}
	
	/** Removes a mob from the world without it dying */
	private void removeMob()
	{
		if (isActionCancelled("remove mob")) return;
		
		for (LivingEntity le : getEntities())
		{
			if (!(le instanceof Player)) le.remove();
		}
	}
	
	/** Returns a mob's skin to default (requires Spout) */
	private void removeSkin()
	{
		if (!Mobs.isSpoutEnabled())
		{
			actionFailed("remove skin", ReasonType.NO_SPOUT);
			return;
		}
		
		if (isActionCancelled("remove skin")) return;
		
		for (LivingEntity le : getEntities()) Spout.getServer().resetEntitySkin(le);
	}
	
	/** Removes some data from a mob */
	private void removeProperty()
	{		
		//TODO meta stuff
	}
	
// Set action
	
	/** Sets something (mob property, door, etc.) */
	private void setSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("set", ReasonType.NO_SUBACTION);
			return;
		}
		
		switch (st)
		{
			case ADULT: setAdult();
				break;
			case ANGRY: setAngry();
				break;
			case BLOCK: setBlock();
				break;
			case HP: setHp();
				break;
			case LEVEL: setLevel();
				break;
			case MAX_HP: setMaxHp();
				break;  
			case OCELOT_TYPE: setOcelotType();
				break;
			case OPEN: setOpen();
				break;
			case OWNER: setOwner();
				break;
			case POWERED: setPowered();
				break;				
			case SADDLED: setSaddled();
				break;
			case SHEARED: setSheared();
				break;	
			case SIZE: setSize();
				break;
			case SKIN: setSkin();
				break;
			case TAMED: setTamed();
				break;
			case TIME: setTime();
				break;
			case TITLE: setTitle();
				break;
			case VILLAGER_TYPE: setVillagerType();
				break;
			case WEATHER: setWeather();
				break;
			case WOOL: setWool();
				break;
				
			case CUSTOM_FLAG_1:
			case CUSTOM_FLAG_2:
			case CUSTOM_FLAG_3:
			case CUSTOM_FLAG_4:
			case CUSTOM_FLAG_5:
			case CUSTOM_FLAG_6:
			case CUSTOM_FLAG_7:
			case CUSTOM_FLAG_8:
			case CUSTOM_FLAG_9:
			case CUSTOM_FLAG_10:
			case FIERY_EXPLOSION:
			case FRIENDLY:
			case NO_BURN:	
			case NO_CREATE_PORTALS:
			case NO_DESTROY_BLOCKS:
			case NO_DROPPED_EXP:
			case NO_DROPPED_ITEMS:
			case NO_DROPS:
			case NO_DYED:
			case NO_EVOLVE:		
			case NO_FIERY_EXPLOSION:	
			case NO_GRAZE:
			case NO_GROW_WOOL:
			case NO_HEAL:
			case NO_MOVE_BLOCKS:
			case NO_PICK_UP_ITEMS:
			case NO_SADDLED:
			case NO_SHEARING:
			case NO_TAMING:
			case NO_TELEPORT: setMeta(st);
				break;	
				
			case ATTACK_POWER:			
			case CUSTOM_INT_1:
			case CUSTOM_INT_2:
			case CUSTOM_INT_3:
			case CUSTOM_INT_4:
			case CUSTOM_INT_5:
			case CUSTOM_INT_6:
			case CUSTOM_INT_7:
			case CUSTOM_INT_8:
			case CUSTOM_INT_9:
			case CUSTOM_INT_10:
			case DAMAGE_FROM_BLOCK_EXPLOSION:
			case DAMAGE_FROM_CONTACT:
			case DAMAGE_FROM_CUSTOM:
			case DAMAGE_FROM_DROWNING:
			case DAMAGE_FROM_ENTITY_ATTACK:
			case DAMAGE_FROM_ENTITY_EXPLOSION:
			case DAMAGE_FROM_FALL:
			case DAMAGE_FROM_FALLING_BLOCK:
			case DAMAGE_FROM_FIRE:
			case DAMAGE_FROM_FIRE_TICK:
			case DAMAGE_FROM_LAVA:
			case DAMAGE_FROM_LIGHTNING:
			case DAMAGE_FROM_MAGIC:
			case DAMAGE_FROM_MELTING:
			case DAMAGE_FROM_POISON:
			case DAMAGE_FROM_PROJECTILE:
			case DAMAGE_FROM_STARVATION:
			case DAMAGE_FROM_SUFFOCATION:
			case DAMAGE_FROM_SUICIDE:
			case DAMAGE_FROM_VOID:
			case DAMAGE_FROM_WITHER:
			case EXPLOSION_SIZE:
			case MAX_LIFE: 
			case SPLIT_INTO: setCustomInt(st);
				break;
				
			case CUSTOM_STRING_1:
			case CUSTOM_STRING_2:
			case CUSTOM_STRING_3:
			case CUSTOM_STRING_4:
			case CUSTOM_STRING_5:
			case CUSTOM_STRING_6:
			case CUSTOM_STRING_7:
			case CUSTOM_STRING_8:
			case CUSTOM_STRING_9:
			case CUSTOM_STRING_10: 
			case NAME: setCustomValue(st);
				break;
			/*case EXP: setExp(temp, ev);
				break;
			case MONEY: setMoney(temp, ev);
				break;*/
		}
		//TODO flexidamage?
	}

	private void setBlock()
	{
		int id = getItemId();		
		int data = getItemData();
		
		if (isActionCancelled("set block " + id + ":" + data)) return;
		
		for (Location loc : getLocations())
		{
			loc.getBlock().setTypeIdAndData(id, (byte)data, false);
		}
	}
	
	/** Sets a door, gate, etc. open or closed */
	private void setOpen()
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set open " + value, ReasonType.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set open " + value)) return;
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("set open " + value, ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			
			BlockState bs = loc.getBlock().getState();
			MaterialData md = bs.getData();
			if (md instanceof Openable)
			{
				boolean b = getBooleanValue(((Openable)md).isOpen(), value);
				((Openable)md).setOpen(b);
			}
			else if (md instanceof Lever)
			{
				boolean b = getBooleanValue(((Lever)md).isPowered(), value);
				((Lever)md).setPowered(b);
			}
			else
			{
				actionFailed("set open", ReasonType.CANNOT_BE_OPENED);
				return;
			}
			bs.setData(md);
			bs.update(true);
		}
	}
	
	private void setTime()
	{		
		String value = getValue();
		if (value == null)
		{
			actionFailed("set time", ReasonType.NO_VALUE);
			return;
		}	//TODO +/-/%
		
		int time = getNumber(value);
		
		if (isActionCancelled("set time" + ", " + value)) return;
		
		World w = getWorld();
		
		long i = time % 24000; 
		if (i < 0) i += 24000;
		w.setTime(i);
	}
	
	/** Sets the weather */
	private void setWeather()
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set weather", ReasonType.NO_VALUE);
			return;
		}//TODO before and after events
		
		int duration = getDuration();
		
		if (value.equals(ValueType.RANDOM))
		{
			int i = new Random().nextInt(3);
			switch (i)
			{
				case 0: value = ValueType.RAINY;
					break;
				case 1: value = ValueType.SUNNY;
					break;
				case 2: value = ValueType.STORMY;
					break;
			}//TODO docs random + effect / sound
		}
		
		if (isActionCancelled("set weather, " + value + "(" + duration + ")")) return;
		
		World w = getWorld();
		
		switch (value)
		{
			case RAINY:
				w.setStorm(true);
				w.setThundering(false);
				break;
			case STORMY:
				w.setStorm(true);
				w.setThundering(true);
				break;
			case SUNNY:
				w.setStorm(false);
				w.setThundering(false);
				break;
		}
		if (duration != 0) w.setWeatherDuration(duration);
	}
	
// set mob properties
		
	/** Makes an Ageable mob an adult or baby */
	private void setAdult()
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set adult", ReasonType.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set adult, " + value)) return;
		
		for (Ageable a : getAgeables())
		{
			boolean b2 = getBooleanValue(a.isAdult(), value);
			if (a.isAdult() == b2) return;
				
			if (b2) a.setAdult(); else a.setBaby();
		}
	}
	
	/** Makes a Wolf or PigZombie angry or calm */
	private void setAngry()
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set angry", ReasonType.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set angry, " + value)) return;
		
		for (LivingEntity le : getEntities())
		{
			if (le instanceof Wolf)
			{
				Wolf w = (Wolf)le;
				boolean b = getBooleanValue(w.isAngry(), value);
				if (b == w.isAngry()) return;
				
				w.setAngry(b);
				if (b)
				{
					for (Entity e : w.getNearbyEntities(30, 10, 30))
					if (e instanceof Player)
					{
						w.damage(0, e);
						return;
					}
				}
			}
			else if (le instanceof PigZombie)
			{
				boolean b = ((PigZombie)le).isAngry();
				boolean b2 = getBooleanValue(b, value);
				if (b == b2) return;
				
				((PigZombie)le).setAngry(b2);
			}
			else actionFailed("set angry", ReasonType.NOT_AN_ANGERABLE_MOB);
		}
	}
	
	private void setCustomInt(SubactionType st)
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set " + st, ReasonType.NO_VALUE);
			return;
		}	
		
		int amount = getNumber(value);
		
		if (isActionCancelled("set " + st + ", " + amount)) return;
		
		for (LivingEntity le : getEntities())
		{
			Data.putData(le, st, amount);
		}
	}
	
	private void setCustomValue(SubactionType st)
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set " + st, ReasonType.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set " + st + ", " + value)) return;
		
		for (LivingEntity le : getEntities())
		{
			Data.putData(le, st, value);
		}
	}
		
	/** Sets a mob's health */
	private void setHp()
	{//TODO reset health
		String value = getValue();
		if (value == null)
		{
			actionFailed("set hp", ReasonType.NO_VALUE);
			return;
		}	
		
		int amount = getNumber(value);
		
		if (isActionCancelled("set hp " + amount)) return;
		
		for (Damageable d : getDamageables())
		{
			if (amount > d.getMaxHealth()) d.setHealth(d.getMaxHealth()); else d.setHealth(amount);
		}
	}
	
	/** Sets a mob's health */
	private void setMaxHp()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set max_hp", ReasonType.NO_VALUE);
			return;
		}	
		
		int amount = getNumber(value);
		
		if (isActionCancelled("set max_hp " + amount)) return;
		
		for (Damageable d : getDamageables())
		{
			d.setMaxHealth(amount);
		}
	}
	
	/** Sets a player's exp level */
	private void setLevel()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set level", ReasonType.NO_VALUE);
			return;
		}	
		
		int amount = getNumber(value);	
		
		if (isActionCancelled("set level " + amount)) return;
		
		for (Player p : getPlayers())
		{
			p.setLevel(amount);
		}
	}
	
	/** Makes an Ageable mob an adult or baby */
	private void setMeta(SubactionType st)
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set " + st, ReasonType.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set " + st + ", " + value)) return;
		
		for (LivingEntity le : getEntities())
		{
			boolean b = Data.hasData(le, st);
			boolean b2 = getBooleanValue(b, value);
			if (b == b2) continue;
			if (b) Data.putData(le, st); else Data.removeData(le, st);
		}
	}
	
	private void setOcelotType()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set ocelot_type", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set ocelot_type, " + value)) return;
		
		Ocelot.Type ot;
		if (value.equalsIgnoreCase("random"))
		{
			ot = Ocelot.Type.getType(new Random().nextInt(Ocelot.Type.values().length));
		}
		else ot = Ocelot.Type.valueOf(value.toUpperCase());
		
		for (Ocelot o : getOcelots()) o.setCatType(ot);
	}
	
	private void setOwner()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set owner", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set owner, " + value)) return;
		
		for (Tameable t : getTameables())
		{
			t.setOwner(Bukkit.getPlayer(value));
		}
	}
	
	/** Makes an Ageable mob an adult or baby */
	private void setPowered()
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set powered", ReasonType.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set powered, " + value)) return;
		
		for (Creeper c : getCreepers())
		{
			boolean b = getBooleanValue(c.isPowered(), value);
			if (c.isPowered() != b) c.setPowered(b);
		}
	}
	
	/** Makes an Ageable mob an adult or baby */
	private void setSaddled()
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set saddled", ReasonType.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set saddled, " + value)) return;
		
		for (Pig p : getPigs())
		{
			boolean b = getBooleanValue(p.hasSaddle(), value);
			if (p.hasSaddle() != b) p.setSaddle(b);
		}
	}
	
	/** Makes an Ageable mob an adult or baby */
	private void setSheared()
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set sheared", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set sheared, " + value)) return;
		
		for (Sheep s : getSheep())
		{
			boolean b = getBooleanValue(s.isSheared(), value);
			if (s.isSheared() != b) s.setSheared(b);
		}
	}
	
	private void setSize()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set size", ReasonType.NO_VALUE);
			return;
		}
		
		int size = getNumber(value);
		
		if (isActionCancelled("set size " + size)) return;
		
		for (Slime s : getSlimes()) s.setSize(size);
	}
	
	private void setSkin()
	{
		if (!Mobs.isSpoutEnabled())
		{
			actionFailed("set skin", ReasonType.NO_SPOUT);
			return;
		}
		
		String value = getValue();
		if (value == null)
		{
			actionFailed("set skin", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set skin, " + value)) return;
		
		for (LivingEntity le : getEntities())
		{
			Spout.getServer().setEntitySkin(le, value, EntitySkinType.DEFAULT);
		}
	}
	
	/** Makes an Ageable mob an adult or baby */
	private void setTamed()
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set tamed", ReasonType.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set tamed, " + value)) return;	
		
		for (Tameable t : getTameables())
		{
			boolean b = getBooleanValue(t.isTamed(), value);
			if (t.isTamed() != b) t.setTamed(b);
		}
	}
	
	private void setTitle()
	{
		if (!Mobs.isSpoutEnabled())
		{
			actionFailed("set title", ReasonType.NO_SPOUT);
			return;
		}
		
		String value = getValue();
		if (value == null)
		{
			actionFailed("set title", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set title, " + value)) return;
		
		for (LivingEntity le : getEntities())
		{
			Spout.getServer().setTitle(le, value);
		}
	}
	
	private void setVillagerType()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set villager_type", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set villager_type, " + value)) return;
		
		Villager.Profession vp;
		if (value.equalsIgnoreCase("random"))
		{
			vp = Villager.Profession.getProfession(new Random().nextInt(Villager.Profession.values().length));
		}
		else vp = Villager.Profession.valueOf(value.toUpperCase());
		
		for (Villager v : getVillagers()) v.setProfession(vp);
	}
	
	/** Sets a sheep's wool colour */
	private void setWool()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set wool", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set wool, " + value)) return;
		
		DyeColor dc;
		if (value.equalsIgnoreCase("random"))
		{
			dc = DyeColor.getByDyeData((byte) new Random().nextInt(DyeColor.values().length));
		}
		else dc = DyeColor.valueOf(value.toUpperCase());
		
		for (Sheep s : getSheep()) s.setColor(dc);
	}
	
// spawn action
	
	/** Spawns a mob/item/exp_orb, etc. */
	private void spawnSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("damage", ReasonType.NO_SUBACTION);
			return;
		}
		
		switch (st)
		{
			case EXP: spawnExp();
				break;
			case ITEM: spawnItem();
				break;
			case MOB: spawnMob();
				break;
		}
	}

	/** Spawns an exp orb at a location */
	private void spawnExp()
	{
		int amount = getAmount(0);
		if (amount == 0)
		{
			actionFailed("spawn exp", ReasonType.BAD_AMOUNT);
			return;
		}
		
		if (isActionCancelled("spawn exp " + amount)) return;
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("spawn exp " + amount, ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			ExperienceOrb orb = (ExperienceOrb)loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
			orb.setExperience(amount);
		}
	}
	
	/** Spawns an item at a location */
	private void spawnItem()
	{		
		int id = getItemId();
		if (id == 0)
		{
			actionFailed("spawn item", ReasonType.BAD_ITEM_ID);
			return;
		}
		
		int data = getItemData();
		int amount = getAmount(1);
		
		ItemStack is = new ItemStack(id, amount, (short)data);
		if (isActionCancelled("spawn item " + getPrettyItem(is))) return;
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("spawn item " + getPrettyItem(is), ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			loc.getWorld().dropItem(loc, is);
		}
	}
	
	/** Spawns a mob at a location */
	private void spawnMob()
	{		
		EntityType et = getMob();
		if (et == null)
		{
			actionFailed("spawn mob", ReasonType.NO_MOB);
			return;
		}
		
		String mob_name = getMobName();		
		int amount = getAmount(1);
				
		if (isActionCancelled("spawn mob, " + et + "(" + mob_name + ")")) return;
		
		for (int i = 0; i < amount; i++)
		{
			for (Location loc : getLocations())
			{
				if (!loc.getChunk().isLoaded())
				{
					actionFailed("spawn mob " + et + "(" + mob_name + ")", ReasonType.CHUNK_NOT_LOADED);
					continue;
				}
				
				Mobs.getInstance().setMob_name(mob_name);
				loc.getWorld().spawnEntity(loc, et);
			}
		}
		//TODO mob description ?
	}
	
// write action 	 
	
	/** Sends a message to the log/a player/the server */
	private void writeSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("damage", ReasonType.NO_SUBACTION);
			return;
		}
		
		switch (st)
		{
			case BROADCAST: broadcastMessage();
				break;
			case LOG: logMessage();
				break;
			case MESSAGE: sendMessage();
				break;
		}
	}
	
	/** Sends a message to everyone on the server */
	private void broadcastMessage()
	{
		String message = getMessage();
		if (message == null)
		{
			actionFailed("write message", ReasonType.NO_MESSAGE);
			return;
		}		
		
		if (isActionCancelled("write message " + message)) return;
		
		Bukkit.getServer().broadcastMessage(message);
	}
	
	/** Writes a message to the console */
	private void logMessage()
	{
		String message = getMessage();
		if (message == null)
		{
			actionFailed("write message", ReasonType.NO_MESSAGE);
			return;
		}		
		
		if (isActionCancelled("write message " + message)) return;
		
		Mobs.log(message);
	}
	
	/** Sends a message to one player */
	private void sendMessage()
	{		
		String message = getMessage();
		if (message == null)
		{
			actionFailed("write message", ReasonType.NO_MESSAGE);
			return;
		}		
		
		if (isActionCancelled("write message " + message)) return;
			
		for (Player p : getPlayers()) p.sendMessage(message);
	}
	
	
// MobsElement getters
	
	private ActionType getAction()
	{
		ce = ce.getCurrentElement(ElementType.ACTION, ev);
		if (ce == null) return null;
		
		return ActionType.valueOf(ce.getString(ElementType.ACTION).toUpperCase());
	}
	
	private List<MobsElement> getActions() 
	{
		return ce.getActions(ev);
	}
	
	private int getAmount(int orig)
	{
		ce = ce.getCurrentElement(ElementType.AMOUNT, ev);
		if (ce == null) return orig;
		
		return getNumber(ce.getString(ElementType.AMOUNT));
	}
	
	private int getDuration()
	{
		ce = ce.getCurrentElement(ElementType.DURATION, ev);
		if (ce == null) return 0;
		
		return getNumber(ce.getString(ElementType.DURATION)) * 20;//TODO docs = ticks
	}
	
	private Effect getEffect()
	{
		ce = ce.getCurrentElement(ElementType.EFFECT, ev);
		if (ce == null) return null;
		
		return Effect.valueOf(ce.getString(ElementType.EFFECT).toUpperCase());	
	}
	
	private int getItemData()
	{
		ce = ce.getCurrentElement(ElementType.ITEM_DATA, ev);
		if (ce == null) return 0;
		
		return getNumber(ce.getString(ElementType.ITEM_DATA));
	}
	
	private int getItemId()
	{
		ce = ce.getCurrentElement(ElementType.ITEM_ID, ev);
		if (ce == null) return 0;
		
		return getNumber(ce.getString(ElementType.ITEM_ID));
	}
	
	private String getMessage()
	{//TODO message change to value?
		ce = ce.getCurrentElement(ElementType.MESSAGE, ev);
		if (ce == null) return null;
		
		return ce.getString(ElementType.MESSAGE);
	}
	
	private EntityType getMob()
	{
		ce = ce.getCurrentElement(ElementType.MOB, ev);
		if (ce == null) return null;
		
		return EntityType.valueOf(ce.getString(ElementType.MOB).toUpperCase());	
	}
	
	private String getMobName()
	{
		ce = ce.getCurrentElement(ElementType.MOB_NAME, ev);
		if (ce == null) return null;
		
		return ce.getString(ElementType.MOB_NAME);
	}
	
	private String getPlayer()
	{
		ce = ce.getCurrentElement(ElementType.PLAYER, ev);
		if (ce == null) return null;
		
		return ce.getString(ElementType.PLAYER);
	}
	
	private int getSize(int orig)
	{
		ce = ce.getCurrentElement(ElementType.SIZE, ev);
		if (ce == null) return orig;
		
		return getNumber(ce.getString(ElementType.SIZE));
	}
	
	private Sound getSound()
	{
		ce = ce.getCurrentElement(ElementType.SOUND, ev);
		if (ce == null) return null;
		
		return Sound.valueOf(ce.getString(ElementType.SOUND).toUpperCase());	
	}
	
	private float getSoundPitch()
	{
		ce = ce.getCurrentElement(ElementType.SOUND_PITCH, ev);
		if (ce == null) return 1.0f;
	
		return Float.parseFloat(ce.getString(ElementType.SOUND_PITCH)) / 100;
		//TODO docs
	}
	
	private float getSoundVolume()
	{
		ce = ce.getCurrentElement(ElementType.SOUND_VOLUME, ev);
		if (ce == null) return 1.0f;
	
		return Float.parseFloat(ce.getString(ElementType.SOUND_VOLUME)) / 100;
		//TODO docs
	}
	
	private SubactionType getSubaction()
	{
		ce = ce.getCurrentElement(ElementType.SUBACTION, ev);
		if (ce == null) return null;
		
		return SubactionType.valueOf(ce.getString(ElementType.SUBACTION).toUpperCase());
	}
	
	private TargetType getTarget()
	{
		ce = ce.getCurrentElement(ElementType.TARGET, ev);
		if (ce == null)
		{
			ce = this;
			return null;
		}
		
		return TargetType.valueOf(ce.getString(ElementType.TARGET).toUpperCase());
	}
	
	private String getValue()
	{
		ce = ce.getCurrentElement(ElementType.VALUE, ev);
		if (ce == null) return null;
		
		return ce.getString(ElementType.VALUE);
	}
	
	private ValueType getValueType()
	{
		String s = getValue();
		if (s == null) return null;
		
		return ValueType.valueOf(s.toUpperCase());
	}
	
	private World getWorld()
	{
		MobsElement me = ce.getCurrentElement(ElementType.WORLD, ev);
		if (me != null)
		{//TODO bubble?
			ce = me;
			return Bukkit.getWorld(me.getString(ElementType.WORLD));
		}
		
		LivingEntity le = ev.getLivingEntity();
		if (le != null) return le.getWorld();
		
		return null;
	}
	
// Utils
	
	private boolean isAffected()
	{
		if (affected_mobs != null)
		{
			LivingEntity le = ev.getLivingEntity();
			if (le == null) return false;
			if (!affected_mobs.contains(le.getType().toString())) return false;			
		}
		
		if (affected_worlds != null)
		{
			World w = getWorld();
			if (w == null) return false;
			if (!affected_worlds.contains(w.getName().toUpperCase())) return false;
		}//TODO global?
		return true;
	}
	
	/** Returns a randomized int */
	private int getNumber(String s)
	{
		s = s.replace(" ", "");
		String[] temp = s.split(",");
		String s2 = temp[new Random().nextInt(temp.length)];
		if (s2.contains("TO"))
		{
			String[] temp2 = s2.split("TO");
			return new Random().nextInt(Integer.parseInt(temp2[1]) - Integer.parseInt(temp2[0])) +
					Integer.parseInt(temp2[0]);
		}
		else return Integer.parseInt(s2);
	}
	
	/** Returns a boolean calculated from the original value and the valuetype */
	private boolean getBooleanValue(boolean orig, ValueType vt)
	{
		switch (vt)
		{
			case NO: return false;
			case RANDOM: return new Random().nextBoolean();
			case TOGGLED: return !orig;
			case YES: return true;
		}
		
		return orig;
	}
	
	/** Returns a formatted ItemStack */
	private String getPrettyItem(ItemStack is)
	{
		//TODO return proper stuff
		return "an item";
	}
	
	private String getPrettyLoc(Location loc)
	{
		//TODO prettify
		return loc.toString();
	}
	
	/** Returns a list of objects (LivingEntity or Location) to have actions performed on */
	private Object getMCTarget()
	{		
		TargetType tt = getTarget();
		if (tt == null)
		{
			if (ev.getLivingEntity() != null) return ev.getLivingEntity();
		}			
		
		switch (tt)
		{
			case AUX_MOB:
				Event orig_event = ev.getOrigEvent();
				if (orig_event instanceof EntityDamageByEntityEvent)
				{
					Entity ee = ((EntityDamageByEntityEvent)orig_event).getDamager();
					if (ee instanceof LivingEntity) target = ee;
				}
				else if (orig_event instanceof PlayerApproachLivingEntityEvent)
					return ((PlayerApproachLivingEntityEvent)orig_event).getPlayer();
				else if (orig_event instanceof PlayerLeaveLivingEntityEvent)
					return ((PlayerLeaveLivingEntityEvent)orig_event).getPlayer();
				else if (orig_event instanceof PlayerNearLivingEntityEvent)
					return ((PlayerNearLivingEntityEvent)orig_event).getPlayer();
				else if (orig_event instanceof LivingEntityBlockEvent)
					return ((LivingEntityBlockEvent)orig_event).getAttacker();
				else if (orig_event instanceof LivingEntityDamageEvent)
					return ((LivingEntityDamageEvent)orig_event).getAttacker();
				else if (orig_event instanceof EntityTargetLivingEntityEvent)
					return ((EntityTargetLivingEntityEvent)orig_event).getTarget();
				else if (orig_event instanceof EntityTameEvent)
					return (LivingEntity) ((EntityTameEvent)orig_event).getOwner();
				else if (orig_event instanceof PlayerShearEntityEvent)
					return ((PlayerShearEntityEvent)orig_event).getPlayer();
				else if (orig_event instanceof EntityDeathEvent)
					return ((EntityDeathEvent)orig_event).getEntity().getKiller();
				else if (orig_event instanceof PlayerDeathEvent)
					return ((PlayerDeathEvent)orig_event).getEntity().getKiller();
			
				break;	
				//TODO target drilling down
			case PLAYER:
				String s = getPlayer();
				if (s != null) return Bukkit.getPlayer(s);
				break;
			/*case NEAREST:
				for (LivingEntity l : getNearest(le.getNearbyEntities(50, 10, 50), le))
					targets.add(l);
				break;
			case RANDOM:
				for (LivingEntity l : getNearby(le.getWorld().getEntities()))
					targets.add(l);
				break;
			case BLOCK:
				World w = getWorld(le);
				targets.add(new Location(w, getX().getInt_value(0), getY().getInt_value(0), getZ().getInt_value(0)));
				/*
				for (int x : getX())
				{
					for (int y : getY())
					{
						if (y > w.getMaxHeight()) y = w.getMaxHeight();
						for (int z : getZ())
						{
							targets.add(new Location(w, x, y, z));
						}
					}
				}*/
			default: if (ev.getLivingEntity() != null) return ev.getLivingEntity();
			//	break;
		}
		
		return null;
	}
	
	private List<Ageable> getAgeables()
	{
		List<Ageable> temp = new ArrayList<Ageable>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Ageable) temp.add((Ageable)target);
			}
		}
		else if (target instanceof Ageable) temp.add((Ageable)target);
		return temp;
	}
	
	private List<Location> getLocations()
	{//TODO stuff
		return null;
	}
	
	private List<Damageable> getDamageables()
	{
		List<Damageable> temp = new ArrayList<Damageable>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Damageable) temp.add((Damageable)target);
			}
		}
		else if (target instanceof Damageable) temp.add((Damageable)target);
		return temp;
	}
	
	private List<LivingEntity> getEntities()
	{
		List<LivingEntity> temp = new ArrayList<LivingEntity>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof LivingEntity) temp.add((LivingEntity)target);
			}
		}
		else if (target instanceof LivingEntity) temp.add((LivingEntity)target);
		return temp;
	}
	
	private List<Player> getPlayers()
	{
		List<Player> temp = new ArrayList<Player>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Player) temp.add((Player)target);
			}
		}
		else if (target instanceof Player) temp.add((Player)target);
		return temp;
	}
	
	private List<Creeper> getCreepers()
	{
		List<Creeper> temp = new ArrayList<Creeper>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Creeper) temp.add((Creeper)target);
			}
		}
		else if (target instanceof Creeper) temp.add((Creeper)target);
		return temp;
	}
	
	private List<Ocelot> getOcelots()
	{
		List<Ocelot> temp = new ArrayList<Ocelot>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Ocelot) temp.add((Ocelot)target);
			}
		}
		else if (target instanceof Ocelot) temp.add((Ocelot)target);
		return temp;
	}
	
	private List<Pig> getPigs()
	{
		List<Pig> temp = new ArrayList<Pig>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Pig) temp.add((Pig)target);
			}
		}
		else if (target instanceof Pig) temp.add((Pig)target);
		return temp;
	}
	
	private List<Sheep> getSheep()
	{
		List<Sheep> temp = new ArrayList<Sheep>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Sheep) temp.add((Sheep)target);
			}
		}
		else if (target instanceof Sheep) temp.add((Sheep)target);
		return temp;
	}
	
	private List<Slime> getSlimes()
	{
		List<Slime> temp = new ArrayList<Slime>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Slime) temp.add((Slime)target);
			}
		}
		else if (target instanceof Slime) temp.add((Slime)target);
		return temp;
	}
	
	private List<Tameable> getTameables()
	{
		List<Tameable> temp = new ArrayList<Tameable>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Tameable) temp.add((Tameable)target);
			}
		}
		else if (target instanceof Tameable) temp.add((Tameable)target);
		return temp;
	}
	
	private List<Villager> getVillagers()
	{
		List<Villager> temp = new ArrayList<Villager>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Villager) temp.add((Villager)target);
			}
		}
		else if (target instanceof Villager) temp.add((Villager)target);
		return temp;
	}
	
	/*private List<Ageable> test()
	{
		List<Ageable> temp = new ArrayList<Ageable>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Ageable) temp.add((Ageable)target);
			}
		}
		else if (target instanceof Ageable) temp.add((Ageable)target);
		return temp;
	}*/
	
	
	
	/** Calls an event when an action is about to be performed, with the possibility of cancelling */
	private boolean isActionCancelled(String attempting)
	{
		if (Mobs.allow_debug)
		{//TODO pass tuff
			MobsPerformingActionEvent mpae = new MobsPerformingActionEvent(event_name, ev, null, null, null);
			Bukkit.getServer().getPluginManager().callEvent(mpae);
			return mpae.isCancelled();
		}
		return false;
	}
	
	/** Calls an event when an action fails (due to wrong type of mob, etc.) */
	private void actionFailed(String attempted, ReasonType reason)
	{
		if (!Mobs.allow_debug) return;//TODO pass stuff
		Bukkit.getServer().getPluginManager().callEvent(new MobsFailedActionEvent(this, attempted, reason));
	}
	
}