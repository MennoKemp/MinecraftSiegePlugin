package com.github.mennokemp.minecraft.siegeplugin.domain.game;

public enum GameStatus
{
	Village,
	Walls,
	GreatHall,
	Dungeon,
	Garden,
	Lives,
	Time;
	
	public static GameStatus fromCapturePoint(CapturePoint capturePoint)
	{
		return GameStatus.valueOf(capturePoint.toString());
	}
}
