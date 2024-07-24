package cn.chengzhiya.mhdffriend.entity;

import lombok.Data;

@Data
public final class RequestData {
    String TargetPlayer;
    Integer Delay;

    public RequestData(String TargetPlayer, Integer Delay) {
        this.TargetPlayer = TargetPlayer;
        this.Delay = Delay;
    }
}
