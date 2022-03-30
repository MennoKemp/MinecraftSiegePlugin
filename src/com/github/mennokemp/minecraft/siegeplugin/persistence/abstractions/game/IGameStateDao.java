package com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.game;

import com.github.mennokemp.minecraft.pluginhelpers.persistence.abstractions.IScoreboardDao;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameState;

public interface IGameStateDao extends IScoreboardDao<GameState>
{
	public GamePhase getGamePhase();
	
	public void setGamePhase(GamePhase gamePhase);
}