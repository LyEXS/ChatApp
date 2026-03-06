package com.lyes.handlers;

import com.lyes.models.*;
import com.lyes.services.AuthService;
import com.lyes.services.ConversationService;
import com.lyes.session.ClientSession;
import com.lyes.session.SessionManager;
import com.lyes.utils.JsonUtils;

import java.util.List;

/**
 * Handles conversation-related packet types:
 * CONVERSATIONS, NEW_CONVERSATION, NEW_CONVERSATION_USER,
 * EDIT_CONVERSATION, ADD_MEMBER_TO_CONVERSATION, ONLINE_CLIENTS
 */
public class ConversationHandler implements PacketHandler {
    private final ConversationService conversationService;
    private final AuthService authService;
    private final SessionManager sessionManager;
    private final NotificationHandler notificationHandler;


    public ConversationHandler(ConversationService conversationService, AuthService authService,
                               SessionManager sessionManager, NotificationHandler notificationHandler) {
        this.conversationService = conversationService;
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.notificationHandler = notificationHandler;
    }

    @Override
    public void handle(Packet packet, ClientSession session) {
        switch (packet.getType()) {
            case CONVERSATIONS -> handleGetConversations(session);
            case NEW_CONVERSATION -> handleAddNewConversation(packet, session);
            case NEW_CONVERSATION_USER -> handleAddNewConversationUser(packet, session);
            case EDIT_CONVERSATION -> handleEditConversation(packet, session);
            case ADD_MEMBER_TO_CONVERSATION -> handleAddMember(packet, session);
            case ONLINE_CLIENTS -> handleGetOnlineClients(session);
            default -> {}
        }
    }

    private void handleGetConversations(ClientSession session) {
        if (session.getCurrentUser() == null) return;

        List<Conversation> conversations = conversationService.getUserConversations(
                session.getCurrentUser().getIdUtilisateur());
        session.getSender().sendPacket(new Packet(Packet.Type.CONVERSATIONS, JsonUtils.serialize(conversations)));
    }

    private void handleAddNewConversation(Packet packet, ClientSession session) {
        if (session.getCurrentUser() == null) return;

        Conversation conversation = JsonUtils.deserialize(packet.getData(), Conversation.class);
        if (conversation == null) return;

        Utilisateur currentUser = session.getCurrentUser();

        // Check for existing private conversation
        if (conversation.getEstGroupe() == null || !conversation.getEstGroupe()) {
            List<Utilisateur> participants = conversation.getParticipants();
            if (participants != null) {
                String otherUserId = findOtherUserId(participants, currentUser.getIdUtilisateur());
                if (otherUserId != null) {
                    Conversation existing = conversationService.findPrivateConversation(
                            currentUser.getIdUtilisateur(), otherUserId);
                    if (existing != null) {
                        sendExistingConversation(existing, session);
                        return;
                    }
                }
            }
        }

        boolean created = conversationService.createConversation(conversation);
        if (!created) {
            session.getSender().sendResponse(Packet.Type.NEW_CONVERSATION, false,
                    "Erreur lors de la création de la conversation");
            return;
        }

        conversationService.addParticipantsToConversation(conversation, currentUser);
        broadcastNewConversation(conversation,session);

        System.out.println("Nouvelle conversation créée par " + currentUser.getUsername()
                + " : " + conversation.getIdConversation());
    }

    private void handleAddNewConversationUser(Packet packet, ClientSession session) {
        if (session.getCurrentUser() == null) return;

        UtilisateurConversation uc = JsonUtils.deserialize(packet.getData(), UtilisateurConversation.class);
        if (uc == null || uc.getIdUtilisateur() == null || uc.getIdConversation() == null) return;

        conversationService.addUserToConversation(uc.getIdUtilisateur(), uc.getIdConversation());

        Conversation fullConv = conversationService.loadFullConversation(uc.getIdConversation());
        if (fullConv == null) return;

        Packet newConvPacket = new Packet(Packet.Type.NEW_CONVERSATION, JsonUtils.serialize(fullConv));
        sessionManager.sendToUser(uc.getIdUtilisateur(), JsonUtils.serialize(newConvPacket));

        notifyParticipants(fullConv.getParticipants(), uc, Packet.Type.NEW_CONVERSATION_USER);

        System.out.println("Utilisateur " + uc.getIdUtilisateur()
                + " ajouté à la conversation " + uc.getIdConversation());
    }

    private void handleEditConversation(Packet packet, ClientSession session) {
        Conversation conversation = JsonUtils.deserialize(packet.getData(), Conversation.class);
        if (conversation == null) return;

        conversationService.editConversation(conversation);
        List<Utilisateur> participants = conversationService.getParticipants(conversation.getIdConversation());
        conversationService.sanitizeParticipants(participants);
        conversation.setParticipants(participants);

        Packet packetToSend = new Packet(Packet.Type.EDIT_CONVERSATION, JsonUtils.serialize(conversation));
        String json = JsonUtils.serialize(packetToSend);

        System.out.println("Demande de renommage de conversation en " + conversation.getNom());

        for (Utilisateur p : participants) {
            sessionManager.sendToUser(p.getIdUtilisateur(), json);
        }
    }

    private void handleAddMember(Packet packet, ClientSession session) {
        if (session.getCurrentUser() == null) return;

        UtilisateurConversation uc = JsonUtils.deserialize(packet.getData(), UtilisateurConversation.class);
        if (uc == null || uc.getIdUtilisateur() == null || uc.getIdConversation() == null) return;

        boolean added = conversationService.addMember(uc.getIdUtilisateur(), uc.getIdConversation());
        if (!added) return;

        Conversation conv = conversationService.loadFullConversation(uc.getIdConversation());
        if (conv == null) return;

        Packet newConvPacket = new Packet(Packet.Type.NEW_CONVERSATION, JsonUtils.serialize(conv));
        sessionManager.sendToUser(uc.getIdUtilisateur(), JsonUtils.serialize(newConvPacket));

        notifyParticipants(conv.getParticipants(), uc, Packet.Type.ADD_MEMBER_TO_CONVERSATION);

        System.out.println("Membre " + uc.getIdUtilisateur()
                + " ajouté à la conversation " + uc.getIdConversation());
    }

    private void handleGetOnlineClients(ClientSession session) {
        List<Utilisateur> onlineClients = authService.getAllUsers();
        session.getSender().sendPacket(new Packet(Packet.Type.ONLINE_CLIENTS, JsonUtils.serialize(onlineClients)));
    }

    // --- Helper methods ---

    private String findOtherUserId(List<Utilisateur> participants, String currentUserId) {
        for (Utilisateur p : participants) {
            if (!p.getIdUtilisateur().equals(currentUserId)) {
                return p.getIdUtilisateur();
            }
        }
        return null;
    }

    private void sendExistingConversation(Conversation existing, ClientSession session) {
        Conversation fullConv = conversationService.loadFullConversation(existing.getIdConversation());
        if (fullConv != null) {
            session.getSender().sendPacket(new Packet(Packet.Type.NEW_CONVERSATION, JsonUtils.serialize(fullConv)));
        }
    }

    private void broadcastNewConversation(Conversation conversation,ClientSession session) {
        List<Utilisateur> fullParticipants = conversationService.getParticipants(conversation.getIdConversation());
        conversationService.sanitizeParticipants(fullParticipants);
        conversation.setParticipants(fullParticipants);

        Packet newConvPacket = new Packet(Packet.Type.NEW_CONVERSATION, JsonUtils.serialize(conversation));
        String json = JsonUtils.serialize(newConvPacket);

        Notification notification = new Notification();
        notification.setTitle("Nouvelle Conversation! ");
        notification.setMessage("Vous avez été ajouté à une nouvelle conversation!");
        notification.setConcernedUsers(fullParticipants);

        Packet notificationPacket = new Packet(Packet.Type.NOTIFICATION,JsonUtils.serialize(notification));

        for (Utilisateur p : fullParticipants) {
            sessionManager.sendToUser(p.getIdUtilisateur(), json);
            notificationHandler.handleSendNotification(notificationPacket, session);
        }
    }

    private void notifyParticipants(List<Utilisateur> participants, UtilisateurConversation uc, Packet.Type type) {
        Packet notifPacket = new Packet(type, JsonUtils.serialize(uc));
        String notifJson = JsonUtils.serialize(notifPacket);
        for (Utilisateur p : participants) {
            if (!p.getIdUtilisateur().equals(uc.getIdUtilisateur())) {
                sessionManager.sendToUser(p.getIdUtilisateur(), notifJson);
            }
        }
    }
}

