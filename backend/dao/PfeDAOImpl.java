/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import model.Pfe;
import model.PfeEncadreur;
import util.DBConnection;

/**
 *
 * @author taha
 */
public class PfeDAOImpl implements PfeDAO {

    @Override
    public Pfe get(int id) throws SQLException {
        Pfe pfe = null;

        String sql = "SELECT idpfe, titre, description, etat, dateSoutenance, idetudiant FROM pfe WHERE idpfe = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idpfe = rs.getInt("idpfe");
                    int idetudiant = rs.getInt("idetudiant");

                    // load etudiant if present
                    model.Etudiant etudiant = null;
                    if (!rs.wasNull()) {
                        dao.EtudiantDAO etudiantDAO = new dao.EtudiantDAOimpl();
                        etudiant = etudiantDAO.get(idetudiant);
                    }

                    // load encadreurs linked to this pfe
                    java.util.List<model.Encadreur> encadreurs = new java.util.ArrayList<>();
                    dao.pfe_encadreurDAO peDao = new dao.pfe_encadreurDAOImpl();
                    java.util.List<model.PfeEncadreur> peList = peDao.getByPfe(idpfe);
                    dao.EncadreurDAO encDAO = new dao.EncadreurDAOImpl();
                    for (model.PfeEncadreur pe : peList) {
                        try {
                            model.Encadreur enc = encDAO.get(pe.getIdEncadreur());
                            if (enc != null)
                                encadreurs.add(enc);
                        } catch (SQLException ex) {
                            // ignore indiv failure
                        }
                    }

                    java.sql.Date d = rs.getDate("dateSoutenance");
                    java.time.LocalDate dt = d == null ? null : d.toLocalDate();

                    pfe = new Pfe(
                            idpfe,
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getString("etat"),
                            dt,
                            etudiant,
                            encadreurs);
                }
            }
        }

        return pfe;
    }

    @Override
    public List<Pfe> getAll() throws SQLException {
        String sql = "SELECT idpfe, titre, description, etat, dateSoutenance, idetudiant FROM pfe";

        List<Pfe> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idpfe = rs.getInt("idpfe");
                int idetudiant = rs.getInt("idetudiant");

                model.Etudiant etudiant = null;
                try {
                    etudiant = new dao.EtudiantDAOimpl().get(idetudiant);
                } catch (SQLException ex) {
                    // ignore
                }

                java.util.List<model.Encadreur> encadreurs = new java.util.ArrayList<>();
                try {
                    dao.pfe_encadreurDAO peDao = new dao.pfe_encadreurDAOImpl();
                    java.util.List<model.PfeEncadreur> peList = peDao.getByPfe(idpfe);
                    dao.EncadreurDAO encDAO = new dao.EncadreurDAOImpl();
                    for (model.PfeEncadreur pe : peList) {
                        model.Encadreur enc = encDAO.get(pe.getIdEncadreur());
                        if (enc != null)
                            encadreurs.add(enc);
                    }
                } catch (SQLException ex) {
                    // ignore
                }

                java.sql.Date d = rs.getDate("dateSoutenance");
                java.time.LocalDate dt = d == null ? null : d.toLocalDate();

                list.add(new Pfe(
                        idpfe,
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("etat"),
                        dt,
                        etudiant,
                        encadreurs));
            }
        }

        return list;
    }

    private int getColumnSize(String table, String column) {
        try (Connection con = DBConnection.getConnection()) {
            // first try standard metadata
            try (java.sql.ResultSet rs = con.getMetaData().getColumns(con.getCatalog(), null, table, column)) {
                if (rs.next()) {
                    int size = rs.getInt("COLUMN_SIZE");
                    if (size > 0)
                        return size;
                }
            }
            // fallback: query information_schema
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT CHARACTER_MAXIMUM_LENGTH FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?")) {
                ps.setString(1, table);
                ps.setString(2, column);
                try (java.sql.ResultSet rs2 = ps.executeQuery()) {
                    if (rs2.next())
                        return rs2.getInt(1);
                }
            }
        } catch (SQLException e) {
            // ignore - return -1
        }
        return -1;
    }

    @Override
    public int insert(Pfe pfe) throws SQLException {
        if (pfe.getEtudiant() == null) {
            throw new SQLException("PFE must have an Etudiant");
        }

        // validate etat length against DB
        int max = getColumnSize("pfe", "etat");
        if (max > 0 && pfe.getEtat() != null && pfe.getEtat().length() > max) {
            throw new SQLException("Le champ 'etat' est limité à " + max + " caractères dans la base de données.");
        }

        String sql = "INSERT INTO pfe (titre, description, etat, dateSoutenance, idetudiant) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pfe.getTitre());
            ps.setString(2, pfe.getDescription());
            ps.setString(3, pfe.getEtat());
            ps.setDate(4, Date.valueOf(pfe.getDateSoutenance()));
            ps.setInt(5, pfe.getEtudiant().getIdetudiant());

            return ps.executeUpdate();
        }
    }

    @Override
    public int update(Pfe pfe) throws SQLException {
        String sql = "UPDATE pfe SET titre = ?, description = ?, etat = ?, dateSoutenance = ?, idetudiant = ? WHERE idpfe = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pfe.getTitre());
            ps.setString(2, pfe.getDescription());
            ps.setString(3, pfe.getEtat());
            ps.setDate(4, Date.valueOf(pfe.getDateSoutenance()));
            if (pfe.getEtudiant() != null)
                ps.setInt(5, pfe.getEtudiant().getIdetudiant());
            else
                ps.setNull(5, java.sql.Types.INTEGER);
            ps.setInt(6, pfe.getIdpfe());

            return ps.executeUpdate();
        }
    }

    @Override
    public Integer findPfeIdByEtudiant(int idetudiant) throws SQLException {
        String sql = "SELECT idpfe FROM pfe WHERE idetudiant = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idetudiant);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        }
        return null;
    }

    @Override
    public int delete(Pfe pfe) throws SQLException {
        String sql = "DELETE FROM pfe WHERE idpfe = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pfe.getIdpfe());

            return ps.executeUpdate();
        }
    }
}
