package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Group implements CommandExecutor {

	Core core;

	public Group(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player cs = (Player) sender;
			if(core.getGroups().hasPermission(cs, "elerion.command.group")) {
				if(args.length == 0) {
					sendHelp(cs, 1);
				}else if(args.length == 1){
					if(args[0].equals("2")) {
						sendHelp(cs, 2);
					}else{
						sendHelp(cs, 1);
					}
				}else if(args.length == 2) {
					if(args[0].equalsIgnoreCase("create")) {
						String groupName = args[1];
						core.getGroups().createGroup(cs, groupName);
					}else if(args[0].equalsIgnoreCase("remove")) {
						String groupName = args[1];
						core.getGroups().removeGroup(cs, groupName);
					}
				}else if(args.length == 3) {
					if(args[0].equalsIgnoreCase("add")) {
						Player target = Bukkit.getPlayer(args[1]);
						String groupName = args[2];
						if(target == null) {
							cs.sendMessage(core.getPrefix() + "§cThis player is currently unavailable.");
						}else{
							core.getGroups().addToGroup(cs, target, groupName);
						}
					}else if(args[0].equalsIgnoreCase("rem")) {
						Player target = Bukkit.getPlayer(args[1]);
						String groupName = args[2];
						if(target != null) {
							core.getGroups().removeFromGroup(cs, target, groupName);
						}else{
							cs.sendMessage(core.getPrefix() + "§cThis player is currently unavailable.");
						}
					}else if(args[0].equalsIgnoreCase("addp")) {
						String permission = args[1];
						String groupName = args[2];
						core.getGroups().addPermission(cs, groupName, permission);
					}else if(args[0].equalsIgnoreCase("remp")) {
						String permission = args[1];
						String groupName = args[2];
						core.getGroups().removePermission(cs, groupName, permission);
					}else if(args[0].equalsIgnoreCase("setprefix")) {
						String prefix = args[1];
						String groupName = args[2];
						core.getGroups().setPrefix(cs, groupName, prefix);
					}else if(args[0].equalsIgnoreCase("setsuffix")) {
						String suffix = args[1];
						String groupName = args[2];
						core.getGroups().setSuffix(cs, groupName, suffix);
					}
				}
			}else{
				cs.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
			}
		}else{
			sender.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

	public void sendHelp(CommandSender player, int page) {
		player.sendMessage("§7[§a§lELERION§7] §9Help §8| §6Page§8: §e" + page);
		if (page == 1) {
			player.sendMessage("§e/group 2 §8- §7Shows a page two.");
			player.sendMessage("§e/group create <name> §8- §7Creates a new group.");
			player.sendMessage("§e/group remove <name> §8- §7Removes a group.");
			player.sendMessage("§e/group add <player> <group> §8- §7Adds player to a group.");
			player.sendMessage("§e/group rem <player> <group> §8- §7Removes player from a group.");
			player.sendMessage("§e/group addp <permission> <group> §8- §7Adds permission to a group.");
		} else {
			player.sendMessage("§e/group remp <permission> <group> §8- §7Removes permission from a group.");
			player.sendMessage("§e/group setprefix <prefix> <group> §8- §7Sets group's prefix.");
			player.sendMessage("§e/group setsuffix <suffix> <group> §8- §7Sets group's suffix.");
		}
	}

}
