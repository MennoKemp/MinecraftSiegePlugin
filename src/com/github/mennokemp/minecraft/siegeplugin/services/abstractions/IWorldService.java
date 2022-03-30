package com.github.mennokemp.minecraft.siegeplugin.services.abstractions;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Listener;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;

public interface IWorldService extends Listener
{
	Location getSpawnPoint(Side side);
	
	Location getSpawnPoint(Side side, boolean selectClass);
	
	Location getBlock(BlockFace face);
}