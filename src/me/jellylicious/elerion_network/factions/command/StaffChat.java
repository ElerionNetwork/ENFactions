package me.jellylicious.elerion_network.factions.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class StaffChat implements CommandExecutor {

	Core core;
	public StaffChat(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(core.getGroups().hasPermission(p, "elerion.command.staffchat")) {
				if(args.length == 0) {
					p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /sc <message>!");
				}else{
					String msg = "";
					for(int i = 0; i < args.length; i++) {
						msg += " " + args[i];
					}
					for(Player s : core.getServer().getOnlinePlayers()) {
						if(core.getGroups().hasPermission(s, "elerion.command.staffchat")) {
							s.sendMessage("§7[§aENStaffChat§7] §7" + p.getName() + " §a> §b" + msg);
						}
					}
				}
			}else{
				cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
			}
		}
		return true;
	}

}
