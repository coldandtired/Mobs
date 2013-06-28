package eu.sylian.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.EntitySkinType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.sylian.mobs.Enums.*;
import eu.sylian.mobs.api.Data;
import eu.sylian.mobs.api.MobsFailedActionEvent;
import eu.sylian.mobs.api.MobsPerformingActionEvent;

import eu.sylian.extraevents.Area;
import eu.sylian.extraevents.LivingEntityBlockEvent;
import eu.sylian.extraevents.LivingEntityDamageEvent;
import eu.sylian.extraevents.PlayerApproachLivingEntityEvent;
import eu.sylian.extraevents.PlayerLeaveLivingEntityEvent;
import eu.sylian.extraevents.PlayerNearLivingEntityEvent;

public class MobsEvent
{
	private EventValues ev;
	private MobsElement ce;
	private Map<String, MobsElement> linked_actions;	
	private Map<String, MobsCondition> linked_conditions;
	private Map<String, MobsElement> linked_items;
	private Map<String, MobsElement> linked_targets;
	private Map<String, MobsElement> linked_enchantments;
	private List<Object> current_targets;
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
		
		list = (NodeList)Mobs.getXPath().evaluate("linked_item", element, XPathConstants.NODESET);
		if (list.getLength() != 0)
		{
			linked_items = new HashMap<String, MobsElement>();
			for (int i = 0; i < list.getLength(); i++)
			{
				Element el = (Element)list.item(i);
				String name = el.getAttribute("name");
				if (name == null || name.isEmpty())
				{
					Mobs.error("A linked_item is missing its name!");
					continue;
				}
				linked_items.put(name.toUpperCase(), new MobsElement(el, root, linked_conditions));
			}
		}
		
		list = (NodeList)Mobs.getXPath().evaluate("linked_enchantment", element, XPathConstants.NODESET);
		if (list.getLength() != 0)
		{
			linked_enchantments = new HashMap<String, MobsElement>();
			for (int i = 0; i < list.getLength(); i++)
			{
				Element el = (Element)list.item(i);
				String name = el.getAttribute("name");
				if (name == null || name.isEmpty())
				{
					Mobs.error("A linked_enchantment is missing its name!");
					continue;
				}
				linked_enchantments.put(name.toUpperCase(), new MobsElement(el, root, linked_conditions));
			}
		}
	}
		
	/** Performs all the actions on all the targets 
	 * @throws ClassNotFoundException */
	public void performActions(EventValues ev)
	{
		ce = root;
		this.ev = ev;
		current_targets = null;
		
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
				case ATTACK: attackSomething();
					break;
				case BROADCAST: broadcastSomething();
					break;
				case CANCEL_EVENT: cancelEvent();
					break;			
				case CAUSE: causeSomething();
					break;					
				case DAMAGE: damageSomething();
					break;	
				case EXECUTE: executeCommand();
					break;
				case GIVE: giveSomething();
					break;	
				case KILL: killSomething();
					break;
				case LOG: logSomething();
					break;				
				case REMOVE: removeSomething();
					break;	
				case RESET: resetSomething();
					break;
				case SET: setSomething();
					break;	
				case SHOOT: shootSomething();
					break;
				case SPAWN: spawnSomething();
					break;					
				case TELL: tellSomething();
					break;			
			}	
		}
	}

	/** Disabled due to Bukkit bug */
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
	
// Attack action
	
	private void attackSomething()
	{
		final int amount = getAmount(0);
			
		if (isActionCancelled("attack mob " + amount)) return;
			
		for (final Damageable d : getMobType(Damageable.class))
		{
			for (final Entity e : d.getNearbyEntities(30, 10, 30))
			{
				if (e instanceof Player)
				{
					Bukkit.getScheduler().runTaskLater(Mobs.getPlugin(), new Runnable()
					{
						public void run()
						{
							d.damage(amount, e);
						}
					}, 5);
					break;
				}				
			}
		}
	}
	
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
		
		message = replaceText(message);
		
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
			case EFFECT: causeEffect();
				break;
			case EXPLOSION: causeExplosion();
				break;
			case FIERY_EXPLOSION: causeFieryExplosion();
				break;
			case LIGHTNING: causeLightning();
				break;
			case LIGHTNING_EFFECT: causeLightningEffect();
				break;
			case SOUND: causeSound();
				break;
		}
	}
	
	/** Causes a visual or sound effect */
	private void causeEffect()
	{
		Effect effect = getEffect();
		if (effect == null)
		{
			actionFailed("cause effect", ReasonType.NO_EFFECT);
			return;
		}
		
		if (isActionCancelled("cause effect " + effect)) return;
		
		for (Location loc : getLocations(true))
		{
			loc.getWorld().playEffect(loc, effect, 10);
		}
	}
	
	/** Causes an explosion */
	private void causeExplosion()
	{		
		int size = getSize(1);
		
		if (isActionCancelled("cause explosion " + size)) return; 
		
		for (Location loc : getLocations(true))
		{
			loc.getWorld().createExplosion(loc, size);
		}
	}

	/** Causes an explosion which sets blocks on fire */
	private void causeFieryExplosion()
	{
		int size = getSize(1);
		
		if (isActionCancelled("cause fiery_explosion " + size)) return; 
		
		for (Location loc : getLocations(true))
		{
			loc.getWorld().createExplosion(loc, size, true);
		}
	}
	
	/** Strikes a location or mob with lightning */
	private void causeLightning()
	{
		if (isActionCancelled("cause lightning")) return; 
		
		for (Location loc : getLocations(true))
		{
			loc.getWorld().strikeLightning(loc);
		}
	}

	/** Shows lightning without damaging a location or mob */
	private void causeLightningEffect()
	{		
		if (isActionCancelled("cause lightning_effect")) return; 
		
		for (Location loc : getLocations(true))
		{
			loc.getWorld().strikeLightningEffect(loc);
		}
	}
	
	/** Plays a sound */
	private void causeSound()
	{
		Sound sound = getSound();
		if (sound == null)
		{
			actionFailed("cause sound", ReasonType.NO_SOUND);
			return;
		}
		
		float volume = getSoundVolume();
		float pitch = getSoundPitch();
		
		if (isActionCancelled("cause sound " + sound + "(" + volume + ", " + pitch + ")")) return;
		
		for (Location loc : getLocations(true))
		{
			loc.getWorld().playSound(loc, sound, volume, pitch);
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
		List<ItemStack> items = getItems();
		
		if (items == null || items.size() == 0)
		{
			if (isActionCancelled("damage block")) return;
			
			for (Location loc : getLocations(true))
			{
				loc.getBlock().breakNaturally();
			}
			return;
		}
		
		ItemStack is = items.get(0);
		if (isActionCancelled("damage block, " + getPrettyItem(is))) return;
		
		for (Location loc : getLocations(true))
		{
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
	
// Execute Command
	
	private void executeCommand()
	{
		String command = getCommand();
		if (command == null)
		{
			actionFailed("execute", ReasonType.NO_COMMAND);
			return;
		}		
		command = replaceText(command);
		
		if (isActionCancelled("execute " + command)) return;
		
		try
		{
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		}
		catch (Exception e)
		{
			Mobs.error("Something went wrong with the command!");
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
			case CUSTOM_DROPS: giveCustomDrops();
				break;
			case EXP: giveExp();
				break;
			case ITEM: giveItem();
				break;
			case MONEY: giveMoney();
				break;
		}
	}
	
	/** Adds items to drop when a mob or player dies */
	@SuppressWarnings("unchecked")
	private void giveCustomDrops()
	{
		List<ItemStack> list = getItems();

		if (list == null || list.size() == 0)
		{
			actionFailed("give custom_drops", ReasonType.NO_ITEM);
			return;
		}
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			if (isActionCancelled("give custom_drops " + le.toString())) continue;
			if (Data.hasData(le, SubactionType.CUSTOM_DROPS))
			{
				List<ItemStack> temp = (List<ItemStack>)Data.getData(le, SubactionType.CUSTOM_DROPS);
				temp.addAll(list);
				Data.putData(le, SubactionType.CUSTOM_DROPS, temp);
			}
			else Data.putData(le, SubactionType.CUSTOM_DROPS, list);
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
		//TODO set in hand
	//TODO other events
	//TODO EE enter area all LEs.
	/** Sends an item to players */
	private void giveItem()
	{
		List<ItemStack> list = getItems();
		
		if (list == null || list.size() == 0)
		{
			actionFailed("give item", ReasonType.NO_ITEM);
			return;
		}
		
		for (Player p : getMobType(Player.class))
		{
			PlayerInventory inv = p.getInventory();
			for (ItemStack is : list)
			{
				if (isActionCancelled("give item " + getPrettyItem(is) + " (" + p.getName() + ")")) continue;
				inv.addItem(is);
			}
		}
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
			actionFailed("give money", ReasonType.BAD_AMOUNT);
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
		message = replaceText(message);
		
		if (isActionCancelled("log " + message)) return;
		
		Mobs.log(message);
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
			case ALL_ITEMS: removeInventory();
				break;
			case CUSTOM_DROPS: removeCustomDrops();
				break;
			case DEFAULT_DROPS: removeDefaultDrops();
				break;
			case DROPPED_EXP: removeDroppedExp();
				break;
			case ITEM: removeItem();
				break;
			case MOB: removeMob();
				break;
		}
	}
	
	/** Flags a mob to not drop anything on death */
	private void removeAllDrops()
	{
		if (isActionCancelled("remove all_drops")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.putData(le, SubactionType.NO_DROPS);
		}
	}
	
	/** Flags a mob to not drop any custom items on death */
	private void removeCustomDrops()
	{
		if (isActionCancelled("remove custom_drops")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.putData(le, SubactionType.CUSTOM_DROPS);
		}
	}
	
	/** Flags a mob to not drop its default items on death */
	private void removeDefaultDrops()
	{
		if (isActionCancelled("remove default_drops")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.putData(le, SubactionType.NO_DEFAULT_DROPS);
		}
	}
	
	/** Flags a mob to not drop any exp on death */
	private void removeDroppedExp()
	{
		if (isActionCancelled("remove dropped_exp")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.putData(le, SubactionType.DROPPED_EXP, 0);
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
		
	/** Removes a mob from the world without it dying */
	private void removeMob()
	{
		if (isActionCancelled("remove mob")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			if (!(le instanceof Player)) le.remove();
		}
	}
			
// Reset action

	/** Resets values changed by the plugin */
	private void resetSomething()
	{
		SubactionType st = getSubaction();
		if (st == null)
		{
			actionFailed("reset", ReasonType.NO_SUBACTION);
			return;
		}
		
		switch (st)
		{
			case ALL_DATA: resetAllData();
				break;
			case ALL_DROPS: resetAllDrops();
				break;
			case MAX_HP: resetMaxHp();
				break;
			case SKIN: resetSkin();
				break;
			default: resetProperty(st);
				break;
		}
	}
	
	/** Removes all data from a mob or block */
	private void resetAllData()
	{		
		if (isActionCancelled("reset all_data")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class)) Data.clearData(le);
	}
	
	/** Resets what a mob drops on death */
	private void resetAllDrops()
	{
		if (isActionCancelled("reset all_drops")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.removeData(le, SubactionType.CUSTOM_DROPS);
			Data.removeData(le, SubactionType.DROPPED_EXP);
			Data.removeData(le, SubactionType.NO_DROPS);
			Data.removeData(le, SubactionType.NO_DEFAULT_DROPS);
		}
	}
	
	/** Restore a mob's max_hp to its vanilla setting */
	private void resetMaxHp()
	{
		if (isActionCancelled("reset max_hp")) return;
		
		for (Damageable d : getMobType(Damageable.class))
		{
			d.resetMaxHealth();
		}
	}	
	
	/** Resets some data from a mob */
	private void resetProperty(SubactionType st)
	{		
		if (isActionCancelled("remove " + st)) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.removeData(le, st);
		}
	}
	
	/** Returns a mob's skin to default (requires Spout) */
	private void resetSkin()
	{
		if (!Mobs.isSpoutEnabled())
		{
			actionFailed("reset skin", ReasonType.NO_SPOUT);
			return;
		}
		
		if (isActionCancelled("reset skin")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Spout.getServer().resetEntitySkin(le);
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
			case BOOTS: setBoots();
				break;
			case CHESTPLATE: setChestplate();
				break;
			case CURRENT_INV_SLOT: setCurrentInvSlot();
				break;
			case CUSTOM_DROPS: setCustomDrops();
				break;
			case EXP: setExp();
				break;
			case FULL_HP: setFullHp();
				break;
			case HELMET: setHelmet();
				break;
			case HP: setHp();
				break;
			case ITEM_IN_HAND: setItemInHand();
				break;
			case INV_SLOT: setInvSlot();
				break;
			case LEGGINGS: setLeggings();
				break;
			case LEVEL: setLevel();
				break;
			case MAX_HP: setMaxHp();
				break;  
			case MONEY: setMoney();
				break;
			case NAME: setName();
				break;
			case NAME_IS_VISIBLE: setNameIsVisible();
				break;
			case OCELOT: setOcelot();
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
			case SHOULD_DESPAWN: setShouldDespawn();
				break;
			case SIZE: setSize();
				break;
			case SKELETON: setSkeleton();
				break;
			case SKIN: setSkin();
				break;
			case TAMED: setTamed();
				break;
			case TIME: setTime();
				break;
			case VILLAGER: setVillager();
				break;
			case VISIBLE_NAME: setVisibleName();
				break;
			case WEATHER: setWeather();
				break;
			case WOOL: setWool();
				break;
			case ZOMBIE_IS_VILLAGER: setZombieVillager();
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
			case NO_DEFAULT_DROPS:
			case NO_DESTROY_BLOCKS:
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
			case DROPPED_EXP:
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
			case CUSTOM_STRING_10: setCustomValue(st);
				break;
		}
		//TODO flexidamage?
	}
	
	private void setBlock()
	{
		List<ItemStack> items = getItems();
		
		if (items == null || items.size() == 0)
		{
			actionFailed("set block", ReasonType.NO_ITEM);
			return;
		}
		
		ItemStack is = items.get(0);
		if (isActionCancelled("set block, " + getPrettyItem(is))) return;
				
		for (Location loc : getLocations(true))
		{
			loc.getBlock().setTypeIdAndData(is.getTypeId(), is.getData().getData(), false);
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
		
		for (Location loc : getLocations(true))
		{
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
			}
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
		
		for (Zombie z : getMobType(Zombie.class))
		{
			boolean b2 = getBooleanValue(z.isBaby(), value);
			if (z.isBaby() != b2) return;
				
			z.setBaby(!b2);
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
	
	private void setBoots()
	{		
		List<ItemStack> list = getItems();
		if (list.size() == 0)
		{
			actionFailed("set boots", ReasonType.NO_ITEM);
			return;
		}
	
		if (isActionCancelled("set boots")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{		
			le.getEquipment().setBoots(list.get(0));
		}	
	}
	
	private void setChestplate()
	{		
		List<ItemStack> list = getItems();
		if (list.size() == 0)
		{
			actionFailed("set chestplate", ReasonType.NO_ITEM);
			return;
		}
	
		if (isActionCancelled("set chestplate")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{		
			le.getEquipment().setChestplate(list.get(0));
		}	
	}
	
	private void setCurrentInvSlot()
	{
		int slot = getInvSlot();
		
		if (slot > 8 || slot < 0) slot = 0;
		
		if (isActionCancelled("set current_inv_slot " + slot)) return;
		
		for (Player p : getMobType(Player.class))
		{		
			p.getInventory().setHeldItemSlot(slot);
		}	
		
	}
	
	private void setCustomDrops()
	{
		List<ItemStack> list = getItems();
		if (list.size() == 0)
		{
			actionFailed("set custom_drops", ReasonType.NO_ITEM);
			return;
		}
		
		if (isActionCancelled("set custom_drops")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			Data.putData(le, SubactionType.CUSTOM_DROPS, list);
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
		
	private void setExp()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set exp", ReasonType.NO_VALUE);
			return;
		}
		
		int exp = getNumber(value);
		
		if (isActionCancelled("set exp " + exp)) return;
		
		for (Player p : getMobType(Player.class))
		{
			exp = adjustNumber(exp, p.getTotalExperience());
			if (exp <= 272)
			{
				int level = exp / 17;
				double d = exp / 17.0;
				p.setLevel(level);
				p.setExp((float) (d - level));
			}
			else
			{
				int temp = 272;
				int level = 16;
				int temp2 = 0;
				while (temp < exp)
				{
					temp += 20 + temp2;
					level++;
					temp2 += 3;
				}
				p.setLevel(level);
				double d = (temp - exp * 1.0) / (temp2 + 3);
				p.setExp((float)d);
			}
			p.setTotalExperience(exp);
		}
	}
	
	private void setFullHp()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set full_hp", ReasonType.NO_VALUE);
			return;
		}	
		
		int amount = getNumber(value);
		
		if (isActionCancelled("set full_hp " + amount)) return;
		
		for (Damageable d : getMobType(Damageable.class))
		{
			amount = adjustNumber(amount, d.getMaxHealth());
			d.setMaxHealth(amount);
			d.setHealth(amount);
		}
	}
	
	private void setHelmet()
	{		
		List<ItemStack> list = getItems();
		if (list.size() == 0)
		{
			actionFailed("set helmet", ReasonType.NO_ITEM);
			return;
		}
	
		if (isActionCancelled("set helmet")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{		
			le.getEquipment().setHelmet(list.get(0));
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
	
	private void setInvSlot()
	{
		int slot = getInvSlot();
	
		List<ItemStack> list = getItems();
		if (list.size() == 0)
		{
			actionFailed("set inv_slot " + slot, ReasonType.NO_ITEM);
			return;
		}
	
		if (isActionCancelled("set inv_slot " + slot)) return;
		
		for (Player p : getMobType(Player.class))
		{		
			p.getInventory().setItem(slot, list.get(0));
		}	
		
	}
	
	private void setItemInHand()
	{		
		List<ItemStack> list = getItems();
		if (list.size() == 0)
		{
			actionFailed("set item_in_hand", ReasonType.NO_ITEM);
			return;
		}
	
		if (isActionCancelled("set item_in_hand")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{		
			le.getEquipment().setItemInHand(list.get(0));
		}	
	}
	
	private void setLeggings()
	{		
		List<ItemStack> list = getItems();
		if (list.size() == 0)
		{
			actionFailed("set leggings", ReasonType.NO_ITEM);
			return;
		}
	
		if (isActionCancelled("set leggings")) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{		
			le.getEquipment().setLeggings(list.get(0));
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
	
	private void setMoney()
	{
		if (Mobs.getEconomy() == null)
		{
			actionFailed("set money", ReasonType.NO_VAULT);
			return;
		}
		
		String value = getValue();
		if (value == null)
		{
			actionFailed("set money", ReasonType.NO_VALUE);
			return;
		}
		
		int money = getNumber(value);
		
		if (isActionCancelled("set money " + money)) return;
		
		for (Player p : getMobType(Player.class))
		{			
			double amount = Mobs.getEconomy().getBalance(p.getName());
			money = adjustNumber((int)Math.round(amount), money);
			//double difference = money - amount;
			Mobs.getEconomy().depositPlayer(p.getName(), money);
		}
	}
	
	private void setName()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set name", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set name, " + value)) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			if (le instanceof Player) continue;
			
			le.setCustomName(value);
		}
	}
	
	private void setNameIsVisible()
	{		
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set name_is_visible", ReasonType.NO_VALUE);
			return;
		}	
		
		if (isActionCancelled("set name_is_visible, " + value)) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			boolean b2 = getBooleanValue(le.isCustomNameVisible(), value);
			if (le.isCustomNameVisible() == b2) return;
				
			le.setCustomNameVisible(b2);
		}
	}
	
	private void setOcelot()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set ocelot", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set ocelot, " + value)) return;
		
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
	
	/** Sets whether mobs should disappear away from players */
	private void setShouldDespawn()
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set should_despawn", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set should_despawn, " + value)) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			boolean b = getBooleanValue(le.getRemoveWhenFarAway(), value);
			if (le.getRemoveWhenFarAway() != b) le.setRemoveWhenFarAway(b);
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
	
	private void setSkeleton()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set skeleton", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set skeleton, " + value)) return;
		
		Skeleton.SkeletonType st;
		if (value.equalsIgnoreCase("random"))
		{
			st = Skeleton.SkeletonType.getType(new Random().nextInt(Skeleton.SkeletonType.values().length));
		}
		else st = Skeleton.SkeletonType.valueOf(value.toUpperCase());
		
		for (Skeleton s : getMobType(Skeleton.class)) s.setSkeletonType(st);
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
		
	private void setVillager()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set villager", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set villager, " + value)) return;
		
		Villager.Profession vp;
		if (value.equalsIgnoreCase("random"))
		{
			vp = Villager.Profession.getProfession(new Random().nextInt(Villager.Profession.values().length));
		}
		else vp = Villager.Profession.valueOf(value.toUpperCase());
		
		for (Villager v : getMobType(Villager.class)) v.setProfession(vp);
	}
	
	private void setVisibleName()
	{
		String value = getValue();
		if (value == null)
		{
			actionFailed("set visible_name", ReasonType.NO_VALUE);
			return;
		}
		
		if (isActionCancelled("set visible_name, " + value)) return;
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			if (le instanceof Player) continue;
			
			le.setCustomName(value);
			le.setCustomNameVisible(true);
		}
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
	
	private void setZombieVillager()
	{
		ValueType value = getValueType();
		if (value == null)
		{
			actionFailed("set adult", ReasonType.NO_VALUE);
			return;
		}	
		
		for (Zombie z : getMobType(Zombie.class))
		{
			boolean b2 = getBooleanValue(z.isVillager(), value);
			if (z.isVillager() == b2) return;
				
			z.setVillager(b2);
		}
	}
	
// Shoot action
	
	private void shootSomething()
	{
		ProjectileType projectile = getProjectile();
		if (projectile == null)
		{
			actionFailed("shoot", ReasonType.NO_PROJECTILE);
			return;
		}		
		
		Class<? extends Projectile> c = null;
		switch (projectile)
		{
			case ARROW: c = Arrow.class;
				break;
			case EGG: c = Egg.class;
				break;
			case ENDERPEARL: c = EnderPearl.class;
				break;
			case FIREBALL: c = Fireball.class;
				break;
			case FISH: c = Fish.class;
				break;
			case LARGEFIREBALL: c = LargeFireball.class;
				break;
			case SMALLFIREBALL: c = SmallFireball.class;
				break;
			case SNOWBALL: c = Snowball.class;
				break;
			case THROWNEXPBOTTLE: c = ThrownExpBottle.class;
				break;
			case THROWNPOTION: c = ThrownPotion.class;
				break;
			case WITHERSKULL: c = WitherSkull.class;
				break;
		}
		
		if (c == null) return;
			
		int speed = getProjectileSpeed();
		
		List<Location> locs = getLocations(false);
		
		for (LivingEntity le : getMobType(LivingEntity.class))
		{
			for (Location loc : locs)
			{
				Projectile p = le.launchProjectile(c);
				p.setVelocity(loc.toVector().subtract(le.getEyeLocation().toVector()).normalize().multiply(speed));
			}
		}
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
		
		for (Location loc : getLocations(true))
		{
			ExperienceOrb orb = (ExperienceOrb)loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
			orb.setExperience(amount);
		}
	}
	
	/** Spawns an item at a location */
	private void spawnItem()
	{	
		List<ItemStack> list = getItems();

		if (list == null || list.size() == 0)
		{
			actionFailed("spawn item", ReasonType.NO_ITEM);
			return;
		}
		for (Location loc : getLocations(true))
		{
			for (ItemStack is : list)
			{
				if (isActionCancelled("spawn item " + getPrettyItem(is) + " ," + getPrettyLoc(loc))) continue;			
				loc.getWorld().dropItem(loc, is);
			}
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
			for (Location loc : getLocations(true))
			{
				Mobs.setMobName(mob_name);
				loc.getWorld().spawnEntity(loc, et);
			}
		}
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
		
		message = replaceText(message);
		
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
		
		if (s == null) return null;
		
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
	
	private String getAllMobsInArea()
	{
		MobsElement me = ce.getCurrentElement(ElementType.TARGET_AREA, ev);
		if (me == null) return null;

		ce = me;
		return getRatioString(ce.getString(ElementType.TARGET_AREA));	
	}
	
	private int getAmount(int orig)
	{
		MobsElement me = ce.getCurrentElement(ElementType.AMOUNT, ev);
		if (me == null) return orig;

		ce = me;
		return getNumber(ce.getString(ElementType.AMOUNT));
	}
		
	private String getCommand()
	{
		MobsElement me = ce.getCurrentElement(ElementType.COMMAND, ev);
		if (me == null) return null;
		
		ce = me;
		return ce.getString(ElementType.COMMAND);
	}
	
	private int getDuration()
	{
		MobsElement me = ce.getCurrentElement(ElementType.DURATION, ev);
		if (me == null) return 0;

		ce = me;
		return getNumber(ce.getString(ElementType.DURATION)) * 20;
	}
	
	private Effect getEffect()
	{
		MobsElement me = ce.getCurrentElement(ElementType.EFFECT, ev);
		if (me == null) return null;

		ce = me;
		String s = getRatioString(ce.getString(ElementType.EFFECT)).toUpperCase();
		if (s.equalsIgnoreCase("random"))
		{
			return Effect.values()[new Random().nextInt(Effect.values().length)];
		}
		else return Effect.valueOf(s);
	}
	
	private String getEnchantment()
	{
		MobsElement me = ce.getCurrentElement(ElementType.ENCHANTMENT, ev);
		if (me == null) return null;

		ce = me;
		String s = getRatioString(ce.getString(ElementType.ENCHANTMENT)).toUpperCase();
		if (s.equalsIgnoreCase("random"))
		{
			return Enchantment.values()[new Random().nextInt(Enchantment.values().length)].getName();
		}
		else return s;
	}
	
	private Map<Enchantment, Integer> getEnchantments()
	{
		String s = getEnchantment();
		List<MobsElement> list = new ArrayList<MobsElement>();
		
		if (Enums.isEnchantment(s)) list.add(ce);		
		else if (linked_enchantments != null)
		{
			String[] temp = s.replace(" ", "").split(",");
			String ss = temp[new Random().nextInt(temp.length)];
			temp = ss.split("\\+");
			for (String la : temp)
			{
				MobsElement me = linked_enchantments.get(la);
				if (me == null) continue;
				
				me.setParent(ce);
				list.add(me);
			}
		}

		Map<Enchantment, Integer> temp = new HashMap<Enchantment, Integer>();
		for (MobsElement me : list)
		{
			ce = me;
			String en = getEnchantment();
			if (en != null)
			{
				int level = getEnchantmentLevel();
				temp.put(Enchantment.getByName(en), level);
			}
		}
		
		return temp;
	}
	
	private int getEnchantmentLevel()
	{
		MobsElement me = ce.getCurrentElement(ElementType.ENCHANTMENT_LEVEL, ev);
		if (me == null) return 1;

		ce = me;
		return getNumber(ce.getString(ElementType.ENCHANTMENT_LEVEL));
	}
	
	private int getItemData(int orig)
	{
		MobsElement me = ce.getCurrentElement(ElementType.ITEM_DATA, ev);
		if (me == null) return orig;

		ce = me;
		return getNumber(ce.getString(ElementType.ITEM_DATA));
	}
	
	private int getInvSlot()
	{
		MobsElement me = ce.getCurrentElement(ElementType.INV_SLOT, ev);
		if (me == null) return 9;

		ce = me;
		return getNumber(ce.getString(ElementType.INV_SLOT));
	}
	
	private String getItem()
	{
		MobsElement me = ce.getCurrentElement(ElementType.ITEM, ev);
		if (me == null) return null;

		ce = me;
		return getRatioString(ce.getString(ElementType.ITEM)).toUpperCase();
	}//TODO saving!
	
	private int getItemId()
	{
		MobsElement me = ce.getCurrentElement(ElementType.ITEM, ev);
		if (me == null) return 0;

		ce = me;
		return getNumber(ce.getString(ElementType.ITEM));
	}
	
	private List<ItemStack> getItems() 
	{
		String s = getItem();
		List<MobsElement> list = new ArrayList<MobsElement>();
		if (isNumber(s) || Enums.isMaterial(s)) list.add(ce);
		
		else if (linked_items != null)
		{
			String[] temp = s.replace(" ", "").split(",");
			String ss = temp[new Random().nextInt(temp.length)];
			temp = ss.split("\\+");
			for (String la : temp)
			{
				MobsElement me = linked_items.get(la);
				if (me == null) continue;
				
				me.setParent(ce);
				list.add(me);
			}
		}

		List<ItemStack> temp = new ArrayList<ItemStack>();
		for (MobsElement me : list)
		{
			ce = me;

			int id = getNumber(getItem());
			if (id == 0) continue;
			
			int data = getItemData(0);
			int amount = getAmount(1);		
			
			ItemStack is = new ItemStack(id, amount, (short)data);
			
			Map<Enchantment, Integer> en = getEnchantments();
			if (en != null)
			{
				is.addUnsafeEnchantments(en);
			}
			temp.add(is);
		}
		
		return temp;
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
		return EntityType.valueOf(getRatioString(ce.getString(ElementType.MOB)).toUpperCase());	
	}
	
	private String getMobName()
	{
		MobsElement me = ce.getCurrentElement(ElementType.MOB_NAME, ev);
		if (me == null) return null;

		ce = me;
		return getRatioString(ce.getString(ElementType.MOB_NAME));
	}
	
	private NumberType getNumberType()
	{
		MobsElement me = ce.getCurrentElement(ElementType.AMOUNT_TYPE, ev);
		if (me == null) return null;
		
		ce = me;
		return NumberType.valueOf(getRatioString(ce.getString(ElementType.AMOUNT_TYPE)));
	}
	
	private ProjectileType getProjectile()
	{
		MobsElement me = ce.getCurrentElement(ElementType.PROJECTILE, ev);
		if (me == null) return null;
		
		ce = me;
		return ProjectileType.valueOf(ce.getString(ElementType.PROJECTILE).toUpperCase());
	}
	
	private int getProjectileSpeed()
	{
		MobsElement me = ce.getCurrentElement(ElementType.PROJECTILE_SPEED, ev);
		if (me == null) return 1;
		
		ce = me;
		return getNumber(ce.getString(ElementType.PROJECTILE_SPEED).toUpperCase());
	}
	
	private String getProjectileTarget()
	{
		MobsElement me = ce.getCurrentElement(ElementType.PROJECTILE_TARGET, ev);
		if (me == null) return null;
		
		ce = me;
		return getRatioString(ce.getString(ElementType.PROJECTILE_TARGET).toUpperCase());
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
		String s = getRatioString(ce.getString(ElementType.SOUND)).toUpperCase();
		if (s.equalsIgnoreCase("random"))
		{
			return Sound.values()[new Random().nextInt(Sound.values().length)];
		}
		else return Sound.valueOf(s);	
	}
	
	private float getSoundPitch()
	{
		MobsElement me = ce.getCurrentElement(ElementType.SOUND_PITCH, ev);
		if (me == null) return 1.0f;

		ce = me;
		return Float.parseFloat(getRatioString(ce.getString(ElementType.SOUND_PITCH))) / 100;
	}
	
	private float getSoundVolume()
	{
		MobsElement me = ce.getCurrentElement(ElementType.SOUND_VOLUME, ev);
		if (me == null) return 1.0f;

		ce = me;
		return Float.parseFloat(getRatioString(ce.getString(ElementType.SOUND_VOLUME))) / 100;
	}
	
	private SubactionType getSubaction()
	{
		MobsElement me = ce.getCurrentElement(ElementType.SUB, ev);
		if (me == null) return null;

		ce = me;
		return SubactionType.valueOf(getRatioString(ce.getString(ElementType.SUB).toUpperCase()));
	}
	
	private String getTarget()
	{
		MobsElement me = ce.getCurrentElement(ElementType.TARGET, ev);
		if (me == null) return null;
		
		ce = me;
		return getRatioString(ce.getString(ElementType.TARGET).toUpperCase());
	}
	
	@SuppressWarnings("unchecked")
	private List<Object> getTargets() 
	{		
		String old_ce = ce.toString();
		
		String s = getTarget();
		if (current_targets != null && !old_ce.equalsIgnoreCase(ce.toString())) return current_targets;
		
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
			Object o = getMCTarget(true);
			if (o instanceof List<?>)
			{
				mobs.addAll((List<Object>)o);
			}
			else mobs.add(o);
		}
	
		if (!old_ce.equalsIgnoreCase(ce.toString()))
		{
			current_targets = mobs;
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
		return getRatioString(ce.getString(ElementType.TARGET_NAME));
	}

	private int getTargetXOffset()
	{
		MobsElement me = ce.getCurrentElement(ElementType.TARGET_X_OFFSET, ev);
		if (me == null) return 0;
		
		ce = me;
		return getNumber(ce.getString(ElementType.TARGET_X_OFFSET));
	}
	
	private int getTargetYOffset()
	{
		MobsElement me= ce.getCurrentElement(ElementType.TARGET_Y_OFFSET, ev);
		if (me == null) return 0;
		
		ce = me;
		return getNumber(ce.getString(ElementType.TARGET_Y_OFFSET));
	}
	
	private int getTargetZOffset()
	{
		MobsElement me = ce.getCurrentElement(ElementType.TARGET_Z_OFFSET, ev);
		if (me == null) return 0;
		
		ce = me;
		return getNumber(ce.getString(ElementType.TARGET_Z_OFFSET));
	}
	
	private String getValue()
	{
		MobsElement me = ce.getCurrentElement(ElementType.VALUE, ev);
		if (me == null) return null;
		
		ce = me;
		return getRatioString(ce.getString(ElementType.VALUE));
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
		
	private boolean isNumber(String orig)
	{
		try
		{
			Integer.valueOf(orig);
			return true;
		}
		catch (Exception e) {};
		return false;
	}
	
	/** Returns a randomized string */
	private String getRatioString(String s)
	{
		s = s.replace(" ", "");
		if (!s.contains(",")) return s;
		
		String[] temp = s.split(",");
		return temp[new Random().nextInt(temp.length)];
	}
	
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
		NumberType nt = getNumberType();
		if (nt == null) return orig;
		
		switch (nt)
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
			case NO:
			case FALSE: return false;
			case RANDOM: return new Random().nextBoolean();
			case TOGGLED: return !orig;
			case YES:
			case TRUE: return true;
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
	private Object getMCTarget(boolean target)
	{
		String tt = target ? getTarget() : getProjectileTarget();
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
			
			case ALL_MOBS_IN_AREA:
				World w = ev.getWorld();

				String a = getAllMobsInArea();
				if (a == null) return null;
				String needed = w.getName() + ":" + a;
				
				Area area = Mobs.getExtraEvents().getArea(needed);
				if (area == null) return null;	
				
				List<LivingEntity> mobs = getRelevantMobs(w.getEntities(), "LIVINGENTITY", getTargetName());
				if (mobs.size() == 0)
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NO_MATCHING_TARGET);
					return null;			
				}
				
				Iterator<LivingEntity> it = mobs.iterator();
				
				while (it.hasNext())
				{
					LivingEntity le = it.next();
					if (!area.isInArea(le.getLocation())) it.remove();
				}
				
				int i = getTargetAmount(0);
				Collections.shuffle(mobs);
				if (i == 0) return mobs;
				
				if (i > mobs.size()) i = mobs.size();
				return mobs.subList(0, i);
			
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
				w = ev.getWorld();
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
				
				mobs = getRelevantMobs(orig.getNearbyEntities(100, 200, 500), s, getTargetName());
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
				
				i = getTargetAmount(1);
				if (i == 1) return nearby_mobs.get(0).getLivingEntity();
				
				if (i > nearby_mobs.size()) i = nearby_mobs.size();
				nearby_mobs = nearby_mobs.subList(0, i);
				mobs.clear();
				for (NearbyMob m : nearby_mobs)
				{
					mobs.add(m.getLivingEntity());
				}
				return mobs;
				
			case EVERY:
			case EVERY_BAT:
			case EVERY_BLAZE:
			case EVERY_CAVE_SPIDER:
			case EVERY_CHICKEN:
			case EVERY_COW:
			case EVERY_CREEPER:
			case EVERY_ENDER_DRAGON:
			case EVERY_ENDERMAN:
			case EVERY_GHAST:
			case EVERY_GIANT:
			case EVERY_GOLEM:
			case EVERY_IRON_GOLEM:
			case EVERY_MAGMA_CUBE:
			case EVERY_MUSHROOM_COW:
			case EVERY_OCELOT:
			case EVERY_PIG:
			case EVERY_PIG_ZOMBIE:
			case EVERY_PLAYER:
			case EVERY_SILVERFISH:
			case EVERY_SHEEP:
			case EVERY_SKELETON:
			case EVERY_SLIME:
			case EVERY_SNOWMAN:
			case EVERY_SPIDER:
			case EVERY_SQUID:
			case EVERY_VILLAGER:
			case EVERY_WITCH:
			case EVERY_WITHER:
			case EVERY_WOLF:
			case EVERY_ZOMBIE:
				w = ev.getWorld();
				
				s = tt.replace("EVERY", "");
				if (s == "") s = "LIVINGENTITY";
				else if (s.startsWith("_")) s = s.replaceFirst("_", "");
				
				mobs = getRelevantMobs(w.getEntities(), s, getTargetName());
				if (mobs.size() == 0)
				{
					actionFailed(ev.getMobsEvent(), ReasonType.NO_MATCHING_TARGET);
					return null;			
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
			if (!(e instanceof LivingEntity)) continue;
			
			if (!m.equalsIgnoreCase("livingentity") && !m.equalsIgnoreCase(e.getType().toString())) continue;
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
	private List<Location> getLocations(boolean t)
	{
		Object target = getMCTarget(t);
		
		List<Location> temp = new ArrayList<Location>();
		if (target instanceof List<?>)
		{
			for (Object o : (List<?>)target)
			{
				Location loc = null;
				if (o instanceof Location) loc = (Location)o;
				else if (o instanceof LivingEntity)
				{
					if (t) loc = ((LivingEntity)o).getLocation();
					else loc = ((LivingEntity)o).getEyeLocation();
				}
				
				loc = adjustLocation(loc);
				if (loc != null) temp.add(loc);
			}
		}
		else
		{
			Location loc = null;
			if (target instanceof Location) loc = (Location)target;
			else if (target instanceof LivingEntity)
			{
				if (t) loc = ((LivingEntity)target).getLocation();
				else loc = ((LivingEntity)target).getEyeLocation();
			}
			
			loc = adjustLocation(loc);
			if (loc != null) temp.add(loc);
		}
		return temp;
	}
	
	private Location adjustLocation(Location loc)
	{
		if (loc == null) return null;
		Random rng = new Random();
		
		int temp = getTargetXOffset();
		if (temp > 0)
		{
			if (rng.nextBoolean()) temp = temp * -1;
			loc.setX(loc.getX() + temp);
		}
		
		temp = getTargetYOffset();
		if (temp > 0)
		{
			if (rng.nextBoolean()) temp = temp * -1;
			double h = loc.getY() + temp;
			if (h > loc.getWorld().getMaxHeight()) h = loc.getWorld().getMaxHeight();
			loc.setY(h);
		}
		
		temp = getTargetZOffset();
		if (temp > 0)
		{
			if (rng.nextBoolean()) temp = temp * -1;
			loc.setZ(loc.getZ() + temp);
		}		
		
		if (loc.getChunk().isLoaded()) return loc;
		
		return null;
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
	
	private String replaceText(String orig)
	{
		int start = orig.indexOf("^");
		if (start == -1) return orig;
		
		int end = orig.indexOf("^", start + 1);
		String sub = orig.substring(start, end + 1);
		String name = orig.substring(start + 1, end).toUpperCase();
		
		if (!Enums.isGameConstant(name)) return orig;
		String replacement = "";
		switch (GameConstant.valueOf(name))
		{
			case DROPPED_XP:
				if (ev.getMobsEvent().equals(EventType.DIES))
				{
					EntityDeathEvent ede = (EntityDeathEvent)ev.getOrigEvent();
					return "" + ede.getDroppedExp();
				}
				if (ev.getMobsEvent().equals(EventType.PLAYER_DIES))
				{
					PlayerDeathEvent pde = (PlayerDeathEvent)ev.getOrigEvent();
					return "" + pde.getDroppedExp();
				}
				
			case HP:
				if (ev.getLivingEntity() != null)
				{
					replacement = "" + ev.getLivingEntity().getHealth();
					break;
				}
			case NAME:
				if (ev.getLivingEntity() == null) break;
				
				if (ev.getLivingEntity() instanceof Player)
				{
					replacement = ((Player)ev.getLivingEntity()).getName();
				}
				else if (Data.hasData(ev.getLivingEntity(), SubactionType.NAME)) replacement = (String)Data.getData(ev.getLivingEntity(), SubactionType.NAME);
				break;
			/*case PLAYER_HP:
				if (ev.getLivingEntity() != null && ev.getLivingEntity() instanceof Player)
				{
					return "" + ((Player)ev.getLivingEntity()).getHealth();
				}
				if (ev.getAuxMob() != null && ev.getAuxMob() instanceof Player)
				{
					return "" + ((Player)ev.getAuxMob()).getHealth();
				}
				
			case PLAYER_XP:
				if (ev.getLivingEntity() != null && ev.getLivingEntity() instanceof Player)
				{
					return "" + ((Player)ev.getLivingEntity()).getTotalExperience();
				}
				if (ev.getAuxMob() != null && ev.getAuxMob() instanceof Player)
				{
					return "" + ((Player)ev.getAuxMob()).getTotalExperience();
				}*/
			case TYPE:
				if (ev.getLivingEntity() == null) break;
				
				replacement = ev.getLivingEntity().getType().getName();
				break;
		}//TODO attack action
		return replaceText(orig.replace(sub, replacement));
	}
	
	/** Calls an event when an action is about to be performed, with the possibility of cancelling */
	private boolean isActionCancelled(String attempting)
	{
		if (Mobs.canDebug())
		{
			MobsPerformingActionEvent mpae = new MobsPerformingActionEvent(attempting, ev);
			Bukkit.getServer().getPluginManager().callEvent(mpae);
			return mpae.isCancelled();
		}
		return false;
	}
	
	/** Calls an event when an action fails (due to wrong type of mob, etc.) */
	private void actionFailed(Object attempted, ReasonType reason)
	{
		if (!Mobs.canDebug()) return;
		Bukkit.getServer().getPluginManager().callEvent(new MobsFailedActionEvent(attempted.toString(), reason, ev));
	}
}