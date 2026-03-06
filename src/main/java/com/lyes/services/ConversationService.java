package com.lyes.services;

import com.lyes.daos.ConversationDAO;
import com.lyes.daos.MessageDAO;
import com.lyes.daos.UtilisateurConversationDAO;
import com.lyes.models.Conversation;
import com.lyes.models.Utilisateur;
import com.lyes.session.SessionManager;

import java.util.List;

/**
 * Service layer for conversation-related business logic.
 */
public class ConversationService {
    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;
    private final UtilisateurConversationDAO utilisateurConversationDAO;
    private final SessionManager sessionManager;

    public ConversationService(ConversationDAO conversationDAO, MessageDAO messageDAO,
                               UtilisateurConversationDAO utilisateurConversationDAO,
                               SessionManager sessionManager) {
        this.conversationDAO = conversationDAO;
        this.messageDAO = messageDAO;
        this.utilisateurConversationDAO = utilisateurConversationDAO;
        this.sessionManager = sessionManager;
    }

    public Conversation loadFullConversation(String idConversation) {
        Conversation conv = conversationDAO.getConversationById(idConversation);
        if (conv == null) return null;

        conv.setMessages(messageDAO.getMessagesByConversation(idConversation));
        List<Utilisateur> participants = conversationDAO.getParticipants(idConversation);
        sanitizeParticipants(participants);
        conv.setParticipants(participants);
        return conv;
    }

    public List<Conversation> getUserConversations(String userId) {
        List<Conversation> conversations = conversationDAO.getConversationsByUser(userId);
        for (Conversation conv : conversations) {
            conv.setMessages(messageDAO.getMessagesByConversation(conv.getIdConversation()));
            List<Utilisateur> participants = conversationDAO.getParticipants(conv.getIdConversation());
            sanitizeParticipants(participants);
            conv.setParticipants(participants);
        }
        return conversations;
    }

    public Conversation findPrivateConversation(String userId1, String userId2) {
        return conversationDAO.findPrivateConversation(userId1, userId2);
    }

    public boolean createConversation(Conversation conversation) {
        return conversationDAO.createConversation(conversation);
    }

    public void addUserToConversation(String userId, String conversationId) {
        conversationDAO.addUserToConversation(userId, conversationId);
    }

    public boolean addMember(String userId, String conversationId) {
        return utilisateurConversationDAO.addMember(userId, conversationId);
    }

    public List<Utilisateur> getParticipants(String conversationId) {
        return conversationDAO.getParticipants(conversationId);
    }

    public void editConversation(Conversation conversation) {
        conversationDAO.editConversation(conversation);
    }

    /**
     * Adds participants to a conversation, ensuring the creator is included.
     */
    public void addParticipantsToConversation(Conversation conversation, Utilisateur creator) {
        List<Utilisateur> participants = conversation.getParticipants();
        if (participants != null) {
            for (Utilisateur p : participants) {
                conversationDAO.addUserToConversation(p.getIdUtilisateur(), conversation.getIdConversation());
            }
        }

        boolean creatorIncluded = participants != null && participants.stream()
                .anyMatch(p -> p.getIdUtilisateur().equals(creator.getIdUtilisateur()));

        if (!creatorIncluded) {
            conversationDAO.addUserToConversation(creator.getIdUtilisateur(), conversation.getIdConversation());
        }
    }

    /**
     * Sets online status and clears passwords for a list of participants.
     */
    public void sanitizeParticipants(List<Utilisateur> participants) {
        for (Utilisateur p : participants) {
            p.setOnline(sessionManager.isOnline(p));
            p.setPassword(null);
        }
    }
}
