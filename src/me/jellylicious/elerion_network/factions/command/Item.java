package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.jellylicious.elerion_network.factions.Core;

public class Item implements CommandExecutor {

	Core core;
	public Item(Core core) {
		this.core = core;
	}

	//item <id/name>
	//item <id/name> <amount>
	//item <id/name> <target>
	//item <id/name> <amount> <target>
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 1) {
				if(core.getGroups().hasPermission(p, "elerion.command.item")) {
					Material m = null;
					if(core.getNumericAPI().isNumeric(args[0])) m = Material.getMaterial(Integer.parseInt(args[0]));
					else m = Material.valueOf(args[0]);
					if(m != null) {
						ItemStack item = new ItemStack(m, 1);
						p.getInventory().addItem(item);
						p.sendMessage(core.getPrefix() + "§aYou received §71x " + item.getType().toString() + "§a.");
					}else{
						p.sendMessage(core.getPrefix() + "§cThis item does not exist.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else if(args.length == 2) {
				Material m = null;
				if(core.getNumericAPI().isNumeric(args[0])) m = Material.getMaterial(Integer.parseInt(args[0]));
				else m = Material.getMaterial(args[0]);
				if(core.getNumericAPI().isNumeric(args[1])) {
					if(core.getGroups().hasPermission(p, "elerion.command.item")) {
						if(m != null) {
							int amount = Integer.parseInt(args[1]);
							ItemStack item = new ItemStack(m, amount);
							p.getInventory().addItem(item);
							p.sendMessage(core.getPrefix() + "§aYou received §7" + amount + "x " + item.getType().toString() + "§a.");
						}else{
							p.sendMessage(core.getPrefix() + "§cThis item does not exist.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
					}
				}else{
					Player t = Bukkit.getPlayer(args[1]);
					if(core.getGroups().hasPermission(p, "elerion.command.item.others")) {
						if(t != null) {
							Material mat = null;
							if(core.getNumericAPI().isNumeric(args[0])) mat = Material.getMaterial(Integer.parseInt(args[0]));
							else mat = Material.getMaterial(args[0]);
							if(mat != null) {
								ItemStack item = new ItemStack(mat, 1);
								t.getInventory().addItem(item);
								p.sendMessage(core.getPrefix() + "§aYou gave §71x " + item.getType().toString() + " §ato §7" + t.getName() + "§a.");
								t.sendMessage(core.getPrefix() + "§aYou received §71x " + item.getType().toString() + " §afrom §7" + p.getName() + "§a.");
							}else{
								p.sendMessage(core.getPrefix() + "§cThis item does not exist.");
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
					}
				}
			}else if(args.length == 3) {
				if(core.getGroups().hasPermission(p, "elerion.command.item.others")) {
					Material m = null;
					if(core.getNumericAPI().isNumeric(args[0])) m = Material.getMaterial(Integer.parseInt(args[0]));
					else m = Material.getMaterial(args[0]);
					if(m != null) {
						Player t = Bukkit.getPlayer(args[2]);
						if(t != null) {
							int amount = Integer.parseInt(args[1]);
							ItemStack item = new ItemStack(m, amount);
							t.getInventory().addItem(item);
							p.sendMessage(core.getPrefix() + "§aYou gave §7" + amount + "x " + item.getType().toString() + " §ato §7" + t.getName() + "§a.");
							t.sendMessage(core.getPrefix() + "§aYou received §7" + amount + "x " + item.getType().toString() + " §afrom §7" + p.getName() + "§a.");
						}else{
							p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cThis item does not exist.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use one of these commands:");
				p.sendMessage("§e/item <id/name>");
				p.sendMessage("§e/item <id/name> <amount>");
				p.sendMessage("§e/item <id/name> <target>");
				p.sendMessage("§e/item <id/name> <amount> <target>");
			}
		}
		return true;
	}

}
