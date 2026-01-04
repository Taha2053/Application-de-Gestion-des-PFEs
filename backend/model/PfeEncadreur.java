/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author taha
 */
public class PfeEncadreur {
    private int idPfe;
    private int idEncadreur;

    public PfeEncadreur(int idPfe, int idEncadreur) {
        this.idPfe = idPfe;
        this.idEncadreur = idEncadreur;
    }

    public int getIdPfe() {
        return idPfe;
    }

    public void setIdPfe(int idPfe) {
        this.idPfe = idPfe;
    }

    public int getIdEncadreur() {
        return idEncadreur;
    }

    public void setIdEncadreur(int idEncadreur) {
        this.idEncadreur = idEncadreur;
    }

    @Override
    public String toString() {
        return "PfeEncadreur{" +
               "idPfe=" + idPfe +
               ", idEncadreur=" + idEncadreur +
               '}';
    }
}
