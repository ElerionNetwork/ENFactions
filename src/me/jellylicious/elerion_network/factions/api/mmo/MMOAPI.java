package me.jellylicious.elerion_network.factions.api.mmo;

import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class MMOAPI {

	Core core;
	public MMOAPI(Core core) {
		this.core = core;
	}
	
	public double xpRequired(int level) {
		FileConfiguration cfg = core.getFileManager().getConfiguration("settings.yml", "plugins//ENFactions//");
		if(cfg.isSet("MMOSettings.SkillSettings.XPRequired." + level)) {
			return cfg.getDouble("MMOSettings.SkillSettings.XPRequired." + level);
		}
		return 0.0;
	}
	
	public int getReward(String skill, String item) {
		FileConfiguration cfg = core.getFileManager().getConfiguration("settings.yml", "plugins//ENFactions//");
		if(cfg.isSet("MMOSettings.SkillSettings." + skill + ".Rewards." + item)) {
			return cfg.getInt("MMOSettings.SkillSettings." + skill + ".Rewards." + item);
		}
		return 0;
	}
	
	public void updateLevel(String skill, Player p) {
		String uuid = p.getUniqueId().toString();
		int l = (int) core.getPlayerAccount().get(uuid, "enPlayerData", skill + "Level", "UUID");
		int xp = (int) core.getPlayerAccount().get(uuid, "enPlayerData", skill + "XP", "UUID");
		int xpr = (int) xpRequired(l);
		if(xp > xpr) {
			if(l != 500) {
				core.getPlayerAccount().set(uuid, "enPlayerData", skill + "Level", l+1);
				p.sendMessage(core.getPrefix() + "§aYou have just leveled up your §7" + skill + " §askill! Your new level equals §7" + (l+1) + "§a!");
			}
		}else{
			int pl = l-1; //Previous Level
			int plxp = (int) xpRequired(pl); //XP Required for Previous Level
			if(pl != 0) {
				if(xp < plxp) {
					core.getPlayerAccount().set(uuid, "enPlayerData", skill + "Level", pl);
					p.sendMessage(core.getPrefix() + "§aYou have just leveled down your §7" + skill + " §askill! Your new level equals §7" + (l+1) + "§a!");
				}
			}
		}
	}
	
	public float customPercentage(String uuid, int currentLevel, int currentXP) {
		int maxXPCurrentLevel = (int) xpRequired(currentLevel);
		if(currentLevel > 1 && currentLevel < 501) {
			int previousLevel = currentLevel - 1;
			int maxXPPreviousLevel = (int) xpRequired(previousLevel);
			return Math.round(((currentXP - maxXPPreviousLevel) * 100.0f) / maxXPCurrentLevel);
		}else if(currentLevel == 1) {
			return Math.round((currentXP * 100.0f) / maxXPCurrentLevel);
		}else if(currentLevel > 500) {
			return 100.0f;
		}
		return 100.0f;
	}
	
	public boolean hasAbilityUnlocked(String uuid, String skill, String ability) {
		if(core.getPlayerAccount().existsPlayer(uuid)) {
			FileConfiguration cfg = core.getFileManager().getConfiguration("settings.yml", "plugins//ENFactions//");
			if(cfg.isSet("MMOSettings.SkillSettings." + skill + ".Abilities." + ability + ".UnlocksOnLevel")) {
				int requiredLevel = cfg.getInt("MMOSettings.SkillSettings." + skill + ".Abilities." + ability + ".UnlocksOnLevel");
				int playerLevel = (int) core.getPlayerAccount().get(uuid, "enPlayerData", skill + "Level", "UUID");
				if(playerLevel >= requiredLevel) return true;
				else return false;
			}
			return false;
		}
		return false;
	}
	
	public int activeFor(String skill, String ability) {
		FileConfiguration cfg = core.getFileManager().getConfiguration("settings.yml", "plugins//ENFactions//");
		if(cfg.isSet("MMOSettings.SkillSettings." + skill + ".Abilities." + ability + ".ActiveForSeconds")) {
			return cfg.getInt("MMOSettings.SkillSettings." + skill + ".Abilities." + ability + ".ActiveForSeconds");
		}
		return 0;
	}
	
	public int cooldown(String skill, String ability) {
		FileConfiguration cfg = core.getFileManager().getConfiguration("settings.yml", "plugins//ENFactions//");
		if(cfg.isSet("MMOSettings.SkillSettings." + skill + ".Abilities." + ability + ".CooldownInSeconds")) {
			return cfg.getInt("MMOSettings.SkillSettings." + skill + ".Abilities." + ability + ".CooldownInSeconds");
		}
		return 0;
	}
	
	public boolean randomBooleanByChance(int chanceForTrue, int chanceForFalse) {
		Random r = new Random();
		final int ri = r.nextInt(chanceForTrue+chanceForFalse);
		if(ri <= chanceForTrue) return true;
		else if(ri > chanceForTrue) return false;
		return false;
	}
	
	public int calculateBlackLungsPercentage(int currentLevel) {
		int a = 0;
		if(currentLevel <= 25) a = 5;
		else if(currentLevel > 25 && currentLevel <= 50) a = 4;
		else if(currentLevel > 50 && currentLevel <= 75) a = 3;
		else if(currentLevel > 75 && currentLevel <= 100) a = 2;
		else if(currentLevel > 100 && currentLevel <= 125) a = 1;
		else if(currentLevel > 125) a = 0;
		return a;
	}
	
	public int getLessDamageForAgility() {
		FileConfiguration cfg = core.getFileManager().getConfiguration("settings.yml", "plugins//ENFactions//");
		if(cfg.isSet("MMOSettings.SkillSettings.Agility.Abilities.ReducedFallDamage.OffDamageIfLucky")) {
			return cfg.getInt("MMOSettings.SkillSettings.Agility.Abilities.ReducedFallDamage.OffDamageIfLucky");
		}
		return 0;
	}
	
	public int getMinusHPPerSecond() {
		FileConfiguration cfg = core.getFileManager().getConfiguration("settings.yml", "plugins//ENFactions//");
		if(cfg.isSet("MMOSettings.SkillSettings.Swords.Abilities.Bleed.MinusHPPerSecond")) {
			return cfg.getInt("MMOSettings.SkillSettings.Swords.Abilities.Bleed.MinusHPPerSecond");
		}
		return 0;
	}
	
	public String randomFishingRewardByChance() {
		Random r = new Random();
		final int ri = r.nextInt(112);
		if(ri <= 5) return "Salmon";
		else if(ri > 5 && ri <= 20) return "Tuna";
		else if(ri == 21) return "Pirate Chest";
		else if(ri > 21 && ri <= 27) return "Boot";
		else if(ri > 27 && ri <= 29) return "Pearl";
		else if(ri > 29 && ri <= 31) return "Great White Shark";
		else if(ri > 31 && ri <= 39) return "Anchovy";
		else if(ri > 39 && ri <= 42) return "Pufferfish";
		else if(ri > 42 && ri <= 50) return "Mackerel";
		else if(ri > 50 && ri <= 60) return "Clownfish";
		else if(ri > 60 && ri <= 65) return "Crab";
		else if(ri > 65 && ri <= 73) return "Jellyfish";
		else if(ri > 73 && ri <= 76) return "Squid";
		else if( ri == 77) return "Whale";
		else if(ri > 77 && ri <= 87) return "Flatfish";
		else if(ri > 87 && ri <= 90) return "Golden Helmet";
		else if(ri > 90 && ri <= 93) return "Golden Chestplate";
		else if(ri > 93 && ri <= 96) return "Golden Leggings";
		else if(ri > 96 && ri <= 99) return "Golden Boots";
		else if(ri > 99 && ri <= 101) return "Iron Helmet";
		else if(ri > 101 && ri <= 103) return "Iron Chestplate";
		else if(ri > 103 && ri <= 105) return "Iron Leggings";
		else if(ri > 105 && ri <= 107) return "Iron Boots";
		else if(ri == 108) return "Diamond Helmet";
		else if(ri == 109) return "Diamond Chestplate";
		else if(ri == 110) return "Diamond Leggings";
		else if(ri == 111) return "Diamond Boots";
		else if(ri == 112) return "Gas Field";
		return null;
	}

}
