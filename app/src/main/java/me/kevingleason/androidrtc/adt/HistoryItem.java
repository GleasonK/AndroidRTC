package me.kevingleason.androidrtc.adt;

/**
 * Created by GleasonK on 7/31/15.
 */
public class HistoryItem {
    private ChatUser user;
    private Long timeStamp;

    public HistoryItem(ChatUser user, Long timeStamp){
        this.user=user;
        this.timeStamp=timeStamp;
    }

    public ChatUser getUser() {
        return user;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }
}