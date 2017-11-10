package me.jellylicious.elerion_network.factions.api.modifiers;

import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.PacketPlayOutChat;
import net.minecraft.server.v1_9_R1.PlayerConnection;

public class ActionBarHelper {

	public void sendActionBar(String message, Player p) {
        message = message.replace("&", "§");
        message = message.replace("%p", p.getDisplayName());
        PlayerConnection con = ((CraftPlayer)p).getHandle().playerConnection;
        IChatBaseComponent chat = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(chat, (byte) 2);
        con.sendPacket(packet);
    }

}
