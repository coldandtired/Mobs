package me.coldandtired.mobs.listeners;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.extra_events.*;
import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Mobs;
import me.coldandtired.mobs.elements.Config_event;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.w3c.dom.Element;

public class Event_listener implements Listener
{
	private String mob_name = null;
	private String spawn_reason = null;
	private Config_event approached;
	private Config_event blocks;
	private Config_event burns;
	private Config_event changes_block;
	private Config_event creates_portal;
	private Config_event damaged;
	private Config_event dawn;
	private Config_event dies;
	private Config_event dusk;
	private Config_event dyed;
	private Config_event evolves;
	private Config_event explodes;
	private Config_event grows_wool;
	private Config_event heals;
	private Config_event hit;
	private Config_event hour_change;
	private Config_event joins;
	private Config_event leaves;
	private Config_event midday;
	private Config_event midnight;
	private Config_event near;
	private Config_event picks_up_item;
	private Config_event repeating_outcomes;
	private Config_event sheared;
	private Config_event spawns;
	private Config_event splits;
	private Config_event tamed;
	private Config_event targets;
	private Config_event teleports;
	private boolean disabled_timer = false;	
	
	public Event_listener(Element element) throws XPathExpressionException
	{
		approached = Config_event.get(element, MEvent.APPROACHED);
		blocks = Config_event.get(element, MEvent.BLOCKS);
		burns = Config_event.get(element, MEvent.BURNS);
		changes_block = Config_event.get(element, MEvent.CHANGES_BLOCK);
		creates_portal = Config_event.get(element, MEvent.CREATES_PORTAL);
		damaged = Config_event.get(element, MEvent.DAMAGED);
		dawn = Config_event.get(element, MEvent.DAWN);
		dies = Config_event.get(element, MEvent.DIES);
		dusk = Config_event.get(element, MEvent.DUSK);
		dyed = Config_event.get(element, MEvent.DYED);
		evolves = Config_event.get(element, MEvent.EVOLVES);
		explodes = Config_event.get(element, MEvent.EXPLODES);
		grows_wool = Config_event.get(element, MEvent.GROWS_WOOL);
		heals = Config_event.get(element, MEvent.HEALS);
		hit = Config_event.get(element, MEvent.HIT);
		hour_change = Config_event.get(element, MEvent.HOUR_CHANGE);
		joins = Config_event.get(element, MEvent.JOINS);
		leaves = Config_event.get(element, MEvent.LEAVES);
		midday = Config_event.get(element, MEvent.MIDDAY);
		midnight = Config_event.get(element, MEvent.MIDNIGHT);
		near = Config_event.get(element, MEvent.NEAR);
		picks_up_item = Config_event.get(element, MEvent.PICKS_UP_ITEM);
		repeating_outcomes = Config_event.get(element, MEvent.REPEATING_OUTCOMES);
		sheared = Config_event.get(element, MEvent.SHEARED);
		spawns = Config_event.get(element, MEvent.SPAWNS);
		splits = Config_event.get(element, MEvent.SPLITS);
		tamed = Config_event.get(element, MEvent.TAMED);
		targets = Config_event.get(element, MEvent.TARGETS);
		teleports = Config_event.get(element, MEvent.TELEPORTS);
	}	
	
	@EventHandler
	public void approached(PlayerApproachLivingEntityEvent event)
	{
		if (approached != null) approached.performActions(event.getEntity(), event);
	}
	
	@EventHandler
	public void blocks(LivingEntityBlockEvent event)
	{
		if (blocks != null) blocks.performActions(event.getEntity(), event);
	}
	
	@EventHandler
	public void burns(EntityCombustEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (burns != null) burns.performActions(le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_BURN)) event.setCancelled(true);
	}
	
	@EventHandler
	public void changes_block(EntityChangeBlockEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (changes_block != null) changes_block.performActions(le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_MOVE_BLOCKS) || Data.hasData(le, MParam.NO_GRAZE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_creates_portal(EntityCreatePortalEvent event)
	{
		if (creates_portal != null) creates_portal.performActions(event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_CREATE_PORTALS)) event.setCancelled(true);
	}
	
	@EventHandler
	public void damaged(LivingEntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		if (event.getDamage() == 1000) return;
		LivingEntity le = (LivingEntity)event.getEntity();

		if (damaged != null) damaged.performActions(le, event);
		if (event.isCancelled())
		{
			event.setCancelled(true);
			return;
		}
		
		int damage = event.getDamage();		
			
		switch (event.getCause())
		{
			case BLOCK_EXPLOSION:
				damage = Data.adjustInt(le, MParam.BLOCK_EXPLOSION_DAMAGE, damage);
				break;
			case CONTACT:
				damage = Data.adjustInt(le, MParam.CONTACT_DAMAGE, damage);
				break;
			case CUSTOM:
				damage = Data.adjustInt(le, MParam.CUSTOM_DAMAGE, damage);
				break;
			case DROWNING:
				damage = Data.adjustInt(le, MParam.DROWNING_DAMAGE, damage);
				break;
			case ENTITY_ATTACK:
				LivingEntity damager = event.getAttacker();
				if (damager != null) damage = Data.adjustInt(damager, MParam.ATTACK, damage);
				
				damage = Data.adjustInt(le, MParam.ATTACK_DAMAGE, damage);
				break;
			case ENTITY_EXPLOSION:
				damage = Data.adjustInt(le, MParam.ENTITY_EXPLOSION_DAMAGE, damage);
				break;
			case FALL:
				damage = Data.adjustInt(le, MParam.FALL_DAMAGE, damage);
				break;
			case FIRE:
				damage = Data.adjustInt(le, MParam.FIRE_DAMAGE, damage);
				break;
			case FIRE_TICK:
				damage = Data.adjustInt(le, MParam.FIRE_TICK_DAMAGE, damage);
				break;
			case LAVA:
				damage = Data.adjustInt(le, MParam.LAVA_DAMAGE, damage);
				break;
			case LIGHTNING:
				damage = Data.adjustInt(le, MParam.LIGHTNING_DAMAGE, damage);
				break;
			case MAGIC:
				damage = Data.adjustInt(le, MParam.MAGIC_DAMAGE, damage);
				break;
			case MELTING:
				damage = Data.adjustInt(le, MParam.MELTING_DAMAGE, damage);
				break;
			case POISON:
				damage = Data.adjustInt(le, MParam.POISON_DAMAGE, damage);
				break;
			case PROJECTILE:
				damage = Data.adjustInt(le, MParam.PROJECTILE_DAMAGE, damage);
				break;
			case STARVATION:
				damage = Data.adjustInt(le, MParam.STARVATION_DAMAGE, damage);
				break;
			case SUFFOCATION:
				damage = Data.adjustInt(le, MParam.SUFFOCATION_DAMAGE, damage);
				break;
			case SUICIDE:
				damage = Data.adjustInt(le, MParam.SUICIDE_DAMAGE, damage);
				break;
			case VOID:
				damage = Data.adjustInt(le, MParam.VOID_DAMAGE, damage);
				break;
		}
		event.setDamage(damage);
		Integer i = (Integer)Data.getData(le, MParam.HP);
		if (i != null)
		{
			i -= damage;
			int max_hp = (Integer)Data.getData(le, MParam.MAX_HP);
			if (event.getEntity() instanceof Player) le.setHealth((int) (20.0 * (i * 1.0 / max_hp)));
			else event.setDamage(0);
			if (i <= 0)
			{
				le.setHealth(0);
				return;
			}
			Data.putData(le, MParam.HP, i);
		}		
	}
	
	@EventHandler
	public void dawn(DawnEvent event)
	{
		if (dawn != null) dawn.performActions(null, event);
	}
	
	@EventHandler
	public void dies(EntityDeathEvent event)
	{
		if (dies != null) dies.performActions(event.getEntity(), event);
		if (Data.hasData(event.getEntity(), MParam.CLEAR_DROPS)) event.getDrops().clear();
		if (Data.hasData(event.getEntity(), MParam.CLEAR_EXP)) event.setDroppedExp(0);
	}
	
	@EventHandler
	public void player_dies(PlayerDeathEvent event)
	{
		if (dies != null) dies.performActions(event.getEntity(), event);
		if (Data.hasData(event.getEntity(), MParam.CLEAR_DROPS)) event.getDrops().clear();
		if (Data.hasData(event.getEntity(), MParam.CLEAR_EXP)) event.setDroppedExp(0);
	}
	
	@EventHandler
	public void dusk(DuskEvent event)
	{
		if (dusk != null) dusk.performActions(null, event);
	}
	
	@EventHandler
	public void dyed(SheepDyeWoolEvent event)
	{
		if (dyed != null) dyed.performActions(event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_DYED)) event.setCancelled(true);
	}
	
	@EventHandler
	public void creeper_evolves(CreeperPowerEvent event)
	{
		if (evolves != null) evolves.performActions(event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void pig_evolves(PigZapEvent event)
	{
		if (evolves != null) evolves.performActions(event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void explodes(EntityExplodeEvent event)
	{
		Entity entity = event.getEntity();		
		if (entity == null) return;		
		if (entity instanceof Fireball) entity = ((Fireball)entity).getShooter();
		if (!(entity instanceof LivingEntity)) return;

		LivingEntity le = (LivingEntity)entity;
		
		if (explodes != null) explodes.performActions(le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.FRIENDLY)) event.setCancelled(true);
		
		if (event.isCancelled()) return;
		else
		{			
			if (Data.hasData(le, MParam.NO_DESTROY_BLOCKS)) event.blockList().clear();
			else
			{
				if (Data.hasData(le, MParam.EXPLOSION_SIZE))
				{
					Integer size = (Integer)Data.getData(le, MParam.EXPLOSION_SIZE);
					if (size == null) return;
					
					event.setCancelled(true);
					Location loc = event.getLocation();
					if (Data.hasData(le, MParam.FIERY_EXPLOSION)) loc.getWorld().createExplosion(loc, size, true);
					else loc.getWorld().createExplosion(loc, size);
				}
			}
		}
	}
	
	@EventHandler
	public void grows_wool(SheepRegrowWoolEvent event)
	{
		if (grows_wool != null) grows_wool.performActions(event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_GROW_WOOL)) event.setCancelled(true);
	}
	
	@EventHandler
	public void heals(EntityRegainHealthEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();

		if (heals != null) heals.performActions(le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_HEAL))
		{
			event.setCancelled(true);
			return;
		}
		
		if (!Data.hasData(le, MParam.HP)) return;
		
		int hp = (Integer)Data.getData(le, MParam.HP) + event.getAmount();
		int max_hp = (Integer)Data.getData(le, MParam.MAX_HP);
		if (hp > max_hp && Data.hasData(le, MParam.NO_OVERHEAL)) hp = max_hp;
		Data.putData(le, MParam.HP, hp);
	}	
	
	@EventHandler
	public void hit(EntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (hit != null) hit.performActions(le, event);
	}
	
	@EventHandler
	public void hour_change(HourChangeEvent event)
	{
		if (hour_change != null) hour_change.performActions(null, event);
	}
	
	@EventHandler
	public void joins(PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, MParam.NAME, p.getName());
		if (joins != null) joins.performActions(p, event);
	}
	
	@EventHandler
	public void leaves(PlayerLeaveLivingEntityEvent event)
	{
		if (leaves != null) leaves.performActions(event.getEntity(), event);
	}
	
	@EventHandler
	public void midday(MiddayEvent event)
	{
		if (midday != null) midday.performActions(null, event);
	}
	
	@EventHandler
	public void midnight(MidnightEvent event)
	{
		if (midnight != null) midnight.performActions(null, event);
	}
	
	@EventHandler
	public void near(PlayerNearLivingEntityEvent event)
	{
		if (near != null) near.performActions(event.getEntity(), event);
	}
	
	public void picks_up_item(PlayerPickupItemEvent event)
	{
		if (picks_up_item != null) picks_up_item.performActions(event.getPlayer(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getPlayer(), MParam.NO_PICK_UP_ITEMS)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_sheared(PlayerShearEntityEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (sheared != null) sheared.performActions(le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_SHEARED)) event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void spawns(CreatureSpawnEvent event)
	{
		LivingEntity le = event.getEntity();
		if (spawn_reason == null) spawn_reason = event.getSpawnReason().toString();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(MParam.SPAWN_REASON.toString(), spawn_reason);
		if (mob_name != null) data.put(MParam.NAME.toString(), mob_name);
		le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		if (spawns != null) spawns.performActions(le, event);
		mob_name = null;
		spawn_reason = null;
	}
	
	@EventHandler
	public void player_respawns(PlayerRespawnEvent event)
	{
		Player p = event.getPlayer();
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, MParam.NAME, p.getName());
		if (spawns != null) spawns.performActions(p, event);	
	}
	
	@EventHandler
	public void mob_splits(SlimeSplitEvent event)
	{
		if (splits != null) splits.performActions(event.getEntity(), event);
		Integer i = (Integer)Data.getData(event.getEntity(), MParam.SPLIT_INTO);
		if (i == null) return;
		if (i == 0) event.setCancelled(true); else event.setCount(i);
	}
	
	@EventHandler
	public void mob_tamed(EntityTameEvent event)
	{
		if (tamed != null) tamed.performActions(event.getEntity(), event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_TAMED)) event.setCancelled(true);
	}
	
	@EventHandler
	public void targets(EntityTargetLivingEntityEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (targets != null) targets.performActions(le, event);
		if (event.isCancelled()) return;
		//targetd event!
		if (Data.hasData(le, MParam.FRIENDLY)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_teleports(EntityTeleportEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (teleports != null) teleports.performActions(le, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_TELEPORT)) event.setCancelled(true);
	}
	
	@EventHandler
	public void check_max_life(SecondTickEvent event)
	{
		for (World w : Bukkit.getWorlds())
		{
			if (Data.hasData(w, MParam.IGNORED_WORLD)) continue;
			
			for (LivingEntity le : w.getEntitiesByClass(LivingEntity.class))
			{
				if (le instanceof Player) continue;
				
				Integer life = (Integer)Data.getData(le, MParam.MAX_LIFE);
				if (life == null) continue;
				life--;
				if (life < 0) le.remove();	
				else Data.putData(le, MParam.MAX_LIFE, life);
			}
		}
		
		if (!disabled_timer && repeating_outcomes != null) repeating_outcomes.performActions(null, event);
	}
	
 	public void setMob_name(String name)
	{
		spawn_reason = "SPAWNED";
		mob_name = name;
	}

 	public boolean adjustRepeating_outcomes(CommandSender sender, String[] args)
 	{
 		if (args.length == 0)
		{
			sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
			return true;
		}
		if (repeating_outcomes == null)
		{
			sender.sendMessage("There are no repeating outcomes!");
			sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
			return true;
		}
		if (args[0].equalsIgnoreCase("enable"))
		{
			if (args.length == 1)
			{
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					o.setEnabled(true);
				}
				sender.sendMessage("Enabled all repeating outcomes!");
				return true;
			}
			else if (args.length == 2)
			{
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					if (o.checkName(args[1]))
					{
						o.setEnabled(true);
						sender.sendMessage("Enabled repeating outcome " + args[1] + "!");
						return true;
					}
				}
			}
		}
		else if (args[0].equalsIgnoreCase("disable"))
		{
			if (args.length == 1)
			{
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					o.setEnabled(false);
				}
				sender.sendMessage("Disabled all repeating outcomes!");
				return true;
			}
			else if (args.length == 2)
			{
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					if (o.checkName(args[1]))
					{
						o.setEnabled(false);
						sender.sendMessage("Disabled repeating outcome " + args[1] + "!");
						return true;
					}
				}
			}
		}
		if (args[0].equalsIgnoreCase("activate"))
		{
			if (args.length == 1)
			{					
				sender.sendMessage("Activated all repeating outcomes!");
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					if (o.passedConditions_check(null, null, true)) o.performActions(null, MEvent.REPEATING_OUTCOMES, null);
				}
				return true;
			}
			else if (args.length == 2)
			{
				sender.sendMessage("Activated repeating outcome " + args[1] + "!");
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					if (o.checkName(args[1]) && o.passedConditions_check(null, null, true)) o.performActions(null, MEvent.REPEATING_OUTCOMES, null);
				}
				return true;
			}
		}
		else if (args[0].equalsIgnoreCase("force"))
		{
			if (args.length == 1)
			{					
				sender.sendMessage("Forced all repeating outcomes!");
				for (Outcome o : repeating_outcomes.getOutcomes()) o.performActions(null, MEvent.REPEATING_OUTCOMES, null);
				return true;
			}
			else if (args.length == 2)
			{
				sender.sendMessage("Forced repeating outcome " + args[1] + "!");
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					if (o.checkName(args[1])) o.performActions(null, MEvent.REPEATING_OUTCOMES, null);
				}
				return true;
			}
		}
		else if (args[0].equalsIgnoreCase("unpause"))
		{
			if (args.length == 1)
			{
				disabled_timer = false;
				sender.sendMessage("Repeating outcomes are now running!");
				return true;
			}
			return false;
		}
		else if (args[0].equalsIgnoreCase("pause"))
		{
			if (args.length == 1)
			{
				disabled_timer = true;
				sender.sendMessage("Repeating outcomes are now paused!");
				return true;
			}
			return false;
		}
		else if (args[0].equalsIgnoreCase("check"))
		{
			if (args.length == 1)
			{
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					String s = o.getName() == null ? "No name set" : o.getName();
					sender.sendMessage("Outcome " + s + " is " + (o.isEnabled() ? "enabled, " : "disabled, ")
							+ o.getInterval() + " second interval (" + o.getRemaining() + " seconds left)");
				}
				sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
				return true;
			}
			else if (args.length == 2)
			{
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					if (o.checkName(args[1]))
					{
						sender.sendMessage("Outcome " + o.getName() + " is " + (o.isEnabled() ? "enabled, " : "disabled, ")
								+ o.getInterval() + " second interval (" + o.getRemaining() + " seconds left)");
						sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
						return true;
					}
				}
			}
		}
		else if (args[0].equalsIgnoreCase("set_interval"))
		{
			if (args.length == 1) return false;
			int i = Integer.parseInt(args[1]);
			if (args.length == 2)
			{
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					o.setInterval(i);
				}
				sender.sendMessage("All outcome intervals set to " + i + "!");
				return true;
			}
			else if (args.length == 3)
			{
				for (Outcome o : repeating_outcomes.getOutcomes())
				{
					if (o.checkName(args[2]))
					{
						o.setInterval(i);
						sender.sendMessage("Outcome " + o.getName() + " interval set to " + i + "!");
						return true;
					}
				}
			}
		}
		return false;
 	}
}