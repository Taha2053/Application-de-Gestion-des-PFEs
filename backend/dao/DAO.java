/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author taha
 */
public interface DAO<T> {
    T get(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    int insert (T t) throws SQLException;
    int update (T t) throws SQLException;
    int delete (T t) throws SQLException;
    
}
