package org.example.server;

import org.example.common.Category;
import org.example.common.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteAnnuaireRepository implements AnnuaireRepository {

    public SQLiteAnnuaireRepository() {
        // Rien de spécial ici pour l'instant
    }

    // ---------- Méthodes de lecture ----------

    @Override
    public List<Person> findByCategorie(Category categorie) {
        String sql = "SELECT * FROM person WHERE category = ?";
        List<Person> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categorie.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToPerson(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByCategorie : " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Person> findProfesseursByDomaine(String domaine) {
        String sql = "SELECT * FROM person WHERE category = ? AND LOWER(domaine_activite) = LOWER(?)";
        List<Person> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, Category.PROFESSEUR.name());
            ps.setString(2, domaine);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToPerson(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findProfesseursByDomaine : " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Person> searchByNomOuPrenom(String critere) {
        String sql = """
                SELECT * FROM person
                WHERE LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ?
                """;
        List<Person> result = new ArrayList<>();
        String pattern = "%" + critere.toLowerCase() + "%";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToPerson(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur searchByNomOuPrenom : " + e.getMessage());
        }
        return result;
    }

    @Override
    public Optional<Person> findById(String id) {
        String sql = "SELECT * FROM person WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPerson(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById : " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Person> findAll() {
        String sql = "SELECT * FROM person";
        List<Person> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowToPerson(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findAll : " + e.getMessage());
        }
        return result;
    }

    // ---------- Méthodes d'écriture ----------

    @Override
    public void save(Person person) {
        // On utilise un UPSERT SQLite : INSERT ... ON CONFLICT(id) DO UPDATE
        String sql = """
                INSERT INTO person (id, nom, prenom, category, matricule, email, telephone, domaine_activite, liste_rouge)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    nom = excluded.nom,
                    prenom = excluded.prenom,
                    category = excluded.category,
                    matricule = excluded.matricule,
                    email = excluded.email,
                    telephone = excluded.telephone,
                    domaine_activite = excluded.domaine_activite,
                    liste_rouge = excluded.liste_rouge
                """;

        // Si pas d'id, on en génère un
        if (person.getId() == null || person.getId().isEmpty()) {
            person.setId(org.example.common.IdGenerator.newId());
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, person.getId());
            ps.setString(2, person.getNom());
            ps.setString(3, person.getPrenom());
            ps.setString(4, person.getCategory().name());
            ps.setString(5, person.getMatricule());
            ps.setString(6, person.getEmail());
            ps.setString(7, person.getTelephone());
            ps.setString(8, person.getDomaineActivite());
            ps.setInt(9, person.isListeRouge() ? 1 : 0);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur save : " + e.getMessage());
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM person WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur deleteById : " + e.getMessage());
        }
    }

    // ---------- Mapping ResultSet -> Person ----------

    private Person mapRowToPerson(ResultSet rs) throws SQLException {
        Person p = new Person();
        p.setId(rs.getString("id"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));

        String catStr = rs.getString("category");
        if (catStr != null) {
            p.setCategory(Category.valueOf(catStr));
        }

        p.setMatricule(rs.getString("matricule"));
        p.setEmail(rs.getString("email"));
        p.setTelephone(rs.getString("telephone"));
        p.setDomaineActivite(rs.getString("domaine_activite"));
        p.setListeRouge(rs.getInt("liste_rouge") == 1);

        return p;
    }
}
