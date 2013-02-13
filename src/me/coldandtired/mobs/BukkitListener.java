package me.coldandtired.mobs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.extra_events.*;
import me.coldandtired.mobs.Enums.MParam;

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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class BukkitListener implements Listener
{
	public enum EventType
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
	private Map<EventType, MobsEvent> events = new HashMap<EventType, MobsEvent>();
	//private boolean disabled_timer = false;	
	
	public BukkitListener() throws XPathExpressionException
	{		
		File f = null;
		NodeList list = null;
		for (EventType e : EventType.values())
		{
			f = new File(Mobs.getInstance().getDataFolder(), e.toString().toLowerCase() + ".txt");
			if (!f.exists()) continue;
			
			list = (NodeList)Mobs.getXPath().evaluate("outcomes/outcome", new InputSource(f.getPath()), XPathConstants.NODESET);
			
			if (list.getLength() == 0) continue;
			
			events.put(e, new MobsEvent(e.toString(), list));
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
		
		if (events.containsKey(EventType.APPROACHED)) events.get(EventType.APPROACHED).performActions(new EventValues(event.getEntity(), null, event, "approached"));
	}
		
	@EventHandler
	public void blocks(LivingEntityBlockEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.BLOCKS)) events.get(EventType.BLOCKS).performActions(new EventValues(event.getEntity(), null, event, "blocks"));
	}
	
	@EventHandler
	public void burns(EntityCombustEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (events.containsKey(EventType.BURNS)) events.get(EventType.BURNS).performActions(new EventValues(le, null, event, "burns"));
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_BURN)) event.setCancelled(true);
	}
	
	@EventHandler
	public void changes_block(EntityChangeBlockEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (events.containsKey(EventType.CHANGES_BLOCK)) events.get(EventType.CHANGES_BLOCK).performActions(new EventValues(le, null, event, "changes_block"));
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_MOVE_BLOCKS) || Data.hasData(le, MParam.NO_GRAZE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_creates_portal(EntityCreatePortalEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.CREATES_PORTAL)) events.get(EventType.CREATES_PORTAL).performActions(new EventValues(event.getEntity(), null, event, "creates_portal"));
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

		if (events.containsKey(EventType.DAMAGED)) events.get(EventType.DAMAGED).performActions(new EventValues(le, null, event, "damaged"));
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
		if (events.containsKey(EventType.DAWN)) events.get(EventType.DAWN).performActions(new EventValues(null, null, event, "dawn"));
	}
	
	@EventHandler
	public void dies(EntityDeathEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.DIES)) events.get(EventType.DIES).performActions(new EventValues(event.getEntity(), null, event, "dies"));
		if (Data.hasData(event.getEntity(), MParam.CLEAR_DROPS)) event.getDrops().clear();
		if (Data.hasData(event.getEntity(), MParam.CLEAR_EXP)) event.setDroppedExp(0);
	}
	
	@EventHandler
	public void player_dies(PlayerDeathEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.PLAYER_DIES)) events.get(EventType.PLAYER_DIES).performActions(new EventValues(event.getEntity(), null, event, "player_dies"));
		if (Data.hasData(event.getEntity(), MParam.CLEAR_DROPS)) event.getDrops().clear();
		if (Data.hasData(event.getEntity(), MParam.CLEAR_EXP)) event.setDroppedExp(0);
	}
	
	@EventHandler
	public void dusk(DuskEvent event)
	{
		if (events.containsKey(EventType.DUSK)) events.get(EventType.DUSK).performActions(new EventValues(null, null, event, "dusk"));
	}
	
	@EventHandler
	public void dyed(SheepDyeWoolEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.DYED)) events.get(EventType.DYED).performActions(new EventValues(event.getEntity(), null, event, "dyed"));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_DYED)) event.setCancelled(true);
	}
	
	@EventHandler
	public void creeper_evolves(CreeperPowerEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.EVOLVES)) events.get(EventType.EVOLVES).performActions(new EventValues(event.getEntity(), null, event, "evolves"));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void pig_evolves(PigZapEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.EVOLVES)) events.get(EventType.EVOLVES).performActions(new EventValues(event.getEntity(), null, event, "evolves"));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void enters_area(PlayerEnterAreaEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		if (events.containsKey(EventType.ENTERS_AREA)) events.get(EventType.ENTERS_AREA).performActions(new EventValues(event.getPlayer(), null, event, "enters_area"));
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
		
		if (events.containsKey(EventType.EXPLODES)) events.get(EventType.EXPLODES).performActions(new EventValues(le, null, event, "explodes"));
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
		
		if (events.containsKey(EventType.GROWS_WOOL)) events.get(EventType.GROWS_WOOL).performActions(new EventValues(event.getEntity(), null, event, "grows_wool"));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_GROW_WOOL)) event.setCancelled(true);
	}
	
	@EventHandler
	public void heals(EntityRegainHealthEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();

		if (events.containsKey(EventType.HEALS)) events.get(EventType.HEALS).performActions(new EventValues(le, null, event, "heals"));
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
		
		if (events.containsKey(EventType.HIT)) events.get(EventType.HIT).performActions(new EventValues(le, null, event, "hit"));
	}
	
	@EventHandler
	public void projectile_hit(LivingEntityHitByProjectileEvent event) throws XPathExpressionException
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.HIT_BY_PROJECTILE)) events.get(EventType.HIT_BY_PROJECTILE).performActions(new EventValues(event.getEntity(), event.getProjectile(), event, "hit_by_projectile"));
	}
	
	@EventHandler
	public void hour_change(HourChangeEvent event)
	{
		if (events.containsKey(EventType.HOUR_CHANGE)) events.get(EventType.HOUR_CHANGE).performActions(new EventValues(null, null, event, "hour_change"));
	}
	
	@EventHandler
	public void in_area(PlayerInAreaEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		if (events.containsKey(EventType.IN_AREA)) events.get(EventType.IN_AREA).performActions(new EventValues(event.getPlayer(), null, event, "in_area"));
	}
	
	@EventHandler
	public void player_joins(PlayerJoinEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		Player p = event.getPlayer();
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, MParam.NAME, p.getName());
		if (events.containsKey(EventType.PLAYER_JOINS)) events.get(EventType.PLAYER_JOINS).performActions(new EventValues(p, null, event, "player_joins"));
	}
	
	@EventHandler
	public void leaves(PlayerLeaveLivingEntityEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.LEAVES)) events.get(EventType.LEAVES).performActions(new EventValues(event.getEntity(), null, event, "leaves"));
	}
	
	@EventHandler
	public void leaves_area(PlayerLeaveAreaEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		if (events.containsKey(EventType.LEAVES_AREA)) events.get(EventType.LEAVES_AREA).performActions(new EventValues(event.getPlayer(), null, event, "leaves_area"));
	}
	
	@EventHandler
	public void midday(MiddayEvent event)
	{
		if (events.containsKey(EventType.MIDDAY)) events.get(EventType.MIDDAY).performActions(new EventValues(null, null, event, "midday"));
	}
	
	@EventHandler
	public void midnight(MidnightEvent event)
	{
		if (events.containsKey(EventType.MIDNIGHT)) events.get(EventType.MIDNIGHT).performActions(new EventValues(null, null, event, "midnight"));
	}
	
	@EventHandler
	public void near(PlayerNearLivingEntityEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.NEAR)) events.get(EventType.NEAR).performActions(new EventValues(event.getEntity(), null, event, "near"));
	}
	
	@EventHandler
	public void night(NightEvent event)
	{
		if (events.containsKey(EventType.NIGHT)) events.get(EventType.NIGHT).performActions(new EventValues(null, null, event, "night"));
	}
	
	@EventHandler
	public void picks_up_item(PlayerPickupItemEvent event)
	{
		if (ignore_world(event.getPlayer().getWorld())) return;
		
		if (events.containsKey(EventType.PICKS_UP_ITEM)) events.get(EventType.PICKS_UP_ITEM).performActions(new EventValues(event.getPlayer(), null, event, "picks_up_item"));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getPlayer(), MParam.NO_PICK_UP_ITEMS)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mob_sheared(PlayerShearEntityEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (events.containsKey(EventType.SHEARED)) events.get(EventType.SHEARED).performActions(new EventValues(le, null, event, "sheared"));
		if (event.isCancelled()) return;
		
		if (Data.hasData(le, MParam.NO_SHEARED)) event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void spawns(CreatureSpawnEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		if (spawn_reason == null) spawn_reason = event.getSpawnReason().toString();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(MParam.SPAWN_REASON.toString(), spawn_reason);
		if (mob_name != null) data.put(MParam.NAME.toString(), mob_name);
		le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getInstance(), data));
		if (events.containsKey(EventType.SPAWNS)) events.get(EventType.SPAWNS).performActions(new EventValues(le, null, event, "spawns"));
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
		if (events.containsKey(EventType.PLAYER_SPAWNS)) events.get(EventType.PLAYER_SPAWNS).performActions(new EventValues(p, null, event, "player_spawns"));	
	}
	
	@EventHandler
	public void mob_splits(SlimeSplitEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.SPLITS)) events.get(EventType.SPLITS).performActions(new EventValues(event.getEntity(), null, event, "splits"));
		Integer i = (Integer)Data.getData(event.getEntity(), MParam.SPLIT_INTO);
		if (i == null) return;
		if (i == 0) event.setCancelled(true); else event.setCount(i);
	}
	
	@EventHandler
	public void mob_tamed(EntityTameEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (events.containsKey(EventType.TAMED)) events.get(EventType.TAMED).performActions(new EventValues(event.getEntity(), null, event, "tamed"));
		if (event.isCancelled()) return;
		
		if (Data.hasData(event.getEntity(), MParam.NO_TAMED)) event.setCancelled(true);
	}
	
	@EventHandler
	public void targets(EntityTargetLivingEntityEvent event)
	{
		if (ignore_world(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		
		if (events.containsKey(EventType.TARGETS)) events.get(EventType.TARGETS).performActions(new EventValues(le, null, event, "targets"));
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
		
		if (events.containsKey(EventType.TELEPORTS)) events.get(EventType.TELEPORTS).performActions(new EventValues(le, null, event, "teleports"));
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
		
		//if (!disabled_timer && events.containsKey(MEvents.REPEATING)) events.get(MEvents.REPEATING).performActions(new Bukkit_values(null, null, event));
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