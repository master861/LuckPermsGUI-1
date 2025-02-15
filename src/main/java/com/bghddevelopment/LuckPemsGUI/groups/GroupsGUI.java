package com.bghddevelopment.LuckPermsGui.groups;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bghddevelopment.LuckPermsGui.util.OpenGUI;
import com.bghddevelopment.LuckPermsGui.util.Tools;
import com.bghddevelopment.LuckPermsGui.LuckPermsGui;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GroupsGUI implements Listener {

    List<Player> newGroup = new ArrayList<>();

    @EventHandler
    public void onGroupAdd(AsyncPlayerChatEvent e) {
        if (!newGroup.contains(e.getPlayer())) return;
        String message = e.getMessage();

        Tools.sendCommand(e.getPlayer(), "lp creategroup "+message);
        newGroup.remove(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckPermsGui.plugin, () -> {
            open(e.getPlayer());
        }, 5);
        e.setCancelled(true);
    }

    static LuckPerms l = LuckPermsProvider.get();

    public static void open(Player p) {
        Inventory myInventory = Bukkit.createInventory(null, 54, ChatColor.AQUA+"LuckPerms groups");
        Tools.onAsync(() -> {

            int sk = 9;
            for (Group group : l.getGroupManager().getLoadedGroups()) {
                String name = group.getName();
                ItemStack item = Tools.button(Material.TNT, "&6"+name, Arrays.asList("&ePress to edit this group"), 1);
                myInventory.setItem(sk, item);
                sk++;
            }

            ItemStack back = Tools.button(Material.BARRIER, "&6Back", Arrays.asList(""), 1);
            myInventory.setItem(8, back);

            ItemStack newGroup = Tools.button(Material.PAPER, "&6New group", Arrays.asList("&eCreate new group"), 1);
            myInventory.setItem(0, newGroup);

        });
        p.openInventory(myInventory);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        if (inv != null && item != null)
            if (e.getView().getTitle().equals(ChatColor.AQUA+"LuckPerms groups")) {
                e.setCancelled(true);
                if (item.hasItemMeta())
                    if (item.getItemMeta().hasDisplayName()) {
                        String group = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if (group.equals("Back")) {
                            OpenGUI.openGUI(p);
                        } else if (group.equals("New group")) {
                            Tools.sendMessage(p, "&eWrite in chat:");
                            Tools.sendMessage(p, "&8<&7Name&8>");
                            Tools.sendMessage(p, "&aName - Group name");
                            newGroup.add(p);
                            p.closeInventory();
                        } else EditGroup.open(p, l.getGroupManager().getGroup(group));
                    }
            }
    }
}