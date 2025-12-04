package org.example.common;

import java.io.Serializable;
import java.util.Objects;

public class Person implements Serializable {

    private String id;                // identifiant unique interne
    private String nom;
    private String prenom;
    private Category category;
    private String matricule;         // seulement pour ETUDIANT
    private String email;
    private String telephone;         // Seulement pour PROFESSEUR / AUXILIAIRE
    private String domaineActivite;   // ex: "MATHEMATIQUE", "INFORMATIQUE", ...
    private boolean listeRouge;

    public Person() { }

    public Person(String id,
                  String nom,
                  String prenom,
                  Category category,
                  String matricule,
                  String email,
                  String telephone,
                  String domaineActivite,
                  boolean listeRouge) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.category = category;
        this.matricule = matricule;
        this.email = email;
        this.telephone = telephone;
        this.domaineActivite = domaineActivite;
        this.listeRouge = listeRouge;
    }

    // Getters / setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getDomaineActivite() { return domaineActivite; }
    public void setDomaineActivite(String domaineActivite) { this.domaineActivite = domaineActivite; }

    public boolean isListeRouge() { return listeRouge; }
    public void setListeRouge(boolean listeRouge) { this.listeRouge = listeRouge; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", category=" + category +
                ", listeRouge=" + listeRouge +
                '}';
    }
}
