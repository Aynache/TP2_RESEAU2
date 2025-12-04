package org.example.client;

import org.example.common.*;

import java.rmi.Naming;
import java.util.List;
import java.util.Scanner;

public class ConsoleClient {

    private AnnuaireService service;
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new ConsoleClient().lancer();
    }

    private void lancer() {
        try {
            String url = "rmi://localhost:1099/AnnuaireService";
            service = (AnnuaireService) Naming.lookup(url);
            System.out.println("Connecté au serveur : " + url);

            boolean quitter = false;
            while (!quitter) {
                afficherMenu();
                int choix = lireEntier("Votre choix : ");
                quitter = traiterChoix(choix);
            }

        } catch (Exception e) {
            System.err.println("Impossible de se connecter au serveur RMI.");
            e.printStackTrace();
        }
    }

    private void afficherMenu() {
        System.out.println("\n--- Menu principal ---");
        System.out.println("1. Lister les membres par catégorie");
        System.out.println("2. Lister les professeurs par domaine");
        System.out.println("3. Rechercher par nom/prénom");
        System.out.println("4. Rechercher par ID");
        System.out.println("5. [ADMIN] Ajouter un membre");
        System.out.println("6. [ADMIN] Supprimer un membre");
        System.out.println("7. [ADMIN] Modifier un membre");
        System.out.println("8. [ADMIN] Mettre sur liste rouge");
        System.out.println("9. [ADMIN] Retirer de la liste rouge");
        System.out.println("0. Quitter");
    }

    private boolean traiterChoix(int choix) {
        try {
            switch (choix) {
                case 1:
                    actionListerParCategorie();
                    break;
                case 2:
                    actionListerProfParDomaine();
                    break;
                case 3:
                    actionRechercherNomPrenom();
                    break;
                case 4:
                    actionRechercherId();
                    break;
                case 5:
                    actionAdminAjouter();
                    break;
                case 6:
                    actionAdminSupprimer();
                    break;
                case 7:
                    actionAdminModifier();
                    break;
                case 8:
                    actionAdminMettreListeRouge();
                    break;
                case 9:
                    actionAdminRetirerListeRouge();
                    break;
                case 0:
                    System.out.println("Au revoir.");
                    return true;
                default:
                    System.out.println("Choix invalide.");
            }
        } catch (AuthenticationException e) {
            System.out.println("Erreur d'authentification : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // --- Actions ---

    private void actionListerParCategorie() throws Exception {
        System.out.println("Catégories : 1=PROFESSEUR, 2=AUXILIAIRE, 3=ETUDIANT");
        int c = lireEntier("Choisissez la catégorie : ");
        Category cat = switch (c) {
            case 1 -> Category.PROFESSEUR;
            case 2 -> Category.AUXILIAIRE;
            case 3 -> Category.ETUDIANT;
            default -> {
                System.out.println("Catégorie invalide.");
                yield null;
            }
        };
        if (cat == null) return;
        List<Person> personnes = service.listerParCategorie(cat);
        ClientUtils.afficherListePersonnes(personnes);
    }

    private void actionListerProfParDomaine() throws Exception {
        String domaine = lireLigne("Domaine d'activité : ");
        List<Person> personnes = service.listerProfesseursParDomaine(domaine);
        ClientUtils.afficherListePersonnes(personnes);
    }

    private void actionRechercherNomPrenom() throws Exception {
        String critere = lireLigne("Nom ou prénom (ou partie) : ");
        List<Person> personnes = service.rechercherParNomOuPrenom(critere);
        ClientUtils.afficherListePersonnes(personnes);
    }

    private void actionRechercherId() throws Exception {
        String id = lireLigne("ID : ");
        Person p = service.rechercherParId(id);
        ClientUtils.afficherPersonne(p);
    }

    private void actionAdminAjouter() throws Exception {
        String pwd = lireLigne("Mot de passe admin : ");
        Person p = saisirPersonneSansId();
        service.ajouterMembre(p, pwd);
        System.out.println("Membre ajouté.");
    }

    private void actionAdminSupprimer() throws Exception {
        String pwd = lireLigne("Mot de passe admin : ");
        String id = lireLigne("ID du membre à supprimer : ");
        service.supprimerMembre(id, pwd);
        System.out.println("Membre supprimé (si existant).");
    }

    private void actionAdminModifier() throws Exception {
        String pwd = lireLigne("Mot de passe admin : ");
        String id = lireLigne("ID du membre à modifier : ");
        Person existant = service.rechercherParId(id);
        if (existant == null) {
            System.out.println("Aucun membre avec cet ID.");
            return;
        }
        System.out.println("Membre actuel :");
        ClientUtils.afficherPersonne(existant);
        Person modifie = saisirPersonneAvecId(id);
        service.modifierMembre(modifie, pwd);
        System.out.println("Membre modifié.");
    }

    private void actionAdminMettreListeRouge() throws Exception {
        String pwd = lireLigne("Mot de passe admin : ");
        String id = lireLigne("ID du membre à mettre sur liste rouge : ");
        service.mettreSurListeRouge(id, pwd);
        System.out.println("Liste rouge activée.");
    }

    private void actionAdminRetirerListeRouge() throws Exception {
        String pwd = lireLigne("Mot de passe admin : ");
        String id = lireLigne("ID du membre à retirer de la liste rouge : ");
        service.retirerDeListeRouge(id, pwd);
        System.out.println("Liste rouge retirée.");
    }

    // --- Saisie Person ---

    private Person saisirPersonneSansId() {
        String nom = lireLigne("Nom : ");
        String prenom = lireLigne("Prénom : ");

        System.out.println("Catégories : 1=PROFESSEUR, 2=AUXILIAIRE, 3=ETUDIANT");
        int c = lireEntier("Catégorie : ");
        Category cat = switch (c) {
            case 1 -> Category.PROFESSEUR;
            case 2 -> Category.AUXILIAIRE;
            case 3 -> Category.ETUDIANT;
            default -> Category.ETUDIANT;
        };

        String matricule = null;
        if (cat == Category.ETUDIANT) {
            matricule = lireLigne("Matricule : ");
        }

        String email = lireLigne("Email : ");
        String telephone = lireLigne("Téléphone : ");
        String domaine = lireLigne("Domaine d'activité : ");

        Person p = new Person();
        p.setNom(nom);
        p.setPrenom(prenom);
        p.setCategory(cat);
        p.setMatricule(matricule);
        p.setEmail(email);
        p.setTelephone(telephone);
        p.setDomaineActivite(domaine);
        p.setListeRouge(false);

        return p;
    }

    private Person saisirPersonneAvecId(String id) {
        Person p = saisirPersonneSansId();
        p.setId(id);
        return p;
    }

    // --- Utilitaires de saisie ---

    private int lireEntier(String message) {
        while (true) {
            try {
                System.out.print(message);
                String line = scanner.nextLine();
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un entier.");
            }
        }
    }

    private String lireLigne(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
}
