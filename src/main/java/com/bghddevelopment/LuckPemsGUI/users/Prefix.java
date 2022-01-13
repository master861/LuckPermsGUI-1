package com.master86.Luckpermsgui.users;
import java.util.*;

import com.master86.Luckpermsgui.Luckpermsgui;
import com.master86.Luckpermsgui.util.Tools;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
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

public class Prefix implements Listener {

    public static Map<Player, User> addPrefix = new HashMap<>();
    public static Map<Player, User> addTempPrefix = new HashMap<>();

    @EventHandler
    public void onaddParent(AsyncPlayerChatEvent e) {
        if (!addPrefix.containsKey(e.getPlayer())) return;
        String message = e.getMessage();
        User g = addPrefix.get(e.getPlayer());

        Tools.sendCommand(e.getPlayer(), "lp user "+g.getUsername()+" meta addprefix "+message);
        addPrefix.remove(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(Luckpermsgui.plugin, () -> {
            EditUser.open(e.getPlayer(), g);
        }, 5);
        e.setCancelled(true);
    }

    @EventHandler
    public void onaddTempParent(AsyncPlayerChatEvent e) {
        if (!addTempPrefix.containsKey(e.getPlayer())) return;
        String message = e.getMessage();
        User g = addTempPrefix.get(e.getPlayer());

        Tools.sendCommand(e.getPlayer(), "lp user "+g.getUsername()+" meta addtempprefix "+message);
        addTempPrefix.remove(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(Luckpermsgui.plugin, () -> {
            EditUser.open(e.getPlayer(), g);
        }, 5);
        e.setCancelled(true);
    }

    static LuckPerms l = LuckPermsProvider.get();

    public static void open(Player p, User user, int page) {
        Inventory myInventory = Bukkit.createInventory(null, 54, ChatColor.AQUA+"LuckPerms user prefixs");
        Tools.onAsync(() -> {


            // ----------------------- INFO ------------------------------
            ItemStack info = Tools.button(Material.ARMOR_STAND,
                    "&6Info",
                    Arrays.asList(
                            "&cName: &e"+user.getUsername(),
                            "&cUUID: &e"+user.getUniqueId(),
                            "&cGroup: &e"+user.getPrimaryGroup(),
                            "&cCounts:",
                            "&c   Nodes: &e"+user.getNodes().size(),
                            "&c   Permissions: &e"+user.getDistinctNodes().size(),
                            "&c   Prefixes: &e"+user.getCachedData().getMetaData().getPrefixes().size(),
                            "&c   Suffixes: &e"+user.getCachedData().getMetaData().getSuffixes().size(),
                            "&cCached data:",
                            "&c   Current prefix: &e"+user.getCachedData().getMetaData().getPrefix(),
                            "&c   Current suffix: &e"+user.getCachedData().getMetaData().getSuffix()
                    ),
                    1);
            myInventory.setItem(4, info);
            // ----------------------- INFO ------------------------------

            int sk = 0;
            int i = 9;

            int from = 45*page-1;
            int to = 45*(page+1)-1;
            for (Node permission : user.getDistinctNodes()) {
                if (permission.getType() != NodeType.PREFIX) continue;
                if (from <= sk && sk < to) {
                    PrefixNode prefix = ((PrefixNode) permission);
                    String expiration = permission.hasExpiry() ? Tools.getTime(permission.getExpiry().toEpochMilli()) : "Never";
                    String server = permission.getContexts().getAnyValue(DefaultContextKeys.SERVER_KEY).orElse("global");
                    String world = permission.getContexts().getAnyValue(DefaultContextKeys.WORLD_KEY).orElse("global");
                    ItemStack item = Tools.button(Material.TNT,
                            "&6"+prefix.getMetaValue(),
                            Arrays.asList(
                                    "&cID: &e"+sk,
                                    "&cPosition: &e"+prefix.getPriority(),
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
            if (e.getView().getTitle().equals(ChatColor.AQUA+"LuckPerms user prefixs")) {
                e.setCancelled(true);
                if (item.hasItemMeta())
                    if (item.getItemMeta().hasDisplayName()) {

                        String group = ChatColor.stripColor(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(inv.getItem(4)).getItemMeta()).getLore()).get(0).split(" ")[1]);
                        User g = l.getUserManager().getUser(group);

                        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if (name.equals("Next")) {
                            int current = Integer.parseInt(ChatColor.stripColor(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(inv.getItem(53)).getItemMeta()).getLore()).get(1).split(" ")[1]));
                            open(p, g, current+1);
                        } else if (name.equals("Back")) {
                            EditUser.open(p, g);
                        } else if (!name.equals("Info")) {

                            int id = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0).split(" ")[1]));

                            int sk = 0;
                            assert g != null;
                            for (Node permission : g.getDistinctNodes()) {
                                if (permission.getType() != NodeType.PREFIX) continue;
                                if (sk == id) {
                                    PrefixNode prefix = ((PrefixNode) permission);

                                    String server = permission.getContexts().getAnyValue(DefaultContextKeys.SERVER_KEY).orElse("global");
                                    String world = permission.getContexts().getAnyValue(DefaultContextKeys.WORLD_KEY).orElse("global");

                                    if (permission.hasExpiry())
                                        Tools.sendCommand(p, "lp user " + g.getUsername() + " meta removetempprefix " + prefix.getPriority() + " " + '"' + prefix.getMetaValue() + '"' + " " + server + " " + world);
                                    else
                                        Tools.sendCommand(p, "lp user " + g.getUsername() + " meta removeprefix " + prefix.getPriority() + " " + '"' + prefix.getMetaValue() + '"' + " " + server + " " + world);
                                    break;
                                }
                                sk++;
                            }

                            int current = 0;
                            if (inv.getItem(53) != null)
                                current = Integer.parseInt(ChatColor.stripColor(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(inv.getItem(53)).getItemMeta()).getLore()).get(1).split(" ")[1]));

                            int page = current;
                            Bukkit.getScheduler().runTaskLater(Luckpermsgui.plugin, () -> {
                                open(p, g, page);
                            }, 5);
                        }
                    }
            }
    }
}