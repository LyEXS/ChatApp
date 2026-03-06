package com.lyes.services;

import com.lyes.daos.MessageDAO;
import com.lyes.models.Message;

import java.util.List;

/**
 * Service layer for message-related business logic.
 */
public class MessageService {
    private final MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public void saveMessage(Message message) {
        messageDAO.saveMessage(message);
    }

    public List<Message> getMessagesByConversation(String conversationId) {
        return messageDAO.getMessagesByConversation(conversationId);
    }
}
