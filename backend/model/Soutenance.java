/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.time.LocalDate;

/**
 *
 * @author taha
 */
public class Soutenance {
    private int idsoutenance;
    private LocalDate date;
    private String salle;
    private double note;
    private Pfe pfe;
    
    public Soutenance(LocalDate date,String salle,double note, Pfe pfe){
        this.date = date;
        this.salle = salle;
        this.note = note;
        this.pfe = pfe;
    }

    public Soutenance(int idsoutenance, LocalDate date,String salle,double note, Pfe pfe){
        this.idsoutenance = idsoutenance;
        this.date = date;
        this.salle = salle;
        this.note = note;
        this.pfe = pfe;
    }
    
    public int getIdsoutenance(){
        return idsoutenance;
    }
    public void setIdsoutenance( int idsoutenance){
        this.idsoutenance = idsoutenance;
    }
    
    public LocalDate getDate(){
        return date;
    }
    public void setDate(LocalDate date){
        this.date= date;
    }
    
    public String getSalle(){
        return salle;
    }
    public void setSalle(String salle){
        this.salle= salle;
    } 
    
    public double getNote(){
        return note;
    }
    public void setNote(double note){
        this.note= note;
    }
    
    public Pfe getPfe(){
        return pfe;
    }
    
    public void setPfe(Pfe pfe){
        this.pfe = pfe;
    }
    
    @Override
    public String toString(){
        return "soutenance : LocalDate est : " + date + "salle est : "+ salle ;
    }
}
