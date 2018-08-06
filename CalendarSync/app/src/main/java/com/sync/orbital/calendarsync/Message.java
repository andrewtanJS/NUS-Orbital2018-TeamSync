package com.sync.orbital.calendarsync;

public class Message {

    private String messageText;
    private String messageUser;
    private String messageUserId;
    private String messageTime;

    public Message(String messageText, String messageUser, String messageUserId, String messageTime) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageTime = messageTime;
        this.messageUserId = messageUserId;
    }

    public Message(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId;
    }


}
