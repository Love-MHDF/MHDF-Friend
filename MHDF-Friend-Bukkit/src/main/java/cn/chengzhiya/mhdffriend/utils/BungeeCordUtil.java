package cn.chengzhiya.mhdffriend.utils;

import cn.chengzhiya.mhdffriend.main;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.chengzhiya.mhdffriend.utils.FriendUtil.sendAcceptFriendRequestMessage;
import static cn.chengzhiya.mhdffriend.utils.FriendUtil.sendDefuseFriendRequestMessage;
import static cn.chengzhiya.mhdffriend.utils.Util.i18n;
import static cn.chengzhiya.mhdffriend.utils.menu.MenuUtil.getPlayerHead;

public final class BungeeCordUtil {
    public static List<String> onlinePlayerList = new ArrayList<>();

    public static boolean ifPlayerOnline(String playerName) {
        updateOnlinePlayerList();
        return onlinePlayerList.contains(playerName);
    }

    public static void updateOnlinePlayerList() {
        if (main.main.getConfig().getBoolean("BungeeCordMode")) {
            Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
            if (player != null) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("PlayerList");
                out.writeUTF("ALL");

                player.sendPluginMessage(main.main, "BungeeCord", out.toByteArray());
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                onlinePlayerList.add(player.getName());
            }
        }
    }

    public static void acceptFriendRequest(String playerName, String friendName) {
        if (main.main.getConfig().getBoolean("BungeeCordMode")) {
            Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
            if (player != null) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("acceptFriendRequest");
                out.writeUTF(playerName);
                out.writeUTF(friendName);

                player.sendPluginMessage(main.main, "BungeeCord", out.toByteArray());
            }
        } else {
            if (Bukkit.getPlayer(friendName) != null) {
                sendAcceptFriendRequestMessage(friendName, playerName);
            }
        }
        sendAcceptFriendRequestMessage(playerName, friendName);
    }

    public static void defuseFriendRequest(String playerName, String friendName) {
        if (main.main.getConfig().getBoolean("BungeeCordMode")) {
            Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
            if (player != null) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("defuseFriendRequest");
                out.writeUTF(playerName);
                out.writeUTF(friendName);

                player.sendPluginMessage(main.main, "BungeeCord", out.toByteArray());
            }
        } else {
            if (Bukkit.getPlayer(friendName) != null) {
                sendDefuseFriendRequestMessage(friendName, playerName);
            }
        }
    }

    public static void friendJoin(String playerName, String friendName) {
        if (ifPlayerOnline(friendName)) {
            if (!main.main.getConfig().getBoolean("BungeeCordMode")) {
                sendFriendJoinMessage(playerName, friendName);
            }
        }
    }

    public static void friendQuit(String playerName, String friendName) {
        if (ifPlayerOnline(friendName)) {
            if (!main.main.getConfig().getBoolean("BungeeCordMode")) {
                sendFriendQuitMessage(playerName, friendName);
            }
        }
    }

    public static void sendFriendJoinMessage(String playerName, String friendName) {
        if (Bukkit.getPlayer(playerName) != null) {
            Player player = Bukkit.getPlayer(playerName);
            switch (Objects.requireNonNull(main.main.getConfig().getString("MessageSettings.DefuseRequest"))) {
                case "ActionBar": {
                    Objects.requireNonNull(player).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(i18n("FriendJoin.Message").replaceAll("\\{Player}", friendName)));
                    break;
                }
                case "Toast": {
                    if (Bukkit.getPluginManager().getPlugin("UltimateAdvancementAPI") != null) {
                        UltimateAdvancementAPI ultimateAdvancementAPI = UltimateAdvancementAPI.getInstance(main.main);
                        ItemStack playerHead = getPlayerHead(friendName, null, null, null, null);
                        String message = i18n("FriendJoin.Message").replaceAll("\\{Player}", friendName);
                        ultimateAdvancementAPI.displayCustomToast(Objects.requireNonNull(player), playerHead, message, AdvancementFrameType.TASK);
                        break;
                    }
                }
                default: {
                    Objects.requireNonNull(player).sendMessage(i18n("FriendJoin.Message").replaceAll("\\{Player}", friendName));
                    break;
                }
            }
        }
    }

    public static void sendFriendQuitMessage(String playerName, String friendName) {
        if (Bukkit.getPlayer(playerName) != null) {
            Player player = Bukkit.getPlayer(playerName);
            switch (Objects.requireNonNull(main.main.getConfig().getString("MessageSettings.DefuseRequest"))) {
                case "ActionBar": {
                    Objects.requireNonNull(player).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(i18n("FriendQuit.Message").replaceAll("\\{Player}", friendName)));
                    break;
                }
                case "Toast": {
                    if (Bukkit.getPluginManager().getPlugin("UltimateAdvancementAPI") != null) {
                        UltimateAdvancementAPI ultimateAdvancementAPI = UltimateAdvancementAPI.getInstance(main.main);
                        ItemStack playerHead = getPlayerHead(friendName, null, null, null, null);
                        String message = i18n("FriendQuit.Message").replaceAll("\\{Player}", friendName);
                        ultimateAdvancementAPI.displayCustomToast(Objects.requireNonNull(player), playerHead, message, AdvancementFrameType.TASK);
                        break;
                    }
                }
                default: {
                    Objects.requireNonNull(player).sendMessage(i18n("FriendQuit.Message").replaceAll("\\{Player}", friendName));
                    break;
                }
            }
        }
    }
}
