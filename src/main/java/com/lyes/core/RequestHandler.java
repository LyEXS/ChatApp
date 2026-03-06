package com.lyes.core;

import com.lyes.ServerMain;
import com.lyes.daos.ConversationDAO;
import com.lyes.daos.MessageDAO;
import com.lyes.daos.UserDAO;
import com.lyes.daos.UtilisateurConversationDAO;
import com.lyes.models.*;
import com.lyes.utils.JsonUtils;

import java.util.List;

public class RequestHandler {
    private final UserDAO userDAO = new UserDAO();
    private final MessageDAO messageDAO = new MessageDAO();
    private final ConversationDAO conversationDAO = new ConversationDAO();
    private final UtilisateurConversationDAO utilisateurConversationDAO = new UtilisateurConversationDAO();

    private final ClientHandler clientHandler;
    private Utilisateur currentUser;

    public RequestHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void handlePacket(Packet packet) {
        switch (packet.getType()) {
            case AUTH:
                handleAuth(packet);
                break;
            case NEW_MESSAGE:
                handleNewMessage(packet);
                break;
            case CONVERSATIONS:
                handleGetConversations();
                break;
            case NEW_CONVERSATION:
                handleAddNewConversation(packet);
                break;
            case NEW_CONVERSATION_USER:
                handleAddNewConversationUser(packet);
                break;
            case MESSAGES:
                handleGetMessages(packet);
                break;
            case ONLINE_CLIENTS:
                handleGetOnlineClients();
                break;
            case EDIT_CONVERSATION:
                handleEditConversation(packet);
                break;
            case ADD_MEMBER_TO_CONVERSATION:
                handleAddMember(packet);
                break;
            default:
                System.out.println("Type de paquet inconnu : " + packet.getType());
                break;
        }
    }

    private void handleAuth(Packet packet) {
        Utilisateur credentials = JsonUtils.deserialize(packet.getData(), Utilisateur.class);
        if (credentials == null) {
            sendResponse(Packet.Type.AUTH, false, "Données d'authentification invalides");
            return;
        }

        Utilisateur user = userDAO.authenticate(credentials.getUsername(), credentials.getPassword());
        if (user == null) {
            sendResponse(Packet.Type.AUTH, false, "Nom d'utilisateur ou mot de passe incorrect");
            return;
        }

        this.currentUser = user;
        user.setPassword(null);
        user.setOnline(true);

        sendResponse(Packet.Type.AUTH, true, "Connexion réussie");
        ServerMain.addOnlineClient(currentUser, clientHandler);
        sendInitData();
    }

    private void sendInitData() {
        List<Utilisateur> allUsers = userDAO.getAllUsers();
        for (Utilisateur u : allUsers) {
            u.setOnline(ServerMain.isOnline(u));
            u.setPassword(null);
        }

        List<Conversation> conversations = conversationDAO.getConversationsByUser(currentUser.getIdUtilisateur());
        for (Conversation conv : conversations) {
            List<Message> messages = messageDAO.getMessagesByConversation(conv.getIdConversation());
            conv.setMessages(messages);

            List<Utilisateur> participants = conversationDAO.getParticipants(conv.getIdConversation());
            for (Utilisateur p : participants) {
                p.setOnline(ServerMain.isOnline(p));
                p.setPassword(null);
            }
            conv.setParticipants(participants);
        }

        Utilisateur safeUser = new Utilisateur(currentUser.getIdUtilisateur(), currentUser.getUsername(), null);
        safeUser.setOnline(true);

        InitData initData = new InitData(safeUser, allUsers, conversations);
        sendPacket(new Packet(Packet.Type.INIT_DATA, JsonUtils.serialize(initData)));

        System.out.println("Données initiales envoyées à " + currentUser.getUsername());
    }

    private void handleNewMessage(Packet packet) {
        if (currentUser == null) return;

        Message message = JsonUtils.deserialize(packet.getData(), Message.class);
        if (message == null) return;

        message.setIdUtilisateur(currentUser.getIdUtilisateur());
        messageDAO.saveMessage(message);

        List<Utilisateur> participants = conversationDAO.getParticipants(message.getIdConversation());
        Packet msgPacket = new Packet(Packet.Type.NEW_MESSAGE, JsonUtils.serialize(message));
        String json = JsonUtils.serialize(msgPacket);

        for (Utilisateur participant : participants) {
            ServerMain.sendToUser(participant.getIdUtilisateur(), json);
        }

        System.out.println("Message de " + currentUser.getUsername() + " envoyé dans la conversation " + message.getIdConversation());
    }

    private void handleGetConversations() {
        if (currentUser == null) return;

        List<Conversation> conversations = conversationDAO.getConversationsByUser(currentUser.getIdUtilisateur());
        for (Conversation conv : conversations) {
            conv.setMessages(messageDAO.getMessagesByConversation(conv.getIdConversation()));
            List<Utilisateur> participants = conversationDAO.getParticipants(conv.getIdConversation());
            for (Utilisateur p : participants) {
                p.setOnline(ServerMain.isOnline(p));
                p.setPassword(null);
            }
            conv.setParticipants(participants);
        }

        sendPacket(new Packet(Packet.Type.CONVERSATIONS, JsonUtils.serialize(conversations)));
    }

    private void handleAddNewConversation(Packet packet) {
        if (currentUser == null) return;

        Conversation conversation = JsonUtils.deserialize(packet.getData(), Conversation.class);
        if (conversation == null) return;

        if (conversation.getEstGroupe() == null || !conversation.getEstGroupe()) {
            List<Utilisateur> participants = conversation.getParticipants();
            if (participants != null) {
                String otherUserId = findOtherUserId(participants);
                if (otherUserId != null) {
                    Conversation existing = conversationDAO.findPrivateConversation(currentUser.getIdUtilisateur(), otherUserId);
                    if (existing != null) {
                        sendExistingConversation(existing);
                        return;
                    }
                }
            }
        }

        boolean created = conversationDAO.createConversation(conversation);
        if (!created) {
            sendResponse(Packet.Type.NEW_CONVERSATION, false, "Erreur lors de la création de la conversation");
            return;
        }

        addParticipantsToConversation(conversation);
        broadcastNewConversation(conversation);

        System.out.println("Nouvelle conversation créée par " + currentUser.getUsername() + " : " + conversation.getIdConversation());
    }

    private String findOtherUserId(List<Utilisateur> participants) {
        for (Utilisateur p : participants) {
            if (!p.getIdUtilisateur().equals(currentUser.getIdUtilisateur())) {
                return p.getIdUtilisateur();
            }
        }
        return null;
    }

    private void sendExistingConversation(Conversation existing) {
        existing.setMessages(messageDAO.getMessagesByConversation(existing.getIdConversation()));
        List<Utilisateur> existingParticipants = conversationDAO.getParticipants(existing.getIdConversation());
        for (Utilisateur p : existingParticipants) {
            p.setOnline(ServerMain.isOnline(p));
            p.setPassword(null);
        }
        existing.setParticipants(existingParticipants);
        sendPacket(new Packet(Packet.Type.NEW_CONVERSATION, JsonUtils.serialize(existing)));
    }

    private void addParticipantsToConversation(Conversation conversation) {
        List<Utilisateur> participants = conversation.getParticipants();
        if (participants != null) {
            for (Utilisateur p : participants) {
                conversationDAO.addUserToConversation(p.getIdUtilisateur(), conversation.getIdConversation());
            }
        }

        boolean creatorIncluded = participants != null && participants.stream()
                .anyMatch(p -> p.getIdUtilisateur().equals(currentUser.getIdUtilisateur()));

        if (!creatorIncluded) {
            conversationDAO.addUserToConversation(currentUser.getIdUtilisateur(), conversation.getIdConversation());
        }
    }

    private void broadcastNewConversation(Conversation conversation) {
        List<Utilisateur> fullParticipants = conversationDAO.getParticipants(conversation.getIdConversation());
        for (Utilisateur p : fullParticipants) {
            p.setOnline(ServerMain.isOnline(p));
            p.setPassword(null);
        }
        conversation.setParticipants(fullParticipants);

        Packet newConvPacket = new Packet(Packet.Type.NEW_CONVERSATION, JsonUtils.serialize(conversation));
        String json = JsonUtils.serialize(newConvPacket);

        for (Utilisateur p : fullParticipants) {
            ServerMain.sendToUser(p.getIdUtilisateur(), json);
        }
    }

    private void handleAddNewConversationUser(Packet packet) {
        if (currentUser == null) return;

        UtilisateurConversation uc = JsonUtils.deserialize(packet.getData(), UtilisateurConversation.class);
        if (uc == null || uc.getIdUtilisateur() == null || uc.getIdConversation() == null) return;

        boolean added = conversationDAO.addUserToConversation(uc.getIdUtilisateur(), uc.getIdConversation());
        if (!added) {
            sendResponse(Packet.Type.NEW_CONVERSATION_USER, false, "Erreur lors de l'ajout de l'utilisateur à la conversation");
            return;
        }

        Conversation fullConv = loadFullConversation(uc.getIdConversation());
        if (fullConv == null) return;

        Packet newConvPacket = new Packet(Packet.Type.NEW_CONVERSATION, JsonUtils.serialize(fullConv));
        ServerMain.sendToUser(uc.getIdUtilisateur(), JsonUtils.serialize(newConvPacket));

        notifyParticipants(fullConv.getParticipants(), uc, Packet.Type.NEW_CONVERSATION_USER);

        System.out.println("Utilisateur " + uc.getIdUtilisateur() + " ajouté à la conversation " + uc.getIdConversation());
    }

    private void handleGetMessages(Packet packet) {
        if (currentUser == null) return;

        String idConversation = packet.getData().replace("\"", "");
        List<Message> messages = messageDAO.getMessagesByConversation(idConversation);
        sendPacket(new Packet(Packet.Type.MESSAGES, JsonUtils.serialize(messages)));
    }

    private void handleGetOnlineClients() {
        List<Utilisateur> onlineClients = userDAO.getAllUsers();
        sendPacket(new Packet(Packet.Type.ONLINE_CLIENTS, JsonUtils.serialize(onlineClients)));
    }

    private void handleEditConversation(Packet packet) {
        Conversation conversation = JsonUtils.deserialize(packet.getData(), Conversation.class);
        conversationDAO.editConversation(conversation);
        List<Utilisateur> participants = conversationDAO.getParticipants(conversation.getIdConversation());
        conversation.setParticipants(participants);

        Packet packetToSend = new Packet(Packet.Type.EDIT_CONVERSATION, JsonUtils.serialize(conversation));
        System.out.println("Demande de renommage de conversation en " + conversation.getNom());

        for (Utilisateur p : participants) {
            ServerMain.sendToUser(p.getIdUtilisateur(), JsonUtils.serialize(packetToSend));
        }
    }

    private void handleAddMember(Packet packet) {
        if (currentUser == null) return;

        UtilisateurConversation uc = JsonUtils.deserialize(packet.getData(), UtilisateurConversation.class);
        if (uc == null || uc.getIdUtilisateur() == null || uc.getIdConversation() == null) return;

        boolean added = utilisateurConversationDAO.addMember(uc.getIdUtilisateur(), uc.getIdConversation());
        if (!added) return;

        Conversation conv = loadFullConversation(uc.getIdConversation());
        if (conv == null) return;

        Packet newConvPacket = new Packet(Packet.Type.NEW_CONVERSATION, JsonUtils.serialize(conv));
        ServerMain.sendToUser(uc.getIdUtilisateur(), JsonUtils.serialize(newConvPacket));

        notifyParticipants(conv.getParticipants(), uc, Packet.Type.ADD_MEMBER_TO_CONVERSATION);

        System.out.println("Membre " + uc.getIdUtilisateur() + " ajouté à la conversation " + uc.getIdConversation());
    }

    private Conversation loadFullConversation(String idConversation) {
        Conversation conv = conversationDAO.getConversationById(idConversation);
        if (conv == null) return null;

        conv.setMessages(messageDAO.getMessagesByConversation(idConversation));
        List<Utilisateur> participants = conversationDAO.getParticipants(idConversation);
        for (Utilisateur p : participants) {
            p.setOnline(ServerMain.isOnline(p));
            p.setPassword(null);
        }
        conv.setParticipants(participants);
        return conv;
    }

    private void notifyParticipants(List<Utilisateur> participants, UtilisateurConversation uc, Packet.Type type) {
        Packet notifPacket = new Packet(type, JsonUtils.serialize(uc));
        String notifJson = JsonUtils.serialize(notifPacket);
        for (Utilisateur p : participants) {
            if (!p.getIdUtilisateur().equals(uc.getIdUtilisateur())) {
                ServerMain.sendToUser(p.getIdUtilisateur(), notifJson);
            }
        }
    }

    private void sendPacket(Packet packet) {
        clientHandler.sendMessage(JsonUtils.serialize(packet));
    }

    private void sendResponse(Packet.Type type, boolean success, String message) {
        sendPacket(new Packet(type, JsonUtils.serialize(new Response(success, message))));
    }

    public Utilisateur getCurrentUser() {
        return currentUser;
    }
}
