package org.example.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AnnuaireService extends Remote {

    // --- Services accessibles à tous ---

    List<Person> listerParCategorie(Category categorie)
            throws RemoteException;

    List<Person> listerProfesseursParDomaine(String domaine)
            throws RemoteException;

    List<Person> rechercherParNomOuPrenom(String critere)
            throws RemoteException;

    Person rechercherParId(String id)
            throws RemoteException;

    // --- Services accessible seulement par l’admin ---

    void ajouterMembre(Person person, String adminPassword)
            throws RemoteException, AuthenticationException;

    void supprimerMembre(String id, String adminPassword)
            throws RemoteException, AuthenticationException;

    void modifierMembre(Person person, String adminPassword)
            throws RemoteException, AuthenticationException;

    void mettreSurListeRouge(String id, String adminPassword)
            throws RemoteException, AuthenticationException;

    void retirerDeListeRouge(String id, String adminPassword)
            throws RemoteException, AuthenticationException;
}
