package me.coldandtired.mobs.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.extra_events.*;
import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.Event_report;
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
	private Config_event enters_area;
	private Config_event evolves;
	private Config_event explodes;
	private Config_event grows_wool;
	private Config_event heals;
	private Config_event hit;
	private Config_event hit_by_projectile;
	private Config_event hour_change;
	private Config_event in_area;
	private Config_event joins;
	private Config_event leaves;
	private Config_event leaves_area;
	private Config_event midday;
	private Config_event midnight;
	private Config_event near;
	private Config_event night;
	private Config_event picks_up_item;
	private Config_event repeating;
	private Config_event sheared;
	private Config_event spawns;
	private Config_event splits;
	private Config_event tamed;
	private Config_event targets;
	private Config_event teleports;
	private boolean disabled_timer = false;	
	
	public Event_listener(Set<String> events_to_debug) throws XPathExpressionException
	{
		approached = Config_event.get(MEvent.APPROACHED, events_to_debug);
		blocks = Config_event.get(MEvent.BLOCKS, events_to_debug);
		burns = Config_event.get(MEvent.BURNS, events_to_debug);
		changes_block = Config_event.get(MEvent.CHANGES_BLOCK, events_to_debug);
		creates_portal = Config_event.get(MEvent.CREATES_PORTAL, events_to_debug);
		damaged = Config_event.get(MEvent.DAMAGED, events_to_debug);
		dawn = Config_event.get(MEvent.DAWN, events_to_debug);
		dies = Config_event.get(MEvent.DIES, events_to_debug);
		dusk = Config_event.get(MEvent.DUSK, events_to_debug);
		dyed = Config_event.get(MEvent.DYED, events_to_debug);
		enters_area = Config_event.get(MEvent.ENTERS_AREA, events_to_debug);
		evolves = Config_event.get(MEvent.EVOLVES, events_to_debug);
		explodes = Config_event.get(MEvent.EXPLODES, events_to_debug);
		grows_wool = Config_event.get(MEvent.GROWS_WOOL, events_to_debug);
		heals = Config_event.get(MEvent.HEALS, events_to_debug);
		hit = Config_event.get(MEvent.HIT, events_to_debug);
		hit_by_projectile = Config_event.get(MEvent.HIT_BY_PROJECTILE, events_to_debug);
		hour_change = Config_event.get(MEvent.HOUR_CHANGE, events_to_debug);
		in_area = Config_event.get(MEvent.IN_AREA, events_to_debug);
		joins = Config_event.get(MEvent.JOINS, events_to_debug);
		leaves = Config_event.get(MEvent.LEAVES, events_to_debug);
		leaves_area = Config_event.get(MEvent.LEAVES_AREA, events_to_debug);
		midday = Config_event.get(MEvent.MIDDAY, events_to_debug);
		midnight = Config_event.get(MEvent.MIDNIGHT, events_to_debug);
		near = Config_event.get(MEvent.NEAR, events_to_debug);
		night = Config_event.get(MEvent.NIGHT, events_to_debug);
		picks_up_item = Config_event.get(MEvent.PICKS_UP_ITEM, events_to_debug);
		repeating = Config_event.get(MEvent.REPEATING, events_to_debug);
		sheared = Config_event.get(MEvent.SHEARED, events_to_debug);
		spawns = Config_event.get(MEvent.SPAWNS, events_to_debug);
		splits = Config_event.get(MEvent.SPLITS, events_to_debug);
		tamed = Config_event.get(MEvent.TAMED, events_to_debug);
		targets = Config_event.get(MEvent.TARGETS, events_to_debug);
		teleports = Config_event.get(MEvent.TELEPORTS, events_to_debug);
	}	
	
	@EventHandler
	public void approached(PlayerApproachLivingEntityEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (approached != null) approached.performActions(event.getEntity(), null, event);
	}
	
	@EventHandler
	public void blocks(LivingEntityBlockEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (blocks != null) blocks.performActions(event.getEntity(), null, event);
	}
	
	@EventHandler
	public void burns(EntityCombustEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		if (ignoreWorld(le.getWorld())) return;
		
		if (burns != null) burns.performActions(le, null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_BURN)) event.setCancelled(true);
	}
	
	@EventHandler
	public void changes_block(EntityChangeBlockEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		if (ignoreWorld(le.getWorld())) return;
		
		if (changes_block != null) changes_block.performActions(le, null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_MOVE_BLOCKS) || Data.hasData(le, MParam.NO_GRAZE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_creates_portal(EntityCreatePortalEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (creates_portal != null) creates_portal.performActions(event.getEntity(), null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_CREATE_PORTALS)) event.setCancelled(true);
	}
	
	@EventHandler
	public void damaged(LivingEntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (event.getDamage() == 1000) return;
		LivingEntity le = (LivingEntity)event.getEntity();

		if (damaged != null) damaged.performActions(le, null, event);
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
	}
	
	@EventHandler
	public void dawn(DawnEvent event)
	{
		if (dawn != null) dawn.performActions(null, null, event);
	}
	
	@EventHandler
	public void dies(EntityDeathEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (dies != null) dies.performActions(event.getEntity(), null, event);
		if (Data.hasData(event.getEntity(), MParam.CLEAR_DROPS)) event.getDrops().clear();
		if (Data.hasData(event.getEntity(), MParam.CLEAR_EXP)) event.setDroppedExp(0);
	}
	
	@EventHandler
	public void player_dies(PlayerDeathEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (dies != null) dies.performActions(event.getEntity(), null, event);
		if (Data.hasData(event.getEntity(), MParam.CLEAR_DROPS)) event.getDrops().clear();
		if (Data.hasData(event.getEntity(), MParam.CLEAR_EXP)) event.setDroppedExp(0);
	}
	
	@EventHandler
	public void dusk(DuskEvent event)
	{
		if (dusk != null) dusk.performActions(null, null, event);
	}
	
	@EventHandler
	public void dyed(SheepDyeWoolEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (dyed != null) dyed.performActions(event.getEntity(), null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_DYED)) event.setCancelled(true);
	}
	
	@EventHandler
	public void creeper_evolves(CreeperPowerEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (evolves != null) evolves.performActions(event.getEntity(), null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void pig_evolves(PigZapEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (evolves != null) evolves.performActions(event.getEntity(), null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void enters_area(PlayerEnterAreaEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		if (enters_area != null) enters_area.performActions(event.getPlayer(), null, event);
	}
	
	@EventHandler
	public void explodes(EntityExplodeEvent event)
	{
		Entity entity = event.getEntity();		
		if (entity == null) return;	
		if (ignoreWorld(entity.getWorld())) return;
		
		if (entity instanceof Fireball) entity = ((Fireball)entity).getShooter();
		if (!(entity instanceof LivingEntity)) return;

		LivingEntity le = (LivingEntity)entity;
		
		if (explodes != null) explodes.performActions(le, null, event);
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
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (grows_wool != null) grows_wool.performActions(event.getEntity(), null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_GROW_WOOL)) event.setCancelled(true);
	}
	
	@EventHandler
	public void heals(EntityRegainHealthEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		if (ignoreWorld(le.getWorld())) return;

		if (heals != null) heals.performActions(le, null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_HEAL))
		{
			event.setCancelled(true);
			return;
		}
	}	
	
	@EventHandler
	public void hit(EntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		if (ignoreWorld(le.getWorld())) return;
		
		if (hit != null) hit.performActions(le, null, event);
	}
	
	@EventHandler
	public void projectile_hit(LivingEntityHitByProjectileEvent event) throws XPathExpressionException
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (hit_by_projectile != null) hit_by_projectile.performActions(event.getEntity(), event.getProjectile(), event);
	}
	
	@EventHandler
	public void hour_change(HourChangeEvent event)
	{
		if (hour_change != null) hour_change.performActions(null, null, event);
	}
	
	@EventHandler
	public void in_area(PlayerInAreaEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		if (in_area != null) in_area.performActions(event.getPlayer(), null, event);
	}
	
	@EventHandler
	public void joins(PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		if (ignoreWorld(p.getWorld())) return;
		
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, MParam.NAME, p.getName());
		if (joins != null) joins.performActions(p, null, event);
	}
	
	@EventHandler
	public void leaves(PlayerLeaveLivingEntityEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		if (leaves != null) leaves.performActions(event.getEntity(), null, event);
	}
	
	@EventHandler
	public void leaves_area(PlayerLeaveAreaEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		if (leaves_area != null) leaves_area.performActions(event.getPlayer(), null, event);
	}
	
	@EventHandler
	public void midday(MiddayEvent event)
	{
		if (midday != null) midday.performActions(null, null, event);
	}
	
	@EventHandler
	public void midnight(MidnightEvent event)
	{
		if (midnight != null) midnight.performActions(null, null, event);
	}
	
	@EventHandler
	public void near(PlayerNearLivingEntityEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (near != null) near.performActions(event.getEntity(), null, event);
	}
	
	@EventHandler
	public void night(NightEvent event)
	{
		if (night != null) night.performActions(null, null, event);
	}
	
	@EventHandler
	public void picks_up_item(PlayerPickupItemEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		if (picks_up_item != null) picks_up_item.performActions(event.getPlayer(), null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getPlayer(), MParam.NO_PICK_UP_ITEMS)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_sheared(PlayerShearEntityEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		if (ignoreWorld(le.getWorld())) return;
		
		if (sheared != null) sheared.performActions(le, null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_SHEARED)) event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void spawns(CreatureSpawnEvent event)
	{
		LivingEntity le = event.getEntity();
		if (ignoreWorld(le.getWorld())) return;
		
		if (spawn_reason == null) spawn_reason = event.getSpawnReason().toString();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(MParam.SPAWN_REASON.toString(), spawn_reason);
		if (mob_name != null) data.put(MParam.NAME.toString(), mob_name);
		le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		if (spawns != null) spawns.performActions(le, null, event);
		mob_name = null;
		spawn_reason = null;
	}
	
	@EventHandler
	public void player_respawns(PlayerRespawnEvent event)
	{
		Player p = event.getPlayer();
		if (ignoreWorld(p.getWorld())) return;
		
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, MParam.NAME, p.getName());
		if (spawns != null) spawns.performActions(p, null, event);	
	}
	
	@EventHandler
	public void mob_splits(SlimeSplitEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (splits != null) splits.performActions(event.getEntity(), null, event);
		Integer i = (Integer)Data.getData(event.getEntity(), MParam.SPLIT_INTO);
		if (i == null) return;
		if (i == 0) event.setCancelled(true); else event.setCount(i);
	}
	
	@EventHandler
	public void mob_tamed(EntityTameEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (tamed != null) tamed.performActions(event.getEntity(), null, event);
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_TAMED)) event.setCancelled(true);
	}
	
	@EventHandler
	public void targets(EntityTargetLivingEntityEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		if (ignoreWorld(le.getWorld())) return;
		
		if (targets != null) targets.performActions(le, null, event);
		if (event.isCancelled()) return;
		//targetd event!
		if (Data.hasData(le, MParam.FRIENDLY)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_teleports(EntityTeleportEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		if (ignoreWorld(le.getWorld())) return;
		
		if (teleports != null) teleports.performActions(le, null, event);
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
		
		if (!disabled_timer && repeating != null) repeating.performActions(null, null, event);
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
		if (repeating == null)
		{
			sender.sendMessage("There are no repeating outcomes!");
			sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
			return true;
		}
		if (args[0].equalsIgnoreCase("enable"))
		{
			if (args.length == 1)
			{
				for (Outcome o : repeating.getOutcomes())
				{
					o.setEnabled(true);
				}
				sender.sendMessage("Enabled all repeating outcomes!");
				return true;
			}
			else if (args.length == 2)
			{
				for (Outcome o : repeating.getOutcomes())
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
				for (Outcome o : repeating.getOutcomes())
				{
					o.setEnabled(false);
				}
				sender.sendMessage("Disabled all repeating outcomes!");
				return true;
			}
			else if (args.length == 2)
			{
				for (Outcome o : repeating.getOutcomes())
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
			Event_report er = new Event_report(MEvent.REPEATING);
			if (args.length == 1)
			{					
				sender.sendMessage("Activated all repeating outcomes!");
				for (Outcome o : repeating.getOutcomes())
				{
					if (o.passedConditions_check(er, null, null, null, true)) o.performActions(null, MEvent.REPEATING, null);
				}
				return true;
			}
			else if (args.length == 2)
			{
				sender.sendMessage("Activated repeating outcome " + args[1] + "!");
				for (Outcome o : repeating.getOutcomes())
				{
					if (o.checkName(args[1]) && o.passedConditions_check(er, null, null, null, true)) o.performActions(null, MEvent.REPEATING, null);
				}
				return true;
			}
		}
		else if (args[0].equalsIgnoreCase("force"))
		{
			if (args.length == 1)
			{					
				sender.sendMessage("Forced all repeating outcomes!");
				for (Outcome o : repeating.getOutcomes()) o.performActions(null, MEvent.REPEATING, null);
				return true;
			}
			else if (args.length == 2)
			{
				sender.sendMessage("Forced repeating outcome " + args[1] + "!");
				for (Outcome o : repeating.getOutcomes())
				{
					if (o.checkName(args[1])) o.performActions(null, MEvent.REPEATING, null);
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
				for (Outcome o : repeating.getOutcomes())
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
				for (Outcome o : repeating.getOutcomes())
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
				for (Outcome o : repeating.getOutcomes())
				{
					o.setInterval(i);
				}
				sender.sendMessage("All outcome intervals set to " + i + "!");
				return true;
			}
			else if (args.length == 3)
			{
				for (Outcome o : repeating.getOutcomes())
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

 	private boolean ignoreWorld(World w)
 	{
 		return Data.hasData(w, MParam.IGNORED_WORLD);
 	}

}