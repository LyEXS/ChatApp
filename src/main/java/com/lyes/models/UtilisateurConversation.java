package com.lyes.models;

public class UtilisateurConversation {
    private String idUtilisateur;
    private String idConversation;

    public UtilisateurConversation() {}

    public UtilisateurConversation(String idUtilisateur, String idConversation) {
        this.idUtilisateur = idUtilisateur;
        this.idConversation = idConversation;
    }

    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getIdConversation() { return idConversation; }
    public void setIdConversation(String idConversation) { this.idConversation = idConversation; }
}