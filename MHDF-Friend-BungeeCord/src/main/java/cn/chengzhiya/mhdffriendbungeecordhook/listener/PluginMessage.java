package cn.chengzhiya.mhdffriendbungeecordhook.listener;

import cn.chengzhiya.mhdffriendbungeecordhook.main;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import static cn.chengzhiya.mhdffriendbungeecordhook.util.BungeeCordUtil.send;

public final class PluginMessage implements Listener {
    public ServerInfo getServerInfo(PluginMessageEvent event) {
        InetAddress senderAddress = event.getSender().getAddress().getAddress();
        int senderPort = event.getSender().getAddress().getPort();

        for (Map.Entry<String, ServerInfo> entry : main.main.getProxy().getServers().entrySet()) {

            ServerInfo server = entry.getValue();
            InetAddress serverAddress = server.getAddress().getAddress();

            int serverPort = server.getAddress().getPort();

            if (serverAddress.equals(senderAddress) && serverPort == senderPort) {
                if (!server.getPlayers().isEmpty()) {
                    return server;
                } else if (server.getPlayers().isEmpty()) {
                    return null;
                }
            }
        }
        return null;
    }

    @EventHandler
    public void onEvent(PluginMessageEvent event) {
        if (!event.getTag().contains("BungeeCord")) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

        try {
            String subchannel = in.readUTF();

            switch (subchannel) {
                case "addFriendRequest": {
                    String playerName = in.readUTF();
                    String friendName = in.readUTF();

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("addFriendRequest");
                    out.writeUTF(playerName);
                    out.writeUTF(friendName);

                    send(out, friendName);
                    break;
                }
                case "acceptFriendRequest": {
                    String playerName = in.readUTF();
                    String friendName = in.readUTF();

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("acceptFriendRequest");
                    out.writeUTF(playerName);
                    out.writeUTF(friendName);

                    send(out, friendName);
                    break;
                }
                case "defuseFriendRequest": {
                    String playerName = in.readUTF();
                    String friendName = in.readUTF();

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("defuseFriendRequest");
                    out.writeUTF(playerName);
                    out.writeUTF(friendName);

                    send(out, friendName);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
