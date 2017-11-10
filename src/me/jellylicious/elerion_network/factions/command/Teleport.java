package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Teleport implements CommandExecutor {

	Core core;
	public Teleport(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(core.getGroups().hasPermission(p, "elerion.command.teleport")) {
				if(args.length < 1 || args.length > 1) {
					p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /teleport <target>!");
				}else{
					Player t = Bukkit.getPlayer(args[0]);
					if(t != null) {
						p.teleport(t.getLocation());
						p.sendMessage(core.getPrefix() + "§aYou've been teleported to §7" + t.getName() + "§a!");
						t.sendMessage(core.getPrefix() + "§7" + p.getName() + " §ahas teleported to your location.");
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

}
