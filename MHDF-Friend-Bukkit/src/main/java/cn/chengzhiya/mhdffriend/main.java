package cn.chengzhiya.mhdffriend;

import org.bukkit.plugin.java.JavaPlugin;

import static cn.chengzhiya.mhdffriend.utils.Util.loadConfig;

public final class main extends JavaPlugin {
    public static main main;

    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;

        loadConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        main = null;
    }
}
