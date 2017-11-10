package me.jellylicious.elerion_network.factions.api.votetokens;

import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class VoteTokens {

	Core core;
	public VoteTokens(Core core) {
		this.core = core;
	}
	
	public int getTokens(Player p) {
		return (int) core.getPlayerAccount().get(p.getUniqueId().toString(), "enPlayerData", "VoteTokens", "UUID");
	}
	
	public void addTokens(Player p, int tokens) {
		tokens += getTokens(p);
		core.getMySQL().update("UPDATE enPlayerData SET VoteTokens='"+tokens+"' WHERE UUID='"+p.getUniqueId().toString()+"';");
	}
	
	public void removeTokens(Player p, int tokens) {
		tokens = getTokens(p) - tokens;
		core.getMySQL().update("UPDATE enPlayerData SET VoteTokens='"+tokens+"' WHERE UUID='"+p.getUniqueId().toString()+"';");
	}

}
