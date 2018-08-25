package bean;

import com.google.gson.annotations.SerializedName;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogCustomData;

import java.util.Collection;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jessica on 1/19/18.
 */

public class QuickbloxChat extends RealmObject {
    @PrimaryKey
    private String dialogId;
    private String lastMessage;
    private long lastMessageDateSent;
    private String photo;
    private Integer userId;
    private Integer unreadMessageCount;
    private String name;
    private Integer occupants;
    private Integer type;
    private Integer onlineUsers;
    private Integer recipientId;

    public QuickbloxChat() {
    }

    public QuickbloxChat(String dialogId, String lastMessage, long lastMessageDateSent, String photo, Integer userId, Integer unreadMessageCount, String name, Integer occupants, Integer type, Integer onlineUsers, Integer recipientId) {
        this.dialogId = dialogId;
        this.lastMessage = lastMessage;
        this.lastMessageDateSent = lastMessageDateSent;
        this.photo = photo;
        this.userId = userId;
        this.unreadMessageCount = unreadMessageCount;
        this.name = name;
        this.occupants = occupants;
        this.type = type;
        this.onlineUsers = onlineUsers;
        this.recipientId = recipientId;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageDateSent() {
        return lastMessageDateSent;
    }

    public void setLastMessageDateSent(long lastMessageDateSent) {
        this.lastMessageDateSent = lastMessageDateSent;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(Integer unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOccupants() {
        return occupants;
    }

    public void setOccupants(Integer occupants) {
        this.occupants = occupants;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(Integer onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }
}
