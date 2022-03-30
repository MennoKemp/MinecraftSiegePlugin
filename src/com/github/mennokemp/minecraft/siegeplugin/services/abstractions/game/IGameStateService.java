package com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameOverReason;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameState;

public interface IGameStateService
{
	int getGameState(GameState gameState);
	
	void setGameState(GameState gameState, int value);
	
	void addGameState(GameState gameState, int value);
	
	void removeGameState(GameState gameState, int value);
	
	void endGame(GameOverReason reason);
	
	GamePhase getGamePhase();
	
	void registerGameStateListener(IGameStateListener listener);
	
	void registerGameEventListener(IGameEventListener listener);
	
	void addTime(int amount);
	
	void reduceTime();
	
	void addLives(int amount);
	
	void removeLife();
	
	Result resetGame();
	
	Result prepareGame();
	
	Result startGame();
	
	Result stopGame();
}
