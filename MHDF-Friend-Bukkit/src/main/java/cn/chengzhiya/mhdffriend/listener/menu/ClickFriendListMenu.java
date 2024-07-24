package cn.chengzhiya.mhdffriend.listener.menu;

import cn.chengzhiya.mhdffriend.utils.menu.FriendListMenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Objects;

import static cn.chengzhiya.mhdffriend.utils.menu.FriendListMenuUtil.friendListMenuFile;
import static cn.chengzhiya.mhdffriend.utils.menu.MenuUtil.*;
import static cn.chengzhiya.mhdfpluginapi.Util.ChatColor;

public final class ClickFriendListMenu implements Listener {
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().contains(ChatColor(Objects.requireNonNull(getMenu(friendListMenuFile).getString("menu.Title")).split("\\{Page}")[0]))) {
            if (event.getCurrentItem() != null) {
                event.setCancelled(true);
                if (getMenuFromItem(event.getCurrentItem()) != null && getItemNameFromItem(event.getCurrentItem()) != null) {
                    Player player = (Player) event.getWhoClicked();
                    int page = Integer.parseInt(getPlaceholder(event.getView().getTitle(), getMenu(friendListMenuFile).getString("menu.Title"), "{Page}"));
                    String Item = getItemNameFromItem(event.getCurrentItem());
                    if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) {
                        List<String> DenyActionList = ifAllowClick(player, friendListMenuFile, Item, false);
                        if (DenyActionList.isEmpty()) {
                            FriendListMenuUtil.runAction(player, friendListMenuFile, page, event.getCurrentItem(), getMenu(friendListMenuFile).getStringList("menu.ItemList." + Item + ".ClickAction"));
                        } else {
                            FriendListMenuUtil.runAction(player, friendListMenuFile, page, event.getCurrentItem(), DenyActionList);
                        }
                    }
                    if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                        List<String> DenyActionList = ifAllowClick(player, friendListMenuFile, Item, true);
                        if (DenyActionList.isEmpty()) {
                            FriendListMenuUtil.runAction(player, friendListMenuFile, page, event.getCurrentItem(), getMenu(friendListMenuFile).getStringList("menu.ItemList." + Item + ".ShiftClickAction"));
                        } else {
                            FriendListMenuUtil.runAction(player, friendListMenuFile, page, event.getCurrentItem(), DenyActionList);
                        }
                    }
                }
            }
        }
    }
}
