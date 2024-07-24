package cn.chengzhiya.mhdffriend.listener.menu;

import cn.chengzhiya.mhdffriend.utils.menu.FriendMenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Objects;

import static cn.chengzhiya.mhdffriend.utils.menu.FriendMenuUtil.friendMenuFile;
import static cn.chengzhiya.mhdffriend.utils.menu.MenuUtil.*;
import static cn.chengzhiya.mhdfpluginapi.Util.ChatColor;

public final class ClickFriendMenu implements Listener {
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().contains(ChatColor(Objects.requireNonNull(getMenu(friendMenuFile).getString("menu.Title")).split("\\{Friend}")[0]))) {
            if (event.getCurrentItem() != null) {
                event.setCancelled(true);
                if (getMenuFromItem(event.getCurrentItem()) != null && getItemNameFromItem(event.getCurrentItem()) != null) {
                    Player player = (Player) event.getWhoClicked();
                    String Item = getItemNameFromItem(event.getCurrentItem());
                    if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) {
                        List<String> DenyActionList = ifAllowClick(player, friendMenuFile, Item, false);
                        if (DenyActionList.isEmpty()) {
                            FriendMenuUtil.runAction(player, friendMenuFile, event.getView().getTitle(), getMenu(friendMenuFile).getStringList("menu.ItemList." + Item + ".ClickAction"));
                        } else {
                            FriendMenuUtil.runAction(player, friendMenuFile, event.getView().getTitle(), DenyActionList);
                        }
                    }
                    if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                        List<String> DenyActionList = ifAllowClick(player, friendMenuFile, Item, true);
                        if (DenyActionList.isEmpty()) {
                            FriendMenuUtil.runAction(player, friendMenuFile, event.getView().getTitle(), getMenu(friendMenuFile).getStringList("menu.ItemList." + Item + ".ShiftClickAction"));
                        } else {
                            FriendMenuUtil.runAction(player, friendMenuFile, event.getView().getTitle(), DenyActionList);
                        }
                    }
                }
            }
        }
    }
}