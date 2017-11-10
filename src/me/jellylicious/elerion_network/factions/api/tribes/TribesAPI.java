package me.jellylicious.elerion_network.factions.api.tribes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class TribesAPI {
	
	//Used to determine lower and upper corners (two corners are already in claim() method):
	
//	Location l2 = new Location(world, blockX, 1, blockZ + 15);//c.getWorld().getHighestBlockAt(blockX, blockZ+15).getLocation();
//	Location l3 = new Location(world, blockX + 15, 1, blockZ);//c.getWorld().getHighestBlockAt(blockX +15, blockZ).getLocation();
//	Location l4 = new Location(world, blockX + 15, 1, blockZ + 15);//c.getWorld().getHighestBlockAt(blockX +15, blockZ +15).getLocation();
//	Location h1 = new Location(world, blockX, 1, blockZ);//c.getWorld().getHighestBlockAt(blockX, blockZ).getLocation();
//	Location h2 = new Location(world, blockX, 1, blockZ + 15);//c.getWorld().getHighestBlockAt(blockX, blockZ+15).getLocation();
//	Location h3 = new Location(world, blockX + 15, 1, blockZ);//c.getWorld().getHighestBlockAt(blockX +15, blockZ).getLocation();
	
	//Used to set a block on specified location in a chunk:
	
	//c.getWorld().getBlockAt(l1).setType(Material.GLOWSTONE);
	
	HashMap<String, String> src = new HashMap<String, String>();
	
	Core core;
	public TribesAPI(Core core) {
		this.core = core;
	}
	
	public enum EffectType {
		LETHALITY, PRECISION, HEALING_AURA, SWIFT_FEET, LIFTOFF, MINERS_ADRENALINE, HEMORRHAGE;
	}
	
	public void sendHelpMessage(Player p, int page) {
		p.sendMessage("§7[§a§lELERION§7] §9Help §8| §6Page§8: §e" + page + "§7/§e5");
		if (page == 1) {
			p.sendMessage("§e/t help §8- §7Opens up this message.");
			p.sendMessage("§e/t help <page> §8- §7Opens up a help message on specified page.");
			p.sendMessage("§e/t create <name> §8- §7Creates a new tribe.");
			p.sendMessage("§e/t invite <player> §8- §7Invites a player to your tribe.");
			p.sendMessage("§e/t kick <player> §8- §7Kicks a player from the tribe.");
			p.sendMessage("§e/t leave §8- §7Removes you from a tribe.");
		} else if(page == 2){
			p.sendMessage("§e/t home §8- §7Teleports you to a tribe home location.");
			p.sendMessage("§e/t rename <newName> §8- §7Sets a new tribe name.");
			p.sendMessage("§e/t sethome §8- §7Sets a tribe home location.");
			p.sendMessage("§e/t info <tribe> §8- §7Prints out some tribe information.");
			p.sendMessage("§e/t delete §8- §7Deletes a tribe.");
			p.sendMessage("§e/t join <tribe> §8- §7Join a specific tribe.");
		}else if(page == 3) {
			p.sendMessage("§e/t confirm §8- §7Confirms deletion of a tribe.");
			p.sendMessage("§e/t hostile <tribe> §8- §7Sets a hostile status between tribes.");
			p.sendMessage("§e/t neutral <tribe> §8- §7Sets a neutral status between tribes.");
			p.sendMessage("§e/t ally <tribe> §8- §7Sets an alliance status between tribes.");
			p.sendMessage("§e/t crystal §8- §7Spawn a magic crystal that will protect your tribe.");
			p.sendMessage("§e/t claim §8- §7Claim an area and protect it from enemies.");
		}else if(page == 4) {
			p.sendMessage("§e/t creq <tribe> §8- §7Cancel a request to join a specific tribe.");
			p.sendMessage("§e/t areq <player> §8- §7Accept a join request sent by specific player.");
			p.sendMessage("§e/t dreq <player> §8- §7Deny a join request sent by specific player.");
			p.sendMessage("§e/t unclaim §8- §7Unclaim an area owned by your tribe.");
			p.sendMessage("§e/t unclaimall §8- §7Unclaim all areas owned by your tribe.");
			p.sendMessage("§e/t aally <tribe> §8- §7Accept an alliance request.");
		}
		else if(page == 5) {
			p.sendMessage("§e/t dally <tribe> §8- §7Deny an alliance request.");
			p.sendMessage("§e/t setrole <target> <chieftain/champion/recruiter> §8- §7Set role of your tribe's member. Promoting someone to chieftain will also set your own position to champion.");
			p.sendMessage("§e/t srconfirm §8- §7Use that to confirm the request that you want to promote someone else to a chieftain.");
		}
	}

	public boolean tribeExists(String tribeName) {
		return core.getFileManager().getFile(tribeName.toUpperCase() + ".yml" , "plugins//ENFactions//Tribes//").exists();
	}
	
	public String getPlayerTribe(String playerUUID) {
		if(core.getFileManager().getFile(playerUUID + ".yml", "plugins//ENFactions//PlayerData//").exists()) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(playerUUID + ".yml", "plugins//ENFactions//PlayerData//");
			if(cfg.isSet("Information.Tribe")) {
				return cfg.getString("Information.Tribe");
			}
			return null;
		}
		return null;
	}
	
	public void setPlayerTribe(String playerUUID, String tribe) {
		File f = core.getFileManager().getFile(playerUUID + ".yml", "plugins//ENFactions//PlayerData//");
		if(f.exists()) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(playerUUID + ".yml", "plugins//ENFactions//PlayerData//");
			cfg.set("Information.Tribe", tribe);
			try {
				cfg.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createNewTribe(String name, Player leader) {
		if(!tribeExists(name)) {
			String leaderUUID = leader.getUniqueId().toString();
			core.getFileManager().createNewFile(name.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			File f = core.getFileManager().getFile(name.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			FileConfiguration cfg = core.getFileManager().getConfiguration(name.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			List<String> recruiters = new ArrayList<String>();
			List<String> champions = new ArrayList<String>();
			List<String> invitations = new ArrayList<String>();
			List<String> hostile = new ArrayList<String>();
			List<String> alliances = new ArrayList<String>();
			List<String> alliancereq = new ArrayList<String>();
			List<String> req = new ArrayList<String>();
			List<String> n = new ArrayList<String>();
			List<String> eu = new ArrayList<String>();
			recruiters.add(" ");
			champions.add(" ");
			invitations.add(" ");
			hostile.add(" ");
			alliances.add(" ");
			req.add(" ");
			n.add(" ");
			eu.add(" ");
			cfg.options().copyDefaults(true);
			cfg.addDefault("Information.Chieftain", leaderUUID);
			cfg.addDefault("Information.Champions", champions);
			cfg.addDefault("Information.Recruiters", recruiters);
			cfg.addDefault("Information.Level", 1);
			cfg.addDefault("Information.Lives", "100/100");
			cfg.addDefault("Information.Invitations", invitations);
			cfg.addDefault("Information.TribeProtected", false);
			cfg.addDefault("Information.CrystalDestroyed", true);
			cfg.addDefault("Information.Location", "");
			cfg.addDefault("Information.Tribes.Hostile", hostile);
			cfg.addDefault("Information.Tribes.Alliances", alliances);
			cfg.addDefault("Information.Tribes.AllianceRequests", alliancereq);
			cfg.addDefault("Information.JoinRequests", req);
			cfg.addDefault("Information.Notifications", n);
			cfg.addDefault("Information.Effect.Lethality.Level", 1);
			cfg.addDefault("Information.Effect.Lethality.Active", false);
			cfg.addDefault("Information.Effect.Precision.Level", 1);
			cfg.addDefault("Information.Effect.Precision.Active", false);
			cfg.addDefault("Information.Effect.HealingAura.Level", 1);
			cfg.addDefault("Information.Effect.HealingAura.Active", false);
			cfg.addDefault("Information.Effect.SwiftFeet.Level", 1);
			cfg.addDefault("Information.Effect.SwiftFeet.Active", false);
			cfg.addDefault("Information.Effect.Liftoff.Level", 1);
			cfg.addDefault("Information.Effect.Liftoff.Active", false);
			cfg.addDefault("Information.Effect.MinersAdrenaline.Level", 1);
			cfg.addDefault("Information.Effect.MinersAdrenaline.Active", false);
			cfg.addDefault("Information.Effect.Hemorrhage.Level", 1);
			cfg.addDefault("Information.Effect.Hemorrhage.Active", false);
			cfg.addDefault("Information.EffectUsers", eu);
			setPlayerTribe(leaderUUID, name.toUpperCase());
			try {
				cfg.save(f);
				leader.sendMessage(core.getPrefix() + "§aTribe §7" + name + " §asuccessfully created.");
			} catch (IOException e) {
				leader.sendMessage(core.getPrefix() + "§cSomething went wrong while saving the config file for your tribe.");
			}
		}else{
			leader.sendMessage(core.getPrefix() + "§cThis tribe already exists.");
		}
	}
	
	public void invitationAccepted(Player p, String tribe) {
		tribe = tribe.toUpperCase();
		if(tribeExists(tribe)) {
			String playerUUID = p.getUniqueId().toString();
			if(getPlayerTribe(playerUUID) == null) {
				if(core.getFileManager().getFile(playerUUID + ".yml", "plugins//ENFactions//PlayerData//").exists()) {
					addMemberToTribe(tribe, playerUUID);
					removeInvitation(tribe, playerUUID);
					p.sendMessage(core.getPrefix() + "§aYou have just joined a tribe §7" + tribe + "§a.");
					for(String member : getAllMembers(tribe)) {
						Player m = Bukkit.getPlayer(UUID.fromString(member));
						if(m != p) m.sendMessage(core.getPrefix() + "§7" + p.getName() + " §ahas just joined your tribe!");
					}
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cYou already belong to a tribe §7" + getPlayerTribe(playerUUID) + "§a!");
			}
		}else{
			p.sendMessage(core.getPrefix() + "§cTribe does not exist anymore.");
		}
	}
	
	public void sendJoinRequest(Player p, String tribe) {
		tribe = tribe.toUpperCase();
		if(tribeExists(tribe)) {
			String playerUUID = p.getUniqueId().toString();
			String playerTribe = getPlayerTribe(playerUUID);
			if(playerTribe == null) {
				File pf = core.getFileManager().getFile(playerUUID + ".yml", "plugins//ENFactions//PlayerData//");
				if(pf.exists()) {
					if(!joinRequests(tribe).contains(playerUUID) && !tribeRequestsOfPlayer(playerUUID).contains(tribe)) {
						File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
						FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
						List<String> req = joinRequests(tribe);
						req.add(playerUUID);
						cfg.set("Information.JoinRequests", req);
						FileConfiguration pcfg = core.getFileManager().getConfiguration(playerUUID + ".yml", "plugins//ENFactions//PlayerData//");
						List<String> preq = tribeRequestsOfPlayer(playerUUID);
						preq.add(tribe);
						pcfg.set("Information.TribeRequests", preq);
						try {
							cfg.save(f);
							pcfg.save(pf);
							p.sendMessage(core.getPrefix() + "§aYou haven't been invited to this tribe yet, but the request was sent.");
							String tc = tribeChieftain(tribe);
							if(core.getPlayerAccount().isOnline(tc)) {
								Bukkit.getPlayer(UUID.fromString(tc)).sendMessage(core.getPrefix() + "§7" + p.getName() + " §ahas requested to join your tribe. To approve it, use §7/t areq <player>§a. To deny it, use §7/t dreq <player>§a.");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou already requested to join this tribe. If you want to cancel the request, use §7/t creq <tribe>");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cPlease retry. If it keeps happening, then immediately contact developers, so they can fix it.");
				}
			}else{
				if(tribe.equals(playerTribe)) {
					p.sendMessage(core.getPrefix() + "§cYou already belong to that tribe.");
				}else{
					p.sendMessage(core.getPrefix() + "§cYou should leave the tribe you belong to, so you can send a request to another tribe.");
				}
			}
		}else{
			p.sendMessage(core.getPrefix() + "§cTribe does not exist.");
		}
	}
	
	public void kickPlayerFromTribe(CommandSender cs, Player p, String tribe) {
		if(cs instanceof Player) {
			Player sender = (Player) cs;
			if (tribeExists(tribe)) {
				String playerUUID = p.getUniqueId().toString();
				String senderUUID = sender.getUniqueId().toString();
				String playerTribe = getPlayerTribe(playerUUID);
				if (playerTribe != null) {
					if(sender != p) {
						if(belongsToTribe(getPlayerTribe(senderUUID), playerUUID)) {
							if(tribeChieftain(tribe).equals(senderUUID) || tribeChampions(tribe).contains(senderUUID)) {
								if(!tribeChieftain(tribe).equals(playerUUID)) {
									if (core.getFileManager().getFile(playerUUID + ".yml", "plugins//ENFactions//PlayerData//").exists()) {
										setPlayerTribe(playerUUID, null);
										File tribeData = core.getFileManager().getFile(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
										FileConfiguration tribeCfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
										List<String> tribeRecruiters = tribeRecruiters(tribe);
										if(tribeRecruiters.contains(playerUUID)) {
											tribeRecruiters.remove(playerUUID);
											tribeCfg.set("Information.Recruiters", tribeRecruiters);
										}
										List<String> champions = tribeChampions(tribe);
										if(champions.contains(playerUUID)) {
											champions.remove(playerUUID);
											tribeCfg.set("Information.Champions", champions);
										}
										try {
											tribeCfg.save(tribeData);
										} catch (IOException e) {
											core.getServer().getConsoleSender().sendMessage(core.getPrefix() + "§cSomething went wrong while saving one of the configs.");
										}
										sender.sendMessage(core.getPrefix() + "§aPlayer §7" + p.getName() + " §awas removed from tribe §7" + tribe + "§a.");
										p.sendMessage(core.getPrefix() + "§7" + sender.getName() + " §cremoved you from tribe §7" + tribe + "§c.");
										for(String member : getAllMembers(tribe)) {
											Player m = Bukkit.getPlayer(UUID.fromString(member));
											if(m != p) m.sendMessage(core.getPrefix() + "§7" + p.getName() + " §cwas kicked from the tribe by §7" + sender.getName() + "§c!");
										}
									}else{
										sender.sendMessage(core.getPrefix() + "§cData file of this player does not exist.");
									}
								}else{
									sender.sendMessage(core.getPrefix() + "§cYou can not kick the chieftain.");
								}
							}else{
								sender.sendMessage(core.getPrefix() + "§cIn order to execute that command, you need to be a tribe chieftain or tribe champion!");
							}
						}else{
							sender.sendMessage(core.getPrefix() + "§cPlayer does not belong to your tribe.");
						}
					}else{
						sender.sendMessage(core.getPrefix() + "§cYou can not kick yourself from the tribe.");
					}
				}else{
					sender.sendMessage(core.getPrefix() + "§cPlayer does not belong to any tribe.");
				}
			}else{
				sender.sendMessage(core.getPrefix() + "§cThis tribe does not exist!");
			}
		}
	}
	
	public void playerLeavesTribe(Player p, String tribe) {
		tribe = tribe.toUpperCase();
		if(tribe != null && tribeExists(tribe)) {
			String playerUUID = p.getUniqueId().toString();
			if (core.getFileManager().getFile(playerUUID + ".yml", "plugins//ENFactions//PlayerData//").exists()) {
				if(getAllMembers(tribe).size() == 1) {
					deleteTribe(p, tribe, true);
					setPlayerTribe(playerUUID, null);
					p.sendMessage(core.getPrefix() + "§aYou just left the tribe §7" + tribe + "§a. Since you were the only one, your tribe was also deleted.");
				}else if(getAllMembers(tribe).size() > 1) {
					if(tribeChieftain(tribe).equals(playerUUID)) {
						p.sendMessage(core.getPrefix() + "§cPlease consider promoting someone to chieftain before leaving. You may also delete the tribe.");
					}else{
						setPlayerTribe(playerUUID, null);
						File tribeData = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
						FileConfiguration tribeCfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
						List<String> tribeRecruiters = tribeRecruiters(tribe);
						tribeRecruiters.remove(playerUUID);
						tribeCfg.set("Information.Recruiters", tribeRecruiters);
						try {
							tribeCfg.save(tribeData);
							p.sendMessage(core.getPrefix() + "§aYou just left the tribe §7" + tribe + "§a.");
							for(String member : getAllMembers(tribe)) {
								Player m = Bukkit.getPlayer(UUID.fromString(member));
								if(m != p) m.sendMessage(core.getPrefix() + "§7" + p.getName() + " §chas just left your tribe!");
							}
						} catch (IOException e) {
							core.getServer().getConsoleSender().sendMessage(core.getPrefix() + "§cSomething went wrong while saving one of the configs.");
						}
					}
				}
			}
		}
	}
	
	public List<String> tribeRecruiters(String tribe) {
		List<String> r = new ArrayList<String>();
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			r = cfg.getStringList("Information.Recruiters");
			if(r.contains(" ")) r.remove(" ");
			return r;
		}
		return r;
	}
	
	public List<String> tribeChampions(String tribe) {
		List<String> c = new ArrayList<String>();
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			c = cfg.getStringList("Information.Champions");
			if(c.contains(" ")) c.remove(" ");
			return c;
		}
		return c;
	}
	
	public void sendInvitation(CommandSender cs, Player target, String tribe) {
		if(cs instanceof Player) {
			Player sender = (Player) cs;
			String senderUUID = sender.getUniqueId().toString();
			String targetUUID = target.getUniqueId().toString();
			if(getPlayerTribe(senderUUID) != null) {
				if(tribeExists(tribe)) {
					if(tribeChieftain(tribe).equals(senderUUID) || tribeChampions(tribe).contains(senderUUID)) {
						if(!belongsToTribe(tribe, targetUUID)) {
							if(!tribeInvitations(tribe).contains(targetUUID)) {
								addInvitation(tribe, targetUUID);
								sender.sendMessage(core.getPrefix() + "§aTribe invitation sent to §7" + target.getName() + "§a.");
								target.sendMessage(core.getPrefix() + "§7" + sender.getName() + " §asent you an invitation to join tribe §7" + tribe + "§a. If you want to join the tribe, use §7/t join <tribe>§a.");
							}else{
								sender.sendMessage(core.getPrefix() + "§cThis player was already invited to your tribe. Instead, remind him to accept or deny the invitation!");
							}
						}else{
							sender.sendMessage(core.getPrefix() + "§cThis player already belongs to your tribe.");
						}
					}else{
						sender.sendMessage(core.getPrefix() + "§cYou're not a chieftain of tribe §7" + tribe + "§a.");
					}
				}else{
					sender.sendMessage(core.getPrefix() + "§cThis tribe does not exist.");
				}
			}else{
				sender.sendMessage(core.getPrefix() + "§cYou are currently not a member of any tribes.");
			}
		}
	}
	
	public void sendAllianceRequest(CommandSender cs, String targetTribe) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			String puuid = p.getUniqueId().toString();
			String tribe = getPlayerTribe(puuid);
			if(tribe != null) {
				tribe = tribe.toUpperCase();
				targetTribe = targetTribe.toUpperCase();
				if(tribeExists(tribe)) {
					if(tribeChieftain(tribe).equals(puuid) || tribeChampions(tribe).contains(puuid)) {
						if(tribeExists(targetTribe)) {
							if(!tribe.equals(targetTribe)) {
								if(!alliances(targetTribe).contains(tribe)) {
									FileConfiguration cfg = core.getFileManager().getConfiguration(targetTribe + ".yml", "plugins//ENFactions//Tribes//");
									List<String> alliances = allianceRequests(targetTribe);
									alliances.add(tribe);
									cfg.set("Information.Tribes.AllianceRequests", alliances);
									p.sendMessage(core.getPrefix() + "§aAlliance request sent.");
									try {
										cfg.save(core.getFileManager().getFile(targetTribe + ".yml", "plugins//ENFactions//Tribes//"));
									} catch (IOException e) {
										e.printStackTrace();
									}
									String tc = tribeChieftain(targetTribe);
									Player tl = Bukkit.getPlayer(UUID.fromString(tc));
									if(tl != null) tl.sendMessage(core.getPrefix() + "§aYour tribe received an alliance request from tribe §7" + tribe + "§a. To accept it, use /t aally <tribe>. To deny it, use /t dally <tribe>.");
								}else{
									p.sendMessage(core.getPrefix() + "§cYour tribe is allready an alliance with this tribe.");
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cYou can not send a request to your own tribe!");
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cTarget tribe does not exist.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou're not permitted to do that.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cTribe you're supposed to belong to doesn't exist anymore.");
					setPlayerTribe(puuid, null);
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
			}
		}
	}
	
	public String tribeChieftain(String tribe) {
		String c = null;
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			if(cfg.isSet("Information.Chieftain")) c = cfg.getString("Information.Chieftain");
			return c;
		}
		return c;
	}
	
	public List<String> tribeInvitations(String tribe) {
		List<String> invitations = new ArrayList<String>();
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			invitations = cfg.getStringList("Information.Invitations");
			if(invitations.contains(" ")) invitations.remove(" ");
			return invitations;
		}
		return invitations;
	}
	
	public List<String> tribeInvitationsOfPlayer(String uuid) {
		List<String> invitations = new ArrayList<String>();
		if(core.getFileManager().getFile(uuid + ".yml", "plugins//ENFactions//PlayerData//").exists()) {
			FileConfiguration playerCfg = core.getFileManager().getConfiguration(uuid + ".yml", "plugins//ENFactions//PlayerData//");
			if(!playerCfg.getStringList("Information.TribeInvitations").isEmpty()) {
				invitations = playerCfg.getStringList("Information.TribeInvitations");
				if(invitations.contains(" ")) invitations.remove(" ");
				return invitations;
			}
			return invitations;
		}
		return invitations;
	}
	
	public List<String> tribeRequestsOfPlayer(String uuid) {
		List<String> req = new ArrayList<String>();
		if(core.getFileManager().getFile(uuid + ".yml", "plugins//ENFactions//PlayerData//").exists()) {
			FileConfiguration playerCfg = core.getFileManager().getConfiguration(uuid + ".yml", "plugins//ENFactions//PlayerData//");
			if(!playerCfg.getStringList("Information.TribeRequests").isEmpty()) {
				req = playerCfg.getStringList("Information.TribeRequests");
				if(req.contains(" ")) req.remove(" ");
				return req;
			}
			return req;
		}
		return req;
	}
	
	public boolean belongsToTribe(String tribe, String targetUUID) {
		if(tribeChieftain(tribe).equals(targetUUID)) return true;
		if(tribeRecruiters(tribe).contains(targetUUID)) return true;
		if(tribeChampions(tribe).contains(targetUUID)) return true;
		return false;
	}
	
	public void addInvitation(String tribe, String invitedUUID) {
		if(!tribeInvitations(tribe).contains(invitedUUID)) {
			File tribeData = core.getFileManager().getFile(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			FileConfiguration tribeCfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			List<String> invitations = tribeInvitations(tribe);
			invitations.add(invitedUUID);
			tribeCfg.set("Information.Invitations", invitations);
			try {
				tribeCfg.save(tribeData);
			} catch (IOException e) {
				core.getServer().getConsoleSender().sendMessage(core.getPrefix() + "§cSomething went wrong while saving one of the configs.");
			}
			File playerData = core.getFileManager().getFile(invitedUUID + ".yml", "plugins//ENFactions//PlayerData//");
			FileConfiguration playerCfg = core.getFileManager().getConfiguration(invitedUUID + ".yml", "plugins//ENFactions//PlayerData//");
			if(!tribeInvitationsOfPlayer(invitedUUID).contains(tribe.toUpperCase())) {
				List<String> tribeInvitations = tribeInvitationsOfPlayer(invitedUUID);
				tribeInvitations.add(tribe.toUpperCase());
				playerCfg.set("Information.TribeInvitations", tribeInvitations);
				try {
					playerCfg.save(playerData);
				} catch (IOException e) {
					core.getServer().getConsoleSender().sendMessage(core.getPrefix() + "§cSomething went wrong while saving one of the configs.");
				}
			}
		}
	}
	
	public void removeInvitation(String tribe, String invitedUUID) {
		if(tribeInvitations(tribe).contains(invitedUUID)) {
			File tribeData = core.getFileManager().getFile(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			FileConfiguration tribeCfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			List<String> tribeInvitations = tribeInvitations(tribe);
			tribeInvitations.remove(invitedUUID);
			tribeCfg.set("Information.Invitations", tribeInvitations);
			try {
				tribeCfg.save(tribeData);
			} catch (IOException e) {
				core.getServer().getConsoleSender().sendMessage(core.getPrefix() + "§cSomething went wrong while saving one of the configs.");
			}
			File playerData = core.getFileManager().getFile(invitedUUID + ".yml", "plugins//ENFactions//PlayerData//");
			FileConfiguration playerCfg = core.getFileManager().getConfiguration(invitedUUID + ".yml", "plugins//ENFactions//PlayerData//");
			if(tribeInvitationsOfPlayer(invitedUUID).contains(tribe.toUpperCase())) {
				List<String> tribeInvitationsOP = tribeInvitationsOfPlayer(invitedUUID);
				tribeInvitationsOP.remove(tribe.toUpperCase());
				playerCfg.set("Information.TribeInvitations", tribeInvitationsOP);
				try {
					playerCfg.save(playerData);
				} catch (IOException e) {
					core.getServer().getConsoleSender().sendMessage(core.getPrefix() + "§cSomething went wrong while saving one of the configs.");
				}
			}
		}
	}
	
	public void addMemberToTribe(String tribe, String invitedUUID) {
		if(tribeInvitations(tribe).contains(invitedUUID)) {
			File tribeData = core.getFileManager().getFile(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			FileConfiguration tribeCfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			List<String> tribeRecruiters = tribeRecruiters(tribe);
			tribeRecruiters.add(invitedUUID);
			tribeCfg.set("Information.Recruiters", tribeRecruiters);
			try {
				tribeCfg.save(tribeData);
			} catch (IOException e) {
				core.getServer().getConsoleSender().sendMessage(core.getPrefix() + "§cSomething went wrong while saving one of the configs.");
			}
			setPlayerTribe(invitedUUID, tribe.toUpperCase());
		}
	}

	public boolean wasInvited(String tribe, String invitedUUID) {
		return tribeInvitations(tribe).contains(invitedUUID);
	}
	
    public void renameTribe(CommandSender cs, String oldName, String newName) {
    	if(cs != null && cs instanceof Player) {
    	    Player p = (Player) cs;
    		if(!tribeExists(newName.toUpperCase())) {
    			if(tribeChieftain(oldName.toUpperCase()).equals(p.getUniqueId().toString())) {
					Path o = Paths.get("plugins//ENFactions//Tribes//" + oldName + ".yml");
					Path n = Paths.get("plugins//ENFactions//Tribes//" + newName + ".yml");
					try {
						Files.move(o, n, StandardCopyOption.REPLACE_EXISTING);
						p.sendMessage(core.getPrefix() + "§aTribe renamed.");
						newName = newName.toUpperCase();
						resignAllClaims(oldName, newName);
						for (String member : tribeRecruiters(newName)) {
							setPlayerTribe(member, newName);
						}
						
						String leader = tribeChieftain(newName);
						setPlayerTribe(leader, newName);
							
						for (String invitation : tribeInvitations(newName)) {
							List<String> invitations = tribeInvitationsOfPlayer(invitation);
							if (invitations.contains(oldName)) {
								invitations.remove(oldName);
								invitations.add(newName);
								File f = core.getFileManager().getFile(invitation + ".yml", "plugins//ENFactions//PlayerData//");
								if (f.exists()) {
									FileConfiguration cfg = core.getFileManager().getConfiguration(invitation + ".yml", "plugins//ENFactions//PlayerData//");
									cfg.set("Information.TribeInvitations", invitations);
									try {
										cfg.save(f);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
						for(String member : getAllMembers(newName)) {
			    			if(core.getPlayerAccount().isOnline(member)) {
			    				Player m = Bukkit.getPlayer(UUID.fromString(member));
			    				if(m != p) {
			    					m.sendMessage(core.getPrefix() + "§aTribe that you belong to was renamed to §7" + newName + " §aby §7" + p.getName() + "§a.");
			    				}
			    			}
			    		}
						for(String joinRequest : joinRequests(newName)) {
							File jrf = core.getFileManager().getFile(joinRequest + ".yml", "plugins//ENFactions//PlayerData//");
							FileConfiguration jrfcfg = core.getFileManager().getConfiguration(joinRequest + ".yml", "plugins//ENFactions//PlayerData//");
							List<String> requests = tribeRequestsOfPlayer(p.getUniqueId().toString());
							if(requests.contains(oldName)) requests.remove(oldName);
							if(!requests.contains(newName)) requests.add(newName);
							jrfcfg.set("Information.TribeRequests", requests);
							jrfcfg.save(jrf);
						}
					} catch (IOException ex) {
						ex.printStackTrace();
						p.sendMessage(core.getPrefix() + "§cCould not rename the tribe.");
					}
    			}else{
    				p.sendMessage(core.getPrefix() + "§cYou are not a chieftain of this tribe!");
    			}
    		}else{
    			p.sendMessage(core.getPrefix() + "§cTribe with that name already exists.");
    		}
    	}
    }
    
    public void resignAllClaims(String fromTribe, String toTribe) {
    	List<String> claims = new ArrayList<String>();
		File ne = core.getFileManager().getFile("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
    	FileConfiguration cfg = core.getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
		if(cfg.isConfigurationSection("ClaimedAreas")) {
			Set<String> c = cfg.getConfigurationSection("ClaimedAreas").getKeys(false);
			claims.addAll(c);
		}
		for(String claim : claims) {
			if(!getClaimOwner(claim).equals(fromTribe)) claims.remove(claim);
		}
		if(claims.size() != 0) {
			for(String claim : claims) {
				cfg.set("ClaimedAreas." + claim + ".Owner", toTribe);
				try {
					cfg.save(ne);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
    
    public void getTribeInfo(Player e, String tribe) {
    	tribe = tribe.toUpperCase();
    	if(tribeExists(tribe)) {
    		FileConfiguration tribeCfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
    		e.sendMessage("§7[§a§lELERION§7] §9Tribe Info §8| §6" + tribe + "§8:");
    		e.sendMessage(" §e- §7Name: §a" + tribe);
    		e.sendMessage(" §e- §7Level: §a" + tribeCfg.getInt("Information.Level"));
    		e.sendMessage(" §e- §7Chieftain:");
    		String leader = tribeChieftain(tribe);
    		if(core.getPlayerAccount().isOnline(leader)) {
    			e.sendMessage("   §e- §a" + core.getPlayerAccount().get(leader, "enPlayerData", "InGameName", "UUID"));
    		}else{
    			e.sendMessage("   §e- §c" + core.getPlayerAccount().get(leader, "enPlayerData", "InGameName", "UUID"));
    		}
    		e.sendMessage(" §e- §7Champions (§c" + tribeChampions(tribe).size() + "§7):");
    		for(String champion : tribeChampions(tribe)) {
    			if(core.getPlayerAccount().isOnline(champion)) {
    				e.sendMessage("   §e- §a" + core.getPlayerAccount().get(champion, "enPlayerData", "InGameName", "UUID"));
    			}else{
    				e.sendMessage("   §e- §c" + core.getPlayerAccount().get(champion, "enPlayerData", "InGameName", "UUID"));
    			}
    		}
    		e.sendMessage(" §e- §7Recruiters (§c" + tribeRecruiters(tribe).size() + "§7):");
    		for(String member : tribeRecruiters(tribe)) {
    			if(core.getPlayerAccount().isOnline(member)) {
    				e.sendMessage("   §e- §a" + core.getPlayerAccount().get(member, "enPlayerData", "InGameName", "UUID"));
    			}else{
    				e.sendMessage("   §e- §c" + core.getPlayerAccount().get(member, "enPlayerData", "InGameName", "UUID"));
    			}
    		}
    		e.sendMessage(" §e- §7Areas claimed: §a" + tribeClaims(tribe).size() + "§7/§a20");
    		e.sendMessage(" §e- §7Tribe power: §a0");
    		e.sendMessage(" §e- §7Hostile tribes: §a" + tribeCfg.getStringList("Information.Tribes.Hostile"));
    		e.sendMessage(" §e- §7Alliances: §a" + tribeCfg.getStringList("Information.Tribes.Alliances"));
    	}
    }
    
    public void deleteTribe(CommandSender cs, String tribe, boolean forceToDelete) {
    	if(cs instanceof Player) {
    		Player p = (Player) cs;
	    	if(tribeExists(tribe)) {
	    		if(!forceToDelete) {
		    		if(tribeChieftain(tribe).equals(p.getUniqueId().toString())) {
		    			String leader = tribeChieftain(tribe);
			    		setPlayerTribe(leader, null);
			    		for(String member : tribeRecruiters(tribe)) {
			    			setPlayerTribe(member, null);
			    		}
			    		for(String invitation : tribeInvitations(tribe)) {
			    			List<String> tIOP = tribeInvitationsOfPlayer(invitation);
			    			if(tIOP.contains(tribe)) {
			    				File f = core.getFileManager().getFile(invitation + ".yml", "plugins//ENFactions//PlayerData//");
								if (f.exists()) {
									tIOP.remove(tribe);
									FileConfiguration cfg = core.getFileManager().getConfiguration(invitation + ".yml", "plugins//ENFactions//PlayerData//");
									cfg.set("Information.TribeInvitations", tIOP);
									try {
										cfg.save(f);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
			    			}
			    		}
			    		for(String member : getAllMembers(tribe)) {
			    			if(core.getPlayerAccount().isOnline(member)) {
			    				Player m = Bukkit.getPlayer(UUID.fromString(member));
			    				if(m != p) {
			    					m.sendMessage(core.getPrefix() + "§cTribe that you used to belong to was deleted by §7" + p.getName() + "§c.");
			    				}
			    			}
			    		}
			    		despawnCrystal(tribe);
			    		for(String claim : tribeClaims(tribe)) {
			    			unclaim(claim);
			    		}
			    		core.getFileManager().deleteFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
			    		p.sendMessage(core.getPrefix() + "§aTribe successfully deleted.");
		    		}else{
		    			p.sendMessage(core.getPrefix() + "§cYou are not a chieftain of this tribe!");
		    		}
	    		}else{
	    			String leader = tribeChieftain(tribe);
		    		setPlayerTribe(leader, null);
		    		for(String member : tribeRecruiters(tribe)) {
		    			setPlayerTribe(member, null);
		    		}
		    		for(String invitation : tribeInvitations(tribe)) {
		    			List<String> tIOP = tribeInvitationsOfPlayer(invitation);
		    			if(tIOP.contains(tribe)) {
		    				File f = core.getFileManager().getFile(invitation + ".yml", "plugins//ENFactions//PlayerData//");
							if (f.exists()) {
								tIOP.remove(tribe);
								FileConfiguration cfg = core.getFileManager().getConfiguration(invitation + ".yml", "plugins//ENFactions//PlayerData//");
								cfg.set("Information.TribeInvitations", tIOP);
								try {
									cfg.save(f);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
		    			}
		    		}
		    		for(String member : getAllMembers(tribe)) {
		    			if(core.getPlayerAccount().isOnline(member)) {
		    				Player m = Bukkit.getPlayer(UUID.fromString(member));
		    				if(m != p) {
		    					m.sendMessage(core.getPrefix() + "§cTribe that you used to belong to was deleted by §7" + p.getName() + "§c.");
		    				}
		    			}
		    		}
		    		despawnCrystal(tribe);
		    		for(String claim : tribeClaims(tribe)) {
		    			unclaim(claim);
		    		}
		    		core.getFileManager().deleteFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
		    		p.sendMessage(core.getPrefix() + "§aTribe successfully deleted.");
	    		}
	    	}
    	}
    }
    
    public List<String> hostileTribes(String tribe) {
    	List<String> hs = new ArrayList<String>();
    	if(tribe != null) {
	    	if(tribeExists(tribe)) {
		    	FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
				if(cfg.isConfigurationSection("Information.Tribes.Hostile")) {
					Set<String> h = cfg.getConfigurationSection("Information.Tribes.Hostile").getKeys(false);
					hs.addAll(h);
					if(hs.contains(" ")) hs.remove(" ");
					return hs;
				}
				return hs;
	    	}
	    	return hs;
    	}
		return hs;
    }
    
    public String whoMadeTribesHostile(String tribe, String hostileTribe) {
    	if(tribe != null && hostileTribe != null) {
    		tribe = tribe.toUpperCase();
    		hostileTribe = hostileTribe.toUpperCase();
    		if(tribeExists(tribe) && tribeExists(hostileTribe)) {
    			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
    			if(cfg.isSet("Information.Tribes.Hostile." + hostileTribe + ".DoneBy")) {
    				return cfg.getString("Information.Tribes.Hostile." + hostileTribe + ".DoneBy");
    			}
    			return null;
    		}
    		return null;
    	}
    	return null;
    }
    
    public List<String> alliances(String tribe) {
		List<String> alliances = new ArrayList<String>();
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			alliances = cfg.getStringList("Information.Tribes.Alliances");
			if(alliances.contains(" ")) alliances.remove(" ");
			return alliances;
		}
		return alliances;
	}
    
    public List<String> allianceRequests(String tribe) {
		List<String> alliances = new ArrayList<String>();
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			alliances = cfg.getStringList("Information.Tribes.AllianceRequests");
			if(alliances.contains(" ")) alliances.remove(" ");
			return alliances;
		}
		return alliances;
	}
    
    public List<String> joinRequests(String tribe) {
		List<String> requests = new ArrayList<String>();
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe.toUpperCase() + ".yml", "plugins//ENFactions//Tribes//");
			requests = cfg.getStringList("Information.JoinRequests");
			if(requests.contains(" ")) requests.remove(" ");
			return requests;
		}
		return requests;
	}
    
    public boolean isNeutral(String yourTribe, String whichTribe) {
    	if(hostileTribes(yourTribe).contains(whichTribe)) return false;
    	if(alliances(yourTribe).contains(whichTribe)) return false;
    	return true;
    }
    
    public boolean isCrystalLocationSet(String tribe) {
    	tribe = tribe.toUpperCase();
    	if(tribeExists(tribe)) {
    		Location loc = core.getLocationHelper().getLocation(tribe + ".yml", "Information.Location.Crystal", "plugins//ENFactions//Tribes//");
    		return loc != null;
    	}
    	return false;
    }
    
	public void createTribeCrystal(Player receiver) {
    	String receiverUUID = receiver.getUniqueId().toString();
    	String playerTribe = getPlayerTribe(receiverUUID);
    	if(playerTribe != null) {
    		playerTribe = playerTribe.toUpperCase();
    		if(tribeExists(playerTribe)) {
    			if(belongsToTribe(playerTribe, receiverUUID)) {
    				if(tribeChieftain(playerTribe).equals(receiverUUID)) {
    					if(!(isCrystalCoolingDown(playerTribe))) {
    						if(!isCrystalLocationSet(playerTribe)) {
	    						Location loc = receiver.getLocation();
	    						Location cloc = loc.clone();
	    						cloc.setPitch(0); cloc.setYaw(0); cloc.setY(cloc.getY() + 1);
	    						Location a1l = cloc.clone();
	    						Location a2l = cloc.clone();
	    						a1l.setPitch(0); a1l.setYaw(0); a1l.setY(a1l.getY() + 0.5);
	    						a2l.setPitch(0); a2l.setYaw(0);
	    						cloc.getWorld().spawnEntity(cloc, EntityType.ENDER_CRYSTAL);
	    						core.getLocationHelper().saveLocation(cloc, playerTribe + ".yml", "Information.Location.Crystal", "plugins//ENFactions//Tribes//");
	    						World w = a1l.getWorld();
	    						//Crystal that belongs to tribe x
	    						ArmorStand as = (ArmorStand) w.spawnEntity(a1l, EntityType.ARMOR_STAND);
	    						as.setGravity(false);
	    						as.setVisible(false);
	    						as.setCustomName("§fCrystal owned by §a" + playerTribe);
	    						as.setCustomNameVisible(true);
	    						as.setRemoveWhenFarAway(false);
	    						
	    						//Health of crystal
	    						updateCrystalHealth(a2l, playerTribe);
	    						setTribeProtection(playerTribe, true);
	    						setCrystalCoolingDown(playerTribe, false);
	    						receiver.sendMessage(core.getPrefix() + "§aTribe crystal location saved. Watch out, your enemies might be looking for it!");
    						}else{
    							receiver.sendMessage(core.getPrefix() + "§cYour tribe already has a crystal location set.");
    						}
    					}else{
							receiver.sendMessage(core.getPrefix() + "§cYour crystal is regaining its life, so you can not place it right now.");
						}
    				}else{
						receiver.sendMessage(core.getPrefix() + "§cYou're not permitted to do that.");
					}
    			}else{
    				setPlayerTribe(receiverUUID, null);
    				receiver.sendMessage(core.getPrefix() + "§cYou do not belong to tribe set in your data file. We managed to automatically repair the problem.");
    			}
    		}else{
    			receiver.sendMessage(core.getPrefix() + "§cTribe §7" + playerTribe + " §cdoes not exist.");
    		}
    	}else{
    		receiver.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes yet.");
    	}
    }
	
	public void respawnCrystal(String tribe) {
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(isCrystalLocationSet(tribe)) {
				Location loc = core.getLocationHelper().getLocation(tribe + ".yml", "Information.Location.Crystal", "plugins//ENFactions//Tribes//");
				Location a1l = loc.clone();
				Location a2l = loc.clone();
				a1l.setPitch(0); a1l.setYaw(0); a1l.setY(a1l.getY() + 0.5);
				a2l.setPitch(0); a2l.setYaw(0);
				loc.getWorld().spawnEntity(loc, EntityType.ENDER_CRYSTAL);
				
				//Crystal holder
				ArmorStand as = (ArmorStand) a1l.getWorld().spawnEntity(a1l, EntityType.ARMOR_STAND);
				as.setGravity(false);
				as.setSmall(true);
				as.setVisible(false);
				as.setCustomName("§fCrystal owned by §a" + tribe);
				as.setCustomNameVisible(true);
				as.setRemoveWhenFarAway(false);
				
				//Health
				setCrystalLives(tribe, 100);
				updateCrystalHealth(a2l, tribe);
				setTribeProtection(tribe, true);
				
				//Setting crystal status
				setCrystalCoolingDown(tribe, false);
				
				//Notify tribe members
				for(String member : getAllMembers(tribe)) {
					Player p = Bukkit.getPlayer(UUID.fromString(member));
					if(p != null) p.sendMessage(core.getPrefix() + "§aYour crystal has recovered it's health. From now on, your tribe is protected by a crystal which was respawned.");
				}
			}
		}
	}
	
	public void despawnCrystal(String tribe) {
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				if(isCrystalLocationSet(tribe)) {
					Location cloc = core.getLocationHelper().getLocation(tribe + ".yml", "Information.Location.Crystal", "plugins//ENFactions//Tribes//");
					Location l1 = cloc.clone();
					Location l2 = cloc.clone();
					l1.setY(l1.getY() + 1.5);
					l2.setY(l2.getY() + 0.5);
					for(Entity e : Bukkit.getWorld(cloc.getWorld().getName()).getEntities()) {
						if(e.getLocation().equals(cloc) && e instanceof EnderCrystal) {
							e.remove();
						}
						if(e instanceof ArmorStand) {
							if(e.getLocation().equals(l1) || e.getLocation().equals(l2));
							e.remove();
						}
					}
					setTribeProtection(tribe, false);
					setCrystalCoolingDown(tribe, true);
				}
			}
		}
	}
    
    public List<String> getAllMembers(String tribe) {
    	tribe = tribe.toUpperCase();
    	List<String> members = new ArrayList<String>();
    	String c = tribeChieftain(tribe);
    	members.add(c);
    	for(String r : tribeRecruiters(tribe)) {
    		members.add(r);
    	}
    	for(String ch : tribeChampions(tribe)) {
    		members.add(ch);
    	}
    	return members;
    }
    
    public List<String> tribeClaims(String tribe) {
    	tribe = tribe.toUpperCase();
    	List<String> claims = new ArrayList<String>();
    	if(tribeExists(tribe)) {
    		for(String claim : allClaims()) {
    			if(getClaimOwner(claim).equals(tribe)) claims.add(claim);
    		}
    		return claims;
    	}
    	return claims;
    }
    
    public List<String> allClaims() {
    	List<String> claims = new ArrayList<String>();
    	FileConfiguration cfg = core.getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
		if(cfg.isConfigurationSection("ClaimedAreas")) {
			Set<String> c = cfg.getConfigurationSection("ClaimedAreas").getKeys(false);
			claims.addAll(c);
			return claims;
		}
		return claims;
    }
    
    public String getClaimOwner(String claim) {
    	FileConfiguration cfg = core.getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
    	if(cfg.isSet("ClaimedAreas." + claim + ".Owner")) {
    		return cfg.getString("ClaimedAreas." + claim + ".Owner");
    	}
    	return null;
    }
    
    public Location getMinLocation(String claim) {
    	FileConfiguration cfg = core.getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
    	if(cfg.isConfigurationSection("ClaimedAreas." + claim)) {
    		return core.getLocationHelper().getLocation("Claimed_Areas.yml", "ClaimedAreas." + claim + ".Min", "plugins//ENFactions//Tribes//");
    	}
    	return null;
    }
    
    public Location getMaxLocation(String claim) {
    	FileConfiguration cfg = core.getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
    	if(cfg.isConfigurationSection("ClaimedAreas." + claim)) {
    		return core.getLocationHelper().getLocation("Claimed_Areas.yml", "ClaimedAreas." + claim + ".Max", "plugins//ENFactions//Tribes//");
    	}
    	return null;
    }
    
    public boolean isInsideArea(Location loc, Location minLoc, Location maxLoc) {
    	int mix = minLoc.getBlockX(); //Min loc x
		int max = maxLoc.getBlockX(); //Max loc x
		int lox = loc.getBlockX(); //Loc x
		int miy = minLoc.getBlockY();
		int may = maxLoc.getBlockY();
		int loy = loc.getBlockY();
		int miz = minLoc.getBlockZ();
		int maz = maxLoc.getBlockZ();
		int loz = loc.getBlockZ();
		if((lox <= max && lox >= mix) && (loy <= may && loy >= miy) && (loz <= maz && loz >= miz)) return true;
		return false;
//    	Vector pos1 = new Vector(minLoc.getBlockX(),0,minLoc.getBlockZ());
//    	Vector pos2 = new Vector(maxLoc.getBlockZ(),0,maxLoc.getBlockZ());
//    	Vector v = new Vector(loc.getBlockX(), 0, loc.getBlockZ());
//    	return v.isInAABB(Vector.getMinimum(pos1, pos2), Vector.getMaximum(pos1, pos2));
    }
    
    public List<String> allTribes() {
    	List<String> result = new ArrayList<String>();
    	File[] files = new File("plugins//ENFactions//Tribes//").listFiles();
    	for(File file : files) {
    		if(!file.getName().equalsIgnoreCase("claimed_areas")) {
    			if(file.isFile()) {
    				result.add(file.getName());
    			}
    		}
    	}
    	return result;
    }
    
	public void claim(Player p) {
		Location l1 = getMinLocationOfBox(p.getLocation());
		Location h4 = getMaxLocationOfBox(p.getLocation());
		p.sendMessage(l1.toString());
		p.sendMessage(h4.toString());
		p.sendMessage(core.getLocationHelper().getLocation(getPlayerTribe(p.getUniqueId().toString()) + ".yml", "Information.Location.Crystal", "plugins//ENFactions//Tribes//").toString());
//		Location ploc = p.getLocation();
//    	int y = ploc.getBlockY();
//    	World world = ploc.getWorld();
//    	Chunk c = p.getLocation().getChunk();
//    	int blockX = c.getX() << 4;
//    	int blockZ = c.getZ() << 4;
//    	Location l1 = new Location(world, blockX, 1, blockZ, 0, 0);//c.getWorld().getHighestBlockAt(blockX, blockZ).getLocation();
//    	Location h4 = new Location(world, blockX + 15, 1, blockZ + 15, 0, 0);//c.getWorld().getHighestBlockAt(blockX +15, blockZ +15).getLocation();
//    	if(y >= 1 && y <= 16) {
//    		h4.add(0, 15, 0);
//    	}else if(y >= 17 && y <= 32) {
//    		l1.add(0, 16, 0);
//    		h4.add(0, 31, 0);
//    	}else if(y >= 33 && y <= 48) {    		
//    		l1.add(0, 32, 0);
//    		h4.add(0, 47, 0);
//    	}else if(y >= 49 && y <= 64) {
//    		l1.add(0, 48, 0);
//    		h4.add(0, 63, 0);
//    	}else if(y >= 65 && y <= 80) {
//    		l1.add(0, 64, 0);
//    		h4.add(0, 79, 0);
//    	}else if(y >= 81 && y <= 96) {
//    		l1.add(0, 80, 0);
//    		h4.add(0, 95, 0);
//    	}else if(y >= 97 && y <= 112) {
//    		l1.add(0, 96, 0);
//    		h4.add(0, 111, 0);
//    	}else if(y >= 113 && y <= 128) {
//    		l1.add(0, 112, 0);
//    		h4.add(0, 127, 0);
//    	}else if(y >= 129 && y <= 144) {
//    		l1.add(0, 128, 0);
//    		h4.add(0, 143, 0);
//    	}else if(y >= 145 && y <= 160) {
//    		l1.add(0, 144, 0);
//    		h4.add(0, 159, 0);
//    	}else if(y >= 161 && y <= 176) {
//    		l1.add(0, 160, 0);
//    		h4.add(0, 175, 0);
//    	}else if(y >= 177 && y <= 192) {
//    		l1.add(0, 176, 0);
//    		h4.add(0, 191, 0);
//    	}else if(y >= 193 && y <= 208) {
//    		l1.add(0, 192, 0);
//    		h4.add(0, 207, 0);
//    	}else if(y >= 209 && y <= 224) {
//    		l1.add(0, 208, 0);
//    		h4.add(0, 223, 0);
//    	}else if(y >= 225 && y <= 240) {
//    		l1.add(0, 224, 0);
//    		h4.add(0, 239, 0);
//    	}else if(y >= 241 && y <= 256) {
//    		l1.add(0, 240, 0);
//    		h4.add(0, 255, 0);
//    	}
    	String uuid = p.getUniqueId().toString();
    	String tribe = getPlayerTribe(uuid);
    	if(tribe != null) {
    		tribe = tribe.toUpperCase();
    		if(tribeExists(tribe)) {
    			if(belongsToTribe(tribe, uuid)) {
    				if(tribeChieftain(tribe).equals(uuid) || tribeChampions(tribe).contains(uuid)) {
    					if(isClaimed(l1, h4)) {
							p.sendMessage(core.getPrefix() + "§cThis box is already claimed.");
						}else{
							List<String> tclaims = tribeClaims(tribe);
    						int ci = allClaims().size() + 1;
    						if(tclaims.size() == 0) {
	    						if(isCrystalLocationSet(tribe)) {
	    							if(isInsideArea(core.getLocationHelper().getLocation(tribe + ".yml", "Information.Location.Crystal", "plugins//ENFactions//Tribes//"), l1, h4)) {
	    								saveNewClaimInformation(p, tribe, String.valueOf(ci), l1, h4);
	    							}else{
	    								p.sendMessage(core.getPrefix() + "§cYour first claim should also protect a crystal!");
	    							}
	    						}else{
	    							p.sendMessage(core.getPrefix() + "§cCrystal location is not set. To set it, use /t crystal!");
	    						}
    						}else{
    							saveNewClaimInformation(p, tribe, String.valueOf(ci), l1, h4);
    						}
						}
    				}else{
						p.sendMessage(core.getPrefix() + "§cYou're not permitted to do that.");
					}
    			}else{
    				p.sendMessage(core.getPrefix() + "§cYou do not belong to tribe §7" + tribe + "§c.");
    				setPlayerTribe(uuid, null);
    			}
    		}else{
    			p.sendMessage(core.getPrefix() + "§cTribe you are supposed to belong to doesn't exist anymore.");
    			setPlayerTribe(uuid, null);
    		}
    	}else{
    		p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
    	}
    }
	
	public void unclaim(Player p) {
		String uuid = p.getUniqueId().toString();
		String tribe = getPlayerTribe(uuid);
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				if(belongsToTribe(tribe, uuid)) {
					if(tribeChieftain(tribe).equals(uuid) || tribeChampions(tribe).contains(uuid)) {
						Location ploc = p.getLocation();
						Location l = getMinLocationOfBox(ploc);
						Location h = getMaxLocationOfBox(ploc);
						String claim = getClaim(l, h);
						if(claim != null) {
							if(getClaimOwner(claim).equals(tribe)) {
								int tcs = tribeClaims(tribe).size();
								if(tcs == 0) {
									p.sendMessage(core.getPrefix() + "§cYour tribe does not have any claimed boxes yet.");
								}else if(tcs == 1) {
									Location cloc = core.getLocationHelper().getLocation(tribe + ".yml", "Information.Location.Crystal", "plugins//ENFactions//Tribes//");
									if(cloc != null) {
										despawnCrystal(tribe);
										core.getLocationHelper().removeLocation(tribe + ".yml", "Information.Location.Crystal", "plugins//ENFactions//Tribes//");
										List<String> allClaims = allClaims();
										FileConfiguration ccfg = core.getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
										if(allClaims.contains(claim)) {
											ccfg.set("ClaimedAreas." + claim, null);
											try {
												ccfg.save(core.getFileManager().getFile("Claimed_Areas.yml", "plugins//ENFactions//Tribes//"));
											} catch (IOException e) {
												e.printStackTrace();
											}
											p.sendMessage(core.getPrefix() + "§aYou successfully unclaimed this box.");
											for(String member : getAllMembers(tribe)) {
			    								if(core.getPlayerAccount().isOnline(member)) {
			    									Player m = Bukkit.getPlayer(UUID.fromString(member));
			    									if(m != p) m.sendMessage(core.getPrefix() + "§7" + p.getName() + " §aunclaimed an area which used to belong to your tribe!");
			    								}
			    							}
										}
									}
								}else{ 
									List<String> allClaims = allClaims();
									FileConfiguration ccfg = core.getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
									if(allClaims.contains(claim)) {
										Location cloc = core.getLocationHelper().getLocation(tribe + ".yml", "Information.Location.Crystal", "plugins//ENFactions//Tribes//");
										if(!core.getTribesAPI().isInsideArea(cloc, l, h)) {
											ccfg.set("ClaimedAreas." + claim, null);
											try {
												ccfg.save(core.getFileManager().getFile("Claimed_Areas.yml", "plugins//ENFactions//Tribes//"));
											} catch (IOException e) {
												e.printStackTrace();
											}
											p.sendMessage(core.getPrefix() + "§aYou successfully unclaimed this box.");
											for(String member : getAllMembers(tribe)) {
			    								if(core.getPlayerAccount().isOnline(member)) {
			    									Player m = Bukkit.getPlayer(UUID.fromString(member));
			    									if(m != p) m.sendMessage(core.getPrefix() + "§7" + p.getName() + " §aunclaimed an area which used to belong to your tribe!");
			    								}
			    							}
										}else{
											p.sendMessage(core.getPrefix() + "§cYou can not unclaim this area, because you have more than 1 area and the crystal is located inside.");
										}
									}
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cThis box is claimed but does not belong to your tribe.");
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cThis box is not claimed yet.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou're not a chieftain of this tribe.");
					}
				}else{
    				p.sendMessage(core.getPrefix() + "§cYou do not belong to tribe §7" + tribe + "§c.");
    				setPlayerTribe(uuid, null);
    			}
			}else{
    			p.sendMessage(core.getPrefix() + "§cTribe you are supposed to belong to doesn't exist anymore.");
    			setPlayerTribe(uuid, null);
    		}
		}else{
    		p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
    	}
	}
	
	public void unclaim(String claim) {
		if(claim != null) {
			String tribe = getClaimOwner(claim);
			if(tribe != null && tribeExists(tribe)) {
				FileConfiguration ccfg = core.getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
				if(ccfg.isConfigurationSection("ClaimedAreas." + claim)) {
					ccfg.set("ClaimedAreas." + claim, null);
					try {
						ccfg.save(core.getFileManager().getFile("Claimed_Areas.yml", "plugins//ENFactions//Tribes//"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public boolean isClaimed(Location low, Location high) {
		int allClaims = 0;
		for(String claim : allClaims()) {
			Location minLoc = getMinLocation(claim);
			Location maxLoc = getMaxLocation(claim);
			if((minLoc.getX() == low.getX() && minLoc.getY() == low.getY() && minLoc.getZ() == low.getZ() && minLoc.getWorld().getName() == low.getWorld().getName()) && (maxLoc.getX() == high.getX() && maxLoc.getY() == high.getY() && maxLoc.getZ() == high.getZ() && maxLoc.getWorld().getName() == high.getWorld().getName())) {
				allClaims += 1;
			}
		}
		return allClaims != 0;
	}
	
	public Location getMinLocationOfBox(Location loc) {
		World w = loc.getWorld();
		Chunk c = loc.getChunk();
    	int x = c.getX() << 4;
    	int z = c.getZ() << 4;
    	return new Location(w, x, 1, z, 0, 0);
	}
	
	public Location getMaxLocationOfBox(Location loc) {
		World w = loc.getWorld();
		Chunk c = loc.getChunk();
    	int x = c.getX() << 4;
    	int y = w.getMaxHeight() - 1;
    	int z = c.getZ() << 4;
    	return new Location(w, x+15, y, z+15, 0, 0);
	}
	
	public String getClaim(Location low, Location high) {
		FileConfiguration ccfg = core.getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
		int claims = 0;
		String claimName = null;
		for(String c : allClaims()) {
			Location minLoc = getMinLocation(c);
			Location maxLoc = getMaxLocation(c);
			if((minLoc.getX() == low.getX() && minLoc.getY() == low.getY() && minLoc.getZ() == low.getZ() && minLoc.getWorld().getName() == low.getWorld().getName()) && (maxLoc.getX() == high.getX() && maxLoc.getY() == high.getY() && maxLoc.getZ() == high.getZ() && maxLoc.getWorld().getName() == high.getWorld().getName())) {
				if(ccfg.isSet("ClaimedAreas." + c + ".Name")) claimName = ccfg.getString("ClaimedAreas." + c + ".Name");
				claims += 1;
			}
		}
		if(claims == 0) {
			return null;
		}
		return claimName;
	}
	
	public void saveNewClaimInformation(Player p, String tribe, String claimName, Location low, Location high) {
		FileConfiguration cfg = core.getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
        cfg.set("ClaimedAreas." + claimName + ".Min.X", low.getX());
        cfg.set("ClaimedAreas." + claimName + ".Min.Y", low.getY());
        cfg.set("ClaimedAreas." + claimName + ".Min.Z", low.getZ());
        cfg.set("ClaimedAreas." + claimName + ".Min.Yaw", low.getYaw());
        cfg.set("ClaimedAreas." + claimName + ".Min.Pitch", low.getPitch());
        cfg.set("ClaimedAreas." + claimName + ".Min.World", low.getWorld().getName());
        
        cfg.set("ClaimedAreas." + claimName + ".Max.X", high.getX());
        cfg.set("ClaimedAreas." + claimName + ".Max.Y", high.getY());
        cfg.set("ClaimedAreas." + claimName + ".Max.Z", high.getZ());
        cfg.set("ClaimedAreas." + claimName + ".Max.Yaw", high.getYaw());
        cfg.set("ClaimedAreas." + claimName + ".Max.Pitch", high.getPitch());
        cfg.set("ClaimedAreas." + claimName + ".Max.World", high.getWorld().getName());
        
        cfg.set("ClaimedAreas." + claimName + ".Owner", tribe.toUpperCase());
        cfg.set("ClaimedAreas." + claimName + ".Name", claimName);

        try{
            cfg.save(core.getFileManager().getFile("Claimed_Areas.yml", "plugins//ENFactions//Tribes//"));
            p.sendMessage(core.getPrefix() + "§aYou successfully claimed this 16x16x16 box.");
            for(String member : getAllMembers(tribe)) {
            	Player m = Bukkit.getPlayer(UUID.fromString(member));
            	if(m != p) m.sendMessage(core.getPrefix() + "§7" + p.getName() + " §aclaimed a new area for your tribe.");
            }
        }catch(IOException ex) {
            ex.printStackTrace();
        }
	}
	
	public void setRole(Player executor, Player target, String role) {
		if (executor != null) {
			if (target != null) {
				if(executor != target) {
					String euuid = executor.getUniqueId().toString();
					String tuuid = target.getUniqueId().toString();
					String et = getPlayerTribe(euuid);
					if (et != null) {
						et = et.toUpperCase();
						if (tribeExists(et)) {
							if(tribeChieftain(et).equals(euuid)) {
								if (belongsToTribe(et, tuuid)) {
									File f = core.getFileManager().getFile(et + ".yml", "plugins//ENFactions//Tribes//");
									FileConfiguration cfg = core.getFileManager().getConfiguration(et + ".yml", "plugins//ENFactions//Tribes//");
									if (role.equalsIgnoreCase("CHIEFTAIN")) {
										if(!src.containsKey(euuid) && !src.containsValue(tuuid)) {
											src.put(euuid, tuuid);
											executor.sendMessage(core.getPrefix() + "§aPromoting someone else to a chieftain will get you demoted to a champion. If you agree with that, please use §7/t srconfirm §awithin the next 60 seconds, otherwise simply ignore it.");
											core.getServer().getScheduler().scheduleSyncDelayedTask(core, new Runnable() {
												@Override
												public void run() {
													if(src.containsKey(euuid) && src.get(euuid).equals(tuuid)) {
														executor.sendMessage(core.getPrefix() + "§aThe request to promote §7" + Bukkit.getPlayer(UUID.fromString(src.get(euuid))).getName() + " §ato a chieftain has expired.");
														src.remove(euuid, tuuid);
													}
												}
												
											}, 60*20);
										}else{
											executor.sendMessage(core.getPrefix() + "§cYou already requested to promote this player to chieftain. In order to promote the player, please use §7/t srconfirm §awithin the next 60 seconds. After that time, the request will expire. If you decide to confirm it, please note that you will be demoted to a champion.");
										}
									}else if (role.equalsIgnoreCase("CHAMPION")) {
										if (!tribeChampions(et).contains(tuuid)) {
											List<String> c = tribeChampions(et);
											c.add(tuuid);
											List<String> recruiters = tribeRecruiters(et);
											if(recruiters.contains(tuuid)) recruiters.remove(tuuid);
											cfg.set("Information.Champions", c);
											cfg.set("Information.Recruiters", recruiters);
											try {
												cfg.save(f);
												executor.sendMessage(core.getPrefix() + "§aSuccessfully set a new tribe role of player §7" + target.getName());
												target.sendMessage(core.getPrefix() + "§aYour tribe role was updated to §7CHAMPION §aby §7" + executor.getName());
												for(String member : getAllMembers(et)) {
													Player m = Bukkit.getPlayer(UUID.fromString(member));
													if(m != null && m != executor && m != target) {
														m.sendMessage(core.getPrefix() + "§7" + target + " §awas promoted to a tribe champion by §7 + " + executor.getName() + ".");
													}
												}
											} catch (IOException e) {
												e.printStackTrace();
											}
										}else{
											executor.sendMessage(core.getPrefix() + "§cThis player is already a champion of this tribe.");
										}
									}else if (role.equalsIgnoreCase("RECRUITER")) {
										if (!tribeRecruiters(et).contains(tuuid)) {
											List<String> r = tribeRecruiters(et);
											r.add(tuuid);
											List<String> champ = tribeChampions(et);
											if(champ.contains(tuuid)) champ.remove(tuuid);
											cfg.set("Information.Recruiters", r);
											cfg.set("Information.Champions", champ);
											try {
												cfg.save(f);
												executor.sendMessage(core.getPrefix() + "§aSuccessfully set a new tribe role of player §7" + target.getName());
												target.sendMessage(core.getPrefix() + "§aYour tribe role was updated to §7RECRUITER §aby §7" + executor.getName());
												for(String member : getAllMembers(et)) {
													Player m = Bukkit.getPlayer(UUID.fromString(member));
													if(m != null && m != executor && m != target) {
														m.sendMessage(core.getPrefix() + "§7" + target + " §awas demoted to a tribe recruiter by §7 + " + executor.getName() + ".");
													}
												}
											} catch (IOException e) {
												e.printStackTrace();
											}
										}else{
											executor.sendMessage(core.getPrefix() + "§cThis player is already a recruiter of this tribe.");
										}
									}else{
										executor.sendMessage(core.getPrefix() + "§cUnknown tribe role. Please choose between chieftain, champion and recruiter.");
									}
								}else{
									executor.sendMessage(core.getPrefix() + "§cThis player does not belong to your tribe.");
								}
							}else{
								executor.sendMessage(core.getPrefix() + "§cYou are not a chieftain of this tribe.");
							}
						}else{
							executor.sendMessage(core.getPrefix() + "§cTribe you're supposed to belong to doesn't exist.");
							setPlayerTribe(euuid, null);
						}
					}else{
						executor.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
					}
				}else{
					executor.sendMessage(core.getPrefix() + "§cYou can not use this command on yourself.");
				}
			}else{
				executor.sendMessage(core.getPrefix() + "§cThis player is offline.");
			}
		}
	}
	
	public void confirmPromotionToChieftain(Player currentChieftain) {
		String ccUUID = currentChieftain.getUniqueId().toString();
		String ccTribe = getPlayerTribe(ccUUID);
		if(ccTribe != null) {
			ccTribe = ccTribe.toUpperCase();
			if(tribeExists(ccTribe)) {
				if(tribeChieftain(ccTribe).equals(ccUUID)) {
					if(src.containsKey(ccUUID)) {
						String ncUUID = src.get(ccUUID);
						Player newChieftain = Bukkit.getPlayer(UUID.fromString(ncUUID));
						if(newChieftain != null) {
							String ncTribe = getPlayerTribe(ncUUID);
							if(ncTribe != null) {
								ncTribe = ncTribe.toUpperCase();
								if(ccTribe.equals(ncTribe)) {
									File f = core.getFileManager().getFile(ccTribe + ".yml", "plugins//ENFactions//Tribes//");
									FileConfiguration cfg = core.getFileManager().getConfiguration(ccTribe + ".yml", "plugins//ENFactions//Tribes//");
									List<String> champions = tribeChampions(ccTribe);
									champions.add(ccUUID);
									List<String> recruiters = tribeRecruiters(ccTribe);
									if(recruiters.contains(ncUUID)) recruiters.remove(ncUUID);
									cfg.set("Information.Chieftain", ncUUID);
									cfg.set("Information.Champions", champions);
									cfg.set("Information.Recruiters", recruiters);
									try {
										cfg.save(f);
										currentChieftain.sendMessage(core.getPrefix() + "§aSuccessfully set a new tribe role of player §7" + newChieftain.getName() + ". Your were also demoted down to a tribe champion.");
										newChieftain.sendMessage(core.getPrefix() + "§aYour tribe role was updated to §7CHIEFTAIN §aby §7" + currentChieftain.getName());
										for(String member : getAllMembers(ccTribe)) {
											Player m = Bukkit.getPlayer(UUID.fromString(member));
											if(m != null && m != currentChieftain && m != newChieftain) {
												m.sendMessage(core.getPrefix() + "§7" + newChieftain + " §ais a new chieftain of your tribe. Your old chieftain was demoted to a champion.");
											}
										}
										src.remove(ccUUID, ncUUID);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}else{
									currentChieftain.sendMessage(core.getPrefix() + "§cThis player does not belong to your tribe.");
								}
							}else{
								currentChieftain.sendMessage(core.getPrefix() + "§cThis player does not belong to your tribe.");
							}
						}else{
							currentChieftain.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
						}
					}else{
						currentChieftain.sendMessage(core.getPrefix() + "§cYou have not requested to promote anyone to a chieftain.");
					}
				}else{
					currentChieftain.sendMessage(core.getPrefix() + "§cOnly tribe chieftain can execute this command.");
				}
			}else{
				currentChieftain.sendMessage(core.getPrefix() + "§cTribe you're supposed to belong to doesn't exist.");
				setPlayerTribe(ccUUID, null);
			}
		}else{
			currentChieftain.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
		}
	}
	
	public void acceptAllianceRequest(Player p, String req) {
		String puuid = p.getUniqueId().toString();
		String tribe = getPlayerTribe(puuid);
		req = req.toUpperCase();
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				if(tribeChieftain(tribe).equals(puuid) || tribeChampions(tribe).contains(puuid)) {
					if(allianceRequests(tribe).contains(req) && !alliances(tribe).contains(req)) {
						File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
						FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
						File f1 = core.getFileManager().getFile(req + ".yml", "plugins//ENFactions//Tribes//");
						FileConfiguration cfg1 = core.getFileManager().getConfiguration(req + ".yml", "plugins//ENFactions//Tribes//");
						if(hostileTribes(tribe).contains(req)) {
							List<String> ht = hostileTribes(tribe);
							ht.remove(req);
							cfg.set("Information.Tribes.Hostile", ht);
						}
						List<String> reqs = allianceRequests(tribe);
						List<String> ally = alliances(tribe);
						List<String> rally = alliances(req);
						reqs.remove(req);
						ally.add(req);
						rally.add(tribe);
						cfg.set("Information.Tribes.AllianceRequests", reqs);
						cfg.set("Information.Tribes.Alliances", ally);
						cfg1.set("Information.Tribes.Alliances", rally);
						try {
							cfg.save(f);
							cfg1.save(f1);
							p.sendMessage(core.getPrefix() + "§aAlliance request successfully accepted.");
						} catch (IOException e) {
							e.printStackTrace();
						}
						for(String member : getAllMembers(req)) {
							if(core.getPlayerAccount().isOnline(member)) {
								Bukkit.getPlayer(UUID.fromString(member)).sendMessage(core.getPrefix() + "§7" + tribe + " §ais now an alliance to your tribe.");
							}
						}
						for(String member : getAllMembers(tribe)) {
							if(core.getPlayerAccount().isOnline(member)) {
								Bukkit.getPlayer(UUID.fromString(member)).sendMessage(core.getPrefix() + "§7" + req + " §ais now an alliance to your tribe.");
							}
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cThis tribes has not requested to be alliance with your tribe.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cYou're not permitted to do that.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cTribe you're supposed to belong to doesn't exist.");
				setPlayerTribe(puuid, null);
			}
		}else{
			p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
		}
	}
	
	public void denyAllianceRequest(Player p, String req) {
		String puuid = p.getUniqueId().toString();
		String tribe = getPlayerTribe(puuid);
		req = req.toUpperCase();
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				if(tribeChieftain(tribe).equals(puuid) || tribeChampions(tribe).contains(puuid)) {
					if(allianceRequests(tribe).contains(req)) {
						File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
						FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
						List<String> reqs = allianceRequests(tribe);
						reqs.remove(req);
						cfg.set("Information.Tribes.AllianceRequests", reqs);
						try {
							cfg.save(f);
							p.sendMessage(core.getPrefix() + "§aAlliance request successfully denied.");
						} catch (IOException e) {
							e.printStackTrace();
						}
						for(String member : getAllMembers(req)) {
							if(core.getPlayerAccount().isOnline(member)) {
								Bukkit.getPlayer(UUID.fromString(member)).sendMessage(core.getPrefix() + "§7" + tribe + " §cdenied an alliance request.");
							}
						}
						for(String member : getAllMembers(tribe)) {
							Player m = Bukkit.getPlayer(UUID.fromString(member));
							if(m != null && m != p) m.sendMessage(core.getPrefix() + "§7" + p.getName() + " §cdenied an alliance request sent by tribe §7" + req + "§a.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cThis tribes has not requested to be alliance with your tribe.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cYou're not permitted to do that.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cTribe you're supposed to belong to doesn't exist.");
				setPlayerTribe(puuid, null);
			}
		}else{
			p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
		}
	}
	
	public void cancelJoinRequest(Player p, String tribe) {
		String uuid = p.getUniqueId().toString();
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				if(joinRequests(tribe).contains(uuid) && tribeRequestsOfPlayer(uuid).contains(tribe)) {
					File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
					FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
					List<String> req = joinRequests(tribe);
					req.remove(uuid);
					cfg.set("Information.JoinRequests", req);
					File pf = core.getFileManager().getFile(uuid + ".yml", "plugins//ENFactions//PlayerData//");
					FileConfiguration pcfg = core.getFileManager().getConfiguration(uuid + ".yml", "plugins//ENFactions//PlayerData//");
					List<String> preq = tribeRequestsOfPlayer(uuid);
					preq.remove(tribe);
					pcfg.set("Information.TribeRequests", preq);
					try {
						cfg.save(f);
						pcfg.save(pf);
						p.sendMessage(core.getPrefix() + "§aYou have just cancelled the request to join the tribe.");
						String tc = tribeChieftain(tribe);
						if(core.getPlayerAccount().isOnline(tc)) {
							Bukkit.getPlayer(UUID.fromString(tc)).sendMessage(core.getPrefix() + "§7" + p.getName() + " §ahas just cancelled the request to join your tribe.");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cYou haven't requested to join the tribe yet.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cTribe does not exist.");
			}
		}else{
			p.sendMessage(core.getPrefix() + "§cTribe does not exist.");
		}
	}
	
	public void acceptJoinRequest(Player p, Player acceptedPlayer) {
		String uuid = p.getUniqueId().toString();
		String tuuid = acceptedPlayer.getUniqueId().toString();
		String tribe = getPlayerTribe(uuid);
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				if(tribeChieftain(tribe).equals(uuid) || tribeChampions(tribe).contains(uuid)) {
					if(joinRequests(tribe).contains(tuuid) && tribeRequestsOfPlayer(tuuid).contains(tribe)) {
						File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
						FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
						List<String> req = joinRequests(tribe);
						List<String> members = tribeRecruiters(tribe);
						members.add(tuuid);
						req.remove(tuuid);
						cfg.set("Information.JoinRequests", req);
						cfg.set("Information.Recruiters", members);
						File pf = core.getFileManager().getFile(tuuid + ".yml", "plugins//ENFactions//PlayerData//");
						FileConfiguration pcfg = core.getFileManager().getConfiguration(tuuid + ".yml", "plugins//ENFactions//PlayerData//");
						List<String> preq = tribeRequestsOfPlayer(tuuid);
						preq.remove(tribe);
						pcfg.set("Information.Tribe", tribe);
						pcfg.set("Information.TribeRequests", preq);
						try {
							cfg.save(f);
							pcfg.save(pf);
							p.sendMessage(core.getPrefix() + "§aYou have just accepted §7" + acceptedPlayer.getName() + "'s §arequest to join your tribe.");
							acceptedPlayer.sendMessage(core.getPrefix() + "§aYour request to join the tribe §7" + tribe + " §awas accepted by §7" + p.getName());
							for(String member : getAllMembers(tribe)) {
								Player m = Bukkit.getPlayer(UUID.fromString(member));
								if(m != acceptedPlayer && m != p) m.sendMessage(core.getPrefix() + "§7" + acceptedPlayer.getName() + " §awas added to your tribe by §7" + p.getName() + "§a!");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player has not requested to join your tribe.");
					}
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cTribe you're supposed to belong to doesn't exist.");
				setPlayerTribe(uuid, null);
			}
		}else{
			p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
		}
	}
	
	public void denyJoinRequest(Player p, Player acceptedPlayer) {
		String uuid = p.getUniqueId().toString();
		String tuuid = acceptedPlayer.getUniqueId().toString();
		String tribe = getPlayerTribe(uuid);
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				if(tribeChieftain(tribe).equals(uuid) || tribeChampions(tribe).contains(uuid)) {
					if(joinRequests(tribe).contains(tuuid) && tribeRequestsOfPlayer(tuuid).contains(tribe)) {
						File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
						FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
						List<String> req = joinRequests(tribe);
						req.remove(tuuid);
						cfg.set("Information.JoinRequests", req);
						File pf = core.getFileManager().getFile(tuuid + ".yml", "plugins//ENFactions//PlayerData//");
						FileConfiguration pcfg = core.getFileManager().getConfiguration(tuuid + ".yml", "plugins//ENFactions//PlayerData//");
						List<String> preq = tribeRequestsOfPlayer(tuuid);
						preq.remove(tribe);
						pcfg.set("Information.TribeRequests", preq);
						try {
							cfg.save(f);
							pcfg.save(pf);
							p.sendMessage(core.getPrefix() + "§aYou have just denied §7" + acceptedPlayer.getName() + "'s §arequest to join your tribe.");
							acceptedPlayer.sendMessage(core.getPrefix() + "§cYour request to join the tribe §7" + tribe + " §cwas denied by §7" + p.getName() + "§c.");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cThis player has not requested to join your tribe.");
					}
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cTribe you're supposed to belong to doesn't exist.");
				setPlayerTribe(uuid, null);
			}
		}else{
			p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
		}
	}
	
	public boolean isEffectActive(String tribe, EffectType effectType) {
		if(tribe != null) {
			if(tribeExists(tribe)) {
				tribe = tribe.toUpperCase();
				FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
				if(effectType != null) {
					if(effectType.equals(EffectType.LETHALITY)) {
						if(cfg.isSet("Information.Effect.Lethality.Active")) {
							return cfg.getBoolean("Information.Effect.Lethality.Active");
						}else{
							return false;
						}
					}else if(effectType.equals(EffectType.PRECISION)) {
						if(cfg.isSet("Information.Effect.Precision.Active")) {
							return cfg.getBoolean("Information.Effect.Precision.Active");
						}else{
							return false;
						}
					}else if(effectType.equals(EffectType.HEALING_AURA)) {
						if(cfg.isSet("Information.Effect.HealingAura.Active")) {
							return cfg.getBoolean("Information.Effect.HealingAura.Active");
						}else{
							return false;
						}
					}else if(effectType.equals(EffectType.SWIFT_FEET)) {
						if(cfg.isSet("Information.Effect.SwiftFeet.Active")) {
							return cfg.getBoolean("Information.Effect.SwiftFeet.Active");
						}else{
							return false;
						}
					}else if(effectType.equals(EffectType.LIFTOFF)) {
						if(cfg.isSet("Information.Effect.Liftoff.Active")) {
							return cfg.getBoolean("Information.Effect.Liftoff.Active");
						}else{
							return false;
						}
					}else if(effectType.equals(EffectType.MINERS_ADRENALINE)) {
						if(cfg.isSet("Information.Effect.MinersAdrenaline.Active")) {
							return cfg.getBoolean("Information.Effect.MinersAdrenaline.Active");
						}else{
							return false;
						}
					}else if(effectType.equals(EffectType.HEMORRHAGE)) {
						if(cfg.isSet("Information.Effect.Hemorrhage.Active")) {
							return cfg.getBoolean("Information.Effect.Hemorrhage.Active");
						}else{
							return false;
						}
					}
				}
				return false;
			}
			return false;
		}
		return false;
	}
	
	public void setEffectStatus(String tribe, EffectType type, boolean active) {
		File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
		FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
		if(type != null) {
			if(type == EffectType.LETHALITY) {
				cfg.set("Information.Effect.Lethality.Active", active);
			}else if(type == EffectType.PRECISION) {
				cfg.set("Information.Effect.Precision.Active", active);
			}else if(type == EffectType.HEALING_AURA) {
				cfg.set("Information.Effect.HealingAura.Active", active);
			}else if(type == EffectType.SWIFT_FEET) {
				cfg.set("Information.Effect.SwiftFeet.Active", active);
			}else if(type == EffectType.LIFTOFF) {
				cfg.set("Information.Effect.Liftoff.Active", active);
			}else if(type == EffectType.MINERS_ADRENALINE) {
				cfg.set("Information.Effect.MinersAdrenaline.Active", active);
			}else if(type == EffectType.HEMORRHAGE) {
				cfg.set("Information.Effect.Hemorrhage.Active", active);
			}
			try {
				cfg.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<EffectType> activeEffects(String tribe) {
		List<EffectType> effects = new ArrayList<EffectType>();
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				for(EffectType eff : EffectType.values()) {
					if(isEffectActive(tribe, eff)) {
						effects.add(eff);
					}
				}
				return effects;
			}
		}
		return effects;
	}
	
	public List<String> getEffectsUsers(String tribe) {
		List<String> users = new ArrayList<String>();
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
				List<String> u = cfg.getStringList("Information.EffectUsers");
				users.addAll(u);
				if(users.contains(" ")) users.remove(" ");
				return users;
			}
			return users;
		}
		return users;
	}
	
	public boolean isUsingEffects(String tribe, String uuid) {
		return getEffectsUsers(tribe).contains(uuid);
	}
	
	public void applyEffects(String tribe, String toUUID) {
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				if(!isUsingEffects(tribe, toUUID)) {
					List<String> users = getEffectsUsers(tribe);
					users.add(toUUID);
					File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
					FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
					cfg.set("Information.EffectUsers", users);
					try {
						cfg.save(f);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void removeEffects(String tribe, String toUUID) {
		if(tribe != null) {
			tribe = tribe.toUpperCase();
			if(tribeExists(tribe)) {
				if(isUsingEffects(tribe, toUUID)) {
					List<String> users = getEffectsUsers(tribe);
					users.remove(toUUID);
					File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
					FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
					cfg.set("Information.EffectUsers", users);
					try {
						cfg.save(f);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public int getCrystalLives(String tribe) {
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
			String livesString = cfg.getString("Information.Lives");
			String[] parts = livesString.split("/");
			if(core.getNumericAPI().isNumeric(parts[0])) {
				return Integer.valueOf(parts[0]);
			}
		}
		return 0;
	}
	
	public void setCrystalLives(String tribe, int lives) {
		if(tribeExists(tribe)) {
			tribe = tribe.toUpperCase();
			File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
			cfg.set("Information.Lives", lives + "/" + getMaxCrystalLives(tribe));
			try {
				cfg.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getMaxCrystalLives(String tribe) {
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
			String livesString = cfg.getString("Information.Lives");
			String[] parts = livesString.split("/");
			if(core.getNumericAPI().isNumeric(parts[1])) {
				return Integer.valueOf(parts[1]);
			}else return 0;
		}
		return 0;
	}
	
	public void updateCrystalHealth(Location loc, String playerTribe) {
		World w = loc.getWorld();
		for(Entity e : w.getEntities()) {
			if(e instanceof ArmorStand && e.getLocation().equals(loc)) {
				e.remove();
			}
		}
		ArmorStand as1 = (ArmorStand) w.spawnEntity(loc, EntityType.ARMOR_STAND);
		as1.setGravity(false);
		as1.setSmall(true);
		as1.setVisible(false);
		as1.setCustomName("§a" + getCrystalLives(playerTribe) + "§f/§a" + getMaxCrystalLives(playerTribe) + " §flives left!");
		as1.setCustomNameVisible(true);
		as1.setRemoveWhenFarAway(false);
	}
	
	public boolean isTribeProtected(String tribe) {
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
			return cfg.getBoolean("Information.TribeProtected");
		}
		return false;
	}
	
	public void setTribeProtection(String tribe, boolean protection) {
		if(tribeExists(tribe)) {
			File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
			cfg.set("Information.TribeProtected", protection);
			try {
				cfg.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isCrystalCoolingDown(String tribe) {
		if(tribeExists(tribe)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
			return cfg.getBoolean("Information.CrystalCoolingDown");
		}
		return false;
	}
	
	public void setCrystalCoolingDown(String tribe, boolean cd) {
		if(tribeExists(tribe)) {
			File f = core.getFileManager().getFile(tribe + ".yml", "plugins//ENFactions//Tribes//");
			FileConfiguration cfg = core.getFileManager().getConfiguration(tribe + ".yml", "plugins//ENFactions//Tribes//");
			cfg.set("Information.CrystalCoolingDown", cd);
			try {
				cfg.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
