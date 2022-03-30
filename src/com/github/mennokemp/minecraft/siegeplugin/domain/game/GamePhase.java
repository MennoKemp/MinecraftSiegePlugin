package com.github.mennokemp.minecraft.siegeplugin.domain.game;

import java.util.HashMap;
import java.util.Map;

public enum GamePhase 
{
	Lobby(0),
	Preparation(1),
	InProcess(2),
	PostGame(3);
	
    private int value;
    private static Map<Integer, GamePhase> map = new HashMap<Integer, GamePhase>();

    private GamePhase(int value) 
    {
        this.value = value;
    }

    static 
    {
        for (GamePhase gamePhase : GamePhase.values()) 
        	map.put(gamePhase.value, gamePhase);
    }

    public static GamePhase fromValue(int gamePhase) 
    {
        return (GamePhase)map.get(gamePhase);
    }

    public int getValue() 
    {
        return value;
    }
}
