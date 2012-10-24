package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;
import me.coldandtired.mobs.events.Mob_damaged_event;

public class Damaged_listener extends Base_listener
{
	public Damaged_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void damaged(Mob_damaged_event event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		if (event.getOrig_event().getDamage() == 1000) return;
		LivingEntity le = (LivingEntity)event.getEntity();

		performActions(MEvent.DAMAGED, le, event);
		if (event.isCancelled())
		{
			event.getOrig_event().setCancelled(true);
			return;
		}
		
		int damage = event.getOrig_event().getDamage();		
			
		switch (event.getOrig_event().getCause())
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
				if (event.getOrig_event() instanceof EntityDamageByEntityEvent)
				{
					Entity damager = ((EntityDamageByEntityEvent)event.getOrig_event()).getDamager();
					damage = Data.adjustInt(damager, MParam.ATTACK, damage);
				}
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
		event.getOrig_event().setDamage(damage);
		Integer i = (Integer)Data.getData(le, MParam.HP);
		if (i != null)
		{
			i -= damage;
			int max_hp = (Integer)Data.getData(le, MParam.MAX_HP);
			if (event.getEntity() instanceof Player) le.setHealth((int) (20.0 * (i * 1.0 / max_hp)));
			else event.getOrig_event().setDamage(0);
			if (i <= 0)
			{
				le.setHealth(0);
				return;
			}
			Data.putData(le, MParam.HP, i);
		}		
	}
}