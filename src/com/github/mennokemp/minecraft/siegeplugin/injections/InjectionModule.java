package com.github.mennokemp.minecraft.siegeplugin.injections;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.minecraft.pluginhelpers.injections.InjectionModuleBase;
import com.github.mennokemp.minecraft.pluginhelpers.persistence.abstractions.IConfigurationDao;
import com.github.mennokemp.minecraft.pluginhelpers.persistence.abstractions.IReloadable;
import com.github.mennokemp.minecraft.pluginhelpers.persistence.implementations.ConfigurationDao;
import com.github.mennokemp.minecraft.siegeplugin.commands.ReloadConfigCommand;
import com.github.mennokemp.minecraft.siegeplugin.commands.capture.StartCaptureCommand;
import com.github.mennokemp.minecraft.siegeplugin.commands.capture.StopCaptureCommand;
import com.github.mennokemp.minecraft.siegeplugin.commands.game.PrepareGameCommand;
import com.github.mennokemp.minecraft.siegeplugin.commands.game.ResetGameCommand;
import com.github.mennokemp.minecraft.siegeplugin.commands.game.StartGameCommand;
import com.github.mennokemp.minecraft.siegeplugin.commands.game.StopGameCommand;
import com.github.mennokemp.minecraft.siegeplugin.commands.players.RandomizeTeamsCommand;
import com.github.mennokemp.minecraft.siegeplugin.commands.players.RefillPlayerCommand;
import com.github.mennokemp.minecraft.siegeplugin.commands.players.SpawnPlayerCommand;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.IMessageDao;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.game.IGameStateDao;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.game.IGameStatusDao;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.game.ISettingDao;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.players.IClassDao;
import com.github.mennokemp.minecraft.siegeplugin.persistence.implementations.MessageDao;
import com.github.mennokemp.minecraft.siegeplugin.persistence.implementations.game.GameStateDao;
import com.github.mennokemp.minecraft.siegeplugin.persistence.implementations.game.GameStatusDao;
import com.github.mennokemp.minecraft.siegeplugin.persistence.implementations.game.SettingDao;
import com.github.mennokemp.minecraft.siegeplugin.persistence.implementations.players.ClassDao;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.IServerService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.IWorldService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.ICaptureService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStatusService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.ISettingService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.IPlayerService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.IClassService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.IPlayerClassService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.ServerService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.WorldService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.game.CaptureService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.game.GameStateService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.game.GameStatusService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.game.SettingService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.PlayerService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes.ArcherService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes.AssassinService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes.HealerService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes.KnightService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes.MageService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes.PlayerClassService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes.WarriorService;

public class InjectionModule extends InjectionModuleBase 
{
	private final Plugin plugin;
	private final Scoreboard scoreboard;
	
	public InjectionModule(Plugin plugin, Scoreboard scoreboard)
	{
		this.plugin = plugin;
		this.scoreboard = scoreboard;
		
		registerBindings();
	}
	
	@Override
	protected void registerBindings() 
	{
		IConfigurationDao configurationDao = register(IConfigurationDao.class, new ConfigurationDao(plugin));
		IMessageDao messageDao = register(IMessageDao.class, new MessageDao(configurationDao));
		IClassDao classDao = register(IClassDao.class, new ClassDao(configurationDao));
		IGameStateDao gameStateDao = register(IGameStateDao.class, new GameStateDao(scoreboard));
		ISettingDao settingDao = register(ISettingDao.class, new SettingDao(scoreboard));
		IGameStatusDao gameStatusDao = register(IGameStatusDao.class, new GameStatusDao(scoreboard));
		
		Collection<IClassService> classServices = Arrays.asList(
				register(WarriorService.class, new WarriorService(classDao)),
				register(MageService.class, new MageService(classDao)),
				register(AssassinService.class, new AssassinService(classDao)),
				register(KnightService.class, new KnightService(classDao)),
				register(HealerService.class, new HealerService(classDao)),
				register(ArcherService.class, new ArcherService(classDao)));
				
		IServerService serverService = register(IServerService.class, new ServerService());
		ISettingService settingService = register(ISettingService.class, new SettingService(settingDao));
		IGameStateService gameStateService = register(IGameStateService.class, new GameStateService(gameStateDao, settingService));
		IWorldService worldService = register(IWorldService.class, new WorldService(gameStateService));
		IPlayerClassService playerClassService = register(IPlayerClassService.class, new PlayerClassService(serverService, gameStateService, classServices, plugin));
		IPlayerService playerService = register(IPlayerService.class, new PlayerService(messageDao, playerClassService, settingService, gameStateService, serverService, worldService, plugin, scoreboard));
		ICaptureService captureService = register(ICaptureService.class, new CaptureService(settingService, gameStateService, playerService, plugin));

		register(IGameStatusService.class, new GameStatusService(gameStatusDao, gameStateService, scoreboard));
				
		register(ReloadConfigCommand.class, new ReloadConfigCommand(gameStateService, () -> reload()));
		
		register(StopCaptureCommand.class, new StopCaptureCommand(gameStateService, captureService));
		register(StartCaptureCommand.class, new StartCaptureCommand(gameStateService, captureService));
		
		register(PrepareGameCommand.class, new PrepareGameCommand(gameStateService));
		register(ResetGameCommand.class, new ResetGameCommand(gameStateService));
		register(StartGameCommand.class, new StartGameCommand(gameStateService));
		register(StopGameCommand.class, new StopGameCommand(gameStateService));
		
		register(RandomizeTeamsCommand.class, new RandomizeTeamsCommand(gameStateService, playerService));
		register(RefillPlayerCommand.class, new RefillPlayerCommand(gameStateService, playerClassService));
		register(SpawnPlayerCommand.class, new SpawnPlayerCommand(gameStateService, playerService));
	}
	
	private void reload()
	{
		getAll(IReloadable.class).forEach(c -> c.reload());
	}
}
