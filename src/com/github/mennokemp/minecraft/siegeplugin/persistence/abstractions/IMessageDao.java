package com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;

public interface IMessageDao
{
	String[] getMessages(String name, Side side);
		
	String getDeathMessage(DamageCause cause, Player killed, Player killer);
}
