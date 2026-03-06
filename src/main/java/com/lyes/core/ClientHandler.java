package com.lyes.core;

import com.lyes.ServerMain;
import com.lyes.models.Packet;
import com.lyes.utils.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private RequestHandler requestHandler;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            requestHandler = new RequestHandler(this);

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Reçu : " + line);

                Packet packet = JsonUtils.deserialize(line, Packet.class);
                if (packet == null || packet.getType() == null) {
                    continue;
                }

                requestHandler.handlePacket(packet);
            }
        } catch (IOException e) {
            System.err.println("Client déconnecté : " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    public synchronized void sendMessage(String json) {
        if (out != null) {
            out.println(json);
        }
    }

    private void disconnect() {
        if (requestHandler != null && requestHandler.getCurrentUser() != null) {
            ServerMain.removeOnlineClient(requestHandler.getCurrentUser());
            System.out.println("Client déconnecté : " + requestHandler.getCurrentUser().getUsername());
        }
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture du socket : " + e.getMessage());
        }
    }
}
