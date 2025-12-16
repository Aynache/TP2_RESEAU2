# 📚 Annuaire Universitaire – TP2 INF1010

Application client–serveur en **Java** permettant de gérer un annuaire universitaire
(professeurs, étudiants, auxiliaires) avec **RMI**, **SQLite** et une interface graphique **JavaFX**.

---

## 🧠 Objectifs du projet

- Mettre en œuvre une architecture **client–serveur** en Java (RMI)
- Implémenter une **persistance des données** avec SQLite (JDBC)
- Développer une **interface graphique JavaFX**
- Gérer des **droits d’accès administrateur**
- Appliquer une séparation claire des responsabilités (Client / Service / Données)


### Composants
- **Client JavaFX**
    - Interface utilisateur
    - Appels distants RMI
- **Serveur RMI**
    - Logique métier
    - Validation administrateur
    - Accès à la base de données
- **SQLite**
    - Stockage persistant des membres

---

## 🗂️ Structure du projet

src/
├─ main/
│ ├─ java/
│ │ └─ org/example/
│ │ ├─ common/ # Interfaces RMI, modèles (Person, Category)
│ │ ├─ server/ # Serveur RMI, SQLite, repository
│ │ └─ client/gui/ # Interface JavaFX
│ └─ resources/
│ └─ main-view.fxml # Vue JavaFX
└─ test/


---

## 🧾 Modèle de données

### Table `person` (SQLite)

| Champ             | Type    | Description                                    |
|------------------|---------|------------------------------------------------|
| id               | TEXT    | Identifiant unique                             |
| nom              | TEXT    | Nom                                            |
| prenom           | TEXT    | Prénom                                         |
| category         | TEXT    | PROFESSEUR / ETUDIANT / AUXILIAIRE             |
| matricule        | TEXT    | Matricule (seulement étudiant)                 |
| email            | TEXT    | Adresse courriel                               |
| telephone        | TEXT    | Numéro de téléphone                            |
| domaine_activite | TEXT    | Domaine d’activité (professeurs)               |
| liste_rouge      | BOOlEAN | TRUE = sur la liste / FALSE = pas sur la liste |

---

## 🖥️ Fonctionnalités

### 👤 Utilisateur
- Recherche par **nom, prénom ou ID**
- Lister les membres par **catégorie**
- Lister les **professeurs par domaine**
- Affichage des résultats dans un tableau JavaFX

### 🔐 Administrateur
- Accès sécurisé par **mot de passe**
- Ajouter / modifier / supprimer un membre (CRUD)
- Mettre / retirer un membre de la **liste rouge**
- Interface admin intégrée à la fenêtre principale

---

## 🔑 Accès administrateur

- Bouton **Admin** dans l’interface
- Fenêtre popup demandant le mot de passe
- Mot de passe incorrect → message d’erreur
- Mot de passe correct → activation du **mode admin**

> La validation du mot de passe est effectuée côté serveur via RMI.

---

## 🚀 Lancement du projet

### 1️⃣ Prérequis
- Java **21**
- Maven **3.8+**
- IntelliJ IDEA (recommandé)

---

### 2️⃣ Démarrer le serveur RMI

```bash
mvn exec:java -Dexec.mainClass="org.example.server.ServerMain"
```

### 3️⃣ Démarrer le client JavaFX

```bash
mvn javafx:run
```


---
## 🛠️ Technologies utilisées
- **Java 21**
- **JavaFX** pour l’interface graphique
- **RMI** pour la communication client–serveur
- **SQLite** pour la base de données
- **Maven** pour la gestion des dépendances et la construction du projet
- **JDBC** pour l’accès à la base de données
- **FXML** pour la définition des vues JavaFX


---
## Remarques
-Le projet est entièrement fonctionnel sans dépendances externes
-La persistance est assurée par SQLite
-Le code est structuré pour une maintenance et une évolutivité aisées