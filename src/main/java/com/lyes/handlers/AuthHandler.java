package com.lyes.handlers;

import com.lyes.models.InitData;
import com.lyes.models.Packet;
import com.lyes.models.Utilisateur;
import com.lyes.services.AuthService;
import com.lyes.session.ClientSession;
import com.lyes.session.SessionManager;
import com.lyes.utils.JsonUtils;

/**
 * Handles AUTH and SIGNUP packet types.
 */
public class AuthHandler implements PacketHandler {
    private final AuthService authService;
    private final SessionManager sessionManager;

    public AuthHandler(AuthService authService, SessionManager sessionManager) {
        this.authService = authService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(Packet packet, ClientSession session) {
        switch (packet.getType()) {
            case AUTH -> handleAuth(packet, session);
            case SIGNUP -> handleSignUp(packet, session);
            default -> {}
        }
    }

    private void handleAuth(Packet packet, ClientSession session) {
        Utilisateur credentials = JsonUtils.deserialize(packet.getData(), Utilisateur.class);
        if (credentials == null) {
            session.getSender().sendResponse(Packet.Type.AUTH, false, "Données d'authentification invalides");
            return;
        }

        Utilisateur user = authService.authenticate(credentials.getUsername(), credentials.getPassword());
        if (user == null) {
            session.getSender().sendResponse(Packet.Type.AUTH, false, "Nom d'utilisateur ou mot de passe incorrect");
            return;
        }

        session.setCurrentUser(user);
        user.setPassword(null);
        user.setOnline(true);

        session.getSender().sendResponse(Packet.Type.AUTH, true, "Connexion réussie");
        sessionManager.addOnlineClient(user, session);
        sendInitData(session);
    }

    private void handleSignUp(Packet packet, ClientSession session) {
        Utilisateur userToSignUp = JsonUtils.deserialize(packet.getData(), Utilisateur.class);
        if (userToSignUp == null) {
            session.getSender().sendResponse(Packet.Type.SIGNUP, false, "Veuillez vérifier les données fournies");
            return;
        }

        authService.signUp(userToSignUp);
        session.getSender().sendResponse(Packet.Type.AUTH, true, "Connexion réussie");
        handleAuth(packet, session);
    }

    private void sendInitData(ClientSession session) {
        Utilisateur currentUser = session.getCurrentUser();
        InitData initData = authService.buildInitData(currentUser);

        Packet initPacket = new Packet(Packet.Type.INIT_DATA, JsonUtils.serialize(initData));
        session.getSender().sendPacket(initPacket);

        System.out.println("Données initiales envoyées à " + currentUser.getUsername());
    }
}

