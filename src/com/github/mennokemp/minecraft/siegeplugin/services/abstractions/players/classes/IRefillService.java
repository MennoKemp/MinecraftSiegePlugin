package com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes;

import org.bukkit.entity.Player;

public interface IRefillService extends IClassService
{
	public void refillPlayer(Player player);
}
