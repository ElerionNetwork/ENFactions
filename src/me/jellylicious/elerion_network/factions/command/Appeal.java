package me.jellylicious.elerion_network.factions.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Appeal implements CommandExecutor {

	Core core;
	public Appeal(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(core.getGroups().hasPermission(p, "elerion.command.appeal")) {
				if(args.length == 1) {
					if(core.getNumericAPI().isNumeric(args[0])) {
						int punishmentID = Integer.parseInt(args[0]);
						core.getPunisher().appealPunishment(p, punishmentID);
					}else{
						p.sendMessage(core.getPrefix() + "§cSorry, but your must enter an integer as punishment ID.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /appeal <punishmentID>!");
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
