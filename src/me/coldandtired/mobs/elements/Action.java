package me.coldandtired.mobs.elements;

import java.util.List;
import java.util.Random;

import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Action_report;
import me.coldandtired.mobs.Currents;
import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.Outcome_report;
import me.coldandtired.mobs.enums.MAction;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.enums.MSubactions;
import me.coldandtired.mobs.subelements.Item_drop;
import me.coldandtired.mobs.subelements.Target;
import net.minecraft.server.v1_4_6.EntityPigZombie;
import net.minecraft.server.v1_4_6.EntityWolf;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftWolf;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftPigZombie;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.bukkit.metadata.Metadatable;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.EntitySkinType;
import org.w3c.dom.Element;

public class Action extends Config_element
{
	private MAction action_type;
	private Currents currents;
	private Outcome_report report;
		
	public Action(Element element, Config_element parent) throws XPathExpressionException 
	{
		super(element, parent);
		action_type = MAction.valueOf(element.getAttribute("type").toUpperCase());
	}
	
	/** Gets all the targets and loops through them */
	public boolean perform(Outcome_report or, LivingEntity le, Event orig_event)
	{		
		currents = getCurrents();	
		List<Target> targets = getTargets();
		report = or;
		if (targets != null)
		{
			boolean b = false;
			for (Target t : targets)
			{
				for (Object o : t.getTargeted_objects(le, orig_event))
				{
					if (performAction(o, orig_event)) b = true;
				}
			}
			return b;
		}
		else return performAction(le, orig_event);			
	}
	
	/** Performs the top-level action per target */
	private boolean performAction(Object o, Event orig_event)
	{
		switch (action_type)
		{
			case BREAK: breakBlock(o);
				break;
			case CANCEL_EVENT: cancelEvent(orig_event);
				break;
			case CAUSE: causeSomething(o);
				break;
			case CONTINUE: return true;
			case DAMAGE: damageMob(o);
				break;
			case DROP: dropSomething(o);
				break;
			case GIVE: giveSomething(o);
				break;
			case KILL: killMob(o);
				break;
			case PLAY: playSomething(o);
				break;
			case REMOVE: removeSomething(o);
				break;
			case SET: setProperties(o);
				break;
			case SPAWN: spawn(o);
				break;
			case TOGGLE: toggleSomething(o);
				break;
			case WRITE: writeSomething(o);
				break;
		}
		return false;
		/*case PRESS_BUTTON:
			activate_mechanism(le, "button", orig_event);*/
	}
	
	/** Breaks a block */
	private void breakBlock(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		List<Item_drop> items = currents.getItems();
		Action_report ar = new Action_report("BREAK", getString_from_loc(loc));
		if (items == null)
		{
			loc.getBlock().breakNaturally();
			ar.setValue("naturally");
		}
		else
		{
			ItemStack is = items.get(0).getItemstacks(0, 0).get(0);
			Mobs.log(items.get(0).toString());
			loc.getBlock().breakNaturally(is);
			ar.setValue(getString_from_item(is));
		}
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Cancels the original Bukkit event */
	private void cancelEvent(Event event)
	{
		Action_report ar = new Action_report("CANCEL_EVENT", null);
		if (event instanceof Cancellable)
		{			
			((Cancellable)event).setCancelled(true);
			ar.setSuccess();
		}
		else ar.setValue("Not a cancellable event!");
		report.addAction(ar);
	}
	
	/** Causes explosions, lightning, etc. */
	private void causeSomething(Object o)
	{
		for (MSubactions sub : currents.getSubactions())
		{
			switch (sub)
			{
				case EXPLOSION: explode(o);
					break;
				case FIERY_EXPLOSION: explodeFire(o);
					break;
				case LIGHTNING: lightningStrike(o);
					break;
				case LIGHTNING_EFFECT: lightningEffect(o);
					break;
			}
		}
	}
	
	/** Closes doors, gates, etc. */
	private void closeSomething(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		BlockState bs = loc.getBlock().getState();
		MaterialData md = bs.getData();
		Action_report ar = new Action_report("CLOSE", null);
		if (md instanceof Openable)
		{
			((Openable)md).setOpen(false);
			ar.setSuccess();
			ar.setValue(getString_from_loc(loc));
			bs.setData(md);
			bs.update(true);
		}
		else if (md instanceof Lever)
		{
			((Lever)md).setPowered(false);
			ar.setSuccess();
			ar.setValue(getString_from_loc(loc));
			bs.setData(md);
			bs.update(true);
		}
		else ar.setValue("Not something which can open!");
		report.addAction(ar);
	}
	
	/** Damages a mob */
	private void damageMob(Object o)
	{
		Action_report ar = new Action_report("DAMAGE", null);
		if (o instanceof LivingEntity)
		{
			int i = currents.getAmount(0);
			((LivingEntity)o).damage(i);
			ar.setValue(i);
			ar.setSuccess();
		}
		else ar.setValue("Not a mob!");
		report.addAction(ar);
	}
	
	/** Drops items or exp at a specific location */
	private void dropSomething(Object o)
	{		
		for (MSubactions sub : currents.getSubactions())
		{
			switch (sub)
			{
				case ITEM: dropItem(o);
					break;
				case EXP: dropExp(o);
					break;
			}
		}
	}
	
	/** Drops an item at a location */
	private void dropItem(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		for (Item_drop drop : currents.getItems())
		{
			for (ItemStack is : drop.getItemstacks(0, 0))
			{
				Action_report ar = new Action_report("DROP", "ITEM");
				loc.getWorld().dropItem(loc, is);
				ar.setValue(getString_from_item(is));
				ar.setSuccess();
				report.addAction(ar);
			}
		}
	}
	
	/** Drops an exp orb at a location */
	private void dropExp(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		Action_report ar = new Action_report("DROP", "EXP");
		int i = currents.getAmount(0);
		ExperienceOrb orb = (ExperienceOrb)loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
		orb.setExperience(i);
		ar.setValue(i);
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Causes an explosion */
	private void explode(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		Action_report ar = new Action_report("EXPLODE", null);
		int i = currents.getAmount(0);
		loc.getWorld().createExplosion(loc, i);
		ar.setValue(i);
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Causes an explosion which sets blocks on fire */
	private void explodeFire(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		Action_report ar = new Action_report("EXPLODE_FIRE", null);
		int i = currents.getAmount(0);
		loc.getWorld().createExplosion(loc, i, true);
		ar.setValue(i);
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Gives an item, money, or exp directly to a player */
	private void giveSomething(Object o)
	{				
		for (MSubactions sub : currents.getSubactions())
		{
			switch (sub)
			{
				case EXP: giveExp(o);
					break;
				case ITEM: giveItem(o);
					break;
				case MONEY: giveMoney(o);
					break;
			}
		}
	}
	
	/** Gives a player some exp directly */
	private void giveExp(Object o)
	{
		Action_report ar = new Action_report("GIVE", "EXP");
		if (!(o instanceof Player))
		{
			ar.setValue("Not a player!");
			report.addAction(ar);
			return;
		}
		int i = getAmount();
		((Player)o).giveExp(i);
		ar.setValue(((Player)o).getName() + ", " + i);
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Puts an item directly into a player's backpack */
	private void giveItem(Object o)
	{
		Action_report ar = new Action_report("GIVE", "ITEM");
		if (!(o instanceof Player))
		{
			ar.setValue("Not a player!");
			report.addAction(ar);
			return;
		}
		
		for (Item_drop drop : currents.getItems())
		{
			for (ItemStack is : drop.getItemstacks(0, 0))
			{		
				ar = new Action_report("GIVE", "ITEM");
				((Player)o).getInventory().addItem(is);
				ar.setValue(((Player)o).getName() + ", " + getString_from_item(is));
				ar.setSuccess();
				report.addAction(ar);
			}
		}
	}
	
	/** Returns a more friendly description of an itemstack */
	private String getString_from_item(ItemStack is)
	{
		return "item = " + is.getTypeId() + " x " + is.getAmount();
	}
	
	/** Gives a player money using an economy plugin */
	private void giveMoney(Object o)
	{
		Action_report ar = new Action_report("GIVE", "MONEY");
		if (!(o instanceof Player ))
		{
			ar.setValue("Not a player!");
			report.addAction(ar);
			return;
		}
		Player p = (Player)o;
		
		if (Mobs.economy == null)
		{
			Mobs.warn("GIVE MONEY failed - no economy plugin!");
			ar.setValue("No economy plugin!");
			report.addAction(ar);
			return;
		}
		
		int am = Integer.parseInt(currents.getValue());
		Mobs.economy.depositPlayer(p.getName(), am);
		ar.setValue(am);
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Kills a mob immediately */
	private void killMob(Object o)
	{
		Action_report ar = new Action_report("Kill", null);
		if (o instanceof LivingEntity)
		{
			((LivingEntity)o).setHealth(0);
			ar.setSuccess();
		}
		else ar.setValue("Not a mob!");
		report.addAction(ar);
	}
	
	/** Strikes a location or mob with lightning */
	private void lightningStrike(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		Action_report ar = new Action_report("LIGHTNING", null);
		loc.getWorld().strikeLightning(loc);
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Shows lightning without damaging a location or mob */
	private void lightningEffect(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		Action_report ar = new Action_report("LIGHTNING", null);
		loc.getWorld().strikeLightningEffect(loc);
		ar.setSuccess();
		report.addAction(ar);
	}
		
	/** Opens doors, gates, etc. */
	private void openSomething(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		BlockState bs = loc.getBlock().getState();
		MaterialData md = bs.getData();
		Action_report ar = new Action_report("OPEN", null);
		if (md instanceof Openable)
		{
			((Openable)md).setOpen(true);
			ar.setSuccess();
			ar.setValue(getString_from_loc(loc));
			bs.setData(md);
			bs.update(true);
		}
		else if (md instanceof Lever)
		{
			((Lever)md).setPowered(true);
			ar.setSuccess();
			ar.setValue(getString_from_loc(loc));
			bs.setData(md);
			bs.update(true);
		}
		else ar.setValue("Not something which can open!");
		report.addAction(ar);
	}
	
	/** Plays an effect */
	private void playSomething(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		for (String s : currents.getEffects())
		{
			Action_report ar = new Action_report("PLAY", s);
			ar.setValue(getString_from_loc(loc));
			loc.getWorld().playEffect(loc, Effect.valueOf(s.toUpperCase()), 10);
			ar.setSuccess();
			report.addAction(ar);
		}
	}
	
	/** Removes something from the world */
	private void removeSomething(Object o)
	{
		for (MSubactions sub : currents.getSubactions())
		{
			switch (sub)
			{
				case DATA: removeData(o);
					break;
				case DROPPED_EXP: removeExp(o);
					break;
				case DROPPED_ITEMS: removeDrops(o);
					break;
				case ITEM: removeItem(o);
					break;
				case ITEMS: removeInventory(o);
					break;
				case MOB: removeMob(o);
					break;
				case SKIN: removeSkin(o);
					break;
				default: removeProperty(o, sub);
			}
		}
	}
	
	/** Removes all metadata from a mob */
	private void removeData(Object o)
	{
		Action_report ar = new Action_report("REMOVE", "DATA");
		if (!(o instanceof Metadatable))
		{
			ar.setValue("Not a mob!");
			report.addAction(ar);
			return;
		}
		Data.clearData((Metadatable)o);
		ar.setSuccess();
		report.addAction(ar);		
	}

	/** Removes what items a mob would drop */
	private void removeDrops(Object o)
	{
		Action_report ar = new Action_report("REMOVE", "DROPPED_ITEMS");
		if (!(o instanceof Metadatable))
		{
			ar.setValue("Not a mob!");
			report.addAction(ar);
			return;
		}
		Data.putData((Metadatable)o, MParam.CLEAR_DROPS);
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Removes what exp a mob would drop */
	private void removeExp(Object o)
	{
		Action_report ar = new Action_report("REMOVE", "DROPPED_EXP");
		if (!(o instanceof Metadatable))
		{
			ar.setValue("Not a mob!");
			report.addAction(ar);
			return;
		}
		Data.putData((Metadatable)o, MParam.CLEAR_EXP);
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Removes all a player's items */
	private void removeInventory(Object o)
	{
		Action_report ar = new Action_report("REMOVE", "INVENTORY");
		if (!(o instanceof Player))
		{
			ar.setValue("Not a player!");
			report.addAction(ar);
			return;
		}
		((Player)o).getInventory().clear();
		ar.setValue(((Player)o).getName());
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Removes specific items from a player's inventory */
	private void removeItem(Object o)
	{
		Action_report ar = new Action_report("REMOVE", "ITEM");
		if (!(o instanceof Player))
		{
			ar.setValue("Not a player!");
			report.addAction(ar);
			return;
		}
		for (Item_drop drop : currents.getItems())
		{
			for (ItemStack is : drop.getItemstacks(0, 0))
			{
				for (ItemStack is2 : ((Player)o).getInventory().getContents())
				{
					if (drop.matches(is2, is))
					{
						ar = new Action_report("REMOVE", "ITEM");
						((Player)o).getInventory().remove(is2);
						ar.setValue(getString_from_item(is2));
						ar.setSuccess();
						report.addAction(ar);
					}
				}
			}
		}
	}
	
	/** Removes a mob */
	private void removeMob(Object o)
	{
		Action_report ar = new Action_report("REMOVE", "MOB");
		if (o instanceof LivingEntity)
		{
			if (o instanceof Player)
			{
				ar.setValue("Can't be used on players!");
			}
			else
			{
				((LivingEntity)o).remove();
				ar.setSuccess();
			}
		}
		else ar.setValue("Not a mob!");
		report.addAction(ar);		
	}
	
	/** Removes a property from a mob */
	private void removeProperty(Object o, MSubactions prop)
	{
		Action_report ar = new Action_report("REMOVE", prop.toString());
		if (!(o instanceof Metadatable))
		{
			ar.setValue("Not a mob!");
			report.addAction(ar);
			return;
		}
		Data.removeData((Metadatable)o, MParam.valueOf(prop.toString()));
		ar.setSuccess();
		report.addAction(ar);
	}
		
	/** Removes a mob's skin using SpoutPlugin */
	private void removeSkin(Object o)
	{
		Action_report ar = new Action_report("REMOVE", "SKIN");
		if (Mobs.isSpout_enabled())
		{
			if (o instanceof LivingEntity)
			{	
				String v = currents.getValue();
				ar.setValue(v);
				ar.setSuccess();
				Spout.getServer().resetEntitySkin((LivingEntity)o);
			}
			else ar.setValue("Not a mob!");			
		}
		else ar.setValue("No SpoutPlugin found!");
		report.addAction(ar);
	}
		
	/** Sets the various properties a mob can have */
	private void setProperties(Object o)
	{
		for (MSubactions sub : currents.getSubactions())
		{			
			switch (sub)
			{
				case ADULT: setAdult(o);
					break;
				case ANGRY:setAngry(o);
					break;
				case ATTACK_POWER: setAttack_power(o);
					break;
				case BLOCK: setBlock(o);
					break;
				case CLOSED: closeSomething(o);
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
				case NO_TELEPORT: setProperty(o, sub);setProperty(o, sub);
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
				case NAME: setCustom_value(sub, o);
					break;
				case DAMAGE_TAKEN: setDamage_taken(sub, o);
					break;
				case EXP: setExp(o);
					break;
				case EXPLOSION_SIZE: setExplosion_size(o);
					break;
				case HP: setHp(o);
					break;
				case LEVEL: setLevel(o);
					break;	
				case MAX_HP: setMax_hp(o);
					break;
				case MAX_LIFE: setMax_life(o);
					break;	
				case MONEY: setMoney(o);
					break;
				case OCELOT_TYPE: setOcelot_type(o);
					break;
				case OPEN: openSomething(o);
					break;
				case OWNER: setOwner(o);
					break;	
				case POWERED: setPowered(o);
					break;				
				case SADDLED: setSaddled(o);
					break;
				case SHEARED: setSheared(o);
					break;
				case SKIN: setSkin(o);
					break;					
				case TAMED: setTamed(o);
					break;
				case TIME: setTime(o);
					break;
				case TITLE: setTitle(o);
					break;	
				case VILLAGER_TYPE: setVillager_type(o);						
					break;
				case WEATHER: setWeather(o);
					break;
				case WOOL_COLOR: setWool_colour(o);
					break;
			}
		}
	}	
	
	/** Controls whether the animal should be a baby or adult */
	private void setAdult(Object o)
	{
		Action_report ar = new Action_report("SET", "ADULT");
		if (o instanceof Ageable)
		{
			Ageable a = (Ageable)o;
			boolean b = currents.getBool_value();
			ar.setValue(b);
			ar.setSuccess();
			if (b) a.setAdult(); else a.setBaby();
		}
		else ar.setValue("Not an animal!");
		report.addAction(ar);
	}
	
	/** Controls whether the animal should be angry */
	private void setAngry(Object o)
	{
		Action_report ar = new Action_report("SET", "ANGRY");
		if (o instanceof Wolf || o instanceof PigZombie)
		{
			boolean b = currents.getBool_value();
			makeAngry((LivingEntity)o, b);
			ar.setValue(b);
			ar.setSuccess();
		}
		else ar.setValue("Not a mob that can become angry!");
		report.addAction(ar);
	}
	
	/** NMS hack to fix a Bukkit bug */
	private void makeAngry(LivingEntity le, boolean b)
	{
		if (!b)
		{
			if (le instanceof Wolf)
			{
				((Wolf)le).setAngry(false);
				((Wolf)le).setTarget(null);
			}
			else if (le instanceof PigZombie)
			{
				((PigZombie)le).setAngry(false);
				((PigZombie)le).setTarget(null);
			}
			return;
		}
		for (Entity e : le.getNearbyEntities(50, 50, 50)) if (e instanceof Player)
		{
			if (le instanceof Wolf)
			{
				EntityWolf cw = ((CraftWolf)le).getHandle();
				((Wolf)le).setAngry(true);
				cw.b(((CraftLivingEntity)e).getHandle());
			}
			else if (le instanceof PigZombie)
			{
				EntityPigZombie cw = ((CraftPigZombie)le).getHandle();
				((PigZombie)le).setAngry(true);
				cw.b(((CraftLivingEntity)e).getHandle());
			}
			return;
		}
	}
	
	/** Sets a hostile mob's attack power */
	private void setAttack_power(Object o)
	{
		Action_report ar = new Action_report("SET", "ATTACK_POWER");
		if (o instanceof Ocelot)
		{
			int i = currents.getAmount(0);
			Data.putData((Metadatable)o, MParam.ATTACK, i);
			ar.setValue(i);
			ar.setSuccess();		
		}
		else ar.setValue("Not a creeper!");
		report.addAction(ar);
	}
	
	/** Sets a block to a specified type */
	private void setBlock(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		Block b = loc.getBlock();
		for (Item_drop drop : currents.getItems())
		{
			ItemStack is = drop.getItemstacks(b.getTypeId(), b.getData()).get(0);
			Action_report ar = new Action_report("SET", "BLOCK");
			b.setTypeIdAndData(is.getTypeId(), is.getData().getData(), false);
			ar.setValue(is.getTypeId() + ":" + is.getData().getData());
			ar.setSuccess();
			report.addAction(ar);
		}
	}
	
	/** Sets a custom metadata value on a mob */
	private void setCustom_value(MSubactions prop, Object o)
	{
		Action_report ar = new Action_report("SET", prop.toString());
		if (o instanceof Metadatable)
		{
			String v = currents.getValue();
			Data.putData((Metadatable)o, MParam.valueOf(prop.toString()), v);
			ar.setValue(v);
			ar.setSuccess();
		}
		else ar.setValue("Not a mob!");
		report.addAction(ar);
	}

	/** Sets how much damage a mob shold take from certain types of damage */
	private void setDamage_taken(MSubactions prop, Object o)
	{
		Action_report ar = new Action_report("SET", prop.toString());
		if (o instanceof Metadatable)
		{
			int i = currents.getAmount(0);
			Data.putData((Metadatable)o, MParam.valueOf(prop.toString().replace("_DAMAGE", "")), i);
			ar.setValue(i);
			ar.setSuccess();
		}
		else ar.setValue("Not a mob!");
		report.addAction(ar);
	}
	
	/** Sets a player's exp directly */
	private void setExp(Object o)
	{
		Action_report ar = new Action_report("SET", "EXP");
		if (!(o instanceof Player ))
		{
			ar.setValue("Not a player!");
			report.addAction(ar);
			return;
		}
		
		Player p = (Player)o;
		int q = currents.getAmount(p.getTotalExperience());
		ar.setValue(q);
		ar.setSuccess();
		
		if (q <= 272)
		{
			int level = q / 17;
			double d = q / 17.0;
			p.setLevel(level);
			p.setExp((float) (d - level));
		}
		else
		{
			int temp = 272;
			int level = 16;
			int temp2 = 0;
			while (temp < q)
			{
				temp += 20 + temp2;
				level++;
				temp2 += 3;
			}
			p.setLevel(level);
			double d = (temp - q * 1.0) / (temp2 + 3);
			p.setExp((float)d);
		}
		p.setTotalExperience(q);
		ar.setSuccess();
		report.addAction(ar);
	}

	/** Sets how large a creeper/fireball's explosion should be */
	private void setExplosion_size(Object o)
	{
		Action_report ar = new Action_report("SET", "EXPLOSION_SIZE");
		if (!(o instanceof Metadatable))
		{
			int i = currents.getAmount(3);
			Data.putData((Metadatable)o, MParam.EXPLOSION_SIZE, i);
			ar.setValue(i);
			ar.setSuccess();
		}
		else ar.setValue("Not a mob!");
		report.addAction(ar);
	}
	
	/** Sets how much HP a mob has */
	private void setHp(Object o)
	{
		Action_report ar = new Action_report("SET", "HP");
		if (!(o instanceof LivingEntity))
		{
			int hp = currents.getAmount(10);
			int max_hp = ((LivingEntity)o).getMaxHealth();
			if (hp > max_hp) hp = max_hp;
			((LivingEntity)o).setHealth(hp);
			ar.setValue(hp);
			ar.setSuccess();
		}
		else ar.setValue("Not a mob!");
		report.addAction(ar);
	}
	
	/** Sets a player's exp level directly */
	private void setLevel(Object o)
	{
		Action_report ar = new Action_report("SET", "LEVEL");
		if (!(o instanceof Player ))
		{
			ar.setValue("Not a player!");
			report.addAction(ar);
			return;
		}
		
		int i = currents.getAmount(((Player)o).getLevel());
		((Player)o).setLevel(i);
		ar.setValue(i);
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Sets a mob's maximum hp */
	private void setMax_hp(Object o)
	{
		Action_report ar = new Action_report("SET", "MAX_HP");
		if (!(o instanceof LivingEntity))
		{
			int max_hp = currents.getAmount(10);
			if (((LivingEntity)o).getHealth() > max_hp) ((LivingEntity)o).setHealth(max_hp);
			((LivingEntity)o).setMaxHealth(max_hp);
			ar.setValue(max_hp);
			ar.setSuccess();
		}
		else ar.setValue("Not a mob!");
		report.addAction(ar);
	}
	
	/** Sets how many seconds before a mob disappears from the world */
	private void setMax_life(Object o)
	{
		Action_report ar = new Action_report("SET", "MAX_LIFE");
		if (!(o instanceof Metadatable))
		{
			int i = currents.getAmount(0);
			Data.putData((Metadatable)o, MParam.MAX_LIFE, i);
			ar.setValue(i);
			ar.setSuccess();
		}
		else ar.setValue("Not a mob!");
		report.addAction(ar);
	}
	
	/** Sets a player's money using an economy plugin */
	private void setMoney(Object o)
	{
		Action_report ar = new Action_report("SET", "MONEY");
		if (!(o instanceof Player ))
		{
			ar.setValue("Not a player!");
			report.addAction(ar);
			return;
		}
		Player p = (Player)o;
		
		if (Mobs.economy == null)
		{
			Mobs.warn("SET MONEY failed - no economy plugin!");
			ar.setValue("No economy plugin!");
			report.addAction(ar);
			return;
		}
		
		double amount = Mobs.economy.getBalance(p.getName());
		int am = currents.getAmount((int)amount);
		Mobs.economy.withdrawPlayer(p.getName(), amount);
		Mobs.economy.depositPlayer(p.getName(), am);
		ar.setValue(am);
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Sets the type of ocelot (black, red, etc.) */
	private void setOcelot_type(Object o)
	{
		Action_report ar = new Action_report("SET", "OCELOT_TYPE");
		if (o instanceof Ocelot)
		{
			String v = currents.getValue().toUpperCase();
			((Ocelot)o).setCatType(Ocelot.Type.valueOf(v));	
			ar.setValue(v);
			ar.setSuccess();
		}
		else ar.setValue("Not an ocelot!");
		report.addAction(ar);
	}

	/** Sets an animal's owner */
	private void setOwner(Object o)
	{
		Action_report ar = new Action_report("SET", "OWNER");
		if (o instanceof Tameable)
		{
			Tameable t = (Tameable)o;
			String v = currents.getValue();
			ar.setValue(v);
			ar.setSuccess();
			t.setOwner(Bukkit.getPlayer(v));			
		}
		else ar.setValue("Not a tameable animal!");
		report.addAction(ar);
	}

	/** Controls whether a creeper is powered */
	private void setPowered(Object o)
	{
		Action_report ar = new Action_report("SET", "POWERED");
		if (o instanceof Creeper)
		{
			boolean b = currents.getBool_value();
			((Creeper)o).setPowered(b);
			ar.setValue(b);
			ar.setSuccess();
		}
		else ar.setValue("Not a creeper!");
		report.addAction(ar);
	}
		
	/** Sets boolean metadata on a mob */
	private void setProperty(Object o, MSubactions prop)
	{
		Action_report ar = new Action_report("SET", prop.toString());
		if (o instanceof Metadatable)
		{
			MParam param = MParam.valueOf(prop.toString());
			boolean b = currents.getBool_value();
			if (b) Data.putData((Metadatable)o, param);
			else Data.removeData((Metadatable)o, param);
			ar.setValue(b);
			ar.setSuccess();	
		}
		else ar.setValue("Not a mob!");
		report.addAction(ar);
	}
	
	/** Controls whether a pig has a saddle or not */
	private void setSaddled(Object o)
	{
		Action_report ar = new Action_report("SET", "SADDLED");
		if (o instanceof Pig)
		{
			boolean b = currents.getBool_value();

			((Pig)o).setSaddle(b);
			ar.setValue(b);
			ar.setSuccess();
		}
		else ar.setValue("Not a pig!");
		report.addAction(ar);
	}	
	
	/** Controls whether a sheep is sheared */
	private void setSheared(Object o)
	{
		Action_report ar = new Action_report("SET", "SHEARED");
		if (o instanceof Sheep)
		{
			boolean b = currents.getBool_value();
			((Sheep)o).setSheared(b);
			ar.setValue(b);
			ar.setSuccess();
		}
		else ar.setValue("Not a sheep!");
		report.addAction(ar);
	}
	
	/** Uses SpoutPlugin to set a mob's skin */
	private void setSkin(Object o)
	{
		Action_report ar = new Action_report("SET", "SKIN");
		if (Mobs.isSpout_enabled())
		{
			if (o instanceof LivingEntity)
			{	
				String v = currents.getValue();
				ar.setValue(v);
				ar.setSuccess();
				Spout.getServer().setEntitySkin((LivingEntity)o, v, EntitySkinType.DEFAULT);
			}
			else ar.setValue("Not a mob!");			
		}
		else ar.setValue("No SpoutPlugin found!");
		report.addAction(ar);
	}	
	
	/** Controls whether a wolf or ocelot is tamed */
	private void setTamed(Object o)
	{
		Action_report ar = new Action_report("SET", "TAMED");
		if (o instanceof Tameable)
		{
			boolean b = currents.getBool_value();
			((Tameable)o).setTamed(b);
			ar.setValue(b);
			ar.setSuccess();
		}
		else ar.setValue("Not a tameable mob!");
		report.addAction(ar);
	}

	/** Sets the world time */
	private void setTime(Object o)
	{
		int i = currents.getAmount((int)currents.getWorld(o).getTime());
		long time = i % 24000; 
					//getInt_value((int)w.getTime()) % 24000;
		if (time < 0) time += 24000;
		currents.getWorld(o).setTime(time);
	}
	
	/** Uses SpoutPlugin to set a mob's title */
	private void setTitle(Object o)
	{
		Action_report ar = new Action_report("SET", "TITLE");
		if (Mobs.isSpout_enabled())
		{
			if (o instanceof LivingEntity)
			{	
				String v = currents.getValue();
				ar.setValue(v);
				ar.setSuccess();
				Spout.getServer().setTitle((LivingEntity)o, v);
			}
			else ar.setValue("Not a mob!");			
		}
		else ar.setValue("No SpoutPlugin found!");
		report.addAction(ar);
	}
	
	/** Sets the type of villager (farmer, butcher, etc.) */
	private void setVillager_type(Object o)
	{
		Action_report ar = new Action_report("SET", "VILLAGER_TYPE");
		if (o instanceof Villager)
		{
			String v = currents.getValue().toUpperCase();
			((Villager)o).setProfession(Villager.Profession.valueOf(v));
			ar.setValue(v);
			ar.setSuccess();
		}
		else ar.setValue("Not a villager!");
		report.addAction(ar);
	}	
	
	/** Sets a world's weather */
	private void setWeather(Object o)
	{
		String v = currents.getValue();
		if (v.equalsIgnoreCase("rain")) setRain(o);
		else if (v.equalsIgnoreCase("storm")) setStorm(o);
		else if (v.equalsIgnoreCase("sun")) setSunny(o);
	}
	
	/** Sets a world's weather to rain */
	private void setRain(Object o)
	{
		Action_report ar = new Action_report("SET", "WEATHER");
		World w = currents.getWorld(o);
		w.setStorm(true);
		w.setThundering(false);
		Integer i = currents.getAmount(null);
	
		if (i != null)
		{
			i = i * 20;
			w.setWeatherDuration(i);
			ar.setValue("RAIN - " + i + " seconds");
		}
		else ar.setValue("RAIN");
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Sets a world's weather to stormy */
	private void setStorm(Object o)
	{
		Action_report ar = new Action_report("SET", "WEATHER");
		World w = currents.getWorld(o);
		w.setStorm(true);
		w.setThundering(true);
		Integer i = currents.getAmount(null);
	
		if (i != null)
		{
			i = i * 20;
			w.setWeatherDuration(i);
			ar.setValue("STORM - " + i + " seconds");
		}
		else ar.setValue("STORM");
		ar.setSuccess();
		report.addAction(ar);
	}
		
	/** Sets a world's weather to sunny */
	private void setSunny(Object o)
	{
		Action_report ar = new Action_report("SET", "WEATHER");
		World w = currents.getWorld(o);
		w.setStorm(false);
		w.setThundering(false);
		Integer i = currents.getAmount(null);

		if (i != null)
		{
			i = i * 20;
			w.setWeatherDuration(i);
			ar.setValue("SUN - " + i + " seconds");
		}
		else ar.setValue("SUN");
		ar.setSuccess();
		report.addAction(ar);
	}
	
	/** Sets a sheep's wool colour */
	private void setWool_colour(Object o)
	{
		Action_report ar = new Action_report("SET", "WOOL_COLOR");
		if (o instanceof Sheep)
		{
			String v = currents.getValue().toUpperCase();		
			((Sheep)o).setColor(v.equalsIgnoreCase("random") ? DyeColor.getByData((byte) new Random().nextInt(16)) : DyeColor.valueOf(v));
			ar.setValue(v.equalsIgnoreCase("random") ? "random - " + ((Sheep)o).getColor().toString() : ((Sheep)o).getColor().toString());
			ar.setSuccess();
		}
		else ar.setValue("Not a sheep!");
		report.addAction(ar);
	}
	
	/** Spawns a mob at a location */
	private void spawn(Object o)
	{
		/*Action_report ar = new Action_report("SET", "SADDLED");
		//broken
		//Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		
		for (int i = 0; i < currents.getAmount(1); i++)
		{
			//Mobs.getInstance().setMob_name(name);
			//loc.getWorld().spawnEntity(loc, EntityType.valueOf(mob[0]));
		}*/
	}
	
	/** Switch a door, gate, lever, etc. from off to on or vice versa */
	private void switchSomething(Object o)
	{
		Location loc = o instanceof Location ? (Location)o : ((LivingEntity)o).getLocation();
		BlockState bs = loc.getBlock().getState();
		MaterialData md = bs.getData();
		Action_report ar = new Action_report("SWITCH", null);
		if (md instanceof Openable)
		{
			((Openable)md).setOpen(!((Openable)md).isOpen());
			ar.setSuccess();
			ar.setValue(getString_from_loc(loc));
			bs.setData(md);
			bs.update(true);
		}
		else if (md instanceof Lever)
		{
			((Lever)md).setPowered(!((Lever)md).isPowered());
			ar.setSuccess();
			ar.setValue(getString_from_loc(loc));
			bs.setData(md);
			bs.update(true);
		}
		else ar.setValue("Not something which can be switched!");
		report.addAction(ar);
	}
	
	/** Toggle a boolean property */
	private void toggleSomething(Object o)
	{
		/*for (MSubactions sub : currents.getSubactions())
		{			
			Action_report ar = new Action_report("TOGGLE", sub.toString());
			if (sub.equals(MSubactions.MECHANISM)) switchSomething(o);
			else
			{
				if (o instanceof Metadatable)			
				{
					Data.toggleData((Metadatable)o, MParam.valueOf(sub.toString()));
					ar.setValue(Data.hasData((Metadatable)o, MParam.valueOf(sub.toString())) ? "SET" : "REMOVED");
					ar.setSuccess();
				}
				else ar.setValue("Not a mob!");
			}
			report.addAction(ar);
		}*/
	}
	
	/** Returns a more friendly description of a location */
	private String getString_from_loc(Location loc)
	{
		return "loc = " + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ", " + loc.getWorld().getName();
	}

	/** Writes a message to a player, log, or server */
	private void writeSomething(Object o)
	{
		for (MSubactions sub : currents.getSubactions())
		{
			switch (sub)
			{
				case BROADCAST: broadcastMessage(o);
					break;
				case LOG: logMessage(o);
					break;
				case MESSAGE: sendMessage(o);
					break;
			}
		}
	}
	
	/** Sends a message to the whole server */
	private void broadcastMessage(Object o)
	{
		for (String message : currents.getMessages())
		{
			Action_report ar = new Action_report("BROADCAST", null);
			//while (message.contains("^")) message = replaceConstants(message, (Player)o);
			Bukkit.getServer().broadcastMessage(message);
			ar.setValue(message);
			ar.setSuccess();
			report.addAction(ar);
		}
	}
	
	/** Logs a message */
	private void logMessage(Object o)
	{
		for (String message : currents.getMessages())
		{
			Action_report ar = new Action_report("LOG", null);
			//while (message.contains("^")) message = replaceConstants(message, (Player)o);
			Mobs.log(message);
			ar.setValue(message);
			ar.setSuccess();
			report.addAction(ar);
		}
	}
	
	/** Sends a message to a player */
	private void sendMessage(Object o)
	{
		for (String message : currents.getMessages())
		{
			Action_report ar = new Action_report("SEND", null);
			if (o instanceof Player)
			{
			//	while (message.contains("^")) message = replaceConstants(message, (Player)o);
				((Player)o).sendMessage(message);
				ar.setValue(message);
				ar.setSuccess();
			}
			else ar.setValue("Not a player!");
			report.addAction(ar);
		}
	}
	
	/*private String replaceConstants(String s, Player p)
	{
		int start = s.indexOf("^");
		int end = s.indexOf("^", start + 1);
		String sub = s.substring(start, end + 1);
		String name = s.substring(start + 1, end);
		String replacement = "";
		
		if (name.equalsIgnoreCase("killer") && p.getKiller() != null) replacement = p.getKiller().getName();
		
		if (name.equalsIgnoreCase("server_name")) replacement = p.getServer().getName();
		if (name.equalsIgnoreCase("world_name")) replacement = p.getWorld().getName();
		if (name.equalsIgnoreCase("online_player_count")) replacement = Integer.toString(p.getServer().getOnlinePlayers().length);
		if (name.equalsIgnoreCase("offline_player_count")) replacement = Integer.toString(p.getServer().getOfflinePlayers().length);
		if (name.equalsIgnoreCase("world_player_count")) replacement = Integer.toString(p.getWorld().getPlayers().size());

		/*if (name.equalsIgnoreCase("gc_item_in_hand_name")) replacement = me.getItemInHand().getType().name();
		if (name.equalsIgnoreCase("gc_item_in_hand_id")) replacement = Integer.toString(me.getItemInHand().getTypeId());
		if (name.equalsIgnoreCase("gc_item_in_hand_data")) replacement = Integer.toString(me.getItemInHand().getData().getData());
		if (name.equalsIgnoreCase("gc_item_in_hand_amount")) replacement = Integer.toString(me.getItemInHand().getAmount());
		if (name.equalsIgnoreCase("gc_gamemode")) replacement = me.getGameMode().name();
		if (name.equalsIgnoreCase("gc_hp")) replacement = Integer.toString(me.getHealth());
		if (name.equalsIgnoreCase("gc_max_hp")) replacement = Integer.toString(me.getMaxHealth());
		if (name.equalsIgnoreCase("gc_food_level")) replacement = Integer.toString(me.getFoodLevel());
		if (name.equalsIgnoreCase("gc_level_exp"))
		{
			int i = (int) (me.getExp() * 100);
			replacement = Integer.toString(i);
		}
		if (name.equalsIgnoreCase("gc_total_exp")) replacement = Integer.toString(me.getTotalExperience());
		if (name.equalsIgnoreCase("gc_level")) replacement = Integer.toString(me.getLevel());
		if (name.equalsIgnoreCase("gc_air")) replacement = Integer.toString(me.getRemainingAir());
		if (name.equalsIgnoreCase("gc_max_air")) replacement = Integer.toString(me.getMaximumAir());
		if (name.equalsIgnoreCase("gc_title")) replacement = me.getTitle();
		if (name.equalsIgnoreCase("gc_display_name")) replacement = me.getDisplayName();
		if (name.equalsIgnoreCase("gc_list_name")) replacement = me.getPlayerListName();*/
		
		/*s = s.replace(sub, replacement);
		s = s.trim();
		s = s.replaceAll("  ", " ");
		return s;
	}*/
}