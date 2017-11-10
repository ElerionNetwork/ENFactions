package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Tpa implements CommandExecutor {

	Core core;
	public Tpa(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0 || args.length > 1) {
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /tpa <target>!");
			}else{
				Player t = Bukkit.getPlayer(args[0]);
				if(t != null) {
					core.getTPA().put(p, t);
					core.getTPA().put(t, p);
					p.sendMessage(core.getPrefix() + "§aTeleportation request sent to §7" + t.getName() + "§a.");
					t.sendMessage(core.getPrefix() + "§7" + p.getName() + " §ahas requested to teleport to you. To accept it, use /tpaccept. To deny it, simply ignore it. You have 60 seconds to decide!");
					core.getServer().getScheduler().scheduleSyncDelayedTask(core, new Runnable() {

						@Override
						public void run() {
							if(core.getTPA().containsKey(p) && core.getTPA().containsValue(t)) {
								t.sendMessage(core.getPrefix() + "§aTeleportation request sent by §7" + core.getTPA().get(t).getName() + " §ahas expired.");
								core.getTPA().remove(t, p);
								core.getTPA().remove(p, t);
							}
						}
						
					}, 60*20);
				}else{
					p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
				}
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}

}
