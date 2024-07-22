package cn.chengzhiya.mhdffriend.entity;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public final class PlayerData {
    String PlayerName;
    JSONArray Friend;

    public PlayerData(String PlayerName, JSONArray Friend) {
        this.PlayerName = PlayerName;
        this.Friend = Friend;
    }
}
