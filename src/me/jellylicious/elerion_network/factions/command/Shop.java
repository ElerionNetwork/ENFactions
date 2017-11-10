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

public class Shop implements CommandExecutor {

	Core core;
	public Shop(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0) {
				Inventory inv = Bukkit.createInventory(null, 27, "§6§lVirtual Shop");
				ItemStack cobblestone = core.getItemManager().createItemWithMaterial(Material.COBBLESTONE, 0, 32, "", "", "§6§lBuying price: §715", "§6§lSelling price: §77");
				ItemStack oakLog = core.getItemManager().createItemWithID(17, 0, 32, "", "", "§6§lBuying price: §740", "§6§lSelling price: §720");
				ItemStack dirt = core.getItemManager().createItemWithMaterial(Material.DIRT, 0, 32, "", "", "§6§lBuying price: §710", "§6§lSelling price: §75");
				ItemStack apple = core.getItemManager().createItemWithMaterial(Material.APPLE, 0, 32, "", "", "§6§lBuying price: §760", "§6§lSelling price: §730");
				ItemStack iron = core.getItemManager().createItemWithMaterial(Material.IRON_INGOT, 0, 32, "", "", "§6§lBuying price: §7130", "§6§lSelling price: §765");
				ItemStack coal= core.getItemManager().createItemWithMaterial(Material.COAL, 0, 32, "", "", "§6§lBuying price: §790", "§6§lSelling price: §745");
				ItemStack barrier = core.getItemManager().createItemWithMaterial(Material.BARRIER, 0, 1, "§c§lEXIT", null);
				inv.setItem(1, cobblestone);
				inv.setItem(2, oakLog);
				inv.setItem(3, dirt);
				inv.setItem(5, apple);
				inv.setItem(6, iron);
				inv.setItem(7, coal);
				inv.setItem(22, barrier);
				p.openInventory(inv);
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /shop!");
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

}
