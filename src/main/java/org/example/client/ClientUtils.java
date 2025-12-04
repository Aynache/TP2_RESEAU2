package org.example.client;


import org.example.common.Person;

import java.util.List;

public final class ClientUtils {

    private ClientUtils() { }

    public static void afficherPersonne(Person p) {
        if (p == null) {
            System.out.println("Aucun résultat.");
            return;
        }
        if (p.isListeRouge()) {
            System.out.printf("%s %s (sur la liste rouge)%n",
                    p.getPrenom(), p.getNom());
        } else {
            System.out.printf("ID: %s | %s %s | Catégorie: %s | Email: %s | Tel: %s | Domaine: %s | Liste rouge: %b%n",
                    p.getId(), p.getPrenom(), p.getNom(),
                    p.getCategory(), p.getEmail(),
                    p.getTelephone(), p.getDomaineActivite(),
                    p.isListeRouge());
        }
    }

    public static void afficherListePersonnes(List<Person> personnes) {
        if (personnes == null || personnes.isEmpty()) {
            System.out.println("Aucun résultat.");
            return;
        }
        for (Person p : personnes) {
            afficherPersonne(p);
        }
    }
}
