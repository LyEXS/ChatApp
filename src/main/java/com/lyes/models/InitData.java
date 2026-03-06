package com.lyes.models;

import java.util.List;

/**
 * Contient toutes les données initiales envoyées au client après l'authentification.
 */
public class InitData {
    private Utilisateur currentUser;
    private List<Utilisateur> allUsers;
    private List<Conversation> conversations;

    public InitData() {}

    public InitData(Utilisateur currentUser, List<Utilisateur> allUsers, List<Conversation> conversations) {
        this.currentUser = currentUser;
        this.allUsers = allUsers;
        this.conversations = conversations;
    }

    public Utilisateur getCurrentUser() { return currentUser; }
    public void setCurrentUser(Utilisateur currentUser) { this.currentUser = currentUser; }
    public List<Utilisateur> getAllUsers() { return allUsers; }
    public void setAllUsers(List<Utilisateur> allUsers) { this.allUsers = allUsers; }
    public List<Conversation> getConversations() { return conversations; }
    public void setConversations(List<Conversation> conversations) { this.conversations = conversations; }
}

