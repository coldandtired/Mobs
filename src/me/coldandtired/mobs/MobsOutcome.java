package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.xml.xpath.XPathExpressionException;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
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
	//private List<String> affected_worlds;
	
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

		/*if (element.hasAttribute("affected_worlds"))
		{
			affected_worlds = new ArrayList<String>();
			String s = element.getAttribute("affected_worlds").toUpperCase().replace(" ", "");
			affected_worlds = Arrays.asList(s.split(","));
		}*///TODO world stuff
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
			actionFailed(null, FailedReason.NO_ACTION);
			return;
		}
		
		for (MobsElement me : actions)
		{		
			ce = me;
			ActionType at = getAction();
			if (!at.equals(ActionType.CANCEL_EVENT) && target == null)
			{
				actionFailed(at.toString(), FailedReason.NO_TARGET);
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
		else actionFailed("cancel_event", FailedReason.CANNOT_CANCEL_EVENT);
	}
	
// Cause action_type
	
	/** Causes explosions, lightning, etc. */
	private void causeSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("cause", FailedReason.NO_SUBACTION);
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
				actionFailed("cause explosion " + size + ", " + getPrettyLoc(loc), FailedReason.CHUNK_NOT_LOADED);
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
				actionFailed("cause fiery_explosion " + size + ", " + getPrettyLoc(loc), FailedReason.CHUNK_NOT_LOADED);
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
				actionFailed("cause lightning, " + getPrettyLoc(loc), FailedReason.CHUNK_NOT_LOADED);
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
				actionFailed("cause lightning_effect, " + getPrettyLoc(loc), FailedReason.CHUNK_NOT_LOADED);
				continue;
			}
			loc.getWorld().strikeLightningEffect(loc);
		}
	}
	
// Damage action_type
	
	/** Damages a mob or breaks a block */
	private void damageSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("damage", FailedReason.NO_SUBACTION);
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
					actionFailed("damage block, " + getPrettyLoc(loc), FailedReason.CHUNK_NOT_LOADED);
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
				actionFailed("damage block, (" + getPrettyItem(is) + "), " + getPrettyLoc(loc), FailedReason.CHUNK_NOT_LOADED);
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
			actionFailed("damage mob", FailedReason.ZERO_AMOUNT);
			return;		
		}
			
		if (isActionCancelled("damage mob " + amount)) return;
			
		for (LivingEntity le : getEntities())
		{
			if (amount != -1 ) le.damage(amount); else le.setHealth(0);
			//TODO kill docs -1
		}
	}	
	
// Give action_type
	
	/** Sends an item to players */
	private void giveSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("give", FailedReason.NO_SUBACTION);
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
			actionFailed("give exp", FailedReason.ZERO_AMOUNT);
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
			actionFailed("give item", FailedReason.ZERO_ITEM_ID);
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
			actionFailed("give money", FailedReason.NO_VAULT);
			return;
		}
		
		int amount = getAmount(0);
		if (amount == 0)
		{
			actionFailed("give money", FailedReason.ZERO_AMOUNT);
			return;		
		}
		
		if (isActionCancelled("give money " + amount)) return;
		
		for (Player p : getPlayers()) Mobs.economy.depositPlayer(p.getName(), amount);
	}
	
// Play action_type
	
	/** Plays a visual effect or sound effect */
	private void playSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("play", FailedReason.NO_SUBACTION);
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
			actionFailed("play effect", FailedReason.NO_EFFECT);
			return;
		}
		
		if (isActionCancelled("play effect " + effect)) return;
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("play effect " + effect, FailedReason.CHUNK_NOT_LOADED);
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
			actionFailed("play sound", FailedReason.NO_SOUND);
			return;
		}
		
		float volume = getSoundVolume();
		float pitch = getSoundPitch();
		
		if (isActionCancelled("play sound " + sound + "(" + volume + ", " + pitch + ")")) return;
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("play sound", FailedReason.CHUNK_NOT_LOADED);
				continue;
			}
			loc.getWorld().playSound(loc, sound, volume, pitch);
		}
	}

// Remove action_type

	/** Removes something from the world */
	private void removeSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("remove", FailedReason.NO_SUBACTION);
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
			actionFailed("remove skin", FailedReason.NO_SPOUT);
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
	
// Set action_type
	
	/** Sets something (mob property, door, etc.) */
	private void setSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("damage", FailedReason.NO_SUBACTION);
			return;
		}
		
		switch (st)
		{
			case ADULT: setAdult();
				break;
			case ANGRY: setAngry();
				break;
			case OPEN: setOpen();
				break;
			case POWERED: setPowered();
				break;				
			case SADDLED: setSaddled();
				break;
			case SHEARED: setSheared();
				break;					
			case TAMED: setTamed();
				break;
			/*case ATTACK_POWER: setAttack_power(temp, ev);
				break;
			case BLOCK: setBlock(temp, ev);
				break;
			/*case CUSTOM_FLAG_1:
			case CUSTOM_FLAG_2:
			case CUSTOM_FLAG_3:
			case CUSTOM_FLAG_4:
			case CUSTOM_FLAG_5:
			case CUSTOM_FLAG_6:
			case CUSTOM_FLAG_7:
			case CUSTOM_FLAG_8:
			case CUSTOM_FLAG_9:
			case CUSTOM_FLAG_10: 
			case FRIENDLY:					
			case NO_BURN:	
			case NO_CREATE_PORTALS:
			case NO_DESTROY_BLOCKS:
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
			case NO_TELEPORT: setProperty(temp, ev);
				break;					
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
			case NAME: setCustom_value(temp, ev);
				break;
			case DAMAGE_TAKEN: setDamage_taken(temp, ev);
				break;
			case EXP: setExp(temp, ev);
				break;
			case EXPLOSION_SIZE: setExplosion_size(temp, ev);
				break;
			case HP: setHp(temp, ev);
				break;
			case LEVEL: setLevel(temp, ev);
				break;	
			case MAX_HP: setMax_hp(temp, ev);
				break;
			case MAX_LIFE: setMax_life(temp, ev);
				break;	
			case MONEY: setMoney(temp, ev);
				break;
			case OCELOT_TYPE: setOcelot_type(temp, ev);
				break;
			case OPEN: openSomething(temp, ev);
				break;
			case OWNER: setOwner(temp, ev);
				break;	
			case SKIN: setSkin(temp, ev);
				break;
			case TIME: setTime(temp, ev);
				break;
			case TITLE: setTitle(temp, ev);
				break;	
			case VILLAGER_TYPE: setVillager_type(temp, ev);						
				break;
			case WEATHER: setWeather(temp, ev);
				break;
			case WOOL_COLOR: setWool_colour(temp, ev);
				break;*/
		}
	}

	/** Sets a door, gate, etc. open or closed */
	private void setOpen()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set open " + value, FailedReason.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set open " + value)) return;
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("set open " + value, FailedReason.CHUNK_NOT_LOADED);
				continue;
			}
			
			BlockState bs = loc.getBlock().getState();
			MaterialData md = bs.getData();
			if (md instanceof Openable)
			{
				boolean b = getBooleanPropertyValue(((Openable)md).isOpen(), value);
				((Openable)md).setOpen(b);
			}
			else if (md instanceof Lever)
			{
				boolean b = getBooleanPropertyValue(((Lever)md).isPowered(), value);
				((Lever)md).setPowered(b);
			}
			else
			{
				actionFailed("set open", FailedReason.CANNOT_BE_OPENED);
				return;
			}
			bs.setData(md);
			bs.update(true);
		}
	}
	
// set mob properties
		
	/** Makes an Ageable mob an adult or baby */
	private void setAdult()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set adult", FailedReason.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set adult, " + value)) return;
		
		for (Ageable a : getAgeables())
		{
			boolean b2 = getBooleanPropertyValue(a.isAdult(), value);
			if (a.isAdult() == b2) return;
				
			if (b2) a.setAdult(); else a.setBaby();
		}
	}
	
	/** Makes a Wolf or PigZombie angry or calm */
	private void setAngry()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set angry", FailedReason.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set angry, " + value)) return;
		
		for (LivingEntity le : getEntities())
		{
			if (le instanceof Wolf)
			{
				Wolf w = (Wolf)le;
				boolean b = getBooleanPropertyValue(w.isAngry(), value);
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
				boolean b2 = getBooleanPropertyValue(b, value);
				if (b == b2) return;
				
				((PigZombie)le).setAngry(b2);
			}
			else actionFailed("set angry", FailedReason.NOT_AN_ANGERABLE_MOB);
		}
	}
		
	/** Makes an Ageable mob an adult or baby */
	private void setPowered()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set powered", FailedReason.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set powered, " + value)) return;
		
		for (Creeper c : getCreepers())
		{
			boolean b = getBooleanPropertyValue(c.isPowered(), value);
			if (c.isPowered() != b) c.setPowered(b);
		}
	}
	
	/** Makes an Ageable mob an adult or baby */
	private void setSaddled()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set saddled", FailedReason.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set saddled, " + value)) return;
		
		for (Pig p : getPigs())
		{
			boolean b = getBooleanPropertyValue(p.hasSaddle(), value);
			if (p.hasSaddle() != b) p.setSaddle(b);
		}
	}
	
	/** Makes an Ageable mob an adult or baby */
	private void setSheared()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set sheared", FailedReason.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set sheared, " + value)) return;
		
		for (Sheep s : getSheep())
		{
			boolean b = getBooleanPropertyValue(s.isSheared(), value);
			if (s.isSheared() != b) s.setSheared(b);
		}
	}
	
	/** Makes an Ageable mob an adult or baby */
	private void setTamed()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set tamed", FailedReason.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set tamed, " + value)) return;	
		
		for (Tameable t : getTameables())
		{
			boolean b = getBooleanPropertyValue(t.isTamed(), value);
			if (t.isTamed() != b) t.setTamed(b);
		}
	}
	
// Spawn action_type
	
	/** Spawns a mob/item/exp_orb, etc. */
	private void spawnSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("damage", FailedReason.NO_SUBACTION);
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
			actionFailed("spawn exp", FailedReason.ZERO_AMOUNT);
			return;
		}
		
		if (isActionCancelled("spawn exp " + amount)) return;
		
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("spawn exp " + amount, FailedReason.CHUNK_NOT_LOADED);
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
			actionFailed("spawn item", FailedReason.ZERO_ITEM_ID);
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
				actionFailed("spawn item " + getPrettyItem(is), FailedReason.CHUNK_NOT_LOADED);
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
			actionFailed("spawn mob", FailedReason.NO_MOB);
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
					actionFailed("spawn mob " + et + "(" + mob_name + ")", FailedReason.CHUNK_NOT_LOADED);
					continue;
				}
				
				Mobs.getInstance().setMob_name(mob_name);
				loc.getWorld().spawnEntity(loc, et);
			}
		}
		//TODO mob description ?
	}
	
// Write action_type	 
	
	/** Sends a message to the log/a player/the server */
	private void writeSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("damage", FailedReason.NO_SUBACTION);
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
			actionFailed("write message", FailedReason.NO_MESSAGE);
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
			actionFailed("write message", FailedReason.NO_MESSAGE);
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
			actionFailed("write message", FailedReason.NO_MESSAGE);
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
	
// Utils
	
	private boolean isAffected()
	{
		if (affected_mobs != null)
		{
			LivingEntity le = ev.getLivingEntity();
			if (le == null) return false;
			if (!affected_mobs.contains(le.getType().toString())) return false;			
		}
		
		/*s = orig.getAffectedWorlds();
		if (s != null)
		{
			World w = getWorld(orig, ev);
			if (w == null) return false;
			if (!Arrays.asList(s.replace(" ", "").split(",")).contains(w.getName().toUpperCase())) return false;
		}*/
		//TODO world stuff, including global
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
	
	/** */
	private boolean getBooleanPropertyValue(boolean orig, String s)
	{
		if (s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("true")) return true;
		
		if (s.equalsIgnoreCase("no") || s.equalsIgnoreCase("false")) return false;
		
		if (s.equalsIgnoreCase("random")) return new Random().nextBoolean();
		
		if (s.equalsIgnoreCase("toggle")) return !orig;
		
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
	
	private List<Location> getLocations()
	{//TODO stuff
		return null;
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
	private void actionFailed(String attempted, FailedReason reason)
	{
		if (!Mobs.allow_debug) return;//TODO pass stuff
		Bukkit.getServer().getPluginManager().callEvent(new MobsFailedActionEvent(this, attempted, reason));
	}
	
}