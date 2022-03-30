package com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.players.IClassDao;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.IDamageService;

public class DamageService extends ClassService implements IDamageService
{
	private final ItemStack ammoType;
	private final int refillRate;
	
	public DamageService(IClassDao classDao, PlayerClass playerClass)
	{
		super(classDao, playerClass);
		
		ammoType = classDao.getAmmo(playerClass);
		refillRate = classDao.getRefillRate(playerClass);
	}

	@Override
	public void refillPlayer(Player player)
	{
		EntityEquipment equipment = player.getEquipment();
		
		ItemStack ammo = equipment.getItemInOffHand();
		
		if(ammo == null)
			equipment.setItemInOffHand(new ItemStack(ammoType.getType(), refillRate));
		else
			ammo.setAmount(Math.min(ammoType.getAmount(), ammo.getAmount() + refillRate));
	}
}
