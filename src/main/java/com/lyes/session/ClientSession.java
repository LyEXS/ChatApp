package com.lyes.session;

import com.lyes.handlers.PacketRouter;
import com.lyes.models.Utilisateur;
import com.lyes.network.PacketReceiver;
import com.lyes.network.PacketSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Represents a connected client session.
 * Owns the socket, packet sender/receiver, and the authenticated user.
 */
public class ClientSession extends Thread {
    private final Socket socket;
    private final PacketRouter router;

    private PacketSender sender;
    private PacketReceiver receiver;
    private Utilisateur currentUser;

    public ClientSession(Socket socket, PacketRouter router) {
        this.socket = socket;
        this.router = router;
    }

    @Override
    public void run() {
        try {
            sender = new PacketSender(new PrintWriter(socket.getOutputStream(), true));
            receiver = new PacketReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream())));

            receiver.listen(packet -> router.route(packet, this));

        } catch (IOException e) {
            System.err.println("Client déconnecté : " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void disconnect() {
        if (currentUser != null) {
            SessionManager.getInstance().removeOnlineClient(currentUser);
            System.out.println("Client déconnecté : " + currentUser.getUsername());
        }
        try {
            if (receiver != null) receiver.close();
            if (sender != null) sender.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture du socket : " + e.getMessage());
        }
    }

    // --- Getters & Setters ---

    public PacketSender getSender() {
        return sender;
    }

    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Utilisateur currentUser) {
        this.currentUser = currentUser;
    }
}

