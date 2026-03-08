package com.lyes.network;

import com.lyes.models.Packet;
import com.lyes.models.Response;
import com.lyes.utils.JsonUtils;

import java.io.PrintWriter;

/**
 * Responsible for sending packets to a client through a socket output stream.
 */
public class PacketSender {
    private final PrintWriter out;

    public PacketSender(PrintWriter out) {
        this.out = out;
    }

    public synchronized void sendJson(String json) {
        if (out != null) {
            System.out.println("Envoyé : " + json);
            out.println(json);
        }
    }

    public void sendPacket(Packet packet) {
        sendJson(JsonUtils.serialize(packet));
    }

    public void sendResponse(Packet.Type type, boolean success, String message) {
        sendPacket(new Packet(type, JsonUtils.serialize(new Response(success, message))));
    }

    public void sendError(String message) {
        sendPacket(new Packet(Packet.Type.ERROR, message));
    }

    public void close() {
        if (out != null) {
            out.close();
        }
    }
}

