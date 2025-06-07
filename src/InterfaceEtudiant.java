import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class InterfaceEtudiant extends JFrame {

    private JTextField txtNom, txtPrenom, txtNote;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblMoyenne;

    private GestionEtudiants gestion = new GestionEtudiants();

    public InterfaceEtudiant() {
        initUI();
        chargerTable();
    }

    private void initUI() {
        setTitle("Gestion Avancée des Étudiants");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Formulaire
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Formulaire"));

        txtNom = new JTextField();
        txtPrenom = new JTextField();
        txtNote = new JTextField();

        panelForm.add(new JLabel("Nom :"));
        panelForm.add(txtNom);
        panelForm.add(new JLabel("Prénom :"));
        panelForm.add(txtPrenom);
        panelForm.add(new JLabel("Note :"));
        panelForm.add(txtNote);

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");

        panelForm.add(btnAjouter);
        panelForm.add(btnModifier);

        add(panelForm, BorderLayout.NORTH);

        // Tableau
        tableModel = new DefaultTableModel(new Object[]{"Nom", "Prénom", "Note"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des étudiants"));
        add(scrollPane, BorderLayout.CENTER);

        // Bas
        JPanel panelBas = new JPanel(new FlowLayout());

        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnRecharger = new JButton("Recharger");

        lblMoyenne = new JLabel("Moyenne : -");

        panelBas.add(btnSupprimer);
        panelBas.add(btnSauvegarder);
        panelBas.add(btnRecharger);
        panelBas.add(lblMoyenne);

        add(panelBas, BorderLayout.SOUTH);

        // Actions
        btnAjouter.addActionListener(e -> ajouterEtudiant());
        btnModifier.addActionListener(e -> modifierEtudiant());
        btnSupprimer.addActionListener(e -> supprimerEtudiant());
        btnSauvegarder.addActionListener(e -> sauvegarder());
        btnRecharger.addActionListener(e -> chargerTable());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtNom.setText(tableModel.getValueAt(row, 0).toString());
                    txtPrenom.setText(tableModel.getValueAt(row, 1).toString());
                    txtNote.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });
    }

    private void ajouterEtudiant() {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String noteStr = txtNote.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || noteStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.");
            return;
        }

        try {
            double note = Double.parseDouble(noteStr);
            Etudiant e = new Etudiant(nom, prenom, note);
            gestion.ajouterEtudiant(e);
            chargerTable();
            viderChamps();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Note invalide !");
        }
    }

    private void modifierEtudiant() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une ligne à modifier.");
            return;
        }

        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String noteStr = txtNote.getText().trim();

        try {
            double note = Double.parseDouble(noteStr);

            // Supprime l'ancien étudiant
            Etudiant ancien = gestion.getListeEtudiants().get(row);
            gestion.supprimerEtudiant(ancien);

            // Ajoute le modifié
            Etudiant nouveau = new Etudiant(nom, prenom, note);
            gestion.ajouterEtudiant(nouveau);

            chargerTable();
            viderChamps();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Note invalide !");
        }
    }

    private void supprimerEtudiant() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une ligne à supprimer.");
            return;
        }

        Etudiant e = gestion.getListeEtudiants().get(row);
        gestion.supprimerEtudiant(e);

        chargerTable();
        viderChamps();
    }

    private void sauvegarder() {
        gestion.sauvegarderDansFichier();
        JOptionPane.showMessageDialog(this, "Sauvegarde dans le fichier réussie !");
    }

    private void chargerTable() {
        gestion.chargerDepuisBase();
        List<Etudiant> liste = gestion.getListeEtudiants();
        tableModel.setRowCount(0); // vider
        for (Etudiant e : liste) {
            tableModel.addRow(new Object[]{e.getNom(), e.getPrenom(), e.getNote()});
        }
        lblMoyenne.setText("Moyenne : " + String.format("%.2f", gestion.calculerMoyenne()));
    }

    private void viderChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtNote.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InterfaceEtudiant().setVisible(true);
        });
    }
}
