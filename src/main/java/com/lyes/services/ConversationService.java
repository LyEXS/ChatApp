package com.lyes.services;

import com.lyes.ServerMain;
import com.lyes.daos.ConversationDAO;
import com.lyes.daos.MessageDAO;
import com.lyes.models.Conversation;
import com.lyes.models.Utilisateur;

import java.util.List;

public class ConversationService {
    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;

    public ConversationService() {
        this.conversationDAO = new ConversationDAO();
        this.messageDAO = new MessageDAO();
    }

    public Conversation loadFullConversation(String idConversation) {
        Conversation conv = conversationDAO.getConversationById(idConversation);
        if (conv == null) return null;

        conv.setMessages(messageDAO.getMessagesByConversation(idConversation));
        List<Utilisateur> participants = conversationDAO.getParticipants(idConversation);
        updateParticipantsStatus(participants);
        conv.setParticipants(participants);
        return conv;
    }

    public List<Conversation> getUserConversations(String userId) {
        List<Conversation> conversations = conversationDAO.getConversationsByUser(userId);
        for (Conversation conv : conversations) {
            conv.setMessages(messageDAO.getMessagesByConversation(conv.getIdConversation()));
            List<Utilisateur> participants = conversationDAO.getParticipants(conv.getIdConversation());
            updateParticipantsStatus(participants);
            conv.setParticipants(participants);
        }
        return conversations;
    }

    public void updateParticipantsStatus(List<Utilisateur> participants) {
        for (Utilisateur p : participants) {
            p.setOnline(ServerMain.isOnline(p));
            p.setPassword(null);
        }
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

    public List<Utilisateur> getParticipants(String conversationId) {
        return conversationDAO.getParticipants(conversationId);
    }

    public void editConversation(Conversation conversation) {
        conversationDAO.editConversation(conversation);
    }
}
