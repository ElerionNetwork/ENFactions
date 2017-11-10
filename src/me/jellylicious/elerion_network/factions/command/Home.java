package me.jellylicious.elerion_network.factions.command;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Home implements CommandExecutor {

	Core core;
	public Home(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			File file = core.getFileManager().getFile(p.getUniqueId() + ".yml", "plugins//ENFactions//PlayerData//");
			FileConfiguration cfg = core.getFileManager().getConfiguration(p.getUniqueId() + ".yml", "plugins//ENFactions//PlayerData//");
			if(args.length == 0) {
				sendHelp(p, 1);
			}else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("list")) {
					if(!file.exists()) {
						p.sendMessage(core.getPrefix() + "§cThere are no homes available.");
						core.getFileManager().createNewFile(p.getUniqueId() + ".yml", "plugins//ENFactions//PlayerData//");
					}else{
						if(cfg.isConfigurationSection("Information.Home")) {
							Set<String> homes = cfg.getConfigurationSection("Information.Home").getKeys(false);
							if(homes.size() != 0) {
								p.sendMessage(core.getPrefix() + "§9§lHome List §7(§e" + homes.size() + "§7)");
								for(String home : homes) {
									p.sendMessage("§7✪ §e" + home);
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cThere are no homes available.");
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cThere are no homes available.");
						}
					}
				}else{
					String homeName = args[0];
					if(!file.exists()) {
						p.sendMessage(core.getPrefix() + "§cHome §7" + homeName + " §cdoes not exist.");
						core.getFileManager().createNewFile(p.getUniqueId() + ".yml", "plugins//ENFactions//PlayerData//");
					}else{
						if(cfg.isConfigurationSection("Information.Home." + homeName)) {
							Location loc = core.getLocationHelper().getLocation(p.getUniqueId() + ".yml", "Information.Home." + homeName + ".Location", "plugins//ENFactions//PlayerData//");
							p.teleport(loc);
							p.sendMessage(core.getPrefix() + "§aYou've been teleported to home §7" + homeName + "§a!");
						}else{
							p.sendMessage(core.getPrefix() + "§cHome §7" + homeName + " §cdoes not exist.");
						}
					}
				}
			}else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("set")) {
					String name = args[1];
					if(core.getGroups().hasPermission(p, "elerion.homes.1") && getHomesSize(p) >= 1) p.sendMessage(core.getPrefix() + "§cYou reached your home limit. Please delete one of your homes.");
					else if(core.getGroups().hasPermission(p, "elerion.homes.2") && getHomesSize(p) >= 2) p.sendMessage(core.getPrefix() + "§cYou reached your home limit. Please delete one of your homes.");
					else if(core.getGroups().hasPermission(p, "elerion.homes.3") && getHomesSize(p) >= 3) p.sendMessage(core.getPrefix() + "§cYou reached your home limit. Please delete one of your homes.");
					else if(core.getGroups().hasPermission(p, "elerion.homes.5") && getHomesSize(p) >= 5) p.sendMessage(core.getPrefix() + "§cYou reached your home limit. Please delete one of your homes.");
					else if(core.getGroups().hasPermission(p, "elerion.homes.7") && getHomesSize(p) >= 7) p.sendMessage(core.getPrefix() + "§cYou reached your home limit. Please delete one of your homes.");
					else if(core.getGroups().hasPermission(p, "elerion.homes.10") && getHomesSize(p) >= 10) p.sendMessage(core.getPrefix() + "§cYou reached your home limit. Please delete one of your homes.");
					else if(core.getGroups().hasPermission(p, "elerion.homes.15") && getHomesSize(p) >= 15) p.sendMessage(core.getPrefix() + "§cYou reached your home limit. Please delete one of your homes.");
					else if(core.getGroups().hasPermission(p, "elerion.homes.20") && getHomesSize(p) >= 20) p.sendMessage(core.getPrefix() + "§cYou reached your home limit. Please delete one of your homes.");
					else if(core.getGroups().hasPermission(p, "elerion.homes.30") && getHomesSize(p) >= 30) p.sendMessage(core.getPrefix() + "§cYou reached your home limit. Please delete one of your homes.");
					else{
						if(!cfg.isConfigurationSection("Information.Home." + name)) {
							core.getFileManager().createNewFile(p.getUniqueId() + ".yml", "plugins//ENFactions//PlayerData//");
						    cfg.set("Information.Home." + name + ".Location.X", p.getLocation().getX());
						    cfg.set("Information.Home." + name + ".Location.Y", p.getLocation().getY());
						    cfg.set("Information.Home." + name + ".Location.Z", p.getLocation().getZ());
						    cfg.set("Information.Home." + name + ".Location.Yaw", p.getLocation().getYaw());
						    cfg.set("Information.Home." + name + ".Location.Pitch", p.getLocation().getPitch());
						    cfg.set("Information.Home." + name + ".Location.World", p.getWorld().getName());
						    try {
						    	cfg.save(core.getFileManager().getFile(p.getUniqueId() + ".yml", "plugins//ENFactions//PlayerData//"));
						    } catch (IOException e) {
						       	e.printStackTrace();
					        }
						    p.sendMessage(core.getPrefix() + "§aHome §7" + name + " §ahas been created.");
						}else{
							p.sendMessage(core.getPrefix() + "§cThis home already exists.");
						}
					}
				}else if(args[0].equalsIgnoreCase("delete")) {
					String name = args[1];
					if(!file.exists()) {
						p.sendMessage(core.getPrefix() + "§cHome §7" + name + " §cdoes not exist.");
						core.getFileManager().createNewFile(p.getUniqueId() + ".yml", "plugins//ENFactions//PlayerData//");
					}else{
						if(cfg.isConfigurationSection("Information.Home." + name)) {
							cfg.set("Information.Home." + name, null);
							try {
								cfg.save(core.getFileManager().getFile(p.getUniqueId() + ".yml", "plugins//ENFactions//PlayerData//"));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cHome §7" + name + " §cdoes not exist.");
						}
					}
				    p.sendMessage(core.getPrefix() + "§aHome §7" + name + " §ahas been deleted.");
				}
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cOnly players can use this command.");
			return true;
		}
		return true;
	}
	
	public void sendHelp(CommandSender receiver, int page) {
		receiver.sendMessage(core.getPrefix() + "§9§lHome Management §8| §6Page§8: §e" + page);
		if (page == 1) {
			// 8 na stran
			receiver.sendMessage("§e/home §8- §7Shows this message.");
			receiver.sendMessage("§e/home <name> §8- §7Teleport to an existing home.");
			receiver.sendMessage("§e/home list §8- §7Lists all your homes.");
			receiver.sendMessage("§e/home set <name> §8- §7Creates a new home.");
			receiver.sendMessage("§e/home delete <name> §8- §7Deletes an existing home.");
		}
	}
	
	private int getHomesSize(Player p) {
		FileConfiguration cfg = core.getFileManager().getConfiguration(p.getUniqueId() + ".yml", "plugins//ENFactions//PlayerData//");
		if(cfg.isConfigurationSection("Information.Home")) {
			Set<String> homes = cfg.getConfigurationSection("Information.Home").getKeys(false);
			if(homes.size() != 0) {
				return homes.size();
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}

}
