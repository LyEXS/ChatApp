package com.lyes;

import com.lyes.daos.ConversationDAO;
import com.lyes.daos.MessageDAO;
import com.lyes.daos.UserDAO;
import com.lyes.daos.UtilisateurConversationDAO;
import com.lyes.handlers.*;
import com.lyes.models.Packet;
import com.lyes.services.AuthService;
import com.lyes.services.ConversationService;
import com.lyes.services.MessageService;
import com.lyes.session.ClientSession;
import com.lyes.session.SessionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PORT = 2026;

    public static void main(String[] args) {
        // --- Wire dependencies ---
        SessionManager sessionManager = SessionManager.getInstance();

        // DAOs
        UserDAO userDAO = new UserDAO();
        MessageDAO messageDAO = new MessageDAO();
        ConversationDAO conversationDAO = new ConversationDAO();
        UtilisateurConversationDAO utilisateurConversationDAO = new UtilisateurConversationDAO();

        // Services
        MessageService messageService = new MessageService(messageDAO);
        ConversationService conversationService = new ConversationService(
                conversationDAO, messageDAO, utilisateurConversationDAO, sessionManager);
        AuthService authService = new AuthService(userDAO, conversationService, sessionManager);

        // Handlers
        NotificationHandler notificationHandler = new NotificationHandler(sessionManager);
        AuthHandler authHandler = new AuthHandler(authService, sessionManager);
        MessageHandler messageHandler = new MessageHandler(messageService, conversationService, sessionManager);
        ConversationHandler conversationHandler = new ConversationHandler(conversationService, authService, sessionManager,notificationHandler);

        // Router
        PacketRouter router = new PacketRouter();
        router.register(authHandler, Packet.Type.AUTH, Packet.Type.SIGNUP);
        router.register(messageHandler, Packet.Type.NEW_MESSAGE, Packet.Type.MESSAGES);
        router.register(conversationHandler,
                Packet.Type.CONVERSATIONS, Packet.Type.NEW_CONVERSATION, Packet.Type.NEW_CONVERSATION_USER,
                Packet.Type.EDIT_CONVERSATION, Packet.Type.ADD_MEMBER_TO_CONVERSATION, Packet.Type.ONLINE_CLIENTS);

        // --- Start server ---
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur de Chat lancé sur le port " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());

                ClientSession session = new ClientSession(clientSocket, router);
                session.start();
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

}