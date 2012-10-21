package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import me.coldandtired.mobs.Data;
import me.coldandtired.mobs.elements.Outcome;
import me.coldandtired.mobs.enums.MEvent;
import me.coldandtired.mobs.enums.MParam;

public class Heals_listener extends Base_listener
{
	public Heals_listener(List<Outcome> outcomes) 
	{
		super(outcomes);
	}

	@EventHandler
	public void heals(EntityRegainHealthEvent event)
	{
		if (!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity)event.getEntity();

		performActions(MEvent.HEALS, le, event);
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
}