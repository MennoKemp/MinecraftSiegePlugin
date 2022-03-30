package com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes;

import java.util.Map;

import org.bukkit.entity.Player;

public interface ISupportService extends IRefillService
{
	void reset(Player player);
	
	void onSpellCast(Player player, int hotbarSlot, Map<Integer, Integer> cooldowns);
	
	void update(Player player, Map<Integer, Integer> cooldowns, boolean countDown);
}
