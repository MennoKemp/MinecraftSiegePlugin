package com.github.mennokemp.minecraft.siegeplugin.commands.players;

import java.util.Arrays;
import java.util.List;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.commands.Command;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.IPlayerService;

public class RandomizeTeamsCommand extends Command
{
	private final IPlayerService playerService;
	
	public RandomizeTeamsCommand(IGameStateService gameStateService, IPlayerService playerService)
	{
		super(gameStateService);
		
		this.playerService = playerService;
	}
	
	@Override
	protected List<GamePhase> getValidGamePhases()
	{
		return Arrays.asList(GamePhase.Lobby);
	}

	@Override
	protected Result checkConditions(String[] args)
	{
		return Result.success("Can execute command.");
	}

	@Override
	protected Result onCommand(String[] args)
	{
		return args.length == 1
			? playerService.randomizeTeams(Side.valueOf(args[0]))
			: playerService.randomizeTeams(Side.Any);	
	}
}
