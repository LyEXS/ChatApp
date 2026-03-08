package com.lyes.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Message {
    private String idMessage;
    private String idConversation;
    private String idUtilisateur;
    private String content;
    private boolean read;
    private Timestamp dateTime;

    public Message() {}

    public Message(String idConversation, String idUtilisateur, String content) {
        this.idMessage = UUID.randomUUID().toString();
        this.idConversation = idConversation;
        this.idUtilisateur = idUtilisateur;
        this.content = content;
        this.dateTime = new Timestamp(System.currentTimeMillis());
    }

    public Message(String idMessage, String idConversation, String idUtilisateur, String content, Timestamp dateTime, boolean read) {
        this.idMessage = idMessage;
        this.idConversation = idConversation;
        this.idUtilisateur = idUtilisateur;
        this.content = content;
        this.dateTime = dateTime;
        this.read = read ;
    }

    public String getIdMessage() { return idMessage; }
    public void setIdMessage(String idMessage) { this.idMessage = idMessage; }
    public String getIdConversation() { return idConversation; }
    public void setIdConversation(String idConversation) { this.idConversation = idConversation; }
    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Timestamp getDateTime() { return dateTime; }
    public void setDateTime(Timestamp dateTime) { this.dateTime = dateTime; }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}