package me.coldandtired.mobs.elements;

import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.enums.MAction;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.managers.Target_manager;
import me.coldandtired.mobs.subelements.Item_drop;
import me.coldandtired.mobs.subelements.Target;
import net.minecraft.server.v1_4_6.EntityWolf;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftWolf;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Gate;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.EntitySkinType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SuppressWarnings("deprecation")
public class Action extends Config_element
{
	private MAction action_type;
	private Alternatives items;
	private Text_value mob;
	private Text_value amount;
	private boolean locked = false;
	private Text_value value;
	private Text_value message;
	
	public Action(Element element, Config_element parent) throws XPathExpressionException 
	{
		super(element, parent);
		action_type = MAction.valueOf(element.getLocalName().toUpperCase());
		
		NodeList list = (NodeList)Mobs.getXPath().evaluate("value", element, XPathConstants.NODESET);
	
		if (element.getChildNodes().getLength() == 1 || list.getLength() > 0)
		{
			value = new Text_value(element);
			//return;
		}
		
		if (element.hasAttribute("lock")) locked = Boolean.parseBoolean(element.getAttribute("lock"));	
		
		Element el = (Element)Mobs.getXPath().evaluate("amount", element, XPathConstants.NODE);
		if (el != null) amount = new Text_value(el);
			
		el = (Element)Mobs.getXPath().evaluate("message", element, XPathConstants.NODE);
		if (el != null) message = new Text_value(el);
			
		el = (Element)Mobs.getXPath().evaluate("mob", element, XPathConstants.NODE);
		if (el != null) mob = new Text_value(el);	
		
		fillItems(element);
	}
		
	public boolean perform(LivingEntity le, MEvent event, Event orig_event)
	{
		switch (getAction_type())
		{
			case SET_CUSTOM_FLAG_1_NO:
			case SET_CUSTOM_FLAG_2_NO:
			case SET_CUSTOM_FLAG_3_NO:
			case SET_CUSTOM_FLAG_4_NO:
			case SET_CUSTOM_FLAG_5_NO:
			case SET_CUSTOM_FLAG_6_NO:
			case SET_CUSTOM_FLAG_7_NO:
			case SET_CUSTOM_FLAG_8_NO:
			case SET_CUSTOM_FLAG_9_NO:
			case SET_CUSTOM_FLAG_10_NO:
			case SET_CUSTOM_FLAG_1_RANDOM:
			case SET_CUSTOM_FLAG_2_RANDOM:
			case SET_CUSTOM_FLAG_3_RANDOM:
			case SET_CUSTOM_FLAG_4_RANDOM:
			case SET_CUSTOM_FLAG_5_RANDOM:
			case SET_CUSTOM_FLAG_6_RANDOM:
			case SET_CUSTOM_FLAG_7_RANDOM:
			case SET_CUSTOM_FLAG_8_RANDOM:
			case SET_CUSTOM_FLAG_9_RANDOM:
			case SET_CUSTOM_FLAG_10_RANDOM:
			case SET_CUSTOM_FLAG_1_YES:
			case SET_CUSTOM_FLAG_2_YES:
			case SET_CUSTOM_FLAG_3_YES:
			case SET_CUSTOM_FLAG_4_YES:
			case SET_CUSTOM_FLAG_5_YES:
			case SET_CUSTOM_FLAG_6_YES:
			case SET_CUSTOM_FLAG_7_YES:
			case SET_CUSTOM_FLAG_8_YES:
			case SET_CUSTOM_FLAG_9_YES:
			case SET_CUSTOM_FLAG_10_YES:
				setCustom_flag(le, orig_event);
				break;
		
			case SET_CUSTOM_INT_1:
			case SET_CUSTOM_INT_2:
			case SET_CUSTOM_INT_3:
			case SET_CUSTOM_INT_4:
			case SET_CUSTOM_INT_5:
			case SET_CUSTOM_INT_6:
			case SET_CUSTOM_INT_7:
			case SET_CUSTOM_INT_8:
			case SET_CUSTOM_INT_9:
			case SET_CUSTOM_INT_10:
			case SET_CUSTOM_STRING_1:
			case SET_CUSTOM_STRING_2:
			case SET_CUSTOM_STRING_3:
			case SET_CUSTOM_STRING_4:
			case SET_CUSTOM_STRING_5:
			case SET_CUSTOM_STRING_6:
			case SET_CUSTOM_STRING_7:
			case SET_CUSTOM_STRING_8:
			case SET_CUSTOM_STRING_9:
			case SET_CUSTOM_STRING_10:
				setCustom_value(le, orig_event);
				break;
		
			case REMOVE_CUSTOM_FLAG_1:
			case REMOVE_CUSTOM_FLAG_2:
			case REMOVE_CUSTOM_FLAG_3:
			case REMOVE_CUSTOM_FLAG_4:
			case REMOVE_CUSTOM_FLAG_5:
			case REMOVE_CUSTOM_FLAG_6:
			case REMOVE_CUSTOM_FLAG_7:
			case REMOVE_CUSTOM_FLAG_8:
			case REMOVE_CUSTOM_FLAG_9:
			case REMOVE_CUSTOM_FLAG_10:
			case REMOVE_CUSTOM_INT_1:
			case REMOVE_CUSTOM_INT_2:
			case REMOVE_CUSTOM_INT_3:
			case REMOVE_CUSTOM_INT_4:
			case REMOVE_CUSTOM_INT_5:
			case REMOVE_CUSTOM_INT_6:
			case REMOVE_CUSTOM_INT_7:
			case REMOVE_CUSTOM_INT_8:
			case REMOVE_CUSTOM_INT_9:
			case REMOVE_CUSTOM_INT_10:
			case REMOVE_CUSTOM_STRING_1:
			case REMOVE_CUSTOM_STRING_2:
			case REMOVE_CUSTOM_STRING_3:
			case REMOVE_CUSTOM_STRING_4:
			case REMOVE_CUSTOM_STRING_5:
			case REMOVE_CUSTOM_STRING_6:
			case REMOVE_CUSTOM_STRING_7:
			case REMOVE_CUSTOM_STRING_8:
			case REMOVE_CUSTOM_STRING_9:
			case REMOVE_CUSTOM_STRING_10:
				removeCustom_value(le, orig_event);
				break;
				
			case TOGGLE_CUSTOM_FLAG_1:
			case TOGGLE_CUSTOM_FLAG_2:
			case TOGGLE_CUSTOM_FLAG_3:
			case TOGGLE_CUSTOM_FLAG_4:
			case TOGGLE_CUSTOM_FLAG_5:
			case TOGGLE_CUSTOM_FLAG_6:
			case TOGGLE_CUSTOM_FLAG_7:
			case TOGGLE_CUSTOM_FLAG_8:
			case TOGGLE_CUSTOM_FLAG_9:
			case TOGGLE_CUSTOM_FLAG_10:
				toggleCustom_flag(le, orig_event);
				break;
				
			case CONTINUE: return true;
			case DROP_ITEM:
				drop_item(le, orig_event);
				break;
			case GIVE_ITEM:
			case REMOVE_ITEM:
			case CLEAR_ITEMS:
				give_item(le, orig_event);
				break;
			case SET_MONEY:
				setMoney(le, orig_event);
				break;
			case DROP_EXP:
				drop_exp(le, orig_event);
				break;
			case SET_EXP:
			case SET_LEVEL:
				give_exp(le, orig_event);
				break;
			case PRESS_BUTTON:
				activate_mechanism(le, "button", orig_event);
				break;
			case CLOSE_DOOR:
			case OPEN_DOOR:
			case TOGGLE_DOOR:
				activate_mechanism(le, "door", orig_event);
				break;
			case CLOSE_GATE:
			case OPEN_GATE:
			case TOGGLE_GATE:
				activate_mechanism(le, "gate", orig_event);
				break;
			case PULL_LEVER:
			case PUSH_LEVER:
			case TOGGLE_LEVER:
				activate_mechanism(le, "lever", orig_event);
				break;
			case CLOSE_TRAPDOOR:
			case OPEN_TRAPDOOR:
			case TOGGLE_TRAPDOOR:
				activate_mechanism(le, "trapdoor", orig_event);
				break;
			case LIGHTNING:
			case LIGHTNING_EFFECT:
				strike_with_lightning(le, orig_event);
				break;
			case EXPLOSION:
			case FIERY_EXPLOSION:
				cause_explosion(le, orig_event);
				break;
			case DAMAGE:
			case KILL:
			case REMOVE:
				damage_mob(le, orig_event);
				break;
			case SET_TIME:
				change_time(le);
				break;
			case RAIN:
			case STORM:
			case SUN:
				change_weather(le);
				break;
			case SET_BLOCK:
			case DESTROY_BLOCK:
				change_block(le, orig_event);
				break;	
			case SEND_MESSAGE:
			case BROADCAST:
			case LOG:
				send_message(le, orig_event);
				break;
			case SPAWN_MOB:
				spawn_mob(le, orig_event);
				break;
			case CLEAR_DATA:
				clearData(le, orig_event);
				break;
			case CLEAR_DROPS:
			case CLEAR_EXP:
				clear_drops(le, event, orig_event);
				break;
			case CANCEL_EVENT:
				if (orig_event instanceof Cancellable)
				{
					((Cancellable)orig_event).setCancelled(true);
				}
				break;	
			case PLAY_BLAZE_EFFECT:
			case PLAY_BOW_EFFECT:
			case PLAY_CLICK1_EFFECT:
			case PLAY_CLICK2_EFFECT:
			case PLAY_DOOR_EFFECT:
			case PLAY_ENDER_EFFECT:
			case PLAY_EXTINGUISH_EFFECT:
			case PLAY_GHAST1_EFFECT:
			case PLAY_GHAST2_EFFECT:
			case PLAY_FLAMES_EFFECT:
			case PLAY_POTION_EFFECT:
			case PLAY_SMOKE_EFFECT:
			case PLAY_STEP_EFFECT:
			case PLAY_ZOMBIE1_EFFECT:
			case PLAY_ZOMBIE2_EFFECT:
			case PLAY_ZOMBIE3_EFFECT:
				playEffect(le, orig_event);
				break;
			default:		
				setProperty(le, orig_event);
				break;
		}
		return false;
	}
	
	private void toggleCustom_flag(LivingEntity live, Event orig_event)
	{
		for (LivingEntity le : Target_manager.get().getTargets(getTarget(), live, orig_event)) Data.toggleData(le, MParam.valueOf(getAction_type().toString().substring(7)));
	}
	
	private void removeCustom_value(LivingEntity live, Event orig_event)
	{
		for (LivingEntity le : Target_manager.get().getTargets(getTarget(), live, orig_event)) 
			Data.removeData(le, MParam.valueOf(getAction_type().toString().substring(7)));
	}
	
	private void setCustom_flag(LivingEntity live, Event orig_event)
	{
		String s = getAction_type().toString().substring(4);
		String temp = s.substring(s.lastIndexOf("_"));
		s = s.substring(0, s.lastIndexOf("_"));
		
		if (temp.endsWith("YES"))
		{
			for (LivingEntity le : Target_manager.get().getTargets(getTarget(), live, orig_event)) Data.putData(le, MParam.valueOf(s));
			return;
		}
		else if (temp.endsWith("NO"))
		{
			for (LivingEntity le : Target_manager.get().getTargets(getTarget(), live, orig_event)) Data.removeData(le, MParam.valueOf(s));
			return;
		}
		else if (temp.endsWith("RANDOM"))
		{
			for (LivingEntity le : Target_manager.get().getTargets(getTarget(), live, orig_event)) 
			if (new Random().nextBoolean())	Data.putData(le, MParam.valueOf(s)); else Data.removeData(le, MParam.valueOf(s));
			return;
		}
	}
	
	private void setCustom_value(LivingEntity live, Event orig_event)
	{
		for (LivingEntity le : Target_manager.get().getTargets(getTarget(), live, orig_event)) 
			Data.putData(le, MParam.valueOf(getAction_type().toString().substring(4)), getValue());
	}
	
	private void clearData(LivingEntity live, Event orig_event)
	{
		for (LivingEntity le : Target_manager.get().getTargets(getTarget(), live, orig_event)) 
		{
			Data.clearData(le);
		}
	}
	
	private void setMoney(LivingEntity le, Event orig_event)
	{
		if (Mobs.economy == null)
		{
			Mobs.warn("SET_MONEY failed - no economy plugin!");
			return;
		}
		List<LivingEntity> list = Target_manager.get().getTargets(getTarget(), le, orig_event);
		
		Text_value value = getPure_amount();
		for (LivingEntity l : list)
		{		
			if (!(l instanceof Player)) continue;
			Player p = (Player)l;
			double amount = Mobs.economy.getBalance(p.getName());
			Mobs.economy.withdrawPlayer(p.getName(), amount);
			int am = value.getInt_value((int)amount);
			Mobs.economy.depositPlayer(p.getName(), am);
			if (!isLocked() && list.size() > 1) value = getPure_amount();
		}
	}
	
	private void playEffect(LivingEntity le, Event orig_event)
	{
		Effect effect = null;
		switch (getAction_type())
		{
			case PLAY_BLAZE_EFFECT:
				effect = Effect.BLAZE_SHOOT;
				break;
			case PLAY_BOW_EFFECT:
				effect = Effect.BOW_FIRE;
				break;
			case PLAY_CLICK1_EFFECT:
				effect = Effect.CLICK1;
				break;
			case PLAY_CLICK2_EFFECT:
				effect = Effect.CLICK2;
				break;
			case PLAY_DOOR_EFFECT:
				effect = Effect.DOOR_TOGGLE;
				break;
			case PLAY_ENDER_EFFECT:
				effect = Effect.ENDER_SIGNAL;
				break;
			case PLAY_EXTINGUISH_EFFECT:
				effect = Effect.EXTINGUISH;
				break;
			case PLAY_GHAST1_EFFECT:
				effect = Effect.GHAST_SHOOT;
				break;
			case PLAY_GHAST2_EFFECT:
				effect = Effect.GHAST_SHRIEK;
				break;
			case PLAY_FLAMES_EFFECT:
				effect = Effect.MOBSPAWNER_FLAMES;
				break;
			case PLAY_POTION_EFFECT:
				effect = Effect.POTION_BREAK;
				break;
			case PLAY_SMOKE_EFFECT:
				effect = Effect.SMOKE;
				break;
			case PLAY_STEP_EFFECT:
				effect = Effect.STEP_SOUND;
				break;
			case PLAY_ZOMBIE1_EFFECT:
				effect = Effect.ZOMBIE_CHEW_IRON_DOOR;
				break;
			case PLAY_ZOMBIE2_EFFECT:
				effect = Effect.ZOMBIE_CHEW_WOODEN_DOOR;
				break;
			case PLAY_ZOMBIE3_EFFECT:
				effect = Effect.ZOMBIE_DESTROY_DOOR;
				break;
		}
		for (Location loc : Target_manager.get().getLocations(this, le, orig_event)) loc.getWorld().playEffect(loc, effect, 10);
	}
	
	private void setProperty(LivingEntity live, Event orig_event)
	{
		List<LivingEntity> list = Target_manager.get().getTargets(getTarget(), live, orig_event);
		
		for (LivingEntity le : list)
		{
			if (le == null) le = live;
			if (le instanceof Ageable) setAgeable_property((Ageable)le);
			if (le instanceof Player) setPlayer_property((Player)le);
			if (le instanceof Animals) setAnimal_property((Animals)le);
			if (le instanceof Monster) setMonster_property((Monster)le);
			if (le instanceof EnderDragon) setEnder_dragon_property((EnderDragon)le);
			
			switch (getAction_type())
			{
				case SET_TITLE:
					if (Mobs.isSpout_enabled()) Spout.getServer().setTitle(le, getValue());
					break;
				case SET_SKIN:
					if (Mobs.isSpout_enabled()) Spout.getServer().setEntitySkin(le, getValue(), EntitySkinType.DEFAULT);
					break;
				case RESTORE_SKIN:
					if (Mobs.isSpout_enabled()) Spout.getServer().resetEntitySkin(le);
					break;
				case SET_MAX_LIFE:
					Integer i = (Integer)Data.getData(le, MParam.MAX_LIFE);
					if (i == null) i = 0;
					Data.putData(le, MParam.MAX_LIFE, getInt_value(i));
					break;	
				case REMOVE_MAX_LIFE:
					Data.removeData(le, MParam.MAX_LIFE);
					break;			
				
				case SET_CAN_BURN_NO:
					Data.putData(le, MParam.NO_BURN);
					break;
				case SET_CAN_BURN_RANDOM:
					Data.putRandom_data(le, MParam.NO_BURN);
					break;
				case SET_CAN_BURN_YES:
					Data.removeData(le, MParam.NO_BURN);
					break;
				case TOGGLE_CAN_BURN:
					Data.toggleData(le, MParam.NO_BURN); 
					break;
									
				case SET_CAN_HEAL_NO:
					Data.putData(le, MParam.NO_HEAL);
					break;
				case SET_CAN_HEAL_RANDOM:
					Data.putRandom_data(le, MParam.NO_HEAL);
					break;
				case SET_CAN_HEAL_YES:
					Data.removeData(le, MParam.NO_HEAL);
					break;
				case TOGGLE_CAN_HEAL:
					Data.toggleData(le, MParam.NO_HEAL);
					break;
									
				case SET_CAN_OVERHEAL_NO:
					Data.putData(le, MParam.NO_OVERHEAL);
					break;
				case SET_CAN_OVERHEAL_RANDOM:
					Data.putRandom_data(le, MParam.NO_OVERHEAL);
					break;
				case SET_CAN_OVERHEAL_YES:
					Data.removeData(le, MParam.NO_OVERHEAL);
					break;	
				case TOGGLE_CAN_OVERHEAL:
					Data.toggleData(le, MParam.NO_OVERHEAL);
					break;
				
				case SET_FRIENDLY_NO:
					Data.removeData(le, MParam.FRIENDLY);
					return;
				case SET_FRIENDLY_RANDOM:
					Data.putRandom_data(le, MParam.FRIENDLY);
					return;
				case SET_FRIENDLY_YES:
					Data.putData(le, MParam.FRIENDLY);
					return;
				case TOGGLE_FRIENDLY:
					Data.toggleData(le, MParam.FRIENDLY);
					break;
				
				case SET_NAME:
					Data.putData(le, MParam.NAME, getValue());
				break;
				case SET_MAX_HP:
					int max_hp = getInt_value(le.getMaxHealth());
					int hp = le.getHealth();
					if (hp > max_hp) hp = max_hp;
					le.setMaxHealth(max_hp);
					break;
				case SET_HP:
					hp = getInt_value(le.getHealth());
					max_hp = le.getMaxHealth();
					if (hp > max_hp) hp = max_hp;
					le.setHealth(hp);
					break;
				case SET_ATTACK_POWER:
					Data.putData(le, MParam.ATTACK, getValue());
					break;
					
				case SET_DAMAGE_TAKEN_FROM_BLOCK_EXPLOSION:
					Data.putData(le, MParam.BLOCK_EXPLOSION_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_CONTACT:
					Data.putData(le, MParam.CONTACT_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_CUSTOM:
					Data.putData(le, MParam.CUSTOM_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_DROWNING:
					Data.putData(le, MParam.DROWNING_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_ATTACK:
					Data.putData(le, MParam.ATTACK_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_ENTITY_EXPLOSION:
					Data.putData(le, MParam.ENTITY_EXPLOSION_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_FALL:
					Data.putData(le, MParam.FALL_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_FIRE:
					Data.putData(le, MParam.FIRE_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_FIRE_TICK:
					Data.putData(le, MParam.FIRE_TICK_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_LAVA:
					Data.putData(le, MParam.LAVA_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_LIGHTNING:
					Data.putData(le, MParam.LIGHTNING_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_MAGIC:
					Data.putData(le, MParam.MAGIC_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_MELTING:
					Data.putData(le, MParam.MELTING_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_POISON:
					Data.putData(le, MParam.POISON_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_PROJECTILE:
					Data.putData(le, MParam.PROJECTILE_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_STARVATION:
					Data.putData(le, MParam.STARVATION_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_SUFFOCATION:
					Data.putData(le, MParam.SUFFOCATION_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_SUICIDE:
					Data.putData(le, MParam.SUICIDE_DAMAGE, getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_VOID:
					Data.putData(le, MParam.VOID_DAMAGE, getValue());
					break;
					
				case SET_VILLAGER_TYPE:
					((Villager)le).setProfession(Villager.Profession.valueOf(getValue().toUpperCase()));
					break;
			}
		}
	}
		
	private void setAgeable_property(Ageable a)
	{
		switch (action_type)
		{
			case SET_ADULT_NO:
				a.setBaby();
				return;
			case SET_ADULT_RANDOM:
				if (new Random().nextBoolean()) a.setAdult(); else a.setBaby();
				return;
			case SET_ADULT_YES:
				a.setAdult();
				return;
			case TOGGLE_ADULT:
				if (a.isAdult()) a.setBaby(); else a.setAdult();
				return;
		}
	}
	
	private void setPlayer_property(Player p)
	{
		switch (getAction_type())
		{
			case SET_CAN_PICK_UP_ITEMS_NO:
				Data.putData(p, MParam.NO_PICK_UP_ITEMS);
				return;
			case SET_CAN_PICK_UP_ITEMS_RANDOM:
				Data.putRandom_data(p, MParam.NO_PICK_UP_ITEMS);
				return;
			case SET_CAN_PICK_UP_ITEMS_YES:
				Data.removeData(p, MParam.NO_PICK_UP_ITEMS);
				return;
			case TOGGLE_CAN_PICK_UP_ITEMS:
				Data.toggleData(p ,MParam.NO_PICK_UP_ITEMS);
				return;
		}
	}
	
	private void setAnimal_property(Animals animal)
	{
		// Chicken, Cow, MushroomCow, Ocelot, Pig, Sheep, Wolf
		switch (getAction_type())
		{			
			case SET_OWNER:
				if (animal instanceof Tameable)
				{
					((Tameable)animal).setOwner(Bukkit.getPlayer(getValue()));
				}
				return;
		}
		
		switch (animal.getType())
		{
			case OCELOT:
				setOcelot_property((Ocelot)animal);
				return;
			case PIG:
				setPig_property((Pig)animal);
				return;
			case SHEEP:
				setSheep_property((Sheep)animal);
				return;
			case WOLF:
				setWolf_property((Wolf)animal);
				return;
				
			case MUSHROOM_COW:
				switch (getAction_type())
				{
					case SET_CAN_BE_SHEARED_NO:
						Data.putData(animal, MParam.NO_SHEARED);
						return;
					case SET_CAN_BE_SHEARED_RANDOM:
						Data.putRandom_data(animal, MParam.NO_SHEARED);
						return;
					case SET_CAN_BE_SHEARED_YES:
						Data.removeData(animal, MParam.NO_SHEARED);
						return;
					case TOGGLE_CAN_BE_SHEARED:
						Data.toggleData(animal ,MParam.NO_SHEARED);
						return;
				}
				break;				
		}
	}
	
	private void setMonster_property(Monster monster)
	{
		// Blaze, CaveSpider, Creeper, Enderman, Giant, PigZombie, Silverfish, Skeleton, Spider, Zombie
		switch (getAction_type())
		{
			case SET_FRIENDLY_YES:
				Data.putData(monster, MParam.FRIENDLY);
				return;
		}
		
		switch (monster.getType())
		{
			//case BLAZE:
			//case CAVE_SPIDER:
			case CREEPER:
				setCreeper_property((Creeper)monster);
				return;
			case ENDERMAN:
				setEnderman_property((Enderman)monster);
				return;
		//	case GIANT:
			case PIG_ZOMBIE:
				setPig_zombie_property((PigZombie)monster);
				return;
			case SILVERFISH:
			case SKELETON:
			case SPIDER:
			case ZOMBIE:
				return;
		}
	}
	
	private void setCreeper_property(Creeper creeper)
	{
		switch (getAction_type())
		{
			case SET_CAN_EVOLVE_NO:
				Data.putData(creeper, MParam.NO_EVOLVE);
				return;
			case SET_CAN_EVOLVE_RANDOM:
				Data.putRandom_data(creeper, MParam.NO_EVOLVE);
				return;
			case SET_CAN_EVOLVE_YES:
				Data.removeData(creeper, MParam.NO_EVOLVE);
				return;
			case TOGGLE_CAN_EVOLVE:
				Data.toggleData(creeper, MParam.NO_EVOLVE);
				return;
				
			case SET_FIERY_EXPLOSION_NO:
				Data.removeData(creeper, MParam.FIERY_EXPLOSION);
				return;
			case SET_FIERY_EXPLOSION_RANDOM:
				Data.putRandom_data(creeper, MParam.FIERY_EXPLOSION);
				return;
			case SET_FIERY_EXPLOSION_YES:
				Data.putData(creeper, MParam.FIERY_EXPLOSION);
				return;	
			case TOGGLE_FIERY_EXPLOSION:
				Data.toggleData(creeper, MParam.FIERY_EXPLOSION);
				return;
				
			case SET_EXPLOSION_SIZE:
				Data.putData(creeper, MParam.EXPLOSION_SIZE, getInt_value(0));
				return;	
		}		
		
		switch (getAction_type())
		{
			case SET_POWERED_NO:
				creeper.setPowered(false);
				return;
			case SET_POWERED_RANDOM:
				creeper.setPowered(new Random().nextBoolean());
				return;
			case SET_POWERED_YES:
				creeper.setPowered(true);
				return;
			case TOGGLE_POWERED:
				creeper.setPowered(!creeper.isPowered());
				return;
		}
	}
	
	private void setWolf_property(Wolf wolf)
	{
		switch (action_type)
		{
			case SET_CAN_BE_TAMED_NO:
				Data.putData(wolf, MParam.NO_TAMED);
				return;
			case SET_CAN_BE_TAMED_RANDOM:
				Data.putRandom_data(wolf, MParam.NO_TAMED);
				return;
			case SET_CAN_BE_TAMED_YES:
				Data.removeData(wolf, MParam.NO_TAMED);
				return;
			case TOGGLE_CAN_BE_TAMED:
				Data.toggleData(wolf, MParam.NO_TAMED);
				return;
		}
		
		switch (action_type)
		{
			case SET_ANGRY_NO:
				wolf.setAngry(false);
				return;
			case SET_ANGRY_RANDOM:
				if (new Random().nextBoolean()) setAngry(wolf); else wolf.setAngry(false);
				return;
			case SET_ANGRY_YES:
				setAngry(wolf);
				return;
			case TOGGLE_ANGRY:
				if (!wolf.isAngry()) setAngry(wolf); else wolf.setAngry(false);
				return;
				
			case SET_TAMED_NO:
				wolf.setTamed(false);
				return;
			case SET_TAMED_RANDOM:
				wolf.setTamed(new Random().nextBoolean());
				return;
			case SET_TAMED_YES:
				wolf.setTamed(true);
				return;
			case TOGGLE_TAMED:
				wolf.setTamed(!wolf.isTamed());
				return;
		}
	}
	
	private void setAngry(Wolf wolf)
	{
		for (Entity e : wolf.getNearbyEntities(50, 50, 50)) if (e instanceof Player)
		{
			EntityWolf cw = ((CraftWolf)wolf).getHandle();
			cw.b(((CraftLivingEntity)e).getHandle());
			return;
		}
	}
	
	private void setEnderman_property(Enderman enderman)
	{
		switch (action_type)
		{
			case SET_CAN_MOVE_BLOCKS_NO:
				Data.putData(enderman, MParam.NO_MOVE_BLOCKS);
				return;
			case SET_CAN_MOVE_BLOCKS_RANDOM:
				Data.putRandom_data(enderman, MParam.NO_MOVE_BLOCKS);
				return;
			case SET_CAN_MOVE_BLOCKS_YES:
				Data.removeData(enderman, MParam.NO_MOVE_BLOCKS);
				return;	
			case TOGGLE_CAN_MOVE_BLOCKS:
				Data.toggleData(enderman, MParam.NO_MOVE_BLOCKS);
				return;
				
			case SET_CAN_TELEPORT_NO:
				Data.putData(enderman, MParam.NO_TELEPORT);
				return;
			case SET_CAN_TELEPORT_RANDOM:
				Data.putRandom_data(enderman, MParam.NO_TELEPORT);
				return;
			case SET_CAN_TELEPORT_YES:
				Data.removeData(enderman, MParam.NO_TELEPORT);
				return;	
			case TOGGLE_CAN_TELEPORT:
				Data.toggleData(enderman, MParam.NO_TELEPORT);
				return;	
		}
	}
	
	private void setEnder_dragon_property(EnderDragon ender_dragon)
	{
		switch (action_type)
		{
			case SET_CAN_CREATE_PORTALS_NO:
				Data.putData(ender_dragon, MParam.NO_CREATE_PORTALS);
				return;
			case SET_CAN_CREATE_PORTALS_RANDOM:
				Data.putRandom_data(ender_dragon, MParam.NO_CREATE_PORTALS);
				return;
			case SET_CAN_CREATE_PORTALS_YES:
				Data.removeData(ender_dragon, MParam.NO_CREATE_PORTALS);
				return;	
			case TOGGLE_CAN_CREATE_PORTALS:
				Data.toggleData(ender_dragon, MParam.NO_CREATE_PORTALS);
				return;
				
			case SET_CAN_DESTROY_BLOCKS_NO:
				Data.putData(ender_dragon, MParam.NO_DESTROY_BLOCKS);
				return;
			case SET_CAN_DESTROY_BLOCKS_RANDOM:
				Data.putRandom_data(ender_dragon, MParam.NO_DESTROY_BLOCKS);
				return;
			case SET_CAN_DESTROY_BLOCKS_YES:
				Data.removeData(ender_dragon, MParam.NO_DESTROY_BLOCKS);
				return;	
			case TOGGLE_CAN_DESTROY_BLOCKS:
				Data.toggleData(ender_dragon, MParam.NO_DESTROY_BLOCKS);
				return;
		}	
	}
	
	private void setPig_zombie_property(PigZombie pig_zombie)
	{		
		switch (action_type)
		{
			case SET_ANGRY_NO:
				pig_zombie.setAngry(false);
				return;
			case SET_ANGRY_RANDOM:
				pig_zombie.setAngry(new Random().nextBoolean());
				return;
			case SET_ANGRY_YES:
				pig_zombie.setAngry(true);
				return;
			case TOGGLE_ANGRY:
				pig_zombie.setAngry(!pig_zombie.isAngry());
				return;
		}
	}
	
	private void setOcelot_property(Ocelot ocelot)
	{
		switch (action_type)
		{
			case SET_CAN_BE_TAMED_NO:
				Data.putData(ocelot, MParam.NO_TAMED);
				return;
			case SET_CAN_BE_TAMED_RANDOM:
				Data.putRandom_data(ocelot, MParam.NO_TAMED);
				return;
			case SET_CAN_BE_TAMED_YES:
				Data.removeData(ocelot, MParam.NO_TAMED);
				return;
			case TOGGLE_CAN_BE_TAMED:
				Data.toggleData(ocelot, MParam.NO_TAMED);
				return;
		}
		
		switch (action_type)
		{
			//sitting
				
			case SET_TAMED_NO:
				ocelot.setTamed(false);
				return;
			case SET_TAMED_RANDOM:
				ocelot.setTamed(new Random().nextBoolean());
				return;
			case SET_TAMED_YES:
				ocelot.setTamed(true);
				return;
			case TOGGLE_TAMED:
				ocelot.setTamed(!ocelot.isTamed());
				return;
			case SET_OCELOT_TYPE:
				ocelot.setCatType(Ocelot.Type.valueOf(getValue().toUpperCase()));
				return;
		}
	}
		
	private void setPig_property(Pig pig)
	{
		switch (action_type)
		{
			case SET_CAN_BE_SADDLED_NO:
				Data.putData(pig, MParam.NO_SADDLED);
				return;
			case SET_CAN_BE_SADDLED_RANDOM:
				Data.putRandom_data(pig, MParam.NO_SADDLED);
				return;
			case SET_CAN_BE_SADDLED_YES:
				Data.removeData(pig, MParam.NO_SADDLED);
				return;
			case TOGGLE_CAN_BE_SADDLED:
				Data.toggleData(pig, MParam.NO_SADDLED);
				return;
				
			case SET_CAN_EVOLVE_NO:
				Data.putData(pig, MParam.NO_EVOLVE);
				return;
			case SET_CAN_EVOLVE_RANDOM:
				Data.putRandom_data(pig, MParam.NO_EVOLVE);
				return;
			case SET_CAN_EVOLVE_YES:
				Data.removeData(pig, MParam.NO_EVOLVE);
				return;
			case TOGGLE_CAN_EVOLVE:
				Data.toggleData(pig, MParam.NO_EVOLVE);
				return;
		}

		switch (action_type)
		{
			case SET_SADDLED_NO:
				pig.setSaddle(false);
				return;
			case SET_SADDLED_RANDOM:
				pig.setSaddle(new Random().nextBoolean());
				return;
			case SET_SADDLED_YES:
				pig.setSaddle(true);
				return;
			case TOGGLE_SADDLED:
				pig.setSaddle(!pig.hasSaddle());
				return;
		}
	}
	
	private void setSheep_property(Sheep sheep)
	{
		switch (action_type)
		{
			case SET_CAN_BE_DYED_NO:
				Data.putData(sheep ,MParam.NO_DYED);
				return;
			case SET_CAN_BE_DYED_RANDOM:
				Data.putRandom_data(sheep ,MParam.NO_DYED);
				return;
			case SET_CAN_BE_DYED_YES:
				Data.removeData(sheep ,MParam.NO_DYED);
				return;
			case TOGGLE_CAN_BE_DYED:
				Data.toggleData(sheep ,MParam.NO_DYED);
				return;
				
			case SET_CAN_BE_SHEARED_NO:
				Data.putData(sheep ,MParam.NO_SHEARED);
				return;
			case SET_CAN_BE_SHEARED_RANDOM:
				Data.putRandom_data(sheep ,MParam.NO_SHEARED);
				return;
			case SET_CAN_BE_SHEARED_YES:
				Data.removeData(sheep ,MParam.NO_SHEARED);
				return;
			case TOGGLE_CAN_BE_SHEARED:
				Data.toggleData(sheep ,MParam.NO_SHEARED);
				return;
				
			case SET_CAN_GRAZE_NO:
				Data.putData(sheep ,MParam.NO_GRAZE);
				return;
			case SET_CAN_GRAZE_RANDOM:
				Data.putRandom_data(sheep ,MParam.NO_GRAZE);
				return;
			case SET_CAN_GRAZE_YES:
				Data.removeData(sheep ,MParam.NO_GRAZE);
				return;
			case TOGGLE_CAN_GRAZE:
				Data.toggleData(sheep ,MParam.NO_GRAZE);
				return;
				
			case SET_CAN_GROW_WOOL_NO:
				Data.putData(sheep ,MParam.NO_GROW_WOOL);
				return;
			case SET_CAN_GROW_WOOL_RANDOM:
				Data.putRandom_data(sheep ,MParam.NO_GROW_WOOL);
				return;
			case SET_CAN_GROW_WOOL_YES:
				Data.removeData(sheep ,MParam.NO_GROW_WOOL);
				return;
			case TOGGLE_CAN_GROW_WOOL:
				Data.toggleData(sheep ,MParam.NO_GROW_WOOL);
				return;
		}

		switch (action_type)
		{
			case SET_SHEARED_NO:
				sheep.setSheared(false);
				return;
			case SET_SHEARED_RANDOM:
				sheep.setSheared(new Random().nextBoolean());
				return;
			case SET_SHEARED_YES:
				sheep.setSheared(true);
				return;
			case TOGGLE_SHEARED:
				sheep.setSheared(!sheep.isSheared());
				return;
			
			case SET_WOOL_BLACK:
				sheep.setColor(DyeColor.BLACK);
				return;
			case SET_WOOL_BLUE:
				sheep.setColor(DyeColor.BLUE);
				return;
			case SET_WOOL_BROWN:
				sheep.setColor(DyeColor.BROWN);
				return;
			case SET_WOOL_CYAN:
				sheep.setColor(DyeColor.CYAN);
				return;
			case SET_WOOL_GRAY:
				sheep.setColor(DyeColor.GRAY);
				return;
			case SET_WOOL_GREEN:
				sheep.setColor(DyeColor.GREEN);
				return;
			case SET_WOOL_LIGHT_BLUE:
				sheep.setColor(DyeColor.LIGHT_BLUE);
				return;
			case SET_WOOL_LIME:
				sheep.setColor(DyeColor.LIME);
				return;
			case SET_WOOL_MAGENTA:
				sheep.setColor(DyeColor.MAGENTA);
				return;
			case SET_WOOL_ORANGE:
				sheep.setColor(DyeColor.ORANGE);
				return;
			case SET_WOOL_PINK:
				sheep.setColor(DyeColor.PINK);
				return;
			case SET_WOOL_PURPLE:
				sheep.setColor(DyeColor.PURPLE);
				return;
			case SET_WOOL_RANDOM:
				sheep.setColor(DyeColor.getByData((byte) new Random().nextInt(16)));
				return;
			case SET_WOOL_RED:
				sheep.setColor(DyeColor.RED);
				return;
			case SET_WOOL_SILVER:
				sheep.setColor(DyeColor.SILVER);
				return;
			case SET_WOOL_WHITE:
				sheep.setColor(DyeColor.WHITE);
				return;
			case SET_WOOL_YELLOW:
				sheep.setColor(DyeColor.YELLOW);
				return;
		}
	}
	
	private void clear_drops(LivingEntity live, MEvent event, Event orig_event)
	{
		List<LivingEntity> list = Target_manager.get().getTargets(getTarget(), live, orig_event);
		
		for (LivingEntity le : list)
		{
			if (event == MEvent.DIES)
			{
				EntityDeathEvent e = (EntityDeathEvent)orig_event;
				switch (getAction_type())
				{
					case CLEAR_EXP:
						e.setDroppedExp(0);
						break;
					case CLEAR_DROPS:
						e.setDroppedExp(0);
						e.getDrops().clear();
						break;
				}
			}
			else Data.putData(le, MParam.valueOf(getAction_type().toString()));
		}
	}
		
	private void spawn_mob(LivingEntity le, Event orig_event)
	{
		String[] mob = null;
		String name = null;
		mob = getMob();
		name = mob.length > 1 ? mob[1] : null;
		int amount = getAmount(1);
		List<Location> list = Target_manager.get().getLocations(this, le, orig_event);
		for (int i = 0; i < amount; i++)
		{
			Mobs.getInstance().setMob_name(name);
			for (Location loc : list)
			{
				loc.getWorld().spawnEntity(loc, EntityType.valueOf(mob[0]));
			}
			if (!isLocked())
			{
				mob = getMob();
				name = mob.length > 1 ? mob[1] : null;
			}
		}
	}
		
	private void send_message(LivingEntity le, Event orig_event)
	{
		String s = getValue();
		if (s == null) s = getMessage();
		while (s.contains("^")) s = replace_constants(s, le);
			
		if (getAction_type().equals(MAction.SEND_MESSAGE))
		{
			for (LivingEntity l : Target_manager.get().getTargets(getTarget(), le, orig_event))
			{
				if (l instanceof Player)
				{
					Player p = (Player)l;
					p.sendMessage(s);
				}
				else 
				{
					continue;
				}
				if (!isLocked())
				{
					s = getValue();
					if (s == null) s = getMessage();
					while (s.contains("^")) s = replace_constants(s, le);
				}
			}
		}
		else if (getAction_type().equals(MAction.BROADCAST))
		{
			Bukkit.getServer().broadcastMessage(s);
		}
		else Mobs.log(s);
	}
	
	private void change_block(LivingEntity le, Event orig_event)
	{
		List<Location> locs = Target_manager.get().getLocations(this, le, orig_event);
			
		if (getAction_type() == MAction.SET_BLOCK)
		{
			Item_drop drop = getItem();
			if (drop == null) return;
			
			int id = drop.getId();
			short data = drop.getData();
			for (Location loc : locs)
			{
				loc.getBlock().setTypeIdAndData(id, (byte) data, false);
			}
		}
		else
		{
			for (Location loc : locs)
			{
				loc.getBlock().breakNaturally();
			}
		}
	}
	
	private void strike_with_lightning(LivingEntity le, Event orig_event)
	{
		for (Location loc : Target_manager.get().getLocations(this, le, orig_event))
		{
			World w = loc.getWorld();
			if (getAction_type() == MAction.LIGHTNING_EFFECT) w.strikeLightningEffect(loc);
			else w.strikeLightning(loc);
		}
	}
	
	private void cause_explosion(LivingEntity le, Event orig_event)
	{
		int p = getInt_value(0);
		for (Location loc : Target_manager.get().getLocations(this, le, orig_event))
		{
			loc.getWorld().createExplosion(loc, p, getAction_type() == MAction.FIERY_EXPLOSION);
		}
	}
	
	private void damage_mob(LivingEntity le, Event orig_event)
	{
		for (LivingEntity l : Target_manager.get().getTargets(getTarget(), le, orig_event))
		{
			if (l == null) break;
			int q = 0;
			if (getAction_type().equals(MAction.KILL))
			{
				l.damage(10000);
			}
			else if (getAction_type().equals(MAction.DAMAGE))
			{
				q = getInt_value(0);
				l.damage(q);
			}
			else if (!(l instanceof Player))
			{
				l.remove();
			}
		}
	}
	
	private void change_weather(LivingEntity le)
	{
		World w = getWorld(le);
		if (w == null) return;
		
		//String s2 = getAction_type().toString();
		switch (getAction_type())
		{
			case SUN:
				w.setStorm(false);
				w.setThundering(false);
				break;
			case RAIN:
				w.setStorm(true);
				w.setThundering(false);
				break;
			case STORM:
				w.setStorm(true);
				w.setThundering(true);
				break;
		}
		Integer i = getInt_value(0);

		if (i != null)
		{
			//s2 += ", DURATION = " + i + " seconds";
			w.setWeatherDuration(i * 20);
		}
		//else s2 += ", DEFAULT DURATION";
	}
	
	private void change_time(LivingEntity le)
	{
		World w = getWorld(le);
		if (w == null) return;
		
		long time = getInt_value((int)w.getTime()) % 24000;
		if (time < 0) time += 24000;
		w.setTime(time);
	}
	
	private void drop_item(LivingEntity le, Event orig_event)
	{	
		Item_drop drop = getItem();
		int id = drop.getId();
		short data = drop.getData();
		ItemStack is = new ItemStack(id, getAmount(1), data);
		
		for (Location loc : Target_manager.get().getLocations(this, le, orig_event))
		{
			loc.getWorld().dropItem(loc, is);
		}
	}
	
	private void give_item(LivingEntity le, Event orig_event)
	{
		Target t = getTarget();
		if (t == null)
		{
			return;
		}
		
		List<LivingEntity> list = Target_manager.get().getTargets(t, le, orig_event);
		
		if (getAction_type() == MAction.CLEAR_ITEMS)
		{
			for (LivingEntity l : list)
			{		
				if (!(l instanceof Player)) continue;
				Player p = (Player)l;
				p.getInventory().clear();				
			}
			return;
		}
		
		Item_drop drop = getItem();
		if (drop == null) return;
		int id = drop.getId();
		short data = drop.getData();
		int amount = drop.getAmount();
		ItemStack stack = new ItemStack(id, amount, data);
		
		for (LivingEntity l : list)
		{		
			if (!(l instanceof Player)) continue;
			Player p = (Player)l;			
			
			if (getAction_type() == MAction.GIVE_ITEM)
			{
				p.getInventory().addItem(stack);
			}
			else
			{
				for (ItemStack is : p.getInventory().getContents())
				{
					if (drop.matches(is, stack)) p.getInventory().remove(is);
				}
			}	
			if (!isLocked() && list.size() > 1) 
			{
				drop = getItem();
				id = drop.getId();
				data = drop.getData();
				amount = getAmount(1);
				stack = new ItemStack(id, amount, data);
			}
		}
	}
	
	private void drop_exp(LivingEntity le, Event orig_event)
	{
		Integer q = getInt_value(0);
		if (q == null || q == 0) return;
		
		for (Location loc : Target_manager.get().getLocations(this, le, orig_event))
		{	
			ExperienceOrb orb = (ExperienceOrb)loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
			orb.setExperience(q);
		}
	}
	
	private void give_exp(LivingEntity le, Event orig_event)
	{
		Integer q = getInt_value(0);
		if (q == null || q == 0) return;
		
		for (LivingEntity l : Target_manager.get().getTargets(getTarget(), le, orig_event))
		{
			if (!(l instanceof Player)) continue;
			Player p = (Player)l;
			
			if (getAction_type() == MAction.SET_EXP)
			{
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
			}
			else
			{
				p.setLevel(q);	
			}
		}
	}
	
	private void activate_mechanism(LivingEntity le, String type, Event orig_event)
	{
		for (Location l : Target_manager.get().getLocations(this, le, orig_event))
		{
			BlockState bs = l.getBlock().getState();
			MaterialData md = bs.getData();
			if (type.equalsIgnoreCase("button") && bs.getData() instanceof Button)
			{
				((Button)md).setPowered(true);
			}
			else if (type.equalsIgnoreCase("door") && bs.getData() instanceof Door)
			{
				switch (getAction_type())
				{
					case CLOSE_DOOR:
						((Door)md).setOpen(false);
						break;
					case OPEN_DOOR:
						((Door)md).setOpen(true);
						break;
					case TOGGLE_DOOR:
						((Door)md).setOpen(!((Door)md).isOpen());
						break;
				}
			}
			else if (type.equalsIgnoreCase("gate") && bs.getData() instanceof Gate)
			{
				switch (getAction_type())
				{
					case CLOSE_GATE:
						((Gate)md).setOpen(false);
						break;
					case OPEN_GATE:
						((Gate)md).setOpen(true);
						break;
					case TOGGLE_GATE:
						((Gate)md).setOpen(!((Gate)md).isOpen());
						break;
				}
			}
			else if (type.equalsIgnoreCase("lever") && bs.getData() instanceof Lever)
			{
				switch (getAction_type())
				{
					case PUSH_LEVER:
						((Lever)md).setPowered(false);
						break;
					case PULL_LEVER:
						((Lever)md).setPowered(true);
						break;
					case TOGGLE_LEVER:
						((Lever)md).setPowered(!((Lever)md).isPowered());
						break;
				}
			}
			else if (type.equalsIgnoreCase("trapdoor") && bs.getData() instanceof TrapDoor)
			{
				switch (getAction_type())
				{
					case CLOSE_TRAPDOOR:
						((TrapDoor)md).setOpen(false);
						break;
					case OPEN_TRAPDOOR:
						((TrapDoor)md).setOpen(true);
						break;
					case TOGGLE_TRAPDOOR:
						((TrapDoor)md).setOpen(!((TrapDoor)md).isOpen());
						break;
				}
			}
			if (md != null)
			{
				bs.setData(md);
				bs.update(true);
			}
		}
	}

	//private String get_string_from_loc(Location loc)
	//{
	//	return "loc = " + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ", " + loc.getWorld().getName();
	//}
	
	private String replace_constants(String s, LivingEntity le)
	{
		int start = s.indexOf("^");
		int end = s.indexOf("^", start + 1);
		String sub = s.substring(start, end + 1);
		String name = s.substring(start + 1, end);
		String replacement = "";
		
		if (name.equalsIgnoreCase("killer") && le.getKiller() != null) replacement = le.getKiller().getName();
		
		if (name.equalsIgnoreCase("server_name")) replacement = le.getServer().getName();
		if (name.equalsIgnoreCase("world_name")) replacement = le.getWorld().getName();
		if (name.equalsIgnoreCase("online_player_count")) replacement = Integer.toString(le.getServer().getOnlinePlayers().length);
		if (name.equalsIgnoreCase("offline_player_count")) replacement = Integer.toString(le.getServer().getOfflinePlayers().length);
		if (name.equalsIgnoreCase("world_player_count")) replacement = Integer.toString(le.getWorld().getPlayers().size());

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
		
		s = s.replace(sub, replacement);
		s = s.trim();
		s = s.replaceAll("  ", " ");
		return s;
	}
	
	private void fillItems(Element element) throws XPathExpressionException
	{
		NodeList list = (NodeList)Mobs.getXPath().evaluate("item", element, XPathConstants.NODESET);		
		if (list.getLength() > 0)
		{
			SortedMap<Integer, Object> temp = new TreeMap<Integer, Object>();
			int count = 0;
			for (int i = 0; i < list.getLength(); i ++)
			{
				Element el = (Element)list.item(i);
				int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
				count += ratio;
				if (list.getLength() == 1) count = 1;						
				temp.put(count, new Item_drop(el));	
			}
			items = new Alternatives(count, temp);
		}
	}
	
	/*private void fillMisc(Param p, Element element)
	{
		NodeList list;
		Map<Integer, Object> temp;
		int count;
		try
		{			
			list = (NodeList)Mobs.getXPath().evaluate("power | duration | time", element, XPathConstants.NODESET);		
			if (list.getLength() > 0)
			{
				temp = new HashMap<Integer, Object>();
				count = 0;
				for (int i = 0; i < list.getLength(); i ++)
				{
					Element el = (Element)list.item(i);
					int ratio = el.hasAttribute("ratio") ? Integer.parseInt(el.getAttribute("ratio")) : 1;
					count += ratio;
					if (list.getLength() == 1) count = 1;						
					temp.put(count, el.getTextContent());	
				}
				p.addParam(MParam.NUMBER, new Alternatives(count, temp));
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}*/
	
	public MAction getAction_type() 
	{
		return action_type;
	}
	
	public Item_drop getItem()
	{
		if (items == null) return null;
		return (Item_drop)items.get_alternative();
	}
	
	public String[] getMob()
	{
		if (mob == null) return null;
		return mob.getValue().toUpperCase().split(":");
	}
	
	public Text_value getPure_amount()
	{
		if (amount != null)	return amount;
		return value;
	}
	
	public int getAmount(int orig)
	{
		if (amount == null) return orig;
		return amount.getInt_value(orig);			
	}
	
	public String getValue()
	{
		if (value == null) return null;
		return value.getValue();
	}
	
	public Integer getInt_value(int orig)
	{
		Text_value tv = value == null ? amount : value;
		if (tv == null) return orig;
		return tv.getInt_value(orig);
	}	
	
	public boolean isLocked()
	{
		return locked;
	}
	
	public String getMessage()
	{
		if (message == null) return null;
		return message.getValue();
	}
}