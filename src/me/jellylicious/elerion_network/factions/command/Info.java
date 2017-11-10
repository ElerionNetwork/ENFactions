package me.jellylicious.elerion_network.factions.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Info implements CommandExecutor {

	Core core;
	public Info(Core core) {
		this.core = core;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 0) {
				String uuid = p.getUniqueId().toString();
				String tt = core.getTribesAPI().getPlayerTribe(uuid);
				//Mining
				int miningLevel = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningLevel", "UUID");
				int miningXP = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID");
				int miningXPRequired = (int) core.getMMOAPI().xpRequired(miningLevel);
				float mp = core.getMMOAPI().customPercentage(uuid, miningLevel, miningXP);
				//Fishing
				int fl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "FishingLevel", "UUID");
				int fxp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "FishingXP", "UUID");
				int fxpr = (int) core.getMMOAPI().xpRequired(fl);
				float fp = core.getMMOAPI().customPercentage(uuid, fl, fxp);
				//Woodcutting
				int wcl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "WoodcuttingLevel", "UUID");
				int wcxp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "WoodcuttingXP", "UUID");
				int wcxpr = (int) core.getMMOAPI().xpRequired(wcl);
				float wcp = core.getMMOAPI().customPercentage(uuid, wcl, wcxp);
				//Swords
				int sl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "SwordsLevel", "UUID");
				int sxp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "SwordsXP", "UUID");
				int sxpr = (int) core.getMMOAPI().xpRequired(sl);
				float sp = core.getMMOAPI().customPercentage(uuid, sl, sxp);
				//Agility
				int al = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "AgilityLevel", "UUID");
				int axp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "AgilityXP", "UUID");
				int axpr = (int) core.getMMOAPI().xpRequired(al);
				float ap = core.getMMOAPI().customPercentage(uuid, al, axp);
				p.sendMessage(core.getPrefix() + "§aBasic information about you:");
				p.sendMessage("  §7- §aYour username: §6" + p.getName());
				if(tt != null) {
					p.sendMessage("  §7- §aTribe: §6" + tt);
				}else{
					p.sendMessage("  §7- §aTribe: §cDoes not belong to any.");
				}
				p.sendMessage("  §7- §aTribe MMO Level: §6TODO");
				p.sendMessage("  §7- §aKDR: §6" + core.getPlayerAccount().getKDR(uuid) + " §7(§aK: §6" + core.getPlayerAccount().get(uuid, "enPlayerData", "Kills", "UUID") + "§7; §aD: §6" + core.getPlayerAccount().get(uuid, "enPlayerData", "Deaths", "UUID") + "§7)");
				p.sendMessage("  §7- §aTotal MMO Level: §6" + (miningLevel + fl + wcl + sl + al));
				p.sendMessage("  §7- §aMiner: §6lvl " + miningLevel + " §7(§a" + (miningXPRequired - miningXP) + " XP left§7) - " + core.getProgress().getProgress(mp) + " §7(§a" + mp + "§7%)");
				p.sendMessage("  §7- §aFisherman: §6lvl " + fl + " §7(§a" + (fxpr - fxp) + " XP left§7) - " + core.getProgress().getProgress(fp) + " §7(§a" + fp + "§7%)");
				p.sendMessage("  §7- §aLumberjack: §6lvl " + wcl + " §7(§a" + (wcxpr - wcxp) + " XP left§7) - " + core.getProgress().getProgress(wcp) + " §7(§a" + wcp + "§7%)");
				p.sendMessage("  §7- §aSwordsman: §6lvl " + sl + " §7(§a" + (sxpr - sxp) + " XP left§7) - " + core.getProgress().getProgress(sp) + " §7(§a" + sp + "§7%)");
				p.sendMessage("  §7- §aAgility: §6lvl " + al + " §7(§a" + (axpr - axp) + " XP left§7) - " + core.getProgress().getProgress(ap) + " §7(§a" + ap + "§7%)");
			}else if(args.length == 1) {
				Player t = Bukkit.getPlayer(args[0]);
				String uuid = null;
				if(t != null) {
					uuid = t.getUniqueId().toString();
				}else{
					OfflinePlayer t1 = Bukkit.getOfflinePlayer(args[0]);
					uuid = (String) core.getPlayerAccount().get(t1.getName(), "enPlayerData", "UUID", "InGameName");
				}
				if(uuid != null && core.getPlayerAccount().existsPlayer(uuid)) {
					String ign = (String) core.getPlayerAccount().get(uuid, "enPlayerData", "InGameName", "UUID");
					String tt = core.getTribesAPI().getPlayerTribe(uuid);
					//Mining
					int miningLevel = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningLevel", "UUID");
					int miningXP = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID");
					int miningXPRequired = (int) core.getMMOAPI().xpRequired(miningLevel);
					float mp = core.getMMOAPI().customPercentage(uuid, miningLevel, miningXP);
					//Fishing
					int fl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "FishingLevel", "UUID");
					int fxp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "FishingXP", "UUID");
					int fxpr = (int) core.getMMOAPI().xpRequired(fl);
					float fp = core.getMMOAPI().customPercentage(uuid, fl, fxp);
					//Woodcutting
					int wcl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "WoodcuttingLevel", "UUID");
					int wcxp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "WoodcuttingXP", "UUID");
					int wcxpr = (int) core.getMMOAPI().xpRequired(wcl);
					float wcp = core.getMMOAPI().customPercentage(uuid, wcl, wcxp);
					//Swords
					int sl = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "SwordsLevel", "UUID");
					int sxp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "SwordsXP", "UUID");
					int sxpr = (int) core.getMMOAPI().xpRequired(sl);
					float sp = core.getMMOAPI().customPercentage(uuid, sl, sxp);
					//Agility
					int al = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "AgilityLevel", "UUID");
					int axp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "AgilityXP", "UUID");
					int axpr = (int) core.getMMOAPI().xpRequired(al);
					float ap = core.getMMOAPI().customPercentage(uuid, al, axp);
					p.sendMessage(core.getPrefix() + "§aBasic information about §7" + ign + "§a:");
					if(core.getPlayerAccount().isOnline(uuid)) {
						p.sendMessage("  §7- §aUsername: §a" + ign);
					}else{
						p.sendMessage("  §7- §aUsername: §c" + ign);
					}
					if(tt != null) {
						p.sendMessage("  §7- §aTribe: §6" + tt);
					}else{
						p.sendMessage("  §7- §aTribe: §cDoes not belong to any.");
					}
					p.sendMessage("  §7- §aTribe MMO Level: §6TODO");
					p.sendMessage("  §7- §aKDR: §6" + core.getPlayerAccount().getKDR(uuid) + " §7(§aK: §6" + core.getPlayerAccount().get(uuid, "enPlayerData", "Kills", "UUID") + "§7; §aD: §6" + core.getPlayerAccount().get(uuid, "enPlayerData", "Deaths", "UUID") + "§7)");
					p.sendMessage("  §7- §aTotal MMO Level: §6" + (miningLevel + fl + wcl + sl + al));
					p.sendMessage("  §7- §aMiner: §6lvl " + miningLevel + " §7(§a" + (miningXPRequired - miningXP) + " XP left§7) - " + core.getProgress().getProgress(mp) + " §7(§a" + mp + "§7%)");
					p.sendMessage("  §7- §aFisherman: §6lvl " + fl + " §7(§a" + (fxpr - fxp) + " XP left§7) - " + core.getProgress().getProgress(fp) + " §7(§a" + fp + "§7%)");
					p.sendMessage("  §7- §aLumberjack: §6lvl " + wcl + " §7(§a" + (wcxpr - wcxp) + " XP left§7) - " + core.getProgress().getProgress(wcp) + " §7(§a" + wcp + "§7%)");
					p.sendMessage("  §7- §aSwordsman: §6lvl " + sl + " §7(§a" + (sxpr - sxp) + " XP left§7) - " + core.getProgress().getProgress(sp) + " §7(§a" + sp + "§7%)");
					p.sendMessage("  §7- §aAgility: §6lvl " + al + " §7(§a" + (axpr - axp) + " XP left§7) - " + core.getProgress().getProgress(ap) + " §7(§a" + ap + "§7%)");
				}else{
					p.sendMessage(core.getPrefix() + "§cThis player does not exist.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /info <target[optional]>!");
			}
		}
		return true;
	}

}
