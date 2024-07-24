package cn.chengzhiya.mhdffriend.utils.database;

import cn.chengzhiya.mhdffriend.entity.PlayerData;
import cn.chengzhiya.mhdffriend.main;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public final class DatabaseUtil {
    public static HikariDataSource dataSource;
    @Getter
    public static HashMap<String, PlayerData> playerDataHashMap = new HashMap<>();

    public static void initDatabase() {
        {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + main.main.getConfig().getString("DatabaseSettings.Host") + "/" + main.main.getConfig().getString("DatabaseSettings.Database") + "?autoReconnect=true&serverTimezone=" + TimeZone.getDefault().getID());
            config.setUsername(main.main.getConfig().getString("DatabaseSettings.User"));
            config.setPassword(main.main.getConfig().getString("DatabaseSettings.Password"));
            config.addDataSourceProperty("useUnicode", "true");
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.setMaximumPoolSize(60);

            dataSource = new HikariDataSource(config);
        }

        {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `mhdffriend_friend` (" +
                                "`PlayerName` VARCHAR(50) NOT NULL DEFAULT ''," +
                                "`Friend` LONGTEXT NOT NULL DEFAULT ''," +
                                "PRIMARY KEY (`PlayerName`)) " +
                                "COLLATE='utf8mb4_general_ci';"
                )) {
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void closeDatabase() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public static void initPlayerData(PlayerData playerData) {
        getPlayerDataHashMap().put(playerData.getPlayerName(), playerData);
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement("INSERT INTO mhdffriend_friend (PlayerName, Friend) VALUES (?,?)")) {
                    ps.setString(1, playerData.getPlayerName());
                    ps.setString(2, playerData.getFriend().toJSONString());
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static boolean ifPlayerDataExists(String playerName) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM mhdffriend_friend WHERE PlayerName = ? LIMIT 1")) {
                ps.setString(1, playerName);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static PlayerData getPlayerData(String playerName) {
        if (getPlayerDataHashMap().get(playerName) == null) {
            if (ifPlayerDataExists(playerName)) {
                return updatePlayerDataCache(playerName);
            }
        } else {
            return getPlayerDataHashMap().get(playerName);
        }
        return new PlayerData(playerName, new JSONArray());
    }

    public static PlayerData updatePlayerDataCache(String playerName) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM mhdffriend_friend WHERE PlayerName = ?")) {
                ps.setString(1, playerName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        JSONArray friendData = JSON.parseArray(rs.getString("Friend"));
                        PlayerData playerData = new PlayerData(playerName, friendData);
                        getPlayerDataHashMap().put(playerName, playerData);
                        return playerData;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        getPlayerDataHashMap().put(playerName, new PlayerData(playerName, new JSONArray()));
        return new PlayerData(playerName, new JSONArray());
    }

    public static void updatePlayerData(PlayerData playerData) {
        getPlayerDataHashMap().put(playerData.getPlayerName(), playerData);
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("UPDATE mhdffriend_friend SET Friend=? WHERE PlayerName = ?")) {
                ps.setString(1, playerData.getFriend().toJSONString());
                ps.setString(2, playerData.getPlayerName());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addFriend(String playerName, String friendName) {
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            PlayerData playerData = getPlayerData(playerName);
            List<String> friendList = playerData.getFriend().toJavaList(String.class);
            friendList.add(friendName);
            playerData.setFriend(JSON.parseArray(JSON.toJSONString(friendList)));
            if (!ifPlayerDataExists(playerName)) {
                initPlayerData(playerData);
            } else {
                updatePlayerData(playerData);
            }
        });
    }

    public static void removeFriend(String playerName, String friendName) {
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            PlayerData playerData = getPlayerData(playerName);
            List<String> friendList = playerData.getFriend().toJavaList(String.class);
            friendList.remove(friendName);
            playerData.setFriend(JSON.parseArray(JSON.toJSONString(friendList)));
            updatePlayerData(playerData);
        });
    }

    public static List<String> getPlayerFriendList(String playerName, int size, int min) {
        PlayerData playerData = getPlayerData(playerName);
        List<String> friendList = playerData.getFriend().toJavaList(String.class);
        if (friendList.size() >= min) {
            int max = size + min;
            if (friendList.size() < max) {
                max = friendList.size();
            }
            return friendList.subList(min, max);
        }
        return new ArrayList<>();
    }
}
