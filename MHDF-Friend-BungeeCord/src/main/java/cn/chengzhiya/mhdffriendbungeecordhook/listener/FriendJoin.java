package cn.chengzhiya.mhdffriendbungeecordhook.listener;

import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import static cn.chengzhiya.mhdffriendbungeecordhook.util.BungeeCordUtil.friendJoin;

public final class FriendJoin implements Listener {
    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (event.getReason() == ServerConnectEvent.Reason.JOIN_PROXY) {
            if (!event.isCancelled()) {
                friendJoin(event.getPlayer().getName());
            }
        }
    }
}
