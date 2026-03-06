package com.lyes.daos;

import com.lyes.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UtilisateurConversationDAO {

    /**
     * Ajoute un membre à une conversation.
     */
    public boolean addMember(String idUtilisateur, String idConversation) {
        String query = "INSERT INTO Utilisateur_Conversation (id_utilisateur, id_conversation) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idUtilisateur);
            ps.setString(2, idConversation);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du membre à la conversation : " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un membre d'une conversation.
     */
    public boolean removeMember(String idUtilisateur, String idConversation) {
        String query = "DELETE FROM Utilisateur_Conversation WHERE id_utilisateur = ? AND id_conversation = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idUtilisateur);
            ps.setString(2, idConversation);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du membre de la conversation : " + e.getMessage());
            return false;
        }
    }
}

