package com.github.mennokemp.minecraft.siegeplugin.domain.game;

public enum GameState 
{
	CapturedDungeon,
	CapturedGarden,
	CapturedGreatHall,
	CapturedThrone,
	CapturedVillage,
	CapturedWalls,
	CapturingDungeon,
	CapturingGarden,
	CapturingGreatHall,
	CapturingThrone,
	CapturingVillage,
	CapturingWalls,
	CountdownDungeon,
	CountdownGarden,
	CountdownGreatHall,
	CountdownThrone,
	CountdownVillage,
	CountdownWalls,
	Lives,
	Phase,
	PointsCaptured,
	Time;
	
	public static GameState getCapturedState(CapturePoint capturePoint)
	{
		return switch(capturePoint)
		{
			case Dungeon -> CapturedDungeon;
			case Garden -> CapturedGarden;
			case GreatHall -> CapturedGreatHall;
			case Throne -> CapturedThrone;
			case Village -> CapturedVillage;
			case Walls -> CapturedWalls;
		};
	}
	
	public static GameState getCapturingState(CapturePoint capturePoint)
	{
		return switch(capturePoint)
		{
			case Dungeon -> CapturingDungeon;
			case Garden -> CapturingGarden;
			case GreatHall -> CapturingGreatHall;
			case Throne -> CapturingThrone;
			case Village -> CapturingVillage;
			case Walls -> CapturingWalls;
		};
	}
	
	public static GameState getCountdownState(CapturePoint capturePoint)
	{
		return switch(capturePoint)
		{
			case Dungeon -> CountdownDungeon;
			case Garden -> CountdownGarden;
			case GreatHall -> CountdownGreatHall;
			case Throne -> CountdownThrone;
			case Village -> CountdownVillage;
			case Walls -> CountdownWalls;
		};
	}
}