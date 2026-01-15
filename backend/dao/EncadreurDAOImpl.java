/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import model.Encadreur;
import util.DBConnection;

/**
 *
 * @author taha
 */
public class EncadreurDAOImpl implements EncadreurDAO {

    @Override
    public Encadreur get(int id) throws SQLException {
        Encadreur encadreur = null;

        String sql = "SELECT idencadreur, nom, prenom,grade, email FROM encadreur WHERE idencadreur = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idencadreur = rs.getInt("idencadreur");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String grade = rs.getString("grade");
                    String email = rs.getString("email");

                    encadreur = new Encadreur(idencadreur, nom, prenom, grade, email);
                }
            }
        }

        return encadreur;
    }

    @Override
    public List<Encadreur> getAll() throws SQLException {
        String sql = "SELECT idencadreur, nom, prenom,grade, email FROM encadreur";

        List<Encadreur> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idencadreur = rs.getInt("idencadreur");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String grade = rs.getString("grade");
                String email = rs.getString("email");

                list.add(new Encadreur(idencadreur, nom, prenom, grade, email));
            }
        }

        return list;
    }

    @Override
    public int insert(Encadreur encadreur) throws SQLException {
        String sql = "INSERT INTO encadreur (nom, prenom,grade, email) VALUES (?, ?, ?,?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, encadreur.getNom());
            ps.setString(2, encadreur.getPrenom());
            ps.setString(3, encadreur.getGrade());
            ps.setString(4, encadreur.getEmail());

            return ps.executeUpdate();
        }
    }

    @Override
    public int update(Encadreur encadreur) throws SQLException {
        String sql = "UPDATE encadreur SET nom = ?, prenom = ?,grade = ?, email = ? WHERE idencadreur = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, encadreur.getNom());
            ps.setString(2, encadreur.getPrenom());
            ps.setString(3, encadreur.getGrade());
            ps.setString(4, encadreur.getEmail());
            ps.setInt(5, encadreur.getIdencadreur());

            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(Encadreur encadreur) throws SQLException {
        String sql = "DELETE FROM encadreur WHERE idencadreur = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, encadreur.getIdencadreur());

            return ps.executeUpdate();
        }
    }
}
