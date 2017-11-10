package me.jellylicious.elerion_network.factions.command;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;
import me.jellylicious.elerion_network.factions.api.punishment.Punisher.PunishmentType;

public class Kick implements CommandExecutor {

	Core core;
	public Kick(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(core.getGroups().hasPermission(p, "elerion.command.kick")) {
				if(args.length < 2) {
					p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /kick <player> <reason>!");
				}else{
					Player target = Bukkit.getPlayer(args[0]);
					if(target != null) {
						String reason = "";
						for(int i = 1; i < args.length; i++) {
							reason += " " + args[i];
						}
						try {
							core.getPunisher().issuePunishment(p, target, PunishmentType.KICK, 0, null, reason);
						} catch (SQLException e) {
							e.printStackTrace();
						}
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
