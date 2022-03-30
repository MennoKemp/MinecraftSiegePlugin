package com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;

public interface IClassService
{
	PlayerClass getPlayerClass();
	
	Side getSide();
	
	ItemStack getSelector();
	
	void equipPlayer(Player player, boolean reequip);
	
	void setEffects(Player player);
}