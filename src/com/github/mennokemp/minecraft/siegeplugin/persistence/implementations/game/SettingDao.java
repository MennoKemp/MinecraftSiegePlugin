package com.github.mennokemp.minecraft.siegeplugin.persistence.implementations.game;

import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.minecraft.pluginhelpers.persistence.implementations.ScoreboardDao;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameSetting;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.game.ISettingDao;

public class SettingDao extends ScoreboardDao<GameSetting> implements ISettingDao 
{
	private static String ObjectiveName = "Settings";	
	private static String ObjectiveDisplayName = "Settings";
	
	public SettingDao(Scoreboard scoreboard)
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
