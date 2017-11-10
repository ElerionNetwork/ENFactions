package me.jellylicious.elerion_network.factions.command;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Fly implements CommandExecutor {
	
	ArrayList<Player> flyEnabled = new ArrayList<Player>();

	Core core;
	public Fly(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0) {
				if(core.getGroups().hasPermission(p, "elerion.command.fly")) {
					if(!flyEnabled.contains(p)) {
						flyEnabled.add(p);
						p.setAllowFlight(true);
						p.setFlying(true);
						p.sendMessage(core.getPrefix() + "§aHave fun flying!");
					}else{
						flyEnabled.remove(p);
						p.setAllowFlight(false);
						p.setFlying(false);
						p.sendMessage(core.getPrefix() + "§aYou can not fly anymore.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else if(args.length == 1) {
				if(core.getGroups().hasPermission(p, "elerion.command.fly.others")) {
					Player t = Bukkit.getPlayer(args[0]);
					if(t != null) {
						if(!flyEnabled.contains(t)) {
							flyEnabled.add(t);
							t.setAllowFlight(true);
							t.setFlying(true);
							p.sendMessage(core.getPrefix() + t.getName() + " §acan now fly.");
							t.sendMessage(core.getPrefix() + p.getName() + " §ahas enabled your flight abilities!");
						}else{
							flyEnabled.remove(t);
							t.setAllowFlight(false);
							t.setFlying(false);
							p.sendMessage(core.getPrefix() + t.getName() + " §acan no longer fly.");
							t.sendMessage(core.getPrefix() + p.getName() + " §ahas disabled your flight abilities!");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /fly <target[optional]>!");
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

}
