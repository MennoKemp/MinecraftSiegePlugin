package com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions;

import java.util.Map;

import org.bukkit.Location;

import com.github.mennokemp.minecraft.siegeplugin.domain.game.CapturePoint;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.RefillPoint;

public interface IWorldDao
{
	Map<CapturePoint, Location> getCapturePoints();
	
	Map<RefillPoint, Location> getRefillPoints();
}
