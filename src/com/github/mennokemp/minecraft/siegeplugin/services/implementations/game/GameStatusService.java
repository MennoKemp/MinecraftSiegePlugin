package com.github.mennokemp.minecraft.siegeplugin.services.implementations.game;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.minecraft.siegeplugin.domain.game.CapturePoint;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameEvent;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameStatus;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.game.IGameStatusDao;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStatusService;

public class GameStatusService implements IGameStatusService
{
	private final IGameStatusDao gameStatusDao;
	
	private final IGameStateService gameStateService;
	
	public GameStatusService(IGameStatusDao gameStatusDao, IGameStateService gameStateService, Scoreboard scoreboard)
	{
		this.gameStatusDao = gameStatusDao;
		
		this.gameStateService = gameStateService;
		gameStateService.registerGameStateListener(this);
		gameStateService.registerGameEventListener(this);
	}

	@Override
	public void onGameStateChanged()
	{		
		gameStatusDao.clear();
		gameStatusDao.setDisplaySlot(DisplaySlot.SIDEBAR);
	
		updateCapturePoints();
		gameStatusDao.setValue(GameStatus.Lives, gameStateService.getGameState(GameState.Lives));
		gameStatusDao.setValue(GameStatus.Time, gameStateService.getGameState(GameState.Time));
	}

	@Override
	public void OnGameEvent(GameEvent gameEvent)
	{
		if(gameEvent == GameEvent.GameStarting)
			gameStatusDao.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	private void updateCapturePoints()
	{
		for(CapturePoint capturePoint : CapturePoint.class.getEnumConstants())
		{
			if(gameStateService.getGameState(GameState.getCapturedState(capturePoint)) == 1)
				gameStatusDao.setValue(GameStatus.fromCapturePoint(capturePoint), 0);
			
			if(gameStateService.getGameState(GameState.getCapturingState(capturePoint)) == 1)
			{
				int countdown = gameStateService.getGameState(GameState.getCountdownState(capturePoint));
				gameStatusDao.setValue(GameStatus.fromCapturePoint(capturePoint), countdown);
			}
		}
	}


}
