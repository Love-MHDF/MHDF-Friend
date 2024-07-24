package cn.chengzhiya.mhdffriend.command;

import cn.chengzhiya.mhdffriend.entity.PlayerData;
import cn.chengzhiya.mhdffriend.entity.RequestData;
import cn.chengzhiya.mhdffriend.main;
import cn.chengzhiya.mhdffriend.utils.FriendUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static cn.chengzhiya.mhdffriend.utils.BungeeCordUtil.*;
import static cn.chengzhiya.mhdffriend.utils.FriendUtil.sendAddFriendRequest;
import static cn.chengzhiya.mhdffriend.utils.Util.i18n;
import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.*;
import static cn.chengzhiya.mhdffriend.utils.menu.FriendListMenuUtil.openFriendListMenu;

public final class Friend implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    openFriendListMenu(player, 1);
                    return;
                }
                if (args.length == 2) {
                    switch (args[0]) {
                        case "add": {
                            if (FriendUtil.getFriendRequestHashMap().get(player.getName()) == null) {
                                if (ifPlayerOnline(args[1])) {
                                    PlayerData playerData = getPlayerData(player.getName());
                                    List<String> friendList = playerData.getFriend().toJavaList(String.class);
                                    if (!friendList.contains(args[1])) {
                                        sendAddFriendRequest(player.getName(), args[1]);
                                        FriendUtil.getFriendRequestHashMap().put(player.getName(), new RequestData(args[1], main.main.getConfig().getInt("Delay")));
                                        player.sendMessage(i18n("Command.add.SendDone").replaceAll("\\{Player}", args[1]));
                                    } else {
                                        player.sendMessage(i18n("Command.add.AlreadyIsFriend").replaceAll("\\{Player}", args[1]));
                                    }
                                } else {
                                    player.sendMessage(i18n("Message.PlayerNotOnline"));
                                }
                            } else {
                                RequestData requestData = FriendUtil.getFriendRequestHashMap().get(player.getName());
                                player.sendMessage(i18n("Command.add.Delay").replaceAll("\\{Delay}", String.valueOf(requestData.getDelay())));
                            }
                            return;
                        }
                        case "remove": {
                            PlayerData playerData = getPlayerData(player.getName());
                            List<String> friendList = playerData.getFriend().toJavaList(String.class);
                            if (friendList.contains(args[1])) {
                                removeFriend(player.getName(), args[1]);
                                removeFriend(args[1], player.getName());
                                player.sendMessage(i18n("Command.remove.Done").replaceAll("\\{Player}", args[1]));
                            } else {
                                player.sendMessage(i18n("Command.remove.DontHaveTheFriend").replaceAll("\\{Player}", args[1]));
                            }
                            return;
                        }
                        case "accept": {
                            if (FriendUtil.getFriendRequestHashMap().get(args[1]) != null) {
                                if (ifPlayerOnline(args[1])) {
                                    addFriend(player.getName(), args[1]);
                                    addFriend(args[1], player.getName());
                                    acceptFriendRequest(player.getName(), args[1]);
                                    sender.sendMessage(i18n("Command.accept.Done").replaceAll("\\{Player}", args[1]));
                                } else {
                                    player.sendMessage(i18n("Message.PlayerNotOnline"));
                                }
                                FriendUtil.getFriendRequestHashMap().remove(args[1]);
                            } else {
                                sender.sendMessage(i18n("Command.accept.NoRequest").replaceAll("\\{Player}", args[1]));
                            }
                            return;
                        }
                        case "defuse": {
                            if (FriendUtil.getFriendRequestHashMap().get(args[1]) != null) {
                                if (ifPlayerOnline(args[1])) {
                                    defuseFriendRequest(player.getName(), args[1]);
                                    sender.sendMessage(i18n("Command.defuse.Done").replaceAll("\\{Player}", args[1]));
                                } else {
                                    player.sendMessage(i18n("Message.PlayerNotOnline"));
                                }
                                FriendUtil.getFriendRequestHashMap().remove(args[1]);
                            } else {
                                sender.sendMessage(i18n("Command.defuse.NoRequest").replaceAll("\\{Player}", args[1]));
                            }
                            return;
                        }
                    }
                }
                {
                    sender.sendMessage(i18n("Command.help.Message")
                            .replaceAll("\\{help}", commandHelp("help", label))
                            .replaceAll("\\{add}", commandHelp("add", label))
                            .replaceAll("\\{remove}", commandHelp("remove", label))
                            .replaceAll("\\{accept}", commandHelp("accept", label))
                            .replaceAll("\\{defuse}", commandHelp("defuse", label)));
                }
            } else {
                sender.sendMessage(i18n("Message.OnlyPlayer"));
            }
        });
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("help", "add", "remove", "accept", "defuse");
        }
        if (args.length == 2) {
            return onlinePlayerList;
        }
        return new ArrayList<>();
    }

    private String commandHelp(String Command, String label) {
        return i18n("Command." + Command + ".Usage").replaceAll("\\{Command}", label) + i18n("Command.Center") + i18n("Command." + Command + ".Description").replaceAll("\\{Command}", label);
    }
}
