package me.jellylicious.elerion_network.factions.listener;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.jellylicious.elerion_network.factions.Core;

public class PlayerEvents implements Listener {
	
	private HashMap<String, String> inTerritory = new HashMap<String, String>();
	private ItemStack helmet, chestplate, leggings, boots;
	private HashMap<String, String> activeEffect = new HashMap<String, String>();
	private List<String> minersVision = new ArrayList<String>();
	private List<String> preBleed = new ArrayList<String>();
	private HashMap<String, Boolean> roll = new HashMap<String, Boolean>();
	private HashMap<String, String> postBleed = new HashMap<String, String>();
	public static HashMap<String, String> combatLog = new HashMap<String, String>();
	private List<String> fastCutting = new ArrayList<String>();
	public List<String> launch = new ArrayList<String>();
	private List<String> cint = new ArrayList<String>(); //Players are added to this whenever they aren't able to open chest. They're removed 1 sec after they're added.
	Core core; 

	public PlayerEvents(Core core) {
		this.core = core;
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e) throws SQLException {
		Player p = e.getPlayer();
		int lastPunishment = core.getPunisher().getLastPunishment(p);
		if (core.getPunisher().isPunishmentActive(lastPunishment)) {
			if (core.getPunisher().timeToUnban(lastPunishment)) {
				core.getPunisher().appealPunishment(core.getServer().getConsoleSender(), lastPunishment);
				e.setResult(org.bukkit.event.player.PlayerLoginEvent.Result.ALLOWED);
			} else {
				if (core.getPunisher().getPunishmentType(lastPunishment).equals("TEMPBAN")) {
					e.setKickMessage("§c§lYou've been temporarily banned from the server by §7" + core.getPunisher().getWhoPunished(lastPunishment) + "§c§l! \n" + "§c§lREASON: §b" + core.getPunisher().getReason(lastPunishment) + " \n" + "§cYour ban expires on §7" + core.getPunisher().getExpirationDate(lastPunishment));
				} else if (core.getPunisher().getPunishmentType(lastPunishment).equals("PERMBAN")) {
					e.setKickMessage("§c§lYou've been banned from the server by §7" + core.getPunisher().getWhoPunished(lastPunishment) + "§c§l! \n" + "§c§lREASON: §b" + core.getPunisher().getReason(lastPunishment));
				} else if (core.getPunisher().getPunishmentType(lastPunishment).equals("WARN")) {
					e.setKickMessage("§7" + core.getPunisher().getWhoPunished(lastPunishment) + " §c§lwarned you while you were offline! \n" + "§c§lREASON: §b" + core.getPunisher().getReason(lastPunishment) + " \n" + "§aFeel free to reconnect. :)");
					core.getPunisher().changeStatusToInactive(lastPunishment);
				}
				e.setResult(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER);
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		String uuid = p.getUniqueId().toString();
		String playerTribe = core.getTribesAPI().getPlayerTribe(uuid);
		Location l = core.getTribesAPI().getMinLocationOfBox(p.getLocation());
		Location h = core.getTribesAPI().getMaxLocationOfBox(p.getLocation());
		if (core.getTribesAPI().isClaimed(l, h)) {
			if (!inTerritory.containsKey(uuid)) {
				String owner = core.getTribesAPI().getClaimOwner(core.getTribesAPI().getClaim(l, h));
				if(owner.equals(playerTribe)) {
					core.getTribesAPI().applyEffects(playerTribe, uuid);
				}else{
					core.getTribesAPI().removeEffects(playerTribe, uuid);
				}
				inTerritory.put(uuid, owner);
				String ourOwner = (String) inTerritory.get(uuid);
				if (owner.equals(ourOwner)) {
					if ((core.getTribesAPI().getPlayerTribe(uuid) != null) && (core.getTribesAPI().tribeExists(core.getTribesAPI().getPlayerTribe(uuid))) && (core.getTribesAPI().hostileTribes(core.getTribesAPI().getPlayerTribe(uuid)).contains(owner))) {
						p.sendMessage(core.getPrefix() + "§cYou entered the territory of tribe §7" + owner + "§c!");
					} else {
						p.sendMessage(core.getPrefix() + "§aYou entered the territory of tribe §7" + owner + "§a!");
					}
				} else {
					inTerritory.remove(uuid, ourOwner);
					inTerritory.put(uuid, owner);
					p.sendMessage(core.getPrefix() + "§aYou left the territory of tribe §7" + ourOwner + " §aand entered the territory of tribe §7" + owner + "§a!");
				}
			}
		} else if (inTerritory.containsKey(uuid)) {
			String owner = (String) inTerritory.get(uuid);
			p.sendMessage(core.getPrefix() + "§aYou left the territory of tribe §7" + owner + "§a!");
			inTerritory.remove(uuid, owner);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String message = e.getMessage();
		if ((core.isChatLocked()) && (!core.getGroups().hasPermission(p, "elerion.chat.overridelock"))) {
			p.sendMessage(core.getPrefix() + "§cYou can not send any public messages, because chat is currently locked.");
			return;
		}
		message = message.toLowerCase();
		if ((!message.endsWith(".")) && (!message.endsWith("!")) && (!message.endsWith("?")))
			message = message + ".";
		if (core.getGroups().hasPermission(p, "elerion.colorchat")) {
			message = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
		}
		message = message.substring(0, 1).toUpperCase() + message.substring(1).toLowerCase();
		for (Player a : core.getServer().getOnlinePlayers()) {
			if (message.contains(a.getName())) {
				message = message.replace(a.getName(), "§e" + a.getName() + core.getChatColor().getChatColor(message));
				a.playSound(a.getLocation(), org.bukkit.Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
			}
		}
		String uuid = p.getUniqueId().toString();
		String tribe = core.getTribesAPI().getPlayerTribe(uuid);
		if (tribe != null) {
			if (core.getTribesAPI().tribeExists(tribe)) {
				for (Player o : Bukkit.getOnlinePlayers()) {
					String ot = core.getTribesAPI().getPlayerTribe(o.getUniqueId().toString());
					if (ot != null) {
						if (core.getTribesAPI().tribeExists(ot)) {
							e.setCancelled(true);
							if (core.getTribesAPI().alliances(ot).contains(tribe)) {
								o.sendMessage("§a" + tribe + " §8| " + p.getDisplayName() + " §a> §7§o" + message);
							} else if (core.getTribesAPI().hostileTribes(ot).contains(tribe)) {
								o.sendMessage("§c" + tribe + " §8| " + p.getDisplayName() + " §a> §7§o" + message);
							} else if (core.getTribesAPI().isNeutral(ot, tribe)) {
								o.sendMessage("§f" + tribe + " §8| " + p.getDisplayName() + " §a> §7§o" + message);
							} else if (core.getTribesAPI().belongsToTribe(tribe, o.getUniqueId().toString())) {
								o.sendMessage("§f" + tribe + " §8| " + p.getDisplayName() + " §a> §7§o" + message);
							}
						} else {
							o.sendMessage("§f" + tribe + " §8| " + p.getDisplayName() + " §a> §7§o" + message);
						}
					} else {
						o.sendMessage("§f" + tribe + " §8| " + p.getDisplayName() + " §a> §7§o" + message);
					}
				}
			} else {
				e.setFormat(p.getDisplayName() + " §a> §7§o" + message);
			}
		} else {
			e.setFormat(p.getDisplayName() + " §a> §7§o" + message);
		}
	}

	@EventHandler
	public void onHurt(EntityDamageEvent e) {
		if ((e.getEntity() instanceof Player)) {
			Player p = (Player) e.getEntity();
			if (core.getGods().contains(p)) {
				e.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final String uuid = p.getUniqueId().toString();
		ItemStack iih = p.getItemInHand();
		Block b = e.getClickedBlock();
		if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if (iih.getType().toString().contains("_PICKAXE")) {
				if (!core.getMMOAPI().hasAbilityUnlocked(uuid, "Mining", "SpeedMining4")) {
					if (!core.getMMOAPI().hasAbilityUnlocked(uuid, "Mining", "SpeedMining3")) {
						if (!core.getMMOAPI().hasAbilityUnlocked(uuid, "Mining", "SpeedMining2")) {
							if (core.getMMOAPI().hasAbilityUnlocked(uuid, "Mining", "SpeedMining1")) {
								if (!activeEffect.containsKey(uuid)) {
									activeEffect.put(uuid, "SpeedMining1");
									int activeFor = core.getMMOAPI().activeFor("Mining", "SpeedMining1");
									final int cooldown = core.getMMOAPI().cooldown("Mining", "SpeedMining1");
									p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, activeFor * 20, 2));
									p.sendMessage(core.getPrefix() + "§aYou successfully activated §7Speed Mining 1 §aability. Hurry up, it will only last for §7" + activeFor + " §aseconds!");
									core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
										public void run() {
											if ((activeEffect.containsKey(uuid)) && (p != null)) {
												p.sendMessage(core.getPrefix() + "§7Speed Mining 1 §aability has expired. You can reuse it in §7" + cooldown + " §aseconds!");
												core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
													public void run() {
														String effect = (String) activeEffect.get(uuid);
														activeEffect.remove(uuid, effect);
														p.sendMessage(core.getPrefix() + "§aYou can now reuse the ability you used §7" + cooldown + " §aseconds ago!");
													}
												}, cooldown * 20);
											}

										}

									}, activeFor * 20);
								} else {
									p.sendMessage(core.getPrefix() + "§cYou already have one of the abilities activated!");
								}
							}
						} else if (!activeEffect.containsKey(uuid)) {
							activeEffect.put(uuid, "SpeedMining2");
							int activeFor = core.getMMOAPI().activeFor("Mining", "SpeedMining2");
							final int cooldown = core.getMMOAPI().cooldown("Mining", "SpeedMining2");
							p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, activeFor * 20, 4));
							p.sendMessage(core.getPrefix() + "§aYou successfully activated §7Speed Mining 2 §aability. Hurry up, it will only last for §7" + activeFor + " §aseconds!");
							core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
								public void run() {
									if ((activeEffect.containsKey(uuid)) && (p != null)) {
										p.sendMessage(core.getPrefix() + "§7Speed Mining 2 §aability has expired. You can reuse it in §7" + cooldown + " §aseconds!");
										core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
											public void run() {
												String effect = (String) activeEffect.get(uuid);
												activeEffect.remove(uuid, effect);
												p.sendMessage(core.getPrefix() + "§aYou can now reuse the ability you used §7" + cooldown + " §aseconds ago!");
											}
										}, cooldown * 20);
									}

								}

							}, activeFor * 20);
						} else {
							p.sendMessage(core.getPrefix() + "§cYou already have one of the abilities activated!");
						}

					} else if (!activeEffect.containsKey(uuid)) {
						activeEffect.put(uuid, "SpeedMining3");
						int activeFor = core.getMMOAPI().activeFor("Mining", "SpeedMining3");
						final int cooldown = core.getMMOAPI().cooldown("Mining", "SpeedMining3");
						p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, activeFor * 20, 6));
						p.sendMessage(core.getPrefix() + "§aYou successfully activated §7Speed Mining 3 §aability. Hurry up, it will only last for §7" + activeFor + " §aseconds!");
						core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
							public void run() {
								if ((activeEffect.containsKey(uuid)) && (p != null)) {
									p.sendMessage(core.getPrefix() + "§7Speed Mining 3 §aability has expired. You can reuse it in §7" + cooldown + " §aseconds!");
									core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
										public void run() {
											String effect = (String) activeEffect.get(uuid);
											activeEffect.remove(uuid, effect);
											p.sendMessage(core.getPrefix() + "§aYou can now reuse the ability you used §7" + cooldown + " §aseconds ago!");
										}
									}, cooldown * 20);
								}

							}

						}, activeFor * 20);
					} else {
						p.sendMessage(core.getPrefix() + "§cYou already have one of the abilities activated!");
					}

				} else if (!activeEffect.containsKey(uuid)) {
					activeEffect.put(uuid, "SpeedMining4");
					int activeFor = core.getMMOAPI().activeFor("Mining", "SpeedMining4");
					final int cooldown = core.getMMOAPI().cooldown("Mining", "SpeedMining4");
					p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, activeFor * 20, 8));
					p.sendMessage(core.getPrefix()
							+ "§aYou successfully activated §7Speed Mining 4 §aability. Hurry up, it will only last for §7"
							+ activeFor + " §aseconds!");
					core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
						public void run() {
							if ((activeEffect.containsKey(uuid)) && (p != null)) {
								p.sendMessage(core.getPrefix()
										+ "§7Speed Mining 4 §aability has expired. You can reuse it in §7" + cooldown
										+ " §aseconds!");
								core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
									public void run() {
										String effect = (String) activeEffect.get(uuid);
										activeEffect.remove(uuid, effect);
										p.sendMessage(
												core.getPrefix() + "§aYou can now reuse the ability you used §7"
														+ cooldown + " §aseconds ago!");
									}

								}, cooldown * 20);
							}

						}

					}, activeFor * 20);
				} else {
					p.sendMessage(core.getPrefix() + "§cYou already have one of the abilities activated!");
				}
			} else if (iih.getType().toString().contains("_SWORD")) {
				if ((core.getMMOAPI().hasAbilityUnlocked(uuid, "Swords", "Bleed")) && (!preBleed.contains(uuid))) {
					preBleed.add(uuid);
					int minusHP = core.getMMOAPI().getMinusHPPerSecond();
					int length = core.getMMOAPI().activeFor("Swords", "Bleed");
					p.sendMessage(core.getPrefix()
							+ "§aPlease hit your target to cause it to bleed! Your target will lose §7" + minusHP
							+ " §aHP every second. The effect will run for §7" + length + " §aseconds!");
				}
			} else if ((iih.getType().toString().contains("_AXE"))
					&& (core.getMMOAPI().hasAbilityUnlocked(uuid, "Woodcutting", "FastCutting"))
					&& (!fastCutting.contains(uuid))) {
				fastCutting.add(uuid);
				final int cooldown = core.getMMOAPI().cooldown("Woodcutting", "FastCutting");
				int activeFor = core.getMMOAPI().activeFor("Woodcutting", "FastCutting");
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, activeFor * 20, 2));
				p.sendMessage(core.getPrefix()
						+ "§aYou successfully activated §7Fast Cutting §aability. Hurry up, it will only last for §7"
						+ activeFor + " §aseconds!");
				Bukkit.getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
					public void run() {
						if (fastCutting.contains(uuid)) {
							fastCutting.remove(uuid);
							p.sendMessage(core.getPrefix() + "§aYou can now reuse the Fast Cutting ability you used §7"
									+ cooldown + " §aseconds ago!");
						}

					}
				}, cooldown * 20L);
			}
		}
		
		if (b != null) {
			if (b.getType() != Material.AIR) {
				if ((e.getClickedBlock().getType().isBlock()) && (e.getClickedBlock().getType().isSolid() || e.getClickedBlock().getType().isTransparent())) {
					Location bl = b.getLocation();
					Location l = core.getTribesAPI().getMinLocationOfBox(bl);
					Location h = core.getTribesAPI().getMaxLocationOfBox(bl);
					String claim = core.getTribesAPI().getClaim(l, h);
					if (claim != null) {
//						p.sendMessage(b.toString());
//						p.sendMessage(b.getData() + "");
						String claimOwner = core.getTribesAPI().getClaimOwner(claim);
						if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if (!core.getTribesAPI().belongsToTribe(claimOwner, uuid)) {
//							if(b.getType().toString().contains("DOOR") && !b.getType().toString().contains("TRAP_DOOR")) {
//								e.setCancelled(true);
//								b.setData((byte) 8);
//								b.getState().update(true);
////								Door d = (Door) e.getClickedBlock().getState().getData();
////								d.setOpen(d.isOpen());
//							}
//							if(b.getType().toString().contains("FENCE_GATE")) {
//								e.setCancelled(true);
//								b.setData((byte) 1);
//								b.getState().update(true);
////								Gate fg = (Gate) e.getClickedBlock().getState().getData();
////								fg.setOpen(fg.isOpen());
//							}
//							if(b.getType().toString().contains("TRAP_DOOR")) {
//								e.setCancelled(true);
//								TrapDoor td = (TrapDoor) e.getClickedBlock().getState().getData();
//								td.setOpen(td.isOpen());
//							}
								if ((b.getType() == Material.CHEST) || (b.getType() == Material.DISPENSER)
										|| (b.getType() == Material.WORKBENCH) || (b.getType() == Material.FURNACE)
										|| (b.getType() == Material.BURNING_FURNACE)
										|| (b.getType() == Material.IRON_TRAPDOOR)
										|| (b.getType() == Material.ACACIA_DOOR) || (b.getType() == Material.BIRCH_DOOR)
										|| (b.getType() == Material.DARK_OAK_DOOR)
										|| (b.getType() == Material.IRON_DOOR) || (b.getType() == Material.JUNGLE_DOOR)
										|| (b.getType() == Material.SPRUCE_DOOR) || (b.getType() == Material.TRAP_DOOR)
										|| (b.getType() == Material.WOOD_DOOR) || (b.getType() == Material.WOODEN_DOOR)
										|| (b.getType() == Material.ENCHANTMENT_TABLE)
										|| (b.getType() == Material.ENDER_CHEST)
										|| (b.getType() == Material.TRAPPED_CHEST) || (b.getType() == Material.HOPPER)
										|| (b.getType() == Material.DROPPER) || (b.getType().toString().contains("FENCE_GATE"))
										|| (b.getType() == Material.BREWING_STAND)
										|| (b.getType() == Material.NOTE_BLOCK) || (b.getType() == Material.BEACON)
										|| (b.getType() == Material.END_CRYSTAL) || (b.getType() == Material.DRAGON_EGG)
										|| (b.getType() == Material.REDSTONE_COMPARATOR)
										|| (b.getType() == Material.REDSTONE) || (b.getType() == Material.LEVER)
										|| (b.getType() == Material.ANVIL) || (b.getType() == Material.STONE_BUTTON)
										|| (b.getType() == Material.WOOD_BUTTON) || (b.getType() == Material.DIODE)
										|| (b.getType() == Material.DIODE_BLOCK_OFF)
										|| (b.getType() == Material.DIODE_BLOCK_ON)) {
									if(!cint.contains(p.getName())) {
										cint.add(p.getName());
										core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {

											@Override
											public void run() {
												cint.remove(p.getName());
											}
											
										}, 20L);
									}
									e.setCancelled(true);
									p.sendMessage(core.getPrefix()
											+ "§cYou can not interact with blocks in this area. This territory belongs to tribe §7"
											+ claimOwner
											+ "§c. To gain access to this territory, locate their crystal, destroy it and raid them!");
								}else{
									e.setCancelled(false);
								}
								if ((iih.getType() == Material.LAVA_BUCKET)
										|| (iih.getType() == Material.WATER_BUCKET)) {
									e.setCancelled(true);
									p.sendMessage(core.getPrefix()
											+ "§cYou can not place lava and water in this area. This territory belongs to tribe §7"
											+ claimOwner
											+ "§c. To gain access to this territory, locate their crystal, destroy it and raid them!");
								} else {
									e.setCancelled(false);
								}
							} else {
								e.setCancelled(false);
							}
						} else if (e.getAction() == Action.PHYSICAL) {
							if (!core.getTribesAPI().belongsToTribe(claimOwner, uuid)) {
								if ((b.getType() == Material.GOLD_PLATE) || (b.getType() == Material.IRON_PLATE)
										|| (b.getType() == Material.WOOD_PLATE)
										|| (b.getType() == Material.STONE_PLATE)) {
									e.setCancelled(true);
								} else {
									e.setCancelled(false);
								}
							} else {
								e.setCancelled(false);
							}
						} else {
							e.setCancelled(false);
						}
					} else {
						e.setCancelled(false);
					}
				} else {
					e.setCancelled(false);
				}
			} else {
				e.setCancelled(false);
			}
		} else {
			e.setCancelled(false);
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		Player p = (Player) e.getPlayer();
		e.setCancelled(cint.contains(p.getName()));
	}

	@EventHandler
	public void cauldronLevelChange(CauldronLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		Block b = e.getBlock();
		Location loc = b.getLocation();
		Location l = core.getTribesAPI().getMinLocationOfBox(loc);
		Location h = core.getTribesAPI().getMaxLocationOfBox(loc);
		String claim = core.getTribesAPI().getClaim(l, h);
		if (claim != null) {
			String claimOwner = core.getTribesAPI().getClaimOwner(claim);
			Location cl = core.getTribesAPI().getMinLocation(claim);
			Location hl = core.getTribesAPI().getMaxLocation(claim);
			if ((!core.getTribesAPI().belongsToTribe(claimOwner, p.getUniqueId().toString()))
					&& (core.getTribesAPI().isInsideArea(loc, cl, hl))) {
				e.setCancelled(true);
				p.sendMessage(core.getPrefix() + "§cYou can not interact with cauldrons in this area!");
			}
		}
	}

	@EventHandler
	public void bucketFill(PlayerBucketFillEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlockClicked();
		Location bl = b.getLocation();
		Location l = core.getTribesAPI().getMinLocationOfBox(bl);
		Location h = core.getTribesAPI().getMaxLocationOfBox(bl);
		String claim = core.getTribesAPI().getClaim(l, h);
		if (claim != null) {
			String claimOwner = core.getTribesAPI().getClaimOwner(claim);
			Location cl = core.getTribesAPI().getMinLocation(claim);
			Location hl = core.getTribesAPI().getMaxLocation(claim);
			if ((!core.getTribesAPI().belongsToTribe(claimOwner, p.getUniqueId().toString())) && (core.getTribesAPI().isInsideArea(bl, cl, hl))) {
				e.setCancelled(true);
				p.sendMessage(core.getPrefix() + "§cYou can not fill your buckets in this area. This territory belongs to tribe §7" + claimOwner + "§c. To gain access to this territory, locate their crystal, destroy it and raid them!");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (((e.getDamager() instanceof Player)) && ((e.getEntity() instanceof Player))) {
			final Player damager = (Player) e.getDamager();
			final Player target = (Player) e.getEntity();
			final String duuid = damager.getUniqueId().toString();
			final String tuuid = target.getUniqueId().toString();
			String dt = core.getTribesAPI().getPlayerTribe(duuid);
			String tt = core.getTribesAPI().getPlayerTribe(tuuid);
			boolean allowedToHit = false;
			if (dt != null) {
				dt = dt.toUpperCase();
				if (core.getTribesAPI().tribeExists(dt)) {
					if (tt != null) {
						tt = tt.toUpperCase();
						if (core.getTribesAPI().tribeExists(tt)) {
							if (!dt.equals(tt)) {
								if (core.getTribesAPI().alliances(dt).contains(tt)) {
									allowedToHit = false;
									e.setCancelled(true);
									damager.sendMessage(core.getPrefix() + "§cYou can not hurt someone who belongs to your alliances.");
								} else {
									allowedToHit = true;
									e.setCancelled(false);
									helmet = target.getInventory().getHelmet();
									chestplate = target.getInventory().getChestplate();
									leggings = target.getInventory().getLeggings();
									boots = target.getInventory().getBoots();
								}
							} else {
								allowedToHit = false;
								e.setCancelled(true);
								damager.sendMessage(core.getPrefix() + "§cYou can not hurt someone who belongs to your tribe.");
							}
						} else {
							allowedToHit = true;
							e.setCancelled(false);
							helmet = target.getInventory().getHelmet();
							chestplate = target.getInventory().getChestplate();
							leggings = target.getInventory().getLeggings();
							boots = target.getInventory().getBoots();
						}
					} else {
						allowedToHit = true;
						e.setCancelled(false);
						helmet = target.getInventory().getHelmet();
						chestplate = target.getInventory().getChestplate();
						leggings = target.getInventory().getLeggings();
						boots = target.getInventory().getBoots();
					}
				} else {
					allowedToHit = true;
					e.setCancelled(false);
					helmet = target.getInventory().getHelmet();
					chestplate = target.getInventory().getChestplate();
					leggings = target.getInventory().getLeggings();
					boots = target.getInventory().getBoots();
				}
			} else {
				allowedToHit = true;
				e.setCancelled(false);
				helmet = target.getInventory().getHelmet();
				chestplate = target.getInventory().getChestplate();
				leggings = target.getInventory().getLeggings();
				boots = target.getInventory().getBoots();
			}

			if (allowedToHit) {
				if ((!combatLog.containsKey(duuid)) || (!combatLog.containsValue(tuuid)) || (!combatLog.containsValue(duuid)) || (!combatLog.containsValue(tuuid))) {
					combatLog.put(duuid, tuuid);
					combatLog.put(tuuid, duuid);
					damager.sendMessage(core.getPrefix() + "§aYou are not allowed to quit the server within the next 30 seconds! If you leave you die and lose all your items because of our combat log protection!");
					target.sendMessage(core.getPrefix() + "§aYou are not allowed to quit the server within the next 30 seconds! If you leave you die and lose all your items because of our combat log protection!");
					core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
						public void run() {
							if (((combatLog.containsKey(duuid)) && (combatLog.containsValue(tuuid))) || ((combatLog.containsKey(tuuid)) && (combatLog.containsValue(duuid)))) {
								combatLog.remove(duuid, tuuid);
								combatLog.remove(tuuid, duuid);
								damager.sendMessage(core.getPrefix() + "§aYou're now safe from logging off.");
								target.sendMessage(core.getPrefix() + "§aYou're now safe from logging off.");
								
							}

						}
					}, 600L);
				}
				if ((preBleed.contains(duuid)) && (!postBleed.containsKey(duuid))) {
					postBleed.put(duuid, tuuid);
					final int minusHP = core.getMMOAPI().getMinusHPPerSecond();
					int length = core.getMMOAPI().activeFor("Swords", "Bleed");
					final int cooldown = core.getMMOAPI().cooldown("Swords", "Bleed");
					damager.sendMessage(core.getPrefix() + "§aYou have just used the Bleed ability on §7"
							+ target.getName() + "§a!");
					target.sendMessage(core.getPrefix() + "§7" + target.getName()
							+ " §chas just used the Bleed ability on you! You will lose §7" + minusHP
							+ " §cHP per second. The effect will run for §7" + length + " §cseconds.");
					core.getServer().getScheduler().scheduleSyncRepeatingTask(core, new BukkitRunnable() {
						public void run() {
							target.damage(minusHP);
						}

					}, 20L, length * 20);
					core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
						public void run() {
							postBleed.remove(duuid, tuuid);
							damager.sendMessage(core.getPrefix() + "§cBleed effect on §7" + target.getName()
									+ " §chas just ended. Please wait §7" + cooldown
									+ " §cseconds before reusing this ability.");
							target.sendMessage(core.getPrefix() + "§cBleed effect on §7" + target.getName()
									+ " §chas just ended. Lucky you!");
							core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
								public void run() {
									preBleed.remove(duuid);
									damager
											.sendMessage(core.getPrefix() + "§aYou can now reuse the bleed ability!");
								}

							}, cooldown * 20);
						}

					}, length * 20);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onKill(EntityDeathEvent e) {
		if ((e.getEntity().getKiller() instanceof Player)) {
			Player killer = e.getEntity().getKiller();
			int kills = (int) core.getPlayerAccount().get(killer.getUniqueId().toString(), "enPlayerData", "Kills", "UUID");
			core.getPlayerAccount().set(killer.getUniqueId().toString(), "enPlayerData", "Kills", kills+1);
			if ((e.getEntity() instanceof Player)) {
				ItemStack killedWith = killer.getItemInHand();
				if ((killedWith.getType() == Material.WOOD_SWORD) || (killedWith.getType() == Material.DIAMOND_SWORD)
						|| (killedWith.getType() == Material.GOLD_SWORD)
						|| (killedWith.getType() == Material.IRON_SWORD)
						|| (killedWith.getType() == Material.STONE_SWORD)) {
					int r = 0;

					if (helmet != null) {
						if (helmet.getType() == Material.LEATHER_HELMET) {
							r += core.getMMOAPI().getReward("Swords", "LeatherHelmet");
						} else if (helmet.getType() == Material.CHAINMAIL_HELMET) {
							r += core.getMMOAPI().getReward("Swords", "ChainHelmet");
						} else if (helmet.getType() == Material.GOLD_HELMET) {
							r += core.getMMOAPI().getReward("Swords", "GoldHelmet");
						} else if (helmet.getType() == Material.IRON_HELMET) {
							r += core.getMMOAPI().getReward("Swords", "IronHelmet");
						} else if (helmet.getType() == Material.DIAMOND_HELMET)
							r += core.getMMOAPI().getReward("Swords", "DiamondHelmet");
					} else {
						r += core.getMMOAPI().getReward("Swords", "NoHelmet");
					}

					if (chestplate != null) {
						if (chestplate.getType() == Material.LEATHER_CHESTPLATE) {
							r += core.getMMOAPI().getReward("Swords", "LeatherChestplate");
						} else if (chestplate.getType() == Material.CHAINMAIL_CHESTPLATE) {
							r += core.getMMOAPI().getReward("Swords", "ChainChestplate");
						} else if (chestplate.getType() == Material.GOLD_CHESTPLATE) {
							r += core.getMMOAPI().getReward("Swords", "GoldChestplate");
						} else if (chestplate.getType() == Material.IRON_CHESTPLATE) {
							r += core.getMMOAPI().getReward("Swords", "IronChestplate");
						} else if (chestplate.getType() == Material.DIAMOND_CHESTPLATE)
							r += core.getMMOAPI().getReward("Swords", "DiamondChestplate");
					} else {
						r += core.getMMOAPI().getReward("Swords", "NoChestplate");
					}

					if (leggings != null) {
						if (leggings.getType() == Material.LEATHER_LEGGINGS) {
							r += core.getMMOAPI().getReward("Swords", "LeatherLeggings");
						} else if (leggings.getType() == Material.CHAINMAIL_LEGGINGS) {
							r += core.getMMOAPI().getReward("Swords", "ChainLeggings");
						} else if (leggings.getType() == Material.GOLD_LEGGINGS) {
							r += core.getMMOAPI().getReward("Swords", "GoldLeggings");
						} else if (leggings.getType() == Material.IRON_LEGGINGS) {
							r += core.getMMOAPI().getReward("Swords", "IronLeggings");
						} else if (leggings.getType() == Material.DIAMOND_LEGGINGS)
							r += core.getMMOAPI().getReward("Swords", "DiamondLeggings");
					} else {
						r += core.getMMOAPI().getReward("Swords", "NoLeggings");
					}

					if (boots != null) {
						if (boots.getType() == Material.LEATHER_BOOTS) {
							r += core.getMMOAPI().getReward("Swords", "LeatherBoots");
						} else if (boots.getType() == Material.CHAINMAIL_BOOTS) {
							r += core.getMMOAPI().getReward("Swords", "ChainBoots");
						} else if (boots.getType() == Material.GOLD_BOOTS) {
							r += core.getMMOAPI().getReward("Swords", "GoldBoots");
						} else if (boots.getType() == Material.IRON_BOOTS) {
							r += core.getMMOAPI().getReward("Swords", "IronBoots");
						} else if (boots.getType() == Material.DIAMOND_BOOTS)
							r += core.getMMOAPI().getReward("Swords", "DiamondBoots");
					} else {
						r += core.getMMOAPI().getReward("Swords", "NoBoots");
					}
					int cxp = ((Integer) core.getPlayerAccount().get(killer.getUniqueId().toString(), "enPlayerData",
							"SwordsXP", "UUID")).intValue();
					core.getPlayerAccount().set(killer.getUniqueId().toString(), "enPlayerData", "SwordsXP",
							Integer.valueOf(cxp + r));
					core.getMMOAPI().updateLevel("Swords", killer);
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		String uuid = p.getUniqueId().toString();
		int deaths = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "Deaths", "UUID");
		core.getPlayerAccount().set(uuid, "enPlayerData", "Deaths", Integer.valueOf(deaths + 1));
		if (combatLog.containsKey(uuid)) {
			String opponent = (String) combatLog.get(uuid);
			combatLog.remove(uuid, opponent);
			combatLog.remove(opponent, uuid);
			Player op = Bukkit.getPlayer(UUID.fromString(opponent));
			if (op != null) op.sendMessage(core.getPrefix() + "§aCombat log protection removed. You're now safe from logging off.");
			p.sendMessage(core.getPrefix() + "§aCombat log protection removed. You're now safe from logging off.");
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Location l = core.getLocationHelper().getLocation("SpawnLocation.yml", "Location", "plugins//ENFactions//locations//");
        e.setRespawnLocation(l);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!core.getPlayerAccount().existsPlayer(p.getUniqueId().toString())) {
			core.getPlayerAccount().registerPlayer(p);
			int playerCount = 0;
			try {
				playerCount = core.getMySQL().countRows("enPlayerData");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.setJoinMessage("§7[§a§l+§7] " + p.getName() + " §ajoined Elerion for the first time. §8(§7#§c" + (playerCount + 1) + "§8)");
			Location l = core.getLocationHelper().getLocation("SpawnLocation.yml", "Location", "plugins//ENFactions//locations//");
			p.teleport(l);
			ItemStack pick = core.getItemManager().createItemWithMaterial(Material.STONE_PICKAXE, 0, 1, "", null);
			ItemStack sword = core.getItemManager().createItemWithMaterial(Material.WOOD_SWORD, 0, 1, "", null);
			ItemStack wood = core.getItemManager().createItemWithID(5, 0, 32, "", null);
			ItemStack dirt = core.getItemManager().createItemWithMaterial(Material.DIRT, 0, 64, "", null);
			ItemStack apple = core.getItemManager().createItemWithMaterial(Material.APPLE, 0, 32, "", null);
			ItemStack gold_apple = core.getItemManager().createItemWithMaterial(Material.GOLDEN_APPLE, 0, 1, "", null);
			ItemStack torch = core.getItemManager().createItemWithMaterial(Material.TORCH, 0, 10, "", null);
			ItemStack helm = core.getItemManager().createItemWithMaterial(Material.IRON_HELMET, 0, 1, "", null);
			ItemStack chest = core.getItemManager().createItemWithMaterial(Material.LEATHER_CHESTPLATE, 0, 1, "", null);
			ItemStack legs = core.getItemManager().createItemWithMaterial(Material.LEATHER_LEGGINGS, 0, 1, "", null);
			ItemStack boots = core.getItemManager().createItemWithMaterial(Material.LEATHER_BOOTS, 0, 1, "", null);
			PlayerInventory inv = p.getInventory();
			inv.setItem(0, pick);
			inv.setItem(1, sword);
			inv.setItem(2, wood);
			inv.setItem(3, dirt);
			inv.setItem(4, apple);
			inv.setItem(5, gold_apple);
			inv.setItem(8, torch);
			inv.setHelmet(helm);
			inv.setChestplate(chest);
			inv.setLeggings(legs);
			inv.setBoots(boots);
			core.getCoins().addCoins(p, 250);
			core.getVoteTokensHelper().addTokens(p, 50);
		} else {
			e.setJoinMessage("§7[§a§l+§7] §b" + p.getName());
		}
		String ymlName = p.getUniqueId().toString() + ".yml";
		String filePath = "plugins//ENFactions//PlayerData//";
		if (!core.getFileManager().getFile(ymlName, filePath).exists()) {
			core.getFileManager().createNewFile(ymlName, filePath);
			File playerFile = core.getFileManager().getFile(ymlName, filePath);
			FileConfiguration cfg = core.getFileManager().getConfiguration(ymlName, filePath);
			ArrayList<String> homes = new ArrayList<String>();
			ArrayList<String> n = new ArrayList<String>();
			homes.add(" ");
			n.add(" ");
			cfg.set("Information.Home", homes);
			cfg.set("McMMO.XP", Integer.valueOf(0));
			cfg.set("McMMO.Level", Integer.valueOf(0));
			cfg.set("Notifications", n);
			try {
				cfg.save(playerFile);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		sendMotd(p);
		int joins = (int) core.getPlayerAccount().get(p.getUniqueId().toString(), "enPlayerData", "Joins", "UUID");
		core.getPlayerAccount().set(p.getUniqueId().toString(), "enPlayerData", "Joins", Integer.valueOf(joins + 1));
		core.getPlayerAccount().setLastSeen(p.getUniqueId().toString(), "NOW");
		String group = core.getGroups().getPlayerGroup(p.getUniqueId().toString());
		String prefix = core.getGroups().getPrefix(group);
		String suffix = core.getGroups().getSuffix(group);
		p.setDisplayName(prefix + " " + suffix + p.getName());
		boolean emptyInventory = Boolean.valueOf((String) core.getPlayerAccount().get(p.getUniqueId().toString(), "enPlayerData", "CombatLog", "UUID"));
		if (emptyInventory) {
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.sendMessage(core.getPrefix() + "§cYou inventory was cleared because you logged off while being hit by another player.");
			core.getPlayerAccount().set(p.getUniqueId().toString(), "enPlayerData", "CombatLog", "false");
		}
		core.getTabHelper().sendTabTitle(p, "§a§lELERION.NET \n §7Vote daily using §c/vote§7! \n", "\n §7Need help? Our §cstaff team §7will gladly help you \n §7Our official shop is available @ §awww.elerion.net");
		core.getTitleHelper().sendTitle(p, "&a&lElerion Network", "&7&lWelcome, &6&l" + p.getName() + "&7&l!");
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		String uuid = p.getUniqueId().toString();
		e.setQuitMessage("§7[§c§l-§7] §b" + p.getName());
		core.getPlayerAccount().setLastSeen(p.getUniqueId().toString(), "DATE");
		if ((combatLog.containsKey(uuid)) || (combatLog.containsValue(uuid))) {
			String ouuid = (String) combatLog.get(uuid);
			Player opponent = Bukkit.getPlayer(UUID.fromString(ouuid));
			if (opponent != null) {
				opponent.sendMessage(core.getPrefix() + "§7" + p.getName() + " §chas just left the server. Since you two were fighting, you're now safe from logging off. You have also received all items that used to belong to your opponent!");
				ItemStack[] items = p.getInventory().getContents();
				if ((items != null) && (items.length != 0)) {
					for (ItemStack item : items) {
						if (item != null) {
							opponent.getInventory().addItem(new ItemStack[] { item });
						}
					}
				}
			}
			combatLog.remove(uuid, ouuid);
			combatLog.remove(ouuid, uuid);
			int deaths = ((Integer) core.getPlayerAccount().get(uuid, "enPlayerData", "Deaths", "UUID")).intValue();
			core.getPlayerAccount().set(uuid, "enPlayerData", "Deaths", Integer.valueOf(deaths + 1));
			int kills = ((Integer) core.getPlayerAccount().get(ouuid, "enPlayerData", "Kills", "UUID")).intValue();
			core.getPlayerAccount().set(ouuid, "enPlayerData", "Kills", Integer.valueOf(kills + 1));
			core.getPlayerAccount().set(uuid, "enPlayerData", "CombatLog", "true");
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Location bloc = e.getBlock().getLocation();
		Location l = core.getTribesAPI().getMinLocationOfBox(bloc);
		Location h = core.getTribesAPI().getMaxLocationOfBox(bloc);
		String uuid = p.getUniqueId().toString();
		String tribe = core.getTribesAPI().getPlayerTribe(uuid);
		String claim = core.getTribesAPI().getClaim(l, h);
		if (claim != null) {
			Location ll = core.getTribesAPI().getMinLocation(claim);
			Location hl = core.getTribesAPI().getMaxLocation(claim);
			if (core.getTribesAPI().isInsideArea(bloc, ll, hl)) {
				String co = core.getTribesAPI().getClaimOwner(core.getTribesAPI().getClaim(l, h));
				if (tribe != null) {
					if (!co.equals(tribe)) {
						if(core.getTribesAPI().isTribeProtected(co)) {
							e.setCancelled(true);
							p.sendMessage(core.getPrefix() + "§cYou can not place blocks on this territory.");
						}
					}
				} else {
					if(core.getTribesAPI().isTribeProtected(co)) {
						e.setCancelled(true);
						p.sendMessage(core.getPrefix() + "§cYou can not place blocks on this territory.");
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDestroy(BlockBreakEvent e) {
		Player p = e.getPlayer();
		String uuid = p.getUniqueId().toString();
		String tribe = core.getTribesAPI().getPlayerTribe(uuid);
		Block b = e.getBlock();
		Location bloc = b.getLocation();
		boolean allowedToDestroy = true;
		if ((b != null) && (bloc != null)) {
			Location l = core.getTribesAPI().getMinLocationOfBox(bloc);
			Location h = core.getTribesAPI().getMaxLocationOfBox(bloc);
			String claim = core.getTribesAPI().getClaim(l, h);
			if (claim != null) {
				String claimOwner = core.getTribesAPI().getClaimOwner(claim);
				if (tribe != null) {
					if (!claimOwner.equals(tribe)) {
//						if(!core.getTribesAPI().alliances(claimOwner).contains(tribe)) {
							if(core.getTribesAPI().isTribeProtected(claimOwner)) { // 
								e.setCancelled(true);
								allowedToDestroy = false;
								p.sendMessage(core.getPrefix() + "§cYou can not break blocks on this territory.");
							} else {
								allowedToDestroy = true;
							}
//						}else{
//							allowedToDestroy = false;
//							e.setCancelled(true);
//						}
					}else{
						allowedToDestroy = true;
					}
				} else{
					if(core.getTribesAPI().isTribeProtected(claimOwner)) {
						allowedToDestroy = false;
						e.setCancelled(true);
						p.sendMessage(core.getPrefix() + "§cYou can not break blocks on this territory.");
					}else{
						allowedToDestroy = true;
					}
				}
			} else {
				allowedToDestroy = true;
			}

			if (allowedToDestroy) {
				ItemStack is = p.getItemInHand();
				int miningXP = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID");
				int woodcuttingXP = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "WoodcuttingXP", "UUID");
				int reward = 0;
				if (is != null)
					if (is.getType().toString().contains("PICKAXE")) {
						int miningLevel = (int) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningLevel","UUID");

						int percentage = miningLevel / 10;
						boolean doubleDrop = core.getMMOAPI().randomBooleanByChance(percentage, 100 - percentage);

						int blackLungsPercentage = core.getMMOAPI().calculateBlackLungsPercentage(miningLevel);
						boolean blackLungs = core.getMMOAPI().randomBooleanByChance(blackLungsPercentage, 100 - blackLungsPercentage);
						if (blackLungs) {
							int length = core.getMMOAPI().activeFor("Mining", "BlackLungs");
							p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, length * 20, 2));
						}
						ItemMeta im = is.getItemMeta();
						if (b.getType() == Material.NETHERRACK) {
							reward = core.getMMOAPI().getReward("Mining", "Netherrack");
						} else if (b.getType() == Material.SANDSTONE) {
							reward = core.getMMOAPI().getReward("Mining", "Sandstone");
						} else if (b.getType() == Material.STONE) {
							reward = core.getMMOAPI().getReward("Mining", "Stone");
						} else if (b.getType() == Material.HARD_CLAY) {
							reward = core.getMMOAPI().getReward("Mining", "HardClay");
						} else if (b.getType() == Material.MOSSY_COBBLESTONE) {
							reward = core.getMMOAPI().getReward("Mining", "MossyCobblestone");
						} else if (b.getType() == Material.PACKED_ICE) {
							reward = core.getMMOAPI().getReward("Mining", "PackedIce");
						} else if (b.getType() == Material.RED_SANDSTONE) {
							reward = core.getMMOAPI().getReward("Mining", "RedSandstone");
						} else if (b.getType() == Material.STAINED_CLAY) {
							reward = core.getMMOAPI().getReward("Mining", "StainedClay");
						} else if (b.getType() == Material.ENDER_STONE) {
							reward = core.getMMOAPI().getReward("Mining", "EndStone");
						} else if (b.getType() == Material.PRISMARINE) {
							reward = core.getMMOAPI().getReward("Mining", "Prismarine");
						} else if (b.getType() == Material.PURPUR_BLOCK) {
							reward = core.getMMOAPI().getReward("Mining", "Purpur");
						} else if (b.getType() == Material.OBSIDIAN) {
							reward = core.getMMOAPI().getReward("Mining", "Obsidian");
						}
						if (doubleDrop) {
							e.setCancelled(true);
							if ((b.getType() == Material.NETHERRACK) || (b.getType() == Material.SANDSTONE)
									|| (b.getType() == Material.STONE) || (b.getType() == Material.HARD_CLAY)
									|| (b.getType() == Material.MOSSY_COBBLESTONE)
									|| (b.getType() == Material.PACKED_ICE) || (b.getType() == Material.RED_SANDSTONE)
									|| (b.getType() == Material.STAINED_CLAY) || (b.getType() == Material.ENDER_STONE)
									|| (b.getType() == Material.PRISMARINE) || (b.getType() == Material.PURPUR_BLOCK)
									|| (b.getType() == Material.OBSIDIAN)) {
								int id = b.getTypeId();
								byte data = b.getData();
								b.setType(Material.AIR);
								ItemStack nis = core.getItemManager().createItemWithID(id, data, 2, "", null);
								b.getWorld().dropItem(p.getLocation(), nis);
							} else if (b.getType() == Material.IRON_ORE) {
								if ((is.getType() == Material.STONE_PICKAXE) || (is.getType() == Material.GOLD_PICKAXE)
										|| (is.getType() == Material.DIAMOND_PICKAXE)
										|| (is.getType() == Material.IRON_PICKAXE)) {
									if (im.getEnchants().containsKey(Enchantment.SILK_TOUCH)) {
										e.setCancelled(false);
									} else {
										e.setCancelled(true);
										b.setType(Material.AIR);
										bloc.getWorld().dropItem(bloc, new ItemStack(Material.IRON_INGOT, 2));
									}
								} else {
									e.setCancelled(true);
									b.setType(Material.AIR);
								}
							} else if (b.getType() == Material.GOLD_ORE) {
								if ((is.getType() == Material.GOLD_PICKAXE)
										|| (is.getType() == Material.DIAMOND_PICKAXE)
										|| (is.getType() == Material.IRON_PICKAXE)) {
									if (im.getEnchants().containsKey(Enchantment.SILK_TOUCH)) {
										e.setCancelled(false);
									} else {
										e.setCancelled(true);
										b.setType(Material.AIR);
										bloc.getWorld().dropItem(bloc, new ItemStack(Material.GOLD_INGOT, 2));
									}
								} else {
									e.setCancelled(true);
									b.setType(Material.AIR);
								}
							}
						} else if (b.getType() == Material.IRON_ORE) {
							if ((is.getType() == Material.STONE_PICKAXE) || (is.getType() == Material.GOLD_PICKAXE)
									|| (is.getType() == Material.DIAMOND_PICKAXE)
									|| (is.getType() == Material.IRON_PICKAXE)) {
								if (im.getEnchants().containsKey(Enchantment.SILK_TOUCH)) {
									e.setCancelled(false);
								} else {
									e.setCancelled(true);
									b.setType(Material.AIR);
									bloc.getWorld().dropItem(bloc, new ItemStack(Material.IRON_INGOT, 1));
								}
							} else {
								e.setCancelled(true);
								b.setType(Material.AIR);
							}
						} else if (b.getType() == Material.GOLD_ORE) {
							if ((is.getType() == Material.GOLD_PICKAXE) || (is.getType() == Material.DIAMOND_PICKAXE)
									|| (is.getType() == Material.IRON_PICKAXE)) {
								if (im.getEnchants().containsKey(Enchantment.SILK_TOUCH)) {
									e.setCancelled(false);
								} else {
									e.setCancelled(true);
									b.setType(Material.AIR);
									bloc.getWorld().dropItem(bloc, new ItemStack(Material.GOLD_INGOT, 1));
								}
							} else {
								e.setCancelled(true);
								b.setType(Material.AIR);
							}
						}

						core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP",
								Integer.valueOf(miningXP + reward));
						reward = 0;
						core.getMMOAPI().updateLevel("Mining", p);
					} else if (is.getType().toString().contains("AXE")) {
						int wcl = ((Integer) core.getPlayerAccount().get(uuid, "enPlayerData", "WoodcuttingLevel",
								"UUID")).intValue();
						boolean doubleDrop = false;
						int percentage = 0;
						if (wcl < 5) {
							doubleDrop = false;
						} else {
							percentage = wcl / 5;
							doubleDrop = core.getMMOAPI().randomBooleanByChance(percentage, 100 - percentage);
						}
						int wcr = 0;
						if (doubleDrop) {
							int id = b.getTypeId();
							byte data = b.getData();
							ItemStack nis = core.getItemManager().createItemWithID(id, data, 2, "", null);
							e.setCancelled(true);
							b.getWorld().dropItem(bloc, nis);
							if (id == 5) {
								wcr = core.getMMOAPI().getReward("Woodcutting", "Plank");
							} else if ((id == 17) || (id == 162)) {
								wcr = core.getMMOAPI().getReward("Woodcutting", "Log");
							} else if ((id == 99) || (id == 100)) {
								wcr = core.getMMOAPI().getReward("Woodcutting", "MushroomLog");
							}
						} else if (b.getTypeId() == 5) {
							wcr = core.getMMOAPI().getReward("Woodcutting", "Plank");
						} else if ((b.getTypeId() == 17) || (b.getTypeId() == 162)) {
							wcr = core.getMMOAPI().getReward("Woodcutting", "Log");
						} else if ((b.getTypeId() == 99) || (b.getTypeId() == 100)) {
							wcr = core.getMMOAPI().getReward("Woodcutting", "MushroomLog");
						}

						core.getPlayerAccount().set(uuid, "enPlayerData", "WoodcuttingXP",
								Integer.valueOf(woodcuttingXP + wcr));
						core.getMMOAPI().updateLevel("Woodcutting", p);
					}
			}
		} else {
			e.setCancelled(false);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		String uuid = p.getUniqueId().toString();
		ItemStack is = e.getItem().getItemStack();
		ItemMeta im = is.getItemMeta();
		if (is.getType() == Material.IRON_INGOT) {
			if ((im.getDisplayName() == null) || (!im.getDisplayName().equals("§7§lIron Ingot"))) {
				im.setDisplayName("§7§lIron Ingot");
				is.setItemMeta(im);
				int mxp = ((Integer) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID")).intValue();
				int r = core.getMMOAPI().getReward("Mining", "IronOre");
				core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP",
						Integer.valueOf(mxp + r * is.getAmount()));
				core.getMMOAPI().updateLevel("Mining", p);
			}
		} else if (is.getType() == Material.GOLD_INGOT) {
			if ((im.getDisplayName() == null) || (!im.getDisplayName().equalsIgnoreCase("§6§lGold Ingot"))) {
				im.setDisplayName("§6§lGold Ingot");
				is.setItemMeta(im);
				int mxp = ((Integer) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID")).intValue();
				int r = core.getMMOAPI().getReward("Mining", "Gold");
				core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP",
						Integer.valueOf(mxp + r * is.getAmount()));
				core.getMMOAPI().updateLevel("Mining", p);
			}
		} else if (is.getType() == Material.COAL) {
			if ((im.getDisplayName() == null) || (!im.getDisplayName().equalsIgnoreCase("§0§lCoal"))) {
				im.setDisplayName("§0§lCoal");
				is.setItemMeta(im);
				int mxp = ((Integer) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID")).intValue();
				int r = core.getMMOAPI().getReward("Mining", "CoalOre");
				core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP",
						Integer.valueOf(mxp + r * is.getAmount()));
				core.getMMOAPI().updateLevel("Mining", p);
			}
		} else if (is.getType() == Material.QUARTZ) {
			if ((im.getDisplayName() == null) || (!im.getDisplayName().equalsIgnoreCase("§f§lNether Quartz"))) {
				im.setDisplayName("§f§lNether Quartz");
				is.setItemMeta(im);
				int mxp = ((Integer) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID")).intValue();
				int r = core.getMMOAPI().getReward("Mining", "QuartzOre");
				core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP",
						Integer.valueOf(mxp + r * is.getAmount()));
				core.getMMOAPI().updateLevel("Mining", p);
			}
		} else if (is.getType() == Material.REDSTONE) {
			if ((im.getDisplayName() == null) || (!im.getDisplayName().equalsIgnoreCase("§c§lRedstone"))) {
				im.setDisplayName("§c§lRedstone");
				is.setItemMeta(im);
				int mxp = ((Integer) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID")).intValue();
				int r = core.getMMOAPI().getReward("Mining", "RedstoneOre");
				core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP",
						Integer.valueOf(mxp + r * is.getAmount()));
				core.getMMOAPI().updateLevel("Mining", p);
			}
		} else if (is.getType() == Material.DIAMOND) {
			if ((im.getDisplayName() == null) || (!im.getDisplayName().equalsIgnoreCase("§b§lDiamond"))) {
				im.setDisplayName("§b§lDiamond");
				is.setItemMeta(im);
				int mxp = ((Integer) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID")).intValue();
				int r = core.getMMOAPI().getReward("Mining", "Diamond");
				core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP",
						Integer.valueOf(mxp + r * is.getAmount()));
				core.getMMOAPI().updateLevel("Mining", p);
			}
		} else if (is.getType() == Material.EMERALD) {
			if ((im.getDisplayName() == null) || (!im.getDisplayName().equalsIgnoreCase("§a§lEmerald"))) {
				im.setDisplayName("§a§lEmerald");
				is.setItemMeta(im);
				int mxp = ((Integer) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID")).intValue();
				int r = core.getMMOAPI().getReward("Mining", "Emerald");
				core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP",
						Integer.valueOf(mxp + r * is.getAmount()));
				core.getMMOAPI().updateLevel("Mining", p);
			}
		} else if ((is.getTypeId() == 351) && (is.getData().getData() == 4)
				&& ((im.getDisplayName() == null) || (!im.getDisplayName().equalsIgnoreCase("§1§lLapis Lazuli")))) {
			im.setDisplayName("§1§lLapis Lazuli");
			is.setItemMeta(im);
			int mxp = ((Integer) core.getPlayerAccount().get(uuid, "enPlayerData", "MiningXP", "UUID")).intValue();
			int r = core.getMMOAPI().getReward("Mining", "Lapis");
			core.getPlayerAccount().set(uuid, "enPlayerData", "MiningXP", Integer.valueOf(mxp + r * is.getAmount()));
			core.getMMOAPI().updateLevel("Mining", p);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onFall(EntityDamageEvent e) {
		if (((e.getEntity() instanceof Player))
				&& (e.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL)) {
			final Player p = (Player) e.getEntity();
			double finalDamage = e.getFinalDamage();
			double hp = p.getHealth();

			if ((roll.containsKey(p.getUniqueId().toString()))
					&& (!((Boolean) roll.get(p.getUniqueId().toString())).booleanValue()))
				if (p.isSneaking()) {
					int cooldown = core.getMMOAPI().cooldown("Agility", "Roll");
					e.setCancelled(true);
					e.setDamage(0.0D);
					p.sendMessage(core.getPrefix() + "§cYou have just cancelled a fall damage!");
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
						public void run() {
							if ((roll.containsKey(p.getUniqueId().toString()))
									&& (((Boolean) roll.get(p.getUniqueId().toString())).booleanValue())) {
								roll.remove(p.getUniqueId().toString(), roll.get(p.getUniqueId().toString()));
								if (p != null) {
									p.sendMessage(core.getPrefix() + "§aYou can now use Roll ability again!");
								}
							}
						}
					}, cooldown * 20L);
				} else {
					e.setCancelled(false);
				}
			int agilityLevel = ((Integer) core.getPlayerAccount().get(p.getUniqueId().toString(), "enPlayerData",
					"AgilityLevel", "UUID")).intValue();
			int percentage = 0;
			if (agilityLevel < 10) {
				percentage = 5;
			} else if (agilityLevel >= 10) {
				percentage = 5 + agilityLevel / 10;
			}
			boolean reducedFallDamage = core.getMMOAPI().randomBooleanByChance(percentage, 100 - percentage);
			if (!reducedFallDamage) {
				if (hp - finalDamage > 0.0D) {
					double multiplier = core.getMMOAPI().getReward("Agility", "Multiplier");
					double reward = finalDamage / 2.0D * multiplier;
					int xp = ((Integer) core.getPlayerAccount().get(p.getUniqueId().toString(), "enPlayerData",
							"AgilityXP", "UUID")).intValue();
					core.getPlayerAccount().set(p.getUniqueId().toString(), "enPlayerData", "AgilityXP",
							Double.valueOf(xp + reward));
					core.getMMOAPI().updateLevel("Agility", p);
				}
			} else {
				e.setCancelled(true);
				int dividedBy = 100 / core.getMMOAPI().getLessDamageForAgility();
				p.damage(finalDamage / dividedBy);
				p.sendMessage(core.getPrefix() + "§cYou just got lucky and reduced the fall damage! :)");
				double multiplier = core.getMMOAPI().getReward("Agility", "Multiplier");
				double reward = finalDamage / 2.0D * multiplier / dividedBy;
				int xp = ((Integer) core.getPlayerAccount().get(p.getUniqueId().toString(), "enPlayerData", "AgilityXP",
						"UUID")).intValue();
				core.getPlayerAccount().set(p.getUniqueId().toString(), "enPlayerData", "AgilityXP",
						Double.valueOf(xp + reward));
				core.getMMOAPI().updateLevel("Agility", p);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Item i = e.getItemDrop();
		if (i.getType() == EntityType.DROPPED_ITEM) {
			ItemStack is = i.getItemStack();
			ItemMeta im = is.getItemMeta();
			if (is.getType() == Material.IRON_INGOT) {
				if ((im.getDisplayName() == null) || (im.getDisplayName().equals("Iron Ingot"))) {
					im.setDisplayName("§7§lIron Ingot");
					is.setItemMeta(im);
				}
			} else if (is.getType() == Material.COAL) {
				if ((im.getDisplayName() == null) || (im.getDisplayName().equals("Coal"))) {
					im.setDisplayName("§0§lCoal");
					is.setItemMeta(im);
				}
			} else if (is.getType() == Material.GOLD_INGOT) {
				if ((im.getDisplayName() == null) || (im.getDisplayName().equals("Gold Ingot"))) {
					im.setDisplayName("§6§lGold Ingot");
					is.setItemMeta(im);
				}
			} else if (is.getType() == Material.EMERALD) {
				if ((im.getDisplayName() == null) || (im.getDisplayName().equals("Emerald"))) {
					im.setDisplayName("§a§lEmerald");
					is.setItemMeta(im);
				}
			} else if (is.getType() == Material.DIAMOND) {
				if ((im.getDisplayName() == null) || (im.getDisplayName().equals("Diamond"))) {
					im.setDisplayName("§b§lDiamond");
					is.setItemMeta(im);
				}
			} else if (is.getType() == Material.QUARTZ) {
				if ((im.getDisplayName() == null) || (im.getDisplayName().equals("Nether Quartz"))) {
					im.setDisplayName("§f§lNether Quartz");
					is.setItemMeta(im);
				}
			} else if (is.getType() == Material.REDSTONE) {
				if ((im.getDisplayName() == null) || (im.getDisplayName().equals("Redstone"))) {
					im.setDisplayName("§c§lRedstone");
					is.setItemMeta(im);
				}
			} else if ((is.getTypeId() == 351) && (is.getData().getData() == 4)
					&& ((im.getDisplayName() == null) || (im.getDisplayName().equals("Lapis Lazuli")))) {
				im.setDisplayName("§1§lLapis Lazuli");
				is.setItemMeta(im);
			}
		}
	}

	@EventHandler
	public void onItemSwitch(PlayerItemHeldEvent e) {
		int newSlot = e.getNewSlot();
		Player p = e.getPlayer();
		String uuid = p.getUniqueId().toString();
		if ((p.getInventory().getItem(newSlot) != null)
				&& (p.getInventory().getItem(newSlot).getType() == Material.TORCH)) {
			if ((core.getMMOAPI().hasAbilityUnlocked(uuid, "Mining", "MinersVision"))
					&& (!minersVision.contains(uuid))) {
				minersVision.add(uuid);
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
				p.sendMessage(core.getPrefix() + "§7Miner's vision §aability enabled.");
			}

		} else if (minersVision.contains(uuid)) {
			minersVision.remove(uuid);
			if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION))
				p.removePotionEffect(PotionEffectType.NIGHT_VISION);
			p.sendMessage(core.getPrefix() + "§7Miner's vision §aability disabled.");
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onFish(PlayerFishEvent e) {
		final Player p = e.getPlayer();
		if ((e.getState() == State.CAUGHT_FISH)  && ((e.getCaught() instanceof Item))) {
			int xp = (int) core.getPlayerAccount().get(p.getUniqueId().toString(), "enPlayerData", "FishingXP", "UUID");
			org.bukkit.entity.Entity en = e.getCaught();
			String reward = core.getMMOAPI().randomFishingRewardByChance();
			Item caught = (Item) en;

			ItemStack defaultFish = null;
			if (core.getMMOAPI().hasAbilityUnlocked(p.getUniqueId().toString(), "Fishing", "CookedFish"))
				defaultFish = core.getItemManager().createItemWithMaterial(Material.COOKED_FISH, 0, 1, "", null);
			else
				defaultFish = core.getItemManager().createItemWithMaterial(Material.RAW_FISH, 0, 1, "", null);
			ItemMeta rfm = defaultFish.getItemMeta();
			if (reward.equals("Salmon")) {
				ItemStack salmon = null;
				if (core.getMMOAPI().hasAbilityUnlocked(p.getUniqueId().toString(), "Fishing", "CookedFish"))
					salmon = core.getItemManager().createItemWithID(350, 1, 1, "", null);
				else
					salmon = core.getItemManager().createItemWithID(349, 1, 1, "", null);
				caught.setItemStack(salmon);
				xp += core.getMMOAPI().getReward("Fishing", "Salmon");
				p.sendMessage(core.getPrefix() + "§aYou have caught a §7salmon§a!");
			} else if (reward.equals("Tuna")) {
				rfm.setDisplayName("§fTuna");
				defaultFish.setItemMeta(rfm);
				caught.setItemStack(defaultFish);
				p.sendMessage(core.getPrefix() + "§aYou have caught a §7tuna§a!");
				xp += core.getMMOAPI().getReward("Fishing", "Tuna");
			} else if (reward.equals("Pirate Chest")) {
				e.setCancelled(true);
				Location loc = p.getLocation();
				FireworkEffect f = null;
				FireworkEffect.Builder fb = FireworkEffect.builder();
				Random r = new Random();
				fb.withColor(org.bukkit.Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
				org.bukkit.FireworkEffect.Type[] type = org.bukkit.FireworkEffect.Type.values();
				fb.with(type[r.nextInt(type.length)]);
				f = fb.build();
				Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
				FireworkMeta fwm = fw.getFireworkMeta();
				fwm.clearEffects();
				fwm.addEffect(f);
				fw.setFireworkMeta(fwm);

				p.sendMessage(core.getPrefix()
						+ "§aAye aye, captain! You have just found an §7old mysterious pirate chest§a!");
				xp += core.getMMOAPI().getReward("Fishing", "PirateChest");
			} else if (reward.equals("Boot")) {
				e.setCancelled(true);
				xp += core.getMMOAPI().getReward("Fishing", "Boot");
				p.sendMessage(core.getPrefix()
						+ "§aYou have caught an §7old nasty boot§a! We're not giving it to you for sure.");
			} else if (reward.equals("Pearl")) {
				ItemStack pearl = core.getItemManager().createItemWithMaterial(Material.ENDER_PEARL, 0, 1, "", null);
				caught.setItemStack(pearl);
				p.sendMessage(core.getPrefix() + "§aYou have found a §7pearl§a!");
				xp += core.getMMOAPI().getReward("Fishing", "Pearl");
			} else if (reward.equals("Great White Shark")) {
				e.setCancelled(true);
				p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
				p.setVelocity(p.getLocation().getDirection().multiply(2));
				p.sendMessage(core.getPrefix() + "§aYou have been pulled by a §7great white shark§a!");
				xp += core.getMMOAPI().getReward("Fishing", "GreatWhiteShark");
			} else if (reward.equals("Anchovy")) {
				rfm.setDisplayName("§fAnchovy");
				defaultFish.setItemMeta(rfm);
				caught.setItemStack(defaultFish);
				p.sendMessage(core.getPrefix() + "§aYou have caught an §7anchovy§a!");
				xp += core.getMMOAPI().getReward("Fishing", "Anchovy");
			} else if (reward.equals("Pufferfish")) {
				caught.setItemStack(core.getItemManager().createItemWithID(349, 3, 1, "", null));
				p.damage(4.0D);
				p.sendMessage(core.getPrefix() + "§aYou just got stung by a §7pufferfish§a.");
				xp += core.getMMOAPI().getReward("Fishing", "Pufferfish");
			} else if (reward.equals("Mackerel")) {
				rfm.setDisplayName("§fMackerel");
				defaultFish.setItemMeta(rfm);
				caught.setItemStack(defaultFish);
				p.sendMessage(core.getPrefix() + "§aYou have caught a §7mackerel§a!");
				xp += core.getMMOAPI().getReward("Fishing", "Mackerel");
			} else if (reward.equals("Clownfish")) {
				ItemStack clownfish = core.getItemManager().createItemWithID(349, 2, 1, "", null);
				caught.setItemStack(clownfish);
				p.sendMessage(core.getPrefix() + "§aYou have caught a §7clownfish§a.");
				xp += core.getMMOAPI().getReward("Fishing", "Clownfish");
			} else if (reward.equals("Crab")) {
				e.setCancelled(true);
				p.sendMessage(core.getPrefix() + "§aA crab has almost pulled off one of your fingers and it's bleeding!");
				xp += core.getMMOAPI().getReward("Fishing", "Crab");
				Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(core, new BukkitRunnable() {
					int hd = 0;
					@Override
					public void run() {
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
							public void run() {
								if(!(hd >= 3)) {
									hd += 1;
									p.damage(2.0D);
								}
							}

						}, 20L);
					}
					
				}, 0L, 5*20L);
			} else if (reward.equals("Jellyfish")) {
				e.setCancelled(true);
				p.damage(6.0D);
				p.sendMessage(core.getPrefix() + "§7Jellyfish §ahas stung you with its tentacles.");
				xp += core.getMMOAPI().getReward("Fishing", "Jellyfish");
			} else if (reward.equals("Squid")) {
				e.setCancelled(true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 2));
				p.sendMessage(core.getPrefix() + "§aThe §7squid §ahas injected ink into your face!");
				xp += core.getMMOAPI().getReward("Fishing", "Squid");
			} else if (reward.equals("Whale")) {
				e.setCancelled(true);
				p.getInventory().removeItem(new ItemStack[] { p.getItemInHand() });
				p.sendMessage(core.getPrefix()
						+ "§aUnfortunatelly, the §7whale §awas too strong and broke your fishing rod.");
				xp += core.getMMOAPI().getReward("Fishing", "Whale");
			} else if (reward.equals("Flatfish")) {
				rfm.setDisplayName("§fFlatfish");
				defaultFish.setItemMeta(rfm);
				caught.setItemStack(defaultFish);
				p.sendMessage(core.getPrefix() + "§aYou have caught a §7flatfish§a!");
				xp += core.getMMOAPI().getReward("Fishing", "Flatfish");
			} else if (reward.equals("Golden Helmet")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.GOLD_HELMET, 0, 1, "", null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found a §7golden helmet§a!");
				xp += core.getMMOAPI().getReward("Fishing", "GoldArmorPart");
			} else if (reward.equals("Golden Chestplate")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.GOLD_CHESTPLATE, 0, 1, "", null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found a §7golden chestplate§a!");
				xp += core.getMMOAPI().getReward("Fishing", "GoldArmorPart");
			} else if (reward.equals("Golden Leggings")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.GOLD_LEGGINGS, 0, 1, "", null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found §7golden leggings§a!");
				xp += core.getMMOAPI().getReward("Fishing", "GoldArmorPart");
			} else if (reward.equals("Golden Boots")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.GOLD_BOOTS, 0, 1, "", null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found §7golden boots§a!");
				xp += core.getMMOAPI().getReward("Fishing", "GoldArmorPart");
			} else if (reward.equals("Iron Helmet")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.IRON_HELMET, 0, 1, "", null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found an §7iron helmet§a!");
				xp += core.getMMOAPI().getReward("Fishing", "IronArmorPart");
			} else if (reward.equals("Iron Chestplate")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.IRON_CHESTPLATE, 0, 1, "", null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found an §7iron chestplate§a!");
				xp += core.getMMOAPI().getReward("Fishing", "IronArmorPart");
			} else if (reward.equals("Iron Leggings")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.IRON_LEGGINGS, 0, 1, "", null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found §7iron leggings§a!");
				xp += core.getMMOAPI().getReward("Fishing", "IronArmorPart");
			} else if (reward.equals("Iron Boots")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.IRON_BOOTS, 0, 1, "", null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found §7iron boots§a!");
				xp += core.getMMOAPI().getReward("Fishing", "IronArmorPart");
			} else if (reward.equals("Diamond Helmet")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.DIAMOND_HELMET, 0, 1, "", null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found a §7diamond helmet§a!");
				xp += core.getMMOAPI().getReward("Fishing", "DiamondArmorPart");
			} else if (reward.equals("Diamond Chestplate")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.DIAMOND_CHESTPLATE, 0, 1, "",
						null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found a §7diamond chestplate§a!");
				xp += core.getMMOAPI().getReward("Fishing", "DiamondArmorPart");
			} else if (reward.equals("Diamond Leggings")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.DIAMOND_LEGGINGS, 0, 1, "",
						null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found §7diamond leggings§a!");
				xp += core.getMMOAPI().getReward("Fishing", "DiamondArmorPart");
			} else if (reward.equals("Diamond Boots")) {
				ItemStack helm = core.getItemManager().createItemWithMaterial(Material.DIAMOND_BOOTS, 0, 1, "", null);
				caught.setItemStack(helm);
				p.sendMessage(core.getPrefix() + "§aYou have just found §7diamond boots§a!");
				xp += core.getMMOAPI().getReward("Fishing", "DiamondArmorPart");
			} else if (reward.equalsIgnoreCase("Gas Field")) {
				e.setCancelled(true);
				p.getLocation().getWorld().spawnEntity(p.getLocation().add(0.0D, 20.0D, 0.0D), EntityType.PRIMED_TNT);
				p.sendMessage(core.getPrefix() + "§aOooops! You've accidentally thrown your hook on a gas field!");
				xp += core.getMMOAPI().getReward("Fishing", "GasBell");
			}
			core.getPlayerAccount().set(p.getUniqueId().toString(), "enPlayerData", "FishingXP", Integer.valueOf(xp));
			core.getMMOAPI().updateLevel("Fishing", p);
		} else if (e.getState() == State.CAUGHT_ENTITY && e.getCaught() instanceof Player) {
			Player t = (Player) e.getCaught();
			if (core.getMMOAPI().hasAbilityUnlocked(p.getUniqueId().toString(), "Fishing", "Launch")) {
				if(!launch.contains(p.getUniqueId().toString())) {
					launch.add(p.getUniqueId().toString());
					Location nloc = t.getLocation().add(0, 15, 0);
					t.setVelocity(nloc.toVector());
					core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable(){

						@Override
						public void run() {
							if(p != null) p.sendMessage(core.getPrefix() + "§aYou can use the launch ability again!");
							launch.remove(p.getUniqueId().toString());
						}
						
					}, 5*60*20L);
				}else{
					p.sendMessage(core.getPrefix() + "§cYou can not use launch ability right now.");
				}
			}
		}
	}

	@EventHandler
	public void onShift(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		String uuid = p.getUniqueId().toString();
		if (core.getMMOAPI().hasAbilityUnlocked(uuid, "Agility", "Roll")) {
			if (e.isSneaking()) {
				if (!roll.containsKey(uuid)) {
					roll.put(uuid, Boolean.valueOf(false));
				}
			} else if ((roll.containsKey(uuid)) && (!((Boolean) roll.get(uuid)).booleanValue())) {
				roll.remove(uuid, roll.get(uuid));
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onItemInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack is = e.getItem();
		if(is != null && is.getType() != Material.AIR) {
			ItemMeta im = is.getItemMeta();
			if(is.getTypeId() == 383 && is.getData().getData() == 50) {
				if(im.getDisplayName().equals("§2§lCreeper Army")) {
					e.setCancelled(true);
					Location pLoc = p.getLocation();
					World w = pLoc.getWorld();
					w.spawnEntity(pLoc.add(4, 0, 0), EntityType.CREEPER);
					w.spawnEntity(pLoc.add(0, 0, 4), EntityType.CREEPER);
					w.spawnEntity(pLoc.add(4, 0, 4), EntityType.CREEPER);
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
				}else if(im.getDisplayName().equals("§2§lCharged Creeper")) {
					e.setCancelled(true);
					Location pLoc = p.getLocation();
					World w = pLoc.getWorld();
					Creeper cr = (Creeper) w.spawnEntity(pLoc.add(3, 0, 0), EntityType.CREEPER);
					cr.setPowered(true);
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
				}
			}
		}else{
			e.setCancelled(false);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void inventoryInteractions(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getAction() != org.bukkit.event.inventory.InventoryAction.PLACE_ALL) {
			if ((e.getClickedInventory() != null)
					&& (e.getClickedInventory().getType() != org.bukkit.event.inventory.InventoryType.PLAYER)
					&& (e.getClickedInventory().getType() == org.bukkit.event.inventory.InventoryType.CHEST)
					&& (e.getClickedInventory().getTitle() != null)) {
				if (e.getClickedInventory().getTitle().equals("§6§lVote or spend tokens!")) {
					e.setCancelled(true);
					ItemStack is = e.getCurrentItem();
					ItemMeta im = is.getItemMeta();
					if (is.getType() == Material.BARRIER) {
						p.getOpenInventory().close();
					} else if ((is.getType() == Material.DIAMOND) && (im.hasDisplayName()) && (im.getDisplayName().equals("§a§lSpend Tokens"))) {
						Inventory inv = Bukkit.createInventory(null, 54, "§6§lSpend Tokens");
						ItemStack creeperEgg1 = core.getItemManager().createItemWithID(383, 50, 1, "§2§lCreeper Army", "", "§6§lPrice: §720");
						ItemStack creeperEgg2 = core.getItemManager().createItemWithID(383, 50, 1, "§2§lCharged Creeper", "", "§6§lPrice: §725");
						ItemStack compass = core.getItemManager().createItemWithMaterial(Material.COMPASS, 0, 1, "§f§lMagic Compass", "", "§6§lPrice: §7150");
						ItemStack tnt = core.getItemManager().createItemWithMaterial(Material.TNT, 0, 1, "§c§lNuke", "", "§6§lPrice: §735");
						ItemStack flintAndSteel = core.getItemManager().createItemWithMaterial(Material.FLINT_AND_STEEL, 0, 1, "§b§lLightning Powder", "", "§6§lPrice: §740");
						ItemStack shears = core.getItemManager().createItemWithMaterial(Material.SHEARS, 0, 1, "§f§lEquipment Disrober", "", "§6§lPrice: §7200");
						ItemStack sponge = core.getItemManager().createItemWithMaterial(Material.SPONGE, 0, 1, "§e§lSponge", "", "§6§lPrice: §75");
						ItemStack pearl = core.getItemManager().createItemWithMaterial(Material.ENDER_PEARL, 0, 1, "§2§lEnder Pearl", "", "§6§lPrice: §75");
						ItemStack exp = core.getItemManager().createItemWithMaterial(Material.EXP_BOTTLE, 0, 32, "§a§lEXP Bottle", "", "§6§lPrice: §710");
						ItemStack leatherHelmet = core.getItemManager().createItemWithMaterial(Material.LEATHER_HELMET, 0, 1, "§3§lPoseidon's Hat", "", "§6§lPrice: §750");
						ItemStack ironHelmet = core.getItemManager().createItemWithMaterial(Material.IRON_HELMET, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack ironChestplate = core.getItemManager().createItemWithMaterial(Material.IRON_CHESTPLATE, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack ironLeggings = core.getItemManager().createItemWithMaterial(Material.IRON_LEGGINGS, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack ironBoots = core.getItemManager().createItemWithMaterial(Material.IRON_BOOTS, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack paper1 = core.getItemManager().createItemWithMaterial(Material.PAPER, 0, 1, "§a§l10 MMO Points", "", "§6§lPrice: §710");
						ItemStack paper2 = core.getItemManager().createItemWithMaterial(Material.PAPER, 0, 1, "§a§l25 MMO Points", "", "§6§lPrice: §720");
						ItemStack paper3 = core.getItemManager().createItemWithMaterial(Material.PAPER, 0, 1, "§a§l50 MMO Points", "", "§6§lPrice: §730");
						ItemStack bow = core.getItemManager().createItemWithMaterial(Material.BOW, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack pickaxe = core.getItemManager().createItemWithMaterial(Material.DIAMOND_PICKAXE, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack axe = core.getItemManager().createItemWithMaterial(Material.DIAMOND_AXE, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack spade = core.getItemManager().createItemWithMaterial(Material.DIAMOND_SPADE, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack sword = core.getItemManager().createItemWithMaterial(Material.DIAMOND_SWORD, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack map1 = core.getItemManager().createItemWithMaterial(Material.MAP, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack map2 = core.getItemManager().createItemWithMaterial(Material.MAP, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack map3 = core.getItemManager().createItemWithMaterial(Material.MAP, 0, 1, "§4§lTODO", "", "§6§lPrice: §7UNDEFINED");
						ItemStack barrier = core.getItemManager().createItemWithMaterial(Material.BARRIER, 0, 1, "§c§lEXIT", null);
						inv.setItem(0, creeperEgg1);
						inv.setItem(1, creeperEgg2);
						inv.setItem(2, compass);
						inv.setItem(3, tnt);
						inv.setItem(4, flintAndSteel);
						inv.setItem(5, shears);
						inv.setItem(6, sponge);
						inv.setItem(7, pearl);
						inv.setItem(8, exp);
						inv.setItem(18, leatherHelmet);
						inv.setItem(19, ironHelmet);
						inv.setItem(20, ironChestplate);
						inv.setItem(21, ironLeggings);
						inv.setItem(22, ironBoots);
						inv.setItem(24, paper1);
						inv.setItem(25, paper2);
						inv.setItem(26, paper3);
						inv.setItem(27, bow);
						inv.setItem(28, pickaxe);
						inv.setItem(29, axe);
						inv.setItem(30, spade);
						inv.setItem(31, sword);
						inv.setItem(33, map1);
						inv.setItem(34, map2);
						inv.setItem(35, map3);
						inv.setItem(49, barrier);
						p.openInventory(inv);
					} else if (is.getType() == Material.PAPER && im.hasDisplayName() && im.getDisplayName().equals("§a§lVote Links")) {
						p.getOpenInventory().close();
						sendVotingLinks(p);
					}
				} else if (e.getClickedInventory().getTitle().equals("§6§lSpend Tokens")) {
					e.setCancelled(true);
					int tokens = core.getVoteTokensHelper().getTokens(p);
					ItemStack is = e.getCurrentItem();
					ItemMeta im = is.getItemMeta();
					if (is.getTypeId() == 383 && is.getData().getData() == 50) {
						if(im.hasDisplayName() && im.getDisplayName().equals("§2§lCreeper Army")) {
							if(tokens >= 20) {
								core.getVoteTokensHelper().removeTokens(p, 20);
								p.getInventory().addItem(is);
							}else{
								p.sendMessage(core.getPrefix() + "§cYou do not have enough tokens to purchase this item!");
							}
						}else if(im.hasDisplayName() && im.getDisplayName().equals("§2§lCharged Creeper")) {
							if(tokens >= 25) {
								core.getVoteTokensHelper().removeTokens(p, 25);
								p.getInventory().addItem(is);
							}else{
								p.sendMessage(core.getPrefix() + "§cYou do not have enough tokens to purchase this item!");
							}
						}
					}//else if(is.getType() == Material.COMPASS) {
//						if(tokens >= 150) {
//							core.getVoteTokensHelper().removeTokens(p, 150);
//							p.getInventory().addItem(is);
//						}else{
//							p.sendMessage(core.getPrefix() + "§cYou do not have enough tokens to purchase this item!");
//						}
//					}else if(is.getType() == Material.TNT) {
//						if(tokens >= 35) {
//							core.getVoteTokensHelper().removeTokens(p, 35);
//							p.getInventory().addItem(is);
//						}else{
//							p.sendMessage(core.getPrefix() + "§cYou do not have enough tokens to purchase this item!");
//						}
//					}else if(is.getType() == Material.FLINT_AND_STEEL) {
//						if(tokens >= 40) {
//							core.getVoteTokensHelper().removeTokens(p, 40);
//							p.getInventory().addItem(is);
//						}else{
//							p.sendMessage(core.getPrefix() + "§cYou do not have enough tokens to purchase this item!");
//						}
//					}else if(is.getType() == Material.SHEARS) {
//						if(tokens >= 200) {
//							core.getVoteTokensHelper().removeTokens(p, 200);
//							p.getInventory().addItem(is);
//						}else{
//							p.sendMessage(core.getPrefix() + "§cYou do not have enough tokens to purchase this item!");
//						}
//					}
					else if(is.getType() == Material.SPONGE) {
						if(tokens >= 5) {
							core.getVoteTokensHelper().removeTokens(p, 5);
							p.getInventory().addItem(core.getItemManager().createItemWithMaterial(Material.SPONGE, 0, 1, "", null));
						}else{
							p.sendMessage(core.getPrefix() + "§cYou do not have enough tokens to purchase this item!");
						}						p.getInventory().addItem(is);
					}else if(is.getType() == Material.ENDER_PEARL) {
						if(tokens >= 5) {
							core.getVoteTokensHelper().removeTokens(p, 5);
							p.getInventory().addItem(core.getItemManager().createItemWithMaterial(Material.ENDER_PEARL, 0, 1, "", null));
						}else{
							p.sendMessage(core.getPrefix() + "§cYou do not have enough tokens to purchase this item!");
						}
					}else if(is.getType() == Material.EXP_BOTTLE) {
						if(tokens >= 10) {
							core.getVoteTokensHelper().removeTokens(p, 10);
							p.getInventory().addItem(core.getItemManager().createItemWithMaterial(Material.EXP_BOTTLE, 0, 32, "", null));
						}else{
							p.sendMessage(core.getPrefix() + "§cYou do not have enough tokens to purchase this item!");
						}
					}//else if(is.getType() == Material.LEATHER_HELMET) {
//						if(tokens >= 50) {
//							core.getVoteTokensHelper().removeTokens(p, 50);
//							p.getInventory().addItem(is);
//						}else{
//							p.sendMessage(core.getPrefix() + "§cYou do not have enough tokens to purchase this item!");
//						}
			//		}
					else if(is.getType() == Material.BARRIER) {
						if(im.hasDisplayName() && im.getDisplayName().equals("§c§lEXIT")) {
							p.getOpenInventory().close();
						}
					}else{
						if(is != null) {
							p.sendMessage(core.getPrefix() + "§cThis shop is still under maintenance, so some items may not be purchasable.");
						}
					}

				} else if (e.getClickedInventory().getTitle().equals("§6§lVirtual Shop")) {
					e.setCancelled(true);
					ItemStack is = e.getCurrentItem();
					ItemMeta im = is.getItemMeta();
					if(is != null) {
						if(is.getType() != Material.BARRIER && is.getType() != Material.AIR) {
							if(is.getType() == Material.COBBLESTONE) {
								
							}
							ItemStack ita = core.getItemManager().createItemWithMaterial(is.getType(), is.getData().getData(), is.getAmount(), "", null);
							p.getInventory().addItem(ita);
						}else if(is.getType() == Material.BARRIER) {
							if(im.hasDisplayName() && im.getDisplayName().equals("§c§lEXIT")) {
								p.getOpenInventory().close();
							}
						}
					}
				}
			} else {
				e.setCancelled(false);
			}
		} else {
			e.setCancelled(false);
		}
	}
	
	//Other methods
	private void sendMotd(Player p) {
		p.sendMessage("§a§l§m---------------§2§l§m---------------");
		p.sendMessage("");
		p.sendMessage("           §f§l+ §a§lElerion §2§lNetwork §f§l+");
		p.sendMessage("");
		p.sendMessage("              §2§lSite §f§nwww.elerion.net");
		p.sendMessage("            §2§lStore §f§nbuy.elerion.net");
		p.sendMessage("          §2§lVote §f§nwww.elerion.net/vote");
		p.sendMessage("");
		p.sendMessage("§2§l§m---------------§a§l§m---------------");
	}
	
	private void sendVotingLinks(Player p) {
		List<String> voteLinks = new ArrayList<String>();
		FileConfiguration cfg = core.getFileManager().getConfiguration("settings.yml", "plugins//ENFactions//");
		p.sendMessage(core.getPrefix() + "§cYou can currently vote on these links:");
		if(cfg.isConfigurationSection("Vote.Links")) {
			voteLinks = cfg.getStringList("Vote.Links");
			for(String vote : voteLinks) {
				p.sendMessage("  §7- §6" + vote);
			}
		}
	}
}
