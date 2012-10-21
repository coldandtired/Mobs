package me.coldandtired.mobs.listeners;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

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
		
		Integer i = (Integer)Data.getData(le, MParam.HP);
		if (i != null)
		{
			i -= event.getOrig_event().getDamage();
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