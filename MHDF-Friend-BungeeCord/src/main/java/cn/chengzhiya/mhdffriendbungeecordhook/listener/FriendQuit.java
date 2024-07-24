package cn.chengzhiya.mhdffriendbungeecordhook.listener;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import static cn.chengzhiya.mhdffriendbungeecordhook.util.BungeeCordUtil.friendQuit;

public final class FriendQuit implements Listener {
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        friendQuit(event.getPlayer().getName());
    }
}
