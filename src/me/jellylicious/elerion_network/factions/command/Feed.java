package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Feed implements CommandExecutor {

	Core core;
	public Feed(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0) {
				if(core.getGroups().hasPermission(p, "elerion.command.feed")) {
					p.setFoodLevel(20);
					p.sendMessage(core.getPrefix() + "§aYour hunger has been satiated.");
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else if(args.length == 1) {
				if(core.getGroups().hasPermission(p, "elerion.command.feed.others")) {
					Player t = Bukkit.getPlayer(args[0]);
					if(t != null) {
						t.setFoodLevel(20);
						p.sendMessage(core.getPrefix() + "§7" + t.getName() + "'s §ahunger has been satiated.");
						t.sendMessage(core.getPrefix() + "§aYour hunger has been satiated by §7" + p.getName() + "§a.");
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /feed <target[optional]>!");
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

}
