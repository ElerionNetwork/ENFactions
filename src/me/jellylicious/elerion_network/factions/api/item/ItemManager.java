package me.jellylicious.elerion_network.factions.api.item;

import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import me.jellylicious.elerion_network.factions.Core;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.NBTTagList;

@SuppressWarnings("deprecation")
public class ItemManager {

	Core core;
	public ItemManager(Core core) {
		this.core = core;
	}
	
	public ItemStack addGlow(ItemStack item){
        net.minecraft.server.v1_9_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

	public ItemStack createItemWithID(int id, int subid, int amount, String displayName, String... lore) {
        ItemStack is = new ItemStack(id, amount, (short)subid);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(displayName);
        if(lore != null) im.setLore(Arrays.asList(lore));
        is.setItemMeta(im);
        return is;
    }

    public ItemStack createItemWithMaterial(Material m, int subid, int amount, String displayName, String... lore) {
        ItemStack is = new ItemStack(m, amount, (short) subid);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(displayName);
        if(lore != null) im.setLore(Arrays.asList(lore));
        is.setItemMeta(im);
        return is;
    }

    public ItemStack createHead(String owner, String name, String... lore) {
        ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta sm = (SkullMeta) is.getItemMeta();
        sm.setOwner(owner);
        sm.setDisplayName(name);
        if(lore != null) sm.setLore(Arrays.asList(lore));
        is.setItemMeta(sm);
        return is;
    }
    
    public ItemStack createBanner(DyeColor baseColor) {
    	ItemStack banner = new ItemStack(Material.BANNER, 1);
    	BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
    	bannerMeta.setBaseColor(baseColor);
    	banner.setItemMeta(bannerMeta);
    	return banner;
    }
    
    public ItemStack createPotion(PotionType type, int amount, boolean splash){
    	ItemStack is = new ItemStack(Material.POTION, amount);
    	Potion p = new Potion(1);
    	p.setType(type);
    	p.setSplash(splash);
    	p.apply(is);
    	return is;
    }

}
