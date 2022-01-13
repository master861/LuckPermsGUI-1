package com.master86.Luckpermsgui.util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;

import com.master86.Luckpermsgui.Luckpermsgui;
import net.luckperms.api.context.ContextSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Tools {

    public static ItemStack button(Material material, String pavadinimas, List<String> lore, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(ChatColor.translateAlternateColorCodes('&', pavadinimas));
        meta.setLore(ctranslate(lore));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack head(String owner, String pavadinimas, List<String> lore, int amount) {
        ItemStack item = new ItemStack(Material.BONE_BLOCK, amount);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        Objects.requireNonNull(meta).setOwner(owner);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', pavadinimas));
        meta.setLore(ctranslate(lore));
        item.setItemMeta(meta);
        return item;
    }

    private static List<String> ctranslate(List<String> lore) {
        List<String> lore2 = new ArrayList<>();
        for (String eilute : lore)
            lore2.add(ChatColor.translateAlternateColorCodes('&', eilute));
        return lore2;
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }


    public static void onAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Luckpermsgui.plugin, runnable);
    }

    public static void sendCommand(Player p, String command) {
        //THIS SENDS THE DEV THE COMMAND THE GUI IS RUNNING FOR DEBUGGING AND OTHER DEVELOPMENT NEEDS
        if (p.getName().equalsIgnoreCase("Noodles_YT")) {
            p.sendMessage(ChatColor.RED + "[DEBUG] " + command);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Luckpermsgui.plugin, () -> {
            Bukkit.dispatchCommand(p, command);
        });
    }

    // Not usable doesn't send the correct context's anymore
    public static String contextConverter(ContextSet contextSet) {
        StringBuilder eilute = new StringBuilder();
        for (Entry<String, String> entry : contextSet.toFlattenedMap().entrySet()) {
            if (eilute.length() != 0)
                eilute.append(" ");
            eilute.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return eilute.toString();
    }

    public static String getTime(long time) {
        time = time - System.currentTimeMillis();

        long tempSec = time/(1000);
        long min = (tempSec /60) % 60;
        long hour = (tempSec /(60*60)) % 24;
        long temphour = (tempSec /(60*60));
        int days = 0;
        while (temphour >= 24) {
            days++;
            temphour -= 24;
        }

        return days+"d. "+hour+"h. "+min+"min.";
    }

    public static boolean isOnline(String name) {
        return Bukkit.getPlayerExact(name) != null;
    }

    public static void sendConsole(String string) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Luckpermsgui.plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string);
        });
    }
}
