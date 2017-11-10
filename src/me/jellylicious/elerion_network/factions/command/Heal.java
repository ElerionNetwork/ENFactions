package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Heal implements CommandExecutor {

	Core core;
	public Heal(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0) {
				if(core.getGroups().hasPermission(p, "elerion.command.heal")) {
					p.setHealth(20D);
					p.setFoodLevel(20);
					p.sendMessage(core.getPrefix() + "§aYou have been healed.");
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else if(args.length == 1) {
				if(core.getGroups().hasPermission(p, "elerion.command.heal.others")) {
					Player t = Bukkit.getPlayer(args[0]);
					if(t != null) {
						t.setHealth(20D);
						t.setFoodLevel(20);
						p.sendMessage(core.getPrefix() + "§7" + t.getName() + " §ahas been healed.");
						t.sendMessage(core.getPrefix() + "§aYou have been healed by §7" + p.getName() + "§a.");
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /heal <target[optional]>!");
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

}
