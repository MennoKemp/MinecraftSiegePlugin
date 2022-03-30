package com.github.mennokemp.minecraft.siegeplugin.commands;

import java.util.Arrays;
import java.util.List;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;

public class ReloadConfigCommand extends Command
{
	private Runnable reloadAction;
	
	public ReloadConfigCommand(IGameStateService gameStateService, Runnable reloadAction)
	{
		super(gameStateService);
		
		this.reloadAction = reloadAction;
	}

	@Override
	protected List<GamePhase> getValidGamePhases()
	{
		return Arrays.asList(GamePhase.values());
	}

	@Override
	protected Result checkConditions(String[] args)
	{
		return Result.success("Can execute command.");
	}

	@Override
	protected Result onCommand(String[] args)
	{
		reloadAction.run();
		return Result.success("Reloaded plugin");
	}
}
