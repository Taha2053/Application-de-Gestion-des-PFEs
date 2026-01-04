/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.Pfe;
import java.sql.SQLException;

/**
 *
 * @author taha
 */
public interface PfeDAO extends DAO<Pfe> {
    // return the idpfe of a PFE assigned to the given student, or null if none
    Integer findPfeIdByEtudiant(int idetudiant) throws SQLException;
}
