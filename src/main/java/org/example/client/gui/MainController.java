package org.example.client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;



import javafx.scene.layout.VBox;
import org.example.common.*;

import java.rmi.Naming;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MainController {

    @FXML private TextField searchField;
    @FXML private TableView<Person> tableView;

    @FXML private TableColumn<Person, String> colId;
    @FXML private TableColumn<Person, String> colPrenom;
    @FXML private TableColumn<Person, String> colNom;
    @FXML private TableColumn<Person, Category> colCategorie;
    @FXML private TableColumn<Person, String> colMatricule;
    @FXML private TableColumn<Person, String> colEmail;
    @FXML private TableColumn<Person, String> colDomaine;
    @FXML private TableColumn<Person, Boolean> colListeRouge;

    @FXML private ComboBox<String> domainCombo;
    @FXML private ComboBox<Category> categoryCombo;

    @FXML private Button adminButton;
    @FXML private VBox adminPane;

    @FXML private TextField idField, prenomField, nomField, matriculeField, emailField, telephoneField, domaineField;
    @FXML private ComboBox<Category> adminCategoryCombo;


    @FXML private Label statusLabel;

    private AnnuaireService service;
    private final ObservableList<Person> data = FXCollections.observableArrayList();

    private boolean isAdmin = false;
    private String adminPassword = null;


    @FXML
    public void initialize() {
        try {
            service = (AnnuaireService) Naming.lookup("rmi://localhost:1099/AnnuaireService");
            statusLabel.setText("Connecté au serveur RMI");
        } catch (Exception e) {
            statusLabel.setText("Erreur connexion RMI");
            e.printStackTrace();
        }

        // ComboBox catégories
        categoryCombo.setItems(FXCollections.observableArrayList(Category.values()));
        categoryCombo.getSelectionModel().select(Category.PROFESSEUR); // valeur par défaut


        // ComboBox catégories
        categoryCombo.setItems(FXCollections.observableArrayList(Category.values()));
        categoryCombo.getSelectionModel().select(Category.PROFESSEUR);

        // Remplir les domaines à partir des professeurs existants
        loadDomains();



        // Colonnes
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colMatricule.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMatricule()));

        colPrenom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrenom()));
        colNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNom()));
        colCategorie.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCategory()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colDomaine.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDomaineActivite()));
        colListeRouge.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isListeRouge()));

        tableView.setItems(data);

        // Admin panel hidden by default
        adminPane.setVisible(false);
        adminPane.setManaged(false);

        adminCategoryCombo.setItems(FXCollections.observableArrayList(Category.values()));
        adminCategoryCombo.getSelectionModel().select(Category.PROFESSEUR);

// Remplir le formulaire quand on clique une ligne
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, p) -> {
            if (p == null) return;
            idField.setText(p.getId());
            prenomField.setText(p.getPrenom());
            nomField.setText(p.getNom());
            adminCategoryCombo.getSelectionModel().select(p.getCategory());
            matriculeField.setText(p.getMatricule());
            emailField.setText(p.getEmail());
            telephoneField.setText(p.getTelephone());
            domaineField.setText(p.getDomaineActivite());
        });

    }

    @FXML
    private void onSearch() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            statusLabel.setText("Entrez un nom, prénom ou ID.");
            return;
        }

        try {
            // 1️⃣ Tentative de recherche par ID (exacte)
            Person byId = service.rechercherParId(query);
            if (byId != null) {
                data.setAll(byId);
                statusLabel.setText("Résultat pour ID : " + query);
                return;
            }

            // 2️⃣ Sinon recherche par nom / prénom (partielle)
            List<Person> result = service.rechercherParNomOuPrenom(query);
            data.setAll(result);

            statusLabel.setText("Résultats pour : " + query);

        } catch (Exception e) {
            showError(e);
        }
    }


    @FXML
    private void onListProfessors() {
        try {
            List<Person> result = service.listerParCategorie(Category.PROFESSEUR);
            data.setAll(result);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Erreur serveur");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void onListByCategory() {
        Category selected = categoryCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Choisis une catégorie.");
            return;
        }

        try {
            List<Person> result = service.listerParCategorie(selected);
            data.setAll(result);
            statusLabel.setText("Affichage : " + selected);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void loadDomains() {
        try {
            // On récupère tous les professeurs
            List<Person> profs = service.listerParCategorie(Category.PROFESSEUR);

            // Extraire les domaines sans doublons
            Set<String> domains = profs.stream()
                    .map(Person::getDomaineActivite)
                    .filter(d -> d != null && !d.isBlank())
                    .collect(Collectors.toCollection(TreeSet::new)); // tri + unique

            domainCombo.setItems(FXCollections.observableArrayList(domains));

            if (!domains.isEmpty()) {
                domainCombo.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    @FXML
    private void onListProfessorsByDomain() {
        String domain = domainCombo.getSelectionModel().getSelectedItem();

        if (domain == null) {
            statusLabel.setText("Choisis un domaine.");
            return;
        }

        try {
            List<Person> result = service.listerProfesseursParDomaine(domain);
            data.setAll(result);
            statusLabel.setText("Professeurs – Domaine : " + domain);
        } catch (Exception e) {
            showError(e);
        }
    }

    @FXML
    private void onAdmin() {
        if (isAdmin) {
            statusLabel.setText("Déjà en mode Admin.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Accès Admin");

        ButtonType loginBtn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginBtn, ButtonType.CANCEL);

        PasswordField pf = new PasswordField();
        pf.setPromptText("Mot de passe admin");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: red;");

        VBox box = new VBox(10, new Label("Entrez le mot de passe :"), pf, msg);
        box.setStyle("-fx-padding: 10;");
        dialog.getDialogPane().setContent(box);

        // On empêche la fermeture si mdp incorrect
        Node okButton = dialog.getDialogPane().lookupButton(loginBtn);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String pwd = pf.getText();

            try {
                boolean ok = service.verifierAdmin(pwd);
                if (!ok) {
                    msg.setText("Mot de passe incorrect.");
                    event.consume(); // ne ferme pas
                    return;
                }

                // OK -> passer en admin
                isAdmin = true;
                adminPassword = pwd;

                adminPane.setVisible(true);
                adminPane.setManaged(true);

                statusLabel.setText("Mode Admin activé.");
            } catch (Exception e) {
                msg.setText("Erreur serveur.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    @FXML
    private void onAdminAdd() {
        ensureAdmin();
        try {
            Person p = buildPersonFromForm(false);
            service.ajouterMembre(p, adminPassword);
            statusLabel.setText("Ajout OK.");
            refreshAfterAdminAction();
        } catch (Exception e) { showError(e); }
    }

    @FXML
    private void onAdminUpdate() {
        ensureAdmin();

        if (tableView.getSelectionModel().getSelectedItem() == null) {
            statusLabel.setText("Sélectionne un membre dans le tableau avant de modifier.");
            return;
        }

        try {
            Person p = buildPersonFromForm(true);
            service.modifierMembre(p, adminPassword);
            statusLabel.setText("Modification OK.");
            refreshAfterAdminAction();
        } catch (Exception e) {
            showError(e);
        }
    }


    @FXML
    private void onAdminDelete() {
        ensureAdmin();
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) { statusLabel.setText("ID requis pour supprimer."); return; }
            service.supprimerMembre(id, adminPassword);
            statusLabel.setText("Suppression OK.");
            refreshAfterAdminAction();
        } catch (Exception e) { showError(e); }
    }

    @FXML
    private void onAdminRedOn() {
        ensureAdmin();
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) { statusLabel.setText("ID requis."); return; }
            service.mettreSurListeRouge(id, adminPassword);
            statusLabel.setText("Liste rouge activée.");
            refreshAfterAdminAction();
        } catch (Exception e) { showError(e); }
    }

    @FXML
    private void onAdminRedOff() {
        ensureAdmin();
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) { statusLabel.setText("ID requis."); return; }
            service.retirerDeListeRouge(id, adminPassword);
            statusLabel.setText("Liste rouge retirée.");
            refreshAfterAdminAction();
        } catch (Exception e) { showError(e); }
    }

    private void ensureAdmin() {
        if (!isAdmin || adminPassword == null) {
            throw new IllegalStateException("Mode Admin non activé.");
        }
    }

    private Person buildPersonFromForm(boolean requireId) {
        String id = safeText(idField);
        if (requireId && id.isEmpty()) {
            throw new IllegalArgumentException("ID requis.");
        }

        Person p = new Person();
        if (!id.isEmpty()) p.setId(id);

        p.setPrenom(safeText(prenomField));
        p.setNom(safeText(nomField));

        Category cat = adminCategoryCombo.getSelectionModel().getSelectedItem();
        if (cat == null) throw new IllegalArgumentException("Catégorie requise.");
        p.setCategory(cat);

        p.setMatricule(emptyToNull(safeText(matriculeField)));
        p.setEmail(emptyToNull(safeText(emailField)));
        p.setTelephone(emptyToNull(safeText(telephoneField)));
        p.setDomaineActivite(emptyToNull(safeText(domaineField)));

        return p;
    }


    private void refreshAfterAdminAction() {
        // Recharger la table selon le contexte actuel (simple: re-lister catégorie choisie)
        onListByCategory();

        // Recharger domaines (si ajouts/modifs ont changé domaines)
        loadDomains();
    }

    private String safeText(TextField tf) {
        if (tf == null) return "";
        String v = tf.getText();
        return v == null ? "" : v.trim();
    }

    private String emptyToNull(String s) {
        if (s == null) return null;
        String v = s.trim();
        return v.isEmpty() ? null : v;
    }


}
