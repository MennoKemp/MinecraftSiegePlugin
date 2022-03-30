package com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.players.IClassDao;

public class AssassinService extends DamageService
{
	public AssassinService(IClassDao classDao)
	{
		super(classDao, PlayerClass.Assassin);
	}
}
