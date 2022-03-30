package com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameEventListener;

public interface IPlayerClassService extends IGameEventListener
{
	void showClassSection(Player player);
	
	PlayerClass equipPlayer(Player player, ItemStack selector);
	
	void equipPlayer(Player player, PlayerClass playerClass);

	void onItemConsume(PlayerItemConsumeEvent event);
	
	void onSpellCast(Player player, int hotbarSlot);
	
	Result refillPlayer(Player player);
}