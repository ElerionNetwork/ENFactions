package me.jellylicious.elerion_network.factions.api.location;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import me.jellylicious.elerion_network.factions.Core;

public class LocationHelper {

	Core core;
	
	public LocationHelper(Core core) {
		this.core = core;
	}
	
	public Location getLocation(String ymlName, String name, String path) {
        FileConfiguration cfg = core.getFileManager().getConfiguration(ymlName, path);
        if(cfg.isConfigurationSection(name)) {
	        double x = cfg.getDouble(name + ".X");
	        double y = cfg.getDouble(name + ".Y");
	        double z = cfg.getDouble(name + ".Z");
	        double yaw = cfg.getDouble(name + ".Yaw");
	        double pitch = cfg.getDouble(name + ".Pitch");
	        String world = cfg.getString(name + ".World");
	
	        Location l = new Location(Bukkit.getWorld(world), x, y, z);
	        l.setYaw((float) yaw);
	        l.setPitch((float) pitch);
	        return l;
        }
        return null;
    }

    public void saveLocation(Location loc, String ymlName, String name, String path) {
        FileConfiguration cfg = core.getFileManager().getConfiguration(ymlName, path);
        cfg.set(name + ".X", loc.getX());
        cfg.set(name + ".Y", loc.getY());
        cfg.set(name + ".Z", loc.getZ());
        cfg.set(name + ".Yaw", loc.getYaw());
        cfg.set(name + ".Pitch", loc.getPitch());
        cfg.set(name + ".World", loc.getWorld().getName());

        try{
            cfg.save(core.getFileManager().getFile(ymlName, path));
        }catch(IOException ex) {
            ex.printStackTrace();
        }
        cfg = null;
    }
    
    public void removeLocation(String ymlName, String name, String path) {
        FileConfiguration cfg = core.getFileManager().getConfiguration(ymlName, path);
        cfg.set(name, null);
        try{
            cfg.save(core.getFileManager().getFile(ymlName, path));
        }catch(IOException ex) {
            ex.printStackTrace();
        }
        cfg = null;
    }

}
