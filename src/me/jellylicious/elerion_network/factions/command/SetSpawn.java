package me.jellylicious.elerion_network.factions.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class SetSpawn implements CommandExecutor {

	Core core;

	public SetSpawn(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)) {
			cs.sendMessage(core.getPrefix() + "§cOnly players can use this command.");
			return true;
		}else{
			Player p = (Player) cs;
			if(core.getGroups().hasPermission(p, "elerion.command.setspawn")) {
				if(args.length > 0) {
					p.sendMessage(core.getPrefix() + "§cWrong usage. Please use /setspawn!");
					return true;
				}else{
					core.getLocationHelper().saveLocation(p.getLocation(), "SpawnLocation.yml", "Location", "plugins//ENFactions//locations//");
					p.sendMessage(core.getPrefix() + "§aSpawn location was saved successfully!");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cYou have no permission to do that!");
			}
		}
		return true;
	}

}
