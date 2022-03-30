package com.github.mennokemp.minecraft.siegeplugin.services.implementations.game;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;

import com.github.mennokemp.minecraft.pluginhelpers.FileReader;
import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.pluginhelpers.TaskRunner;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.CapturePoint;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameEvent;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameSetting;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.ICaptureService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.ISettingService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.IPlayerService;

public class CaptureService implements ICaptureService
{
	private static final Map<CapturePoint, Set<CapturePoint>> CaptureConditions = new HashMap<>();

	private static final String CaptureConditionsPath = "CaptureConditions.txt";
	
	private final ISettingService settingService;
	private final IGameStateService gameStateService;
	private final IPlayerService playerService;
	
	private final Plugin plugin;
	
	private Map<CapturePoint, TaskRunner> captureTasks = new HashMap<>();
	
	private TaskRunner clock;
	
	public CaptureService(ISettingService settingService, IGameStateService gameStateService, IPlayerService playerService, Plugin plugin)
	{
		this.settingService = settingService;
		this.gameStateService = gameStateService;
		this.playerService = playerService;
		
		this.plugin = plugin;
		
		loadCaptureConditions();
	}

	@Override
	public Result startCapture(CapturePoint capturePoint) 
	{
		Result checkResult = canCapture(capturePoint);
		
		if(!checkResult.isSuccessful())
			return checkResult;
		
		gameStateService.setGameState(GameState.getCapturingState(capturePoint), 1);
		
		int captureTime = settingService.getSetting(GameSetting.CaptureTime);
		GameState captureCountdown = GameState.getCountdownState(capturePoint);
		
		gameStateService.setGameState(captureCountdown, captureTime);
		
		TaskRunner captureTask = new TaskRunner(plugin);
		captureTask.setDelay(1);
		captureTask.setTask(() ->
		{
			int timeLeft = gameStateService.getGameState(captureCountdown) - 1;
			gameStateService.setGameState(captureCountdown, timeLeft);
			
			if(timeLeft == 0)
			{
				stopCapture(capturePoint);
				onCaptured(capturePoint);
			}
			else
			{
				playerService.playSound(Side.Any, Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON);
			}
		});
		
		captureTasks.put(capturePoint, captureTask);
		captureTask.runRepeatedly();
		
		playerService.sendMessage(Side.Any, "capturing_" + capturePoint);
		
		return Result.success("Capturing " + capturePoint);
	}

	@Override
	public Result stopCapture(CapturePoint capturePoint)
	{
		if(!captureTasks.containsKey(capturePoint))
			return Result.success(capturePoint + " is not being captured.");
		
		gameStateService.setGameState(GameState.getCapturingState(capturePoint), 0);
		captureTasks.remove(capturePoint).cancel();
		
		return Result.success("Stopped capturing " + capturePoint);
	}

	private Result canCapture(CapturePoint capturePoint)
	{
		if(gameStateService.getGameState(GameState.getCapturedState(capturePoint)) == 1)
			return Result.failure(capturePoint + " has already been captured.");
		
		if(captureTasks.containsKey(capturePoint))
			return Result.failure(capturePoint + " is being captured.");
		
		for(CapturePoint condition : CaptureConditions.get(capturePoint))
		{
			if(gameStateService.getGameState(GameState.getCapturedState(condition)) != 1)
				return Result.failure(condition + " must be captured first.");
		}
		
		return Result.success(capturePoint + " can be captured.");
		
	}

	private void onCaptured(CapturePoint capturePoint)
	{
		gameStateService.setGameState(GameState.getCapturedState(capturePoint), 1);
		gameStateService.addGameState(GameState.PointsCaptured, 1);
		playerService.sendMessage(Side.Any, "captured_" + capturePoint);
		playerService.playSound(Side.Any, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST);
		gameStateService.addLives(settingService.getSetting(GameSetting.BonusLives));
		gameStateService.addTime(settingService.getSetting(GameSetting.BonusTime));
	}
	
	private void loadCaptureConditions()
	{
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(CaptureConditionsPath);
        
        try
        {
        	for(String line : new FileReader().readLines(resourceStream))
        	{
    			String[] buffer = line.split("\t");
        		
        		CapturePoint capturePoint = CapturePoint.valueOf(buffer[0]);
        		Set<CapturePoint> conditions = new HashSet<CapturePoint>();
        		
        		for(int c = 1; c < buffer.length; c++)
        			conditions.add(CapturePoint.valueOf(buffer[c]));
        		
        		CaptureConditions.put(capturePoint, conditions);
        	}
        }
        catch(IOException exception)
        {
        	Bukkit.getLogger().severe("Could not load capture conditions. " + exception);
        }
	}

	@Override
	public void OnGameEvent(GameEvent gameEvent)
	{
		if(clock != null)
			clock.cancel();
		
		if(gameEvent == GameEvent.GameStarting)
			resetClock();
	}
	
	private void resetClock()
	{
		clock = new TaskRunner(plugin);
		clock.setTask(() ->
		{
			gameStateService.reduceTime();
			
			if(gameStateService.getGameState(GameState.Time) == 0)
				clock.cancel();
		});
		clock.runRepeatedly();
	}
}
