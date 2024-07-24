package cn.chengzhiya.mhdffriend.utils.menu;

import cn.chengzhiya.mhdffriend.main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.chengzhiya.mhdffriend.utils.Util.Placeholder;
import static cn.chengzhiya.mhdffriend.utils.menu.MenuUtil.*;

public final class FriendMenuUtil {
    public final static String friendMenuFile = "Friend.yml";

    public static void openFriendMenu(Player player, String friendName) {
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            String title = Placeholder(player, getMenu(friendMenuFile).getString("menu.Title")).replaceAll("\\{Friend}", friendName);
            Inventory menu = Bukkit.createInventory(player, getMenu(friendMenuFile).getInt("menu.Size"), title);

            for (String itemID : Objects.requireNonNull(getMenu(friendMenuFile).getConfigurationSection("menu.ItemList")).getKeys(false)) {
                String type = placeholder(getMenu(friendMenuFile).getString("menu.ItemList." + itemID + ".Type"), player, Bukkit.getOfflinePlayer(friendName));
                String displayName = placeholder(getMenu(friendMenuFile).getString("menu.ItemList." + itemID + ".DisplayName"), player, Bukkit.getOfflinePlayer(friendName));
                Integer customModelData = getMenu(friendMenuFile).getObject("menu.ItemList." + itemID + ".CustomModelData", Integer.class);
                Integer amount = getMenu(friendMenuFile).getObject("menu.ItemList." + itemID + ".Amount", Integer.class);
                List<String> lore = new ArrayList<>();
                getMenu(friendMenuFile).getStringList("menu.ItemList." + itemID + ".Lore").forEach(s -> lore.add(placeholder(s, player, Bukkit.getOfflinePlayer(friendName))));

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

    public static void runAction(Player player, String menuFileName, String title, List<String> actionList) {
        for (String action : actionList) {
            String friendName = getPlaceholder(title, getMenu(friendMenuFile).getString("menu.Title"), "{Friend}");
            MenuUtil.runAction(player, menuFileName, placeholder(action, player, Bukkit.getOfflinePlayer(friendName)).split("\\|"));
        }
    }

    public static String placeholder(String message, OfflinePlayer player, OfflinePlayer friend) {
        message = message.replace("{Friend}", Objects.requireNonNull(friend.getName()));
        Pattern pattern = Pattern.compile("\\?(.*?)\\?");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            message = message.replaceAll("\\?" + placeholder + "\\?", Placeholder(friend, placeholder));
        }
        message = Placeholder(player, message);
        return message;
    }
}
