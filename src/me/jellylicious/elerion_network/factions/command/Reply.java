package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Reply implements CommandExecutor {

	Core core;

	public Reply(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (cs instanceof Player) {
			Player player = (Player) cs;
			if (args.length == 0) {
				player.sendMessage(core.getPrefix() + "§cCorrect usage: /r <message>");
			} else {
				if (Message.message.containsKey(player.getName())) {
					Player target = Bukkit.getPlayer(Message.message.get(player.getName()));
					if (target != null) {
						String msg = "";
						for (int i = 0; i < args.length; i++) {
							msg += " " + args[i];
						}
						target.sendMessage("§a✉ §7from §a" + player.getName() + "§7: §b" + msg);
						player.sendMessage("§a✉ §7to §a" + target.getName() + "§7: §b" + msg);
					}else{
						player.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				} else {
					player.sendMessage(core.getPrefix() + "§cYou do not have any recent conversations.");
				}
			}
		} else {
			cs.sendMessage(core.getPrefix() + "§cOnly players can send private messages.");
		}
		return true;
	}

}
