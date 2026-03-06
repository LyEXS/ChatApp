package com.lyes.session;

import com.lyes.daos.UserDAO;
import com.lyes.models.Packet;
import com.lyes.models.Utilisateur;
import com.lyes.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages online client sessions: tracks who is connected, handles broadcasting.
 * Singleton — use getInstance() to access.
 */
public class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();

    private final ConcurrentHashMap<Utilisateur, ClientSession> onlineClients = new ConcurrentHashMap<>();
    private final UserDAO userDAO = new UserDAO();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void addOnlineClient(Utilisateur user, ClientSession session) {
        onlineClients.put(user, session);
        System.out.println("Utilisateur connecté : " + user.getIdUtilisateur() + " | Total en ligne : " + onlineClients.size());
        broadcastOnlineUsers();
    }

    public void removeOnlineClient(Utilisateur user) {
        onlineClients.remove(user);
        System.out.println("Utilisateur déconnecté : " + user.getIdUtilisateur() + " | Total en ligne : " + onlineClients.size());
        broadcastOnlineUsers();
    }

    public boolean isOnline(Utilisateur user) {
        return onlineClients.containsKey(user);
    }

    public List<Utilisateur> getOnlineUsers() {
        return new ArrayList<>(onlineClients.keySet());
    }

    /**
     * Broadcasts the updated online users list to all connected clients.
     */
    public void broadcastOnlineUsers() {
        List<Utilisateur> users = userDAO.getAllUsers();
        List<Utilisateur> onlineUsers = getOnlineUsers();

        for (Utilisateur user : users) {
            user.setOnline(onlineUsers.contains(user));
        }

        String data = JsonUtils.serialize(users);
        Packet packet = new Packet(Packet.Type.ONLINE_CLIENTS, data);
        String json = JsonUtils.serialize(packet);

        for (ClientSession session : onlineClients.values()) {
            session.getSender().sendJson(json);
        }
    }

    /**
     * Sends a JSON message to a specific user by their id.
     */
    public void sendToUser(String idUtilisateur, String json) {
        for (Map.Entry<Utilisateur, ClientSession> entry : onlineClients.entrySet()) {
            if (entry.getKey().getIdUtilisateur().equals(idUtilisateur)) {
                entry.getValue().getSender().sendJson(json);
                return;
            }
        }
    }

    /**
     * Sends a JSON message to a list of users.
     */
    public void sendToUsers(List<String> userIds, String json) {
        for (String userId : userIds) {
            sendToUser(userId, json);
        }
    }


}

