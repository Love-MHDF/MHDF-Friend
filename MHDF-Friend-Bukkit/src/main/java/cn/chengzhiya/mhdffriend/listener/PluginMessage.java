package cn.chengzhiya.mhdffriend.listener;

import cn.chengzhiya.mhdffriend.entity.PlayerData;
import cn.chengzhiya.mhdffriend.entity.RequestData;
import cn.chengzhiya.mhdffriend.main;
import cn.chengzhiya.mhdffriend.utils.FriendUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import static cn.chengzhiya.mhdffriend.utils.BungeeCordUtil.*;
import static cn.chengzhiya.mhdffriend.utils.FriendUtil.*;
import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.getPlayerData;

public final class PluginMessage implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

        try {
            String subchannel = in.readUTF();

            switch (subchannel) {
                case "PlayerList": {
                    in.readUTF();
                    onlinePlayerList.addAll(Arrays.asList(in.readUTF().split(", ")));
                    break;
                }
                case "friendJoin": {
                    String playerName = in.readUTF();
                    Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
                        PlayerData playerData = getPlayerData(playerName);
                        for (String friendName : playerData.getFriend().toJavaList(String.class)) {
                            sendFriendJoinMessage(friendName, playerName);
                        }
                    });
                    break;
                }
                case "friendQuit": {
                    String playerName = in.readUTF();
                    Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
                        PlayerData playerData = getPlayerData(playerName);
                        for (String friendName : playerData.getFriend().toJavaList(String.class)) {
                            sendFriendQuitMessage(friendName, playerName);
                        }
                    });
                    break;
                }
                case "addFriendRequest": {
                    String playerName = in.readUTF();
                    String friendName = in.readUTF();

                    Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
                        FriendUtil.getFriendRequestHashMap().put(playerName, new RequestData(friendName, main.main.getConfig().getInt("Delay")));
                        sendAddFriendRequest(playerName, friendName);
                    });
                    break;
                }
                case "acceptFriendRequest": {
                    String playerName = in.readUTF();
                    String friendName = in.readUTF();

                    Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
                        FriendUtil.getFriendRequestHashMap().remove(friendName);
                        sendAcceptFriendRequestMessage(friendName, playerName);
                    });
                    break;
                }
                case "defuseFriendRequest": {
                    String playerName = in.readUTF();
                    String friendName = in.readUTF();

                    Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
                        FriendUtil.getFriendRequestHashMap().remove(friendName);
                        sendDefuseFriendRequestMessage(friendName, playerName);
                    });
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
