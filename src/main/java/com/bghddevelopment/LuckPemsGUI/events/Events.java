package com.master86.Luckpermsgui.events;
import com.master86.Luckpermsgui.groups.GroupsGUI;
import com.master86.Luckpermsgui.tracks.TracksGUI;
import com.master86.Luckpermsgui.users.UsersGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        if (inv != null && item != null)
            if (e.getView().getTitle().equals(ChatColor.AQUA + "LuckPerms")) {
                e.setCancelled(true);
                if (item.hasItemMeta())
                    if (item.getItemMeta().hasDisplayName()) {
                        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if (name.equals("Groups"))
                            GroupsGUI.open(p);
                        if (name.equals("Users"))
                            UsersGUI.open(p);
                        if (name.equals("Tracks"))
                            TracksGUI.open(p);
                    }
            }
    }
}
