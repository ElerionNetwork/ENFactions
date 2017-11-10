package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class God implements CommandExecutor {

	Core core;
	public God(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0) {
				if(core.getGroups().hasPermission(p, "elerion.command.god")) {
					if(core.getGods().contains(p)) {
						core.getGods().remove(p);
						p.sendMessage(core.getPrefix() + "§aGod mode disabled.");
					}else{
						core.getGods().add(p);
						p.sendMessage(core.getPrefix() + "§aGod mode enabled.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else if(args.length == 1) {
				if(core.getGroups().hasPermission(p, "elerion.command.god.others")) {
					Player t = Bukkit.getPlayer(args[0]);
					if(t != null) {
						if(core.getGods().contains(t)) {
							core.getGods().remove(t);
							p.sendMessage(core.getPrefix() + "§aYou disabled §7" + t.getName() + "§a's god mode.");
							t.sendMessage(core.getPrefix() + "§aYour god mode was disabled by §7" + p.getName() + "§a.");
						}else{
							core.getGods().add(t);
							p.sendMessage(core.getPrefix() + "§aYou enabled §7" + t.getName() + "§a's god mode.");
							t.sendMessage(core.getPrefix() + "§aYour god mode was enabled by §7" + p.getName() + "§a.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /god <target[optional]>!");
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

}
