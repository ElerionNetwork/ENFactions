package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Gamemode implements CommandExecutor {

	Core core;
	public Gamemode(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(core.getGroups().hasPermission(p, "elerion.command.gamemode")) {
				if(args.length == 0 || args.length > 2) {
					p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use one of these commands:");
					p.sendMessage("§e/gm <0/1/2/3> <target[optional]>");
					p.sendMessage("§e/gm <s/c/a/spec> <target[optional]>");
				}else if(args.length == 1) {
					if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("0")) {
						p.setGameMode(GameMode.SURVIVAL);
						p.sendMessage(core.getPrefix() + "§aYour gamemode was updated to survival mode.");
					}else if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("1")) {
						p.setGameMode(GameMode.CREATIVE);
						p.sendMessage(core.getPrefix() + "§aYour gamemode was updated to creative mode.");
					}else if(args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("2")) {
						p.setGameMode(GameMode.ADVENTURE);
						p.sendMessage(core.getPrefix() + "§aYour gamemode was updated to adventure mode.");
					}else if(args[0].equalsIgnoreCase("spec") || args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("3")) {
						p.setGameMode(GameMode.SPECTATOR);
						p.sendMessage(core.getPrefix() + "§aYour gamemode was updated to spectator mode.");
					}else{
						p.sendMessage(core.getPrefix() + "§cThis gamemode does not exist.");
					}
				}else if(args.length == 2) {
					Player t = Bukkit.getPlayer(args[1]);
					if(t != null) {
						if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("0")) {
							t.setGameMode(GameMode.SURVIVAL);
							t.sendMessage(core.getPrefix() + "§aYour gamemode was updated to survival mode.");
							p.sendMessage(core.getPrefix() + "§aSuccessfully updated §7" + t.getName() + "§a's gamemode to survival mode.");
						}else if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("1")) {
							t.setGameMode(GameMode.CREATIVE);
							t.sendMessage(core.getPrefix() + "§aYour gamemode was updated to creative mode.");
							p.sendMessage(core.getPrefix() + "§aSuccessfully updated §7" + t.getName() + "§a's gamemode to creative mode.");
						}else if(args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("2")) {
							t.setGameMode(GameMode.ADVENTURE);
							t.sendMessage(core.getPrefix() + "§aYour gamemode was updated to adventure mode.");
							p.sendMessage(core.getPrefix() + "§aSuccessfully updated §7" + t.getName() + "§a's gamemode to adventure mode.");
						}else if(args[0].equalsIgnoreCase("spec") || args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("3")) {
							t.setGameMode(GameMode.SPECTATOR);
							t.sendMessage(core.getPrefix() + "§aYour gamemode was updated to spectator mode.");
							p.sendMessage(core.getPrefix() + "§aSuccessfully updated §7" + t.getName() + "§a's gamemode to spectator mode.");
						}else{
							p.sendMessage(core.getPrefix() + "§cThis gamemode does not exist.");
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
