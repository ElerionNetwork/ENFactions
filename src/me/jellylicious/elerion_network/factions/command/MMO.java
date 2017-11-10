package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class MMO implements CommandExecutor {

	Core core;
	public MMO(Core core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			String uuid = p.getUniqueId().toString();
			if(args.length == 0 || args.length == 3 || args.length > 4) {
				p.performCommand("mmo help");
			}else{
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("help")) {
						sendHelpMessage(p, 1);
					}else{
						p.performCommand("mmo help");
					}
				}else if(args.length == 2) {
					if(args[0].equalsIgnoreCase("skill")) {
						if(args[1].equalsIgnoreCase("miner")) {
							int miningLevel = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningLevel", "UUID");
							int miningXP = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID");
							int miningXPRequired = (int) core.getMMOAPI().xpRequired(miningLevel);
							p.sendMessage(core.getPrefix() + "§aSome information about mining skill:");
							p.sendMessage("  §7- §aYour level: §6" + miningLevel);
							p.sendMessage("  §7- §aYour XP: §6" + miningXP + "§b/§6" + miningXPRequired + " §7(§a" + (miningXPRequired - miningXP) + " XP needed§7)");
							p.sendMessage("  §7- §aAbilities:");
							p.sendMessage("   §7- §aDouble drops");
							p.sendMessage("   §7- §aBlack lungs");
							p.sendMessage("   §7- §aSpeed mining lvl 1");
							p.sendMessage("   §7- §aSpeed mining lvl 2");
							p.sendMessage("   §7- §aSpeed mining lvl 3");
							p.sendMessage("   §7- §aSpeed mining lvl 4");
							p.sendMessage("   §7- §aMiner's vision");
							p.sendMessage("   §7- §aExplosive mining");
							p.sendMessage("   §7- §aVein miner");
						}else if(args[1].equalsIgnoreCase("fisherman")) {
							int fl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "FishingLevel", "UUID");
							int fxp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "FishingXP", "UUID");
							int fxpr = (int) core.getMMOAPI().xpRequired(fl);
							p.sendMessage(core.getPrefix() + "§aSome information about fishing skill:");
							p.sendMessage("  §7- §aYour level: §6" + fl);
							p.sendMessage("  §7- §aYour XP: §6" + fxp + "§b/§6" + fxpr + " §7(§a" + (fxpr - fxp) + " XP needed§7)");
							p.sendMessage("  §7- §aAbilities:");
							p.sendMessage("   §7- §aDrag");
						}else if(args[1].equalsIgnoreCase("lumberjack")) {
							int fl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "WoodcuttingLevel", "UUID");
							int fxp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "WoodcuttingXP", "UUID");
							int fxpr = (int) core.getMMOAPI().xpRequired(fl);
							p.sendMessage(core.getPrefix() + "§aSome information about woodcutting skill:");
							p.sendMessage("  §7- §aYour level: §6" + fl);
							p.sendMessage("  §7- §aYour XP: §6" + fxp + "§b/§6" + fxpr + " §7(§a" + (fxpr - fxp) + " XP needed§7)");
							p.sendMessage("  §7- §aAbilities:");
							p.sendMessage("   §7- §aDouble drops");
							p.sendMessage("   §7- §aTree feller");
							p.sendMessage("   §7- §aFaster chopping");
						}else if(args[1].equalsIgnoreCase("swordsman")) {
							int fl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "SwordsLevel", "UUID");
							int fxp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "SwordsXP", "UUID");
							int fxpr = (int) core.getMMOAPI().xpRequired(fl);
							p.sendMessage(core.getPrefix() + "§aSome information about swords skill:");
							p.sendMessage("  §7- §aYour level: §6" + fl);
							p.sendMessage("  §7- §aYour XP: §6" + fxp + "§b/§6" + fxpr + " §7(§a" + (fxpr - fxp) + " XP needed§7)");
							p.sendMessage("  §7- §aAbilities:");
							p.sendMessage("   §7- §aBleed");
						}else if(args[1].equalsIgnoreCase("agility")) {
							int fl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "AgilityLevel", "UUID");
							int fxp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "AgilityXP", "UUID");
							int fxpr = (int) core.getMMOAPI().xpRequired(fl);
							p.sendMessage(core.getPrefix() + "§aSome information about agility skill:");
							p.sendMessage("  §7- §aYour level: §6" + fl);
							p.sendMessage("  §7- §aYour XP: §6" + fxp + "§b/§6" + fxpr + " §7(§a" + (fxpr - fxp) + " XP needed§7)");
							p.sendMessage("  §7- §aAbilities:");
							p.sendMessage("   §7- §aRoll");
						}else{
							p.sendMessage(core.getPrefix() + "§cUnknown skill. You can choose from miner, fisherman, lumberjack, swordsman and agility.");
						}
					}else{
						p.performCommand("mmo help");
					}
				}else if(args.length == 4) {
					if(args[0].equalsIgnoreCase("levels")) {
						if(args[1].equalsIgnoreCase("add")) {
							int ltr = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "LevelsToRedeem", "UUID");
							if(ltr != 0) {
								if(args[2].equalsIgnoreCase("miner")) {
									if(core.getNumericAPI().isNumeric(args[3])) {
										int oltr = Integer.parseInt(args[3]);
										if(!(oltr < 0)) {
											if(!(oltr > ltr)) {
												int ml = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningLevel", "UUID");
												int maxXPprevious = (int) core.getMMOAPI().xpRequired(ml+oltr-1) + 1;
												core.getPlayerAccount().set(uuid, "enPlayerData", "MiningLevel", ml+oltr);
												core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP", maxXPprevious);
												core.getPlayerAccount().set(uuid, "enPlayerData", "LevelsToRedeem", ltr-oltr);
												p.sendMessage(core.getPrefix() + "§aYou successfully leveled up your §7Mining §askill! Your new level equals §7" + (ml+oltr) + "§a!");
											}else{
												p.sendMessage(core.getPrefix() + "§cYou do not have enough levels to do that. You currently own §7" + ltr + " §cunassigned levels.");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cLevel amount should not be negative.");
										}
									}else{
										p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
									}
								}else if(args[2].equalsIgnoreCase("fisherman")) {
									if(core.getNumericAPI().isNumeric(args[3])) {
										int oltr = Integer.parseInt(args[3]);
										if(!(oltr < 0)) {
											if(!(oltr > ltr)) {
												int fl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "FishingLevel", "UUID");
												int maxXPprevious = (int) core.getMMOAPI().xpRequired(fl+oltr-1) + 1;
												core.getPlayerAccount().set(uuid, "enPlayerData", "FishingLevel", fl+oltr);
												core.getPlayerAccount().set(uuid, "enPlayerData", "FishingXP", maxXPprevious);
												core.getPlayerAccount().set(uuid, "enPlayerData", "LevelsToRedeem", ltr-oltr);
												p.sendMessage(core.getPrefix() + "§aYou successfully leveled up your §7Fishing §askill! Your new level equals §7" + (fl+oltr) + "§a!");
											}else{
												p.sendMessage(core.getPrefix() + "§cYou do not have enough levels to do that. You currently own §7" + ltr + " §cunassigned levels.");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cLevel amount should not be negative.");
										}
									}else{
										p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
									}
								}else if(args[2].equalsIgnoreCase("lumberjack")) {
									if(core.getNumericAPI().isNumeric(args[3])) {
										int oltr = Integer.parseInt(args[3]);
										if(!(oltr < 0)) {
											if(!(oltr > ltr)) {
												int wcl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "WoodcuttingLevel", "UUID");
												int maxXPprevious = (int) core.getMMOAPI().xpRequired(wcl+oltr-1) + 1;
												core.getPlayerAccount().set(uuid, "enPlayerData", "WoodcuttingLevel", wcl+oltr);
												core.getPlayerAccount().set(uuid, "enPlayerData", "WoodcuttingXP", maxXPprevious);
												core.getPlayerAccount().set(uuid, "enPlayerData", "LevelsToRedeem", ltr-oltr);
												p.sendMessage(core.getPrefix() + "§aYou successfully leveled up your §7Woodcutting §askill! Your new level equals §7" + (wcl+oltr) + "§a!");
											}else{
												p.sendMessage(core.getPrefix() + "§cYou do not have enough levels to do that. You currently own §7" + ltr + " §cunassigned levels.");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cLevel amount should not be negative.");
										}
									}else{
										p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
									}
								}else if(args[2].equalsIgnoreCase("fighter")) {
									if(core.getNumericAPI().isNumeric(args[3])) {
										int oltr = Integer.parseInt(args[3]);
										if(!(oltr < 0)) {
											if(!(oltr > ltr)) {
												int fl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "SwordsLevel", "UUID");
												int maxXPprevious = (int) core.getMMOAPI().xpRequired(fl+oltr-1) + 1;
												core.getPlayerAccount().set(uuid, "enPlayerData", "SwordsLevel", fl+oltr);
												core.getPlayerAccount().set(uuid, "enPlayerData", "SwordsXP", maxXPprevious);
												core.getPlayerAccount().set(uuid, "enPlayerData", "LevelsToRedeem", ltr-oltr);
												p.sendMessage(core.getPrefix() + "§aYou successfully leveled up your §7Fighting §askill! Your new level equals §7" + (fl+oltr) + "§a!");
											}else{
												p.sendMessage(core.getPrefix() + "§cYou do not have enough levels to do that. You currently own §7" + ltr + " §cunassigned levels.");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cLevel amount should not be negative.");
										}
									}else{
										p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
									}
								}else if(args[2].equalsIgnoreCase("agility")) {
									if(core.getNumericAPI().isNumeric(args[3])) {
										int oltr = Integer.parseInt(args[3]);
										if(!(oltr < 0)) {
											if(!(oltr > ltr)) {
												int al = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "AgilityLevel", "UUID");
												int maxXPprevious = (int) core.getMMOAPI().xpRequired(al+oltr-1) + 1;
												core.getPlayerAccount().set(uuid, "enPlayerData", "AgilityLevel", al+oltr);
												core.getPlayerAccount().set(uuid, "enPlayerData", "AgilityXP", maxXPprevious);
												core.getPlayerAccount().set(uuid, "enPlayerData", "LevelsToRedeem", ltr-oltr);
												p.sendMessage(core.getPrefix() + "§aYou successfully leveled up your §7Agility §askill! Your new level equals §7" + (al+oltr) + "§a!");
											}else{
												p.sendMessage(core.getPrefix() + "§cYou do not have enough levels to do that. You currently own §7" + ltr + " §cunassigned levels.");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cLevel amount should not be negative.");
										}
									}else{
										p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
									}
								}else{
									p.sendMessage(core.getPrefix() + "§cUnknown skill. You can choose from miner, fisherman, lumberjack, fighter and agility.");
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cYou do not have enough levels to do that. You currently own §7" + ltr + " §cunassigned levels.");
							}
						}else if(args[1].equalsIgnoreCase("give")) {
							if(core.getGroups().hasPermission(p, "elerion.command.mmo.givelevels")) {
								Player t = Bukkit.getPlayer(args[2]);
								if(t != null) {
									if(core.getNumericAPI().isNumeric(args[3])) {
										String tuuid = t.getUniqueId().toString();
										int ltr = (int) core.getPlayerAccount().get(tuuid, "enPlayerData", "LevelsToRedeem", "UUID");
										core.getPlayerAccount().set(tuuid, "enPlayerData", "LevelsToRedeem", ltr+Integer.parseInt(args[3]));
										p.sendMessage(core.getPrefix() + "§aYou successfully gave §7" + Integer.parseInt(args[3]) + " §alevels to §7" + t.getName() + "§a!");
										t.sendMessage(core.getPrefix() + "§aYou have just received §7" + Integer.parseInt(args[3]) + " §alevels from §7" + p.getName() + "§a! To assign them to a specific skill, please use §7/mmo levels add <skill> <amount>§a.");
									}else{
										p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
									}
								}else{
									p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cSorry, but you have no permission to execute that command.");
							}
						}else if(args[1].equalsIgnoreCase("take")) {
							if(core.getGroups().hasPermission(p, "elerion.command.mmo.takelevels")) {
								Player t = Bukkit.getPlayer(args[2]);
								if(t != null) {
									String tuuid = t.getUniqueId().toString();
									String skill = args[3];
									if(skill.equalsIgnoreCase("miner")) {
										if(core.getNumericAPI().isNumeric(args[3])) {
											int ol = Integer.parseInt(args[3]);
											if(!(ol < 0)) {
												int l = (int) core.getPlayerAccount().get(tuuid, "enPlayerData", "MiningLevel", "UUID");
												if(!(ol > l)) {
													int xpReq = (int) core.getMMOAPI().xpRequired(l-ol);
													core.getPlayerAccount().set(uuid, "enPlayerData", "MiningLevel", l-ol);
													core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP", xpReq);
													p.sendMessage(core.getPrefix() + "§aYou just took " + ol + " levels from " + t.getName() + "'s mining skill.");
													t.sendMessage(core.getPrefix() + p.getName() + " §ajust took " + ol + " levels from your mining skill.");
												}else{
													p.sendMessage(core.getPrefix() + t.getName() + " §conly has " + l + " levels on his mining skill.");
												}
											}else{
												p.sendMessage(core.getPrefix() + "§cLevel amount should not be negative.");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
										}
									}else if(skill.equalsIgnoreCase("fisherman")) {
										if(core.getNumericAPI().isNumeric(args[3])) {
											int ol = Integer.parseInt(args[3]);
											if(!(ol < 0)) {
												int l = (int) core.getPlayerAccount().get(tuuid, "enPlayerData", "FishingLevel", "UUID");
												if(!(ol > l)) {
													int xpReq = (int) core.getMMOAPI().xpRequired(l-ol);
													core.getPlayerAccount().set(uuid, "enPlayerData", "FishingLevel", l-ol);
													core.getPlayerAccount().set(uuid, "enPlayerData", "FishingXP", xpReq);
													p.sendMessage(core.getPrefix() + "§aYou just took " + ol + " levels from " + t.getName() + "'s fishing skill.");
													t.sendMessage(core.getPrefix() + p.getName() + " §ajust took " + ol + " levels from your fishing skill.");
												}else{
													p.sendMessage(core.getPrefix() + t.getName() + " §conly has " + l + " levels on his fishing skill.");
												}
											}else{
												p.sendMessage(core.getPrefix() + "§cLevel amount should not be negative.");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
										}
									}else if(skill.equalsIgnoreCase("lumberjack")) {
										if(core.getNumericAPI().isNumeric(args[3])) {
											int ol = Integer.parseInt(args[3]);
											if(!(ol < 0)) {
												int l = (int) core.getPlayerAccount().get(tuuid, "enPlayerData", "WoodcuttingLevel", "UUID");
												if(!(ol > l)) {
													int xpReq = (int) core.getMMOAPI().xpRequired(l-ol);
													core.getPlayerAccount().set(uuid, "enPlayerData", "WoodcuttingLevel", l-ol);
													core.getPlayerAccount().set(uuid, "enPlayerData", "WoodcuttingXP", xpReq);
													p.sendMessage(core.getPrefix() + "§aYou just took " + ol + " levels from " + t.getName() + "'s lumberjack skill.");
													t.sendMessage(core.getPrefix() + p.getName() + " §ajust took " + ol + " levels from your lumberjack skill.");
												}else{
													p.sendMessage(core.getPrefix() + t.getName() + " §conly has " + l + " levels on his lumberjack skill.");
												}
											}else{
												p.sendMessage(core.getPrefix() + "§cLevel amount should not be negative.");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
										}
									}else if(skill.equalsIgnoreCase("fighter")) {
										if(core.getNumericAPI().isNumeric(args[3])) {
											int ol = Integer.parseInt(args[3]);
											if(!(ol < 0)) {
												int l = (int) core.getPlayerAccount().get(tuuid, "enPlayerData", "SwordsLevel", "UUID");
												if(!(ol > l)) {
													int xpReq = (int) core.getMMOAPI().xpRequired(l-ol);
													core.getPlayerAccount().set(uuid, "enPlayerData", "SwordsLevel", l-ol);
													core.getPlayerAccount().set(uuid, "enPlayerData", "SwordsXP", xpReq);
													p.sendMessage(core.getPrefix() + "§aYou just took " + ol + " levels from " + t.getName() + "'s fighter skill.");
													t.sendMessage(core.getPrefix() + p.getName() + " §ajust took " + ol + " levels from your fighter skill.");
												}else{
													p.sendMessage(core.getPrefix() + t.getName() + " §conly has " + l + " levels on his fighter skill.");
												}
											}else{
												p.sendMessage(core.getPrefix() + "§cLevel amount should not be negative.");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
										}
									}else if(skill.equalsIgnoreCase("agility")) {
										if(core.getNumericAPI().isNumeric(args[3])) {
											int ol = Integer.parseInt(args[3]);
											if(!(ol < 0)) {
												int l = (int) core.getPlayerAccount().get(tuuid, "enPlayerData", "AgilityLevel", "UUID");
												if(!(ol > l)) {
													int xpReq = (int) core.getMMOAPI().xpRequired(l-ol);
													core.getPlayerAccount().set(uuid, "enPlayerData", "AgilityLevel", l-ol);
													core.getPlayerAccount().set(uuid, "enPlayerData", "AgilityXP", xpReq);
													p.sendMessage(core.getPrefix() + "§aYou just took " + ol + " levels from " + t.getName() + "'s agility skill.");
													t.sendMessage(core.getPrefix() + p.getName() + " §ajust took " + ol + " levels from your agility skill.");
												}else{
													p.sendMessage(core.getPrefix() + t.getName() + " §conly has " + l + " levels on his agility skill.");
												}
											}else{
												p.sendMessage(core.getPrefix() + "§cLevel amount should not be negative.");
											}
										}else{
											p.sendMessage(core.getPrefix() + "§cAmount can only be numeric!");
										}
									}else{
										p.sendMessage(core.getPrefix() + "§cUnknown skill. You can choose from miner, fisherman, lumberjack, fighter and agility.");
									}
								}else{
									p.sendMessage(core.getPrefix() + "§cThis player is not online at the moment.");
								}
							}
						}else{
							p.performCommand("mmo help");
						}
					}else{
						p.performCommand("mmo help");
					}
				}else{
					p.performCommand("mmo help");
				}
			}
		}
		return true;
	}
	
	public void sendHelpMessage(Player p, int page) {
		p.sendMessage("§7[§a§lELERION§7] §9Help §8| §6Page§8: §e" + page + "§7/§e1");
		if (page == 1) {
			p.sendMessage("§e/mmo skill <skill> §8- §7Shows more detailed info about a specific skill. You can choose from miner, fisherman, lumberjack, fighter and agility.");
			p.sendMessage("§e/mmo levels add <skill> <amount> §8- §7Use your unused levels to level up a specific skill.");
			p.sendMessage("§e/mmo levels give <player> <amount> §8- §7Give your target a specific amount of levels.");
		}
	}

}
