package com.github.mennokemp.minecraft.siegeplugin.persistence.implementations.game;

import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.minecraft.pluginhelpers.persistence.implementations.ScoreboardDao;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.game.IGameStateDao;

public class GameStateDao extends ScoreboardDao<GameState> implements IGameStateDao
{
	private static String ObjectiveName = "Game";	
	private static String ObjectiveDisplayName = "Game";
	
	public GameStateDao(Scoreboard scoreboard)
	{
		super(scoreboard);
	}

	@Override
	protected String getObjectiveName() 
	{
		return ObjectiveName;
	}

	@Override
	protected String getObjectiveDisplayName() 
	{
		return ObjectiveDisplayName;
	}

	@Override
	public GamePhase getGamePhase() 
	{
		return GamePhase.fromValue(getValue(GameState.Phase));
	}

	@Override
	public void setGamePhase(GamePhase gamePhase) 
	{
		setValue(GameState.Phase, gamePhase.getValue());
	}
}
