/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author taha
 */
public class Etudiant {
    private int idetudiant;
    private String nom;
    private String prenom;
    private String email;
    private String classe;
    
    public Etudiant(String nom, String prenom, String email, String classe) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.classe = classe;
    }

    public Etudiant(int idetudiant, String nom, String prenom, String email, String classe) {
        this.idetudiant = idetudiant;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.classe = classe;
    }
    
    public int getIdetudiant(){
        return idetudiant;
    }
    public void setIdetudiant( int idetudiant){
        this.idetudiant = idetudiant;
    }
    
    public String getNom(){
        return nom;
    }
    public void setNom(String nom){
        this.nom= nom;
    }
    
    public String getPrenom(){
        return prenom;
    }
    public void setPrenom(String prenom){
        this.prenom= prenom;
    } 
    
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email= email;
    }
    
    public String getClasse(){
        return classe;
    }
    public void setClasse(String classe){
        this.classe= classe;
    }
    
    @Override
    public String toString(){
        return "Etudiant{id=" + idetudiant + ", nom='" + nom + "', prenom='" + prenom + "'}";
    }
}
