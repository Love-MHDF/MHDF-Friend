package cn.chengzhiya.mhdffriend;

import cn.chengzhiya.mhdffriend.command.Friend;
import cn.chengzhiya.mhdffriend.command.FriendAdmin;
import cn.chengzhiya.mhdffriend.listener.FriendJoin;
import cn.chengzhiya.mhdffriend.listener.FriendQuit;
import cn.chengzhiya.mhdffriend.listener.PluginMessage;
import cn.chengzhiya.mhdffriend.listener.initPlayerData;
import cn.chengzhiya.mhdffriend.listener.menu.ClickFriendListMenu;
import cn.chengzhiya.mhdffriend.listener.menu.ClickFriendMenu;
import cn.chengzhiya.mhdffriend.task.TakeDelay;
import cn.chengzhiya.mhdffriend.task.UpdateData;
import cn.chengzhiya.mhdffriend.task.UpdatePlayerList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static cn.chengzhiya.mhdffriend.utils.Util.initConfig;
import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.closeDatabase;
import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.initDatabase;
import static cn.chengzhiya.mhdfpluginapi.Util.ColorLog;

public final class main extends JavaPlugin {
    public static main main;

    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;

        initConfig();
        initDatabase();

        Objects.requireNonNull(getCommand("friend")).setExecutor(new Friend());
        Objects.requireNonNull(getCommand("friend")).setTabCompleter(new Friend());

        Objects.requireNonNull(getCommand("friendadmin")).setExecutor(new FriendAdmin());
        Objects.requireNonNull(getCommand("friendadmin")).setTabCompleter(new FriendAdmin());

        Bukkit.getPluginManager().registerEvents(new initPlayerData(), this);
        Bukkit.getPluginManager().registerEvents(new FriendJoin(), this);
        Bukkit.getPluginManager().registerEvents(new FriendQuit(), this);
        Bukkit.getPluginManager().registerEvents(new ClickFriendListMenu(), this);
        Bukkit.getPluginManager().registerEvents(new ClickFriendMenu(), this);

        new TakeDelay().runTaskTimerAsynchronously(this, 0L, 20L);
        new UpdatePlayerList().runTaskTimerAsynchronously(this, 0L, 20L);
        new UpdateData().runTaskTimerAsynchronously(this, 0L, 100L);

        if (getConfig().getBoolean("BungeeCordMode")) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessage());
        }

        ColorLog("&f============&6梦之好友&f============");
        ColorLog("&e插件启动完成!");
        ColorLog("&f============&6梦之好友&f============");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        main = null;

        closeDatabase();

        ColorLog("&f============&6梦之好友&f============");
        ColorLog("&e插件已卸载!");
        ColorLog("&f============&6梦之好友&f============");
    }
}
