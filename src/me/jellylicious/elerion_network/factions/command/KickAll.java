package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class KickAll implements CommandExecutor {

	Core core;
	public KickAll(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(core.getGroups().hasPermission(p, "elerion.command.kickall")) {
				if(args.length == 0) {
					p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /kickall <reason>!");
				}else{
					String reason = "";
					for(int i = 0; i < args.length; i++) {
						reason += " " + args[i];
					}
					for(Player po : Bukkit.getOnlinePlayers()) {
						if(!core.getGroups().hasPermission(po, "elerion.kickall.override") || p != po) {
							po.kickPlayer("§c§lYou've been kicked by §7" + p.getName() + "§c§l! \n" 
									+ "§c§lREASON: §b" + reason);
						}
					}
					p.sendMessage(core.getPrefix() + "§aAll players have been kicked, except the ones with permission to override kickall.");
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
