package cn.chengzhiya.mhdffriend.utils.database;

import cn.chengzhiya.mhdffriend.entity.PlayerData;
import cn.chengzhiya.mhdffriend.main;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseUtil {
    public static HikariDataSource dataSource;

    public static void initDatabase() {
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

    public static void initPlayerData(PlayerData playerData) {
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
        if (ifPlayerDataExists(playerName)) {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM mhdffriend_friend WHERE PlayerName = ?");
                ps.setString(1, playerName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JSONArray friendData = JSON.parseArray(rs.getString("Friend"));
                    return new PlayerData(playerName, friendData);
                }
                rs.close();
                ps.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return new PlayerData(playerName, new JSONArray());
    }

    public static void updatePlayerData(PlayerData playerData) {
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
            if (!friendList.contains(friendName)) {
                friendList.add(friendName);
                playerData.setFriend(JSON.parseArray(JSON.toJSONString(friendList)));
                if (!ifPlayerDataExists(playerName)) {
                    initPlayerData(playerData);
                } else {
                    updatePlayerData(playerData);
                }
            }
        });
    }

    public static void removeFriend(String playerName, String friendName) {
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            PlayerData playerData = getPlayerData(playerName);
            List<String> friendList = playerData.getFriend().toJavaList(String.class);
            if (friendList.contains(friendName)) {
                updatePlayerData(playerData);
            }
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
