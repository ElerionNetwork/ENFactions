package me.jellylicious.elerion_network.factions.listener;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import me.jellylicious.elerion_network.factions.Core;

public class Interact implements Listener {

	Core core;
	public Interact(Core core) {
		this.core = core;
	}
	
	@EventHandler
	public void onASI(PlayerArmorStandManipulateEvent e) {
		Player p = (Player) e.getPlayer();
		ArmorStand as = e.getRightClicked();
		Location loc = as.getLocation();
		Location l = core.getTribesAPI().getMinLocationOfBox(loc);
		Location h = core.getTribesAPI().getMaxLocationOfBox(loc);
		String claim = core.getTribesAPI().getClaim(l, h);
		if(claim != null) {
			String claimOwner = core.getTribesAPI().getClaimOwner(claim);
			Location cl = core.getTribesAPI().getMinLocation(claim);
			Location hl = core.getTribesAPI().getMaxLocation(claim);
			if(!core.getTribesAPI().belongsToTribe(claimOwner, p.getUniqueId().toString())) {
				if(core.getTribesAPI().isInsideArea(loc, cl, hl)) {
					e.setCancelled(true);
					p.sendMessage(core.getPrefix() + "§cYou can not interact with armor stands in this area!");
				}
			}
		}
	}
	
	@EventHandler
	public void hangingBreak(HangingBreakByEntityEvent e) {
		Player p = (Player) e.getRemover();
		Entity ent = e.getEntity();
		Location loc = ent.getLocation();
		Location l = core.getTribesAPI().getMinLocationOfBox(loc);
		Location h = core.getTribesAPI().getMaxLocationOfBox(loc);
		String claim = core.getTribesAPI().getClaim(l, h);
		if(claim != null) {
			String claimOwner = core.getTribesAPI().getClaimOwner(claim);
			Location cl = core.getTribesAPI().getMinLocation(claim);
			Location hl = core.getTribesAPI().getMaxLocation(claim);
			if(!core.getTribesAPI().belongsToTribe(claimOwner, p.getUniqueId().toString())) {
				if(core.getTribesAPI().isInsideArea(loc, cl, hl)) {
					e.setCancelled(true);
					p.sendMessage(core.getPrefix() + "§cYou can not interact with item frames in this area!");
				}
			}
		}
	}

}
