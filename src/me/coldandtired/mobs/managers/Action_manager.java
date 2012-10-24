package me.coldandtired.mobs.managers;

import java.util.List;
import java.util.Random;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Action;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.elements.Text_value;
import me.coldandtired.mobs.enums.MAction;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.subelements.Item_drop;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
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

public class Action_manager 
{
	private Random rng = new Random();
	private static Target_manager tm = Target_manager.get();
	private static Action_manager am;
		
	private Action_manager()
	{
		// empty for singleton
	}
	
	public static Action_manager get()
	{
		if (am == null) am = new Action_manager();
		return am;
	}
	
	boolean performActions(Outcome o, MEvent event, LivingEntity le, Event orig_event)
	{
		boolean single_outcome = true;
		Mobs.debug("Performing actions");
		List<Action> actions = o.getActions();
		if (actions == null) Mobs.debug("No actions to perform!");
		else for (Action a : actions)
		{
			boolean b = perform_action(event, a, le, orig_event);
			if (b) single_outcome = false;
		}
		Mobs.debug("------------------");
		return single_outcome;
	}
	
	boolean perform_action(MEvent event, Action a, LivingEntity le, Event orig_event)
	{				
		//properties should have reversed, default x% values

		//Hp = default
		switch (a.getAction_type())
		{
			case SET_CUSTOM_FLAG_1:
			case SET_CUSTOM_FLAG_2:
			case SET_CUSTOM_FLAG_3:
			case SET_CUSTOM_FLAG_4:
			case SET_CUSTOM_FLAG_5:
			case SET_CUSTOM_FLAG_6:
			case SET_CUSTOM_FLAG_7:
			case SET_CUSTOM_FLAG_8:
			case SET_CUSTOM_FLAG_9:
			case SET_CUSTOM_FLAG_10:
				setCustom_flag(a, le);
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
				setCustom_value(a, le);
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
				removeCustom_value(a, le);
				break;
				
			case CONTINUE: return true;
			case DROP_ITEM:
				drop_item(a, le);
				break;
			case GIVE_ITEM:
			case REMOVE_ITEM:
			case CLEAR_ITEMS:
				give_item(a, le);
				break;
			case SET_MONEY:
				setMoney(a, le);
				break;
			case DROP_EXP:
				drop_exp(a, le);
				break;
			case SET_EXP:
			case SET_LEVEL:
				give_exp(a, le);
				break;
			case PRESS_BUTTON:
				activate_mechanism(a, le, "button");
				break;
			case CLOSE_DOOR:
			case OPEN_DOOR:
			case TOGGLE_DOOR:
				activate_mechanism(a, le, "door");
				break;
			case CLOSE_GATE:
			case OPEN_GATE:
			case TOGGLE_GATE:
				activate_mechanism(a, le, "gate");
				break;
			case PULL_LEVER:
			case PUSH_LEVER:
			case TOGGLE_LEVER:
				activate_mechanism(a, le, "lever");
				break;
			case CLOSE_TRAPDOOR:
			case OPEN_TRAPDOOR:
			case TOGGLE_TRAPDOOR:
				activate_mechanism(a, le, "trapdoor");
				break;
			case LIGHTNING:
			case LIGHTNING_EFFECT:
				strike_with_lightning(a, le);
				break;
			case EXPLOSION:
			case FIERY_EXPLOSION:
				cause_explosion(a, le);
				break;
			case DAMAGE:
			case KILL:
			case REMOVE:
				damage_mob(a, le);
				break;
			case SET_TIME:
				change_time(a, le);
				break;
			case RAIN:
			case STORM:
			case SUN:
				change_weather(a, le);
				break;
			case SET_BLOCK:
			case DESTROY_BLOCK:
				change_block(a, le);
				break;	
			case SEND_MESSAGE:
			case BROADCAST:
			case LOG:
				send_message(a, le); // ok
				break;
			case SPAWN_MOB:
				spawn_mob(a, le);
				break;
			case CLEAR_DATA:
				clearData(a, le);
				break;
			case CLEAR_DROPS:
			case CLEAR_EXP:
				clear_drops(a, le, event, orig_event);
				break;
			case CANCEL_EVENT:
				if (orig_event instanceof Cancellable)
				{
					((Cancellable)orig_event).setCancelled(true);
					Mobs.debug("CANCEL_EVENT");
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
				playEffect(a, le);
				break;
			default:		
				setProperty(a, le);
				Mobs.debug(a.getAction_type().toString());
				break;
		}
		return false;
	}
	
	private void removeCustom_value(Action a, LivingEntity live)
	{
		for (LivingEntity le : tm.getTargets(a.getTarget(), live)) 
			Data.removeData(le, MParam.valueOf(a.getAction_type().toString().substring(7)));
	}
	
	private void setCustom_flag(Action a, LivingEntity live)
	{
		for (LivingEntity le : tm.getTargets(a.getTarget(), live)) 
			Data.putData(le, MParam.valueOf(a.getAction_type().toString().substring(4)));
	}
	
	private void setCustom_value(Action a, LivingEntity live)
	{
		for (LivingEntity le : tm.getTargets(a.getTarget(), live)) 
			Data.putData(le, MParam.valueOf(a.getAction_type().toString().substring(4)), a.getValue());
	}
	
	private void clearData(Action a, LivingEntity live)
	{
		for (LivingEntity le : tm.getTargets(a.getTarget(), live)) 
		{
			Data.clearData(le);
			Mobs.debug("CLEAR_DATA, " + le.getType().toString());
		}
	}
	
	private void setMoney(Action a, LivingEntity le)
	{
		if (Mobs.economy == null)
		{
			Mobs.warn("SET_MONEY failed - no economy plugin!");
			return;
		}
		List<LivingEntity> list = tm.getTargets(a.getTarget(), le);
		
		Text_value value = a.getPure_amount();
		for (LivingEntity l : list)
		{		
			if (!(l instanceof Player)) continue;
			Player p = (Player)l;
			double amount = Mobs.economy.getBalance(p.getName());
			Mobs.economy.withdrawPlayer(p.getName(), amount);
			int am = value.getInt_value((int)amount);
			Mobs.economy.depositPlayer(p.getName(), am);
			if (!a.isLocked() && list.size() > 1) value = a.getPure_amount();
			Mobs.debug("SET_MONEY " + p.getName() + ", " + am);
		}
	}
	
	private void playEffect(Action a, LivingEntity le)
	{
		Effect effect = null;
		switch (a.getAction_type())
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
		for (Location loc : Target_manager.get().getLocations(a.getTarget(), le)) loc.getWorld().playEffect(loc, effect, 10);
	}
	
	private void setProperty(Action a, LivingEntity live)
	{
		List<LivingEntity> list = tm.getTargets(a.getTarget(), live);
		
		for (LivingEntity le : list)
		{
			if (le == null) le = live;
			if (le instanceof Player) setPlayer_property(a, (Player)le);
			if (le instanceof Animals) setAnimal_property(a, (Animals)le);
			if (le instanceof Monster) setMonster_property(a, (Monster)le);
			if (le instanceof EnderDragon) setEnder_dragon_property(a.getAction_type(), (EnderDragon)le);
			
			switch (a.getAction_type())
			{
				case SET_TITLE:
					if (Mobs.isSpout_enabled()) Spout.getServer().setTitle(le, a.getValue());
					break;
				case SET_SKIN:
					if (Mobs.isSpout_enabled()) Spout.getServer().setEntitySkin(le, a.getValue(), EntitySkinType.DEFAULT);
					break;
				case RESTORE_SKIN:
					if (Mobs.isSpout_enabled()) Spout.getServer().resetEntitySkin(le);
					break;
				case SET_MAX_LIFE:
					Integer i = (Integer)Data.getData(le, MParam.MAX_LIFE);
					if (i == null) i = 0;
					Data.putData(le, MParam.MAX_LIFE, a.getInt_value(i));
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
					Data.putData(le, MParam.NAME, a.getValue());
				break;
				case SET_MAX_HP:
					i = (Integer)Data.getData(le, MParam.MAX_HP);
					if (i == null) i = 0;
					Integer max_hp = a.getInt_value(i);
					Integer hp = (Integer)Data.getData(le, MParam.HP);
					if (hp == null || hp > max_hp) hp = max_hp;
					Data.putData(le, MParam.HP, hp);
					Data.putData(le, MParam.MAX_HP, max_hp);
					break;
				case SET_HP:
					i = (Integer)Data.getData(le, MParam.HP);
					if (i == null) i = 0;
					hp = a.getInt_value(i);
					max_hp = (Integer)Data.getData(le, MParam.MAX_HP);
					if (max_hp == null) max_hp = hp;
					if (hp > max_hp) hp = max_hp;
					Data.putData(le, MParam.HP, hp);
					Data.putData(le, MParam.MAX_HP, max_hp);
					break;
				case SET_ATTACK_POWER:
					Data.putData(le, MParam.ATTACK, a.getValue());
					break;
					
				case SET_DAMAGE_TAKEN_FROM_BLOCK_EXPLOSION:
					Data.putData(le, MParam.BLOCK_EXPLOSION_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_CONTACT:
					Data.putData(le, MParam.CONTACT_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_CUSTOM:
					Data.putData(le, MParam.CUSTOM_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_DROWNING:
					Data.putData(le, MParam.DROWNING_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_ATTACK:
					Data.putData(le, MParam.ATTACK_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_ENTITY_EXPLOSION:
					Data.putData(le, MParam.ENTITY_EXPLOSION_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_FALL:
					Data.putData(le, MParam.FALL_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_FIRE:
					Data.putData(le, MParam.FIRE_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_FIRE_TICK:
					Data.putData(le, MParam.FIRE_TICK_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_LAVA:
					Data.putData(le, MParam.LAVA_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_LIGHTNING:
					Data.putData(le, MParam.LIGHTNING_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_MAGIC:
					Data.putData(le, MParam.MAGIC_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_MELTING:
					Data.putData(le, MParam.MELTING_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_POISON:
					Data.putData(le, MParam.POISON_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_PROJECTILE:
					Data.putData(le, MParam.PROJECTILE_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_STARVATION:
					Data.putData(le, MParam.STARVATION_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_SUFFOCATION:
					Data.putData(le, MParam.SUFFOCATION_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_SUICIDE:
					Data.putData(le, MParam.SUICIDE_DAMAGE, a.getValue());
					break;
				case SET_DAMAGE_TAKEN_FROM_VOID:
					Data.putData(le, MParam.VOID_DAMAGE, a.getValue());
					break;
			}
		}
	}
		
	private void setPlayer_property(Action a, Player p)
	{
		switch (a.getAction_type())
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
	
	private void setAnimal_property(Action a, Animals animal)
	{
		// Chicken, Cow, MushroomCow, Ocelot, Pig, Sheep, Wolf
		switch (a.getAction_type())
		{
			case SET_ADULT_NO:
				animal.setBaby();
				return;
			case SET_ADULT_RANDOM:
				if (rng.nextBoolean()) animal.setAdult(); else animal.setBaby();
				return;
			case SET_ADULT_YES:
				animal.setAdult();
				return;
			case TOGGLE_ADULT:
				if (animal.isAdult()) animal.setBaby(); else animal.setAdult();
				return;
			
			case SET_OWNER:
				if (animal instanceof Tameable)
				{
					((Tameable)animal).setOwner(Bukkit.getPlayer(a.getValue()));
				}
				return;
		}
		
		switch (animal.getType())
		{
			case OCELOT:
				setOcelot_property(a.getAction_type(), (Ocelot)animal);
				return;
			case PIG:
				setPig_property(a.getAction_type(), (Pig)animal);
				return;
			case SHEEP:
				setSheep_property(a.getAction_type(), (Sheep)animal);
				return;
			case WOLF:
				setWolf_property(a.getAction_type(), (Wolf)animal);
				return;
				
			case MUSHROOM_COW:
				switch (a.getAction_type())
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
	
	private void setMonster_property(Action a, Monster monster)
	{
		// Blaze, CaveSpider, Creeper, Enderman, Giant, PigZombie, Silverfish, Skeleton, Spider, Zombie
		switch (a.getAction_type())
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
				setCreeper_property(a, (Creeper)monster);
				return;
			case ENDERMAN:
				setEnderman_property(a.getAction_type(), (Enderman)monster);
				return;
		//	case GIANT:
			case PIG_ZOMBIE:
				setPig_zombie_property(a.getAction_type(), (PigZombie)monster);
				return;
			case SILVERFISH:
			case SKELETON:
			case SPIDER:
			case ZOMBIE:
				return;
		}
	}
	
	private void setCreeper_property(Action a, Creeper creeper)
	{
		switch (a.getAction_type())
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
				Data.putData(creeper, MParam.EXPLOSION_SIZE, a.getInt_value(0));
				return;	
		}		
		
		switch (a.getAction_type())
		{
			case SET_POWERED_NO:
				creeper.setPowered(false);
				return;
			case SET_POWERED_RANDOM:
				creeper.setPowered(rng.nextBoolean());
				return;
			case SET_POWERED_YES:
				creeper.setPowered(true);
				return;
			case TOGGLE_POWERED:
				creeper.setPowered(!creeper.isPowered());
				return;
		}
	}
	
	private void setWolf_property(MAction a, Wolf wolf)
	{
		switch (a)
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
		
		switch (a)
		{
			case SET_ANGRY_NO:
				wolf.setAngry(false);
				return;
			case SET_ANGRY_RANDOM:
				wolf.setAngry(rng.nextBoolean());
				return;
			case SET_ANGRY_YES:
				wolf.setAngry(true);
				return;
			case TOGGLE_ANGRY:
				wolf.setAngry(!wolf.isAngry());
				return;
				
			case SET_TAMED_NO:
				wolf.setTamed(false);
				return;
			case SET_TAMED_RANDOM:
				wolf.setTamed(rng.nextBoolean());
				return;
			case SET_TAMED_YES:
				wolf.setTamed(true);
				return;
			case TOGGLE_TAMED:
				wolf.setTamed(!wolf.isTamed());
				return;
		}
	}
	
	private void setEnderman_property(MAction a, Enderman enderman)
	{
		switch (a)
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
		
		switch (a)
		{
		
		}
	}
	
	private void setEnder_dragon_property(MAction a, EnderDragon ender_dragon)
	{
		switch (a)
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
		
		switch (a)
		{
		
		}
	}
	
	private void setPig_zombie_property(MAction a, PigZombie pig_zombie)
	{		
		switch (a)
		{
			case SET_ANGRY_NO:
				pig_zombie.setAngry(false);
				return;
			case SET_ANGRY_RANDOM:
				pig_zombie.setAngry(rng.nextBoolean());
				return;
			case SET_ANGRY_YES:
				pig_zombie.setAngry(true);
				return;
			case TOGGLE_ANGRY:
				pig_zombie.setAngry(!pig_zombie.isAngry());
				return;
		}
	}
	
	private void setOcelot_property(MAction a, Ocelot ocelot)
	{
		switch (a)
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
		
		switch (a)
		{
			//sitting, cattype
				
			case SET_TAMED_NO:
				ocelot.setTamed(false);
				return;
			case SET_TAMED_RANDOM:
				ocelot.setTamed(rng.nextBoolean());
				return;
			case SET_TAMED_YES:
				ocelot.setTamed(true);
				return;
			case TOGGLE_TAMED:
				ocelot.setTamed(!ocelot.isTamed());
				return;
		}
	}
		
	private void setPig_property(MAction a, Pig pig)
	{
		switch (a)
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

		switch (a)
		{
			case SET_SADDLED_NO:
				pig.setSaddle(false);
				return;
			case SET_SADDLED_RANDOM:
				pig.setSaddle(rng.nextBoolean());
				return;
			case SET_SADDLED_YES:
				pig.setSaddle(true);
				return;
			case TOGGLE_SADDLED:
				pig.setSaddle(!pig.hasSaddle());
				return;
		}
	}
	
	private void setSheep_property(MAction a, Sheep sheep)
	{
		switch (a)
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

		switch (a)
		{
			case SET_SHEARED_NO:
				sheep.setSheared(false);
				return;
			case SET_SHEARED_RANDOM:
				sheep.setSheared(rng.nextBoolean());
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
				sheep.setColor(DyeColor.getByData((byte) rng.nextInt(16)));
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
	
	private void clear_drops(Action a, LivingEntity live, MEvent event, Event orig_event)
	{
		List<LivingEntity> list = tm.getTargets(a.getTarget(), live);
		
		for (LivingEntity le : list)
		{
			if (event == MEvent.DIES)
			{
				EntityDeathEvent e = (EntityDeathEvent)orig_event;
				switch (a.getAction_type())
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
			else Data.putData(le, MParam.valueOf(a.getAction_type().toString()));
			Mobs.debug(a.getAction_type().toString());
		}
	}
		
	private void spawn_mob(Action a, LivingEntity le)
	{
		String[] mob = null;
		String name = null;
		mob = a.getMob();
		name = mob.length > 1 ? mob[1] : null;
		int amount = a.getAmount(1);
		List<Location> list = tm.getLocations(a.getTarget(), le);
		for (int i = 0; i < amount; i++)
		{
			Mobs.getInstance().setMob_name(name);
			for (Location loc : list)
			{
				loc.getWorld().spawnEntity(loc, EntityType.valueOf(mob[0]));
				Mobs.debug("SPAWN_MOB, " + get_string_from_loc(loc) + ", " + name);
			}
			if (!a.isLocked())
			{
				mob = a.getMob();
				name = mob.length > 1 ? mob[1] : null;
			}
		}
	}
		
	private void send_message(Action a, LivingEntity le)
	{
		String s = a.getValue();
		if (s == null) s = a.getMessage();
		while (s.contains("^")) s = replace_constants(s, le);
			
		if (a.getAction_type().equals(MAction.SEND_MESSAGE))
		{
			for (LivingEntity l : tm.getTargets(a.getTarget(), le))
			{
				if (l instanceof Player)
				{
					Player p = (Player)l;
					p.sendMessage(s);
					Mobs.debug("SEND_MESSAGE, " + ((Player)p).getName() + ", MESSAGE = " + s);
				}
				else 
				{
					Mobs.debug("SEND_MESSAGE, failed - target isn't a player or is offline!");
					continue;
				}
				if (!a.isLocked())
				{
					s = a.getValue();
					if (s == null) s = a.getMessage();
					while (s.contains("^")) s = replace_constants(s, le);
				}
			}
		}
		else if (a.getAction_type().equals(MAction.BROADCAST))
		{
			Bukkit.getServer().broadcastMessage(s);
			Mobs.debug("BROADCAST, MESSAGE = " + s);
		}
		else Mobs.log(s);
	}
	
	private void change_block(Action a, LivingEntity le)
	{
		List<Location> locs = tm.getLocations(a.getTarget(), le);
			
		if (a.getAction_type() == MAction.SET_BLOCK)
		{
			Item_drop drop = a.getItem();
			if (drop == null) return;
			
			int id = drop.getId();
			short data = drop.getData();
			for (Location loc : locs)
			{
				loc.getBlock().setTypeIdAndData(id, (byte) data, false);
				Mobs.debug("SET_BLOCK, " + get_string_from_loc(loc) + ", ITEM = " + id + ":" + data);
			}
		}
		else
		{
			for (Location loc : locs)
			{
				loc.getBlock().breakNaturally();
				Mobs.debug("DESTROY_BLOCK, " + get_string_from_loc(loc));
			}
		}
	}
	
	private void strike_with_lightning(Action a, LivingEntity le)
	{
		for (Location loc : tm.getLocations(a.getTarget(), le))
		{
			World w = loc.getWorld();
			if (a.getAction_type() == MAction.LIGHTNING_EFFECT) w.strikeLightningEffect(loc);
			else w.strikeLightning(loc);
			Mobs.debug(a.getAction_type().toString() + ", " + get_string_from_loc(loc));
		}
	}
	
	private void cause_explosion(Action a, LivingEntity le)
	{
		int p = a.getInt_value(0);
		for (Location loc : tm.getLocations(a.getTarget(), le))
		{
			loc.getWorld().createExplosion(loc, p, a.getAction_type() == MAction.FIERY_EXPLOSION);
			Mobs.debug(a.getAction_type().toString() + ", POWER = " + p + ", " + get_string_from_loc(loc));
		}
	}
	
	private void damage_mob(Action a, LivingEntity le)
	{
		for (LivingEntity l : tm.getTargets(a.getTarget(), le))
		{
			int q = 0;
			if (a.getAction_type().equals(MAction.KILL))
			{
				l.damage(10000);
				Mobs.debug("KILL, " + l.getType().toString());
			}
			else if (a.getAction_type().equals(MAction.DAMAGE))
			{
				q = a.getInt_value(0);
				l.damage(q);
				Mobs.debug("DAMAGE, " + l.getType().toString() + ", AMOUNT = " + q);
			}
			else if (!(l instanceof Player))
			{
				l.remove();
				Mobs.debug("REMOVE, " + l.getType().toString());
			}
		}
	}
	
	private void change_weather(Action a, LivingEntity le)
	{
		World w = a.getWorld(le);
		if (w == null) return;
		
		String s2 = a.getAction_type().toString();
		switch (a.getAction_type())
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
		Integer i = a.getInt_value(0);

		if (i != null)
		{
			s2 += ", DURATION = " + i + " seconds";
			w.setWeatherDuration(i * 20);
		}
		else s2 += ", DEFAULT DURATION";
		Mobs.debug(s2 + ", " + w.getName());
	}
	
	private void change_time(Action a, LivingEntity le)
	{
		World w = a.getWorld(le);
		if (w == null) return;
		
		long time = a.getInt_value((int)w.getTime()) % 24000;
		if (time < 0) time += 24000;
		w.setTime(time);
		Mobs.debug("SET_TIME " + Long.toString(time) + ", " + w.getName());
	}
	
	private void drop_item(Action a, LivingEntity le)
	{	
		Item_drop drop = a.getItem();
		int id = drop.getId();
		short data = drop.getData();
		ItemStack is = new ItemStack(id, a.getAmount(1), data);
		
		for (Location loc : tm.getLocations(a.getTarget(), le))
		{
			loc.getWorld().dropItem(loc, is);
			Mobs.debug("DROP_ITEM, " + get_string_from_loc(loc) + ", ITEM = " + id + ":" + data);
		}
	}
	
	private void give_item(Action a, LivingEntity le)
	{
		Target t = a.getTarget();
		if (t == null)
		{
			Mobs.debug(a.getAction_type() + " failed - no target set!");
			return;
		}
		
		List<LivingEntity> list = tm.getTargets(t, le);
		
		if (a.getAction_type() == MAction.CLEAR_ITEMS)
		{
			for (LivingEntity l : list)
			{		
				if (!(l instanceof Player)) continue;
				Player p = (Player)l;
				p.getInventory().clear();
				Mobs.debug("CLEAR_ITEMS, " + p.getName());					
			}
			return;
		}
		
		Item_drop drop = a.getItem();
		if (drop == null) return;
		int id = drop.getId();
		short data = drop.getData();
		int amount = drop.getAmount();
		ItemStack stack = new ItemStack(id, amount, data);
		
		for (LivingEntity l : list)
		{		
			if (!(l instanceof Player)) continue;
			Player p = (Player)l;			
			
			if (a.getAction_type() == MAction.GIVE_ITEM)
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
			if (!a.isLocked() && list.size() > 1) 
			{
				drop = a.getItem();
				id = drop.getId();
				data = drop.getData();
				amount = a.getAmount(1);
				stack = new ItemStack(id, amount, data);
			}
			Mobs.debug(a.getAction_type() + ", " + p.getName() + ", ITEM = " + id + ":" + data);	
		}
	}
	
	private void drop_exp(Action a, LivingEntity le)
	{
		Integer q = a.getInt_value(0);
		if (q == null || q == 0) return;
		
		for (Location loc : tm.getLocations(a.getTarget(), le))
		{	
			ExperienceOrb orb = (ExperienceOrb)loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
			orb.setExperience(q);
			Mobs.debug("DROP_EXP, " + get_string_from_loc(loc) + ", AMOUNT = " + q);
		}
	}
	
	private void give_exp(Action a, LivingEntity le)
	{
		Integer q = a.getInt_value(0);
		if (q == null || q == 0) return;
		
		for (LivingEntity l : tm.getTargets(a.getTarget(), le))
		{
			if (!(l instanceof Player)) continue;
			Player p = (Player)l;
			
			if (a.getAction_type() == MAction.SET_EXP)
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
				Mobs.debug("SET_EXP, " + p.getName() + ", AMOUNT = " + q);
			}
			else
			{
				p.setLevel(q);	
				Mobs.debug("SET_LEVEL, " + p.getName() + ", AMOUNT = " + q);
			}
		}
	}
	
	private void activate_mechanism(Action a, LivingEntity le, String type)
	{
		for (Location l : tm.getLocations(a.getTarget(), le))
		{
			BlockState bs = l.getBlock().getState();
			MaterialData md = bs.getData();
			if (type.equalsIgnoreCase("button") && bs.getData() instanceof Button)
			{
				((Button)md).setPowered(true);
			}
			else if (type.equalsIgnoreCase("door") && bs.getData() instanceof Door)
			{
				switch (a.getAction_type())
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
				switch (a.getAction_type())
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
				switch (a.getAction_type())
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
				switch (a.getAction_type())
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
			
			Mobs.debug(a.getAction_type().toString());
		}
	}

	private String get_string_from_loc(Location loc)
	{
		return "loc = " + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ", " + loc.getWorld().getName();
	}
	
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
}