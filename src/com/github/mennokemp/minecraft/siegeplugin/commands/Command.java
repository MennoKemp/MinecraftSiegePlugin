package com.github.mennokemp.minecraft.siegeplugin.commands;

import java.util.List;

import org.bukkit.command.CommandExecutor;

import com.github.mennokemp.minecraft.pluginhelpers.CommandBase;
import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;

public abstract class Command extends CommandBase implements CommandExecutor
{
	private IGameStateService gameStateService;
	
	public Command(IGameStateService gameStateService)
	{
		this.gameStateService = gameStateService;
	}
	
	@Override
	protected Result canExecute(String[] args)
	{
		GamePhase gamePhase = gameStateService.getGamePhase();
		
		if(!getValidGamePhases().contains(gamePhase))
			return Result.failure("Command not valid during " + gamePhase);
		
		return checkConditions(args);
	}
	
	protected abstract List<GamePhase> getValidGamePhases();
	
	protected abstract Result checkConditions(String[] args);
}
