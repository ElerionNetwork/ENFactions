package me.jellylicious.elerion_network.factions.api.modifiers;

import java.lang.reflect.Constructor;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class TitleHelper {
	
	Core core;
	
	public TitleHelper(Core core) {
		this.core = core;
	}

	public void sendTitle(Player p, String title, String subTitle) {
        int fadeIn = 1;
        int stay = 1;
        int fadeOut = 1;
        try{
            if(title != null) {
                title = ChatColor.translateAlternateColorCodes('&', title);
                title = title.replaceAll("%p%", p.getDisplayName());
                Object enumTitle = core.getPacketsHelper().getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                Object chatTitle = core.getPacketsHelper().getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] {String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor<?> titleConstructor = core.getPacketsHelper().getNMSClass("PacketPlayOutTitle").getConstructor(core.getPacketsHelper().getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], core.getPacketsHelper().getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                Object titlePacket = titleConstructor.newInstance(enumTitle, chatTitle, fadeIn, stay, fadeOut);
                core.getPacketsHelper().sendPacket(p, titlePacket);
            }
            if(subTitle != null) {
                subTitle = ChatColor.translateAlternateColorCodes('&', subTitle);
                subTitle = subTitle.replaceAll("%p%", p.getDisplayName());
                Object enumSubTitle = core.getPacketsHelper().getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                Object chatSubTitle = core.getPacketsHelper().getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + subTitle + "\"}");
                Constructor<?> subTitleConstructor = core.getPacketsHelper().getNMSClass("PacketPlayOutTitle").getConstructor(core.getPacketsHelper().getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], core.getPacketsHelper().getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                Object subTitlePacket = subTitleConstructor.newInstance(enumSubTitle, chatSubTitle, fadeIn, stay, fadeOut);
                core.getPacketsHelper().sendPacket(p, subTitlePacket);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
