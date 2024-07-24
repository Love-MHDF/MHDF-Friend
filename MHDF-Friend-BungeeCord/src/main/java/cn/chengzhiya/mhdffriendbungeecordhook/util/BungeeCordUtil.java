package cn.chengzhiya.mhdffriendbungeecordhook.util;

import cn.chengzhiya.mhdffriendbungeecordhook.main;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Map;

public final class BungeeCordUtil {
    public static void friendJoin(String playerName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("friendJoin");
        out.writeUTF(playerName);

        sendAllServer(out);
    }

    public static void friendQuit(String playerName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("friendQuit");
        out.writeUTF(playerName);

        sendAllServer(out);
    }

    public static void sendAllServer(ByteArrayDataOutput out) {
        for (Map.Entry<String, ServerInfo> entry : main.main.getProxy().getServers().entrySet()) {
            ServerInfo server = entry.getValue();
            if (!server.getPlayers().isEmpty()) {
                server.sendData("BungeeCord", out.toByteArray());
            }
        }
    }

    public static void send(ByteArrayDataOutput out, String targetName) {
        ServerInfo server = main.main.getProxy().getPlayer(targetName).getServer().getInfo();
        server.sendData("BungeeCord", out.toByteArray());
    }
}
