package me.jellylicious.elerion_network.factions.api.settings;

import org.bukkit.configuration.file.FileConfiguration;

import me.jellylicious.elerion_network.factions.Core;

public class Settings {
	
	Core core;
	public Settings(Core core) {
		this.core = core;
	}
	
	public Object getSetting(String path, String fileName, String setting) {
		if(core.getFileManager().getFile(fileName, path).exists()) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(fileName, path);
			if(cfg.isSet(setting)) {
				return cfg.get(setting);
			}
			return null;
		}
		return null;
	}
	
	public void updateSetting(String path, String fileName, String update, Object to) {
		if(core.getFileManager().getFile(fileName, path).exists()) {
			FileConfiguration cfg = core.getFileManager().getConfiguration(fileName, path);
			if(cfg.isConfigurationSection(update)) {
				cfg.set(update, to);
			}
		}
	}

}
