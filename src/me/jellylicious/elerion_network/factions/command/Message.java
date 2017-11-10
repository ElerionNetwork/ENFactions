package me.jellylicious.elerion_network.factions.command;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Message implements CommandExecutor {

	Core core;
	
	public static HashMap<String, String> message = new HashMap<String, String>();
	
	public Message(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player player = (Player) cs;
			if(args.length == 0 || args.length == 1) {
				player.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /msg <player> <message>!");
			}else{
				Player target = Bukkit.getPlayer(args[0]);
				if(target != null) {
					String msg = "";
					for(int i = 1; i < args.length; i++) {
						msg += " " + args[i];
					}
					target.sendMessage("§a✉ §7from §a" + player.getName() + "§7: §b" + msg);
					player.sendMessage("§a✉ §7to §a" + target.getName() + "§7: §b" + msg);
					message.put(target.getName(), player.getName());
					message.put(player.getName(), target.getName());
				}else{
					player.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
				}
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cOnly players can send private messages.");
		}
		return true;
	}

}
