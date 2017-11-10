package me.jellylicious.elerion_network.factions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.jellylicious.elerion_network.factions.api.chatcolor.ENChatColor;
import me.jellylicious.elerion_network.factions.api.coins.Coins;
import me.jellylicious.elerion_network.factions.api.file.FileManager;
import me.jellylicious.elerion_network.factions.api.groups.Groups;
import me.jellylicious.elerion_network.factions.api.item.ItemManager;
import me.jellylicious.elerion_network.factions.api.location.LocationHelper;
import me.jellylicious.elerion_network.factions.api.mmo.MMOAPI;
import me.jellylicious.elerion_network.factions.api.modifiers.ActionBarHelper;
import me.jellylicious.elerion_network.factions.api.modifiers.Packets;
import me.jellylicious.elerion_network.factions.api.modifiers.TabHelper;
import me.jellylicious.elerion_network.factions.api.modifiers.TitleHelper;
import me.jellylicious.elerion_network.factions.api.mysql.MySQL;
import me.jellylicious.elerion_network.factions.api.number.NumericAPI;
import me.jellylicious.elerion_network.factions.api.player.PlayerAccount;
import me.jellylicious.elerion_network.factions.api.progress.Progress;
import me.jellylicious.elerion_network.factions.api.promotion.PromotionHelper;
import me.jellylicious.elerion_network.factions.api.punishment.Punisher;
import me.jellylicious.elerion_network.factions.api.security.email.EmailValidator;
import me.jellylicious.elerion_network.factions.api.security.password.PasswordProtection;
import me.jellylicious.elerion_network.factions.api.security.password.PasswordValidator;
import me.jellylicious.elerion_network.factions.api.settings.Settings;
import me.jellylicious.elerion_network.factions.api.tribes.TribesAPI;
import me.jellylicious.elerion_network.factions.api.votetokens.VoteTokens;
import me.jellylicious.elerion_network.factions.command.Appeal;
import me.jellylicious.elerion_network.factions.command.Ban;
import me.jellylicious.elerion_network.factions.command.ChatClear;
import me.jellylicious.elerion_network.factions.command.ChatLock;
import me.jellylicious.elerion_network.factions.command.ClearInventory;
import me.jellylicious.elerion_network.factions.command.Feed;
import me.jellylicious.elerion_network.factions.command.Fly;
import me.jellylicious.elerion_network.factions.command.Gamemode;
import me.jellylicious.elerion_network.factions.command.GetPos;
import me.jellylicious.elerion_network.factions.command.God;
import me.jellylicious.elerion_network.factions.command.Group;
import me.jellylicious.elerion_network.factions.command.Heal;
import me.jellylicious.elerion_network.factions.command.Home;
import me.jellylicious.elerion_network.factions.command.Info;
import me.jellylicious.elerion_network.factions.command.Item;
import me.jellylicious.elerion_network.factions.command.Kick;
import me.jellylicious.elerion_network.factions.command.KickAll;
import me.jellylicious.elerion_network.factions.command.List;
import me.jellylicious.elerion_network.factions.command.MMO;
import me.jellylicious.elerion_network.factions.command.Message;
import me.jellylicious.elerion_network.factions.command.Register;
import me.jellylicious.elerion_network.factions.command.Reply;
import me.jellylicious.elerion_network.factions.command.SetSpawn;
import me.jellylicious.elerion_network.factions.command.Shop;
import me.jellylicious.elerion_network.factions.command.Spawn;
import me.jellylicious.elerion_network.factions.command.StaffChat;
import me.jellylicious.elerion_network.factions.command.Teleport;
import me.jellylicious.elerion_network.factions.command.Tempban;
import me.jellylicious.elerion_network.factions.command.Tpa;
import me.jellylicious.elerion_network.factions.command.Tpaccept;
import me.jellylicious.elerion_network.factions.command.Tphere;
import me.jellylicious.elerion_network.factions.command.Tribes;
import me.jellylicious.elerion_network.factions.command.Vote;
import me.jellylicious.elerion_network.factions.command.Warn;
import me.jellylicious.elerion_network.factions.command.Warp;
import me.jellylicious.elerion_network.factions.listener.CrystalEvents;
import me.jellylicious.elerion_network.factions.listener.Interact;
import me.jellylicious.elerion_network.factions.listener.PlayerEvents;

public class Core extends JavaPlugin {

	/*
	 * 
	 * - /t joinRequests will send you all join requests sent by players.
	 * - You can accept a request by clicking "ACCEPT" or deny it by clicking "DENY".
	 * - Every button is attached to its own uuid.
	 * ------------ If player sent an invitation to the tribe and this tribe renames itself, then change the name under TribeRequests in playerdata file.
	 * - If player joins another tribe, delete all tribe join requests in both files.
	 * - You can not claim crystals of other claims!
	 * - Cancel renaming all ingots in anvil
	 */
	
	private ArrayList<Listener> listeners = new ArrayList<Listener>();
	private ArrayList<Player> gods = new ArrayList<Player>();
	private HashMap<Player, Player> tpa = new HashMap<Player, Player>();
	private HashMap<String, HashMap<String, ItemStack>> fsInvBeforeDeath = new HashMap<String, HashMap<String, ItemStack>>();
	private EmailValidator emailValidator;
	private PasswordValidator passwordValidator;
	private PasswordProtection passwordProtection;
	private FileManager fileManager;
	private MySQL mysql;
	private PlayerAccount playerAccount;
	private Groups groups;
	private Punisher punisher;
	private LocationHelper locationHelper;
	private NumericAPI numericAPI;
	private TribesAPI tribesAPI;
	private ItemManager itemManager;
	private MMOAPI mmoAPI;
	private Progress progress;
	private Packets packets;
	private TitleHelper titleHelper;
	private TabHelper tabHelper;
	private ActionBarHelper abHelper;
	private VoteTokens voteTokens;
	private Settings settings;
	private PromotionHelper promotionHelper;
	private ENChatColor chatColor;
	private Coins coins;
	
	private boolean chatLocked;
	
	@Override
	public void onEnable() {
		emailValidator = new EmailValidator();
		passwordValidator = new PasswordValidator();
		passwordProtection = new PasswordProtection();
		fileManager = new FileManager(this);
		playerAccount = new PlayerAccount(this);
		groups = new Groups(this);
		punisher = new Punisher(this);
		locationHelper = new LocationHelper(this);
		numericAPI = new NumericAPI(this);
		tribesAPI = new TribesAPI(this);
		itemManager = new ItemManager(this);
		mmoAPI = new MMOAPI(this);
		progress = new Progress(this);
		packets = new Packets();
		mysql = new MySQL(this);
		titleHelper = new TitleHelper(this);
		tabHelper = new TabHelper();
		abHelper = new ActionBarHelper();
		voteTokens = new VoteTokens(this);
		settings = new Settings(this);
		promotionHelper = new PromotionHelper(this);
		chatColor = new ENChatColor(this);
		coins = new Coins(this);
		mysql.createMySQLFile("plugins//ENFactions//");
		mysql.connect();
		mysql.update("CREATE TABLE IF NOT EXISTS enPlayerData(ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT, UUID VARCHAR(64), InGameName VARCHAR(16), Rank VARCHAR(10), JoinDate VARCHAR(20), LastSeen VARCHAR(20), Joins INT, Coins INT, Kills INT, Deaths INT, EmailAddress VARCHAR(30), PasswordHash VARCHAR(90), LastPunishment INT(10), MiningXP INT(6), MiningLevel INT(3), FishingXP INT(6), FishingLevel INT(3), WoodcuttingXP INT(6), WoodcuttingLevel INT(3), SwordsXP INT(6), SwordsLevel INT(3), AgilityXP INT(6), AgilityLevel INT(3), LevelsToRedeem INT(5), CombatLog VARCHAR(5), Votes INT(5), VoteTokens INT(5));");
		mysql.update("CREATE TABLE IF NOT EXISTS enPunishments(ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT, IssuedBy VARCHAR(64), Punished VARCHAR(64), PunishmentType VARCHAR(10), DateIssued VARCHAR(20), ExpirationDate VARCHAR(20), Reason VARCHAR(50), Active VARCHAR(5), Appealed VARCHAR(5), AppealedBy VARCHAR(64));");
		listeners.add(new CrystalEvents(this));
		listeners.add(new PlayerEvents(this));
		listeners.add(new Interact(this));
		registerCommands();
		registerListeners();
		getGroups().createGroupFile("MEMBER.yml", "plugins//ENFactions//Groups//");
		for(Player p : getServer().getOnlinePlayers()) {
			getPlayerAccount().setLastSeen(p.getUniqueId().toString(), "NOW");
		}
		if(!getFileManager().getFile("Claimed_Areas.yml", "plugins//ENFactions//Tribes").exists()) {
			getFileManager().createNewFile("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
			FileConfiguration cfg = getFileManager().getConfiguration("Claimed_Areas.yml", "plugins//ENFactions//Tribes//");
			ArrayList<String> claimed = new ArrayList<String>();
			cfg.set("ClaimedAreas", claimed);
			try {
				cfg.save(getFileManager().getFile("Claimed_Areas.yml", "plugins//ENFactions//Tribes"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!getFileManager().getFile("settings.yml", "plugins//ENFactions//").exists()) {
			getFileManager().createNewFile("settings.yml", "plugins//ENFactions//");
			FileConfiguration cfg = getFileManager().getConfiguration("settings.yml", "plugins//ENFactions//");
			ArrayList<String> voteLinks = new ArrayList<String>();
			voteLinks.add("TEST1");
			voteLinks.add("TEST2");
			voteLinks.add("TEST3");
			//ALL SETTINGS EXCLUDING MMO SYSTEM SETTINGS
			cfg.set("Promotions.XPPromotion.Type.Percentage.Active", false);
			cfg.set("Promotions.XPPromotion.Type.Percentage.PlusPercentagePerReward", 0);
			cfg.set("Promotions.XPPromotion.Type.Multiplier.Active", false);
			cfg.set("Promotions.XPPromotion.Type.Multiplier.MultiplierPerReward", 0);
			cfg.set("Promotions.XPPromotion.ActiveForSeconds", 0);
			cfg.set("Promotions.XPPromotion.StartedOn", "NOT ACTIVE");
			cfg.set("Promotions.XPPromotion.EndsOn", "NOT ACTIVE");
			cfg.set("Vote.Links", voteLinks);
			//SETTINGS REGARDING MMO SYSTEM
			for(int i = 1; i <= 500; i++) {
				double base = 150*i;
				double exponent = 1.3;
				double maxXP = Math.round(Math.pow(base, exponent));
				cfg.set("MMOSettings.SkillSettings.XPRequired." + i, maxXP);
			}
				//Swords skill
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.NoHelmet", 20);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.NoChestplate", 20);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.NoLeggings", 20);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.NoBoots", 20);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.LeatherHelmet", 40);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.LeatherChestplate", 120);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.LeatherLeggings", 80);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.LeatherBoots", 40);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.ChainHelmet", 80);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.ChainChestplate", 200);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.ChainLeggings", 160);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.ChainBoots", 40);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.GoldHelmet", 80);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.GoldChestplate", 200);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.GoldLeggings", 120);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.GoldBoots", 40);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.IronHelmet", 80);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.IronChestplate", 240);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.IronLeggings", 200);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.IronBoots", 80);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.DiamondHelmet", 120);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.DiamondChestplate", 320);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.DiamondLeggings", 240);
			cfg.set("MMOSettings.SkillSettings.Swords.Rewards.DiamondBoots", 120);
			cfg.set("MMOSettings.SkillSettings.Swords.Abilities.Bleed.UnlocksOnLevel", 2);
			cfg.set("MMOSettings.SkillSettings.Swords.Abilities.Bleed.CooldownInSeconds", 300);
			cfg.set("MMOSettings.SkillSettings.Swords.Abilities.Bleed.ActiveForSeconds", 6);
			cfg.set("MMOSettings.SkillSettings.Swords.Abilities.Bleed.MinusHPPerSecond", 1);
				//Woodcutting skill
			cfg.set("MMOSettings.SkillSettings.Woodcutting.Rewards.Plank", 12);
			cfg.set("MMOSettings.SkillSettings.Woodcutting.Rewards.Log", 16);
			cfg.set("MMOSettings.SkillSettings.Woodcutting.Rewards.MushroomLog", 28);
			cfg.set("MMOSettings.SkillSettings.Woodcutting.Abilities.FastCutting.UnlocksOnLevel", 2);
			cfg.set("MMOSettings.SkillSettings.Woodcutting.Abilities.FastCutting.CooldownInSeconds", 300);
			cfg.set("MMOSettings.SkillSettings.Woodcutting.Abilities.FastCutting.ActiveForSeconds", 15);
				//Mining skill
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.Netherrack", 2);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.Sandstone", 2);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.Stone", 2);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.HardClay", 3);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.MossyCobblestone", 4);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.PackedIce", 4);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.RedSandstone", 3);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.StainedClay", 3);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.EndStone", 5);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.Prismarine", 7);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.CoalOre", 10);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.QuartzOre", 10);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.RedstoneOre", 5);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.Purpur", 17);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.Obsidian", 20);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.IronOre", 27);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.Lapis", 6);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.Gold", 40);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.Diamond", 90);
			cfg.set("MMOSettings.SkillSettings.Mining.Rewards.Emerald", 110);
			
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining1.UnlocksOnLevel", 1);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining1.ActiveForSeconds", 15);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining1.CooldownInSeconds", 300);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining2.UnlocksOnLevel", 40);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining2.ActiveForSeconds", 15);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining2.CooldownInSeconds", 300);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining3.UnlocksOnLevel", 100);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining3.ActiveForSeconds", 15);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining3.CooldownInSeconds", 300);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining4.UnlocksOnLevel", 150);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining4.ActiveForSeconds", 15);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.SpeedMining4.CooldownInSeconds", 300);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.MinersVision.UnlocksOnLevel", 50);
			cfg.set("MMOSettings.SkillSettings.Mining.Abilities.BlackLungs.ActiveForSeconds", 10);
			
				//Agility skill
			cfg.set("MMOSettings.SkillSettings.Agility.Rewards.Multiplier", 12);
			cfg.set("MMOSettings.SkillSettings.Agility.Abilities.ReducedFallDamage.OffDamageIfLucky", 50); // x% less damage when falling if you're lucky
			cfg.set("MMOSettings.SkillSettings.Agility.Abilities.Roll.UnlocksOnLevel", 2);
			cfg.set("MMOSettings.SkillSettings.Agility.Abilities.Roll.CooldownInSeconds", 300);
			
				//Fishing
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Salmon", 20);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Tuna", 20);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.PirateChest", 100);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Boot", 0);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Pearl", 80);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.GreatWhiteShark", -30);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Anchovy", 20);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Pufferfish", -20);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Mackerel", 30);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Clownfish", 30);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Crab", 30);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Jellyfish", 15);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Squid", 50);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Whale", 50);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.Flatfish", 40);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.GoldArmorPart", 100);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.IronArmorPart", 120);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.DiamondArmorPart", 200);
			cfg.set("MMOSettings.SkillSettings.Fishing.Rewards.GasBell", 0);
			cfg.set("MMOSettings.SkillSettings.Fishing.Abilities.CookedFish.UnlocksOnLevel", 30);
			cfg.set("MMOSettings.SkillSettings.Fishing.Abilities.Drag.UnlocksOnLevel", 25);
			try {
				cfg.save(getFileManager().getFile("settings.yml", "plugins//ENFactions//"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		chatLocked = false;
	}
	
	@Override
	public void onDisable() {
		PlayerEvents.combatLog.clear();
		for(Player p : getServer().getOnlinePlayers()) {
			getPlayerAccount().setLastSeen(p.getUniqueId().toString(), "DATE");
		}
//		if(mysql.isConnected()) mysql.close();
	}
	
	private void registerCommands() {
		getCommand("register").setExecutor(new Register(this));
		getCommand("group").setExecutor(new Group(this));
		getCommand("list").setExecutor(new List(this));
		getCommand("warn").setExecutor(new Warn(this));
		getCommand("kick").setExecutor(new Kick(this));
		getCommand("ban").setExecutor(new Ban(this));
		getCommand("tempban").setExecutor(new Tempban(this));
		getCommand("msg").setExecutor(new Message(this));
		getCommand("r").setExecutor(new Reply(this));
		getCommand("kickall").setExecutor(new KickAll(this));
		getCommand("home").setExecutor(new Home(this));
		getCommand("appeal").setExecutor(new Appeal(this));
		getCommand("clearinventory").setExecutor(new ClearInventory(this));
		getCommand("getpos").setExecutor(new GetPos(this));
		getCommand("feed").setExecutor(new Feed(this));
		getCommand("heal").setExecutor(new Heal(this));
		getCommand("fly").setExecutor(new Fly(this));
		getCommand("item").setExecutor(new Item(this));
		getCommand("teleport").setExecutor(new Teleport(this));
		getCommand("tphere").setExecutor(new Tphere(this));
		getCommand("gamemode").setExecutor(new Gamemode(this));
		getCommand("god").setExecutor(new God(this));
		getCommand("tpa").setExecutor(new Tpa(this));
		getCommand("tpaccept").setExecutor(new Tpaccept(this));
		getCommand("tribes").setExecutor(new Tribes(this));
		getCommand("spawn").setExecutor(new Spawn(this));
		getCommand("setspawn").setExecutor(new SetSpawn(this));
		getCommand("warp").setExecutor(new Warp(this));
		getCommand("info").setExecutor(new Info(this));
		getCommand("mmo").setExecutor(new MMO(this));
		getCommand("chatclear").setExecutor(new ChatClear(this));
		getCommand("chatlock").setExecutor(new ChatLock(this));
		getCommand("sc").setExecutor(new StaffChat(this));
		getCommand("vote").setExecutor(new Vote(this));
		getCommand("shop").setExecutor(new Shop(this));
	}
	
	private void registerListeners() {
		for(Listener l : listeners) {
			Bukkit.getPluginManager().registerEvents(l, this);
		}
	}
	
	public EmailValidator getEmailValidator() {
		return emailValidator;
	}
	
	public PasswordValidator getPasswordValidator() {
		return passwordValidator;
	}
	
	public PasswordProtection getPasswordProtection() {
		return passwordProtection;
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}
	
	public MySQL getMySQL() {
		return mysql;
	}
	
	public PlayerAccount getPlayerAccount() {
		return playerAccount;
	}
	
	public Groups getGroups() {
		return groups;
	}
	
	public Punisher getPunisher() {
		return punisher;
	}
	
	public LocationHelper getLocationHelper() {
		return locationHelper;
	}
	
	public NumericAPI getNumericAPI() {
		return numericAPI;
	}
	
	public TribesAPI getTribesAPI() {
		return tribesAPI;
	}
	
	public ItemManager getItemManager() {
		return itemManager;
	}
	
	public MMOAPI getMMOAPI() {
		return mmoAPI;
	}
	
	public Progress getProgress() {
		return progress;
	}
	
	public Packets getPacketsHelper() {
		return packets;
	}
	
	public TitleHelper getTitleHelper() {
		return titleHelper;
	}
	
	public TabHelper getTabHelper() {
		return tabHelper;
	}
	
	public ActionBarHelper getActionBarHelper() {
		return abHelper;
	}
	
	public VoteTokens getVoteTokensHelper() {
		return voteTokens;
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public PromotionHelper getPromotionHelper() {
		return promotionHelper;
	}
	
	public ENChatColor getChatColor() {
		return chatColor;
	}
	
	public Coins getCoins() {
		return coins;
	}
	
	public ArrayList<Player> getGods() {
		return gods;
	}
	
	public HashMap<Player, Player> getTPA() {
		return tpa;
	}
	
	public HashMap<String, HashMap<String, ItemStack>> getFighterSkillInventoryBeforeDeath() {
		return fsInvBeforeDeath;
	}
	
	public String getPrefix() {
		return "§7[§a§lELERION§7] ";
	}
	
	public boolean isChatLocked() {
		return chatLocked;
	}
	
	public void setChatLocked(boolean v) {
		chatLocked = v;
	}
	
//	private void fillGroupsAndPermissions() {
//		
//	}

}
