package com.github.mennokemp.minecraft.siegeplugin.persistence.implementations.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Banner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.mennokemp.minecraft.pluginhelpers.persistence.abstractions.IConfigurationDao;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.PlayerClass;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Spell;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.players.IClassDao;

public class ClassDao implements IClassDao
{
	private static final String ClassesPath = "classes";
	
	private static final String NamePath = "name";
	private static final String DescriptionPath = "description";
	private static final String SelectorPath = "selector";
	private static final String SidePath = "side";
	
	private static final String HealthPath = "health";
	
	private static final String EffectsPath = "effects";
	
	private static final String EquipmentPath = "equipment";
	private static final String InventoryPath = "inventory";
	private static final String OffhandPath = "off_hand";
	private static final String HotbarPath = "hotbar";
	private static final String ItemPath = "item";
	private static final String CountPath = "count";
	private static final String EnchantmentsPath = "enchantments";
	private static final String ColorPath = "color";
	
	private static final String ManaPath = "mana";
	private static final String RefillRatePath = "refill_rate";
	private static final String SpellsPath = "spells";
	private static final String EffectPath = "effect";
	private static final String DurationPath = "duration";
	private static final String LevelPath = "level";
	private static final String CostPath = "cost";
	private static final String CooldownPath = "cooldown";
		
	private ConfigurationSection configuration;
	
	public ClassDao(IConfigurationDao configurationDao)
	{
		this.configuration = configurationDao.getConfiguration(ClassesPath);
	}
			
	@Override
	public ConfigurationSection getClassConfiguration(PlayerClass playerClass)
	{
		return configuration.getConfigurationSection(playerClass.toString().toLowerCase());
	}
	
	@Override
	public Side getSide(PlayerClass playerClass)
	{
		return Side.valueOf(getClassConfiguration(playerClass).getString(SidePath));
	}

	@Override
	public ItemStack getSelector(PlayerClass playerClass)
	{
		ConfigurationSection classConfiguration = getClassConfiguration(playerClass);
		
		ItemStack item = new ItemStack(Material.valueOf(classConfiguration.getString(SelectorPath)));
		ItemMeta itemData = item.getItemMeta();
		itemData.setDisplayName(classConfiguration.getString(NamePath));
		itemData.setLore(classConfiguration.getStringList(DescriptionPath));
		item.setItemMeta(itemData);
		return item;
	}

	@Override
	public int getHealth(PlayerClass playerClass)
	{
		return getClassConfiguration(playerClass).getInt(HealthPath);
	}
	
	@Override
	public Collection<PotionEffect> getEffects(PlayerClass playerClass)
	{
		ConfigurationSection classConfiguration = getClassConfiguration(playerClass);
		
		if(!classConfiguration.contains(EffectsPath))
			return new ArrayList<PotionEffect>();
		
		return classConfiguration
				.getStringList(EffectsPath)
				.stream()
				.map(e -> e.split(","))
				.map(e -> new PotionEffect(
						PotionEffectType.getByName(e[0]),
						Integer.MAX_VALUE,
						Integer.valueOf(e[1])))
				.toList();
	}
	
	@Override
	public Map<EquipmentSlot, ItemStack> getEquipment(PlayerClass playerClass)
	{
		ConfigurationSection equipmentConfiguration = getClassConfiguration(playerClass)
				.getConfigurationSection(EquipmentPath);
		
		Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
		
		if(equipmentConfiguration != null)
		{
			equipmentConfiguration
			.getKeys(false)
			.stream()
			.map(k -> EquipmentSlot.valueOf(k.toUpperCase()))
			.forEach(s -> equipment.put(s, getItem(equipmentConfiguration, s.toString().toLowerCase())));			
		}
		
		return equipment;
	}
	
	@Override
	public Map<Integer, ItemStack> getItems(PlayerClass playerClass)
	{
		ConfigurationSection inventoryConfiguration = getClassConfiguration(playerClass)
				.getConfigurationSection(InventoryPath);
		
		Map<Integer, ItemStack> items = new HashMap<>();
		
		if(inventoryConfiguration != null)
		{
			inventoryConfiguration
			.getKeys(false)
			.forEach(s -> items.put(
					getHotbarSlot(s), 
					getItem(inventoryConfiguration, s)));			
		}
		
		return items;
	}

	@Override
	public int getMana(PlayerClass playerClass)
	{
		return getClassConfiguration(playerClass).getInt(ManaPath);
	}
	
	@Override
	public Map<Integer, Spell> getSpells(PlayerClass playerClass)
	{
		ConfigurationSection spellsConfiguration = getClassConfiguration(playerClass)
				.getConfigurationSection(SpellsPath);

		Map<Integer, Spell> spells = new HashMap<>();
		
		if(spellsConfiguration != null)
		{
			for(String hotbarSlot : spellsConfiguration.getKeys(false)) 
			{
				int slot = getHotbarSlot(hotbarSlot);
				Spell spell = getSpell(slot, spellsConfiguration.getConfigurationSection(hotbarSlot));
				spells.put(slot, spell);
			}			
		}
		
		return spells;
	}
	
	@Override
	public ItemStack getAmmo(PlayerClass playerClass)
	{
		ConfigurationSection ammoConfiguration = getClassConfiguration(playerClass)
				.getConfigurationSection(EquipmentPath)
				.getConfigurationSection(OffhandPath);
		
		if(ammoConfiguration == null)
			return null;
		
		Material ammoType = Material.valueOf(ammoConfiguration.getString(ItemPath));
		int ammoCount = ammoConfiguration.getInt(CountPath);
		return new ItemStack(ammoType, ammoCount);
	}
	
	@Override
	public int getRefillRate(PlayerClass playerClass)
	{
		return getClassConfiguration(playerClass).getInt(RefillRatePath);
	}
	
	private ItemStack getItem(ConfigurationSection itemConfiguration, String slot)
	{
		ConfigurationSection slotConfiguration = itemConfiguration
				.getConfigurationSection(slot);
		
		Material material = Material.valueOf(slotConfiguration.getString(ItemPath));
		ItemStack item = slotConfiguration.contains(CountPath)
				? new ItemStack(material, slotConfiguration.getInt(CountPath))
				: new ItemStack(material);
		
		ItemMeta itemData = item.getItemMeta();
		itemData.setDisplayName(itemConfiguration.getString(NamePath));
		itemData.setLore(Arrays.asList(itemConfiguration.getString(DescriptionPath)));
		item.setItemMeta(itemData);
		
		if(slotConfiguration.contains(EnchantmentsPath))
		{
			slotConfiguration
				.getStringList(EnchantmentsPath)
				.stream()
				.map(e -> e.split(","))
				.forEach(e -> 
				{
					Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(e[0]));
					int level = Integer.valueOf(e[1]);
					item.addUnsafeEnchantment(enchantment, level);
				});
		}
		
		if(material == Material.SHIELD)
		{
	        ItemMeta itemMeta = item.getItemMeta();
	        BlockStateMeta blockMeta = (BlockStateMeta)itemMeta;
	        Banner banner = (Banner)blockMeta.getBlockState();
	        banner.setBaseColor(DyeColor.valueOf(slotConfiguration.getString(ColorPath)));
	        banner.update();
	        blockMeta.setBlockState(banner);
	        item.setItemMeta(blockMeta);
		}
				
		return item;
	}
	
	private int getHotbarSlot(String hotbarSlot)
	{
		return Integer.valueOf(hotbarSlot.replace(HotbarPath, ""));
	}
		
	private Spell getSpell(int hotbarSlot, ConfigurationSection spellConfiguration)
	{
		Spell spell = new Spell();
		
		spell.setEffect(PotionEffectType.getByName(spellConfiguration.getString(EffectPath)));
		spell.setDuration(spellConfiguration.getInt(DurationPath));
		spell.setLevel(spellConfiguration.getInt(LevelPath));
		spell.setName(spellConfiguration.getString(NamePath));
		spell.setDescription(spellConfiguration.getString(DescriptionPath));
		
		String[] colorData = spellConfiguration.getString(ColorPath).split(",");
		Color color = Color.fromBGR(Integer.valueOf(colorData[0]),Integer.valueOf(colorData[1]),Integer.valueOf(colorData[2]));
		spell.setColor(color);
		
		spell.setHotbarSlot(hotbarSlot);
		spell.setCost(spellConfiguration.getInt(CostPath));
		spell.setCooldown(spellConfiguration.getInt(CooldownPath));
		
		return spell;
	}
}
