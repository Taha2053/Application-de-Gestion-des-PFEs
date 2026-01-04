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
import model.Soutenance;
import util.DBConnection;
import java.time.LocalDate;

/**
 *
 * @author taha
 */
public class SoutenanceDAOImpl implements SoutenanceDAO {

    @Override
    public Soutenance get(int id) throws SQLException {
        Soutenance soutenance = null;

        String sql = "SELECT idsoutenance, date, salle, note, idpfe FROM soutenance WHERE idsoutenance = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idsoutenance = rs.getInt("idsoutenance");
                    java.sql.Date d = rs.getDate("date");
                    LocalDate date = d == null ? null : d.toLocalDate();
                    String salle = rs.getString("salle");
                    double note = rs.getDouble("note");
                    int idpfe = rs.getInt("idpfe");

                    model.Pfe pfe = null;
                    if (!rs.wasNull()) {
                        dao.PfeDAO pfeDAO = new dao.PfeDAOImpl();
                        pfe = pfeDAO.get(idpfe);
                    }

                    soutenance = new Soutenance(idsoutenance, date, salle, note, pfe);
                }
            }
        }

        return soutenance;
    }

    @Override
    public List<Soutenance> getAll() throws SQLException {
        String sql = "SELECT idsoutenance, date, salle, note, idpfe FROM soutenance";

        List<Soutenance> list = new ArrayList<>();

        dao.PfeDAO pfeDAO = new dao.PfeDAOImpl();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idsoutenance = rs.getInt("idsoutenance");
                java.sql.Date d = rs.getDate("date");
                LocalDate date = d == null ? null : d.toLocalDate();
                String salle = rs.getString("salle");
                double note = rs.getDouble("note");
                int idpfe = rs.getInt("idpfe");
                model.Pfe pfe = null;
                try {
                    pfe = pfeDAO.get(idpfe);
                } catch (SQLException ex) {
                }

                list.add(new Soutenance(idsoutenance, date, salle, note, pfe));
            }
        }

        return list;
    }

    @Override
    public int insert(Soutenance soutenance) throws SQLException {
        String sql = "INSERT INTO soutenance (date, salle, note, idpfe) VALUES (?, ?, ?,?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(soutenance.getDate()));
            ps.setString(2, soutenance.getSalle());
            ps.setDouble(3, soutenance.getNote());
            if (soutenance.getPfe() != null)
                ps.setInt(4, soutenance.getPfe().getIdpfe());
            else
                ps.setNull(4, java.sql.Types.INTEGER);

            return ps.executeUpdate();
        }
    }

    @Override
    public int update(Soutenance soutenance) throws SQLException {
        String sql = "UPDATE soutenance SET date = ?, salle = ?, note = ?, idpfe = ? WHERE idsoutenance = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(soutenance.getDate()));
            ps.setString(2, soutenance.getSalle());
            ps.setDouble(3, soutenance.getNote());
            if (soutenance.getPfe() != null)
                ps.setInt(4, soutenance.getPfe().getIdpfe());
            else
                ps.setNull(4, java.sql.Types.INTEGER);
            ps.setInt(5, soutenance.getIdsoutenance());

            return ps.executeUpdate();
        }
    }

    @Override
    public Integer findSoutenanceIdByPfe(int idpfe) throws SQLException {
        String sql = "SELECT idsoutenance FROM soutenance WHERE idpfe = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idpfe);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        }
        return null;
    }

    @Override
    public int delete(Soutenance soutenance) throws SQLException {
        String sql = "DELETE FROM soutenance WHERE idsoutenance = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, soutenance.getIdsoutenance());

            return ps.executeUpdate();
        }
    }
}
