package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jellylicious.elerion_network.factions.Core;

public class Vote implements CommandExecutor {

	Core core;
	public Vote(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0) {
				Inventory inv = Bukkit.createInventory(null, 27, "§6§lVote or spend tokens!");
				ItemStack paper = core.getItemManager().createItemWithMaterial(Material.PAPER, 0, 1, "§a§lVote Links", "", "§7Click me and I will", "§7send you all vote links!");
				ItemStack diamond = core.getItemManager().createItemWithMaterial(Material.DIAMOND, 0, 1, "§a§lSpend Tokens", "", "§7Click me and I will", "§7take you to the shop!");
				ItemStack barrier = core.getItemManager().createItemWithMaterial(Material.BARRIER, 0, 1, "§c§lEXIT", "", "§7If you click me,", "§7I will close the inventory!");
				inv.setItem(3, paper);
				inv.setItem(5, diamond);
				inv.setItem(22, barrier);
				p.openInventory(inv);
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /vote!");
			}
		}
		return true;
	}

}
