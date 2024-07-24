package cn.chengzhiya.mhdffriend.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.updatePlayerDataCache;

public final class UpdateData extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerDataCache(player.getName());
        }
    }
}
