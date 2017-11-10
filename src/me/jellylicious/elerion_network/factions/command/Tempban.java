package me.jellylicious.elerion_network.factions.command;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;
import me.jellylicious.elerion_network.factions.api.punishment.Punisher.PunishmentType;

public class Tempban implements CommandExecutor {

	Core core;
	public Tempban(Core core) {
		this.core = core;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(core.getGroups().hasPermission(p, "elerion.command.tempban")) {
				if(args.length < 2) {
					p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /tempban <player> <length> <lengthUnit[s,min,h,d,w,m,y]> <reason>!");
				}else{
					Player target = Bukkit.getPlayer(args[0]);
					String reason = "";
					for(int i = 3; i < args.length; i++) {
						reason += " " + args[i];
					}
					int length = Integer.parseInt(args[1]);
					if(target != null) {
						try {
							core.getPunisher().issuePunishment(p, target, PunishmentType.TEMPBAN, length, args[2], reason);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}else{
						OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
						if(core.getPlayerAccount().existsPlayer(t.getUniqueId().toString())) {
							try {
								core.getPunisher().issueOfflinePunishment(p, t.getUniqueId().toString(), t.getName(), PunishmentType.TEMPBAN, length, args[2], reason);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cWe could not find that player.");
						}
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
