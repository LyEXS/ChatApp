package com.lyes.handlers;

import com.lyes.models.Packet;
import com.lyes.session.ClientSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Routes incoming packets to the appropriate handler based on packet type.
 * Replaces the monolithic RequestHandler switch statement.
 */
public class PacketRouter {
    private final Map<Packet.Type, PacketHandler> handlers = new HashMap<>();

    /**
     * Registers a handler for one or more packet types.
     */
    public void register(PacketHandler handler, Packet.Type... types) {
        for (Packet.Type type : types) {
            handlers.put(type, handler);
        }
    }

    /**
     * Routes a packet to the appropriate handler.
     */
    public void route(Packet packet, ClientSession session) {
        PacketHandler handler = handlers.get(packet.getType());
        if (handler != null) {
            handler.handle(packet, session);
        } else {
            System.out.println("Type de paquet inconnu : " + packet.getType());
        }
    }
}

