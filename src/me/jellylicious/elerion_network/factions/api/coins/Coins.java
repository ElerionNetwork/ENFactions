package me.jellylicious.elerion_network.factions.api.coins;

import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Coins {
	
	Core core;
	
	public Coins(Core core) {
		this.core = core;
	}
	
	public int getCoins(Player p) {
		return (int) core.getPlayerAccount().get(p.getUniqueId().toString(), "enPlayerData", "Coins", "UUID");
	}
	
	public void addCoins(Player p, int coins) {
		coins += getCoins(p);
		core.getMySQL().update("UPDATE enPlayerData SET Coins='"+coins+"' WHERE UUID='"+p.getUniqueId().toString()+"';");
	}
	
	public void removeCoins(Player p, int coins) {
		coins = getCoins(p) - coins;
		core.getMySQL().update("UPDATE enPlayerData SET Coins='"+coins+"' WHERE UUID='"+p.getUniqueId().toString()+"';");
	}

}
