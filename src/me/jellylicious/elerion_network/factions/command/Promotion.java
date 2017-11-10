package me.jellylicious.elerion_network.factions.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Promotion implements CommandExecutor{

	//promotion start <promotionType[xppromotion]> <promotionInfo[multiplier|percentage]> <endsOn[yyyy-MM-dd HH:mm:ss]> 
	Core core;
	public Promotion(Core core) {
		this.core = core;
	}
	
	// By clicking on an inactive one, you're redirected to another inventory which asks you to select a promotion length, type (if more types like XP Promotion has) and multiplier or anything related to the specified promotion.
	// At the end you're required to confirm your actions by entering your password. After that all data is saved to MySQL including who activated it.
	// To select a multiplier or percentage, your need to click as many times as your multiplier is big. So, for 5x multiplier, you need to click 4 times (because initial itemstack amount is 1 and +4 = 5).
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(core.getGroups().hasPermission(p, "elerion.command.promotion")) {
				if(args.length == 4) {
					String action = args[0];
					String promotionType = args[1];
					if(action.equalsIgnoreCase("start")) {
						if(promotionType.equalsIgnoreCase("xppromotion")) {
							if(!core.getPromotionHelper().isPromotionActive(promotionType)) {
								core.getPromotionHelper().setPromotionStatus(promotionType, true);
								if(core.getPromotionHelper().isPromotionActive(promotionType)) {
									p.sendMessage(core.getPrefix() + "§cThis promotion is already active.");
								}else{
									
								}
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cUnsupported promotion type. The only type that exists for now is XPPromotion.");
						}
					}else if(action.equalsIgnoreCase("end")){
						
					}else{
						p.sendMessage(core.getPrefix() + "§cUnsupported action type. Please choose between start and end.");
					}
				}else if(args.length == 0) {
					sendPromotionHelp(p);
				}
			}
		}else{
			cs.sendMessage(core.getPrefix() + "§cSorry, but only players can execute that command.");
		}
		return true;
	}
	
	private void sendPromotionHelp(Player p) {
		p.sendMessage("§7[§a§lELERION§7] §9Help §8| §6Page§8: §e1§7/§e5");
		p.sendMessage("§eUsage: §7<action> <promotionType> <promotionInfo> <endsOn>");
		p.sendMessage("§bExplanation:");
		p.sendMessage("  §7- §aAction§7: §6You can choose from start or end.");
		p.sendMessage("  §7- §aPromotion Type§7: §6For now you can only choose XPPromotion.");
		p.sendMessage("  §7- §aPromotion Info§7: §6You should enter something like 5x or 5%.");
		p.sendMessage("  §7- §aEnds on§7: §6Format the date this promotion ends on.");
	}
	
}
