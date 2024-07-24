package cn.chengzhiya.mhdffriend.listener;

import cn.chengzhiya.mhdffriend.entity.PlayerData;
import cn.chengzhiya.mhdffriend.main;
import cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil;
import com.alibaba.fastjson.JSONArray;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.ifPlayerDataExists;
import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.updatePlayerDataCache;

public final class initPlayerData implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            if (!ifPlayerDataExists(player.getName())) {
                DatabaseUtil.initPlayerData(new PlayerData(player.getName(), new JSONArray()));
            }
            updatePlayerDataCache(player.getName());
        });
    }
}
