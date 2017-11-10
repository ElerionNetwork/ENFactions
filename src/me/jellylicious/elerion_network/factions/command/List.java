package me.jellylicious.elerion_network.factions.command;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class List implements CommandExecutor {

	Core core;
	public List(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0) {
				sendPlayerList(p);
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /list!");
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}
	
	private void sendPlayerList(Player receiver) {
		ArrayList<String> onlinePlayers = new ArrayList<String>();
		receiver.sendMessage(core.getPrefix() + "§9Online players §8(§6" + Bukkit.getOnlinePlayers().size() + "§8/§6" + Bukkit.getMaxPlayers() + "§8):");
		for(Player p : Bukkit.getOnlinePlayers()) {
			onlinePlayers.add(p.getName());
		}
		String playersText = onlinePlayers.toString();
		playersText = playersText.replace("[", "");
		playersText = playersText.replace("]", "");
		receiver.sendMessage("§b" + playersText);
	}

}
