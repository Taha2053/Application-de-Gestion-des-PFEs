/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Etudiant;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author taha
 */
public class EtudiantDAOimpl implements EtudiantDAO {

    @Override
    public Etudiant get(int id) throws SQLException {
        Etudiant etudiant = null;

        String sql = "SELECT idetudiant,nom,prenom,email, classe FROM etudiant WHERE idetudiant= ? ";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idetudiant = rs.getInt("idetudiant");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String email = rs.getString("email");
                    String classe = rs.getString("classe");

                    etudiant = new Etudiant(idetudiant, nom, prenom, email, classe);
                }
            }
        }
        return etudiant;
    }

    @Override
    public List<Etudiant> getAll() throws SQLException {
        String sql = "SELECT idetudiant, nom, prenom, email, classe FROM etudiant";
        List<Etudiant> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("idetudiant");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                String classe = rs.getString("classe");
                list.add(new Etudiant(id, nom, prenom, email, classe));
            }
        }
        return list;
    }

    @Override
    public int insert(Etudiant etudiant) throws SQLException {
        String sql = "INSERT INTO etudiant (nom, prenom, email, classe) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, etudiant.getNom());
            ps.setString(2, etudiant.getPrenom());
            ps.setString(3, etudiant.getEmail());
            ps.setString(4, etudiant.getClasse());

            return ps.executeUpdate();
        }
    }

    @Override
    public int update(Etudiant etudiant) throws SQLException {
        String sql = "UPDATE etudiant SET nom = ?, prenom = ?, email = ?, classe = ? WHERE idetudiant = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, etudiant.getNom());
            ps.setString(2, etudiant.getPrenom());
            ps.setString(3, etudiant.getEmail());
            ps.setString(4, etudiant.getClasse());
            ps.setInt(5, etudiant.getIdetudiant());

            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(Etudiant etudiant) throws SQLException {
        String sql = "DELETE FROM etudiant WHERE idetudiant = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, etudiant.getIdetudiant());

            return ps.executeUpdate();
        }
    }

}