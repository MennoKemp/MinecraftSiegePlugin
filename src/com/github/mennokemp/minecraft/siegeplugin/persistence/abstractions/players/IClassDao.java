package com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.players;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Spell;

public interface IClassDao
{
	ConfigurationSection getClassConfiguration(PlayerClass playerClass);
	
	Side getSide(PlayerClass playerClass);
	
	ItemStack getSelector(PlayerClass playerClass);
	
	int getHealth(PlayerClass playerClass);
	
	Collection<PotionEffect> getEffects(PlayerClass playerClass);
	
	Map<EquipmentSlot, ItemStack> getEquipment(PlayerClass playerClass);
	
	Map<Integer, ItemStack> getItems(PlayerClass playerClass);
	
	int getMana(PlayerClass playerClass);
	
	Map<Integer, Spell> getSpells(PlayerClass playerClass);
	
	ItemStack getAmmo(PlayerClass playerClass);
	
	int getRefillRate(PlayerClass playerClass);
}
