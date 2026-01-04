package main;

import dao.*;
import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("===== DAO TEST START =====\n");

            testEtudiantDAO();
            testEncadreurDAO();
            testPfeDAO();
            testPfeEncadreurDAO();
            testSoutenanceDAO();

            System.out.println("\n===== DAO TEST END =====");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Etudiant globalEtudiant;
    private static List<Encadreur> globalEncadreurs = new ArrayList<>();
    private static Pfe globalPfe;

    /* ===================== ETUDIANT ===================== */
    private static void testEtudiantDAO() throws Exception {
        System.out.println("---- ETUDIANT DAO ----");
        EtudiantDAO dao = new EtudiantDAOimpl();

        Etudiant e = new Etudiant("Mohsen", "Hasan", "mohsen@gmail.com", "G30");
        dao.insert(e);

        globalEtudiant = dao.getAll().get(0);

        System.out.println("Etudiants in DB:");
        dao.getAll().forEach(System.out::println);
        System.out.println();
    }

    /* ===================== ENCADREUR ===================== */
    private static void testEncadreurDAO() throws Exception {
        System.out.println("---- ENCADREUR DAO ----");
        EncadreurDAO dao = new EncadreurDAOImpl();

        Encadreur enc1 = new Encadreur("Ali", "Ben Salah", "Professeur", "ali@gmail.com");
        Encadreur enc2 = new Encadreur("Sara", "Khalfallah", "Maître de Conférences", "sara@gmail.com");

        dao.insert(enc1);
        dao.insert(enc2);

        globalEncadreurs = dao.getAll();

        System.out.println("Encadreurs in DB:");
        globalEncadreurs.forEach(System.out::println);
        System.out.println();
    }

    /* ===================== PFE ===================== */
    private static void testPfeDAO() throws Exception {
        System.out.println("---- PFE DAO ----");
        PfeDAO dao = new PfeDAOImpl();

        LocalDate soutenanceDate = LocalDate.now().plusDays(7);
        Pfe pfe = new Pfe("AI Platform", "ML-based system", "En cours", soutenanceDate, globalEtudiant, globalEncadreurs);

        dao.insert(pfe);

        globalPfe = dao.getAll().get(0);

        System.out.println("PFEs in DB:");
        dao.getAll().forEach(System.out::println);
        System.out.println();
    }

    /* ===================== PFE_ENCADREUR ===================== */
    private static void testPfeEncadreurDAO() throws Exception {
        System.out.println("---- PFE_ENCADREUR DAO ----");
        pfe_encadreurDAO dao = new pfe_encadreurDAOImpl();

        // Insert associations between globalPfe and its encadreurs
        for (Encadreur enc : globalEncadreurs) {
            PfeEncadreur pe = new PfeEncadreur(globalPfe.getIdpfe(), enc.getIdencadreur());
            dao.insert(pe);
        }

        System.out.println("PFE_Encadreur associations in DB:");
        dao.getAll().forEach(System.out::println);
        System.out.println();
    }

    /* ===================== SOUTENANCE ===================== */
    private static void testSoutenanceDAO() throws Exception {
        System.out.println("---- SOUTENANCE DAO ----");
        SoutenanceDAO dao = new SoutenanceDAOImpl();

        LocalDate dateSoutenance = LocalDate.now().plusDays(7);
        Soutenance s = new Soutenance(dateSoutenance, "Salle A", 15.5, globalPfe);
        dao.insert(s);

        System.out.println("Soutenances in DB:");
        dao.getAll().forEach(System.out::println);
        System.out.println();
    }
}
