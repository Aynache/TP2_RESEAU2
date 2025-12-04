package org.example.server;


import org.example.common.AnnuaireService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServerMain {

    public static void main(String[] args) {
        try {
            // 1) Initialiser la base (création de la table si besoin)
            Database.init();

            // 2) Utiliser le repository SQLite au lieu de InMemory
            AnnuaireRepository repository = new SQLiteAnnuaireRepository();

            //DEMARRAGE SERVEUR RMI
            LocateRegistry.createRegistry(ServerConfig.RMI_PORT);

            AnnuaireService service = new AnnuaireServiceImpl(repository);

            String url = "rmi://localhost:" + ServerConfig.RMI_PORT + "/" + ServerConfig.SERVICE_NAME;
            Naming.rebind(url, service);

            System.out.println("Serveur Annuaire RMI démarré sur " + url);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
