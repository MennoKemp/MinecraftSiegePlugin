package com.github.mennokemp.minecraft.siegeplugin.commands.players;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.commands.Command;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.IPlayerService;

public class SpawnPlayerCommand extends Command
{
	private final IPlayerService playerService;
	
	public SpawnPlayerCommand(IGameStateService gameStateService, IPlayerService playerService)
	{
		super(gameStateService);
		
		this.playerService = playerService;
	}
	
	@Override
	public List<GamePhase> getValidGamePhases()
	{
		return Arrays.asList(GamePhase.InProcess);
	}

	@Override
	protected Result checkConditions(String[] args)
	{
		if(args.length > 1)
			return Result.failure("Incorrect format. Use: " + command.getUsage());
		
		return Result.success("Can execute command.");
	}

	@Override
	protected Result onCommand(String[] args)
	{
		PlayerClass playerClass = args.length == 1
				? PlayerClass.valueOf(args[0])
				: null;
		
		Player target = this.sender instanceof Player
			? (Player)this.sender
			: null; 
		
		return playerService.spawnPlayer(target, playerClass);
	}
}
