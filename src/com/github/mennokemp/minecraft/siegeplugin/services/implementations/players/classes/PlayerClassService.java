package com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;

import com.github.mennokemp.minecraft.pluginhelpers.Result;
import com.github.mennokemp.minecraft.pluginhelpers.TaskRunner;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GameEvent;
import com.github.mennokemp.minecraft.siegeplugin.domain.game.GamePhase;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.IServerService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.game.IGameStateService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.IClassService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.IDamageService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.IPlayerClassService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.IRefillService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.ISupportService;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.PlayerService;

public class PlayerClassService implements IPlayerClassService
{
	private static final String SuicidePotionName = "Suicide Potion";
	private static final String SuicidePotionDescription = "It could be the solution";
	private static final int SuicidePotionSlot = 8;
	
	private static final String RefillDamageObjective = "RefillDamage";
	private static final String RefillSupportObjective = "RefillSupport";
	
	private static final int SelectorStartIndex = 12;

	private final IServerService serverService;
	private final Collection<IClassService> classServices;
	
	private final Plugin plugin;
	
	private final Map<Player, Map<Integer, Integer>> cooldowns = new HashMap<>();
	
	private TaskRunner updateTask;
	
	public PlayerClassService(
			IServerService serverService, 
			IGameStateService gameStateService, 
			Collection<IClassService> classServices, 
			Plugin plugin)
	{
		this.serverService = serverService;
		this.classServices = classServices;
		
		this.plugin = plugin;
		
		gameStateService.registerGameEventListener(this);
		
		if(gameStateService.getGamePhase() == GamePhase.InProcess)
			resetUpdate();
	}
		
	@Override
	public void showClassSection(Player player)
	{
		int index = SelectorStartIndex;
		Inventory inventory = Bukkit.createInventory(player, InventoryType.CHEST);
		
		for(IClassService classService : forPlayer(player))
		{
			inventory.setItem(index, classService.getSelector());
			index++;
		}
		
		player.openInventory(inventory);
	}
	
	@Override
	public void onItemConsume(PlayerItemConsumeEvent event)
	{
		if(event.getItem().getItemMeta().getDisplayName().equals(SuicidePotionName))
		{
			Player player = event.getPlayer();
			player.setLastDamageCause(new EntityDamageEvent(null, DamageCause.CUSTOM, 0));
			player.setHealth(0);
		}
	}
	
	@Override
	public void onSpellCast(Player player, int hotbarSlot)
	{
		IClassService classService = fromPlayer(player);
		
		if(classService instanceof ISupportService)
			((ISupportService)classService).onSpellCast(player, hotbarSlot, getCooldowns(player));
	}
	
	@Override
	public void OnGameEvent(GameEvent gameEvent)
	{
		if(gameEvent == GameEvent.GameStarting)
			resetUpdate();
	}
	
	@Override
	public PlayerClass equipPlayer(Player player, ItemStack selector)
	{
		IClassService classService = fromSelector(selector);
		equipPlayer(player, classService);
		return classService.getPlayerClass();
	}
	
	@Override
	public Result refillPlayer(Player player)
	{
		IClassService classService = fromPlayer(player);
		
		if(classService instanceof IRefillService)
		{
			((IRefillService)classService).refillPlayer(player);
			return Result.success("Refilled " + player.getName());
		}
		
		return Result.failure("Cannot refill  " + PlayerService.getPlayerClass(player) + " " + player.getName());
	}
	
	@Override
	public void equipPlayer(Player player, PlayerClass playerClass)
	{
		if(playerClass == null)
			PlayerService.setPlayerClass(player, null);
		else
			equipPlayer(player, fromPlayerClass(playerClass));
	}
	
	private void equipPlayer(Player player, IClassService classService)
	{
		PlayerClass playerClass = classService.getPlayerClass();
		PlayerService.setPlayerClass(player, playerClass);
				
		PlayerInventory inventory = player.getInventory();
		inventory.setItem(SuicidePotionSlot, getSuicidePotion());
		inventory.setHeldItemSlot(0);
		
		classService.equipPlayer(player, false);
		cooldowns.get(player).clear();
	}
	
	private Collection<IClassService> forPlayer(Player player)
	{
		Side side = PlayerService.getSide(player);
		return classServices.stream().filter(s -> s.getSide() == side).toList();
	}
	
	private IClassService fromSelector(ItemStack selector)
	{
		Material selectorType = selector.getType();
		return classServices
				.stream()
				.filter(s -> s.getSelector().getType() == selectorType)
				.findFirst()
				.orElse(null);
	}
	
	private IClassService fromPlayerClass(PlayerClass playerClass)
	{
		return classServices
				.stream()
				.filter(s -> s.getPlayerClass() == playerClass)
				.findFirst()
				.orElse(null);
	}
	
	private IClassService fromPlayer(Player player)
	{
		return fromPlayerClass(PlayerService.getPlayerClass(player));
	}
	
	private void resetUpdate()
	{
		cooldowns.clear();
		serverService.getPlayers().forEach(p -> cooldowns.put(p, new HashMap<>()));
		
		if(updateTask != null)
			updateTask.cancel();
		
		updateTask = new TaskRunner(plugin);
		updateTask.setTask(() -> 
		{
			for(Player player : serverService.getPlayers(GameMode.ADVENTURE))
			{
				IClassService classService = fromPlayer(player);
				
				if(classService instanceof ISupportService)
				{
					ISupportService supportService = (ISupportService)classService; 
					
					supportService.update(player, getCooldowns(player), true);
					
					if(PlayerService.getScore(player, RefillSupportObjective) == 1)
						supportService.refillPlayer(player);
				}
				else if(classService instanceof IDamageService)
				{
					IDamageService damageService = (IDamageService)classService; 
					
					if(PlayerService.getScore(player, RefillDamageObjective) == 1)
						damageService.refillPlayer(player);
				}
			}
		});
		updateTask.runRepeatedly();
	}
	
	private Map<Integer, Integer> getCooldowns(Player player)
	{
		if(!cooldowns.containsKey(player))
			cooldowns.put(player, new HashMap<>());
			
		return cooldowns.get(player);
	}
	
	private static ItemStack getSuicidePotion()
	{
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta potionData = (PotionMeta)potion.getItemMeta();
		potionData.setDisplayName(SuicidePotionName);
		potionData.setLore(Arrays.asList(SuicidePotionDescription));
		potionData.setColor(Color.BLACK);
		potion.setItemMeta(potionData);
		return potion;
	}
}
