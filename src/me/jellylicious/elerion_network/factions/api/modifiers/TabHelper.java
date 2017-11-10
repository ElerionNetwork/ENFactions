package me.jellylicious.elerion_network.factions.api.modifiers;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_9_R1.PlayerConnection;

public class TabHelper {
	
	public void sendTabTitle(Player p, String header, String footer) {
        if(header == null) header = "";
        if(footer == null) footer = "";

        PlayerConnection con = ((CraftPlayer)p).getHandle().playerConnection;
        IChatBaseComponent tabTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
        IChatBaseComponent tabFoot = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
        PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(tabTitle);

        try{
            Field field = headerPacket.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(headerPacket, tabFoot);
        }catch(Exception ex) {
            ex.printStackTrace();
        }finally{
            con.sendPacket(headerPacket);
        }
    }
	
//	public void setColoredNameInTab(Player p) {
//		Scoreboard b;
//	}

}
