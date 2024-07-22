package cn.chengzhiya.mhdffriend.utils.menu;

import cn.chengzhiya.mhdffriend.main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.chengzhiya.mhdffriend.utils.Util.Placeholder;
import static cn.chengzhiya.mhdffriend.utils.menu.MenuUtil.getMenu;
import static cn.chengzhiya.mhdffriend.utils.menu.MenuUtil.setMenuItem;

public final class FriendMenuUtil {
    public final static String friendMenuFile = "Friend.yml";

    public static void openFriendMenu(Player player, String friendName) {
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            String title = Placeholder(player, getMenu(friendMenuFile).getString("menu.Title"));
            Inventory menu = Bukkit.createInventory(player, getMenu(friendMenuFile).getInt("menu.Size"), title);

            for (String itemID : Objects.requireNonNull(getMenu(friendMenuFile).getConfigurationSection("menu.ItemList")).getKeys(false)) {
                String type = getMenu(friendMenuFile).getString("menu.ItemList." + itemID + ".Type");
                String displayName = Objects.requireNonNull(getMenu(friendMenuFile).getString("menu.ItemList." + itemID + ".DisplayName")).replaceAll("\\{Friend}", friendName);
                Integer customModelData = getMenu(friendMenuFile).getObject("menu.ItemList." + itemID + ".CustomModelData", Integer.class);
                Integer amount = getMenu(friendMenuFile).getObject("menu.ItemList." + itemID + ".Amount", Integer.class);
                List<String> lore = new ArrayList<>();
                getMenu(friendMenuFile).getStringList("menu.ItemList." + itemID + ".Lore").forEach(s -> lore.add(Placeholder(player, s).replaceAll("\\{Friend}", friendName)));

                List<String> slotList = new ArrayList<>();
                if (getMenu(friendMenuFile).getString("menu.ItemList." + itemID + ".Slot") != null) {
                    slotList.add(getMenu(friendMenuFile).getString("menu.ItemList." + itemID + ".Slot"));
                } else {
                    slotList.addAll(getMenu(friendMenuFile).getStringList("menu.ItemList." + itemID + ".Slots"));
                }

                setMenuItem(menu, friendMenuFile, itemID, type, displayName, lore, customModelData, amount, slotList);
            }
            Bukkit.getScheduler().runTask(main.main, () -> player.openInventory(menu));
        });
    }
}
