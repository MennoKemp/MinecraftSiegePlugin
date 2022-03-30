package com.github.mennokemp.minecraft.siegeplugin.commands.game;

import java.util.Arrays;
import java.util.List;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.commands.Command;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;

public class StopGameCommand extends Command
{
	private final IGameStateService gameStateService;
	
	public StopGameCommand(IGameStateService gameStateService)
	{
		super(gameStateService);
		
		this.gameStateService = gameStateService;
	}
	
	@Override
	public List<GamePhase> getValidGamePhases()
	{
		return Arrays.asList(GamePhase.InProcess);
	}

	@Override
	protected Result checkConditions(String[] args)
	{
		return Result.success("Can execute command.");
	}

	@Override
	protected Result onCommand(String[] args)
	{
		return gameStateService.stopGame();
	}
}
