package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class ChatClear implements CommandExecutor {

	Core core;
	
	public ChatClear(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs.hasPermission("elerion.command.chatclear")) {
			if(args.length == 0) {
				for(Player all : Bukkit.getOnlinePlayers()) {
					for(int i = 0; i < 100; i++) {
						all.sendMessage("");
					}
					all.sendMessage(core.getPrefix() + "§aChat was cleared by §7" + cs.getName() + "§a!");
					cs.sendMessage(core.getPrefix() + "§aChat was successfully cleared.");
				}
			}else{
				cs.sendMessage(core.getPrefix() + "§cCorrect usage: /chatclear");
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cYou do not have permission to clear the chat.");
		}
		return true;
	}

}
