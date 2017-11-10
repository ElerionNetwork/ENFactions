package me.jellylicious.elerion_network.factions.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Tribes implements CommandExecutor {
	
	ArrayList<String> tribeDeletion = new ArrayList<String>();
	HashMap<String, String> enemyToNeutral = new HashMap<String, String>();
	
	Core core;
	public Tribes(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			String pUUID = p.getUniqueId().toString();
			if(args.length == 0) {
				p.performCommand("tribes help");
			}else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("help")) {
					core.getTribesAPI().sendHelpMessage(p, 1);
				}else if(args[0].equalsIgnoreCase("leave")) {
					String playerTribe = core.getTribesAPI().getPlayerTribe(pUUID);
					if(playerTribe != null) {
						core.getTribesAPI().playerLeavesTribe(p, core.getTribesAPI().getPlayerTribe(pUUID));
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any existing tribes.");
					}
				}else if(args[0].equalsIgnoreCase("home")) {
					String playerTribe = core.getTribesAPI().getPlayerTribe(pUUID);
					if(playerTribe != null) {
						Location homeLoc = core.getLocationHelper().getLocation(playerTribe.toUpperCase() + ".yml", "Information.Location.Home", "plugins//ENFactions//Tribes//");
						if(homeLoc != null) {
							p.teleport(homeLoc);
							p.sendMessage(core.getPrefix() + "§aYou have been teleported to your tribe's home location.");
						}else{
							p.sendMessage(core.getPrefix() + "§cYour tribe does not have a home location set yet.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any existing tribes.");
					}
				}else if(args[0].equalsIgnoreCase("sethome")) {
					String playerTribe = core.getTribesAPI().getPlayerTribe(pUUID);
					if(playerTribe != null) {
						if(core.getTribesAPI().tribeExists(playerTribe)) {
							if(core.getTribesAPI().tribeChieftain(playerTribe).equals(pUUID)) {
								Location loc = p.getLocation();
								if(core.getTribesAPI().tribeClaims(playerTribe).size() != 0) {
									Location low = core.getTribesAPI().getMinLocationOfBox(loc);
									Location high = core.getTribesAPI().getMaxLocationOfBox(loc);
									String claim = core.getTribesAPI().getClaim(low, high);
									if(claim != null) {
										if(core.getTribesAPI().getClaimOwner(claim).equals(playerTribe)) {
											core.getLocationHelper().saveLocation(loc, playerTribe + ".yml", "Information.Location.Home", "plugins//ENFactions//Tribes//");
											p.sendMessage(core.getPrefix() + "§aYou have successfully set a new tribe's home location.");
											for(String member : core.getTribesAPI().getAllMembers(playerTribe)) {
												Player m = Bukkit.getPlayer(UUID.fromString(member));
												if(m != p) m.sendMessage(core.getPrefix() + "§7" + p.getName() + " §ahas just set a new home location for your tribe!");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cYour home location should be located in one of your claims.");
										}
									}else{
										if(core.getTribesAPI().isCrystalLocationSet(playerTribe)) {
											p.sendMessage(core.getPrefix() + "§cYour home location should be located in one of your claims.");
										}else{
											p.sendMessage(core.getPrefix() + "§cTo set a home, firstly create a claim which also protects your crystal. To spawn a crystal, use §7/t crystal§a.");
										}
									}
								}else{
									p.sendMessage(core.getPrefix() + "§cYou should have at least one area claimed before setting a home location for your tribe.");
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cYou are not a chieftain of this tribe!");
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cThis tribe does not exist.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any existing tribes.");
					}
				}else if(args[0].equalsIgnoreCase("delete")) {
					String tribe = core.getTribesAPI().getPlayerTribe(pUUID);
					if(tribe != null) {
						if(core.getTribesAPI().tribeExists(tribe)) {
							tribeDeletion.add(pUUID);
							p.sendMessage(core.getPrefix() + "§aPlease use §7/t confirm §ato confirm the tribe deletion.");
						}else{
							p.sendMessage(core.getPrefix() + "§cThis tribe does not exist.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
					}
				}else if(args[0].equalsIgnoreCase("confirm")) {
					String tribe = core.getTribesAPI().getPlayerTribe(pUUID);
					if(tribe != null) {
						if(tribeDeletion.contains(pUUID)) {
							core.getTribesAPI().deleteTribe(p, tribe, false);
							tribeDeletion.remove(pUUID);
						}else{
							p.sendMessage(core.getPrefix() + "§cFirst you must use §7/t delete §cin order to confirm the deletion.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
					}
				}else if(args[0].equalsIgnoreCase("crystal")) {
					core.getTribesAPI().createTribeCrystal(p);
				}else if(args[0].equalsIgnoreCase("info")) {
					String tribe = core.getTribesAPI().getPlayerTribe(pUUID);
					if(tribe != null) {
						tribe = tribe.toUpperCase();
						if(core.getTribesAPI().tribeExists(tribe)) {
							core.getTribesAPI().getTribeInfo(p, tribe);
						}else{
							p.sendMessage(core.getPrefix() + "§cThis tribe does not exist.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
					}
				}else if(args[0].equalsIgnoreCase("claim")) {
					String tribe = core.getTribesAPI().getPlayerTribe(pUUID);
					if(tribe != null && core.getTribesAPI().tribeExists(tribe.toUpperCase())) {
						if(core.getTribesAPI().tribeClaims(tribe).size() <= 19) {
							core.getTribesAPI().claim(p);
						}else{
							p.sendMessage(core.getPrefix() + "§cYour tribe already reached it's limit. You can only claim 20 claims for now.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
					}
				}else if(args[0].equalsIgnoreCase("unclaim")) {
					core.getTribesAPI().unclaim(p);
				}else if(args[0].equalsIgnoreCase("unclaimall")) {
					String tribe = core.getTribesAPI().getPlayerTribe(pUUID);
					if(tribe != null) {
						if(core.getTribesAPI().tribeExists(tribe.toUpperCase())) {
							tribe = tribe.toUpperCase();
							if(core.getTribesAPI().tribeChieftain(tribe).equals(pUUID)) {
								for(String claim : core.getTribesAPI().tribeClaims(tribe)) {
									core.getTribesAPI().unclaim(claim);
								}
								core.getTribesAPI().despawnCrystal(tribe);
								p.sendMessage(core.getPrefix() + "§aAll claims were unclaimed.");
								for(String member : core.getTribesAPI().getAllMembers(tribe)) {
									Player t = Bukkit.getPlayer(UUID.fromString(member));
									if(t != p) t.sendMessage(core.getPrefix() + "§cAll claims were unclaimed by §7" + p.getName());
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cYou're not a chieftain of this tribe.");
							}
						}else{
			    			p.sendMessage(core.getPrefix() + "§cTribe you are supposed to belong to doesn't exist anymore.");
			    			core.getTribesAPI().setPlayerTribe(pUUID, null);
			    		}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
					}
					core.getTribesAPI().unclaim(p);
				}else if(args[0].equalsIgnoreCase("srconfirm")) {
					core.getTribesAPI().confirmPromotionToChieftain(p);
				}else if(args[0].equalsIgnoreCase("create")) {
					p.sendMessage("§e/t create <name> §8- §7Creates a new tribe.");
				}else if(args[0].equalsIgnoreCase("invite")) {
					p.sendMessage("§e/t invite <player> §8- §7Invites a player to your tribe.");
				}else if(args[0].equalsIgnoreCase("kick")) {
					p.sendMessage("§e/t kick <player> §8- §7Kicks a player from the tribe.");
				}else if(args[0].equalsIgnoreCase("rename")) {
					p.sendMessage("§e/t rename <newName> §8- §7Sets a new tribe name.");
				}else if(args[0].equalsIgnoreCase("join")) {
					p.sendMessage("§e/t join <tribe> §8- §7Join a specific tribe.");
				}else if(args[0].equalsIgnoreCase("hostile")) {
					p.sendMessage("§e/t hostile <tribe> §8- §7Sets a hostile status between tribes.");
				}else if(args[0].equalsIgnoreCase("neutral")) {
					p.sendMessage("§e/t neutral <tribe> §8- §7Sets a neutral status between tribes.");
				}else if(args[0].equalsIgnoreCase("ally")) {
					p.sendMessage("§e/t ally <tribe> §8- §7Sets an alliance status between tribes.");
				}else if(args[0].equalsIgnoreCase("creq")) {
					p.sendMessage("§e/t creq <tribe> §8- §7Cancel a request to join a specific tribe.");
				}else if(args[0].equalsIgnoreCase("areq")) {
					p.sendMessage("§e/t areq <player> §8- §7Accept a join request sent by specific player.");
				}else if(args[0].equalsIgnoreCase("dreq")) {
					p.sendMessage("§e/t dreq <player> §8- §7Deny a join request sent by specific player.");
				}else if(args[0].equalsIgnoreCase("aally")) {
					p.sendMessage("§e/t aally <tribe> §8- §7Accept an alliance request.");
				}else if(args[0].equalsIgnoreCase("dally")) {
					p.sendMessage("§e/t dally <tribe> §8- §7Deny an alliance request.");
				}else if(args[0].equalsIgnoreCase("setrole")) {
					p.sendMessage("§e/t setrole <target> <chieftain/champion/recruiter> §8- §7Set role of your tribe's member. Promoting someone to chieftain will also set your own position to champion.");
				}else if(args[0].equalsIgnoreCase("player")) {
					p.sendMessage("§e/t player <target> §8- §7Check some information about player regarding tribes.");
				}else{
					core.getTribesAPI().sendHelpMessage(p, 1);
				}
			}else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("help")) {
					if(args[1].equalsIgnoreCase("1")) {
						core.getTribesAPI().sendHelpMessage(p, 1);
					}else if(args[1].equalsIgnoreCase("2")) {
						core.getTribesAPI().sendHelpMessage(p, 2);
					}else if(args[1].equalsIgnoreCase("3")) {
						core.getTribesAPI().sendHelpMessage(p, 3);
					}else if(args[1].equalsIgnoreCase("4")) {
						core.getTribesAPI().sendHelpMessage(p, 4);
					}else if(args[1].equalsIgnoreCase("5")) {
						core.getTribesAPI().sendHelpMessage(p, 5);
					}else{
						p.sendMessage(core.getPrefix() + "§cSorry, but this page does not exist.");
					}
				}else if(args[0].equalsIgnoreCase("create")) {
					String name = args[1];
					if(name.length() >= 3 && name.length() <= 12 && name.chars().allMatch(Character::isLetter)) {
						if(core.getTribesAPI().tribeExists(name)) {
							p.sendMessage(core.getPrefix() + "§cTribe §7" + name + " §calready exists!");
						}else{
							String playerTribe = core.getTribesAPI().getPlayerTribe(pUUID);
							if(playerTribe == null) {
								core.getTribesAPI().createNewTribe(name, p);
							}else{
								p.sendMessage(core.getPrefix() + "§cYou already belong to a tribe named §7" + playerTribe + "§c. To create a new tribe, please leave the current one.");
							}
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cPlease choose a name that is at least 3 characters long, contains a maximum of 12 characters and does only contain letters.");
					}
				}else if(args[0].equalsIgnoreCase("invite")) {
					Player target = Bukkit.getPlayer(args[1]);
					if(target != null) {
						core.getTribesAPI().sendInvitation(p, target, core.getTribesAPI().getPlayerTribe(pUUID));
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				}else if(args[0].equalsIgnoreCase("kick")) {
					Player target = Bukkit.getPlayer(args[1]);
					if(target != null) {
						String tribe = core.getTribesAPI().getPlayerTribe(pUUID);
						if(tribe != null) {
							core.getTribesAPI().kickPlayerFromTribe(p, target, core.getTribesAPI().getPlayerTribe(pUUID));
						}else{
							p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
					}
				}else if(args[0].equalsIgnoreCase("join")) {
					String tribe = args[1].toUpperCase();
					String uuid = pUUID;
					if(core.getTribesAPI().tribeInvitationsOfPlayer(uuid).contains(tribe)) {
						core.getTribesAPI().invitationAccepted(p, tribe);
					}else{
						core.getTribesAPI().sendJoinRequest(p, tribe);
					}
				}else if(args[0].equalsIgnoreCase("rename")) {
					String oldName = core.getTribesAPI().getPlayerTribe(pUUID);
					String newName = args[1];
					if(newName.length() >= 3 && newName.length() <= 12 && newName.chars().allMatch(Character::isLetter)) {
						core.getTribesAPI().renameTribe(p, oldName.toUpperCase(), newName.toUpperCase());
					}else{
						p.sendMessage(core.getPrefix() + "§cPlease choose a name that is at least 3 characters long, contains a maximum of 12 characters and does only contain letters.");
					}
				}else if(args[0].equalsIgnoreCase("info")) {
					String tribe = args[1].toUpperCase();
					if(core.getTribesAPI().tribeExists(tribe)) {
						core.getTribesAPI().getTribeInfo(p, tribe);
					}else{
						p.sendMessage(core.getPrefix() + "§cThis tribe does not exist.");
					}
				}else if(args[0].equalsIgnoreCase("hostile")) {
					String tribe = core.getTribesAPI().getPlayerTribe(pUUID);
					if(tribe != null) {
						tribe = tribe.toUpperCase();
						if(core.getTribesAPI().tribeExists(tribe)) {
							String targetTribe = args[1].toUpperCase();
							if(core.getTribesAPI().tribeExists(targetTribe)) {
								if(core.getTribesAPI().tribeChieftain(tribe).equals(pUUID) || core.getTribesAPI().tribeChampions(tribe).contains(pUUID)) {
									if(!tribe.equals(targetTribe)) {
										if(!core.getTribesAPI().hostileTribes(tribe).contains(targetTribe)) {
											File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
											FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
											File f1 = core.getFileManager().getFile(targetTribe + ".yml", "plugins//ENFactions//Tribes//");
											FileConfiguration cfg1 = core.getFileManager().getConfiguration(targetTribe + ".yml", "plugins//ENFactions//Tribes//");
											if(core.getTribesAPI().isNeutral(tribe, targetTribe)) {
												List<String> hostile = core.getTribesAPI().hostileTribes(tribe);
												hostile.add(targetTribe);
												List<String> hostile1 = core.getTribesAPI().hostileTribes(targetTribe);
												hostile1.add(tribe);
												cfg.set("Information.Tribes.Hostile", hostile);
												cfg1.set("Information.Tribes.Hostile", hostile1);
												cfg.set("Information.Tribes.Hostile." + hostile + ".DoneBy", p.getUniqueId().toString());
												cfg1.set("Information.Tribes.Hostile." + hostile1 + ".DoneBy", p.getUniqueId().toString());
												try {
													cfg.save(f);
													cfg1.save(f1);
													p.sendMessage(core.getPrefix() + "§aTribe §7" + targetTribe + " §ais now hostile.");
													for(String tmember : core.getTribesAPI().getAllMembers(tribe)) {
														Player tm = Bukkit.getPlayer(UUID.fromString(tmember));
														if(tm != null && tm != p) tm.sendMessage(core.getPrefix() + "§cTribe §7" + targetTribe + " §cis now hostile.");
													}
													for(String ttmember : core.getTribesAPI().getAllMembers(targetTribe)) {
														Player ttm = Bukkit.getPlayer(UUID.fromString(ttmember));
														if(ttm != null) ttm.sendMessage(core.getPrefix() + "§cTribe §7" + tribe + " §cis now hostile.");
													}
												} catch (IOException e) {
													e.printStackTrace();
												}
											}else{
												if(core.getTribesAPI().alliances(tribe).contains(targetTribe)) {
													List<String> ally = core.getTribesAPI().alliances(tribe);
													List<String> hostile = core.getTribesAPI().hostileTribes(tribe);
													List<String> ally1 = core.getTribesAPI().alliances(targetTribe);
													List<String> hostile1 = core.getTribesAPI().hostileTribes(targetTribe);
													ally.remove(targetTribe);
													hostile.add(targetTribe);
													ally1.remove(tribe);
													hostile1.add(tribe);
													cfg.set("Information.Tribes.Hostile", hostile);
													cfg.set("Information.Tribes.Alliances", ally);
													cfg1.set("Information.Tribes.Hostile", hostile1);
													cfg1.set("Information.Tribes.Alliances", ally1);
													try {
														cfg.save(f);
														cfg1.save(f1);
														p.sendMessage(core.getPrefix() + "§aTribe §7" + targetTribe + " §aused to be an alliance, but now it is hostile.");
														for(String tmember : core.getTribesAPI().getAllMembers(tribe)) {
															Player tm = Bukkit.getPlayer(UUID.fromString(tmember));
															if(tm != null && tm != p) tm.sendMessage(core.getPrefix() + "§cTribe §7" + targetTribe + " §cused to be an alliance, but now it is hostile.");
														}
														for(String ttmember : core.getTribesAPI().getAllMembers(targetTribe)) {
															Player ttm = Bukkit.getPlayer(UUID.fromString(ttmember));
															if(ttm != null) ttm.sendMessage(core.getPrefix() + core.getPrefix() + "§cTribe §7" + tribe + " §cused to be an alliance, but now it is hostile.");
														}
													} catch (IOException e) {
														e.printStackTrace();
													}
												}else{
													p.sendMessage(core.getPrefix() + "§cSomething went wrong. Contact developers immediately and explain them the situation.");
												}
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cThis tribe is already hostile.");
										}
									}else{
										p.sendMessage(core.getPrefix() + "§cYou can not send a request to your own tribe!");
									}
								}else{
									p.sendMessage(core.getPrefix() + "§cYou're not permitted to do that.");
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cThis tribe does not exist.");
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cTribe you're supposed to belong to does not exist.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
					}
				}else if(args[0].equalsIgnoreCase("ally")) {
					String targetTribe = args[1];
					core.getTribesAPI().sendAllianceRequest(p, targetTribe);
				}else if(args[0].equalsIgnoreCase("neutral")) {
					String tribe = core.getTribesAPI().getPlayerTribe(pUUID);
					if(tribe != null) {
						tribe = tribe.toUpperCase();
						if(core.getTribesAPI().tribeExists(tribe)) {
							if(core.getTribesAPI().tribeChieftain(tribe).equals(pUUID) || core.getTribesAPI().tribeChampions(tribe).contains(pUUID)) {
								String targetTribe = args[1].toUpperCase();
								if(core.getTribesAPI().tribeExists(targetTribe)) {
									if(!tribe.equals(targetTribe)) {
										if(!core.getTribesAPI().isNeutral(tribe, targetTribe)) {
											if(!core.getTribesAPI().hostileTribes(tribe).contains(targetTribe)) {
												List<String> ally = core.getTribesAPI().alliances(tribe);
												List<String> ally1 = core.getTribesAPI().alliances(targetTribe);
												if(ally.contains(targetTribe)) ally.remove(targetTribe);
												if(ally1.contains(tribe)) ally1.remove(tribe);
												File f = core.getFileManager().getFile(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
												FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
												File f1 = core.getFileManager().getFile(targetTribe + ".yml", "plugins//ENFactions//Tribes//");
												FileConfiguration cfg1 = core.getFileManager().getConfiguration(targetTribe + ".yml", "plugins//ENFactions//Tribes//");
												cfg.set("Information.Tribes.Alliances", ally);
												cfg1.set("Information.Tribes.Alliances", ally1);
												try {
													cfg.save(f);
													cfg1.save(f1);
													p.sendMessage(core.getPrefix() + "§aTribe §7" + targetTribe + " §ais now neutral.");
													for(String tmember : core.getTribesAPI().getAllMembers(tribe)) {
														Player tm = Bukkit.getPlayer(UUID.fromString(tmember));
														if(tm != null && tm != p) tm.sendMessage(core.getPrefix() + "§aTribe §7" + targetTribe + " §ais now neutral.");
													}
													for(String ttmember : core.getTribesAPI().getAllMembers(targetTribe)) {
														Player ttm = Bukkit.getPlayer(UUID.fromString(ttmember));
														if(ttm != null) ttm.sendMessage(core.getPrefix() + "§aTribe §7" + tribe + " §ais now neutral.");
													}
												} catch (IOException e) {
													e.printStackTrace();
												}
											}else{
												if(core.getTribesAPI().whoMadeTribesHostile(tribe, targetTribe).equals(pUUID)) {
													File f = core.getFileManager().getFile(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
													FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
													File f1 = core.getFileManager().getFile(targetTribe + ".yml", "plugins//ENFactions//Tribes//");
													FileConfiguration cfg1 = core.getFileManager().getConfiguration(targetTribe + ".yml", "plugins//ENFactions//Tribes//");
													List<String> ht1 = core.getTribesAPI().hostileTribes(tribe);
													List<String> ht2 = core.getTribesAPI().hostileTribes(targetTribe);
													ht1.remove(targetTribe);
													ht2.remove(tribe);
													cfg.set("Information.Tribes.Hostile", ht1);
													cfg1.set("Information.Tribes.Hostile", ht2);
													try {
														cfg.save(f);
														cfg1.save(f1);
													} catch (IOException e) {
														e.printStackTrace();
													}
													for(String member : core.getTribesAPI().getAllMembers(tribe)) {
														Player m = (Player) Bukkit.getPlayer(UUID.fromString(member));
														if(m != null) m.sendMessage(core.getPrefix() + "§aYour tribe is now neutral with tribe §7" + targetTribe + "§a.");
													}
													for(String member : core.getTribesAPI().getAllMembers(targetTribe)) {
														Player m = (Player) Bukkit.getPlayer(UUID.fromString(member));
														if(m != null) m.sendMessage(core.getPrefix() + "§aYour tribe is now neutral with tribe §7" + tribe + "§a.");
													}
												}else{
													p.sendMessage(core.getPrefix() + "§cYou can not change the status between you and your enemy, because you are not the one who set it initially.");
												}
											}
										}else{
											p.sendMessage(core.getPrefix() + "§aThis tribe is already neutral.");
										}
									}else{
										p.sendMessage(core.getPrefix() + "§cYou can not send a request to your own tribe!");
									}
								}else{
									p.sendMessage(core.getPrefix() + "§cThis tribe does not exist.");
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cYou're not permitted to do that.");
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cTribe you're supposed to belong to doesn't exist anymore.");
							core.getTribesAPI().setPlayerTribe(pUUID, null);
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
					}
				}else if(args[0].equalsIgnoreCase("aally")) {
					core.getTribesAPI().acceptAllianceRequest(p, args[1]);
				}else if(args[0].equalsIgnoreCase("dally")) {
					core.getTribesAPI().denyAllianceRequest(p, args[1]);
				}else if(args[0].equalsIgnoreCase("creq")) {
					core.getTribesAPI().cancelJoinRequest(p, args[1]);
				}else if(args[0].equalsIgnoreCase("areq")) {
					Player acceptedPlayer = Bukkit.getPlayer(args[1]);
					if(acceptedPlayer != null){
						core.getTribesAPI().acceptJoinRequest(p, acceptedPlayer);
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment. Support for offline players might be available later.");
					}
				}else if(args[0].equalsIgnoreCase("dreq")) {
					Player acceptedPlayer = Bukkit.getPlayer(args[1]);
					if(acceptedPlayer != null){
						core.getTribesAPI().denyJoinRequest(p, acceptedPlayer);
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment. Support for offline players might be available later.");
					}
				}else{
					core.getTribesAPI().sendHelpMessage(p, 1);
				}
			}else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("setrole")) {
					Player t = Bukkit.getPlayer(args[1]);
					String role = args[2];
					core.getTribesAPI().setRole(p, t, role);
				}else{
					core.getTribesAPI().sendHelpMessage(p, 1);
				}
			}
		}
		return true;
	}

}
