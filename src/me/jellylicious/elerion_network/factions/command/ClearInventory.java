package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.jellylicious.elerion_network.factions.Core;

public class ClearInventory implements CommandExecutor {

	Core core;
	public ClearInventory(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0) {
				int count = 0;
				ItemStack[] items = p.getInventory().getContents();
				for(int i = 0; i < items.length; i++) {
					if(items[i] != null) {
						count += items[i].getAmount();
					}
				}
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
				p.sendMessage(core.getPrefix() + "§aSuccessfully cleared your own inventory §7(§c" + count + "§7).");
			}else if(args.length == 1) {
				if(core.getGroups().hasPermission(p, "elerion.command.clearinventory.others")) {
					Player target = Bukkit.getPlayer(args[0]);
					if(target != null) {
						int count = 0;
						ItemStack[] items = p.getInventory().getContents();
						for(int i = 0; i < items.length; i++) {
							if(items[i] != null) {
								count += items[i].getAmount();
							}
						}
						target.getInventory().clear();
						target.getInventory().setArmorContents(null);
						p.sendMessage(core.getPrefix() + "§aSuccessfully cleared §7" + target.getName() + "§a's inventory §7(§c" + count + "§7).");
						target.sendMessage(core.getPrefix() + "§aYour inventory was cleared by §7" + p.getName() + "§a.");
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /ci <target[optional]>!");
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

}
