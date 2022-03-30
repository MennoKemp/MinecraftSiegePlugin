package com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game;

import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameSetting;

public interface ISettingService
{
	int getSetting(GameSetting gameSetting);
	
	void setSetting(GameSetting gameSetting, int value);
	
	void showSettings();
}
