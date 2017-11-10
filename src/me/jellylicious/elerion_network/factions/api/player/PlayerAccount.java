package me.jellylicious.elerion_network.factions.api.player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class PlayerAccount {

	Core core;
	public PlayerAccount(Core core) {
		this.core = core;
	}
	
	public boolean existsPlayer(String uuid) {
		try{
			ResultSet rs = core.getMySQL().getResult("SELECT * FROM enPlayerData WHERE UUID='" + uuid + "';");
			if(rs.next()) {
				return rs.getString("UUID") != null;
			}
			rs.close();
			return false;
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public void registerPlayer(Player p) {
		if(!existsPlayer(p.getUniqueId().toString())) {
			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);
			core.getMySQL().update("INSERT INTO enPlayerData(ID, UUID, InGameName, Rank, JoinDate, LastSeen, Joins, Coins, Kills, Deaths, EmailAddress, PasswordHash, LastPunishment, MiningXP, MiningLevel, FishingXP, FishingLevel, WoodcuttingXP, WoodcuttingLevel, SwordsXP, SwordsLevel, AgilityXP, AgilityLevel, LevelsToRedeem, CombatLog, Votes, VoteTokens) VALUES "
					+ "('0', '" + p.getUniqueId().toString() + "', '" + p.getName() + "', 'MEMBER', '" + currentTime + "', 'NOW', '1', '0', '0', '0', 'null', 'null', '0', '0', '1', '0', '1', '0', '1', '0', '1', '0', '1', '0', 'false', '0', '0');");
		}else{
			core.getMySQL().update("UPDATE enPlayerData SET InGameName='" + p.getName() + "' WHERE UUID='" + p.getUniqueId().toString() + "';");
		}
	}
	
	public boolean hasPlayerAccount(String playerUUID) {
		String email, passwordHash;
		if(existsPlayer(playerUUID)) {
			try {
				ResultSet rs;
				rs = core.getMySQL().getResult("SELECT * FROM enPlayerData WHERE UUID='" + playerUUID + "'");
				if(rs.next()) {
					email = rs.getString("EmailAddress");
					passwordHash = rs.getString("PasswordHash");
					return (!email.equalsIgnoreCase("null")) && (!passwordHash.equalsIgnoreCase("null"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void createPlayerAccount(String playerUUID, String email, String passwordHash) {
		if(existsPlayer(playerUUID) && !hasPlayerAccount(playerUUID)) {
			core.getMySQL().update("UPDATE enPlayerData SET EmailAddress='" + email + "' WHERE UUID='" + playerUUID + "'");
			core.getMySQL().update("UPDATE enPlayerData SET PasswordHash='" + passwordHash + "' WHERE UUID='" + playerUUID + "'");
		}
	}
	
	//RESET - set it to null.
	//DATE - set it to current date.
	//NOW - set it to "NOW".
	public void setLastSeen(String playerUUID, String value) {
		if(existsPlayer(playerUUID)) {
			if(value == "DATE") {
				Date dt = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String currentTime = sdf.format(dt);
				core.getMySQL().update("UPDATE enPlayerData SET LastSeen='" + currentTime + "' WHERE UUID='" + playerUUID + "';");
			}else if(value == "NOW") {
				core.getMySQL().update("UPDATE enPlayerData SET LastSeen='NOW' WHERE UUID='" + playerUUID + "';");
			}else{
				core.getMySQL().update("UPDATE enPlayerData SET LastSeen='null' WHERE UUID='" + playerUUID + "';");
			}
		}
	}
	
	public boolean isOnline(String uuid) {
		String online = "";
		if(existsPlayer(uuid)) {
			try {
				ResultSet rs;
				rs = core.getMySQL().getResult("SELECT LastSeen FROM enPlayerData WHERE UUID='" + uuid + "'");
				if(rs.next()) {
					online = rs.getString("LastSeen");
					if(online.equals("NOW")) return true;
					else return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public double getKDR(String playerUUID){
		if(existsPlayer(playerUUID)) {
			int k = (int) get(playerUUID, "enPlayerData", "Kills", "UUID");
			int d = (int) get(playerUUID, "enPlayerData", "Deaths", "UUID");
			if(d != 0) {
				double fkdr = k / d;
				int aux = (int)(fkdr*100);
				double kdr = aux/100d;
				return kdr;
			}else{
				return k;
			}
		}
		return 0.0;
	}
	
	public Object get(String whereResult, String database, String what, String where) {
		Object obj = null;
		try{
			ResultSet rs = core.getMySQL().getResult("SELECT " + what + " FROM " + database + " WHERE " + where + "='" + whereResult + "';");
			if(rs.next()) obj = rs.getObject(what);
			return obj;
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
		return obj;
	}
	
	public void set(String playerUUID, String table, String what, Object to) {
		if(existsPlayer(playerUUID)) {
			core.getMySQL().update("UPDATE " + table + " SET " + what + "='" + to + "' WHERE UUID='" + playerUUID + "';");
		}
	}

}
