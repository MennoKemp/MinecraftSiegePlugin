package com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.ITriggerListener;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameEventListener;

public interface IPlayerService extends Listener, IGameEventListener
{
	void sendMessage(Side side, String message);

	void sendMessage(Side side, String message, ChatColor color);
	
	void playSound(Side side, Sound sound);
	
	void playSound(Side side, Sound sound, float volume, float pitch);
	
	Result spawnPlayer(Player player, PlayerClass playerClass);
	
	Result randomizeTeams(Side favorSide);
	
	void registerTriggerListener(ITriggerListener listener);
}
