package com.github.mennokemp.minecraft.siegeplugin.commands.players;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.commands.Command;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.IPlayerClassService;

public class RefillPlayerCommand extends Command
{	
	private final IPlayerClassService playerClassService;
	
	public RefillPlayerCommand(IGameStateService gameStateService, IPlayerClassService playerClassService)
	{
		super(gameStateService);
		
		this.playerClassService = playerClassService;
	}

	@Override
	protected List<GamePhase> getValidGamePhases()
	{
		return Arrays.asList(GamePhase.InProcess);
	}

	@Override
	protected Result checkConditions(String[] args)
	{
		return sender instanceof Player
			? Result.success("Can execute command.")
			: Result.failure("Can only be executed by a player.");
	}

	@Override
	protected Result onCommand(String[] args)
	{
		return playerClassService.refillPlayer((Player)sender);
	}
}
