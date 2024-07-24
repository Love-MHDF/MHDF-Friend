package cn.chengzhiya.mhdffriend.listener;

import cn.chengzhiya.mhdffriend.entity.PlayerData;
import cn.chengzhiya.mhdffriend.main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

import static cn.chengzhiya.mhdffriend.utils.BungeeCordUtil.friendQuit;
import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.getPlayerData;

public final class FriendQuit implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            if (!main.main.getConfig().getBoolean("BungeeCordMode")) {
                PlayerData playerData = getPlayerData(player.getName());
                List<String> friendList = playerData.getFriend().toJavaList(String.class);
                if (!friendList.isEmpty()) {
                    for (String friend : friendList) {
                        if (Bukkit.getPlayer(friend) != null) {
                            friendQuit(friend, player.getName());
                        }
                    }
                }
            }
        });
    }
}
