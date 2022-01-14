package com.bghddevelopment.LuckPermsGui.groups;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.bghddevelopment.LuckPermsGui.util.Tools;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
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
import com.bghddevelopment.LuckPermsGui.LuckPermsGui;

public class Permissions implements Listener {

    public static Map<Player, Group> addPermission = new HashMap<>();
    public static Map<Player, Group> addTempPermission = new HashMap<>();
    public static Map<Player, Group> checkIfHas = new HashMap<>();

    @EventHandler
    public void onPermissionAdd(AsyncPlayerChatEvent e) {
        if (!addPermission.containsKey(e.getPlayer())) return;
        String message = e.getMessage();
        Group g = addPermission.get(e.getPlayer());

        Tools.sendCommand(e.getPlayer(), "lp group "+g.getName()+" permission set "+message);
        addPermission.remove(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckPermsGui.plugin, () -> {
            EditGroup.open(e.getPlayer(), g);
        }, 5);
        e.setCancelled(true);
    }

    @EventHandler
    public void onTempPermissionAdd(AsyncPlayerChatEvent e) {
        if (!addTempPermission.containsKey(e.getPlayer())) return;
        String message = e.getMessage();
        Group g = addTempPermission.get(e.getPlayer());

        Tools.sendCommand(e.getPlayer(), "lp group "+g.getName()+" permission settemp "+message);
        addTempPermission.remove(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckPermsGui.plugin, () -> {
            EditGroup.open(e.getPlayer(), g);
        }, 5);
        e.setCancelled(true);
    }

    @EventHandler
    public void onPermissionCheck(AsyncPlayerChatEvent e) {
        if (!checkIfHas.containsKey(e.getPlayer())) return;
        String message = e.getMessage();
        Group g = checkIfHas.get(e.getPlayer());

        Tools.sendCommand(e.getPlayer(), "lp group "+g.getName()+" permission check "+message);
        checkIfHas.remove(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(LuckPermsGui.plugin, () -> {
            EditGroup.open(e.getPlayer(), g);
        }, 5);
        e.setCancelled(true);
    }

    static LuckPerms l = LuckPermsProvider.get();

    public static void open(Player p, Group group, int page) {
        Inventory myInventory = Bukkit.createInventory(null, 54, ChatColor.AQUA+"LuckPerms group permissions");
        Tools.onAsync(() -> {


            // ----------------------- INFO ------------------------------
            int weight = group.getWeight().orElse(0);
            ItemStack info = Tools.button(Material.ARMOR_STAND,
                    "&6Info",
                    Arrays.asList(
                            "&cGroup info: &e"+group.getName(),
                            "&cDisplay name: &e"+group.getFriendlyName(),
                            "&cWeight: &e"+weight,
                            "&cCounts:",
                            "  &cNodes: &e"+group.getNodes().size(),
                            "  &cPermissions: &e"+group.getDistinctNodes().size(),
                            "  &cPrefixes: &e"+group.getCachedData().getMetaData().getPrefixes().size(),
                            "  &cSuffixes: &e"+group.getCachedData().getMetaData().getSuffixes().size()),
                    1);
            myInventory.setItem(4, info);
            // ----------------------- INFO ------------------------------

            int sk = 0;
            int i = 9;

            int from = 45*page-1;
            int to = 45*(page+1)-1;
            for (Node permission : group.getDistinctNodes()) {
                if (permission.getType() == NodeType.META) continue;
                if (permission.getType() == NodeType.PREFIX) continue;
                if (permission.getType() == NodeType.SUFFIX) continue;
                if (permission.getType() == NodeType.CHAT_META) continue;
                if (permission.getType() == NodeType.WEIGHT) continue;
                if (from <= sk && sk < to) {
                    String expiration = permission.hasExpiry() ? Tools.getTime(permission.getExpiry().toEpochMilli()) : "Never";
                    String server = permission.getContexts().getAnyValue(DefaultContextKeys.SERVER_KEY).orElse("global");
                    String world = permission.getContexts().getAnyValue(DefaultContextKeys.WORLD_KEY).orElse("global");
                    ItemStack item = Tools.button(Material.TNT,
                            "&6"+permission.getKey(),
                            Arrays.asList(
                                    "&cID: &e"+sk,
                                    "&cExpires in: &e"+expiration,
                                    "&cValue: &e"+permission.getValue(),
                                    "&cServer: &e"+server,
                                    "&cWorld: &e"+world,
                                    "&eClick to remove"
                            ), 1);
                    myInventory.setItem(i, item);
                    i++;
                }
                sk++;
            }

            if (to < sk) {
                ItemStack next = Tools.button(Material.DARK_OAK_WALL_SIGN, "&6Next", Arrays.asList("&eNext page", "&cCurrent: &e"+page), 1);
                myInventory.setItem(53, next);
            }

            ItemStack back = Tools.button(Material.BARRIER, "&6Back", Arrays.asList(""), 1);
            myInventory.setItem(8, back);

        });
        p.openInventory(myInventory);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        if (inv != null && item != null)
            if (e.getView().getTitle().equals(ChatColor.AQUA+"LuckPerms group permissions")) {
                e.setCancelled(true);
                if (item.hasItemMeta())
                    if (item.getItemMeta().hasDisplayName()) {

                        String group = ChatColor.stripColor(inv.getItem(4).getItemMeta().getLore().get(0).split(" ")[2]);
                        Group g =l.getGroupManager().getGroup(group);

                        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if (name.equals("Next")) {
                            int current = Integer.parseInt(ChatColor.stripColor(inv.getItem(53).getItemMeta().getLore().get(1).split(" ")[1]));
                            open(p, g, current+1);
                        } else if (name.equals("Back")) {
                            EditGroup.open(p, g);
                        } else if (!name.equals("Info")) {

                            int id = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0).split(" ")[1]));

                            int sk = 0;
                            for (Node permission : g.getDistinctNodes()) {
                                if (permission.getType() == NodeType.META) continue;
                                if (permission.getType() == NodeType.PREFIX) continue;
                                if (permission.getType() == NodeType.SUFFIX) continue;
                                if (permission.getType() == NodeType.CHAT_META) continue;
                                if (permission.getType() == NodeType.WEIGHT) continue;
                                if (sk == id) {

                                    String server = permission.getContexts().getAnyValue(DefaultContextKeys.SERVER_KEY).orElse("global");
                                    String world = permission.getContexts().getAnyValue(DefaultContextKeys.WORLD_KEY).orElse("global");

                                    if (permission.hasExpiry())
                                        Tools.sendCommand(p, "lp group " + g.getName() + " permission unsettemp " + '"' + permission.getKey() + '"' + " " + server + " " + world);
                                    else
                                        Tools.sendCommand(p, "lp group " + g.getName() + " permission unset " + '"' + permission.getKey() + '"' + " " + server + " " + world);
                                    break;
                                }
                                sk++;
                            }

                            int current = 0;
                            if (inv.getItem(53) != null)
                                current = Integer.parseInt(ChatColor.stripColor(inv.getItem(53).getItemMeta().getLore().get(1).split(" ")[1]));

                            int page = current;
                            Bukkit.getScheduler().runTaskLater(LuckPermsGui.plugin, () -> {
                                open(p, g, page);
                            }, 5);
                        }
                    }
            }
    }
}
