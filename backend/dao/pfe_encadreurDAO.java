/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.PfeEncadreur;
import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author taha
 */


public interface pfe_encadreurDAO extends DAO<PfeEncadreur> {
    // Optionally, you can add specific methods for querying by PFE or Encadreur
    List<PfeEncadreur> getByPfe(int idPfe) throws SQLException;
    List<PfeEncadreur> getByEncadreur(int idEncadreur) throws SQLException;
}
