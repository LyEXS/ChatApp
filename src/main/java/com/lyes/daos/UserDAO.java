package com.lyes.daos;

import com.lyes.db.DatabaseConnection;
import com.lyes.models.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Vérifie les identifiants d'un utilisateur.
     */
    public boolean checkUser(Utilisateur utilisateur) {
        String query = "SELECT * FROM Utilisateur WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, utilisateur.getUsername());
            ps.setString(2, utilisateur.getPassword());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    /**
     * Authentifie un utilisateur et retourne l'objet complet (avec son id) depuis la BDD.
     */
    public Utilisateur authenticate(String username, String password) {
        String query = "SELECT id_utilisateur, username, password FROM Utilisateur WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getString("id_utilisateur"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification : " + e.getMessage());
        }
        return null;
    }

    /**
     * Retourne tous les utilisateurs enregistrés (sans le mot de passe).
     */
    public List<Utilisateur> getAllUsers() {
        List<Utilisateur> users = new ArrayList<>();
        String query = "SELECT id_utilisateur, username FROM Utilisateur";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Utilisateur u = new Utilisateur(
                        rs.getString("id_utilisateur"),
                        rs.getString("username"),
                        null
                );
                users.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return users;
    }

    /**
     * Retourne un utilisateur par son id.
     */
    public Utilisateur getUserById(String idUtilisateur) {
        String query = "SELECT id_utilisateur, username FROM Utilisateur WHERE id_utilisateur = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getString("id_utilisateur"),
                            rs.getString("username"),
                            null
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return null;
    }

    public void createUser(Utilisateur user) {
        String query = "INSERT INTO Utilisateur(id_utilisateur, username, password) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getIdUtilisateur());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
