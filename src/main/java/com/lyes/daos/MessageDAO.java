package com.lyes.daos;

import com.lyes.db.DatabaseConnection;
import com.lyes.models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    /**
     * Sauvegarde un message en BDD.
     */
    public void saveMessage(Message message) {
        String query = "INSERT INTO Message (id_message, id_conversation, id_utilisateur, content, date_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, message.getIdMessage());
            pstmt.setString(2, message.getIdConversation());
            pstmt.setString(3, message.getIdUtilisateur());
            pstmt.setString(4, message.getContent());
            pstmt.setTimestamp(5, message.getDateTime());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde du message : " + e.getMessage());
        }
    }

    /**
     * Récupère tous les messages d'une conversation, triés par date.
     */
    public List<Message> getMessagesByConversation(String idConversation) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT id_message, id_conversation, id_utilisateur, content, date_time FROM Message WHERE id_conversation = ? ORDER BY date_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, idConversation);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Message m = new Message(
                            rs.getString("id_message"),
                            rs.getString("id_conversation"),
                            rs.getString("id_utilisateur"),
                            rs.getString("content"),
                            rs.getTimestamp("date_time")
                    );
                    messages.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des messages : " + e.getMessage());
        }
        return messages;
    }
}
