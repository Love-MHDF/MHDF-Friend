package cn.chengzhiya.mhdffriend.utils;

import cn.chengzhiya.mhdffriend.entity.RequestData;
import cn.chengzhiya.mhdffriend.main;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

import static cn.chengzhiya.mhdffriend.utils.Util.i18n;
import static cn.chengzhiya.mhdffriend.utils.menu.MenuUtil.getPlayerHead;
import static cn.chengzhiya.mhdfpluginapi.Util.ChatColor;

public final class FriendUtil {
    @Getter
    public static HashMap<String, RequestData> friendRequestHashMap = new HashMap<>();

    public static void sendAddFriendRequest(String playerName, String friendName) {
        if (Bukkit.getPlayer(friendName) == null && main.main.getConfig().getBoolean("BungeeCordMode")) {
            Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
            if (player != null) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("addFriendRequest");
                out.writeUTF(playerName);
                out.writeUTF(friendName);

                player.sendPluginMessage(main.main, "BungeeCord", out.toByteArray());
            }
        } else {
            if (Bukkit.getPlayer(friendName) != null) {
                Player friendPlayer = Bukkit.getPlayer(friendName);
                friendPlayer.spigot().sendMessage(getAddFriendRequestMessage(playerName));
            }
        }
    }

    public static TextComponent getAddFriendRequestMessage(String sender) {
        TextComponent message = new TextComponent();
        for (String messages : i18n("Command.add.RequestMessage").split("\\?")) {
            if (messages.equals("Accept")) {
                TextComponent messageButton = new TextComponent(i18n("Command.add.Accept"));
                messageButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + sender));
                messageButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(i18n("Command.add.AcceptHover").replaceAll("\\{Player}", sender))));
                message.addExtra(messageButton);
            } else {
                if (messages.equals("Defuse")) {
                    TextComponent messageButton = new TextComponent(i18n("Command.add.Defuse"));
                    messageButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend defuse " + sender));
                    messageButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(i18n("Command.add.DefuseHover").replaceAll("\\{Player}", sender))));
                    message.addExtra(messageButton);
                } else {
                    message.addExtra(new TextComponent(ChatColor(messages.replaceAll("\\{Player}", sender))));
                }
            }
        }
        return message;
    }

    public static void sendAcceptFriendRequestMessage(String playerName, String friend) {
        if (Bukkit.getPlayer(playerName) != null) {
            Player player = Bukkit.getPlayer(playerName);
            switch (Objects.requireNonNull(main.main.getConfig().getString("MessageSettings.AcceptRequest"))) {
                case "ActionBar": {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(i18n("Command.accept.Message").replaceAll("\\{Player}", friend)));
                    break;
                }
                case "Toast": {
                    if (Bukkit.getPluginManager().getPlugin("UltimateAdvancementAPI") != null) {
                        UltimateAdvancementAPI ultimateAdvancementAPI = UltimateAdvancementAPI.getInstance(main.main);
                        ItemStack playerHead = getPlayerHead(friend, null, null, null, null);
                        String message = i18n("Command.accept.Message").replaceAll("\\{Player}", friend);
                        ultimateAdvancementAPI.displayCustomToast(player, playerHead, message, AdvancementFrameType.TASK);
                        break;
                    }
                }
                default: {
                    player.sendMessage(i18n("Command.accept.Message").replaceAll("\\{Player}", friend));
                    break;
                }
            }
        }
    }

    public static void sendDefuseFriendRequestMessage(String playerName, String friend) {
        if (Bukkit.getPlayer(playerName) != null) {
            Player player = Bukkit.getPlayer(playerName);
            switch (Objects.requireNonNull(main.main.getConfig().getString("MessageSettings.DefuseRequest"))) {
                case "ActionBar": {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(i18n("Command.defuse.Message").replaceAll("\\{Player}", friend)));
                    break;
                }
                case "Toast": {
                    if (Bukkit.getPluginManager().getPlugin("UltimateAdvancementAPI") != null) {
                        UltimateAdvancementAPI ultimateAdvancementAPI = UltimateAdvancementAPI.getInstance(main.main);
                        ItemStack playerHead = getPlayerHead(friend, null, null, null, null);
                        String message = i18n("Command.defuse.Message").replaceAll("\\{Player}", friend);
                        ultimateAdvancementAPI.displayCustomToast(player, playerHead, message, AdvancementFrameType.TASK);
                        break;
                    }
                }
                default: {
                    player.sendMessage(i18n("Command.defuse.Message").replaceAll("\\{Player}", friend));
                    break;
                }
            }
        }
    }
}
