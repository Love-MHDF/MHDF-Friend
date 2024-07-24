package cn.chengzhiya.mhdffriend.listener;

import cn.chengzhiya.mhdffriend.entity.PlayerData;
import cn.chengzhiya.mhdffriend.main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

import static cn.chengzhiya.mhdffriend.utils.BungeeCordUtil.friendJoin;
import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.getPlayerData;

public final class FriendJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            if (!main.main.getConfig().getBoolean("BungeeCordMode")) {
                PlayerData playerData = getPlayerData(player.getName());
                List<String> friendList = playerData.getFriend().toJavaList(String.class);
                if (!friendList.isEmpty()) {
                    for (String friend : friendList) {
                        if (Bukkit.getPlayer(friend) != null) {
                            friendJoin(friend, player.getName());
                        }
                    }
                }
            }
        });
    }
}
