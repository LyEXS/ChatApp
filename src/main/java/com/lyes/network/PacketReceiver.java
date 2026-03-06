package com.lyes.network;

import com.lyes.models.Packet;
import com.lyes.utils.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Responsible for reading packets from a client through a socket input stream.
 * Runs a blocking read loop and delegates each packet to a callback.
 */
public class PacketReceiver {
    private final BufferedReader in;

    public PacketReceiver(BufferedReader in) {
        this.in = in;
    }

    /**
     * Starts blocking read loop. Calls onPacket for each valid packet received.
     * Returns when the client disconnects or an IOException occurs.
     */
    public void listen(Consumer<Packet> onPacket) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("Reçu : " + line);

            Packet packet = JsonUtils.deserialize(line, Packet.class);
            if (packet == null || packet.getType() == null) {
                continue;
            }

            onPacket.accept(packet);
        }
    }

    public void close() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture du reader : " + e.getMessage());
        }
    }
}

