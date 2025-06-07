public class Etudiant {
    private String nom;
    private String prenom;
    private double note;

    public Etudiant(String nom, String prenom, double note) {
        this.nom = nom;
        this.prenom = prenom;
        this.note = note;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public double getNote() {
        return note;
    }

    public String toString() {
        return nom + " " + prenom + " - Note : " + note;
    }
}
