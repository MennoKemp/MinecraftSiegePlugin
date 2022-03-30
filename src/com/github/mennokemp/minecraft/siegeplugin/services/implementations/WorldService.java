package com.github.mennokemp.minecraft.siegeplugin.services.implementations;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.mennokemp.minecraft.pluginhelpers.FileReader;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameState;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.SpawnPoint;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.IWorldService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;

public class WorldService implements IWorldService
{
	private static String SpawnPointsPath = "SpawnPoints.txt";
	private static String WorldName = "world";
	
	private final Map<SpawnPoint, Location> spawnPoints = new HashMap<>();
	
//	private final Map<CapturePoint, Location> capturePoints = new HashMap<>();
	
	private final IGameStateService gameStateService;
	
	private final World world;
	
	public WorldService(IGameStateService gameStateService)
	{
		this.gameStateService = gameStateService;
		
		world = Bukkit.getWorld(WorldName);
		
		loadSpawnPoints();
	}
	
	@EventHandler
    public void onDoorClick(PlayerInteractEvent e)
    {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE &&
				e.getClickedBlock() != null && 
				e.getClickedBlock().getType().name().endsWith("TRAPDOOR")) 
			e.setCancelled(true);
    }
	
	@Override
	public Location getSpawnPoint(Side side) 
	{
		return switch(gameStateService.getGamePhase())
		{
			case Preparation -> (side == Side.Attackers
					? spawnPoints.get(SpawnPoint.Camp)
					: spawnPoints.get(SpawnPoint.ThroneRoom));
			case InProcess -> switch(gameStateService.getGameState(GameState.PointsCaptured))
			{
				case 0 -> side == Side.Attackers
					? spawnPoints.get(SpawnPoint.Camp)
					: spawnPoints.get(SpawnPoint.Bridge);
				case 1 -> side == Side.Attackers
					? spawnPoints.get(SpawnPoint.Village)
					: spawnPoints.get(SpawnPoint.Barracks);
				default -> side == Side.Attackers
					? spawnPoints.get(SpawnPoint.Walls)
					: spawnPoints.get(SpawnPoint.RoyalChambers);
			};
			default -> spawnPoints.get(SpawnPoint.Lobby);
		};
	}

	@Override
	public Location getSpawnPoint(Side side, boolean selectClass)
	{
		return side == Side.Attackers
			? spawnPoints.get(SpawnPoint.AttackerClassSelection)
			: spawnPoints.get(SpawnPoint.DefenderClassSelection);
	}
	
	@Override
	public Location getBlock(BlockFace face)
	{
		return new Location(world, face.getModX(), face.getModY(), face.getModZ());
	}
	
	private void loadSpawnPoints()
	{
		InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(SpawnPointsPath);
        
        try
        {
        	for(String line : new FileReader().readLines(resourceStream))
        	{
    			String[] buffer = line.split("\t");
        		
    			SpawnPoint spawnPoint = SpawnPoint.valueOf(buffer[0]);
    			
    			String[] coordinates = buffer[1].split(" ");
    			
    			int x = Integer.valueOf(coordinates[0]);
    			int y = Integer.valueOf(coordinates[1]);
    			int z = Integer.valueOf(coordinates[2]);
    			int yaw = 0;
    			int pitch = 0;
    			
    			if(coordinates.length == 5)
    			{
    				yaw = Integer.valueOf(coordinates[3]);
    				pitch = Integer.valueOf(coordinates[4]);
    			}
    			
    			spawnPoints.put(spawnPoint, new Location(world, x, y, z, yaw, pitch));
        	}
        }
        catch(IOException exception)
        {
        	Bukkit.getLogger().severe("Could not load capture conditions. " + exception);
        }
	}
}
