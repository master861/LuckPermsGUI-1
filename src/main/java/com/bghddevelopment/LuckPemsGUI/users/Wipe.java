package com.bghddevelopment.LuckPermsGui.users;
import java.util.Arrays;

import com.bghddevelopment.LuckPermsGui.util.Tools;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Wipe implements Listener {

    static LuckPerms l = LuckPermsProvider.get();

    public static void open(Player p) {
        Inventory myInventory = Bukkit.createInventory(null, 54, ChatColor.AQUA + "LuckPerms WIPE");
        Tools.onAsync(() -> {

            int sk = 9;
            for (Group group : l.getGroupManager().getLoadedGroups()) {
                String name = group.getName();
                ItemStack item = Tools.button(Material.TNT, "&6" + name, Arrays.asList("&eSelect to remove from users"), 1);
                myInventory.setItem(sk, item);
                sk++;
            }

            ItemStack back = Tools.button(Material.BARRIER, "&6Back", Arrays.asList(""), 1);
            myInventory.setItem(7, back);
            ItemStack confirm = Tools.button(Material.DARK_OAK_WALL_SIGN, "&2&lCONFIRM", Arrays.asList("&ePress when everything selected", "&cThis will take some time!!"), 1);
            myInventory.setItem(8, confirm);

        });
        p.openInventory(myInventory);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        if (inv != null && item != null)
            if (e.getView().getTitle().equals(ChatColor.AQUA + "LuckPerms WIPE")) {
                e.setCancelled(true);
                if (item.hasItemMeta())
                    if (item.getItemMeta().hasDisplayName()) {
                        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if (name.equals("Back")) {
                            UsersGUI.open(p);
                        } else if (name.equals("CONFIRM")) {
                            for (ItemStack groupItem : inv) {
                                if (groupItem == null)
                                    continue;
                                if (groupItem.getType().equals(Material.EMERALD)) {
                                    String groupName = ChatColor.stripColor(groupItem.getItemMeta().getDisplayName());
                                    Tools.sendConsole("lp bulkupdate users delete \"permission ~~ group." + groupName + "\"");
                                }
                            }
                            UsersGUI.open(p);
                            Tools.sendMessage(p, "&aAction was completed!");
                        } else {
                            if (item.getType().equals(Material.TNT)) {
                                item.setType(Material.EMERALD);
                            } else {
                                item.setType(Material.TNT);
                            }
                        }
                    }
            }
    }
}
