package com.lyes.daos;

import com.lyes.db.DatabaseConnection;
import com.lyes.models.Conversation;
import com.lyes.models.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConversationDAO {

    /**
     * Crée une nouvelle conversation en BDD.
     */
    public boolean createConversation(Conversation conversation) {
        String query = "INSERT INTO Conversation (id_conversation, nom, est_groupe) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, conversation.getIdConversation());
            ps.setString(2, conversation.getNom());
            ps.setBoolean(3, conversation.getEstGroupe());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la conversation : " + e.getMessage());
            return false;
        }
    }

    /**
     * Met à jour le nom et le type (groupe ou non) d'une conversation existante.
     */
    public void editConversation(Conversation conversation) {
        String query = "UPDATE Conversation SET nom = ?, est_groupe = ? WHERE id_conversation = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, conversation.getNom());
            ps.setBoolean(2, conversation.getEstGroupe() != null ? conversation.getEstGroupe() : false);
            ps.setString(3, conversation.getIdConversation());
            int rows = ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la conversation : " + e.getMessage());
        }
    }

    /**
     * Récupère une conversation par son id.
     */
    public Conversation getConversationById(String idConversation) {
        String query = "SELECT id_conversation, nom, est_groupe FROM Conversation WHERE id_conversation = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idConversation);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Conversation(
                            rs.getString("id_conversation"),
                            rs.getString("nom"),
                            rs.getBoolean("est_groupe")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la conversation : " + e.getMessage());
        }
        return null;
    }

    /**
     * Ajoute un utilisateur à une conversation (table Utilisateur_Conversation).
     */
    public boolean addUserToConversation(String idUtilisateur, String idConversation) {
        String query = "INSERT INTO Utilisateur_Conversation (id_utilisateur, id_conversation) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idUtilisateur);
            ps.setString(2, idConversation);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur à la conversation : " + e.getMessage());
            return false;
        }
    }

    /**
     * Cherche une conversation privée (non-groupe) existante entre deux utilisateurs.
     * @return la Conversation si elle existe, null sinon.
     */
    public Conversation findPrivateConversation(String idUser1, String idUser2) {
        String query = "SELECT c.id_conversation, c.nom, c.est_groupe " +
                "FROM Conversation c " +
                "INNER JOIN Utilisateur_Conversation uc1 ON c.id_conversation = uc1.id_conversation " +
                "INNER JOIN Utilisateur_Conversation uc2 ON c.id_conversation = uc2.id_conversation " +
                "WHERE c.est_groupe = false AND uc1.id_utilisateur = ? AND uc2.id_utilisateur = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idUser1);
            ps.setString(2, idUser2);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Conversation(
                            rs.getString("id_conversation"),
                            rs.getString("nom"),
                            rs.getBoolean("est_groupe")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de conversation privée : " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère toutes les conversations auxquelles un utilisateur participe.
     */
    public List<Conversation> getConversationsByUser(String idUtilisateur) {
        List<Conversation> conversations = new ArrayList<>();
        String query = "SELECT c.id_conversation, c.nom, c.est_groupe " +
                "FROM Conversation c " +
                "INNER JOIN Utilisateur_Conversation uc ON c.id_conversation = uc.id_conversation " +
                "WHERE uc.id_utilisateur = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Conversation conv = new Conversation(
                            rs.getString("id_conversation"),
                            rs.getString("nom"),
                            rs.getBoolean("est_groupe")
                    );
                    conversations.add(conv);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des conversations : " + e.getMessage());
        }
        return conversations;
    }

    /**
     * Récupère les participants d'une conversation.
     */
    public List<Utilisateur> getParticipants(String idConversation) {
        List<Utilisateur> participants = new ArrayList<>();
        String query = "SELECT u.id_utilisateur, u.username " +
                "FROM Utilisateur u " +
                "INNER JOIN Utilisateur_Conversation uc ON u.id_utilisateur = uc.id_utilisateur " +
                "WHERE uc.id_conversation = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idConversation);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Utilisateur u = new Utilisateur(
                            rs.getString("id_utilisateur"),
                            rs.getString("username"),
                            null
                    );
                    participants.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des participants : " + e.getMessage());
        }
        return participants;
    }
}

