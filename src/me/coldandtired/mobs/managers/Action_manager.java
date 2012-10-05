package me.coldandtired.mobs.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Action;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.Mobs_action;
import me.coldandtired.mobs.enums.Mobs_const;
import me.coldandtired.mobs.enums.Mobs_event;
import me.coldandtired.mobs.subelements.Item_drop;
import me.coldandtired.mobs.subelements.Mobs_number;
import me.coldandtired.mobs.subelements.Target;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import org.bukkit.metadata.FixedMetadataValue;

public class Action_manager 
{
	private Random rng = new Random();
	
	public Action_manager()
	{
	}
	
	void performActions(Outcome o, Mobs_event event, LivingEntity le, Event orig_event)
	{
		Mobs.debug("Performing actions");
		List<Action> actions = o.getActions();
		if (actions == null) Mobs.debug("No actions to perform!");
		else for (Action a : actions) perform_action(event, a, le, orig_event);
		Mobs.debug("------------------");
	}
	
	void perform_action(Mobs_event event, Action a, LivingEntity le, Event orig_event)
	{		
		switch (a.getAction_type())
		{
			case DROP_ITEM:
				drop_item(a, le);
				break;
			case GIVE_ITEM:
			case REMOVE_ITEM:
			case CLEAR_ITEMS:
				give_item(a, le);
				break;
			case DROP_EXP:
				drop_exp(a, le);
				break;
			case SET_EXP:
			case SET_LEVEL:
				give_exp(a, le);
				break;
			case PRESS_BUTTON:
				activate_mechanism(a, "button");
				break;
			case CLOSE_DOOR:
			case OPEN_DOOR:
			case TOGGLE_DOOR:
				activate_mechanism(a, "door");
				break;
			case CLOSE_GATE:
			case OPEN_GATE:
			case TOGGLE_GATE:
				activate_mechanism(a, "gate");
				break;
			case PULL_LEVER:
			case PUSH_LEVER:
			case TOGGLE_LEVER:
				activate_mechanism(a, "lever");
				break;
			case CLOSE_TRAPDOOR:
			case OPEN_TRAPDOOR:
			case TOGGLE_TRAPDOOR:
				activate_mechanism(a, "trapdoor");
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
				damage_player(a, le);
				break;
			case SET_TIME:
				change_time(a);
				break;
			case RAIN:
			case STORM:
			case SUN:
				change_weather(a);
				break;
			case SET_BLOCK:
			case DESTROY_BLOCK:
				change_block(a, le);
				break;	
			case SEND_MESSAGE:
			case BROADCAST:
			case LOG:
				send_message(a, le);
				break;
			case SPAWN_MOB:
				spawn_mob(a, le);
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
	}
	
	private void playEffect(Action a, LivingEntity le)
	{
		Location loc = Mobs.getInstance().getTarget_manager().getLocation_from_target(a.getTarget(), le);
		if (loc == null) return;
		
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
		loc.getWorld().playEffect(loc, effect, 10);
	}
	
	private void setProperty(Action a, LivingEntity le)
	{
		Target target = a.getTarget();
		if (target != null && target.getTarget_type() == Mobs_const.PLAYER) le = Bukkit.getPlayer(target.getString_param(Mobs_const.NAME));
				
		if (le instanceof Animals) setAnimal_property(a, (Animals)le);
		if (le instanceof Monster) setMonster_property(a, (Monster)le);
		if (le instanceof EnderDragon) setEnder_dragon_property(a.getAction_type(), (EnderDragon)le);
		
		switch (a.getAction_type())
		{
			case SET_CAN_BURN_NO:
				putData(le, Mobs_const.NO_BURN);
				break;
			case SET_CAN_BURN_RANDOM:
				if (rng.nextBoolean()) putData(le, Mobs_const.NO_BURN); else removeData(le, Mobs_const.NO_BURN);
				break;
			case SET_CAN_BURN_YES:
				removeData(le, Mobs_const.NO_BURN);
				break;
			case TOGGLE_CAN_BURN:
				if (hasData(le, Mobs_const.NO_BURN)) removeData(le, Mobs_const.NO_BURN); else putData(le, Mobs_const.NO_BURN); 
				break;
								
			case SET_CAN_HEAL_NO:
				putData(le, Mobs_const.NO_HEAL);
				break;
			case SET_CAN_HEAL_RANDOM:
				if (rng.nextBoolean()) putData(le, Mobs_const.NO_HEAL); else removeData(le, Mobs_const.NO_HEAL);
				break;
			case SET_CAN_HEAL_YES:
				removeData(le, Mobs_const.NO_HEAL);
				break;
			case TOGGLE_CAN_HEAL:
				if (hasData(le, Mobs_const.NO_HEAL)) removeData(le, Mobs_const.NO_HEAL); else putData(le, Mobs_const.NO_HEAL);
				break;
								
			case SET_CAN_OVERHEAL_NO:
				putData(le, Mobs_const.NO_OVERHEAL);
				break;
			case SET_CAN_OVERHEAL_RANDOM:
				if (rng.nextBoolean()) putData(le, Mobs_const.NO_OVERHEAL); else removeData(le, Mobs_const.NO_OVERHEAL);
				break;
			case SET_CAN_OVERHEAL_YES:
				removeData(le, Mobs_const.NO_OVERHEAL);
				break;	
			case TOGGLE_CAN_OVERHEAL:
				if (hasData(le, Mobs_const.NO_OVERHEAL)) removeData(le, Mobs_const.NO_OVERHEAL); else putData(le, Mobs_const.NO_OVERHEAL);
				break;
			
			case SET_FIRE_EFFECT_NO:
				return;
			case SET_FRIENDLY_NO:
				return;
			case SET_FRIENDLY_RANDOM:
				return;
			case SET_FRIENDLY_YES:
				return;
			
			case SET_MOB_NAME:
				putData(le ,Mobs_const.NAME, a.getString_param(Mobs_const.NAME));
			break;
				//case SET_HP:
				//	q = number.getAbsolute_value(le2.getHealth());
				//	le2.setHealth(q);
				//	break;
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean hasData(LivingEntity le, Mobs_const param)
	{
		if (le.hasMetadata("mobs_data"))
		{
			return ((Map<String, Object>)le.getMetadata("mobs_data").get(0).value()).containsKey(param.toString());
		} else return false;
	}
	
	@SuppressWarnings("unchecked")
	private void putData(LivingEntity le, Mobs_const param)
	{
		Map<String, Object> data;
		if (le.hasMetadata("mobs_data"))
		{
			data = (Map<String, Object>)le.getMetadata("mobs_data").get(0).value();
			data.put(param.toString(), null);
		}
		else 
		{
			data = new HashMap<String, Object>();
			data.put(param.toString(), null);
			le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void putData(LivingEntity le, Mobs_const param, Object value)
	{
		Map<String, Object> data;
		if (le.hasMetadata("mobs_data"))
		{
			data = (Map<String, Object>)le.getMetadata("mobs_data").get(0).value();
			data.put(param.toString(), value);
		}
		else 
		{
			data = new HashMap<String, Object>();
			data.put(param.toString(), value);
			le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void removeData(LivingEntity le, Mobs_const param)
	{
		if (le.hasMetadata("mobs_data"))
		{
			((Map<String, Object>)le.getMetadata("mobs_data").get(0).value()).remove(param.toString());
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
					((Tameable)animal).setOwner(Bukkit.getPlayer(a.getString_param(Mobs_const.NAME)));
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
						putData(animal, Mobs_const.NO_SHEARED);
						return;
					case SET_CAN_BE_SHEARED_RANDOM:
						if (rng.nextBoolean()) putData(animal, Mobs_const.NO_SHEARED); else removeData(animal, Mobs_const.NO_SHEARED);
						return;
					case SET_CAN_BE_SHEARED_YES:
						removeData(animal, Mobs_const.NO_SHEARED);
						return;
					case TOGGLE_CAN_BE_SHEARED:
						if (hasData(animal, Mobs_const.NO_SHEARED)) removeData(animal, Mobs_const.NO_SHEARED); else putData(animal ,Mobs_const.NO_SHEARED);
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
				putData(monster, Mobs_const.FRIENDLY);
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
				putData(creeper, Mobs_const.NO_EVOLVE);
				return;
			case SET_CAN_EVOLVE_RANDOM:
				if (rng.nextBoolean()) putData(creeper, Mobs_const.NO_EVOLVE); else removeData(creeper, Mobs_const.NO_EVOLVE);
				return;
			case SET_CAN_EVOLVE_YES:
				removeData(creeper, Mobs_const.NO_EVOLVE);
				return;
			case TOGGLE_CAN_EVOLVE:
				if (hasData(creeper, Mobs_const.NO_EVOLVE)) removeData(creeper, Mobs_const.NO_EVOLVE); else putData(creeper, Mobs_const.NO_EVOLVE);
				return;
				
			case SET_FIERY_EXPLOSION_NO:
				removeData(creeper, Mobs_const.FIERY_EXPLOSION);
				return;
			case SET_FIERY_EXPLOSION_RANDOM:
				if (rng.nextBoolean()) putData(creeper, Mobs_const.FIERY_EXPLOSION); else removeData(creeper, Mobs_const.FIERY_EXPLOSION);
				return;
			case SET_FIERY_EXPLOSION_YES:
				putData(creeper, Mobs_const.FIERY_EXPLOSION);
				return;	
			case TOGGLE_FIERY_EXPLOSION:
				if (hasData(creeper, Mobs_const.FIERY_EXPLOSION)) removeData(creeper, Mobs_const.FIERY_EXPLOSION); else putData(creeper, Mobs_const.FIERY_EXPLOSION);
				return;
				
			case SET_EXPLOSION_SIZE:
				putData(creeper ,Mobs_const.EXPLOSION_SIZE, a.getMobs_number().getAbsolute_value(0));
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
	
	private void setWolf_property(Mobs_action a, Wolf wolf)
	{
		switch (a)
		{
			case SET_CAN_BE_TAMED_NO:
				putData(wolf, Mobs_const.NO_TAMED);
				return;
			case SET_CAN_BE_TAMED_RANDOM:
				if (rng.nextBoolean()) putData(wolf, Mobs_const.NO_TAMED); else removeData(wolf, Mobs_const.NO_TAMED);
				return;
			case SET_CAN_BE_TAMED_YES:
				removeData(wolf, Mobs_const.NO_TAMED);
				return;
			case TOGGLE_CAN_BE_TAMED:
				if (hasData(wolf, Mobs_const.NO_TAMED)) removeData(wolf, Mobs_const.NO_TAMED); else putData(wolf, Mobs_const.NO_TAMED);
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
	
	private void setEnderman_property(Mobs_action a, Enderman enderman)
	{
		switch (a)
		{
			case SET_CAN_MOVE_BLOCKS_NO:
				putData(enderman, Mobs_const.NO_MOVE_BLOCKS);
				return;
			case SET_CAN_MOVE_BLOCKS_RANDOM:
				if (rng.nextBoolean()) putData(enderman, Mobs_const.NO_MOVE_BLOCKS); else removeData(enderman, Mobs_const.NO_MOVE_BLOCKS);
				return;
			case SET_CAN_MOVE_BLOCKS_YES:
				removeData(enderman, Mobs_const.NO_MOVE_BLOCKS);
				return;	
			case TOGGLE_CAN_MOVE_BLOCKS:
				if (hasData(enderman, Mobs_const.NO_MOVE_BLOCKS)) removeData(enderman, Mobs_const.NO_MOVE_BLOCKS); else putData(enderman, Mobs_const.NO_MOVE_BLOCKS);
				return;
				
			case SET_CAN_TELEPORT_NO:
				putData(enderman, Mobs_const.NO_TELEPORT);
				return;
			case SET_CAN_TELEPORT_RANDOM:
				if (rng.nextBoolean()) putData(enderman, Mobs_const.NO_TELEPORT); else removeData(enderman, Mobs_const.NO_TELEPORT);
				return;
			case SET_CAN_TELEPORT_YES:
				removeData(enderman, Mobs_const.NO_TELEPORT);
				return;	
			case TOGGLE_CAN_TELEPORT:
				if (hasData(enderman, Mobs_const.NO_TELEPORT)) removeData(enderman, Mobs_const.NO_TELEPORT); else putData(enderman, Mobs_const.NO_TELEPORT);
				return;	
		}
		
		switch (a)
		{
		
		}
	}
	
	private void setEnder_dragon_property(Mobs_action a, EnderDragon ender_dragon)
	{
		switch (a)
		{
			case SET_CAN_CREATE_PORTALS_NO:
				putData(ender_dragon, Mobs_const.NO_CREATE_PORTALS);
				return;
			case SET_CAN_CREATE_PORTALS_RANDOM:
				if (rng.nextBoolean()) putData(ender_dragon, Mobs_const.NO_CREATE_PORTALS); else removeData(ender_dragon, Mobs_const.NO_CREATE_PORTALS);
				return;
			case SET_CAN_CREATE_PORTALS_YES:
				removeData(ender_dragon, Mobs_const.NO_CREATE_PORTALS);
				return;	
			case TOGGLE_CAN_CREATE_PORTALS:
				if (hasData(ender_dragon, Mobs_const.NO_CREATE_PORTALS)) removeData(ender_dragon, Mobs_const.NO_CREATE_PORTALS); else putData(ender_dragon, Mobs_const.NO_CREATE_PORTALS);
				return;
				
			case SET_CAN_DESTROY_BLOCKS_NO:
				putData(ender_dragon, Mobs_const.NO_DESTROY_BLOCKS);
				return;
			case SET_CAN_DESTROY_BLOCKS_RANDOM:
				if (rng.nextBoolean()) putData(ender_dragon, Mobs_const.NO_DESTROY_BLOCKS); else removeData(ender_dragon, Mobs_const.NO_DESTROY_BLOCKS);
				return;
			case SET_CAN_DESTROY_BLOCKS_YES:
				removeData(ender_dragon, Mobs_const.NO_DESTROY_BLOCKS);
				return;	
			case TOGGLE_CAN_DESTROY_BLOCKS:
				if (hasData(ender_dragon, Mobs_const.NO_DESTROY_BLOCKS)) removeData(ender_dragon, Mobs_const.NO_DESTROY_BLOCKS); else putData(ender_dragon, Mobs_const.NO_DESTROY_BLOCKS);
				return;
		}		
		
		switch (a)
		{
		
		}
	}
	
	private void setPig_zombie_property(Mobs_action a, PigZombie pig_zombie)
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
	
	private void setOcelot_property(Mobs_action a, Ocelot ocelot)
	{
		switch (a)
		{
			case SET_CAN_BE_TAMED_NO:
				putData(ocelot, Mobs_const.NO_TAMED);
				return;
			case SET_CAN_BE_TAMED_RANDOM:
				if (rng.nextBoolean()) putData(ocelot, Mobs_const.NO_TAMED); else removeData(ocelot, Mobs_const.NO_TAMED);
				return;
			case SET_CAN_BE_TAMED_YES:
				removeData(ocelot, Mobs_const.NO_TAMED);
				return;
			case TOGGLE_CAN_BE_TAMED:
				if (hasData(ocelot, Mobs_const.NO_TAMED)) removeData(ocelot, Mobs_const.NO_TAMED); else putData(ocelot, Mobs_const.NO_TAMED);
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
		
	private void setPig_property(Mobs_action a, Pig pig)
	{
		switch (a)
		{
			case SET_CAN_BE_SADDLED_NO:
				putData(pig, Mobs_const.NO_SADDLED);
				return;
			case SET_CAN_BE_SADDLED_RANDOM:
				if (rng.nextBoolean()) putData(pig, Mobs_const.NO_SADDLED); else removeData(pig, Mobs_const.NO_SADDLED);
				return;
			case SET_CAN_BE_SADDLED_YES:
				removeData(pig, Mobs_const.NO_SADDLED);
				return;
			case TOGGLE_CAN_BE_SADDLED:
				if (hasData(pig, Mobs_const.NO_SADDLED)) removeData(pig, Mobs_const.NO_SADDLED); else putData(pig, Mobs_const.NO_SADDLED);
				return;
				
			case SET_CAN_EVOLVE_NO:
				putData(pig, Mobs_const.NO_EVOLVE);
				return;
			case SET_CAN_EVOLVE_RANDOM:
				if (rng.nextBoolean()) putData(pig, Mobs_const.NO_EVOLVE); else removeData(pig, Mobs_const.NO_EVOLVE);
				return;
			case SET_CAN_EVOLVE_YES:
				removeData(pig, Mobs_const.NO_EVOLVE);
				return;
			case TOGGLE_CAN_EVOLVE:
				if (hasData(pig, Mobs_const.NO_EVOLVE)) removeData(pig, Mobs_const.NO_EVOLVE); else putData(pig, Mobs_const.NO_EVOLVE);
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
	
	private void setSheep_property(Mobs_action a, Sheep sheep)
	{
		switch (a)
		{
			case SET_CAN_BE_DYED_NO:
				putData(sheep ,Mobs_const.NO_DYED);
				return;
			case SET_CAN_BE_DYED_RANDOM:
				if (rng.nextBoolean()) putData(sheep ,Mobs_const.NO_DYED); else removeData(sheep ,Mobs_const.NO_DYED);
				return;
			case SET_CAN_BE_DYED_YES:
				removeData(sheep ,Mobs_const.NO_DYED);
				return;
			case TOGGLE_CAN_BE_DYED:
				if (hasData(sheep ,Mobs_const.NO_DYED)) removeData(sheep ,Mobs_const.NO_DYED); else putData(sheep ,Mobs_const.NO_DYED);
				return;
				
			case SET_CAN_BE_SHEARED_NO:
				putData(sheep ,Mobs_const.NO_SHEARED);
				return;
			case SET_CAN_BE_SHEARED_RANDOM:
				if (rng.nextBoolean()) putData(sheep ,Mobs_const.NO_SHEARED); else removeData(sheep ,Mobs_const.NO_SHEARED);
				return;
			case SET_CAN_BE_SHEARED_YES:
				removeData(sheep ,Mobs_const.NO_SHEARED);
				return;
			case TOGGLE_CAN_BE_SHEARED:
				if (hasData(sheep ,Mobs_const.NO_SHEARED)) removeData(sheep ,Mobs_const.NO_SHEARED); else putData(sheep ,Mobs_const.NO_SHEARED);
				return;
				
			case SET_CAN_GRAZE_NO:
				putData(sheep ,Mobs_const.NO_GRAZE);
				return;
			case SET_CAN_GRAZE_RANDOM:
				if (rng.nextBoolean()) putData(sheep ,Mobs_const.NO_GRAZE); else removeData(sheep ,Mobs_const.NO_GRAZE);
				return;
			case SET_CAN_GRAZE_YES:
				removeData(sheep ,Mobs_const.NO_GRAZE);
				return;
			case TOGGLE_CAN_GRAZE:
				if (hasData(sheep ,Mobs_const.NO_GRAZE)) removeData(sheep ,Mobs_const.NO_GRAZE); else putData(sheep ,Mobs_const.NO_GRAZE);
				return;
				
			case SET_CAN_GROW_WOOL_NO:
				putData(sheep ,Mobs_const.NO_GROW_WOOL);
				return;
			case SET_CAN_GROW_WOOL_RANDOM:
				if (rng.nextBoolean()) putData(sheep ,Mobs_const.NO_GROW_WOOL); else removeData(sheep ,Mobs_const.NO_GROW_WOOL);
				return;
			case SET_CAN_GROW_WOOL_YES:
				removeData(sheep ,Mobs_const.NO_GROW_WOOL);
				return;
			case TOGGLE_CAN_GROW_WOOL:
				if (hasData(sheep ,Mobs_const.NO_GROW_WOOL)) removeData(sheep ,Mobs_const.NO_GROW_WOOL); else putData(sheep ,Mobs_const.NO_GROW_WOOL);
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
	
	private void clear_drops(Action a, LivingEntity le, Mobs_event event, Event orig_event)
	{
		if (event == Mobs_event.DIES)
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
		else putData(le, Mobs_const.valueOf(a.getAction_type().toString()));
		Mobs.debug(a.getAction_type().toString());
	}
		
	private void spawn_mob(Action a, LivingEntity le)
	{
		String[] mob = ((String)a.getAlternative(Mobs_const.MOB)).split(":");
		int amount = a.hasParam(Mobs_const.NUMBER) ? a.getMobs_number().getAbsolute_value(1) : 1;
		for (int i = 0; i < amount; i++)
		{
			Location loc = Mobs.getInstance().getTarget_manager().getLocation_from_target(a.getTarget(), le);
			Mobs.getInstance().setMob_name(mob);
			loc.getWorld().spawnEntity(loc, EntityType.fromName(mob[0]));
			Mobs.debug("SPAWN_MOB, " + get_string_from_loc(loc) + ", " + mob[0]);
		}
	}
		
	private void send_message(Action a, LivingEntity le)
	{
		String s = (String)a.getAlternative(Mobs_const.MESSAGE);
		while (s.contains("^")) s = replace_constants(s, le);
			
		if (a.getAction_type() == Mobs_action.SEND_MESSAGE)
		{
			List<Player> players = Mobs.getInstance().getTarget_manager().getPlayers(a.getTarget(), le);
			if (players == null) return;
			for (Player p : players)
			{
				p.sendMessage(s);
				Mobs.debug("SEND_MESSAGE, " + ((Player)p).getName() + ", MESSAGE = " + s);
			}
		}
		else if (a.getAction_type() == Mobs_action.BROADCAST)
		{
			Bukkit.getServer().broadcastMessage(s);
			Mobs.debug("BROADCAST, MESSAGE = " + s);
		}
		else Mobs.log(s);
	}
	
	private void change_block(Action a, LivingEntity le)
	{
		Location loc = Mobs.getInstance().getTarget_manager().getLocation_from_target(a.getTarget(), le);
		if (loc == null) return;
			
		if (a.getAction_type() == Mobs_action.SET_BLOCK)
		{
			Item_drop drop = (Item_drop)a.getAlternative(Mobs_const.ITEM);
			if (drop == null) return;
			loc.getBlock().setTypeIdAndData(drop.getItem_id(), (byte) drop.getItem_data(), false);
			Mobs.debug("SET_BLOCK, " + get_string_from_loc(loc) + ", ITEM = " + 
					drop.getItem_id() + ":" + drop.getItem_data());
		}
		else
		{
			loc.getBlock().breakNaturally();
			Mobs.debug("DESTROY_BLOCK, " + get_string_from_loc(loc));
		}
	}
	
	private BlockState get_mechanism(Target t)
	{			
		World w = Mobs.getInstance().getTarget_manager().getWorld(t);
		if (w == null) return null;
		int[] block_loc = t.getInt_array(Mobs_const.BLOCK);
		Block block = w.getBlockAt(block_loc[0], block_loc[1], block_loc[2]);
		return block.getState();
	}
	
	private void strike_with_lightning(Action a, LivingEntity le)
	{
		Location loc = Mobs.getInstance().getTarget_manager().getLocation_from_target(a.getTarget(), le);
		
		World w = loc.getWorld();
		if (a.getAction_type() == Mobs_action.LIGHTNING_EFFECT) w.strikeLightningEffect(loc);
		else w.strikeLightning(loc);
		Mobs.debug(a.getAction_type().toString() + ", " + get_string_from_loc(loc));
		return;
	}
	
	private void cause_explosion(Action a, LivingEntity le)
	{
		Mobs_number number = a.getMobs_number();

		Location loc = Mobs.getInstance().getTarget_manager().getLocation_from_target(a.getTarget(), le);
		
		int p = number.getAbsolute_value(0);
		loc.getWorld().createExplosion(loc, p, a.getAction_type() == Mobs_action.FIERY_EXPLOSION);
		Mobs.debug(a.getAction_type().toString() + ", POWER = " + p + ", " + get_string_from_loc(loc));
	}
	
	private void damage_player(Action a, LivingEntity le)
	{
		List<Player> players = Mobs.getInstance().getTarget_manager().getPlayers(a.getTarget(), le);
		if (players == null) return;
		for (Player p : players)
		{
			int q = 0;
			if (a.getAction_type() == Mobs_action.KILL)
			{
				p.setHealth(0);
				Mobs.debug("KILL, " + p.getName());
			}
			else
			{
				Mobs_number number = a.getMobs_number();
				q = number.getAbsolute_value(0);
				p.damage(q);
				Mobs.debug("DAMAGE, " + p.getName() + ", AMOUNT = " + q);
			}
		}
	}
	
	private void change_weather(Action a)
	{
		World w = Mobs.getInstance().getTarget_manager().getWorld(a.getTarget());
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
		Mobs_number number = a.getMobs_number();
		
		if (number != null)
		{
			int i = number.getAbsolute_value(0);
			s2 += ", DURATION = " + i + " seconds";
			w.setWeatherDuration(i * 20);
		}
		else s2 += ", DEFAULT DURATION";
		Mobs.debug(s2 + ", " + w.getName());
	}
	
	private void change_time(Action a)
	{
		World w = Mobs.getInstance().getTarget_manager().getWorld(a.getTarget());
		Mobs.log(a.getTarget().getString_param(Mobs_const.NAME));
		if (w == null) return;
		
		Mobs_number number = a.getMobs_number();
		
		long time = number.getAbsolute_value((int) w.getTime()) % 24000;
		if (time < 0) time += 24000;
		//if (s.startsWith("+")) time = (Long.parseLong(s.substring(1)) + w.getTime()) % 24000;
		//else if (s.startsWith("-"))
		//{ 
		//	time = (w.getTime() - Long.parseLong(s.substring(1))) % 24000;
		//	if (time < 0) time += 24000;
			
		//}
		//else time = Long.parseLong(s);
		w.setTime(time);
		Mobs.debug("SET_TIME " + Long.toString(time) + ", " + w.getName());
	}
	
	private void drop_item(Action a, LivingEntity le)
	{	
		Item_drop drop = (Item_drop)a.getAlternative(Mobs_const.ITEM);
		ItemStack is = new ItemStack(drop.getItem_id(), drop.getAmount(), drop.getItem_data());
		
		Location loc = Mobs.getInstance().getTarget_manager().getLocation_from_target(a.getTarget(), le);
		loc.getWorld().dropItem(loc, is);
		Mobs.debug("DROP_ITEM, " + get_string_from_loc(loc) + ", ITEM = " + 
					drop.getItem_id() + ":" + drop.getItem_data());
	}
	
	private void give_item(Action a, LivingEntity le)
	{
		List<Player> players = Mobs.getInstance().getTarget_manager().getPlayers(a.getTarget(), le);
		if (players == null) return;
		for (Player p : players)
		{		
			if (a.getAction_type() == Mobs_action.CLEAR_ITEMS)
			{
				p.getInventory().clear();
				Mobs.debug("CLEAR_ITEMS, " + p.getName());
				return;
			}	
			
			Item_drop drop = (Item_drop)a.getAlternative(Mobs_const.ITEM);
			if (drop == null) return;
			
			ItemStack stack = new ItemStack(drop.getItem_id(), drop.getAmount(), drop.getItem_data());
			
			if (a.getAction_type() == Mobs_action.GIVE_ITEM)
			{
				p.getInventory().addItem(stack);
			}
			else
			{
				if (a.hasParam(Mobs_const.MATCH_DATA) && a.hasParam(Mobs_const.MATCH_ENCHANTMENTS) && a.hasParam(Mobs_const.MATCH_AMOUNT) == false) p.getInventory().remove(drop.getItem_id());
				else
				for (ItemStack is : p.getInventory().getContents())
				{
					if (is == null) continue;
					if (is.getType() != stack.getType()) continue;
					if (a.hasParam(Mobs_const.MATCH_DATA) && is.getData().getData() != stack.getData().getData()) continue;
					if (a.hasParam(Mobs_const.MATCH_AMOUNT) && is.getAmount() != stack.getAmount()) continue;
					p.getInventory().remove(is);
				}
			}	
			Mobs.debug(a.getAction_type().toString() + ", " + p.getName() + ", ITEM = " + 
					drop.getItem_id() + ":" + drop.getItem_data());	
		}
	}
	
	private void drop_exp(Action a, LivingEntity le)
	{
		Location loc = Mobs.getInstance().getTarget_manager().getLocation_from_target(a.getTarget(), le);
		Mobs_number number = a.getMobs_number();
		int q = number.getAbsolute_value(0);	
		
		ExperienceOrb orb = (ExperienceOrb)loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
		orb.setExperience(q);
		Mobs.debug("DROP_EXP, " + get_string_from_loc(loc) + ", AMOUNT = " + q);
	}
	
	private void give_exp(Action a, LivingEntity le)
	{
		List<Player> players = Mobs.getInstance().getTarget_manager().getPlayers(a.getTarget(), le);
		if (players == null) return;
		for (Player p : players)
		{
			Mobs_number number = a.getMobs_number();
			
			if (a.getAction_type() == Mobs_action.SET_EXP)
			{
				int q = number.getAbsolute_value(p.getTotalExperience());
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
				int q = number.getAbsolute_value(p.getLevel());
				p.setLevel(q);	
				Mobs.debug("SET_LEVEL, " + p.getName() + ", AMOUNT = " + q);
			}
		}
	}
	
	private void activate_mechanism(Action a, String type)
	{
		Target target = a.getTarget();
		if (target == null) return;
		
		BlockState bs = get_mechanism(target);
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