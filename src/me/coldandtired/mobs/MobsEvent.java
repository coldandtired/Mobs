package me.coldandtired.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.EntitySkinType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import me.coldandtired.extra_events.LivingEntityBlockEvent;
import me.coldandtired.extra_events.LivingEntityDamageEvent;
import me.coldandtired.extra_events.PlayerApproachLivingEntityEvent;
import me.coldandtired.extra_events.PlayerLeaveLivingEntityEvent;
import me.coldandtired.extra_events.PlayerNearLivingEntityEvent;
import me.coldandtired.mobs.api.Data;
import me.coldandtired.mobs.api.MobsFailedActionEvent;
import me.coldandtired.mobs.api.MobsPerformingActionEvent;
import me.coldandtired.mobs.Enums.*;

public class MobsEvent
{	
	private EventValues ev;
	private MobsElement ce;
	private Map<String, MobsElement> linked_actions;	
	private Map<String, MobsCondition> linked_conditions;
	private Map<String, MobsElement> linked_targets;
	private MobsElement root;
	
	MobsEvent(Element element) throws XPathExpressionException
	{				
		NodeList list = (NodeList)Mobs.getXPath().evaluate("linked_condition", element, XPathConstants.NODESET);
		if (list.getLength() != 0)
		{
			linked_conditions = new HashMap<String, MobsCondition>();
			for (int i = 0; i < list.getLength(); i++)
			{
				Element el = (Element)list.item(i);
				String name = el.getAttribute("name");
				if (name == null || name.isEmpty())
				{
					Mobs.error("A linked_condition is missing its name!");
					continue;
				}
				MobsCondition mc = MobsCondition.fill(el);
				if (mc != null) linked_conditions.put(name.toUpperCase(), mc);
			}
		}
		
		root = new MobsElement(element, null, linked_conditions);		

		list = (NodeList)Mobs.getXPath().evaluate("linked_action", element, XPathConstants.NODESET);
		if (list.getLength() != 0)
		{
			linked_actions = new HashMap<String, MobsElement>();
			for (int i = 0; i < list.getLength(); i++)
			{
				Element el = (Element)list.item(i);
				String name = el.getAttribute("name");
				if (name == null || name.isEmpty())
				{
					Mobs.error("A linked_action is missing its name!");
					continue;
				}
				linked_actions.put(name.toUpperCase(), new MobsElement(el, root, linked_conditions));
			}
		}
		
		list = (NodeList)Mobs.getXPath().evaluate("linked_target", element, XPathConstants.NODESET);
		if (list.getLength() != 0)
		{
			linked_targets = new HashMap<String, MobsElement>();
			for (int i = 0; i < list.getLength(); i++)
			{
				Element el = (Element)list.item(i);
				String name = el.getAttribute("name");
				if (name == null || name.isEmpty())
				{
					Mobs.error("A linked_target is missing its name!");
					continue;
				}
				linked_targets.put(name.toUpperCase(), new MobsElement(el, root, linked_conditions));
			}
		}
	}
		
	/** Performs all the actions on all the targets */
	public void performActions(EventValues ev)
	{
		ce = root;
		this.ev = ev;
		
		List<MobsElement> actions = getActions();
		if (actions == null)
		{
			actionFailed(null, ReasonType.NO_ACTION);
			return;
		}
		
		for (MobsElement me : actions)
		{		
			ce = me;
			String s = getAction();
			if (!Enums.isActionType(s)) continue;
			
			switch (ActionType.valueOf(s))
			{	
				//case ACTIVATE_BUTTON: activateSomething();
					//break;
				case BROADCAST: broadcastSomething();
					break;
				case CANCEL_EVENT: cancelEvent();
					break;			
				case CAUSE: causeSomething();
					break;					
				case DAMAGE: damageSomething();
					break;					
				case GIVE: giveSomething();
					break;	
				case KILL: killSomething();
					break;
				case LOG: logSomething();
					break;
				case PLAY: playSomething();
					break;				
				case REMOVE: removeSomething();
					break;					
				case SET: setSomething();
					break;					
				case SPAWN: spawnSomething();
					break;					
				case TELL: tellSomething();
					break;			
			}	
		}
	}

// Activate_button action
	
	/*private void activateSomething()
	{
		for (Location loc : getLocations())
		{
			if (!loc.getChunk().isLoaded())
			{
				actionFailed("activate_button", ReasonType.CHUNK_NOT_LOADED);
				continue;
			}
			Mobs.log(loc.toString());
			
			BlockState bs = loc.getBlock().getState();
			MaterialData md = bs.getData();
			if (md instanceof Button)
			{
				((Button)md).setPowered(true);
				((Button)md).setPowered(false);
			}
			else
			{
				actionFailed("activate_button", ReasonType.NOT_A_BUTTON);
				return;
			}
			bs.setData(md);
			bs.update(true);
		}
	}*/
	
	
// Broadcast action
	
	/** Sends a message to everyone on the server */
	private void broadcastSomething()
	{
		String message = getMessage();
		if (message == null)
		{
			actionFailed("broadcast", ReasonType.NO_MESSAGE);
			return;
		}		
		
		if (isActionCancelled("broadcast " + message)) return;
		
		Bukkit.getServer().broadcastMessage(message);
	}
	
// Cancel_event action
	
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
		
		int data = getItemData(0);
		
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
			
		for (Damageable d : getMobType(Damageable.class))
		{
			d.damage(amount);
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
		
		for (Player p : getMobType(Player.class)) p.giveExp(amount);
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
		
		int data = getItemData(0);
		int amount = getAmount(1);		
		
		ItemStack is = new ItemStack(id, amount, (short)data);
		if (isActionCancelled("give item " + getPrettyItem(is))) return;
			
		for (Player p : getMobType(Player.class)) p.getInventory().addItem(is);
	}
	
	/** Gives a player money (needs Vault) */
	private void giveMoney()
	{
		if (Mobs.getEconomy() == null)
		{
			actionFailed("give money", ReasonType.NO_VAULT);
			return;
		}
		
		int amount = getAmount(0);
		if (amount < 0)
		{
			actionFailed("give exp", ReasonType.BAD_AMOUNT);
			return;		
		}
		
		if (isActionCancelled("give money " + amount)) return;
		
		for (Player p : getMobType(Player.class)) Mobs.getEconomy().depositPlayer(p.getName(), amount);
	}
	
// Kill action
	
	/** Kills a mob */
	private void killSomething()
	{			
		if (isActionCancelled("kill")) return;
		
		for (Damageable d : getMobType(Damageable.class))
		{
			d.setHealth(0);
		}
	}	
	
// Log action
	
	/** Writes a message to the console */
	private void logSomething()
	{
		String message = getMessage();
		if (message == null)
		{
			actionFailed("log", ReasonType.NO_MESSAGE);
			return;
		}		
		
		if (isActionCancelled("log " + message)) return;
		
		Mobs.log(message);
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
			case ALL_DATA: removeData();
				break;
			case ALL_DROPS: removeAllDrops();
				break;
			case ALL_ITEMS: removeInventory();
				break;
			case DROPPED_EXP: removeDroppedExp();
				break;
			case DROPPED_ITEMS: removeDroppedItems();
				break;
			case ITEM: removeItem();
				break;
			case MAX_HP: removeMaxHp();
				break;
			case MOB: removeMob();
				break;
			case SKIN: removeSkin();
				break;
			default: removeProperty(st);
				break;
		}
	}
	
	/** Flags a mob to not drop anything on death */
	private void removeAllDrops()
	{
		if (isActionCancelled("remove all_drops")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.putData(le, SubactionType.NO_DROPPED_ITEMS);
			Data.putData(le, SubactionType.NO_DROPPED_EXP);
		}
	}
	
	/** Removes all data from a mob or block */
	private void removeData()
	{		
		if (isActionCancelled("remove data")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class)) Data.clearData(le);
	}
	
	/** Flags a mob to not drop any exp on death */
	private void removeDroppedExp()
	{
		if (isActionCancelled("remove dropped_exp")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.putData(le, SubactionType.NO_DROPPED_EXP);
		}
	}
	
	/** Flags a mob to not drop any items on death */
	private void removeDroppedItems()
	{
		if (isActionCancelled("remove dropped_items")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.putData(le, SubactionType.NO_DROPPED_ITEMS);
		}
	}
	
	/** Removes all matching items from a player's inventory */
	private void removeItem()
	{
		int id = getItemId();		
		int data = getItemData(-1);
		int amount = getAmount(0);
		
		if (id == 0 && data == -1 && amount == 0)
		{
			actionFailed("remove item", ReasonType.NO_ITEM);
			return;
		}
		
		for (Player p : getMobType(Player.class))
		{
			Inventory inv = p.getInventory();
			for (ItemStack is : inv.getContents())
			{
				if (is == null) continue;
				if (id > 0 && is.getTypeId() != id) continue;
				if (data > -1 && is.getData().getData() != data) continue;
				if (amount > 0 && is.getAmount() != amount) continue;
				inv.remove(is);
			}
		}
	}
	
	/** Clears all items from a player */
	private void removeInventory()
	{
		if (isActionCancelled("remove items")) return;
		
		for (Player p : getMobType(Player.class)) p.getInventory().clear();
	}
	
	/** Restore a mob's max_hp to its vanilla setting */
	private void removeMaxHp()
	{
		if (isActionCancelled("remove max_hp")) return;
		
		for (Damageable d : getMobType(Damageable.class))
		{
			d.resetMaxHealth();
		}
	}
	
	/** Removes a mob from the world without it dying */
	private void removeMob()
	{
		if (isActionCancelled("remove mob")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
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
		
		for (LivingEntity le : getMobType(LivingEntity.class)) Spout.getServer().resetEntitySkin(le);
	}
	
	/** Removes some data from a mob */
	private void removeProperty(SubactionType st)
	{		
		if (isActionCancelled("remove " + st)) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.removeData(le, st);
		}
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
		int data = getItemData(0);
		
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
		}	
		
		World w = ev.getWorld();
		int time = adjustNumber((int)w.getTime(), getNumber(value));
		
		if (isActionCancelled("set time" + ", " + time)) return;
		
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
		}
		
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
		
		World w = ev.getWorld();
		
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
		
		for (Ageable a : getMobType(Ageable.class))
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
		
		for (LivingEntity le : getMobType(LivingEntity.class))
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
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			int i = Data.hasData(le, st) ? (Integer)Data.getData(le, st) : 0;
			amount = adjustNumber(amount, i);
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
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.putData(le, st, value);
		}
	}
		
	/** Sets a mob's health */
	private void setHp()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set hp", ReasonType.NO_VALUE);
			return;
		}	
		
		int amount = getNumber(value);
		
		if (isActionCancelled("set hp " + amount)) return;
		
		for (Damageable d : getMobType(Damageable.class))
		{
			amount = adjustNumber(amount, d.getHealth());
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
		
		for (Damageable d : getMobType(Damageable.class))
		{
			amount = adjustNumber(amount, d.getMaxHealth());
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
		
		for (Player p : getMobType(Player.class))
		{
			amount = adjustNumber(amount, p.getLevel());
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
		
		for (LivingEntity le : getMobType(LivingEntity.class))
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
		
		for (Ocelot o : getMobType(Ocelot.class)) o.setCatType(ot);
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
		
		for (Tameable t : getMobType(Tameable.class))
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
		
		for (Creeper c : getMobType(Creeper.class))
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
		
		for (Pig p : getMobType(Pig.class))
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
		
		for (Sheep s : getMobType(Sheep.class))
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
		
		for (Slime s : getMobType(Slime.class))
		{
			size = adjustNumber(size, s.getSize());
			s.setSize(size);
		}
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
		
		for (LivingEntity le : getMobType(LivingEntity.class))
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
		
		for (Tameable t : getMobType(Tameable.class))
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
		
		for (LivingEntity le : getMobType(LivingEntity.class))
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
		
		for (Villager v : getMobType(Villager.class)) v.setProfession(vp);
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
		
		for (Sheep s : getMobType(Sheep.class)) s.setColor(dc);
	}
	
// spawn action
	
	/** Spawns a mob/item/exp_orb, etc. */
	private void spawnSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("spawn", ReasonType.NO_SUBACTION);
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
		
		int data = getItemData(0);
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
				
				Mobs.setMobName(mob_name);
				loc.getWorld().spawnEntity(loc, et);
			}
		}
		//TODO mob description ?
	}
	
// Tell action
	
	/** Sends a message to one player */
	private void tellSomething()
	{		
		String message = getMessage();
		if (message == null)
		{
			actionFailed("tell", ReasonType.NO_MESSAGE);
			return;
		}		
		
		if (isActionCancelled("tell " + message)) return;
			
		for (Player p : getMobType(Player.class)) p.sendMessage(message);
	}
	
// MobsElement getters
	
	private String getAction()
	{
		MobsElement me = ce.getCurrentElement(ElementType.ACTION, ev);
		if (me == null) return null;

		ce = me;
		return ce.getString(ElementType.ACTION).toUpperCase();
	}
	
	private List<MobsElement> getActions() 
	{
		String s = getAction();
		List<MobsElement> list = new ArrayList<MobsElement>();
		if (Enums.isActionType(s)) list.add(ce);
		
		else if (linked_actions != null)
		{
			String[] temp = s.replace(" ", "").split(",");
			String ss = temp[new Random().nextInt(temp.length)];
			temp = ss.split("\\+");
			for (String la : temp)
			{
				MobsElement me = linked_actions.get(la);
				if (me == null) continue;
				
				me.setParent(ce); list.add(me);
			}
		}
		return list;
	}
		
	private int getAmount(int orig)
	{
		MobsElement me = ce.getCurrentElement(ElementType.AMOUNT, ev);
		if (me == null) return orig;

		ce = me;
		return getNumber(ce.getString(ElementType.AMOUNT));
	}
	
	private AmountType getAmountType()
	{
		MobsElement me = ce.getCurrentElement(ElementType.AMOUNT_TYPE, ev);
		if (me == null) return null;
		
		ce = me;
		return AmountType.valueOf(ce.getString(ElementType.AMOUNT_TYPE));
	}
	
	private int getDuration()
	{
		MobsElement me = ce.getCurrentElement(ElementType.DURATION, ev);
		if (me == null) return 0;

		ce = me;
		return getNumber(ce.getString(ElementType.DURATION)) * 20;//TODO docs = ticks
	}
	
	private Effect getEffect()
	{
		MobsElement me = ce.getCurrentElement(ElementType.EFFECT, ev);
		if (me == null) return null;

		ce = me;
		return Effect.valueOf(ce.getString(ElementType.EFFECT).toUpperCase());	
	}
	
	private int getItemData(int orig)
	{
		MobsElement me = ce.getCurrentElement(ElementType.ITEM_DATA, ev);
		if (me == null) return orig;

		ce = me;
		return getNumber(ce.getString(ElementType.ITEM_DATA));
	}
	
	private int getItemId()
	{
		MobsElement me = ce.getCurrentElement(ElementType.ITEM_ID, ev);
		if (me == null) return 0;

		ce = me;
		return getNumber(ce.getString(ElementType.ITEM_ID));
	}
	
	private String getMessage()
	{
		MobsElement me = ce.getCurrentElement(ElementType.MESSAGE, ev);
		if (me == null) return null;
		
		ce = me;
		return ce.getString(ElementType.MESSAGE);
	}
	
	private EntityType getMob()
	{
		MobsElement me = ce.getCurrentElement(ElementType.MOB, ev);
		if (me == null) return null;

		ce = me;
		return EntityType.valueOf(ce.getString(ElementType.MOB).toUpperCase());	
	}
	
	private String getMobName()
	{
		MobsElement me = ce.getCurrentElement(ElementType.MOB_NAME, ev);
		if (me == null) return null;

		ce = me;
		return ce.getString(ElementType.MOB_NAME);
	}
	
	private int getSize(int orig)
	{
		MobsElement me = ce.getCurrentElement(ElementType.SIZE, ev);
		if (me == null) return orig;

		ce = me;
		return getNumber(ce.getString(ElementType.SIZE));
	}
	
	private Sound getSound()
	{
		MobsElement me = ce.getCurrentElement(ElementType.SOUND, ev);
		if (me == null) return null;

		ce = me;
		return Sound.valueOf(ce.getString(ElementType.SOUND).toUpperCase());	
	}
	
	private float getSoundPitch()
	{
		MobsElement me = ce.getCurrentElement(ElementType.SOUND_PITCH, ev);
		if (me == null) return 1.0f;

		ce = me;
		return Float.parseFloat(ce.getString(ElementType.SOUND_PITCH)) / 100;
	}
	
	private float getSoundVolume()
	{
		MobsElement me = ce.getCurrentElement(ElementType.SOUND_VOLUME, ev);
		if (me == null) return 1.0f;

		ce = me;
		return Float.parseFloat(ce.getString(ElementType.SOUND_VOLUME)) / 100;
	}
	
	private SubactionType getSubaction()
	{
		MobsElement me = ce.getCurrentElement(ElementType.SUB, ev);
		if (me == null) return null;

		ce = me;
		return SubactionType.valueOf(ce.getString(ElementType.SUB).toUpperCase());
	}
	
	private String getTarget()
	{
		MobsElement me = ce.getCurrentElement(ElementType.TARGET, ev);
		if (me == null) return null;

		ce = me;
		return ce.getString(ElementType.TARGET).toUpperCase();
	}
	
	@SuppressWarnings("unchecked")
	private List<Object> getTargets() 
	{
		String s = getTarget();
		if (s == null) s = "SELF";
		List<MobsElement> list = new ArrayList<MobsElement>();
		if (Enums.isTargetType(s)) list.add(ce);
		
		else if (linked_targets != null)
		{
			String[] temp = s.replace(" ", "").split(",");
			String ss = temp[new Random().nextInt(temp.length)];
			temp = ss.split("\\+");
			for (String la : temp)
			{
				MobsElement me = linked_targets.get(la);
				if (me == null) continue;
				
				me.setParent(ce); list.add(me);
			}
		}
		
		List<Object> mobs = new ArrayList<Object>();
		for (MobsElement me : list)
		{
			ce = me;
			Object o = getMCTarget();
			if (o instanceof List<?>)
			{
				mobs.addAll((List<Object>)o);
			}
			else mobs.add(o);
		}
	
		return mobs;
	}
	
	private int getTargetAmount(int orig)
	{
		MobsElement me = ce.getCurrentElement(ElementType.TARGET_AMOUNT, ev);
		if (me == null) return orig;
		
		ce = me;
		return getNumber(ce.getString(ElementType.TARGET_AMOUNT));
	}
	
	private String getTargetName()
	{
		MobsElement me = ce.getCurrentElement(ElementType.TARGET_NAME, ev);
		if (me == null) return null;
		
		ce = me;
		return ce.getString(ElementType.TARGET_NAME);
	}

	/*private int getTargetXOffset(int orig)
	{
		MobsElement me = ce.getCurrentElement(ElementType.TARGET_X_OFFSET, ev);
		if (me == null) return orig;
		
		ce = me;
		return getNumber(ce.getString(ElementType.TARGET_X_OFFSET));
	}
	
	private int getTargetYOffset(int orig)
	{
		MobsElement me= ce.getCurrentElement(ElementType.TARGET_Y_OFFSET, ev);
		if (me == null) return orig;
		
		ce = me;
		return getNumber(ce.getString(ElementType.TARGET_Y_OFFSET));
	}
	
	private int getTargetZOffset(int orig)
	{
		MobsElement me = ce.getCurrentElement(ElementType.TARGET_Z_OFFSET, ev);
		if (me == null) return orig;
		
		ce = me;
		return getNumber(ce.getString(ElementType.TARGET_Z_OFFSET));
	}*/
	
	private String getValue()
	{
		MobsElement me = ce.getCurrentElement(ElementType.VALUE, ev);
		if (me == null) return null;
		
		ce = me;
		return ce.getString(ElementType.VALUE);
	}
	
	private ValueType getValueType()
	{
		String s = getValue();
		if (s == null) return null;
		
		return ValueType.valueOf(s.toUpperCase());
	}
	
	private String getX()
	{
		MobsElement me = ce.getCurrentElement(ElementType.X, ev);
		if (me == null) return null;
		
		ce = me;
		return ce.getString(ElementType.X);
	}
	
	private String getY()
	{
		MobsElement me= ce.getCurrentElement(ElementType.Y, ev);
		if (me == null) return null;
		
		ce = me;
		return ce.getString(ElementType.Y);
	}
	
	private String getZ()
	{
		MobsElement me = ce.getCurrentElement(ElementType.Z, ev);
		if (me == null) return null;
		
		ce = me;
		return ce.getString(ElementType.Z);
	}
	
// Utils
		
	/** Returns a randomized int */
	private int getNumber(String s)
	{
		s = s.replace(" ", "").toUpperCase();
		String[] temp = s.split(",");
		String s2 = temp[new Random().nextInt(temp.length)];
		if (s2.contains("TO"))
		{
			String[] temp2 = s2.split("TO");
			return new Random().nextInt(Integer.parseInt(temp2[1]) - Integer.parseInt(temp2[0])) +
					Integer.parseInt(temp2[0]);
		}
		else if (Enums.isMaterial(s2))
		{
			return Material.valueOf(s2).getId();
		}
		else return Integer.parseInt(s2);
	}
	
	/** Adjusts an int by percentage */
	private int adjustNumber(int orig, int value)
	{
		AmountType at = getAmountType();
		if (at == null) return orig;
		
		switch (at)
		{
			case ABSOLUTE: return orig;
			case DEC: return orig - value;
			case DEC_PERCENT: return orig - ((orig / 100) * value);
			case INC: return orig + value;
			case INC_PERCENT: return orig + ((orig / 100) * value);
			case PERCENT: return (orig / 100) * value;
		}
		return orig;
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
		return "" + is.getType() + ":" + is.getData().getData() + " x " + is.getAmount();
	}
	
	/** returns a formatted location */
	private String getPrettyLoc(Location loc)
	{
		return "x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:" + loc.getBlockZ() + " (" + loc.getWorld().getName() + ")";
	}
			
	/** Returns an object or a list of objects (LivingEntity or Location) to have actions performed on */
	private Object getMCTarget()
	{
		String tt = getTarget();
		if (tt == null)
		{
			if (ev.getLivingEntity() != null) return ev.getLivingEntity();
		}	
		
		switch (TargetType.valueOf(tt))
		{			
			case APPROACHED_PLAYER:
			if (!(ev.getOrigEvent() instanceof PlayerApproachLivingEntityEvent))
			{
				actionFailed(ev.getMobsEvent(), ReasonType.NOT_THE_APPROACHES_EVENT);
				return null;
			}
			return ((PlayerApproachLivingEntityEvent)ev.getOrigEvent()).getPlayer();
			
			case ATTACKER:
			if (ev.getOrigEvent() instanceof EntityDamageByEntityEvent
					|| ev.getOrigEvent() instanceof LivingEntityBlockEvent
					|| ev.getOrigEvent() instanceof LivingEntityDamageEvent)
			{
				return ev.getAuxMob();
			}
			else
			{
				actionFailed(ev.getMobsEvent(), ReasonType.NOT_AN_EVENT_WITH_AN_ATTACKER);
				return null;
			}
			
			case BLOCK:
				World w = ev.getWorld();
				String temp = getX();
				if (temp == null)
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NO_X);
					return null;
				}
				int x = getNumber(temp);
				
				temp = getY();
				if (temp == null)
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NO_Y);
					return null;
				}
				int y = getNumber(temp);
				if (y > w.getMaxHeight())
				{
					actionFailed(ev.getMobsEvent(), ReasonType.Y_EXCEEDS_MAX_HEIGHT);
					return null;
				}
				
				temp = getZ();
				if (temp == null)
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NO_Z);
					return null;
				}
				int z = getNumber(temp);
				
				return new Location(w, x, y, z);
				
			case CLOSEST:	
			case CLOSEST_BAT:
			case CLOSEST_BLAZE:
			case CLOSEST_CAVE_SPIDER:
			case CLOSEST_CHICKEN:
			case CLOSEST_COW:
			case CLOSEST_CREEPER:
			case CLOSEST_ENDER_DRAGON:
			case CLOSEST_ENDERMAN:
			case CLOSEST_GHAST:
			case CLOSEST_GIANT:
			case CLOSEST_GOLEM:
			case CLOSEST_IRON_GOLEM:
			case CLOSEST_MAGMA_CUBE:
			case CLOSEST_MUSHROOM_COW:
			case CLOSEST_OCELOT:
			case CLOSEST_PIG:
			case CLOSEST_PIG_ZOMBIE:
			case CLOSEST_PLAYER:
			case CLOSEST_SILVERFISH:
			case CLOSEST_SHEEP:
			case CLOSEST_SKELETON:
			case CLOSEST_SLIME:
			case CLOSEST_SNOWMAN:
			case CLOSEST_SPIDER:
			case CLOSEST_SQUID:
			case CLOSEST_VILLAGER:
			case CLOSEST_WITCH:
			case CLOSEST_WITHER:
			case CLOSEST_WOLF:
			case CLOSEST_ZOMBIE:
				LivingEntity orig = ev.getLivingEntity();
				if (orig == null)
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NO_MATCHING_TARGET);
					return null;			
				}
				
				String s = tt.replace("CLOSEST", "");
				if (s == "") s = "LIVINGENTITY";
				else if (s.startsWith("_")) s = s.replaceFirst("_", "");
				
				List<LivingEntity> mobs = getRelevantMobs(orig.getNearbyEntities(50, 10, 50), s, getTargetName());
				if (mobs.size() == 0)
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NO_MATCHING_TARGET);
					return null;			
				}
				
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
				
				int i = getTargetAmount(1);
				if (i == 1) return nearby_mobs.get(0).getLivingEntity();
				
				if (i > nearby_mobs.size()) i = nearby_mobs.size();
				nearby_mobs = nearby_mobs.subList(0, i);
				mobs.clear();
				for (NearbyMob m : nearby_mobs)
				{
					mobs.add(m.getLivingEntity());
				}
				return mobs;
				
			case KILLER:
				if (!(ev.getOrigEvent() instanceof EntityDeathEvent || ev.getOrigEvent() instanceof PlayerDeathEvent))
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NOT_THE_DIES_EVENT);
					return null;
				}
				return ev.getAuxMob();
				
			case LEFT_PLAYER:
				if (!(ev.getOrigEvent() instanceof PlayerLeaveLivingEntityEvent))
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NOT_THE_LEAVES_EVENT);
					return null;
				}
				return ev.getAuxMob();
				
			case NEAR_PLAYER:
				if (!(ev.getOrigEvent() instanceof PlayerNearLivingEntityEvent))
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NOT_THE_NEAR_EVENT);
					return null;
				}
				return ev.getAuxMob();
				
			case OWNER:
				if (!(ev.getLivingEntity() instanceof Tameable))
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NOT_A_TAMEABLE_MOB);
					return null;
				}
				return ((Tameable)ev.getLivingEntity()).getOwner();
				
			case PLAYER:
				s = getTargetName();
				if (s == null)
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NO_PLAYER);
					return null;
				}
				return Bukkit.getPlayer(s);
				
			case RANDOM:	
			case RANDOM_BAT:
			case RANDOM_BLAZE:
			case RANDOM_CAVE_SPIDER:
			case RANDOM_CHICKEN:
			case RANDOM_COW:
			case RANDOM_CREEPER:
			case RANDOM_ENDER_DRAGON:
			case RANDOM_ENDERMAN:
			case RANDOM_GHAST:
			case RANDOM_GIANT:
			case RANDOM_GOLEM:
			case RANDOM_IRON_GOLEM:
			case RANDOM_MAGMA_CUBE:
			case RANDOM_MUSHROOM_COW:
			case RANDOM_OCELOT:
			case RANDOM_PIG:
			case RANDOM_PIG_ZOMBIE:
			case RANDOM_PLAYER:
			case RANDOM_SILVERFISH:
			case RANDOM_SHEEP:
			case RANDOM_SKELETON:
			case RANDOM_SLIME:
			case RANDOM_SNOWMAN:
			case RANDOM_SPIDER:
			case RANDOM_SQUID:
			case RANDOM_VILLAGER:
			case RANDOM_WITCH:
			case RANDOM_WITHER:
			case RANDOM_WOLF:
			case RANDOM_ZOMBIE:
				w = ev.getWorld();
				
				s = tt.replace("RANDOM", "");
				if (s == "") s = "LIVINGENTITY";
				else if (s.startsWith("_")) s = s.replaceFirst("_", "");
				
				mobs = getRelevantMobs(w.getEntities(), s, getTargetName());
				if (mobs.size() == 0)
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NO_MATCHING_TARGET);
					return null;			
				}
				
				i = getTargetAmount(1);
				Collections.shuffle(mobs);
				if (i == 1) return mobs.get(0);
				
				if (i > mobs.size()) i = mobs.size();
				return mobs.subList(0, i);
				
			case SHEARER:
				if (!(ev.getOrigEvent() instanceof PlayerShearEntityEvent))
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NOT_THE_SHEARS_EVENT);
					return null;
				}
				return ev.getAuxMob();
				
			case TAMER:
				if (!(ev.getOrigEvent() instanceof EntityTameEvent))
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NOT_THE_TAMES_EVENT);
					return null;
				}
				return ev.getAuxMob();
				
			case TARGETED:
				if (!(ev.getOrigEvent() instanceof EntityTargetLivingEntityEvent))
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NOT_THE_TARGETS_EVENT);
					return null;
				}
				return ev.getAuxMob();
			
			default: if (ev.getLivingEntity() != null) return ev.getLivingEntity();
				break;
		}
		
		return null;
	}
	
	private List<LivingEntity> getRelevantMobs(List<Entity> orig, String m, String name)
	{		
		List<LivingEntity> mobs = new ArrayList<LivingEntity>();
		
		for (Entity e : orig)
		{
			if (!m.equalsIgnoreCase(e.getType().toString())) continue;
			if (name != null)
			{
				String s = e instanceof Player ? ((Player)e).getName() : (String)Data.getData(e, SubactionType.NAME);
				if (s == null || !s.equalsIgnoreCase(name)) continue;
			}
			mobs.add((LivingEntity)e);
		}
		return mobs;
	}
	
	/** Returns a list of target locations, using livingentity if necessary */
	private List<Location> getLocations()
	{//TODO add chunk check here
		Object target = getMCTarget();
		
		List<Location> temp = new ArrayList<Location>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				if (o instanceof Location) temp.add((Location)o);
				else if (o instanceof LivingEntity) temp.add(((LivingEntity)o).getLocation());
			}
		}
		else
		{
			if (target instanceof Location) temp.add((Location)target);
			else if (target instanceof LivingEntity) temp.add(((LivingEntity)target).getLocation());
		}
		return temp;
	}
	
	/** Returns a list of the relevant mobs (pigs, ageables, etc.) */
	@SuppressWarnings("unchecked")
	private <T> List<T> getMobType(Class<T> type)
	{
		List<T> temp = new ArrayList<T>();
		List<Object> targets = getTargets();
		if (targets == null) return temp;
		
		for (Object o : targets)
		{
			if (type.isInstance(o))
			{
				if (o instanceof Player && !((Player)o).isOnline()) continue;
				temp.add((T)o);
			}
		}
		return temp;
	}
	
	/** Calls an event when an action is about to be performed, with the possibility of cancelling */
	private boolean isActionCancelled(String attempting)
	{
		if (Mobs.canDebug())
		{
			MobsPerformingActionEvent mpae = new MobsPerformingActionEvent(attempting, this);
			Bukkit.getServer().getPluginManager().callEvent(mpae);
			return mpae.isCancelled();
		}
		return false;
	}
	
	/** Calls an event when an action fails (due to wrong type of mob, etc.) */
	private void actionFailed(Object attempted, ReasonType reason)
	{
		if (!Mobs.canDebug()) return;
		Bukkit.getServer().getPluginManager().callEvent(new MobsFailedActionEvent(attempted.toString(), reason, this));
	}
}