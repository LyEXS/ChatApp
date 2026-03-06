package com.lyes.services;

import com.lyes.daos.MessageDAO;
import com.lyes.models.Message;

import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    public void saveMessage(Message message) {
        messageDAO.saveMessage(message);
    }

    public List<Message> getMessagesByConversation(String conversationId) {
        return messageDAO.getMessagesByConversation(conversationId);
    }
}
