package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class ChatLock implements CommandExecutor {

	Core core;
	
	public ChatLock(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs.hasPermission("elerion.command.chatlock")) {
			if(args.length == 0 || args.length > 1) {
				cs.sendMessage(core.getPrefix() + "§cCorrect usage: /chatlock <on/off>");
			}else{
				if(args[0].equalsIgnoreCase("on")) {
					if(!core.isChatLocked()) {
						core.setChatLocked(true);
						for(Player all : Bukkit.getOnlinePlayers()) {
							all.sendMessage(core.getPrefix() + "§aChat was locked by §7" + cs.getName() + "§a!");
						}
						cs.sendMessage(core.getPrefix() + "§aChat is now locked. Only players with permission can chat.");
					}else{
						cs.sendMessage(core.getPrefix() + "§cChat is already locked.");
					}
				}else if(args[0].equalsIgnoreCase("off")){
					if(core.isChatLocked()) {
						core.setChatLocked(false);
						for(Player all : Bukkit.getOnlinePlayers()) {
							all.sendMessage(core.getPrefix() + "§aChat was unlocked by §7" + cs.getName() + "§a!");
						}
						cs.sendMessage(core.getPrefix() + "§aChat is now unlocked. All players may chat.");
					}else{
						cs.sendMessage(core.getPrefix() + "§cChat is already unlocked.");
					}
				}else{
					cs.sendMessage(core.getPrefix() + "§cCorrect usage: /chatlock <on/off>");
				}
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cYou do not have permission to lock the chat.");
		}
		return true;
	}

}
