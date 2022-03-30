package com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game;

import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameEvent;

public interface IGameEventListener
{
	void OnGameEvent(GameEvent gameEvent);
}
