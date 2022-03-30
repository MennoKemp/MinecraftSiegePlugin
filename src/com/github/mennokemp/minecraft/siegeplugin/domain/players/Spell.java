package com.github.mennokemp.minecraft.siegeplugin.domain.players;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.mennokemp.minecraft.siegeplugin.domain.Constants;

public class Spell
{
	private PotionEffectType effect;
	private int duration;
	private int level;
	private String name;
	private String description;
	private Color color;
	
	private int hotbarSlot;
	private int cost;
	private int cooldown;
	
	public PotionEffectType getEffect()
	{
		return effect;
	}

	public void setEffect(PotionEffectType effect)
	{
		this.effect = effect;
	}

	public int getHotbarSlot()
	{
		return hotbarSlot;
	}

	public void setHotbarSlot(int hotbarSlot)
	{
		this.hotbarSlot = hotbarSlot;
	}

	public int getCost()
	{
		return cost;
	}

	public void setCost(int cost)
	{
		this.cost = cost;
	}

	public int getCooldown()
	{
		return cooldown;
	}

	public void setCooldown(int cooldown)
	{
		this.cooldown = cooldown;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public List<String> getDescription()
	{
		return Arrays.asList(
			description, 
			"Cost " + cost + " mana", 
			"Cooldown " + cooldown + "s");
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public ItemStack getPotion()
	{
		ItemStack potion = new ItemStack(Material.SPLASH_POTION);
		PotionMeta potionData = (PotionMeta)potion.getItemMeta();
		potionData.setDisplayName(name);
		potionData.setLore(getDescription());
		potionData.addCustomEffect(new PotionEffect(effect, duration * Constants.TicksPerSecond, level), true);
		potionData.setColor(color);
		potion.setItemMeta(potionData);
		return potion;
	}
}
