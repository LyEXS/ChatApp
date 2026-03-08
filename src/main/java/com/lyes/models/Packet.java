package com.lyes.models;

public class Packet {

    public enum Type {
        AUTH, ONLINE_CLIENTS, CONVERSATIONS, MESSAGES, NEW_MESSAGE, INIT_DATA, NEW_CONVERSATION,NEW_CONVERSATION_USER,CLIENTS,
        EDIT_CONVERSATION,ADD_MEMBER_TO_CONVERSATION,REMOVE_MEMBER_FROM_CONVERSATION,ERROR,SIGNUP,NOTIFICATION,MESSAGE_READ
    }

    private Type type;
    private String data;

    public Packet() {}

    public Packet(Type type, String data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
