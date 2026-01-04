/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.PfeEncadreur;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author taha
 */

public class pfe_encadreurDAOImpl implements pfe_encadreurDAO {

    @Override
    public PfeEncadreur get(int id) throws SQLException {
        PfeEncadreur pe = null;

        String sql = "SELECT idpfe, idencadreur FROM pfe_encadreur WHERE idpfe = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idPfe = rs.getInt("idpfe");
                    int idEnc = rs.getInt("idencadreur");
                    pe = new PfeEncadreur(idPfe, idEnc);
                }
            }
        }
        return pe;
    }

    @Override
    public List<PfeEncadreur> getAll() throws SQLException {
        String sql = "SELECT idpfe, idencadreur FROM pfe_encadreur";

        List<PfeEncadreur> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new PfeEncadreur(
                        rs.getInt("idpfe"),
                        rs.getInt("idencadreur")));
            }
        }
        return list;
    }

    @Override
    public int insert(PfeEncadreur pe) throws SQLException {
        String sql = "INSERT INTO pfe_encadreur (idpfe, idencadreur) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pe.getIdPfe());
            ps.setInt(2, pe.getIdEncadreur());

            return ps.executeUpdate();
        }
    }

    @Override
    public int update(PfeEncadreur pe) throws SQLException {
        String sql = "UPDATE pfe_encadreur SET idencadreur = ? WHERE idpfe = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pe.getIdEncadreur());
            ps.setInt(2, pe.getIdPfe());

            return ps.executeUpdate();
        }
    }

    @Override
    public int delete(PfeEncadreur pe) throws SQLException {
        String sql = "DELETE FROM pfe_encadreur WHERE idpfe = ? AND idencadreur = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pe.getIdPfe());
            ps.setInt(2, pe.getIdEncadreur());

            return ps.executeUpdate();
        }
    }

    @Override
    public List<PfeEncadreur> getByPfe(int idPfe) throws SQLException {
        String sql = "SELECT idpfe, idencadreur FROM pfe_encadreur WHERE idpfe = ?";

        List<PfeEncadreur> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPfe);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PfeEncadreur(
                            rs.getInt("idpfe"),
                            rs.getInt("idencadreur")));
                }
            }
        }
        return list;
    }

    @Override
    public List<PfeEncadreur> getByEncadreur(int idEncadreur) throws SQLException {
        String sql = "SELECT idpfe, idencadreur FROM pfe_encadreur WHERE idencadreur = ?";

        List<PfeEncadreur> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEncadreur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PfeEncadreur(
                            rs.getInt("idpfe"),
                            rs.getInt("idencadreur")));
                }
            }
        }
        return list;
    }
}
