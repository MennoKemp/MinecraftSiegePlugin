package com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.players.IClassDao;

public class MageService extends SupportService
{
	public MageService(IClassDao classDao)
	{
		super(classDao, PlayerClass.Mage);
	}
}
