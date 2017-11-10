package me.jellylicious.elerion_network.factions.api.progress;

import me.jellylicious.elerion_network.factions.Core;

public class Progress {

	Core core;
	public Progress(Core core) {
		this.core = core;
	}
	
	public String getProgress(float percentage) {
		if(!(percentage < 0)) {
			if(percentage <= 15) {
				return "§a❖§c❖❖❖❖❖❖❖❖❖";
			}else if(percentage > 15 && percentage <= 25) {
				return "§a❖❖§c❖❖❖❖❖❖❖❖";
			}else if(percentage > 25 && percentage <= 35) {
				return "§a❖❖❖§c❖❖❖❖❖❖❖";
			}else if(percentage > 35 && percentage <= 45) {
				return "§a❖❖❖❖§c❖❖❖❖❖❖";
			}else if(percentage > 45 && percentage <= 55) {
				return "§a❖❖❖❖❖§c❖❖❖❖❖";
			}else if(percentage > 55 && percentage <= 65) {
				return "§a❖❖❖❖❖❖§c❖❖❖❖";
			}else if(percentage > 65 && percentage <= 75) {
				return "§a❖❖❖❖❖❖❖§c❖❖❖";
			}else if(percentage > 75 && percentage <= 85) {
				return "§a❖❖❖❖❖❖❖❖§c❖❖";
			}else if(percentage > 85 && percentage <= 95) {
				return "§a❖❖❖❖❖❖❖❖❖§c❖";
			}else if(percentage > 95 && percentage <= 100) {
				return "§a❖❖❖❖❖❖❖❖❖❖";
			}
		}
		return null;
	}

}
