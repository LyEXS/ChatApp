package com.lyes.handlers;

import com.lyes.models.Packet;
import com.lyes.session.ClientSession;

/**
 * Interface for all packet handlers.
 * Each implementation handles one or more packet types.
 */
public interface PacketHandler {
    void handle(Packet packet, ClientSession session);
}

