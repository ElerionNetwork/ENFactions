package me.jellylicious.elerion_network.factions.api.groups;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Groups {
	
	public HashMap<String, ArrayList<String>> gap = new HashMap<String, ArrayList<String>>(); //Group name, permission list
	Core core;
	public Groups(Core core) {
		this.core = core;
	}
	
	public void createGroupFile(String fileName, String path) {
    	core.getFileManager().createNewFile(fileName, path);
        File f = core.getFileManager().getFile(fileName, path);
        FileConfiguration cfg = core.getFileManager().getConfiguration(fileName, path);
        List<String> permissions = new ArrayList<String>();
        permissions.add("A");
        permissions.add("Lot");
        permissions.add("Of");
        permissions.add("Permissions");
        cfg.options().copyDefaults(true);
        cfg.addDefault("Information.Prefix", "&7[&bPREFIX&7]");
        cfg.addDefault("Information.Suffix", "&8");
        cfg.addDefault("Information.Permissions", permissions);
        try{
            cfg.save(f);
        }catch(IOException ex) {
            core.getServer().getConsoleSender().sendMessage(core.getPrefix() + "§cCould not save file '" + fileName + "'.");
        }
	}
	
	public String getPlayerGroup(String playerUUID) {
		if(core.getPlayerAccount().existsPlayer(playerUUID)) {
			String group = null;
			try{
				ResultSet rs;
				rs = core.getMySQL().getResult("SELECT Rank FROM enPlayerData WHERE UUID='" + playerUUID + "';");
				if(rs.next()) {
					group = rs.getString("Rank");
				}
				return group.toUpperCase();
			}catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	public void addToGroup(CommandSender sender, Player target, String newGroup) {
		if(core.getPlayerAccount().existsPlayer(target.getUniqueId().toString())) {
			if(groupExists(newGroup)) {
				String currentGroup = getPlayerGroup(target.getUniqueId().toString());
				if(newGroup != currentGroup) {
					core.getMySQL().update("UPDATE enPlayerData SET Rank='" + newGroup.toUpperCase() + "' WHERE UUID='" + target.getUniqueId().toString() + "';");
					sender.sendMessage(core.getPrefix() + "§aYou added §7" + target.getName() + " §ato group §7" + newGroup + "§a!");
					target.sendMessage(core.getPrefix() + "§aYou were added to group §7" + newGroup + " §aby §7" + sender.getName() + "§a.");
				}else{
					sender.sendMessage(core.getPrefix() + target.getName() + " §cis already a member of this group.");
				}
			}else{
				sender.sendMessage(core.getPrefix() + "§cGroup §7" + newGroup + " §cdoes not exist.");
			}
		}else{
			sender.sendMessage(core.getPrefix() + "§cPlease make sure that this player is registered in our database.");
		}
	}
	
	public void removeFromGroup(CommandSender sender, Player target, String group) {
		if(core.getPlayerAccount().existsPlayer(target.getUniqueId().toString())) {
			if(groupExists(group)) {
				String currentGroup = getPlayerGroup(target.getUniqueId().toString());
				if(group.toUpperCase().equals(currentGroup.toUpperCase())) {
					core.getMySQL().update("UPDATE enPlayerData SET Rank='MEMBER' WHERE UUID='" + target.getUniqueId().toString() + "';");
					sender.sendMessage(core.getPrefix() + "§aYou removed §7" + target.getName() + " §afrom group §7" + group + "§a!");
					target.sendMessage(core.getPrefix() + "§aYou were removed from group §7" + group + " §aby §7" + sender.getName() + "§a.");
				}else{
					sender.sendMessage(core.getPrefix() + target.getName() + " §cis not a member of this group.");
				}
			}else{
				sender.sendMessage(core.getPrefix() + "§cGroup §7" + group + " §cdoes not exist.");
			}
		}else{
			sender.sendMessage(core.getPrefix() + "§cPlease make sure that this player is registered in our database.");
		}
	}
	
	public boolean groupExists(String group) {
		return core.getFileManager().getFile(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//").exists();
	}
	
	public List<String> getPermissions(String group) {
		List<String> permissions = new ArrayList<String>();
		if (groupExists(group)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//");
			permissions = cfg.getStringList("Information.Permissions");
			return permissions;
		}
		return null;
	}
	
	public boolean hasPermission(CommandSender target, String permission) {
		if(target instanceof ConsoleCommandSender) return true;
		if(target instanceof Player) {
			Player p = (Player) target;
			if(p.isOp()) return true;
			String group = getPlayerGroup(p.getUniqueId().toString());
			return getPermissions(group).contains(permission);
		}
		return false;
	}
	
	public void addPermission(CommandSender sender, String group, String permission) {
		if(groupExists(group)) {
			if(!getPermissions(group.toUpperCase()).contains(permission)) {
				FileConfiguration cfg = core.getFileManager().getConfiguration(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//");
				List<String> permissions = getPermissions(group.toUpperCase());
				permissions.add(permission);
				cfg.set("Information.Permissions", permissions);
				try {
					cfg.save(core.getFileManager().getFile(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//"));
					sender.sendMessage(core.getPrefix() + "§aPermission §7" + permission + " §asuccessfully added to group §7" + group + "§a!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				sender.sendMessage(core.getPrefix() + "§cGroup §7" + group + " §calready contains that permission.");
			}
		}else{
			sender.sendMessage(core.getPrefix() + "§cGroup §7" + group + " §cdoes not exist.");
		}
	}
	
	public void removePermission(CommandSender sender, String group, String permission) {
		if(groupExists(group)) {
			if(getPermissions(group).contains(permission)) {
				FileConfiguration cfg = core.getFileManager().getConfiguration(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//");
				List<String> permissions = cfg.getStringList("Information.Permissions");
				permissions.remove(permission);
				cfg.set("Information.Permissions", permissions);
				try {
					cfg.save(core.getFileManager().getFile(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//"));
					sender.sendMessage(core.getPrefix() + "§aPermission §7" + permission + " §asuccessfully removed from group §7" + group + "§a!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				sender.sendMessage(core.getPrefix() + "§cGroup §7" + group + " §cdoes not contain that permission.");
			}
		}else{
			sender.sendMessage(core.getPrefix() + "§cGroup §7" + group + " §cdoes not exist.");
		}
	}
	
	public String getPrefix(String group) {
		if(groupExists(group)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//");
			String prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("Information.Prefix"));
			return prefix;
		}
		return null;
	}
	
	public void setPrefix(CommandSender sender, String group, String prefix) {
		if(groupExists(group)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//");
			cfg.set("Information.Prefix", prefix);
			try {
				cfg.save(core.getFileManager().getFile(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//"));
				sender.sendMessage(core.getPrefix() + "§aPrefix was successfully changed to: " + prefix);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			sender.sendMessage(core.getPrefix() + "§cGroup §7" + group + " §cdoes not exist.");
		}
	}
	
	public String getSuffix(String group) {
		if(groupExists(group)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//");
			String suffix = ChatColor.translateAlternateColorCodes('&', cfg.getString("Information.Suffix"));
			return suffix;
		}
		return null;
	}
	
	public void setSuffix(CommandSender sender, String group, String suffix) {
		if(groupExists(group)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//");
			cfg.set("Information.Suffix", suffix);
			try {
				cfg.save(core.getFileManager().getFile(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//"));
				sender.sendMessage(core.getPrefix() + "§aSuffix was successfully changed to: " + suffix);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			sender.sendMessage(core.getPrefix() + "§cGroup §7" + group + " §cdoes not exist.");
		}
	}
	
	public List<String> getPlayersInGroup(String group) {
		if(groupExists(group)) {
			group.toUpperCase();
			List<String> players = new ArrayList<String>();
			ResultSet rs;
			rs = core.getMySQL().getResult("SELECT UUID FROM enPlayerData WHERE Rank='" + group + "'");
			try {
				while(rs.next()) {
					players.add(rs.getString("UUID"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return players;
		}
		return null;
	}
	
	public void createGroup(CommandSender sender, String group) {
		if (!groupExists(group)) {
			File file = core.getFileManager().createNewFile(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//");
			FileConfiguration cfg = core.getFileManager().getConfiguration(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//");
			cfg.set("Prefix", "§7Prefix");
			cfg.set("Suffix", "§7");
			cfg.set("Permissions", "");
			try {
				cfg.save(file);
			} catch (IOException e) {
				sender.sendMessage(core.getPrefix() + " §cSomething went wrong! ERROR: " + e.getMessage());
			}
			sender.sendMessage(core.getPrefix() + "§aSuccessfully created a new group named §7" + group + "§a!");
		} else {
			sender.sendMessage(core.getPrefix() + "§cThis group already exists!");
		}
	}
	
	public void removeGroup(CommandSender sender, String group) {
		if (groupExists(group)) {
			if(!group.equalsIgnoreCase("MEMBER")) {
			core.getFileManager().deleteFile(group.toUpperCase() + ".yml", "plugins//ENFactions//Groups//");
			ResultSet rs;
				rs = core.getMySQL().getResult("SELECT UUID FROM enPlayerData WHERE Rank='" + group.toUpperCase() + "'");
				List<String> players = new ArrayList<String>();
				try {
					while (rs.next()) {
						String uuid = rs.getString("UUID");
						players.add(uuid);
					}
				} catch (SQLException e) {
					sender.sendMessage(core.getPrefix() + "§cSomething went wrong! ERROR: " + e.getMessage());
				}
				for (String entry : players) {
					core.getMySQL().update("UPDATE enPlayerData SET Rank='MEMBER' WHERE UUID='" + entry + "'");
				}
				sender.sendMessage(core.getPrefix() + "§aSuccessfully removed a group named §7" + group + "§a!");
			}else{
				sender.sendMessage(core.getPrefix() + "§cYou can not delete this group.");
			}
		} else {
			sender.sendMessage(core.getPrefix() + "§cThis group does not exist.");
		}
	}

}
