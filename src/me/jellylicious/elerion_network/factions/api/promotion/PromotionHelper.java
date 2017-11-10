package me.jellylicious.elerion_network.factions.api.promotion;

import me.jellylicious.elerion_network.factions.Core;

public class PromotionHelper {

	Core core;
	public PromotionHelper(Core core) {
		this.core = core;
	}
	
	public boolean isPromotionActive(String promotionType) {
		if(promotionType.equalsIgnoreCase("xppromotion")) {
			boolean multiplierActive = (boolean) core.getSettings().getSetting("plugins//ENFactions//", "settings.yml", "Promotions.XPPromotion.Type.Multiplier.Active");
			boolean percentagesActive = (boolean) core.getSettings().getSetting("plugins//ENFactions//", "settings.yml", "Promotions.XPPromotion.Type.Percentage.Active");
			return multiplierActive || percentagesActive;
		}
		return false;
	}
	
	public void setPromotionStatus(String promotionType, boolean active) {
		if(promotionType.equalsIgnoreCase("xppromotion")) {
			if(isPromotionActive(promotionType) && !active) {
				core.getSettings().updateSetting("plugins//ENFactions//", "settings.yml", "Promotions.XPPromotion.Type.Multiplier.Active", false);
				core.getSettings().updateSetting("plugins//ENFactions//", "settings.yml", "Promotions.XPPromotion.Type.Percentage.Active", false);
			}else if(!isPromotionActive(promotionType) && active) {
				core.getSettings().updateSetting("plugins//ENFactions//", "settings.yml", "Promotions.XPPromotion.Type.Multiplier.Active", true);
				core.getSettings().updateSetting("plugins//ENFactions//", "settings.yml", "Promotions.XPPromotion.Type.Percentage.Active", true);
			}
		}
	}

}
