package com.github.mennokemp.minecraft.siegeplugin.services.implementations;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.IServerService;

public class ServerService implements IServerService
{
	@Override
	public Player getPlayer(String name) 
	{
		return Bukkit.getPlayer(name);
	}

	@Override
	public Set<Player> getPlayers() 
	{
		return new HashSet<Player>(Bukkit.getOnlinePlayers());
	}

	@Override
	public Set<Player> getPlayers(GameMode gameMode) 
	{
		Set<Player> players = new HashSet<Player>();
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(player.getGameMode() == gameMode)
				players.add(player);
		}
		
		return players;
	}

	@Override
	public Set<Player> getPlayers(Set<String> playerNames) 
	{
		Set<Player> players = new HashSet<Player>();
		
		for(String playerName : playerNames)
		{
			Player player = Bukkit.getServer().getPlayer(playerName); 
			
			if(player != null)
				players.add(player);
		}
		
		return players;
	}
}