package com.lyes.handlers;

import com.lyes.models.Message;
import com.lyes.models.Packet;
import com.lyes.models.Utilisateur;
import com.lyes.services.ConversationService;
import com.lyes.services.MessageService;
import com.lyes.session.ClientSession;
import com.lyes.session.SessionManager;
import com.lyes.utils.JsonUtils;

import java.util.List;

/**
 * Handles NEW_MESSAGE and MESSAGES packet types.
 */
public class MessageHandler implements PacketHandler {
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final SessionManager sessionManager;

    public MessageHandler(MessageService messageService, ConversationService conversationService,
                          SessionManager sessionManager) {
        this.messageService = messageService;
        this.conversationService = conversationService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(Packet packet, ClientSession session) {
        switch (packet.getType()) {
            case NEW_MESSAGE -> handleNewMessage(packet, session);
            case MESSAGES -> handleGetMessages(packet, session);
            default -> {}
        }
    }

    private void handleNewMessage(Packet packet, ClientSession session) {
        if (session.getCurrentUser() == null) return;

        Message message = JsonUtils.deserialize(packet.getData(), Message.class);
        if (message == null) return;

        message.setIdUtilisateur(session.getCurrentUser().getIdUtilisateur());
        messageService.saveMessage(message);

        List<Utilisateur> participants = conversationService.getParticipants(message.getIdConversation());
        Packet msgPacket = new Packet(Packet.Type.NEW_MESSAGE, JsonUtils.serialize(message));
        String json = JsonUtils.serialize(msgPacket);

        for (Utilisateur participant : participants) {
            sessionManager.sendToUser(participant.getIdUtilisateur(), json);
        }

        System.out.println("Message de " + session.getCurrentUser().getUsername()
                + " envoyé dans la conversation " + message.getIdConversation());
    }

    private void handleGetMessages(Packet packet, ClientSession session) {
        if (session.getCurrentUser() == null) return;

        String idConversation = packet.getData().replace("\"", "");
        List<Message> messages = messageService.getMessagesByConversation(idConversation);
        session.getSender().sendPacket(new Packet(Packet.Type.MESSAGES, JsonUtils.serialize(messages)));
    }
}

