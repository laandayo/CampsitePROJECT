package com.lan.campsiteproject.model;

public class Conversation {
    private String chatId;
    private String otherUserId;
    private String otherUserName;
    private String otherUserProfileImageUrl;
    private String lastMessage;
    private long lastMessageTime;

    public Conversation() {}

    // Getters and setters...
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getOtherUserId() { return otherUserId; }
    public void setOtherUserId(String otherUserId) { this.otherUserId = otherUserId; }
    public String getOtherUserName() { return otherUserName; }
    public void setOtherUserName(String otherUserName) { this.otherUserName = otherUserName; }
    public String getOtherUserProfileImageUrl() { return otherUserProfileImageUrl; }
    public void setOtherUserProfileImageUrl(String url) { this.otherUserProfileImageUrl = url; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public long getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(long lastMessageTime) { this.lastMessageTime = lastMessageTime; }
}