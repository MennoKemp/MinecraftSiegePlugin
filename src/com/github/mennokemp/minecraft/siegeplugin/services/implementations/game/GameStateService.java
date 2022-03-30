package com.github.mennokemp.minecraft.siegeplugin.services.implementations.game;

import java.util.HashSet;
import java.util.Set;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameEvent;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameOverReason;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameSetting;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.game.IGameStateDao;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameEventListener;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateListener;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.ISettingService;

public class GameStateService implements IGameStateService
{
	private final Set<IGameEventListener> gameEventListeners = new HashSet<>();
	private final Set<IGameStateListener> gameStateListeners = new HashSet<>();
	
	private IGameStateDao gameStateDao;
	
	private ISettingService settingService;
	
	public GameStateService(IGameStateDao gameStateDao, ISettingService settingService)
	{
		this.gameStateDao = gameStateDao;
		
		this.settingService = settingService;
	}

	@Override
	public int getGameState(GameState gameState)
	{
		return gameStateDao.getValue(gameState);
	}

	@Override
	public void setGameState(GameState gameState, int value)
	{
		gameStateDao.setValue(gameState, value);
		
		for(IGameStateListener listener : gameStateListeners)
			listener.onGameStateChanged();
	}
	
	@Override
	public void addGameState(GameState gameState, int value)
	{
		gameStateDao.setValue(gameState, getGameState(gameState) + value);
	}
	
	@Override
	public void removeGameState(GameState gameState, int value)
	{
		gameStateDao.setValue(gameState, getGameState(gameState) - value);
	}

	@Override
	public GamePhase getGamePhase()
	{
		return gameStateDao.getGamePhase();
	}

	@Override
	public void registerGameStateListener(IGameStateListener listener)
	{
		gameStateListeners.add(listener);
	}
	
	@Override
	public void registerGameEventListener(IGameEventListener listener)
	{
		gameEventListeners.add(listener);
	}

	@Override
	public Result resetGame()
	{
		gameStateDao.setGamePhase(GamePhase.Lobby);
		gameStateDao.clear();
		
		settingService.showSettings();
		
		onGameEvent(GameEvent.GameResetting);
		
		return Result.success("Game reset.");
	}
	
	@Override
	public Result prepareGame()
	{
		if(gameStateDao.getGamePhase() != GamePhase.Lobby)
			resetGame();
		
		gameStateDao.setGamePhase(GamePhase.Preparation);
		onGameEvent(GameEvent.GamePreparing);
		
		return Result.success("Game prepared.");
	}

	@Override
	public Result startGame()
	{
		switch(getGamePhase())
		{
			case InProcess:
				return Result.failure("Game has already started.");
			case PostGame:
				resetGame();
				break;
			default:
				break;
		}
		
		gameStateDao.setValue(GameState.Lives, settingService.getSetting(GameSetting.Lives));
		gameStateDao.setValue(GameState.Time, settingService.getSetting(GameSetting.TimeLimit));
		
		gameStateDao.setGamePhase(GamePhase.InProcess);
		onGameEvent(GameEvent.GameStarting);
		
		return Result.success("Game started.");
	}

	@Override
	public Result stopGame()
	{
		if(getGamePhase() != GamePhase.InProcess)
			return Result.failure("Can only stop game when the phase is in progress.");
		
		gameStateDao.setGamePhase(GamePhase.PostGame);
		onGameEvent(GameEvent.GameStopping);
		
		return Result.success("Game stopped.");
	}
	
	private void onGameEvent(GameEvent gameEvent)
	{
		for(IGameEventListener listener : gameEventListeners)
			listener.OnGameEvent(gameEvent);
	}

	@Override
	public void addLives(int amount)
	{
		gameStateDao.setValue(GameState.Lives, gameStateDao.getValue(GameState.Lives) + amount);
	}

	@Override
	public void removeLife()
	{
		gameStateDao.setValue(GameState.Lives, gameStateDao.getValue(GameState.Lives) - 1);
	}

	@Override
	public void addTime(int amount)
	{
		gameStateDao.setValue(GameState.Time, gameStateDao.getValue(GameState.Time) + amount);
	}

	@Override
	public void endGame(GameOverReason reason)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reduceTime()
	{
		// TODO Auto-generated method stub
		
	}
}
