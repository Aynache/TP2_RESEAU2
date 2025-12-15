package org.example.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestDataInserter {

    public static void main(String[] args) {
        Database.init();

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            insertProfessors(conn);
            insertStudents(conn);
            insertAuxiliaries(conn);

            conn.commit();
            System.out.println("✅ Données de test insérées avec succès.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur insertion données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===================== PROFESSEURS =====================
    private static void insertProfessors(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO person (id, nom, prenom, category, matricule, email, telephone, domaine_activite, liste_rouge)
                VALUES (?, ?, ?, 'PROFESSEUR', NULL, ?, ?, ?, 0)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 10; i++) {
                ps.setString(1, "prof-" + i);
                ps.setString(2, "ProfNom" + i);
                ps.setString(3, "ProfPrenom" + i);
                ps.setString(4, "prof" + i + "@uqtr.ca");
                ps.setString(5, "514-555-10" + String.format("%02d", i));
                ps.setString(6, getDomaine(i));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ===================== ÉTUDIANTS =====================
    private static void insertStudents(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO person (id, nom, prenom, category, matricule, email, telephone, domaine_activite, liste_rouge)
                VALUES (?, ?, ?, 'ETUDIANT', ?, ?, NULL, ?, 0)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 10; i++) {
                ps.setString(1, "etud-" + i);
                ps.setString(2, "EtudNom" + i);
                ps.setString(3, "EtudPrenom" + i);
                ps.setString(4, "E2024" + String.format("%03d", i));
                ps.setString(5, "etud" + i + "@uqtr.ca");
                ps.setString(6, getDomaine(i));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ===================== AUXILIAIRES =====================
    private static void insertAuxiliaries(Connection conn) throws SQLException {
        String sql = """
                INSERT INTO person (id, nom, prenom, category, matricule, email, telephone, domaine_activite, liste_rouge)
                VALUES (?, ?, ?, 'AUXILIAIRE', NULL, ?, ?, ?, 0)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 10; i++) {
                ps.setString(1, "aux-" + i);
                ps.setString(2, "AuxNom" + i);
                ps.setString(3, "AuxPrenom" + i);
                ps.setString(4, "aux" + i + "@uqtr.ca");
                ps.setString(5, "514-555-20" + String.format("%02d", i));
                ps.setString(6, getDomaine(i));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ===================== DOMAINES =====================
    private static String getDomaine(int i) {
        return switch (i % 5) {
            case 0 -> "Informatique";
            case 1 -> "Géographie";
            case 2 -> "Mathématiques";
            case 3 -> "Économie";
            default -> "Biologie";
        };
    }
}
