package com.github.mennokemp.minecraft.siegeplugin.services.implementations.game;

import org.bukkit.scoreboard.DisplaySlot;

import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameSetting;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.game.ISettingDao;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.ISettingService;

public class SettingService implements ISettingService
{
	private final ISettingDao settingDao;
	
	public SettingService(ISettingDao settingDao)
	{
		this.settingDao = settingDao;
	}
	
	@Override
	public int getSetting(GameSetting gameSetting)
	{
		return settingDao.getValue(gameSetting);
	}

	@Override
	public void setSetting(GameSetting gameSetting, int value)
	{
		settingDao.setValue(gameSetting, value);
	}
	
	@Override
	public void showSettings()
	{
		settingDao.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
}
