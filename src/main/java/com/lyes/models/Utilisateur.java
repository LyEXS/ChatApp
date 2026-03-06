package com.lyes.models;

import java.util.Objects;
import java.util.UUID;

public class Utilisateur {
    private String idUtilisateur;
    private String username;
    private String password;
    private boolean online;

    public Utilisateur() {}

    public Utilisateur(String username, String password) {
        this.idUtilisateur = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
    }

    public Utilisateur(String idUtilisateur, String username, String password) {
        this.idUtilisateur = idUtilisateur;
        this.username = username;
        this.password = password;
    }

    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return Objects.equals(idUtilisateur, that.idUtilisateur);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUtilisateur);
    }
}