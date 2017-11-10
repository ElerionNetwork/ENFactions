package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Spawn implements CommandExecutor {

	Core core;
	public Spawn(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)) {
			cs.sendMessage(core.getPrefix() + "§cOnly players can use this command.");
			return true;
		}else{
			Player p = (Player) cs;
			if(args.length > 0) {
				p.sendMessage(core.getPrefix() + "§cWrong usage. Please use /spawn!");
				return true;
			}else{
				FileConfiguration cfg = core.getFileManager().getConfiguration("SpawnLocation.yml", "plugins//ENFactions//locations//");
				if(cfg.isConfigurationSection("Location")) {
					Location loc = core.getLocationHelper().getLocation("SpawnLocation.yml", "Location", "plugins//ENFactions//locations//");
					p.teleport(loc);
					p.sendMessage(core.getPrefix() + "§aYou've been teleported to spawn location!");
				}else{
					p.sendMessage(core.getPrefix() + "§cSpawn location does not exist! Please contact server administrators.");
				}
			}
		}
		return true;
	}

}
