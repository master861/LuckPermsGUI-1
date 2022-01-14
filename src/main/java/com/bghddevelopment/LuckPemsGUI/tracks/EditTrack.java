package com.bghddevelopment.LuckPermsGui.tracks;
import java.util.*;

import com.bghddevelopment.LuckPermsGui.LuckPermsGui;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.track.Track;
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
import com.bghddevelopment.LuckPermsGui.util.Tools;

public class EditTrack implements Listener {

    public static Map<Player, Track> addgroup = new HashMap<>();
    public static Map<Player, Track> insertgroup = new HashMap<>();
    public static Map<Player, Track> rename = new HashMap<>();
    public static Map<Player, Track> clone = new HashMap<>();
    static LuckPerms l = LuckPermsProvider.get();

    @EventHandler
    public void onGroupAdd(AsyncPlayerChatEvent e) {
        if (!addgroup.containsKey(e.getPlayer())) return;
        String message = e.getMessage();
        Track g = addgroup.get(e.getPlayer());

        Tools.sendCommand(e.getPlayer(), "lp track "+g.getName()+" append "+message);
        addgroup.remove(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckPermsGui.plugin, () -> open(e.getPlayer(), g), 5);
        e.setCancelled(true);
    }

    @EventHandler
    public void onGroupInsert(AsyncPlayerChatEvent e) {
        if (!insertgroup.containsKey(e.getPlayer())) return;
        String message = e.getMessage();
        Track g = insertgroup.get(e.getPlayer());

        Tools.sendCommand(e.getPlayer(), "lp track "+g.getName()+" insert "+message);
        insertgroup.remove(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckPermsGui.plugin, () -> open(e.getPlayer(), g), 5);
        e.setCancelled(true);
    }

    @EventHandler
    public void onRename(AsyncPlayerChatEvent e) {
        if (!rename.containsKey(e.getPlayer())) return;
        String message = e.getMessage();
        Track g = rename.get(e.getPlayer());

        Tools.sendCommand(e.getPlayer(), "lp track "+g.getName()+" rename "+message);
        rename.remove(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckPermsGui.plugin, () -> open(e.getPlayer(), g), 5);
        e.setCancelled(true);
    }

    @EventHandler
    public void onClone(AsyncPlayerChatEvent e) {
        if (!clone.containsKey(e.getPlayer())) return;
        String message = e.getMessage();
        Track g = clone.get(e.getPlayer());

        Tools.sendCommand(e.getPlayer(), "lp track "+g.getName()+" clone "+message);
        clone.remove(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckPermsGui.plugin, () -> open(e.getPlayer(), g), 5);
        e.setCancelled(true);
    }

    public static void open(Player p, Track group) {
        Inventory myInventory = Bukkit.createInventory(null, 54, ChatColor.AQUA+"LuckPerms track");
        Tools.onAsync(() -> {

            ItemStack info = Tools.button(Material.ARMOR_STAND,
                    "&6Info",
                    Arrays.asList(
                            "&cName: &e"+group.getName(),
                            "&cAll groups: &e"+group.getGroups().size()),
                    1);
            myInventory.setItem(4, info);
            // ----------------------- INFO ------------------------------

            // ----------------------- PERMISSION ------------------------------
            ItemStack addnewgroup = Tools.button(Material.TORCH, "&6Add group", Collections.singletonList("&eClick here to add a group"), 1);
            myInventory.setItem(0, addnewgroup);

            ItemStack insertgroup = Tools.button(Material.ARROW, "&6Insert group", Collections.singletonList("&eClick to insert a group"), 1);
            myInventory.setItem(1, insertgroup);

            ItemStack clear = Tools.button(Material.REDSTONE_BLOCK, "&cClear", Collections.singletonList("&eRemove all groups"), 1);
            myInventory.setItem(2, clear);

            ItemStack delete = Tools.button(Material.REDSTONE, "&cDelete", Collections.singletonList("&eClick to delete the track"), 1);
            myInventory.setItem(5, delete);

            ItemStack rename = Tools.button(Material.NAME_TAG, "&6Rename", Collections.singletonList("&eClick to rename the track"), 1);
            myInventory.setItem(6, rename);

            ItemStack clone = Tools.button(Material.PAPER, "&6Clone", Collections.singletonList("&eClick to clone the track"), 1);
            myInventory.setItem(7, clone);
            // ----------------------- PERMISSION ------------------------------

            ItemStack back = Tools.button(Material.BARRIER, "&6Back", Collections.singletonList(""), 1);
            myInventory.setItem(8, back);


            int sk = 9;
            for (String g : group.getGroups()) {
                ItemStack item = Tools.button(Material.TNT, "&6"+g, Collections.singletonList("&cClick to remove"), 1);
                myInventory.setItem(sk, item);
                sk++;
            }
        });
        p.openInventory(myInventory);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        if (inv != null && item != null)
            if (e.getView().getTitle().equals(ChatColor.AQUA+"LuckPerms track")) {
                e.setCancelled(true);
                if (item.hasItemMeta())
                    if (Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {

                        String group = ChatColor.stripColor(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(inv.getItem(4)).getItemMeta()).getLore()).get(0).split(" ")[1]);
                        Track g = l.getTrackManager().getTrack(group);

                        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if (name.equals("Back")) {
                            TracksGUI.open(p);
                        } else if (name.equals("Add group")) {
                            Tools.sendMessage(p, "&eWrite in chat:");
                            Tools.sendMessage(p, "&8<&7Group&8>");
                            Tools.sendMessage(p, "&aGroup - The group you want to add");
                            addgroup.put(p, g);
                            p.closeInventory();
                        } else if (name.equals("Insert group")) {
                            Tools.sendMessage(p, "&eWrite in chat:");
                            Tools.sendMessage(p, "&8<&7Group&8> &8<&7Position&8>");
                            Tools.sendMessage(p, "&aGroup - The group you want to add");
                            Tools.sendMessage(p, "&aPosition - The position to insert the group at");
                            insertgroup.put(p, g);
                            p.closeInventory();
                        } else if (name.equals("Clear")) {
                            assert g != null;
                            g.clearGroups();
                            open(p, g);
                        } else if (name.equals("Delete")) {
                            assert g != null;
                            Tools.sendCommand(p, "lp delete-track "+g.getName());
                            Bukkit.getScheduler().runTaskLater(LuckPermsGui.plugin, () -> TracksGUI.open(p), 5);
                        } else if (name.equals("Rename")) {
                            Tools.sendMessage(p, "&eWrite in chat:");
                            Tools.sendMessage(p, "&8<&7Name&8>");
                            Tools.sendMessage(p, "&aName - The new name of the track");
                            rename.put(p, g);
                            p.closeInventory();
                        } else if (name.equals("Clone")) {
                            Tools.sendMessage(p, "&eWrite in chat:");
                            Tools.sendMessage(p, "&8<&7Name&8>");
                            Tools.sendMessage(p, "&aName - The name of the new track");
                            clone.put(p, g);
                            p.closeInventory();
                        } else if (item.getType().equals(Material.TNT)) {
                            assert g != null;
                            Tools.sendCommand(p, "lp track "+g.getName()+" remove "+ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                            Bukkit.getScheduler().runTaskLater(LuckPermsGui.plugin, () -> open(p, g), 5);
                        }
                    }
            }
    }
}
