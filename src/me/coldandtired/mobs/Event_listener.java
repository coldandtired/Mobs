package me.coldandtired.mobs;

import java.util.HashMap;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.extra_events.*;
import me.coldandtired.mobs.enums.MParam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
	public enum MEvents
	{//ATTACKED,
		APPROACHED,
		BLOCKS,
		BURNS,
		CHANGES_BLOCK,
		CREATES_PORTAL,
		DAMAGED,
		DAWN,
		DIES,
		DUSK,
		DYED,
		ENTERS_AREA,
		EVOLVES,
		EXPLODES,
		GROWS_WOOL,
		HEALS,
		HIT,
		HIT_BY_PROJECTILE,
		HOUR_CHANGE,
		IN_AREA,
		LEAVES,
		LEAVES_AREA,
		MIDDAY,
		MIDNIGHT,
		NEAR,
		NIGHT,
		PICKS_UP_ITEM,
		PLAYER_DIES,
		PLAYER_JOINS,
		PLAYER_SPAWNS,
		REPEATING,
		SHEARED,
		SPAWNS,
		SPLITS,
		TAMED,
		TARGETS,
		TELEPORTS
	}
		
	private String mob_name = null;
	private String spawn_reason = null;
	private Map<MEvents, Mobs_event> events = new HashMap<MEvents, Mobs_event>();
	private boolean disabled_timer = false;
	
	
	public Event_listener(boolean allow_debug) throws XPathExpressionException
	{		
		for (MEvents e : MEvents.values())
		{
			Mobs_event me = Mobs_event.fill(e, allow_debug);
			if (me != null) events.put(e, me);
		}		
	}	
	
	private boolean ignore_world(World world)
	{
		return Data.hasData(world, MParam.IGNORED_WORLD);
	}
	
	@EventHandler
	public void approached(PlayerApproachLivingEntityEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		if (events.containsKey(MEvents.APPROACHED)) events.get(MEvents.APPROACHED).performActions(new Bukkit_values(event.getEntity(), null, event));
	}
		
	@EventHandler
	public void blocks(LivingEntityBlockEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.BLOCKS)) events.get(MEvents.BLOCKS).performActions(new Bukkit_values(event.getEntity(), null, event));
	}
	
	@EventHandler
	public void burns(EntityCombustEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (events.containsKey(MEvents.BURNS)) events.get(MEvents.BURNS).performActions(new Bukkit_values(le, null, event));
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_BURN)) event.setCancelled(true);
	}
	
	@EventHandler
	public void changes_block(EntityChangeBlockEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (events.containsKey(MEvents.CHANGES_BLOCK)) events.get(MEvents.CHANGES_BLOCK).performActions(new Bukkit_values(le, null, event));
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_MOVE_BLOCKS) || Data.hasData(le, MParam.NO_GRAZE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_creates_portal(EntityCreatePortalEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.CREATES_PORTAL)) events.get(MEvents.CREATES_PORTAL).performActions(new Bukkit_values(event.getEntity(), null, event));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_CREATE_PORTALS)) event.setCancelled(true);
	}
	
	@EventHandler
	public void damaged(LivingEntityDamageEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		if (event.getDamage() == 1000) return;
		LivingEntity le = (LivingEntity)event.getEntity();

		if (events.containsKey(MEvents.DAMAGED)) events.get(MEvents.DAMAGED).performActions(new Bukkit_values(le, null, event));
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
		if (events.containsKey(MEvents.DAWN)) events.get(MEvents.DAWN).performActions(new Bukkit_values(null, null, event));
	}
	
	@EventHandler
	public void dies(EntityDeathEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.DIES)) events.get(MEvents.DIES).performActions(new Bukkit_values(event.getEntity(), null, event));
		if (Data.hasData(event.getEntity(), MParam.CLEAR_DROPS)) event.getDrops().clear();
		if (Data.hasData(event.getEntity(), MParam.CLEAR_EXP)) event.setDroppedExp(0);
	}
	
	@EventHandler
	public void player_dies(PlayerDeathEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.PLAYER_DIES)) events.get(MEvents.PLAYER_DIES).performActions(new Bukkit_values(event.getEntity(), null, event));
		if (Data.hasData(event.getEntity(), MParam.CLEAR_DROPS)) event.getDrops().clear();
		if (Data.hasData(event.getEntity(), MParam.CLEAR_EXP)) event.setDroppedExp(0);
	}
	
	@EventHandler
	public void dusk(DuskEvent event)
	{
		if (events.containsKey(MEvents.DUSK)) events.get(MEvents.DUSK).performActions(new Bukkit_values(null, null, event));
	}
	
	@EventHandler
	public void dyed(SheepDyeWoolEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.DYED)) events.get(MEvents.DYED).performActions(new Bukkit_values(event.getEntity(), null, event));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_DYED)) event.setCancelled(true);
	}
	
	@EventHandler
	public void creeper_evolves(CreeperPowerEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.EVOLVES)) events.get(MEvents.EVOLVES).performActions(new Bukkit_values(event.getEntity(), null, event));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void pig_evolves(PigZapEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.EVOLVES)) events.get(MEvents.EVOLVES).performActions(new Bukkit_values(event.getEntity(), null, event));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void enters_area(PlayerEnterAreaEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		if (events.containsKey(MEvents.ENTERS_AREA)) events.get(MEvents.ENTERS_AREA).performActions(new Bukkit_values(event.getPlayer(), null, event));
	}
	
	@EventHandler
	public void explodes(EntityExplodeEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		Entity entity = event.getEntity();		
		if (entity == null) return;		
		if (entity instanceof Fireball) entity = ((Fireball)entity).getShooter();
		if (!(entity instanceof LivingEntity)) return;

		LivingEntity le = (LivingEntity)entity;
		
		if (events.containsKey(MEvents.EXPLODES)) events.get(MEvents.EXPLODES).performActions(new Bukkit_values(le, null, event));
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
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.GROWS_WOOL)) events.get(MEvents.GROWS_WOOL).performActions(new Bukkit_values(event.getEntity(), null, event));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_GROW_WOOL)) event.setCancelled(true);
	}
	
	@EventHandler
	public void heals(EntityRegainHealthEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();

		if (events.containsKey(MEvents.HEALS)) events.get(MEvents.HEALS).performActions(new Bukkit_values(le, null, event));
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
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (events.containsKey(MEvents.HIT)) events.get(MEvents.HIT).performActions(new Bukkit_values(le, null, event));
	}
	
	@EventHandler
	public void projectile_hit(LivingEntityHitByProjectileEvent event) throws XPathExpressionException
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.HIT_BY_PROJECTILE)) events.get(MEvents.HIT_BY_PROJECTILE).performActions(new Bukkit_values(event.getEntity(), event.getProjectile(), event));
	}
	
	@EventHandler
	public void hour_change(HourChangeEvent event)
	{
		if (events.containsKey(MEvents.HOUR_CHANGE)) events.get(MEvents.HOUR_CHANGE).performActions(new Bukkit_values(null, null, event));
	}
	
	@EventHandler
	public void in_area(PlayerInAreaEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		if (events.containsKey(MEvents.IN_AREA)) events.get(MEvents.IN_AREA).performActions(new Bukkit_values(event.getPlayer(), null, event));
	}
	
	@EventHandler
	public void player_joins(PlayerJoinEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		Player p = event.getPlayer();
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, MParam.NAME, p.getName());
		if (events.containsKey(MEvents.PLAYER_JOINS)) events.get(MEvents.PLAYER_JOINS).performActions(new Bukkit_values(p, null, event));
	}
	
	@EventHandler
	public void leaves(PlayerLeaveLivingEntityEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.LEAVES)) events.get(MEvents.LEAVES).performActions(new Bukkit_values(event.getEntity(), null, event));
	}
	
	@EventHandler
	public void leaves_area(PlayerLeaveAreaEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		if (events.containsKey(MEvents.LEAVES_AREA)) events.get(MEvents.LEAVES_AREA).performActions(new Bukkit_values(event.getPlayer(), null, event));
	}
	
	@EventHandler
	public void midday(MiddayEvent event)
	{
		if (events.containsKey(MEvents.MIDDAY)) events.get(MEvents.MIDDAY).performActions(new Bukkit_values(null, null, event));
	}
	
	@EventHandler
	public void midnight(MidnightEvent event)
	{
		if (events.containsKey(MEvents.MIDNIGHT)) events.get(MEvents.MIDNIGHT).performActions(new Bukkit_values(null, null, event));
	}
	
	@EventHandler
	public void near(PlayerNearLivingEntityEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.NEAR)) events.get(MEvents.NEAR).performActions(new Bukkit_values(event.getEntity(), null, event));
	}
	
	@EventHandler
	public void night(NightEvent event)
	{
		if (events.containsKey(MEvents.NIGHT)) events.get(MEvents.NIGHT).performActions(new Bukkit_values(null, null, event));
	}
	
	@EventHandler
	public void picks_up_item(PlayerPickupItemEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		if (events.containsKey(MEvents.PICKS_UP_ITEM)) events.get(MEvents.PICKS_UP_ITEM).performActions(new Bukkit_values(event.getPlayer(), null, event));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getPlayer(), MParam.NO_PICK_UP_ITEMS)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_sheared(PlayerShearEntityEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (events.containsKey(MEvents.SHEARED)) events.get(MEvents.SHEARED).performActions(new Bukkit_values(le, null, event));
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_SHEARED)) event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void spawns(CreatureSpawnEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		if (!le.getType().equals(EntityType.PIG)) return;
		if (spawn_reason == null) spawn_reason = event.getSpawnReason().toString();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(MParam.SPAWN_REASON.toString(), spawn_reason);
		if (mob_name != null) data.put(MParam.NAME.toString(), mob_name);
		le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		if (events.containsKey(MEvents.SPAWNS)) events.get(MEvents.SPAWNS).performActions(new Bukkit_values(le, null, event));
		mob_name = null;
		spawn_reason = null;
		
		//event.setCancelled(true);
	}
	
	@EventHandler
	public void player_respawns(PlayerRespawnEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		Player p = event.getPlayer();
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, MParam.NAME, p.getName());
		if (events.containsKey(MEvents.PLAYER_SPAWNS)) events.get(MEvents.PLAYER_SPAWNS).performActions(new Bukkit_values(p, null, event));	
	}
	
	@EventHandler
	public void mob_splits(SlimeSplitEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.SPLITS)) events.get(MEvents.SPLITS).performActions(new Bukkit_values(event.getEntity(), null, event));
		Integer i = (Integer)Data.getData(event.getEntity(), MParam.SPLIT_INTO);
		if (i == null) return;
		if (i == 0) event.setCancelled(true); else event.setCount(i);
	}
	
	@EventHandler
	public void mob_tamed(EntityTameEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(MEvents.TAMED)) events.get(MEvents.TAMED).performActions(new Bukkit_values(event.getEntity(), null, event));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_TAMED)) event.setCancelled(true);
	}
	
	@EventHandler
	public void targets(EntityTargetLivingEntityEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (events.containsKey(MEvents.TARGETS)) events.get(MEvents.TARGETS).performActions(new Bukkit_values(le, null, event));
		if (event.isCancelled()) return;
		//targetd event!
		if (Data.hasData(le, MParam.FRIENDLY)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_teleports(EntityTeleportEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (events.containsKey(MEvents.TELEPORTS)) events.get(MEvents.TELEPORTS).performActions(new Bukkit_values(le, null, event));
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
		
		if (!disabled_timer && events.containsKey(MEvents.REPEATING)) events.get(MEvents.REPEATING).performActions(new Bukkit_values(null, null, event));
	}
	
 	public void setMob_name(String name)
	{
		spawn_reason = "SPAWNED";
		mob_name = name;
	}

 	public boolean adjustRepeating_outcomes(CommandSender sender, String[] args)
 	{
 		/*if (args.length == 0)
		{
			sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
			return true;
		}
		if (!events.containsKey(MEvents.REPEATING))
		{
			sender.sendMessage("There are no repeating outcomes!");
			sender.sendMessage("Repeating outcomes are " + (disabled_timer ? "paused" : "running"));
			return true;
		}
		if (args[0].equalsIgnoreCase("enable"))
		{
			if (args.length == 1)
			{
				for (Outcome o : events.get(MEvents.REPEATING).getOutcomes())
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
		}*/
		return false;
 	}
}