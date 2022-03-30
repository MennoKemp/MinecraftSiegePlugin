package com.github.mennokemp.minecraft.siegeplugin.persistence.implementations;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.mennokemp.minecraft.pluginhelpers.persistence.abstractions.IConfigurationDao;
import com.github.mennokemp.minecraft.siegeplugin.domain.players.Side;
import com.github.mennokemp.minecraft.siegeplugin.persistence.abstractions.IMessageDao;
import com.github.mennokemp.minecraft.siegeplugin.services.implementations.players.PlayerService;

public class MessageDao implements IMessageDao
{	
	private static final String MessagesPath = "messages";
	
	private IConfigurationDao configurationDao;

	public MessageDao(IConfigurationDao configurationDao)
	{
		this.configurationDao = configurationDao;
	}

	@Override
	public String[] getMessages(String name, Side side)
	{
		name = name.toLowerCase();
		
		String[] message = new String[0];
		
		if(side != Side.Any)
			message = getMessages(name + "_" + side.toString());

		if(message.length == 0)
			message = getMessages(name);
		
		if(message.length == 0)
			Bukkit.getLogger().severe("Could not find message " + name);
			
		return message;		
	}
	
	@Override
	public String getDeathMessage(DamageCause cause, Player killed, Player killer)
	{
		String killedName = PlayerService.getTeam(killed).getColor() + killed.getName();
		String killerName = PlayerService.getTeam(killer).getColor() + killer.getName();
		
		String message = " " + getMessage("death_" + cause) + " ";
		
		if(message == null || message.isEmpty())
			return "Missing message for " + cause; 
		
		return switch(cause)
		{
			case BLOCK_EXPLOSION -> killedName + message + killerName;
			case CUSTOM -> killedName + message;
			case DROWNING -> killedName + message;
			case ENTITY_ATTACK -> killedName + message + killerName;
			case ENTITY_EXPLOSION -> killedName + message + killerName;
			case ENTITY_SWEEP_ATTACK -> killedName + message + killerName;
			case FALL -> killedName + message;
			case FIRE -> killedName + message;
			case FIRE_TICK -> killedName + message;
			case FLY_INTO_WALL -> killedName + message;
			case LAVA -> killedName + message;
			case MAGIC -> killedName + message + killerName;
			case POISON -> killedName + message + killerName;
			case PROJECTILE -> killedName + message + killerName;
			case SUICIDE -> killedName + message;
			case THORNS -> killedName + message + killerName;
			default -> killedName + message;
		};
	}
	
	private String[] getMessages(String name)
	{
		name = name.toLowerCase();
		
		ConfigurationSection messageConfiguration = configurationDao.getConfiguration(MessagesPath);
		
		return messageConfiguration.contains(name)
			? messageConfiguration.getString(name).split("\t")
			: new String[0];		
	}
	
	private String getMessage(String name)
	{		
		return configurationDao.getConfiguration(MessagesPath).getString(name); 
	}
}
