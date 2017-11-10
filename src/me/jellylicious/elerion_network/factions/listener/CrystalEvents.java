package me.jellylicious.elerion_network.factions.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import me.jellylicious.elerion_network.factions.Core;
import me.jellylicious.elerion_network.factions.api.tribes.TribesAPI.EffectType;

public class CrystalEvents implements Listener {

	Core core;
	public CrystalEvents(Core core) {
		this.core = core;
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		if(e.getEntity() instanceof EnderCrystal) {
			if(!e.getEntity().getWorld().getName().equalsIgnoreCase("world_the_end")) {
				e.setCancelled(true);
			}
		}else if(e.getEntity() instanceof TNTPrimed) {
			for(Entity en : e.getEntity().getNearbyEntities(5, 5, 5)) {
				if(en instanceof EnderCrystal || en instanceof ArmorStand) {
					en.getLocation().getWorld().spawnEntity(en.getLocation(), en.getType());
				}
			}
		}
	}
	
	//DELETE IF USELESS
	
	@EventHandler
	public void onExplode(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof EnderCrystal) {
			if(!e.getEntity().getWorld().getName().equalsIgnoreCase("world_the_end")) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		Player p = (Player) e.getPlayer();
		String playerUUID = p.getUniqueId().toString();
		Entity en = e.getRightClicked();
		if(en instanceof EnderCrystal) {
			EnderCrystal ec = (EnderCrystal) en;
			if(!ec.getLocation().getWorld().getName().equalsIgnoreCase("world_the_end")) {
				Location low = core.getTribesAPI().getMinLocationOfBox(ec.getLocation());
				Location high = core.getTribesAPI().getMaxLocationOfBox(ec.getLocation());
				String claim = core.getTribesAPI().getClaim(low, high);
				if(claim != null) {
					String owner = core.getTribesAPI().getClaimOwner(claim);
					if(core.getTribesAPI().tribeExists(owner)) {
						String tribe = core.getTribesAPI().getPlayerTribe(playerUUID);
						if(tribe != null) {
							tribe = tribe.toUpperCase();
							if(core.getTribesAPI().tribeExists(tribe)) {
								if(owner.equals(core.getTribesAPI().getPlayerTribe(playerUUID))) {
									Inventory inv = Bukkit.createInventory(null, 36, "§aCrystal Management:");
									ItemStack lethality = core.getItemManager().createItemWithMaterial(Material.GLASS_BOTTLE, 0, 1, "§6§lLethality", "§cTODO");
									ItemStack precision = core.getItemManager().createItemWithMaterial(Material.GLASS_BOTTLE, 0, 1, "§6§lPrecision", "§cTODO");
									ItemStack healingaura = core.getItemManager().createItemWithMaterial(Material.GLASS_BOTTLE, 0, 1, "§6§lHealing Aura", "§cTODO");
									ItemStack swiftfeet = core.getItemManager().createItemWithMaterial(Material.GLASS_BOTTLE, 0, 1, "§6§lSwift Feet", "§cTODO");
									ItemStack liftoff = core.getItemManager().createItemWithMaterial(Material.GLASS_BOTTLE, 0, 1, "§6§lLiftoff", "§cTODO");
									ItemStack minersadrenaline = core.getItemManager().createItemWithMaterial(Material.GLASS_BOTTLE, 0, 1, "§6§lMiner's Adrenaline", "§cTODO");
									ItemStack hemorrhage = core.getItemManager().createItemWithMaterial(Material.GLASS_BOTTLE, 0, 1, "§6§lHemorrhage", "§cTODO");
										
									ItemStack grayWindow = core.getItemManager().createItemWithID(160, 7, 1, "§aActivate", "§cTODO");
									ItemStack limeWindow = core.getItemManager().createItemWithID(160, 5, 1, "§cDeactivate", "§cTODO");
											
									ItemStack diaSword = core.getItemManager().createItemWithMaterial(Material.DIAMOND_SWORD, 0, 1, " ", "§cTODO");
									ItemStack bookAndQuill = core.getItemManager().createItemWithMaterial(Material.BOOK_AND_QUILL, 0, 1, " ", "§cTODO");
									ItemStack head = core.getItemManager().createItemWithMaterial(Material.SKULL_ITEM, 0, 1, " ", "§cTODO");
									ItemStack barrier = core.getItemManager().createItemWithMaterial(Material.BARRIER, 0, 1, "§c§lEXIT", "§cIf you want to exit this management", "§cinventory, feel free to click me!");
											
									inv.setItem(1, lethality);
									inv.setItem(2, precision);
									inv.setItem(3, healingaura);
									inv.setItem(4, swiftfeet);
									inv.setItem(5, liftoff);
									inv.setItem(6, minersadrenaline);
									inv.setItem(7, hemorrhage);
									if(core.getTribesAPI().isEffectActive(owner, EffectType.LETHALITY)) {
										inv.setItem(10, limeWindow);
									}else{
										inv.setItem(10, grayWindow);
									}
									if(core.getTribesAPI().isEffectActive(owner, EffectType.PRECISION)) {
										inv.setItem(11, limeWindow);
									}else{
										inv.setItem(11, grayWindow);
									}
									if(core.getTribesAPI().isEffectActive(owner, EffectType.HEALING_AURA)) {
										inv.setItem(12, limeWindow);
									}else{
										inv.setItem(12, grayWindow);
									}
									if(core.getTribesAPI().isEffectActive(owner, EffectType.SWIFT_FEET)) {
										inv.setItem(13, limeWindow);
									}else{
										inv.setItem(13, grayWindow);
									}
									if(core.getTribesAPI().isEffectActive(owner, EffectType.LIFTOFF)) {
										inv.setItem(14, limeWindow);
									}else{
										inv.setItem(14, grayWindow);
									}
									if(core.getTribesAPI().isEffectActive(owner, EffectType.MINERS_ADRENALINE)) {
										inv.setItem(15, limeWindow);
									}else{
										inv.setItem(15, grayWindow);
									}
									if(core.getTribesAPI().isEffectActive(owner, EffectType.HEMORRHAGE)) {
										inv.setItem(16, limeWindow);
									}else{
										inv.setItem(16, grayWindow);
									}
										
									inv.setItem(30, diaSword);
									inv.setItem(31, head);
									inv.setItem(32, bookAndQuill);
									inv.setItem(35, barrier);
									p.openInventory(inv);
								}else{
									p.sendMessage(core.getPrefix() + "§cYou do not belong to the tribe that owns this crystal.");
								}
							}else{
								p.sendMessage(core.getPrefix() + "§cTribe you should belong to doesn't exist.");
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cTribe that owns this crystal does not exist.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cThis crystal was not claimed yet, so you can not manage it.");
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		String uuid = p.getUniqueId().toString();
		String tribe = core.getTribesAPI().getPlayerTribe(uuid);
		if(e.getAction() != InventoryAction.PLACE_ALL) {
			if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER && e.getClickedInventory().getType() == InventoryType.CHEST && e.getClickedInventory().getTitle() != null && e.getClickedInventory().getTitle().equals("§aCrystal Management:")) {
				if(tribe != null) {
					tribe = tribe.toUpperCase();
					if(core.getTribesAPI().tribeExists(tribe)) {
							ItemStack is = e.getCurrentItem();
							ItemMeta im = is.getItemMeta();
							e.setCancelled(true);
							if(is.getType() == Material.STAINED_GLASS_PANE) {
								if(core.getTribesAPI().tribeChieftain(tribe).equals(uuid) || core.getTribesAPI().tribeChampions(tribe).contains(uuid)) {
									ItemStack grayWindow = core.getItemManager().createItemWithID(160, 7, 1, "§aActivate", "§cTODO");
									ItemStack limeWindow = core.getItemManager().createItemWithID(160, 5, 1, "§cDeactivate", "§cTODO");
									if(e.getSlot() == 10) {
										if(im.getDisplayName().equals("§aActivate")) {
											if(!core.getTribesAPI().isEffectActive(tribe, EffectType.LETHALITY)){
												e.getClickedInventory().setItem(10, limeWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.LETHALITY, true);
											}
										}else if(im.getDisplayName().equalsIgnoreCase("§cDeactivate")) {
											if(core.getTribesAPI().isEffectActive(tribe, EffectType.LETHALITY)){
												e.getClickedInventory().setItem(10, grayWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.LETHALITY, false);
											}
										}
									}else if(e.getSlot() == 11) {
										if(im.getDisplayName().equals("§aActivate")) {
											if(!core.getTribesAPI().isEffectActive(tribe, EffectType.PRECISION)){
												e.getClickedInventory().setItem(11, limeWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.PRECISION, true);
											}
										}else if(im.getDisplayName().equalsIgnoreCase("§cDeactivate")) {
											if(core.getTribesAPI().isEffectActive(tribe, EffectType.PRECISION)){
												e.getClickedInventory().setItem(11, grayWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.PRECISION, false);
											}
										}
									}else if(e.getSlot() == 12) {
										if(im.getDisplayName().equals("§aActivate")) {
											if(!core.getTribesAPI().isEffectActive(tribe, EffectType.HEALING_AURA)){
												e.getClickedInventory().setItem(12, limeWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.HEALING_AURA, true);
											}
										}else if(im.getDisplayName().equalsIgnoreCase("§cDeactivate")) {
											if(core.getTribesAPI().isEffectActive(tribe, EffectType.HEALING_AURA)){
												e.getClickedInventory().setItem(12, grayWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.HEALING_AURA, false);
											}
										}
									}else if(e.getSlot() == 13) {
										if(im.getDisplayName().equals("§aActivate")) {
											if(!core.getTribesAPI().isEffectActive(tribe, EffectType.SWIFT_FEET)){
												e.getClickedInventory().setItem(13, limeWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.SWIFT_FEET, true);
											}
										}else if(im.getDisplayName().equalsIgnoreCase("§cDeactivate")) {
											if(core.getTribesAPI().isEffectActive(tribe, EffectType.SWIFT_FEET)){
												e.getClickedInventory().setItem(13, grayWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.SWIFT_FEET, false);
											}
										}
									}else if(e.getSlot() == 14) {
										if(im.getDisplayName().equals("§aActivate")) {
											if(!core.getTribesAPI().isEffectActive(tribe, EffectType.LIFTOFF)){
												e.getClickedInventory().setItem(14, limeWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.LIFTOFF, true);
											}
										}else if(im.getDisplayName().equalsIgnoreCase("§cDeactivate")) {
											if(core.getTribesAPI().isEffectActive(tribe, EffectType.LIFTOFF)){
												e.getClickedInventory().setItem(14, grayWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.LIFTOFF, false);
											}
										}
									}else if(e.getSlot() == 15) {
										if(im.getDisplayName().equals("§aActivate")) {
											if(!core.getTribesAPI().isEffectActive(tribe, EffectType.MINERS_ADRENALINE)){
												e.getClickedInventory().setItem(15, limeWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.MINERS_ADRENALINE, true);
											}
										}else if(im.getDisplayName().equalsIgnoreCase("§cDeactivate")) {
											if(core.getTribesAPI().isEffectActive(tribe, EffectType.MINERS_ADRENALINE)){
												e.getClickedInventory().setItem(15, grayWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.MINERS_ADRENALINE, false);
											}
										}
									}else if(e.getSlot() == 16) {
										if(im.getDisplayName().equals("§aActivate")) {
											if(!core.getTribesAPI().isEffectActive(tribe, EffectType.HEMORRHAGE)){
												e.getClickedInventory().setItem(16, limeWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.HEMORRHAGE, true);
											}
										}else if(im.getDisplayName().equalsIgnoreCase("§cDeactivate")) {
											if(core.getTribesAPI().isEffectActive(tribe, EffectType.HEMORRHAGE)){
												e.getClickedInventory().setItem(16, grayWindow);
												core.getTribesAPI().setEffectStatus(tribe, EffectType.HEMORRHAGE, false);
											}
										}
									}
								}else{
									e.setCancelled(true);
								}
							}else if(is.getType() == Material.BARRIER) {
								if(im.getDisplayName().equals("§c§lEXIT")) {
									p.closeInventory();
								}
							}else{
								e.setCancelled(true);
							}
						}else{
							e.setCancelled(false);
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYou do not belong to any tribes, so interacting with crystals is not possible for you.");
						e.setCancelled(true);
						p.closeInventory();
					}
				}else{
					e.setCancelled(false);
					if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.CREATIVE) {
						ItemStack is = e.getCurrentItem();
						ItemMeta im = is.getItemMeta();
						if(is.getType() == Material.IRON_INGOT) {
							if(im.getDisplayName() == null || im.getDisplayName().equals("Iron Ingot")) {
								im.setDisplayName("§7§lIron Ingot");
								is.setItemMeta(im);
							}
						}else if(is.getType() == Material.COAL) {
							if(im.getDisplayName() == null || im.getDisplayName().equals("Coal")) {
								im.setDisplayName("§0§lCoal");
								is.setItemMeta(im);
							}
						}else if(is.getType() == Material.GOLD_INGOT) {
							if(im.getDisplayName() == null || im.getDisplayName().equals("Gold Ingot")) {
								im.setDisplayName("§6§lGold Ingot");
								is.setItemMeta(im);
							}
						}else if(is.getType() == Material.EMERALD) {
							if(im.getDisplayName() == null || im.getDisplayName().equals("Emerald")) {
								im.setDisplayName("§a§lEmerald");
								is.setItemMeta(im);
							}
						}else if(is.getType() == Material.DIAMOND) {
							if(im.getDisplayName() == null || im.getDisplayName().equals("Diamond")) {
								im.setDisplayName("§b§lDiamond");
								is.setItemMeta(im);
							}
						}else if(is.getType() == Material.QUARTZ) {
							if(im.getDisplayName() == null || im.getDisplayName().equals("Nether Quartz")) {
								im.setDisplayName("§f§lNether Quartz");
								is.setItemMeta(im);
							}
						}else if(is.getType() == Material.REDSTONE) {
							if(im.getDisplayName() == null || im.getDisplayName().equals("Redstone")) {
								im.setDisplayName("§c§lRedstone");
								is.setItemMeta(im);
							}
						}else if(is.getTypeId() == 351 && is.getData().getData() == 4) {
							if(im.getDisplayName() == null || im.getDisplayName().equals("Lapis Lazuli")) {
								im.setDisplayName("§1§lLapis Lazuli");
								is.setItemMeta(im);
							}
						}
					}
				}
		}else{
			e.setCancelled(false);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e)  {
		boolean atd = false;
		if(e.getEntity() instanceof EnderCrystal) {
			EnderCrystal ec = (EnderCrystal) e.getEntity();
			Location loc = ec.getLocation();
			if(!loc.getWorld().getName().equalsIgnoreCase("world_the_end")) {
				Location ml = core.getTribesAPI().getMinLocationOfBox(loc);
				Location mm = core.getTribesAPI().getMaxLocationOfBox(loc);
				String claim = core.getTribesAPI().getClaim(ml, mm);
				if(claim != null) {
					String claimOwner = core.getTribesAPI().getClaimOwner(claim);
					if(e.getDamager() instanceof Player) {
						Player d = (Player) e.getDamager();
						String pTribe = core.getTribesAPI().getPlayerTribe(d.getUniqueId().toString());
						e.setCancelled(true);
						if(core.getTribesAPI().belongsToTribe(claimOwner, d.getUniqueId().toString())) {
							atd = false;
							d.sendMessage(core.getPrefix() + "§cYou can not damage your own crystal.");
						}else{
							if(pTribe != null) {
								if(core.getTribesAPI().alliances(claimOwner).contains(pTribe)) {
									atd = false;
									d.sendMessage(core.getPrefix() + "§cAs an alliance, you can not damage the crystal owned by this tribe.");
								}else{
									atd = true;
								}
							}else{
								atd = true;
							}
						}
						if(atd) {
							Location cloc = core.getLocationHelper().getLocation(claimOwner.toUpperCase() + ".yml", "Information.Location.Crystal", "plugins//ENFactions//Tribes//");
							int lives = core.getTribesAPI().getCrystalLives(claimOwner);
							for(Entity en : ec.getNearbyEntities(2,2,2)) {
								Location enLoc = en.getLocation();
								if(en instanceof ArmorStand) {
									if(enLoc.equals(cloc)) {
										en.remove();
										if(lives > 1) {
											core.getTribesAPI().setCrystalLives(claimOwner, lives - 1);
											core.getTribesAPI().updateCrystalHealth(enLoc, claimOwner);
										}else if(lives == 1) {
											core.getTribesAPI().setCrystalLives(claimOwner, 0);
											core.getTribesAPI().updateCrystalHealth(enLoc, claimOwner);
											core.getTribesAPI().despawnCrystal(claimOwner);
											if(pTribe != null && core.getTribesAPI().tribeExists(pTribe)) {
												for(String m : core.getTribesAPI().getAllMembers(pTribe)) {
													Player p = Bukkit.getPlayer(UUID.fromString(m));
													if(p != null) p.sendMessage(core.getPrefix() + "§cYou can now raid §7" + claimOwner + "§c's territory.");
												}
												for(String dally : core.getTribesAPI().alliances(pTribe)) {
													for(String m : core.getTribesAPI().getAllMembers(dally)) {
														Player p = Bukkit.getPlayer(UUID.fromString(m));
														if(p != null) p.sendMessage(core.getPrefix() + "§cYou can now raid §7" + claimOwner + "§c's territory.");
													}
												}
											}
											for(String tm : core.getTribesAPI().getAllMembers(claimOwner)) {
												Player p = Bukkit.getPlayer(UUID.fromString(tm));
												if(p != null) p.sendMessage(core.getPrefix() + "§cYour tribe's crystal was destroyed.");
											}
											for(String ally : core.getTribesAPI().alliances(claimOwner)) {
												for(String am : core.getTribesAPI().getAllMembers(ally)) {
													Player m = Bukkit.getPlayer(UUID.fromString(am));
													if(m != null) m.sendMessage(core.getPrefix() + "§cCrystal owned by tribe §7" + claimOwner + " §cwas destroyed. Help them protect their territory!");
												}
											}
											core.getServer().getScheduler().scheduleSyncDelayedTask(core, new BukkitRunnable() {
												
												@Override
												public void run() {
													if(core.getTribesAPI().tribeExists(claimOwner)) {
														core.getTribesAPI().respawnCrystal(claimOwner);
													}
												}
												
											}, 20*20L);
										}
									}
								}
							}
							if(lives == 99 || lives == 84 || lives == 69 || lives == 54 || lives == 39 || lives == 24 || lives == 9) {
								for(String tm : core.getTribesAPI().getAllMembers(claimOwner)) {
									Player p = Bukkit.getPlayer(UUID.fromString(tm));
									if(p != null) p.sendMessage(core.getPrefix() + "§cYour crystal is under attack! Hurry up and protect it!");
								}
								for(String ally : core.getTribesAPI().alliances(claimOwner)) {
									for(String am : core.getTribesAPI().getAllMembers(ally)) {
										Player m = Bukkit.getPlayer(UUID.fromString(am));
										if(m != null) m.sendMessage(core.getPrefix() + "§cCrystal of tribe §7" + claimOwner + " §cis under attack! Since you are an alliance with them you should help them to protect it.");
									}
								}
							}
						}
					}else if(e.getDamager() instanceof Arrow) {
						Arrow a = (Arrow) e.getDamager();
						ProjectileSource en = a.getShooter();
						if(en instanceof Player) {
							Player p = (Player) en;
							e.setCancelled(true);
							if(core.getTribesAPI().belongsToTribe(claimOwner, p.getUniqueId().toString())) {
								p.sendMessage(core.getPrefix() + "§cYou can not damage your own crystal.");
							}else{
								for(String tm : core.getTribesAPI().getAllMembers(claimOwner)) {
									Player m = Bukkit.getPlayer(UUID.fromString(tm));
									if(m != null) m.sendMessage(core.getPrefix() + "§cYour crystal is under attack! Hurry up and protect it!");
								}
								for(String ally : core.getTribesAPI().alliances(claimOwner)) {
									for(String am : core.getTribesAPI().getAllMembers(ally)) {
										Player m = Bukkit.getPlayer(UUID.fromString(am));
										if(m != null) m.sendMessage(core.getPrefix() + "§cCrystal of tribe §7" + claimOwner + " §cis under attack! Since you are an alliance with them you should help them to protect it.");
									}
								}
							}
						}else{
							e.setCancelled(true);
							for(String tm : core.getTribesAPI().getAllMembers(claimOwner)) {
								Player m = Bukkit.getPlayer(tm);
								m.sendMessage(core.getPrefix() + "§cYour crystal is under attack! Hurry up and protect it!");
							}
						}
					}
				}
			}
		}
	}

}
