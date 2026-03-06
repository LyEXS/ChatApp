package com.lyes.models;

public class Request {
    private String type; // ex: "AUTH", "SEND_MSG", "JOIN_GROUP"
    private Object data; // L'objet (Utilisateur ou Message)

    public Request(String type, Object data) {
        this.type = type;
        this.data = data;
    }
    // Getters
}