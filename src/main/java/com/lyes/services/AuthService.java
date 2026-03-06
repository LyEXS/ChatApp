package com.lyes.services;

import com.lyes.daos.UserDAO;
import com.lyes.models.Conversation;
import com.lyes.models.InitData;
import com.lyes.models.Utilisateur;
import com.lyes.session.SessionManager;

import java.util.List;

/**
 * Service layer for authentication and user management.
 */
public class AuthService {
    private final UserDAO userDAO;
    private final ConversationService conversationService;
    private final SessionManager sessionManager;

    public AuthService(UserDAO userDAO, ConversationService conversationService, SessionManager sessionManager) {
        this.userDAO = userDAO;
        this.conversationService = conversationService;
        this.sessionManager = sessionManager;
    }

    public Utilisateur authenticate(String username, String password) {
        return userDAO.authenticate(username, password);
    }

    public void signUp(Utilisateur user) {
        userDAO.createUser(user);
    }

    public List<Utilisateur> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * Builds the initial data payload sent to a client after successful authentication.
     */
    public InitData buildInitData(Utilisateur currentUser) {
        List<Utilisateur> allUsers = userDAO.getAllUsers();
        for (Utilisateur u : allUsers) {
            u.setOnline(sessionManager.isOnline(u));
            u.setPassword(null);
        }

        List<Conversation> conversations = conversationService.getUserConversations(currentUser.getIdUtilisateur());

        Utilisateur safeUser = new Utilisateur(currentUser.getIdUtilisateur(), currentUser.getUsername(), null);
        safeUser.setOnline(true);

        return new InitData(safeUser, allUsers, conversations);
    }
}

