package me.coldandtired.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import me.coldandtired.extra_events.*;
import me.coldandtired.mobs.Enums.EventType;
import me.coldandtired.mobs.Enums.MParam;
import me.coldandtired.mobs.Enums.SubactionType;
import me.coldandtired.mobs.api.Data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class BukkitListener implements Listener
{		
	private String mob_name = null;
	private String spawn_reason = null;
	private Map<EventType, List<MobsAction>> events = new HashMap<EventType, List<MobsAction>>();		
	
	public BukkitListener() throws XPathExpressionException
	{		
		File f = null;
		NodeList list = null;
		for (EventType e : EventType.values())
		{
			f = new File(Mobs.getPlugin().getDataFolder(), e.toString().toLowerCase() + ".txt");
			if (!f.exists()) continue;
			
			list = (NodeList)Mobs.getXPath().evaluate("actions/action", new InputSource(f.getPath()), XPathConstants.NODESET);
			
			if (list.getLength() == 0) continue;
			
			List<MobsAction> temp = new ArrayList<MobsAction>();
			for (int i = 0; i < list.getLength(); i++)
			{
				temp.add(new MobsAction(e.toString(), (Element)list.item(i)));
			}
			
			events.put(e, temp);
		}		
	}	
	
	private boolean ignoreWorld(World world)
	{
		return Data.hasData(world, MParam.IGNORED_WORLD);
	}
	
	@EventHandler
	public void approached(PlayerApproachLivingEntityEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.APPROACHED;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getEntity(), event.getPlayer(), null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
		
	@EventHandler
	public void blocks(LivingEntityBlockEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.BLOCKS;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getEntity(), event.getAttacker(), null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void burns(EntityCombustEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		if (!(event.getEntity() instanceof LivingEntity)) return;

		LivingEntity le = (LivingEntity)event.getEntity();
		EventType et = EventType.BURNS;	
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(le, SubactionType.NO_BURN)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(le, SubactionType.NO_BURN)) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void changesBlock(EntityChangeBlockEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity le = (LivingEntity)event.getEntity();		
		EventType et = EventType.CHANGES_BLOCK;		
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(le, SubactionType.NO_MOVE_BLOCKS) || Data.hasData(le, SubactionType.NO_GRAZE)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(le, SubactionType.NO_MOVE_BLOCKS) || Data.hasData(le, SubactionType.NO_GRAZE)) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void mobCreatesPortal(EntityCreatePortalEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.CREATES_PORTAL;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_CREATE_PORTALS)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, event.getEntity(), null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_CREATE_PORTALS)) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void damaged(LivingEntityDamageEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity le = (LivingEntity)event.getEntity();

		EventType et = EventType.DAMAGED;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			int damage = event.getDamage();		
			
			String s = "DAMAGE_FROM" + event.getCause();
			switch (event.getCause())
			{
				case ENTITY_ATTACK:
					LivingEntity damager = event.getAttacker();
					if (damager != null) damage = Data.adjustInt(damager, SubactionType.ATTACK_POWER, damage);
					
					damage = Data.adjustInt(le, s, damage);
					break;
				default:
					damage = Data.adjustInt(le, s, damage);
					break;
			}
			event.setDamage(damage);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, event.getEntity(), event.getAttacker(), null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			int damage = event.getDamage();		
			
			String s = "DAMAGE_FROM" + event.getCause();
			switch (event.getCause())
			{
				case ENTITY_ATTACK:
					LivingEntity damager = event.getAttacker();
					if (damager != null) damage = Data.adjustInt(damager, SubactionType.ATTACK_POWER, damage);
					
					damage = Data.adjustInt(le, s, damage);
					break;
				default:
					damage = Data.adjustInt(le, s, damage);
					break;
			}
			event.setDamage(damage);
		}
	}
	
	@EventHandler
	public void dawn(DawnEvent event)
	{
		EventType et = EventType.DAWN;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void dies(EntityDeathEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();		
		EventType et = EventType.DIES;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(le, SubactionType.NO_DROPS))
			{
				event.getDrops().clear();
				event.setDroppedExp(0);
			}
			else
			{
				if (Data.hasData(le, SubactionType.NO_DROPPED_ITEMS)) event.getDrops().clear();
				if (Data.hasData(le, SubactionType.NO_DROPPED_EXP)) event.setDroppedExp(0);
			}
		}
		
		if (events.containsKey(et))
		{
			Entity e = le.getLastDamageCause().getEntity();
			LivingEntity attacker = e instanceof LivingEntity ? (LivingEntity)e : null;
			EventValues ev = new EventValues(event, et, le, attacker, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		
		if (!check_before)
		{
			if (Data.hasData(le, SubactionType.NO_DROPS))
			{
				event.getDrops().clear();
				event.setDroppedExp(0);
			}
			else
			{
				if (Data.hasData(le, SubactionType.NO_DROPPED_ITEMS)) event.getDrops().clear();
				if (Data.hasData(le, SubactionType.NO_DROPPED_EXP)) event.setDroppedExp(0);
			}
		}
	}
	
	@EventHandler
	public void playerDies(PlayerDeathEvent event)
	{		
		if (ignoreWorld(event.getEntity().getWorld())) return;

		Player p = event.getEntity();
		EventType et = EventType.PLAYER_DIES;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(p, SubactionType.NO_DROPS))
			{
				event.getDrops().clear();
				event.setDroppedExp(0);
			}
			else
			{
				if (Data.hasData(p, SubactionType.NO_DROPPED_ITEMS)) event.getDrops().clear();
				if (Data.hasData(p, SubactionType.NO_DROPPED_EXP)) event.setDroppedExp(0);
			}
		}
		
		if (events.containsKey(et))
		{			
			Entity e = p.getLastDamageCause().getEntity();
			LivingEntity attacker = e instanceof LivingEntity ? (LivingEntity)e : null;			
			EventValues ev = new EventValues(event, et, p, attacker, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		
		if (!check_before)
		{
			if (Data.hasData(p, SubactionType.NO_DROPS))
			{
				event.getDrops().clear();
				event.setDroppedExp(0);
			}
			else
			{
				if (Data.hasData(p, SubactionType.NO_DROPPED_ITEMS)) event.getDrops().clear();
				if (Data.hasData(p, SubactionType.NO_DROPPED_EXP)) event.setDroppedExp(0);
			}
		}
	}
	
	@EventHandler
	public void dusk(DuskEvent event)
	{
		EventType et = EventType.DUSK;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void dyed(SheepDyeWoolEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.DYED;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_DYED)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_DYED)) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void creeperEvolves(CreeperPowerEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.EVOLVES;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_EVOLVE)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_EVOLVE)) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void pigEvolves(PigZapEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.EVOLVES;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_EVOLVE)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_EVOLVE)) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void entersArea(PlayerEnterAreaEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.ENTERS_AREA;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getPlayer(), null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void explodes(EntityExplodeEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		Entity entity = event.getEntity();		
		if (entity == null) return;		
		if (entity instanceof Fireball) entity = ((Fireball)entity).getShooter();
		if (!(entity instanceof LivingEntity)) return;

		LivingEntity le = (LivingEntity)entity;
		EventType et = EventType.EXPLODES;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(le, SubactionType.FRIENDLY)) event.setCancelled(true);
			
			if (!event.isCancelled())//TODO fix
			{			
				if (Data.hasData(le, SubactionType.NO_DESTROY_BLOCKS)) event.blockList().clear();
				else
				{
					if (Data.hasData(le, SubactionType.EXPLOSION_SIZE))
					{
						Integer size = (Integer)Data.getData(le, SubactionType.EXPLOSION_SIZE);
						if (size != null)
						{
							Location loc = event.getLocation();
							if (Data.hasData(le, SubactionType.FIERY_EXPLOSION)) loc.getWorld().createExplosion(loc, size, true);
							else loc.getWorld().createExplosion(loc, size);
						}
					}
				}
			}
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
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
	}
	
	@EventHandler
	public void growsWool(SheepRegrowWoolEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.GROWS_WOOL;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_GROW_WOOL)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_GROW_WOOL)) event.setCancelled(true);
		}		
	}
	
	@EventHandler
	public void heals(EntityRegainHealthEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();
		EventType et = EventType.HEALS;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(le, SubactionType.NO_HEAL)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(le, SubactionType.NO_HEAL)) event.setCancelled(true);
		}		
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
			EventValues ev = new EventValues(event, et, (LivingEntity)event.getEntity(), le, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void projectileHit(LivingEntityHitByProjectileEvent event) throws XPathExpressionException
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.HIT_BY_PROJECTILE;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getEntity(), event.getAttacker(), event.getProjectile(), null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void hourChange(HourChangeEvent event)
	{
		EventType et = EventType.HOUR_CHANGE;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void inArea(PlayerInAreaEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.IN_AREA;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getPlayer(), null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void playerJoins(PlayerJoinEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		Player p = event.getPlayer();
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, SubactionType.NAME, p.getName());
		
		EventType et = EventType.PLAYER_JOINS;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, p, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void leaves(PlayerLeaveLivingEntityEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.LEAVES;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getEntity(), event.getPlayer(), null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void leavesArea(PlayerLeaveAreaEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.LEAVES_AREA;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getPlayer(), null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void midday(MiddayEvent event)
	{
		EventType et = EventType.MIDDAY;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void midnight(MidnightEvent event)
	{
		EventType et = EventType.MIDNIGHT;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void near(PlayerNearLivingEntityEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.NEAR;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, event.getEntity(), event.getPlayer(), null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void night(NightEvent event)
	{
		EventType et = EventType.NIGHT;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void picksUpItem(PlayerPickupItemEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		EventType et = EventType.PICKS_UP_ITEM;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(event.getPlayer(), SubactionType.NO_PICK_UP_ITEMS)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, event.getPlayer(), null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(event.getPlayer(), SubactionType.NO_PICK_UP_ITEMS)) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void mobSheared(PlayerShearEntityEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity le = (LivingEntity)event.getEntity();
		EventType et = EventType.SHEARED;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(le, SubactionType.NO_SHEARING)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(le, SubactionType.NO_SHEARING)) event.setCancelled(true);
		}
	}
	
 	public void setMobName(String name)
	{
		spawn_reason = "SPAWNED";
		mob_name = name;
	}
 	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void spawns(CreatureSpawnEvent event)
	{//TODO affected here?
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.SPAWNS;
		
		//TODO remove!!!
		if (!le.getType().equals(EntityType.PIG)) return;
		
		if (spawn_reason == null) spawn_reason = event.getSpawnReason().toString();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(MParam.SPAWN_REASON.toString(), spawn_reason);
		if (mob_name != null) data.put(SubactionType.NAME.toString(), mob_name);
		le.setMetadata("mobs_data", new FixedMetadataValue(Mobs.getPlugin(), data));
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		mob_name = null;
		spawn_reason = null;
	}
	
	@EventHandler
	public void playerRespawns(PlayerRespawnEvent event)
	{
		if (ignoreWorld(event.getPlayer().getWorld())) return;
		
		Player p = event.getPlayer();
		Data.putData(p, MParam.SPAWN_REASON, "NATURAL");
		Data.putData(p, SubactionType.NAME, p.getName());		
		
		EventType et = EventType.PLAYER_SPAWNS;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, p, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
	}
	
	@EventHandler
	public void mobSplits(SlimeSplitEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		LivingEntity le = event.getEntity();
		EventType et = EventType.SPLITS;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			Integer i = (Integer)Data.getData(event.getEntity(), SubactionType.SPLIT_INTO);
			if (i == null) return;
			if (i == 0) event.setCancelled(true); else event.setCount(i);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			Integer i = (Integer)Data.getData(event.getEntity(), SubactionType.SPLIT_INTO);
			if (i == null) return;
			if (i == 0) event.setCancelled(true); else event.setCount(i);
		}
	}
	
	@EventHandler
	public void mobTamed(EntityTameEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		
		EventType et = EventType.TAMED;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_TAMING)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, event.getEntity(), null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(event.getEntity(), SubactionType.NO_TAMING)) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void targets(EntityTargetLivingEntityEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;		
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity le = (LivingEntity)event.getEntity();
		EventType et = EventType.TARGETS; //TODO targeted event?
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(le, SubactionType.FRIENDLY)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(le, SubactionType.FRIENDLY)) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void mobTeleports(EntityTeleportEvent event)
	{
		if (ignoreWorld(event.getEntity().getWorld())) return;
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity le = (LivingEntity)event.getEntity();
		EventType et = EventType.TELEPORTS;
		
		boolean check_before = Mobs.checkBefore(et);
		if (check_before)
		{
			if (Data.hasData(le, SubactionType.NO_TELEPORT)) event.setCancelled(true);
		}
		
		if (events.containsKey(et))
		{						
			EventValues ev = new EventValues(event, et, le, null, null, null);
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
		if (event.isCancelled()) return;
		
		if (!check_before)
		{
			if (Data.hasData(le, SubactionType.NO_TELEPORT)) event.setCancelled(true);
		}
	}
		
// timer stuff
	
	@EventHandler
	public void timerTick(MobsTimerTickEvent event)
	{
		EventType et = EventType.TIMER;		
		if (events.containsKey(et))
		{
			EventValues ev = new EventValues(event, et, null, null, null, event.getTimer());
			for (MobsAction ma : events.get(et)) ma.performActions(ev);
		}
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
				
				Integer life = (Integer)Data.getData(le, SubactionType.MAX_LIFE);
				if (life == null) continue;
				life--;
				if (life < 0) le.remove();	
				else Data.putData(le, SubactionType.MAX_LIFE, life);
			}
		}
	}
}