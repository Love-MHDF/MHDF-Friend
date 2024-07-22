package cn.chengzhiya.mhdffriend.utils.menu;

import cn.chengzhiya.mhdffriend.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.chengzhiya.mhdffriend.utils.Util.Placeholder;
import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.getPlayerFriendList;
import static cn.chengzhiya.mhdffriend.utils.menu.FriendMenuUtil.openFriendMenu;
import static cn.chengzhiya.mhdffriend.utils.menu.MenuUtil.*;
import static cn.chengzhiya.mhdfpluginapi.Util.ChatColor;

public final class FriendListMenuUtil {
    public final static String friendListMenuFile = "FriendList.yml";

    public static void openFriendListMenu(Player player, int page) {
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            String title = Placeholder(player, getMenu(friendListMenuFile).getString("menu.Title")).replaceAll("\\{Page}", String.valueOf(page));
            Inventory menu = Bukkit.createInventory(player, getMenu(friendListMenuFile).getInt("menu.Size"), title);

            int FriendSize = getMenu(friendListMenuFile).getInt("menu.FriendSize");

            List<String> playerFriendList = getPlayerFriendList(player.getName(), FriendSize, FriendSize * (page - 1));
            List<String> nextPagePlayerFriendList = getPlayerFriendList(player.getName(), FriendSize, FriendSize * (page));

            for (String itemID : Objects.requireNonNull(getMenu(friendListMenuFile).getConfigurationSection("menu.ItemList")).getKeys(false)) {
                String itemType = getMenu(friendListMenuFile).getString("menu.ItemList." + itemID + ".ItemType");
                String type = getMenu(friendListMenuFile).getString("menu.ItemList." + itemID + ".Type");
                String displayName = getMenu(friendListMenuFile).getString("menu.ItemList." + itemID + ".DisplayName");
                List<String> lore = new ArrayList<>();
                Integer customModelData = getMenu(friendListMenuFile).getObject("menu.ItemList." + itemID + ".CustomModelData", Integer.class);
                Integer amount = getMenu(friendListMenuFile).getObject("menu.ItemList." + itemID + ".Amount", Integer.class);

                List<String> slotList = new ArrayList<>();
                if (getMenu("HomeMenu.yml").getString("menu.ItemList." + itemID + ".Slot") != null) {
                    slotList.add(getMenu("HomeMenu.yml").getString("menu.ItemList." + itemID + ".Slot"));
                } else {
                    slotList.addAll(getMenu("HomeMenu.yml").getStringList("menu.ItemList." + itemID + ".Slots"));
                }

                if (itemType != null) {
                    switch (itemType) {
                        case "Friend": {
                            int i = 0;
                            if (!playerFriendList.isEmpty()) {
                                for (String friend : playerFriendList) {
                                    String homeDisplayName = null;
                                    List<String> homeLore = new ArrayList<>();

                                    if (displayName != null) {
                                        homeDisplayName = displayName.replaceAll("\\{Friend}", friend);
                                    }

                                    getMenu(friendListMenuFile).getStringList("menu.ItemList." + itemID + ".Lore").forEach(s ->
                                            homeLore.add(Placeholder(player, s).replaceAll("\\{Friend}", friend))
                                    );

                                    ItemStack item = getMenuItem(friendListMenuFile, itemID, type, homeDisplayName, homeLore, customModelData, amount);

                                    if (!slotList.isEmpty()) {
                                        menu.setItem(getSlot(slotList).get(i), item);
                                    } else {
                                        menu.addItem(item);
                                    }
                                    i++;
                                }
                            }
                            continue;
                        }
                        case "PageUp": {
                            if (page <= 1) {
                                continue;
                            }
                        }
                        case "PageNext": {
                            if (nextPagePlayerFriendList.isEmpty()) {
                                continue;
                            }
                        }
                    }
                }

                if (displayName != null) {
                    displayName = displayName.replaceAll("\\{Page}", String.valueOf(page));
                }
                getMenu(friendListMenuFile).getStringList("menu.ItemList." + itemID + ".Lore").forEach(s ->
                        lore.add(Placeholder(player, s).replaceAll("\\{Page}", String.valueOf(page)))
                );

                getMenu(friendListMenuFile).getStringList("menu.ItemList." + itemID + ".Lore").forEach(s -> lore.add(Placeholder(player, s)));

                setMenuItem(menu, friendListMenuFile, itemID, type, displayName, lore, customModelData, amount, slotList);
            }
            Bukkit.getScheduler().runTask(main.main, () -> player.openInventory(menu));
        });
    }

    public static void runAction(Player player, String menuFileName, int page, List<String> actionList) {
        for (String action : actionList) {
            switch (action) {
                case "[PageUp]":
                    openFriendListMenu(player, page - 1);
                    break;
                case "[PageNext]":
                    openFriendListMenu(player, page + 1);
                    break;
                case "[Friend]":
                    break;
                default:
                    MenuUtil.runAction(player, menuFileName, action.split("\\|"));
                    break;
            }
        }
    }

    private static void handleFriendAction(Player player, ItemStack clickItem) {
        String friendName = getFriendName(clickItem);
        if (friendName != null) {
            openFriendMenu(player, friendName);
        }
    }

    private static String getFriendName(ItemStack clickItem) {
        try {
            String displayName = ChatColor.stripColor(clickItem.getItemMeta().getDisplayName());
            String menuDisplayName = ChatColor.stripColor(ChatColor(getMenu(friendListMenuFile).getString("menu.ItemList.好友图标.DisplayName")));
            return getPlaceholder(displayName, menuDisplayName, "{Friend}");
        } catch (NullPointerException e) {
            return null;
        }
    }
}
