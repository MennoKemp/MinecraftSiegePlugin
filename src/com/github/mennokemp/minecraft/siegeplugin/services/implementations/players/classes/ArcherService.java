package com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.players.IClassDao;

public class ArcherService extends DamageService
{
	public ArcherService(IClassDao classDao)
	{
		super(classDao, PlayerClass.Archer);
	}

}
