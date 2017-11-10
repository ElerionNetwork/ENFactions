package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Tpaccept implements CommandExecutor {

	Core core;
	public Tpaccept(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length > 0) {
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /tpaccept!");
			}else{
				if(core.getTPA().containsKey(p) && core.getTPA().get(p) != null) {
					Player t = Bukkit.getPlayer(core.getTPA().get(p).getName());
					if(t != null) {
						t.teleport(p);
						core.getTPA().remove(p, t);
						core.getTPA().remove(t, p);
						p.sendMessage(core.getPrefix() + "§aTeleportation request accepted.");
						t.sendMessage(core.getPrefix() + p.getName() + " §aaccepted your teleportation request.");
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cYou don't have any pending teleportation requests!");
				}
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

}
