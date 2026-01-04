/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.Soutenance;
import java.sql.SQLException;

/**
 *
 * @author taha
 */
public interface SoutenanceDAO extends DAO<Soutenance> {
    // return idsoutenance for a given pfe, or null if none
    Integer findSoutenanceIdByPfe(int idpfe) throws SQLException;
}
