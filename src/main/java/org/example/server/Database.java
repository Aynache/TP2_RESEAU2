package org.example.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    static {
        try {
            // Charge le driver SQLite (souvent optionnel mais ça ne fait pas de mal)
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite non trouvé : " + e.getMessage());
        }
    }

    /**
     * Retourne une nouvelle connexion SQLite.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(ServerConfig.DB_URL);
    }

    /**
     * Initialise la base : crée la table person si elle n'existe pas.
     */
    public static void init() {
        String sql = """
                CREATE TABLE IF NOT EXISTS person (
                    id TEXT PRIMARY KEY,
                    nom TEXT NOT NULL,
                    prenom TEXT NOT NULL,
                    category TEXT NOT NULL,
                    matricule TEXT,
                    email TEXT,
                    telephone TEXT,
                    domaine_activite TEXT,
                    liste_rouge INTEGER NOT NULL DEFAULT 0
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Table 'person' OK (créée ou déjà existante).");

        } catch (SQLException e) {
            System.err.println("Erreur init DB : " + e.getMessage());
        }
    }
}
