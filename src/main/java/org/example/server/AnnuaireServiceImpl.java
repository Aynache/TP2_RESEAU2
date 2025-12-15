package org.example.server;


import org.example.common.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.stream.Collectors;

public class AnnuaireServiceImpl extends UnicastRemoteObject implements AnnuaireService {

    private final AnnuaireRepository repository;

    public AnnuaireServiceImpl(AnnuaireRepository repository) throws RemoteException {
        super();
        this.repository = repository;
    }

    // --------- Méthodes accessibles à tous ---------

    @Override
    public List<Person> listerParCategorie(Category categorie) throws RemoteException {
        List<Person> persons = repository.findByCategorie(categorie);
        return appliquerPolitiqueListeRouge(persons);
    }

    @Override
    public List<Person> listerProfesseursParDomaine(String domaine) throws RemoteException {
        List<Person> persons = repository.findProfesseursByDomaine(domaine);
        return appliquerPolitiqueListeRouge(persons);
    }

    @Override
    public List<Person> rechercherParNomOuPrenom(String critere) throws RemoteException {
        List<Person> persons = repository.searchByNomOuPrenom(critere);
        return appliquerPolitiqueListeRouge(persons);
    }

    @Override
    public Person rechercherParId(String id) throws RemoteException {
        return repository.findById(id)
                .map(this::appliquerPolitiqueListeRougeSurUnePersonne)
                .orElse(null);
    }

    // --------- Méthodes admin ---------

    @Override
    public void ajouterMembre(Person person, String adminPassword)
            throws RemoteException, AuthenticationException {
        verifierMotDePasse(adminPassword);
        if (person.getId() == null || person.getId().isEmpty()) {
            person.setId(IdGenerator.newId());
        }
        repository.save(person);
    }

    @Override
    public void supprimerMembre(String id, String adminPassword)
            throws RemoteException, AuthenticationException {
        verifierMotDePasse(adminPassword);
        repository.deleteById(id);
    }

    @Override
    public void modifierMembre(Person person, String adminPassword)
            throws RemoteException, AuthenticationException {
        verifierMotDePasse(adminPassword);
        if (person.getId() == null || person.getId().isEmpty()) {
            throw new AuthenticationException("Personne sans id, impossible de modifier.");
        }
        repository.save(person);
    }

    @Override
    public void mettreSurListeRouge(String id, String adminPassword)
            throws RemoteException, AuthenticationException {
        verifierMotDePasse(adminPassword);
        repository.findById(id).ifPresent(p -> {
            p.setListeRouge(true);
            repository.save(p);
        });
    }

    @Override
    public void retirerDeListeRouge(String id, String adminPassword)
            throws RemoteException, AuthenticationException {
        verifierMotDePasse(adminPassword);
        repository.findById(id).ifPresent(p -> {
            p.setListeRouge(false);
            repository.save(p);
        });
    }

    // --------- Méthodes privées ---------

    private void verifierMotDePasse(String adminPassword) throws AuthenticationException {
        if (!ServerConfig.ADMIN_PASSWORD.equals(adminPassword)) {
            throw new AuthenticationException("Mot de passe administrateur invalide.");
        }
    }

    private List<Person> appliquerPolitiqueListeRouge(List<Person> persons) {
        return persons.stream()
                .map(this::appliquerPolitiqueListeRougeSurUnePersonne)
                .collect(Collectors.toList());
    }

    private Person appliquerPolitiqueListeRougeSurUnePersonne(Person p) {
        if (!p.isListeRouge()) {
            return p;
        }
        // On retourne une copie “masquée”
        Person masked = new Person();
        masked.setId(p.getId());
        masked.setNom(p.getNom());
        masked.setPrenom(p.getPrenom());
        masked.setCategory(p.getCategory());
        masked.setListeRouge(true);
        // les autres champs restent null
        return masked;
    }

    @Override
    public boolean verifierAdmin(String adminPassword) throws RemoteException {
        return ServerConfig.ADMIN_PASSWORD.equals(adminPassword);
    }

}
