package cn.chengzhiya.mhdffriend.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static cn.chengzhiya.mhdffriend.utils.BungeeCordUtil.updateOnlinePlayerList;

public final class UpdatePlayerList extends BukkitRunnable {
    @Override
    public void run() {
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            updateOnlinePlayerList();
        }
    }
}
