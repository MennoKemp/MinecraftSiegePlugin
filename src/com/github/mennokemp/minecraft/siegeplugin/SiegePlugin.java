package com.github.mennokemp.minecraft.siegeplugin;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import com.github.mennokemp.minecraft.pluginhelpers.persistence.abstractions.IReloadable;
import com.github.mennokemp.minecraft.siegeplugin.commands.Command;
import com.github.mennokemp.minecraft.siegeplugin.injections.InjectionModule;
import com.github.mennokemp.minecraft.siegeplugin.services.abstractions.players.IPlayerService;

public class SiegePlugin extends JavaPlugin implements Listener 
{
	private InjectionModule kernel;
	
	@Override
    public void onEnable() 
    {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    	kernel = new InjectionModule(this, scoreboard);
    		    	
		Reload();
    	RegisterCommands();
		RegisterListeners();
    }
    
    @Override
    public void onDisable() 
    {
    }
    
    private void Reload()
    {
    	for(IReloadable	reloadable : kernel.getAll(IReloadable.class))
    		reloadable.reload();
    }
    
    private void RegisterListeners()
    {
    	getServer().getPluginManager().registerEvents(kernel.get(IPlayerService.class), this);    	
    }
    
    private void RegisterCommands()
    {
    	for(Command command : kernel.getAll(Command.class))
    		getCommand(command.getName()).setExecutor(command);
    }
}
