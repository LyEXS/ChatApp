package com.lyes.handlers;

import com.google.gson.reflect.TypeToken;
import com.lyes.models.Notification;
import com.lyes.models.Packet;
import com.lyes.models.Utilisateur;
import com.lyes.session.ClientSession;
import com.lyes.session.SessionManager;
import com.lyes.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.List;

public class NotificationHandler implements  PacketHandler{

    private final SessionManager sessionManager ;

    public NotificationHandler(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }
    @Override
    public void handle(Packet packet, ClientSession session) {
        switch (packet.getType()){
            case NOTIFICATION -> handleSendNotification(packet,session);
        }
    }

    public void handleSendNotification(Packet packet, ClientSession session){
        if(session.getCurrentUser() == null) return ;


        Notification Notification = JsonUtils.deserialize(packet.getData(), com.lyes.models.Notification.class);




        Packet notificationPacket = new Packet(Packet.Type.NOTIFICATION, JsonUtils.serialize(Notification));
        String notificationJson = JsonUtils.serialize(notificationPacket);

        for (Utilisateur user : Notification.getConcernedUsers()) {
            if(!session.getCurrentUser().getIdUtilisateur().equals(user.getIdUtilisateur())){
                sessionManager.sendToUser(user.getIdUtilisateur(), notificationJson);
            }
        }
    }
}
