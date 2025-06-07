import java.io.*;
import java.sql.*;
import java.util.*;

public class GestionEtudiants {

    private List<Etudiant> listeEtudiants = new ArrayList<>();
    private final String FICHIER = "etudiants.txt";

    public GestionEtudiants() {
        creerTableSiAbsente();
        chargerDepuisBase(); // charge depuis la BDD
    }

    public void ajouterEtudiant(Etudiant e) {
        listeEtudiants.add(e);
        insererDansBase(e);
    }

    public void afficherEtudiants() {
        for (Etudiant e : listeEtudiants) {
            System.out.println(e);
        }
    }

    public void sauvegarderDansFichier() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHIER))) {
            for (Etudiant e : listeEtudiants) {
                pw.println(e.getNom() + "," + e.getPrenom() + "," + e.getNote());
            }
            System.out.println("Liste sauvegardée !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void chargerDepuisFichier() {
        listeEtudiants.clear();
        File file = new File(FICHIER);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 3) {
                    Etudiant e = new Etudiant(parts[0], parts[1], Double.parseDouble(parts[2]));
                    listeEtudiants.add(e);
                    insererDansBase(e); // insère dans SQLite aussi
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public double calculerMoyenne() {
        if (listeEtudiants.isEmpty()) return 0;
        double somme = 0;
        for (Etudiant e : listeEtudiants) {
            somme += e.getNote();
        }
        return somme / listeEtudiants.size();
    }

    public List<Etudiant> getListeEtudiants() {
        return listeEtudiants;
    }

    // ------------------ SQLite ----------------------

    private void creerTableSiAbsente() {
        String sql = "CREATE TABLE IF NOT EXISTS etudiants (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "nom TEXT NOT NULL," +
                     "prenom TEXT NOT NULL," +
                     "note REAL NOT NULL)";
        try (Connection conn = ConnexionSQLite.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insererDansBase(Etudiant e) {
        String sql = "INSERT INTO etudiants(nom, prenom, note) VALUES (?, ?, ?)";
        try (Connection conn = ConnexionSQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, e.getNom());
            pstmt.setString(2, e.getPrenom());
            pstmt.setDouble(3, e.getNote());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void chargerDepuisBase() {
        listeEtudiants.clear();
        String sql = "SELECT nom, prenom, note FROM etudiants";
        try (Connection conn = ConnexionSQLite.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Etudiant e = new Etudiant(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getDouble("note")
                );
                listeEtudiants.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void supprimerEtudiant(Etudiant e) {
    listeEtudiants.remove(e);
    try (Connection conn = ConnexionSQLite.getConnection();
         PreparedStatement pstmt = conn.prepareStatement("DELETE FROM etudiants WHERE nom = ? AND prenom = ? AND note = ?")) {
        pstmt.setString(1, e.getNom());
        pstmt.setString(2, e.getPrenom());
        pstmt.setDouble(3, e.getNote());
        pstmt.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

}
