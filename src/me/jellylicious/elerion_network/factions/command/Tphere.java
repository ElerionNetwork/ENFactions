package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Tphere implements CommandExecutor {

	Core core;
	public Tphere(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(core.getGroups().hasPermission(p, "elerion.command.tphere")) {
				if(args.length < 1 || args.length > 1) {
					p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /tphere <target>!");
				}else{
					Player t = Bukkit.getPlayer(args[0]);
					if(t != null) {
						t.teleport(p.getLocation());
						p.sendMessage(core.getPrefix() + "§aYou've just teleported §7" + t.getName() + " §ato your location!");
						t.sendMessage(core.getPrefix() + "§aYou've been teleported to §7" + p.getName() + "§a!");
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
