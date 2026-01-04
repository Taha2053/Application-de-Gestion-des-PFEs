/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.util.List;
import java.time.LocalDate;
/**
 *
 * @author taha
 */
public class Pfe {
    private int idpfe;
    private String titre;
    private String description;
    private String etat; 
    private LocalDate dateSoutenance;
    private Etudiant etudiant;
    private List<Encadreur> encadreur;

    public Pfe(String titre, String description, String etat,
               LocalDate dateSoutenance,
               Etudiant etudiant, List<Encadreur> encadreur) {
        this.titre = titre;
        this.description = description;
        this.etat = etat;
        this.dateSoutenance = dateSoutenance;
        this.etudiant = etudiant;
        this.encadreur = encadreur;
    }

    public Pfe(int idpfe, String titre, String description, String etat,
               LocalDate dateSoutenance,
               Etudiant etudiant, List<Encadreur> encadreur) {
        this.idpfe = idpfe;
        this.titre = titre;
        this.description = description;
        this.etat = etat;
        this.dateSoutenance = dateSoutenance;
        this.etudiant = etudiant;
        this.encadreur = encadreur;
    }

    public int getIdpfe() {
        return idpfe;
    }

    public void setIdpfe(int idpfe) {
        this.idpfe = idpfe;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public LocalDate getDateSoutenance() {
        return dateSoutenance;
    }

    public void setDateSoutenance(LocalDate dateSoutenance) {
        this.dateSoutenance = dateSoutenance;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public List<Encadreur> getEncadreur() {
        return encadreur;
    }

    public void setEncadreur(List<Encadreur> encadreur) {
        this.encadreur = encadreur;
    }

    @Override
    public String toString() {
        return "PFE{" +
                "idpfe=" + idpfe +
                ", titre='" + titre + '\'' +
                ", etat='" + etat + '\'' +
                ", dateSoutenance=" + dateSoutenance +
                '}';
    }
}
