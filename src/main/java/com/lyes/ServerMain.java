package com.lyes;

import com.lyes.core.ClientHandler;
import com.lyes.models.Packet;
import com.lyes.models.Utilisateur;
import com.lyes.utils.JsonUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    private static final int PORT = 2026;

    // Map : idUtilisateur -> ClientHandler
    private static final ConcurrentHashMap<Utilisateur, ClientHandler> onlineClients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur de Chat lancé sur le port " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }

    /**
     * Ajoute un client connecté et notifie tous les autres clients de la mise à jour des utilisateurs en ligne.
     */
    public static void addOnlineClient(Utilisateur user, ClientHandler handler) {
        onlineClients.put(user, handler);
        System.out.println("Utilisateur connecté : " + user.getIdUtilisateur() + " | Total en ligne : " + onlineClients.size());
        broadcastOnlineUsers();
    }

    /**
     * Retire un client déconnecté et notifie tous les autres clients.
     */
    public static void removeOnlineClient(Utilisateur user) {
        onlineClients.remove(user);
        System.out.println("Utilisateur déconnecté : " + user.getIdUtilisateur() + " | Total en ligne : " + onlineClients.size());
        broadcastOnlineUsers();
    }

    /**
     * Vérifie si un utilisateur est en ligne.
     */
    public static boolean isOnline(Utilisateur user) {
        return onlineClients.containsKey(user);
    }

    /**
     * Retourne la liste des IDs des utilisateurs en ligne.
     */
    public static List<Utilisateur> getOnlineUsers() {
        return new ArrayList<>(onlineClients.keySet());
    }

    /**
     * Envoie à tous les clients connectés la liste mise à jour des utilisateurs en ligne.
     */
    public static void broadcastOnlineUsers() {
        List<Utilisateur> onlineClients = getOnlineUsers();
        String data = JsonUtils.serialize(onlineClients);
        Packet packet = new Packet(Packet.Type.ONLINE_CLIENTS, data);
        String json = JsonUtils.serialize(packet);

        for (ClientHandler handler : ServerMain.onlineClients.values()) {
            handler.sendMessage(json);
        }
    }

    /**
     * Envoie un paquet à un utilisateur spécifique (par son id).
     */
    public static void sendToUser(String idUtilisateur, String json) {
        for (Map.Entry<Utilisateur, ClientHandler> entry : onlineClients.entrySet()) {
            if (entry.getKey().getIdUtilisateur().equals(idUtilisateur)) {
                entry.getValue().sendMessage(json);
                return;
            }
        }
    }

    /**
     * Envoie un paquet à une liste d'utilisateurs (participants d'une conversation).
     */
    public static void sendToUsers(List<String> userIds, String json) {
        for (String userId : userIds) {
            sendToUser(userId, json);
        }
    }
}