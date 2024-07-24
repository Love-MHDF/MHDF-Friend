package cn.chengzhiya.mhdffriend.command;

import cn.chengzhiya.mhdffriend.entity.PlayerData;
import cn.chengzhiya.mhdffriend.main;
import com.alibaba.fastjson.JSONArray;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static cn.chengzhiya.mhdffriend.utils.BungeeCordUtil.onlinePlayerList;
import static cn.chengzhiya.mhdffriend.utils.Util.i18n;
import static cn.chengzhiya.mhdffriend.utils.Util.loadConfig;
import static cn.chengzhiya.mhdffriend.utils.database.DatabaseUtil.*;

public final class FriendAdmin implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(main.main, () -> {
            if (sender.hasPermission("MHDFFriend.admin")) {
                if (args.length == 1) {
                    if (args[0].equals("reload")) {
                        loadConfig();
                        sender.sendMessage(i18n("AdminCommand.reload.Done"));
                        return;
                    }
                }
                if (args.length == 2) {
                    if (args[0].equals("clear")) {
                        PlayerData playerData = getPlayerData(args[1]);
                        List<String> friendList = playerData.getFriend().toJavaList(String.class);
                        for (String friend : friendList) {
                            removeFriend(args[1],friend);
                        }
                        sender.sendMessage(i18n("AdminCommand.clear.Done").replaceAll("\\{Player}",args[1]));
                        return;
                    }
                }
                if (args.length == 3) {
                    switch (args[0]) {
                        case "forceadd": {
                            PlayerData playerData = getPlayerData(args[1]);
                            List<String> friendList = playerData.getFriend().toJavaList(String.class);
                            if (!friendList.contains(args[2])) {
                                addFriend(args[1], args[2]);
                                addFriend(args[2], args[1]);
                                sender.sendMessage(i18n("AdminCommand.forceadd.Done").replaceAll("\\{Player}", args[1]).replaceAll("\\{Friend}", args[2]));
                            } else {
                                sender.sendMessage(i18n("AdminCommand.forceadd.AlreadyIsFriend").replaceAll("\\{Player}", args[1]).replaceAll("\\{Friend}", args[2]));
                            }
                            return;
                        }
                        case "forceremove": {
                            PlayerData playerData = getPlayerData(args[1]);
                            List<String> friendList = playerData.getFriend().toJavaList(String.class);
                            if (friendList.contains(args[2])) {
                                removeFriend(args[1], args[2]);
                                removeFriend(args[2], args[1]);
                                sender.sendMessage(i18n("AdminCommand.forceremove.Done").replaceAll("\\{Player}", args[1]).replaceAll("\\{Friend}", args[2]));
                            } else {
                                sender.sendMessage(i18n("AdminCommand.forceremove.DontHaveTheFriend").replaceAll("\\{Player}", args[1]).replaceAll("\\{Friend}", args[2]));
                            }
                            return;
                        }
                    }
                }
                {
                    sender.sendMessage(i18n("AdminCommand.help.Message")
                            .replaceAll("\\{help}", commandHelp("help", label))
                            .replaceAll("\\{forceadd}", commandHelp("forceadd", label))
                            .replaceAll("\\{forceremove}", commandHelp("forceremove", label))
                            .replaceAll("\\{clear}", commandHelp("clear", label))
                            .replaceAll("\\{reload}", commandHelp("reload", label)));
                }
            } else {
                sender.sendMessage(i18n("Message.NoPermission"));
            }
        });
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("help", "forceadd", "forceremove", "clear", "reload");
        }
        if (args.length == 2) {
            return onlinePlayerList;
        }
        return new ArrayList<>();
    }

    private String commandHelp(String Command, String label) {
        return i18n("AdminCommand." + Command + ".Usage").replaceAll("\\{Command}", label) + i18n("AdminCommand.Center") + i18n("AdminCommand." + Command + ".Description").replaceAll("\\{Command}", label);
    }
}

