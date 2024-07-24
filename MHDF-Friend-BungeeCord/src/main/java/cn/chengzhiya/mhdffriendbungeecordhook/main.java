package cn.chengzhiya.mhdffriendbungeecordhook;

import cn.chengzhiya.mhdffriendbungeecordhook.listener.FriendJoin;
import cn.chengzhiya.mhdffriendbungeecordhook.listener.FriendQuit;
import cn.chengzhiya.mhdffriendbungeecordhook.listener.PluginMessage;
import net.md_5.bungee.api.plugin.Plugin;

public final class main extends Plugin {
    public static main main;

    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;

        getProxy().getPluginManager().registerListener(this, new PluginMessage());
        getProxy().getPluginManager().registerListener(this, new FriendJoin());
        getProxy().getPluginManager().registerListener(this, new FriendQuit());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        main = null;

    }
}
