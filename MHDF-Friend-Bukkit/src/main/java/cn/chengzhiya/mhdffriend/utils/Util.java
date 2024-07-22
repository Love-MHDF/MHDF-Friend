package cn.chengzhiya.mhdffriend.utils;

import cn.chengzhiya.mhdffriend.main;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static cn.chengzhiya.mhdfpluginapi.Util.ChatColor;

public final class Util {
    public static YamlConfiguration Lang;

    public static String Placeholder(OfflinePlayer player, String message) {
        return ChatColor(PlaceholderAPI.setPlaceholders(player, message));
    }

    public static void saveConfig() {

    }

    public static void loadConfig() {
        main.main.reloadConfig();
        Lang = YamlConfiguration.loadConfiguration(new File(main.main.getDataFolder(), "lang.yml"));
    }

    public static String i18n(String key) {
        if (Lang == null) {
            Lang = YamlConfiguration.loadConfiguration(new File(main.main.getDataFolder(), "lang.yml"));
        }
        return Placeholder(null, Lang.getString(key));
    }
}
