package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class GetPos implements CommandExecutor {

	Core core;
	public GetPos(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0){
				if(core.getGroups().hasPermission(p, "elerion.command.getpos")) {
					p.sendMessage("§bYour current location:");
					p.sendMessage("§7X: §a" + p.getLocation().getX());
					p.sendMessage("§7Y: §a" + p.getLocation().getY());
					p.sendMessage("§7Z: §a" + p.getLocation().getZ());
					p.sendMessage("§7Yaw: §a" + p.getLocation().getYaw());
					p.sendMessage("§7Pitch: §a" + p.getLocation().getPitch());
					p.sendMessage("§7World: §a" + p.getLocation().getWorld().getName());
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else if(args.length == 1) {
				if(core.getGroups().hasPermission(p, "elerion.command.getpos.others")) {
					Player t = Bukkit.getPlayer(args[0]);
					if(t != null) {
						p.sendMessage("§b" + t.getName() + "'s current location:");
						p.sendMessage("§7X: §a" + t.getLocation().getX());
						p.sendMessage("§7Y: §a" + t.getLocation().getY());
						p.sendMessage("§7Z: §a" + t.getLocation().getZ());
						p.sendMessage("§7Yaw: §a" + t.getLocation().getYaw());
						p.sendMessage("§7Pitch: §a" + t.getLocation().getPitch());
						p.sendMessage("§7World: §a" + t.getLocation().getWorld().getName());
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /getpos <target[optional]>!");
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

}
