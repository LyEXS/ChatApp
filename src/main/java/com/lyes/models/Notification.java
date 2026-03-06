package com.lyes.models;

import java.util.List;

public class Notification {

    private String title;
    private String message;
    private List<Utilisateur> concernedUsers;


    public Notification(String title, String message, List<Utilisateur> concernedUsers) {
        this.title = title;
        this.message = message;
        this.concernedUsers = concernedUsers;
    }
    public Notification(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Utilisateur> getConcernedUsers() {
        return concernedUsers;
    }

    public void setConcernedUsers(List<Utilisateur> concernedUsers) {
        this.concernedUsers = concernedUsers;
    }
}
