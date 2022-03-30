package com.github.mennokemp.minecraft.siegeplugin.commands.capture;

import java.util.Arrays;
import java.util.List;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.commands.Command;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.CapturePoint;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.ICaptureService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;

public class StopCaptureCommand extends Command
{
	private ICaptureService captureService;
	
	public StopCaptureCommand(IGameStateService gameStateService, ICaptureService captureService)
	{
		super(gameStateService);
		
		this.captureService = captureService;
	}

	@Override
	public List<GamePhase> getValidGamePhases()
	{
		return Arrays.asList(GamePhase.InProcess);
	}
	
	@Override
	protected Result checkConditions(String[] args)
	{		
		if(args.length != 1)
			return Result.failure("Incorrect format. Use: " + command.getUsage());
		
		return Result.success("Can execute command.");
	}

	@Override
	protected Result onCommand(String[] args)
	{
		return captureService.stopCapture(CapturePoint.valueOf(args[0]));
	}
}