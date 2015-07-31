package me.kevingleason.androidrtc.adt;

import me.kevingleason.androidrtc.util.Constants;

/**
 * Created by GleasonK on 7/31/15.
 */
public class ChatUser {
    private String userId;
    private String status;

    public ChatUser(String userId) {
        this.userId = userId;
        this.status = Constants.STATUS_OFFLINE;
    }

    public ChatUser(String userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (!(o instanceof ChatUser)) return false;
        ChatUser cu = (ChatUser)o;
        return this.userId.equals(((ChatUser) o).getUserId());
    }

    @Override
    public int hashCode() {
        return this.getUserId().hashCode();
    }
}
