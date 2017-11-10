package me.jellylicious.elerion_network.factions.api.punishment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Punisher {
	
	public enum PunishmentType {
		WARN, KICK, BAN, TEMPBAN;
	}

	Core core;
	
	public Punisher(Core core) {
		this.core = core;
	}
	
	public void issuePunishment(CommandSender issuer, Player punished, PunishmentType type, int length, String lengthUnit, String reason) throws SQLException {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		if(issuer instanceof Player) {
			Player p = (Player) issuer;
			if(!reason.contains("'")) {
				if(type == PunishmentType.WARN) {
					core.getMySQL().update("INSERT INTO enPunishments(ID, IssuedBy, Punished, PunishmentType, DateIssued, ExpirationDate, Reason, Active, Appealed, AppealedBy) VALUES "
							+ "('0', '" + p.getUniqueId().toString() + "', '" + punished.getUniqueId().toString() + "', 'WARN', '" + currentTime + "', 'NEVER', '" + reason + "', 'false', 'false', 'null');");
					savePunishmentIDToPlayer(punished);
					p.sendMessage(core.getPrefix() + "§aSuccessfully warned §7" + punished.getName() + " §afor: §7" + reason + "§a.");
					punished.sendMessage(core.getPrefix() + "§cYou've been warned by §7" + p.getName() + " §cfor: §4" + reason + "§c.");
				}else if(type == PunishmentType.KICK) {
					core.getMySQL().update("INSERT INTO enPunishments(ID, IssuedBy, Punished, PunishmentType, DateIssued, ExpirationDate, Reason, Active, Appealed, AppealedBy) VALUES "
							+ "('0', '" + p.getUniqueId().toString() + "', '" + punished.getUniqueId().toString() + "', 'KICK', '" + currentTime + "', 'NEVER', '" + reason + "', 'false', 'false', 'null');");
					punished.kickPlayer("§c§lYou've been kicked by §7" + p.getName() + "§c§l! \n" 
							+ "§c§lREASON: §b" + reason);
					savePunishmentIDToPlayer(punished);
					p.sendMessage(core.getPrefix() + "§aSuccessfully kicked §7" + punished.getName() + " §afor: §7" + reason + "§a.");
					core.getServer().broadcastMessage(core.getPrefix() + punished.getName() + " §awas kicked by §7" + p.getName() + " §afor: §7" + reason + "§a.");
				}else if(type == PunishmentType.BAN) {
					core.getMySQL().update("INSERT INTO enPunishments(ID, IssuedBy, Punished, PunishmentType, DateIssued, ExpirationDate, Reason, Active, Appealed, AppealedBy) VALUES "
							+ "('0', '" + p.getUniqueId().toString() + "', '" + punished.getUniqueId().toString() + "', 'PERMBAN', '" + currentTime + "', 'NEVER', '" + reason + "', 'true', 'false', 'null');");
					punished.kickPlayer("§c§lYou've been banned from the server by §7" + p.getName() + "§c§l! \n" 
							+ "§c§lREASON: §b" + reason);
					savePunishmentIDToPlayer(punished);
					p.sendMessage(core.getPrefix() + "§aSuccessfully banned §7" + punished.getName() + " §afor: §7" + reason + "§a.");
					core.getServer().broadcastMessage(core.getPrefix() + punished.getName() + " §awas banned from the server by §7" + p.getName() + " §afor: §7" + reason + "§a.");
				}else if(type == PunishmentType.TEMPBAN) {
					Date myDate = null;
					try {
						myDate = sdf.parse(currentTime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(myDate != null) {
						Calendar cal = Calendar.getInstance();
					    cal.setTime(myDate);
					    if(length != 0 && lengthUnit != null) {
						    if(lengthUnit.equals("s")) cal.add(Calendar.SECOND, length);
						    else if(lengthUnit.equals("min")) cal.add(Calendar.MINUTE, length);
						    else if(lengthUnit.equals("h")) cal.add(Calendar.HOUR, length);
						    else if(lengthUnit.equals("d")) cal.add(Calendar.HOUR, length * 24);
						    else if(lengthUnit.equals("w")) cal.add(Calendar.HOUR, length * 168);
						    else if(lengthUnit.equals("m")) cal.add(Calendar.MONTH, length);
						    else if(lengthUnit.equals("y")) cal.add(Calendar.YEAR, length);
						    else p.sendMessage(core.getPrefix() + "§cIncorrect length unit.");
						    Date futureDate = cal.getTime();
						    String futureTime = sdf.format(futureDate);
						    core.getMySQL().update("INSERT INTO enPunishments(ID, IssuedBy, Punished, PunishmentType, DateIssued, ExpirationDate, Reason, Active, Appealed, AppealedBy) VALUES "
									+ "('0', '" + p.getUniqueId().toString() + "', '" + punished.getUniqueId().toString() + "', 'TEMPBAN', '" + currentTime + "', '" + futureTime + "', '" + reason + "', 'true', 'false', 'null');");
							punished.kickPlayer("§c§lYou've been temporarily banned from the server by §7" + p.getName() + "§c§l! \n" 
									+ "§c§lREASON: §b" + reason + " \n" + "§cYour ban expires on §7" + futureTime);
							savePunishmentIDToPlayer(punished);
							p.sendMessage(core.getPrefix() + "§aSuccessfully tempbanned §7" + punished.getName() + " §afor: §7" + reason + "§a. Ban expires on §7" + futureTime);
							core.getServer().broadcastMessage(core.getPrefix() + punished.getName() + " §awas tempbanned from the server by §7" + p.getName() + " §afor: §7" + reason + "§a.");
					    }else{
					    	p.sendMessage(core.getPrefix() + "§cLength should never be 0.");
					    }
					}else{
						p.sendMessage(core.getPrefix() + "§cSomething went wrong while setting an expiration date.");
					}
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cYour reason should not contain ' character.");
			}
		}
	}
	
	public void issueOfflinePunishment(CommandSender issuer, String uuid, String playerName, PunishmentType type, int length, String lengthUnit, String reason) throws SQLException {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		if(issuer instanceof Player) {
			Player p = (Player) issuer;
			if(!reason.contains("'")) {
				if(type == PunishmentType.WARN) {
					core.getMySQL().update("INSERT INTO enPunishments(ID, IssuedBy, Punished, PunishmentType, DateIssued, ExpirationDate, Reason, Active, Appealed, AppealedBy) VALUES "
							+ "('0', '" + p.getUniqueId().toString() + "', '" + uuid + "', 'WARN', '" + currentTime + "', 'NEVER', '" + reason + "', 'true', 'false', 'null');");
					saveOfflinePunishmentIDToPlayer(uuid);
					p.sendMessage(core.getPrefix() + "§aSuccessfully warned §7" + playerName + " §afor: §7" + reason + "§a.");
				}else if(type == PunishmentType.BAN) {
					core.getMySQL().update("INSERT INTO enPunishments(ID, IssuedBy, Punished, PunishmentType, DateIssued, ExpirationDate, Reason, Active, Appealed, AppealedBy) VALUES "
							+ "('0', '" + p.getUniqueId().toString() + "', '" + uuid + "', 'PERMBAN', '" + currentTime + "', 'NEVER', '" + reason + "', 'true', 'false', 'null');");
					saveOfflinePunishmentIDToPlayer(uuid);
					p.sendMessage(core.getPrefix() + "§aSuccessfully banned §7" + playerName + " §afor: §7" + reason + "§a.");
					core.getServer().broadcastMessage(core.getPrefix() + playerName + " §awas banned from the server by §7" + p.getName() + " §afor: §7" + reason + "§a.");
				}else if(type == PunishmentType.TEMPBAN) {
					Date myDate = null;
					try {
						myDate = sdf.parse(currentTime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if(myDate != null) {
						Calendar cal = Calendar.getInstance();
					    cal.setTime(myDate);
					    if(length != 0 && lengthUnit != null) {
						    if(lengthUnit.equals("s")) cal.add(Calendar.SECOND, length);
						    else if(lengthUnit.equals("min")) cal.add(Calendar.MINUTE, length);
						    else if(lengthUnit.equals("h")) cal.add(Calendar.HOUR, length);
						    else if(lengthUnit.equals("d")) cal.add(Calendar.HOUR, length * 24);
						    else if(lengthUnit.equals("w")) cal.add(Calendar.HOUR, length * 168);
						    else if(lengthUnit.equals("m")) cal.add(Calendar.MONTH, length);
						    else if(lengthUnit.equals("y")) cal.add(Calendar.YEAR, length);
						    else p.sendMessage(core.getPrefix() + "§cIncorrect length unit.");
						    Date futureDate = cal.getTime();
						    String futureTime = sdf.format(futureDate);
						    core.getMySQL().update("INSERT INTO enPunishments(ID, IssuedBy, Punished, PunishmentType, DateIssued, ExpirationDate, Reason, Active, Appealed, AppealedBy) VALUES "
									+ "('0', '" + p.getUniqueId().toString() + "', '" + uuid + "', 'TEMPBAN', '" + currentTime + "', '" + futureTime + "', '" + reason + "', 'true', 'false', 'null');");
							saveOfflinePunishmentIDToPlayer(uuid);
							p.sendMessage(core.getPrefix() + "§aSuccessfully tempbanned §7" + playerName + " §afor: §7" + reason + "§a. Ban expires on §7" + futureTime);
							core.getServer().broadcastMessage(core.getPrefix() + playerName + " §awas tempbanned from the server by §7" + p.getName() + " §afor: §7" + reason + "§a.");
					    }else{
					    	p.sendMessage(core.getPrefix() + "§cLength should never be 0.");
					    }
					}else{
						p.sendMessage(core.getPrefix() + "§cSomething went wrong while setting an expiration date.");
					}
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cYour reason should not contain ' character.");
			}
		}
	}
	
	private void savePunishmentIDToPlayer(Player p) throws SQLException {
		int punishmentID = core.getMySQL().countRows("enPunishments");
		core.getMySQL().update("UPDATE enPlayerData SET LastPunishment='" + punishmentID + "' WHERE UUID='" + p.getUniqueId().toString() + "';");
	}
	
	private void saveOfflinePunishmentIDToPlayer(String uuid) throws SQLException {
		int punishmentID = core.getMySQL().countRows("enPunishments");
		core.getMySQL().update("UPDATE enPlayerData SET LastPunishment='" + punishmentID + "' WHERE UUID='" + uuid + "';");
	}
	
	public boolean existsPunishment(int punishmentID) {
		try{
			ResultSet rs = core.getMySQL().getResult("SELECT * FROM enPunishments WHERE ID='" + punishmentID + "';");
			if(rs.next()) {
				return rs.getInt("ID") != 0;
			}
			rs.close();
			return false;
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public int getLastPunishment(Player p) {
		if(core.getPlayerAccount().existsPlayer(p.getUniqueId().toString())) {
			try{
				int id = 0;
				ResultSet rs = core.getMySQL().getResult("SELECT LastPunishment FROM enPlayerData WHERE UUID='" + p.getUniqueId().toString() + "';");
				if(rs.next()) {
					id = rs.getInt("LastPunishment");
				}
				rs.close();
				return id;
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		return 0;
	}
	
	public boolean isPunishmentActive(int punishmentID) {
		if(existsPunishment(punishmentID)) {
			try{
				ResultSet rs = core.getMySQL().getResult("SELECT Active FROM enPunishments WHERE ID='" + punishmentID + "';");
				if(rs.next()) {
					return Boolean.parseBoolean(rs.getString("Active"));
				}
				rs.close();
				return false;
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}
	
	public String getPunishmentType(int punishmentID) {
		if(existsPunishment(punishmentID)) {
			try{
				String type = "";
				ResultSet rs = core.getMySQL().getResult("SELECT PunishmentType FROM enPunishments WHERE ID='" + punishmentID + "';");
				if(rs.next()) {
					type = rs.getString("PunishmentType");
				}
				rs.close();
				return type;
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	public String getReason(int punishmentID) {
		if(existsPunishment(punishmentID)) {
			try{
				String reason = "";
				ResultSet rs = core.getMySQL().getResult("SELECT Reason FROM enPunishments WHERE ID='" + punishmentID + "';");
				if(rs.next()) {
					reason = rs.getString("Reason");
				}
				rs.close();
				return reason;
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public String getWhoPunished(int punishmentID) {
		if(existsPunishment(punishmentID)) {
			try{
				String uuid = "";
				ResultSet rs = core.getMySQL().getResult("SELECT IssuedBy FROM enPunishments WHERE ID='" + punishmentID + "';");
				if(rs.next()) {
					uuid = rs.getString("IssuedBy");
				}
				if(uuid.equals("CONSOLE")) {
					rs.close();
					return uuid;
				}else{
					String name = "";
					rs = core.getMySQL().getResult("SELECT InGameName FROM enPlayerData WHERE UUID='" + uuid + "';");
					if(rs.next()) {
						name = rs.getString("InGameName");
					}
					rs.close();
					return name;
				}
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	public String getExpirationDate(int punishmentID) {
		if(existsPunishment(punishmentID)) {
			try{
				String date = "";
				ResultSet rs = core.getMySQL().getResult("SELECT ExpirationDate FROM enPunishments WHERE ID='" + punishmentID + "';");
				if(rs.next()) {
					date = rs.getString("ExpirationDate");
				}
				rs.close();
				return date;
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	public boolean timeToUnban(int punishmentID) {
		if(isPunishmentActive(punishmentID)) {
			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentTime = sdf.format(dt);
			Date todayDate = null;
			try {
				todayDate = sdf.parse(currentTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String expirationDate = "";
			try {
				ResultSet rs = core.getMySQL().getResult("SELECT ExpirationDate FROM enPunishments WHERE ID='" + punishmentID + "';");
				if(rs.next()) expirationDate = rs.getString("ExpirationDate");
			}catch(SQLException ex){
				ex.printStackTrace();
			}
			if(!expirationDate.equals("NEVER")) {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = null;
				try {
					date = format.parse(expirationDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(todayDate.after(date)) return true;
				else return false;
			}
			return false;
		}
		return false;
	}
	
	public boolean wasPunishmentAppealed(int punishmentID) {
		try{
			boolean appealed = false;
			ResultSet rs = core.getMySQL().getResult("SELECT Appealed FROM enPunishments WHERE ID='" + punishmentID + "';");
			if(rs.next()) {
				appealed = Boolean.parseBoolean(rs.getString("Appealed"));
			}
			rs.close();
			return appealed;
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public void changeStatusToInactive(int punishmentID) {
		if(existsPunishment(punishmentID)) {
			if(isPunishmentActive(punishmentID)) {
				core.getMySQL().update("UPDATE enPunishments SET Active='false' WHERE ID='" + punishmentID + "';");
			}
		}
	}
	
	public void appealPunishment(CommandSender sender, int punishmentID) {
		if(sender != null) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(existsPunishment(punishmentID)) {
					if(!wasPunishmentAppealed(punishmentID)) {
						String punishedUuid = "";
						try{
							ResultSet rs = core.getMySQL().getResult("SELECT Punished FROM enPunishments WHERE ID='" + punishmentID + "';");
							if(rs.next()) punishedUuid = rs.getString("Punished");
						}catch(SQLException ex) {
							ex.printStackTrace();
						}
						Player target = Bukkit.getPlayer(punishedUuid);
						core.getMySQL().update("UPDATE enPunishments SET Active='false' WHERE ID='" + punishmentID + "';");
						core.getMySQL().update("UPDATE enPunishments SET Appealed='true' WHERE ID='" + punishmentID + "';");
						core.getMySQL().update("UPDATE enPunishments SET AppealedBy='" + p.getUniqueId() + "' WHERE ID='" + punishmentID + "';");
						p.sendMessage(core.getPrefix() + "§aSuccessfully appealed punishment with ID §7" + punishmentID + "§c!");
						if(target != null) target.sendMessage(core.getPrefix() + "§7" + p.getName() + " §ahas just appealed your punishment with ID §7" + punishmentID + "§a!");
					}else{
						p.sendMessage(core.getPrefix() + "§cPunishment with ID §7" + punishmentID + " §cwas already appealed.");
					}
				}else{
					sender.sendMessage(core.getPrefix() + "§cPunishment with ID §7" + punishmentID + " §cdoes not exist.");
				}
			}else{
				if(existsPunishment(punishmentID)) {
					if(!wasPunishmentAppealed(punishmentID)) {
						String punishedUuid = "";
						try{
							ResultSet rs = core.getMySQL().getResult("SELECT Punished FROM enPunishments WHERE ID='" + punishmentID + "';");
							if(rs.next()) punishedUuid = rs.getString("Punished");
						}catch(SQLException ex) {
							ex.printStackTrace();
						}
						Player target = Bukkit.getPlayer(punishedUuid);
						core.getMySQL().update("UPDATE enPunishments SET Active='false' WHERE ID='" + punishmentID + "';");
						core.getMySQL().update("UPDATE enPunishments SET Appealed='true' WHERE ID='" + punishmentID + "';");
						core.getMySQL().update("UPDATE enPunishments SET AppealedBy='" + sender.getName() + "' WHERE ID='" + punishmentID + "';");
						sender.sendMessage(core.getPrefix() + "§aSuccessfully appealed punishment with ID §7" + punishmentID + "§c!");
						if(target != null) target.sendMessage(core.getPrefix() + "§7" + sender.getName() + " §ahas just appealed your punishment with ID §7" + punishmentID + "§a!");
					}else{
						sender.sendMessage(core.getPrefix() + "§cPunishment with ID §7" + punishmentID + " §cwas already appealed.");
					}
				}else{
					sender.sendMessage(core.getPrefix() + "§cPunishment with ID §7" + punishmentID + " §cdoes not exist.");
				}
			}
		}
	}
}
