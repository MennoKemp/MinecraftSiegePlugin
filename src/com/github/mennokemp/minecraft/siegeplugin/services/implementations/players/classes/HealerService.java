package com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.players.IClassDao;

public class HealerService extends SupportService
{
	public HealerService(IClassDao classDao)
	{
		super(classDao, PlayerClass.Healer);
	}
}
