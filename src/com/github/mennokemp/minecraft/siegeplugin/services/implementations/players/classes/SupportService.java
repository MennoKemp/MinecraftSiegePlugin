package com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.classes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Spell;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.players.IClassDao;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.classes.ISupportService;

public abstract class SupportService extends ClassService implements ISupportService
{		
	private static final String NotEnoughMana = "Not enough mana";
	private static final String Cooldown = "Cooldown";
	
	private static final Material CooldownIndicator = Material.LEATHER_HELMET;
	private static final Material TooExpensiveIndicator = Material.BARRIER;
	
	private static final int FullSpellDamageRange = 1;
	private static final int SpellDamageRange = 5;
	
	private static final Collection<PotionEffectType> HarmingEffects = Arrays.asList(
		PotionEffectType.BLINDNESS,
		PotionEffectType.CONFUSION,
		PotionEffectType.HARM,
		PotionEffectType.HUNGER,
		PotionEffectType.POISON,
		PotionEffectType.SLOW,
		PotionEffectType.SLOW_DIGGING,
		PotionEffectType.WEAKNESS,
		PotionEffectType.WITHER);
	
	private final int mana;
	private final int refillRate;
	private final Map<Integer, Spell> spells;
	
	public SupportService(IClassDao classDao, PlayerClass playerClass)
	{
		super(classDao, playerClass);
				
		mana = classDao.getMana(playerClass);
		refillRate = classDao.getRefillRate(playerClass);
		spells = classDao.getSpells(playerClass);			
	}
	
	@Override
	public void reset(Player player)
	{
		player.setLevel(mana);
		
		spells.entrySet()
		.stream()
		.forEach(s -> 
		player.getInventory().setItem(s.getValue().getHotbarSlot(), 
				s.getValue().getPotion()));
	}

	@Override
	public void onSpellCast(Player player, int hotbarSlot, Map<Integer, Integer> cooldowns)
	{
		Spell spell = spells.get(hotbarSlot);
		player.setLevel(player.getLevel() - spell.getCost());
		cooldowns.put(hotbarSlot, spell.getCooldown());
		setCooldownIndicator(player, spell, spell.getCooldown());
		update(player, cooldowns, false);
	}
	
	@Override
	public void update(Player player, Map<Integer, Integer> cooldowns, boolean countDown)
	{
		Inventory inventory = player.getInventory();
		
		for(Entry<Integer, Spell> spellSlot : spells.entrySet())
		{
			int hotbarSlot = spellSlot.getKey();
			int timeLeft = cooldowns.getOrDefault(hotbarSlot, 0);
			Spell spell = spellSlot.getValue();
			boolean tooExpensive = spell.getCost() > player.getLevel();
			Material itemType = inventory.getItem(hotbarSlot) == null
					? null
					: inventory.getItem(hotbarSlot).getType();
			
			if(countDown)
			{
				if(timeLeft > 1)
				{
					timeLeft--;
					cooldowns.put(hotbarSlot, timeLeft);
					setCooldownIndicator(player, spells.get(hotbarSlot), timeLeft);
					continue;
				}
				else if(timeLeft == 1)
				{
					cooldowns.put(hotbarSlot, 0);
					
					if(tooExpensive)
						setTooExpensiveIndicator(player, spell);
					else
						player.getInventory().setItem(hotbarSlot, spell.getPotion());

					continue;
				}
			}

			if(timeLeft == 0)
			{
				if(tooExpensive)
				{
					if(itemType != TooExpensiveIndicator)
						setTooExpensiveIndicator(player, spell);
				}
				else
				{
					if(itemType != Material.SPLASH_POTION)
						inventory.setItem(hotbarSlot, spell.getPotion());
				}
			}
		}
	}
	
	@Override
	public void refillPlayer(Player player)
	{
		int mana = player.getLevel();
		
		if(mana != this.mana)
			player.setLevel(Math.min(this.mana, mana + refillRate));
	}
	
	private void setCooldownIndicator(Player player, Spell spell, int cooldown)
	{
		ItemStack indicator = new ItemStack(CooldownIndicator);
		Damageable damageble = (Damageable)indicator.getItemMeta();
		
		int durability = cooldown * CooldownIndicator.getMaxDurability() / spell.getCooldown();
		damageble.setDamage(durability);
		damageble.setDisplayName(spell.getName() + " - " + Cooldown + " " + cooldown + "s");
		damageble.setLore(spell.getDescription());
		
		indicator.setItemMeta(damageble);
		player.getInventory().setItem(spell.getHotbarSlot(), indicator);
	}
	
	private void setTooExpensiveIndicator(Player player, Spell spell)
	{
		ItemStack indicator = new ItemStack(TooExpensiveIndicator);
		ItemMeta indicatorData = indicator.getItemMeta();
		
		indicatorData.setDisplayName(spell.getName() + " - " + NotEnoughMana);
		indicatorData.setLore(spell.getDescription());
		
		indicator.setItemMeta(indicatorData);
		player.getInventory().setItem(spell.getHotbarSlot(), indicator);
	}
	
	public static Collection<PotionEffect> getEffects(ThrownPotion potion, boolean harming)
	{
		return potion.getEffects()
				.stream()
				.filter(e -> HarmingEffects.contains(e.getType()) == harming)
				.toList();
	}
	
	public static PotionEffect getEffect(PotionEffect spell, double distance)
	{
		if(distance < FullSpellDamageRange)
			return spell;
		
		if(distance > SpellDamageRange)
			return null;

		double duration = spell.getDuration() - distance / (SpellDamageRange - FullSpellDamageRange) * spell.getDuration(); 
		return new PotionEffect(spell.getType(), (int)duration, spell.getAmplifier());
	}
}
