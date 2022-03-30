package com.github.mennokemp.minecraft.siegeplugin.domain.players;

import java.util.HashMap;
import java.util.Map;

public enum PlayerClass
{
	Archer(0),
	Assassin(1),
	Commander(2),
	Healer(3),
	Knight(4),
	Mage(5),
	Necromancer(6),
	Sapper(7),
	Warrior(8);

	private int value;
    private static Map<Integer, PlayerClass> map = new HashMap<Integer, PlayerClass>();

    private PlayerClass(int value) 
    {
        this.value = value;
    }

    static 
    {
        for (PlayerClass playerClass : PlayerClass.values()) 
        	map.put(playerClass.value, playerClass);
    }

    public static PlayerClass fromValue(int playerClass) 
    {
        return (PlayerClass)map.get(playerClass);
    }

    public int getValue() 
    {
        return value;
    }
}
