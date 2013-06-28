package eu.sylian.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import eu.sylian.extraevents.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import eu.sylian.extraevents.PlayerCommandEvent;
import eu.sylian.mobs.Enums.EventType;
import eu.sylian.mobs.Enums.SubactionType;
import eu.sylian.mobs.api.Data;

public class BukkitListener implements Listener
{		
	private String mob_name = null;
	private String spawn_reason = null;
	private Map<EventType, MobsEvent> events;	
	
	public void fillEvents() throws XPathExpressionException
	{
		events = new HashMap<EventType, MobsEvent>();
		File f = null;
		Element element = null;
		for (EventType e : EventType.values())
		{
			f = new File(Mobs.getPlugin().getDataFolder(), e.toString().toLowerCase() + ".txt");
			if (!f.exists()) continue;
			
			element = (Element)Mobs.getXPath().evaluate("event", new InputSource(f.getPath()), XPathConstants.NODE);
			
			if (element == null) continue;
			
			events.put(e, new MobsEvent(element));
		}
	}
	
	private boolean ignoreWorld(World world)
	{
		return Data.hasData(world, "IGNORED_WORLD");
	}
	
	@EventHandler
	public void approached(PlayerApproachLivingEntityEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.PLAYER_APPROACHES;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getEntity());
			ev.setAuxMob(event.getPlayer());
			events.get(et).performActions(ev);
		}
	}
		
	@EventHandler
	public void blocks(LivingEntityBlockEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.BLOCKS;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getEntity());
			ev.setAuxMob(event.getAttacker());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void burns(EntityCombustEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		if (!(event.getEntity() instanceof LivingEntity)) return;

		LivingEntity le = (LivingEntity)event.getEntity();
		EventType et = EventType.BURNS;	
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(le, SubactionType.NO_BURN)) event.setCancelled(true);
	}
	
	@EventHandler
	public void changesBlock(EntityChangeBlockEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity le = (LivingEntity)event.getEntity();		
		EventType et = EventType.CHANGES_BLOCK;		
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(le, SubactionType.NO_MOVE_BLOCKS) || Data.hasData(le, SubactionType.NO_GRAZE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void commandEntered(PlayerCommandEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.PLAYER_COMMAND;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getPlayer());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void mobCreatesPortal(EntityCreatePortalEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.CREATES_PORTAL;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, event.getEntity());
			events.get(et).performActions(ev);
		}
		if (Data.hasData(event.getEntity(), SubactionType.NO_CREATE_PORTALS)) event.setCancelled(true);
	}
	
	@EventHandler
	public void damaged(LivingEntityDamageEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;	
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity le = (LivingEntity)event.getEntity();

		EventType et = EventType.DAMAGED;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, event.getEntity());
			ev.setAuxMob(event.getAttacker());
			ev.setProjectile(event.getProjectile());
			events.get(et).performActions(ev);
		}
		
		int damage = event.getDamage();		
			
		SubactionType st = SubactionType.valueOf("DAMAGE_FROM_" + event.getCause());
		switch (event.getCause())
		{
			case ENTITY_ATTACK:
				LivingEntity damager = event.getAttacker();
				if (damager != null && Data.hasData(damager, SubactionType.ATTACK_POWER))
				{
					damage = (Integer)Data.getData(damager, SubactionType.ATTACK_POWER);
				}
				if (!Data.hasData(le, st)) break;
				damage = (Integer)Data.getData(le, st);
				break;
			default:
				if (!Data.hasData(le, st)) break;
				damage = (Integer)Data.getData(le, st);
				break;
		}
		event.setDamage(damage);
	}
	
	@EventHandler
	public void dawn(DawnEvent event)
	{
		EventType et = EventType.DAWN;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null);
			events.get(et).performActions(ev);
		}
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void dies(EntityDeathEvent event)
	{		
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();	
		
		if (le instanceof Player) return;
		
		EventType et = EventType.DIES;
				
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, le);
			ev.setAuxMob(le.getKiller());
			events.get(et).performActions(ev);
		}
				
		if (Data.hasData(le, SubactionType.NO_DROPS))
		{			
			event.getDrops().clear();
			event.setDroppedExp(0);
			return;
		}
		
		if (Data.hasData(le, SubactionType.NO_DEFAULT_DROPS))
		{
			event.getDrops().clear();
		}
		
		if (Data.hasData(le, SubactionType.CUSTOM_DROPS))
		{
			Object o = Data.getData(le, SubactionType.CUSTOM_DROPS);
			if (o != null)
			{
				for (ItemStack is : (ArrayList<ItemStack>)o)
				{
					event.getDrops().add(is);
				}
			}
		}
			
		if (Data.hasData(le, SubactionType.DROPPED_EXP))
		{
			event.setDroppedExp((Integer)Data.getData(le, SubactionType.DROPPED_EXP));
		}
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void playerDies(PlayerDeathEvent event)
	{		
		if (ignoreWorld(event.getEntity().getWorld())) return;

		Player p = event.getEntity();
		EventType et = EventType.PLAYER_DIES;
		
		if (events.containsKey(et))
		{			
			Entity e = p.getLastDamageCause().getEntity();
			LivingEntity attacker = e instanceof LivingEntity ? (LivingEntity)e : null;			
			EventValues ev = new EventValues(event, et, p);
			ev.setAuxMob(attacker);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(p, SubactionType.NO_DROPS))
		{			
			event.getDrops().clear();
			event.setDroppedExp(0);
			return;
		}
		
		if (Data.hasData(p, SubactionType.NO_DEFAULT_DROPS))
		{
			event.getDrops().clear();
		}
		
		if (Data.hasData(p, SubactionType.CUSTOM_DROPS))
		{
			Object o = Data.getData(p, SubactionType.CUSTOM_DROPS);
			if (o != null)
			{
				for (ItemStack is : (ArrayList<ItemStack>)o)
				{
					event.getDrops().add(is);
				}
			}
		}
			
		if (Data.hasData(p, SubactionType.DROPPED_EXP))
		{
			event.setDroppedExp((Integer)Data.getData(p, SubactionType.DROPPED_EXP));
		}
	}
	
	@EventHandler
	public void dusk(DuskEvent event)
	{
		EventType et = EventType.DUSK;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null);
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void dyed(SheepDyeWoolEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.DYED;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(event.getEntity(), SubactionType.NO_DYED)) event.setCancelled(true);
	}
	
	@EventHandler
	public void creeperEvolves(CreeperPowerEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.EVOLVES;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(event.getEntity(), SubactionType.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void pigEvolves(PigZapEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.EVOLVES;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(event.getEntity(), SubactionType.NO_EVOLVE)) event.setCancelled(true);
	}
	
	@EventHandler
	public void playerEntersArea(PlayerEnterAreaEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.PLAYER_ENTERS_AREA;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getPlayer());
			events.get(et).performActions(ev);
		}
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
		EventType et = EventType.EXPLODES;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(le, SubactionType.FRIENDLY)) event.setCancelled(true);
			
		if (event.isCancelled()) return;
		else
		{			
			if (Data.hasData(le, SubactionType.NO_DESTROY_BLOCKS)) event.blockList().clear();
			else
			{
				if (Data.hasData(le, SubactionType.EXPLOSION_SIZE))
				{
					Integer size = (Integer)Data.getData(le, SubactionType.EXPLOSION_SIZE);
					if (size == null) return;
					
					event.setCancelled(true);
					Location loc = event.getLocation();
					if (Data.hasData(le, SubactionType.FIERY_EXPLOSION)) loc.getWorld().createExplosion(loc, size, true);
					else loc.getWorld().createExplosion(loc, size);
				}
			}
		}		
	}
	
	@EventHandler
	public void growsWool(SheepRegrowWoolEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.GROWS_WOOL;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(event.getEntity(), SubactionType.NO_GROW_WOOL)) event.setCancelled(true);		
	}
	
	@EventHandler
	public void heals(EntityRegainHealthEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		EventType et = EventType.HEALS;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(le, SubactionType.NO_HEAL)) event.setCancelled(true);		
	}	
	
	@EventHandler
	public void hit(EntityDamageEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		EventType et = EventType.HIT;		
		if (events.containsKey(et))
		{
			Entity e = event instanceof EntityDamageByEntityEvent ? ((EntityDamageByEntityEvent)event).getDamager() : null;
			LivingEntity le = e instanceof LivingEntity ? (LivingEntity)e : null;
			EventValues ev = new EventValues(event, et, (LivingEntity)event.getEntity());
			ev.setAuxMob(le);
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void projectileHit(LivingEntityHitByProjectileEvent event) throws XPathExpressionException
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.HIT_BY_PROJECTILE;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getEntity());
			ev.setAuxMob(event.getAttacker());
			ev.setProjectile(event.getProjectile());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void hourChange(HourChangeEvent event)
	{
		EventType et = EventType.HOUR_CHANGE;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null);
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void playerInArea(PlayerInAreaEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.PLAYER_IN_AREA;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getPlayer());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void playerJoins(PlayerJoinEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		Player p = event.getPlayer();
		Data.putData(p, SubactionType.SPAWN_REASON, "NATURAL");
		Data.putData(p, SubactionType.NAME, p.getName());
		
		EventType et = EventType.PLAYER_JOINS;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, p);
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void leaves(PlayerLeaveLivingEntityEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.LEAVES;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getEntity());
			ev.setAuxMob(event.getPlayer());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void playerLeavesArea(PlayerLeaveAreaEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.PLAYER_LEAVES_AREA;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getPlayer());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void midday(MiddayEvent event)
	{
		EventType et = EventType.MIDDAY;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null);
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void midnight(MidnightEvent event)
	{
		EventType et = EventType.MIDNIGHT;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null);
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void mobEntersArea(LivingEntityEnterAreaEvent event)
	{
		if (ignoreWorld(event.getLivingEntity().getWorld())) return;
		
		EventType et = EventType.MOB_ENTERS_AREA;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getLivingEntity());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void mobInArea(LivingEntityInAreaEvent event)
	{
		if (ignoreWorld(event.getLivingEntity().getWorld())) return;
		
		EventType et = EventType.MOB_IN_AREA;	
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getLivingEntity());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void mobLeavesArea(LivingEntityLeaveAreaEvent event)
	{
		if (ignoreWorld(event.getLivingEntity().getWorld())) return;
		
		EventType et = EventType.MOB_LEAVES_AREA;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getLivingEntity());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void near(PlayerNearLivingEntityEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.PLAYER_NEAR;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getEntity());
			ev.setAuxMob(event.getPlayer());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void playerTargeted(PlayerTargetedEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;		
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.PLAYER_TARGETED; 
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, event.getPlayer());
			ev.setAuxMob(le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(le, SubactionType.FRIENDLY)) event.setCancelled(true);
	}
	
	@EventHandler
	public void night(NightEvent event)
	{
		EventType et = EventType.NIGHT;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null);
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void picksUpItem(PlayerPickupItemEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.PICKS_UP_ITEM;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, event.getPlayer());
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(event.getPlayer(), SubactionType.NO_PICK_UP_ITEMS)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mobSheared(PlayerShearEntityEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity le = (LivingEntity)event.getEntity();
		EventType et = EventType.SHEARED;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(le, SubactionType.NO_SHEARING)) event.setCancelled(true);
	}
	
 	public void setMobName(String name)
	{
		spawn_reason = "SPAWNED";
		mob_name = name;
	}
 	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void spawns(CreatureSpawnEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.SPAWNS;
		
		if (spawn_reason == null) spawn_reason = event.getSpawnReason().toString();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(SubactionType.SPAWN_REASON.toString(), spawn_reason);
		if (mob_name != null) le.setCustomName(mob_name);
		le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getPlugin(), data));
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		mob_name = null;
		spawn_reason = null;
	}
	
	@EventHandler
	public void playerRespawns(PlayerRespawnEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		Player p = event.getPlayer();
		Data.putData(p, SubactionType.SPAWN_REASON, "NATURAL");
		Data.putData(p, SubactionType.NAME, p.getName());		
		
		EventType et = EventType.PLAYER_SPAWNS;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, p);
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void mobSplits(SlimeSplitEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.SPLITS;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		Integer i = (Integer)Data.getData(event.getEntity(), SubactionType.SPLIT_INTO);
		if (i == null) return;
		if (i == 0) event.setCancelled(true); else event.setCount(i);
	}
	
	@EventHandler
	public void mobTamed(EntityTameEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.TAMED;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, event.getEntity());
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(event.getEntity(), SubactionType.NO_TAMING)) event.setCancelled(true);
	}
	
	@EventHandler
	public void targets(EntityTargetLivingEntityEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity le = (LivingEntity)event.getEntity();
		EventType et = EventType.TARGETS; 
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			ev.setAuxMob(event.getTarget());
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(le, SubactionType.FRIENDLY)) event.setCancelled(true);
	}
	
	@EventHandler
	public void mobTeleports(EntityTeleportEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity le = (LivingEntity)event.getEntity();
		EventType et = EventType.TELEPORTS;
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le);
			events.get(et).performActions(ev);
		}
		
		if (Data.hasData(le, SubactionType.NO_TELEPORT)) event.setCancelled(true);
	}
		
// timer stuff
	
	@EventHandler
	public void timerTick(TimerActivateEvent event)
	{
		if (ignoreWorld(Bukkit.getWorld(event.getTimer().getWorld()))) return;
		
		EventType et = EventType.TIMER;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null);
			ev.setTimer(event.getTimer());
			events.get(et).performActions(ev);
		}
	}
	
	@EventHandler
	public void check_max_life(SecondTickEvent event)
	{
		for (World w : Bukkit.getWorlds())
		{
			if (Data.hasData(w, "IGNORED_WORLD")) continue;
			
			for (LivingEntity le : w.getEntitiesByClass(LivingEntity.class))
			{
				if (le instanceof Player) continue;
				
				Integer life = (Integer)Data.getData(le, SubactionType.MAX_LIFE);
				if (life == null) continue;
				life--;
				if (life < 0) le.remove();	
				else Data.putData(le, SubactionType.MAX_LIFE, life);
			}
		}
	}
}