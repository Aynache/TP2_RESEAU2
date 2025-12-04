package org.example.server;

import org.example.common.Category;
import org.example.common.Person;

import java.util.List;
import java.util.Optional;

public interface AnnuaireRepository {

    List<Person> findByCategorie(Category categorie);

    List<Person> findProfesseursByDomaine(String domaine);

    List<Person> searchByNomOuPrenom(String critere);

    Optional<Person> findById(String id);

    List<Person> findAll();

    void save(Person person);          // ajout ou modification

    void deleteById(String id);
}
