package com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.players.IClassDao;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.IClassService;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.ISupportService;

public abstract class ClassService implements IClassService
{		
	private final IClassDao classDao;
	
	private final PlayerClass playerClass;
	
	public ClassService(IClassDao classDao, PlayerClass playerClass)
	{
		this.classDao = classDao;
		
		this.playerClass = playerClass;
	}

	@Override
	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}
	
	@Override
	public void equipPlayer(Player player, boolean reequip)
	{
		if(!reequip)
		{
			AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			int health = classDao.getHealth(playerClass) * 2;		
			healthAttribute.setBaseValue(health);
			player.setHealth(health);
			
			setEffects(player);
			
			classDao.getEquipment(playerClass)
				.entrySet()
				.stream()
				.forEach(e -> player.getEquipment().setItem(e.getKey(), e.getValue()));
			
			classDao.getItems(playerClass)
				.entrySet()
				.stream()
				.forEach(e -> player.getInventory().setItem(e.getKey(), e.getValue()));
			
			if(this instanceof ISupportService)
				((ISupportService)this).reset(player);
		}
	}

	@Override
	public void setEffects(Player player)
	{
		classDao.getEffects(playerClass).forEach(e -> player.addPotionEffect(e));
	}
		
	@Override
	public ItemStack getSelector()
	{
		return classDao.getSelector(playerClass);
	}
	
	@Override
	public Side getSide()
	{
		return classDao.getSide(playerClass);
	}
}
