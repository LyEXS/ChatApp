package com.lyes.models;

import java.util.List;
import java.util.UUID;

public class Conversation {
    private String idConversation;
    private String nom;
    private Boolean estGroupe;
    private List<Message> messages;
    private List<Utilisateur> participants;

    public Conversation() {
        this.idConversation = UUID.randomUUID().toString();
    }

    public Conversation(String nom, Boolean estGroupe) {
        this.idConversation = UUID.randomUUID().toString();
        this.nom = nom;
        this.estGroupe = estGroupe;
    }

    public Conversation(String idConversation, String nom, Boolean estGroupe) {
        this.idConversation = idConversation;
        this.nom = nom;
        this.estGroupe = estGroupe;
    }

    public String getIdConversation() { return idConversation; }
    public void setIdConversation(String idConversation) { this.idConversation = idConversation; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Boolean getEstGroupe() { return estGroupe; }
    public void setEstGroupe(Boolean estGroupe) { this.estGroupe = estGroupe; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public List<Utilisateur> getParticipants() { return participants; }
    public void setParticipants(List<Utilisateur> participants) { this.participants = participants; }
}