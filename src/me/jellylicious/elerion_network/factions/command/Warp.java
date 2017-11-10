package me.jellylicious.elerion_network.factions.command;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Warp implements CommandExecutor {

	Core core;
	
	public Warp(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)) {
			cs.sendMessage(core.getPrefix() + "§cOnly players can use this command.");
			return true;
		}else{
			Player p = (Player) cs;
			FileConfiguration cfg = core.getFileManager().getConfiguration("WarpList.yml", "plugins//ENFactions//Warps//");
			if(args.length == 0) {
				sendHelp(p, 1);
			}else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("list")) {
					File file = core.getFileManager().getFile("WarpList.yml", "plugins//ENFactions//Warps//");
					if(!file.exists()) {
						p.sendMessage(core.getPrefix() + "§cThere are no warps available.");
						core.getFileManager().createNewFile("WarpList.yml", "plugins//ENFactions//Warps//");
					}else{
						Set<String> warps = cfg.getConfigurationSection("Warp").getKeys(false);
						if(warps.size() != 0) {
							p.sendMessage(core.getPrefix() + "§9§lWarp List §7(§e" + warps.size() + "§7)");
							for(String warp : warps) {
								boolean isVisible = false;
								if(cfg.getString("Warp." + warp + ".Visibility").equalsIgnoreCase("private")) isVisible = false;
								if(cfg.getString("Warp." + warp + ".Visibility").equalsIgnoreCase("public")) isVisible = true;
								if(isVisible || (!isVisible && core.getGroups().hasPermission(p, "elerion.command.warp.overridevisibility"))) {
									String ownerUUID = cfg.getString("Warp." + warp + ".Owner");
									p.sendMessage("§e" + warp + "§8- §7By §e" + core.getPlayerAccount().get(ownerUUID, "enPlayerData", "InGameName", "UUID"));
								}else{
									p.sendMessage("§cThis warp is invisible.");
								}
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cThere are no warps available.");
						}
					}
				}else{
					String warpName = args[0];
					if(core.getFileManager().getFile("WarpList.yml", "plugins//ENFactions//Warps//").exists()) {
						if(cfg.isConfigurationSection("Warp." + warpName + ".Location")) {
							Location warpLoc = core.getLocationHelper().getLocation("WarpList.yml", "Warp." + warpName + ".Location", "plugins//ENFactions//Warps//");
							p.teleport(warpLoc);
							p.sendMessage(core.getPrefix() + "§aYou've been teleported to warp §7" + warpName + "§a!");
						}else{
							p.sendMessage(core.getPrefix() + "§cWarp §7" + warpName + " §cdoes not exist!");
						}
					}else{
						core.getFileManager().createNewFile("WarpList.yml", "plugins//ENFactions//Warps//");
						p.sendMessage(core.getPrefix() + "§cWarp §7" + warpName + " §cdoes not exist!");
					}
				}
			}else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("create")) {
					String warpName = args[1];
					if(core.getGroups().hasPermission(p, "elerion.command.warp.create")) {
						if(core.getFileManager().getFile("WarpList.yml", "plugins//ENFactions//Warps//").exists()) {
							if(!(cfg.isConfigurationSection("Warp." + warpName))) {
								cfg.set("Warp." + warpName + ".Owner", p.getUniqueId().toString());
						        cfg.set("Warp." + warpName + ".Location.X", p.getLocation().getX());
						        cfg.set("Warp." + warpName + ".Location.Y", p.getLocation().getY());
						        cfg.set("Warp." + warpName + ".Location.Z", p.getLocation().getZ());
						        cfg.set("Warp." + warpName + ".Location.Yaw", p.getLocation().getYaw());
						        cfg.set("Warp." + warpName + ".Location.Pitch", p.getLocation().getPitch());
						        cfg.set("Warp." + warpName + ".Location.World", p.getWorld().getName());
						        cfg.set("Warp." + warpName + ".Visibility", "public");
								try {
									cfg.save(core.getFileManager().getFile("WarpList.yml", "plugins//ENFactions//Warps//"));
								} catch (IOException e) {
									e.printStackTrace();
								}
								p.sendMessage(core.getPrefix() + "§aWarp §7" + warpName + " §ahas been created.");
							}else{
								p.sendMessage(core.getPrefix() + "§cWarp §7" + warpName + " §calready exists!");
							}
						}else{
							core.getFileManager().createNewFile("WarpList.yml", "plugins//ENFactions//Warps//");
							core.getLocationHelper().saveLocation(p.getLocation(), "WarpList.yml", "Warp." + warpName + ".Location", "plugins//ENFactions//Warps//");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not have permission to create a warp!");
					}
				}else if(args[0].equalsIgnoreCase("remove")) {
					String warpName = args[1];
					if(core.getGroups().hasPermission(p, "elerion.command.warp.remove")) {
						if(core.getFileManager().getFile("WarpList.yml", "plugins//ENFactions//Warps//").exists()) {
							if(!(cfg.isConfigurationSection("Warp." + warpName))) {
								p.sendMessage(core.getPrefix() + "§cWarp §7" + warpName + " §cdoes not exist!");
							}else{
								String ownerUUID = cfg.getString("Warp." + warpName + ".Owner");
								if(p.getUniqueId().toString() == ownerUUID || p.isOp() || p.hasPermission("elerion.command.warp.remove.others")) {
									cfg.set("Warp." + warpName, null);
									try {
										cfg.save(core.getFileManager().getFile("WarpList.yml", "plugins//ENFactions//Warps//"));
									} catch (IOException e) {
										e.printStackTrace();
									}
									p.sendMessage(core.getPrefix() + "§aWarp §7" + warpName + " §ahas been removed!");
								}else{
									p.sendMessage(core.getPrefix() + "§cYou do not have permission to remove this warp!");
								}
							}
						}else{
							core.getFileManager().createNewFile("WarpList.yml", "plugins//ENFactions//Warps//");
							p.sendMessage(core.getPrefix() + "§cWarp §7" + warpName + " §cdoes not exist!");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not have permission to remove this warp!");
					}
				}else{
					String warpName = args[0];
					Player target = Bukkit.getPlayer(args[1]);
					if(core.getFileManager().getFile("WarpList.yml", "plugins//ENFactions//Warps//").exists()) {
						if(!(cfg.isConfigurationSection("Warp." + warpName))) {
							p.sendMessage(core.getPrefix() + "§cWarp §7" + warpName + " §cdoes not exist!");
						}else{
							if(target != null) {
								boolean isVisible = false;
								if(cfg.getString("Warp." + warpName + ".Visibility").equalsIgnoreCase("private")) isVisible = false;
								if(cfg.getString("Warp." + warpName + ".Visibility").equalsIgnoreCase("public")) isVisible = true;
								if(isVisible || (!isVisible && core.getGroups().hasPermission(p, "elerion.command.warp.overridevisibility"))) {
									Location loc = core.getLocationHelper().getLocation("WarpList.yml", "Warp." + warpName + ".Location", "plugins//ENFactions//Warps//");
									target.teleport(loc);
									target.sendMessage(core.getPrefix() + "§aYou've been teleported to warp §7" + warpName + " §aby §7" + p.getName() + "§a!");
									p.sendMessage(core.getPrefix() + "§aPlayer §7" + target.getName() + " §awas teleported to warp §7" + warpName + "§a!");
								}else{
									p.sendMessage(core.getPrefix() + "§cYou can not teleport to this warp. It is not accessible by normal players.");
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cPlayer §7" + target + " §cis not online!");
							}
						}
					}else{
						core.getFileManager().createNewFile("WarpList.yml", "plugins//ENFactions//Warps//");
						p.sendMessage(core.getPrefix() + "§cWarp §7" + warpName + " §cdoes not exist!");
					}
				}
			}else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("setVisibility")) {
					if(core.getGroups().hasPermission(p, "elerion.command.warp.setvisibility")) {
						if(args[1].equalsIgnoreCase("public") || args[1].equalsIgnoreCase("private")) {
							String option = args[1];
							String warpName = args[2];
							if(core.getFileManager().getFile("WarpList.yml", "plugins//ENFactions//Warps//").exists()) {
								if(!(cfg.isConfigurationSection("Warp." + warpName))) {
									p.sendMessage(core.getPrefix() + "§cWarp §7" + warpName + " §cdoes not exist!");
								}else{
									cfg.set("Warp." + warpName + ".Visibility", option);
									try {
										cfg.save(core.getFileManager().getFile("WarpList.yml", "plugins//ENFactions//Warps//"));
									} catch (IOException e) {
										e.printStackTrace();
									}
									p.sendMessage(core.getPrefix() + "§aWarp visibility of warp §7" + warpName + " §awas changed successfully!");
								}
							}else{
								core.getFileManager().createNewFile("WarpList.yml", "plugins//ENFactions//Warps//");
								p.sendMessage(core.getPrefix() + "§cWarp §7" + warpName + " §cdoes not exist!");
							}
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not have permission to change the visibility of this warp!");
					}
				}
			}
		}
		return true;
	}
	
	public void sendHelp(CommandSender receiver, int page) {
		receiver.sendMessage(core.getPrefix() + "§9§lWarp Management §8| §6Page§8: §e" + page);
		if (page == 1) {
			// 8 na stran
			receiver.sendMessage("§e/warp list §8- §7List all available warps.");
			receiver.sendMessage("§e/warp <name> §8- §7Teleport to a warp.");
			receiver.sendMessage("§e/warp create <name> §8- §7Create a warp.");
			receiver.sendMessage("§e/warp remove <name> §8- §7Remove a warp.");
			receiver.sendMessage("§e/warp <name> <player> §8- §7Teleport someone to a warp.");
			receiver.sendMessage("§e/warp setVisibility <public/private> <name> §8- §7Set a visibility of a warp.");
		}
	}

}
