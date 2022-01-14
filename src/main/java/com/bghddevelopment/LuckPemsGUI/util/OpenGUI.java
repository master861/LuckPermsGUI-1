package com.bghddevelopment.LuckPermsGui.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class OpenGUI {

    public static void openGUI(Player sender) {
        Inventory myInventory = Bukkit.createInventory(null, 9, ChatColor.AQUA + "LuckPerms");
        Tools.onAsync(() -> {
            ItemStack button1 = Tools.button(Material.ANVIL, "&6Groups", Arrays.asList("&eSelect to edit groups"), 1);
            ItemStack button2 = Tools.button(Material.ANVIL, "&6Users", Arrays.asList("&eSelect to edit online users"), 1);
            ItemStack button3 = Tools.button(Material.ANVIL, "&6Tracks", Arrays.asList("&eSelect to edit tracks"), 1);

            myInventory.setItem(2, button1);
            myInventory.setItem(4, button2);
            myInventory.setItem(6, button3);
        });
        sender.openInventory(myInventory);
    }
}
