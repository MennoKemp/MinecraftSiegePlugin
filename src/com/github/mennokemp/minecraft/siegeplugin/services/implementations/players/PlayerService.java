package com.github.mennokemp.minecraft.siegeplugin.services.implementations.players;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.pluginhelpers.TaskRunner;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameEvent;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameSetting;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.IMessageDao;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.IServerService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.ITriggerListener;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.IWorldService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.ISettingService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.IPlayerService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.IPlayerClassService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes.SupportService;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerService implements IPlayerService
{
	private static final String ClassSetting = "Class";
	private static final String RespawnObjective = "Respawn";
	
	private static final String SuicideMessage = " chose the easy way out";
	
	private static final Random Random = new Random();
	
	private final IMessageDao messageDao;
	
	private final IPlayerClassService playerClassService;
	private final ISettingService settingService;
	private final IGameStateService gameStateService;
	private final IServerService serverService;
	private final IWorldService worldService;
	
	private final Plugin plugin;
	private final Scoreboard scoreboard;
		
	private final Set<ITriggerListener> triggerListeners = new HashSet<>();

	private TaskRunner respawnTask;
	
	public PlayerService(
			IMessageDao messageDao,
			IPlayerClassService playerClassService,
			ISettingService settingService,
			IGameStateService gameStateService, 
			IServerService serverService, 
			IWorldService worldService,
			Plugin plugin,
			Scoreboard scoreboard)
	{
		this.messageDao = messageDao;
		
		this.playerClassService = playerClassService;
		this.settingService = settingService;
		this.gameStateService = gameStateService;
		this.serverService = serverService;
		this.worldService = worldService;
		
		this.plugin = plugin;
		this.scoreboard = scoreboard;
		
		gameStateService.registerGameEventListener(this);
		
		if(gameStateService.getGamePhase() == GamePhase.InProcess)
			StartRespawnTask();
	}
	
	public static int getScore(Player player, String objective)
	{
		return player.getScoreboard()
			.getObjective(objective)
			.getScore(player.getName())
			.getScore();
	}
	
	public static void setScore(Player player, String objective, int score)
	{
		player.getScoreboard()
			.getObjective(objective)
			.getScore(player.getName())
			.setScore(score);
	}
	
	public static PlayerClass getPlayerClass(Player player)
	{
		int value = getScore(player, ClassSetting);
		
		return value == -1
			? null
			: PlayerClass.fromValue(value);
	}
	
	public static void setPlayerClass(Player player, PlayerClass playerClass)
	{
		int value = playerClass == null
			? -1
			: playerClass.getValue();
		
		setScore(player, ClassSetting, value);
	}
	
	public static Side getSide(Player player)
	{
		return Side.valueOf(getTeam(player).getName());
	}
	
	public static Team getTeam(Player player)
	{
		for(Team team : player.getScoreboard().getTeams())
		{
			if(team.getEntries().contains(player.getName()))
				return team;
		}

		return null;
	}
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) 
    {
        Player player = event.getPlayer();
     
        switch(gameStateService.getGamePhase())
        {
	        case Lobby:
	        {
	        	clearPlayer(player, GameMode.ADVENTURE);
	        	player.teleport(worldService.getSpawnPoint(Side.Any));
	        	
	        	break;
	        }
	        case InProcess:
	        {
	        	if(getPlayerClass(player) == null && player.getGameMode() == GameMode.ADVENTURE)
	        		playerClassService.showClassSection(player);
	        	
	        	break;
	        }
	        default:
	        {
	        	player.sendMessage(ChatColor.DARK_GREEN + "Welcome back " + player.getName() + "!");
	        	break;
	        }
        }
    }
		
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if(gameStateService.getGamePhase() != GamePhase.InProcess)
			return;
						
		Player player = event.getEntity();
		Location deathLocation = player.getLocation();
		clearPlayer(player, GameMode.SPECTATOR);
		player.setGameMode(GameMode.SPECTATOR);
		setPlayerClass(player, null);
		Side side = getSide(player);
		
		if(side == Side.Attackers)
			gameStateService.removeLife();
		
		EntityDamageEvent damageCause = player.getLastDamageCause();
		if(damageCause != null && damageCause.getCause() == DamageCause.CUSTOM)
			event.setDeathMessage(getTeam(player).getColor() + player.getName() + ChatColor.WHITE + SuicideMessage);
		
		int resapwnTime = side == Side.Attackers
			? settingService.getSetting(GameSetting.RespawnAttackers)
			: settingService.getSetting(GameSetting.RespawnDefenders);
		setScore(player, RespawnObjective, resapwnTime);
		
		new TaskRunner(plugin, () -> player.teleport(deathLocation), 1, true).run();
	}
		
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event)
	{
		playerClassService.onItemConsume(event);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			Player player = (Player)event.getEntity();
						
			if(event.getDamage() > player.getHealth())
			{
				event.setCancelled(true);
				String deathMessage = messageDao.getDeathMessage(event.getCause(), player, player);
				PlayerDeathEvent deathEvent = new PlayerDeathEvent(player, null, 0, deathMessage);
				onPlayerDeath(deathEvent);				
			}
		}
	}
	
	@EventHandler
	public void onPotionThrow(PotionSplashEvent event)
	{	
		event.setCancelled(true);
		
        ThrownPotion potion = event.getPotion();
        Player thrower = (Player)potion.getShooter();
        Side side = getSide(thrower);
        
        Location spellLocation = potion.getLocation();
        
        boolean getOpponents = true;

        do
        {
        	getOpponents = !getOpponents;
        	
        	for(Player player : getPlayers(side, getOpponents))
        	{
        		double distance = Math.min(
        				player.getLocation().distance(spellLocation),
        				player.getLocation().add(0, player.getHeight(), 0).distance(spellLocation));
        		
        		SupportService.getEffects(potion, getOpponents)
    	    		.stream()
    	    		.map(e -> SupportService.getEffect(e, distance))
    	    		.filter(e -> e != null)
    	    		.forEach(e -> player.addPotionEffect(e));
        	}
        }while(!getOpponents);
    }
	
//	@EventHandler
//	public void onBlockRedstone(BlockRedstoneEvent event)
//	{		
//		if(event.getBlock().getType() == Material.STONE_PRESSURE_PLATE)
//		{
//			
//		}
//	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		
		if(event.getAction() == Action.PHYSICAL)
		{
			return;
		}
		
		if(event.getClickedBlock() != null && event.getClickedBlock().getType().toString().endsWith("TRAPDOOR"))
			event.setCancelled(true);
		
		if(event.getItem() != null && event.getItem().getType() == Material.SPLASH_POTION)
			new TaskRunner(plugin, () -> playerClassService.onSpellCast(player, player.getInventory().getHeldItemSlot()), 1, true).run();
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		Inventory inventory = event.getInventory();
		Player player = (Player)event.getPlayer();
		
		if(inventory.getType() == InventoryType.CHEST && getPlayerClass(player) == null)
			new TaskRunner(plugin, () -> player.openInventory(inventory), 1, true).run();
	}

	@EventHandler
	public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event)
	{
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent event)
	{
		Player player = (Player)event.getWhoClicked();

		event.setCancelled(player.getGameMode() != GameMode.CREATIVE);
		
		if(player.getGameMode() == GameMode.ADVENTURE)
		{
			PlayerClass playerClass = getPlayerClass(player);
			ItemStack selector = event.getCurrentItem();
			
			if(playerClass == null && selector != null)
			{
				playerClass = playerClassService.equipPlayer(player, selector);
				spawnPlayer(player, playerClass);			
			}			
		}
	}
	
	@Override
	public void sendMessage(Side side, String message)
	{
		sendMessage(side, message, ChatColor.WHITE);
	}
    
	@Override
	public void sendMessage(Side side, String message, ChatColor color)
	{
		if(side == Side.Any)
		{
			sendMessage(Side.Attackers, message, color);
			sendMessage(Side.Defenders, message, color);
		}
		else
		{
			String[] texts = messageDao.getMessages(message, side);
			
			for(Player player : getPlayers(side))
			{
				if(texts.length == 2)
					player.sendTitle(color + texts[0], texts[1], 0, 40, 0);			
				else
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color + texts[0]));
			}
		}
	}

	@Override
	public void playSound(Side side, Sound sound)
	{
		playSound(side, sound, 1, 1);
	}
	
	@Override
	public void playSound(Side side, Sound sound, float volume, float pitch)
	{
		if(side == Side.Any)
		{
			playSound(Side.Attackers, sound);
			playSound(Side.Defenders, sound);
		}
		else
		{
			for(Player player : getPlayers(side))
				player.playSound(player, sound, SoundCategory.MASTER, volume, pitch);
		}
	}

	@Override
	public void OnGameEvent(GameEvent gameEvent)
	{		
		if(gameEvent == GameEvent.GameStarting)
			StartRespawnTask();
		else if(respawnTask != null)
			respawnTask.cancel();			
		
		switch(gameEvent)
		{
			case GamePreparing:
			{
				for(Player player : getPlayers(Side.Any))
				{
					clearPlayer(player, GameMode.ADVENTURE);
					player.teleport(worldService.getSpawnPoint(getSide(player)));
				}
				
				break;
			}
			case GameStarting:
			{
				for(Player player : getPlayers(Side.Any))
				{
					clearPlayer(player, GameMode.ADVENTURE);
					player.teleport(worldService.getSpawnPoint(getSide(player), true));
					playerClassService.showClassSection(player);
				}

				sendMessage(Side.Any, GameEvent.GameStarting.toString());
				
				break;
			}
			case AttackersWon:
				sendMessage(Side.Any, gameEvent.toString());
				break;
			default:
				break;
		}
	}
	
	@Override
	public Result spawnPlayer(Player player, PlayerClass playerClass)
	{
		getRespawnTimer(player).setScore(-1);
		
		if(playerClass == null)
		{
			clearPlayer(player, GameMode.ADVENTURE);
			player.teleport(worldService.getSpawnPoint(getSide(player), true));
			playerClassService.showClassSection(player);
			return Result.success("Sent " + player.getName() + " to class selection.");
		}
		else
		{
			clearPlayer(player, GameMode.ADVENTURE);
			playerClassService.equipPlayer(player, playerClass);		
			player.teleport(worldService.getSpawnPoint(getSide(player)));			
			return Result.success("Spawned " + player.getName() + " as " + playerClass);
		}
	}
	
	@Override
	public Result randomizeTeams(Side favorSide)
	{
		ClearTeams();
		
		List<Player> players = new ArrayList<>(serverService.getPlayers());
		
		Team largeTeam = favorSide == Side.Defenders
			? scoreboard.getTeam(Side.Defenders.toString())
			: scoreboard.getTeam(Side.Attackers.toString());
		
		Team smallTeam = favorSide == Side.Defenders
				? scoreboard.getTeam(Side.Attackers.toString())
				: scoreboard.getTeam(Side.Defenders.toString());
				
		while(!players.isEmpty()) 
		{
			largeTeam.addEntry(players.remove(Random.nextInt(players.size() + 1)).getName());
			
			if(!players.isEmpty())
				smallTeam.addEntry(players.remove(Random.nextInt(players.size() + 1)).getName());
		}
		
		return Result.success("Randomized teams.");
	}

	@Override
	public void registerTriggerListener(ITriggerListener listener)
	{
		triggerListeners.add(listener);
	}
	
	private void clearPlayer(Player player, GameMode gameMode)
    {
		playerClassService.equipPlayer(player, (PlayerClass)null);
    	player.setGameMode(gameMode);
        player.getInventory().clear();
        player.getEquipment().clear();
		player.setLevel(0);
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        
        for (PotionEffect effect : player.getActivePotionEffects())
        	player.removePotionEffect(effect.getType());
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false));
    }
		
	private Set<Player> getPlayers(Side side)
	{
		Set<Player> players = new HashSet<Player>();
		
		switch(side)
		{
			case Any:
			{
				players.addAll(getPlayers(Side.Attackers));
				players.addAll(getPlayers(Side.Defenders));
				
				break;
			}
			case Attackers:
			{
				players.addAll(serverService.getPlayers(scoreboard.getTeam(Side.Attackers.toString()).getEntries()));
				break;
			}
			case Defenders:
			{
				players.addAll(serverService.getPlayers(scoreboard.getTeam(Side.Defenders.toString()).getEntries()));
				break;
			}
		}
		
		return players;
	}
	
	private Set<Player> getPlayers(Side side, boolean getOpponents)
	{
		if(!getOpponents)
			return getPlayers(side);
		
		return side == Side.Attackers
				? getPlayers(Side.Defenders)
				: getPlayers(Side.Attackers);
	}
	
	private void ClearTeams() 
	{
		for(Team team : scoreboard.getTeams())
		{
			for(String entry : team.getEntries())
				team.removeEntry(entry);			
		}
	}
		
	private void StartRespawnTask()
	{
		respawnTask = new TaskRunner(plugin);
		respawnTask.setTask(() ->
		{				
			for(Player player : serverService.getPlayers(GameMode.SPECTATOR))
			{
				Score respawnTimer = getRespawnTimer(player); 
				
				if(getScore(player, RespawnObjective)<= 0)
					spawnPlayer(player, null);
				else
					respawnTimer.setScore(respawnTimer.getScore() - 1);
			}
		});
		respawnTask.runRepeatedly();
	}
	
	private static Score getRespawnTimer(Player player)
	{
		return player.getScoreboard().getObjective(RespawnObjective).getScore(player.getName());
	}
}
