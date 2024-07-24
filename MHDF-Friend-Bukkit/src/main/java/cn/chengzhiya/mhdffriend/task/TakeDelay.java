package cn.chengzhiya.mhdffriend.task;

import cn.chengzhiya.mhdffriend.entity.RequestData;
import cn.chengzhiya.mhdffriend.utils.FriendUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static cn.chengzhiya.mhdffriend.utils.Util.i18n;

public final class TakeDelay extends BukkitRunnable {
    @Override
    public void run() {
        for (String key : FriendUtil.getFriendRequestHashMap().keySet()) {
            RequestData requestData = FriendUtil.getFriendRequestHashMap().get(key);
            if (requestData != null) {
                if (requestData.getDelay() > 0) {
                    requestData.setDelay(requestData.getDelay() - 1);
                    FriendUtil.getFriendRequestHashMap().put(key, requestData);
                } else {
                    FriendUtil.getFriendRequestHashMap().remove(key);
                    if (Bukkit.getPlayer(key) != null) {
                        Player sender = Bukkit.getPlayer(key);
                        sender.sendMessage(i18n("Command.add.TimeOut.Sender").replaceAll("\\{Player}", requestData.getTargetPlayer()));
                    }
                    if (Bukkit.getPlayer(requestData.getTargetPlayer()) != null) {
                        Player receiver = Bukkit.getPlayer(requestData.getTargetPlayer());
                        receiver.sendMessage(i18n("Command.add.TimeOut.Receiver").replaceAll("\\{Player}", key));
                    }
                }
            }
        }
    }
}
