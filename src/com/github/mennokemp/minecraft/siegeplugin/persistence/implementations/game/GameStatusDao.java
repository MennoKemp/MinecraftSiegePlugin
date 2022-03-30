package com.github.mennokemp.minecraft.siegeplugin.persistence.implementations.game;

import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.minecraft.pluginhelpers.persistence.implementations.ScoreboardDao;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameStatus;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.game.IGameStatusDao;

public class GameStatusDao extends ScoreboardDao<GameStatus> implements IGameStatusDao
{
	private static String ObjectiveName = "Siege";	
	private static String ObjectiveDisplayName = "Siege";
	
	public GameStatusDao(Scoreboard scoreboard) 
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
}
