package database.dao;

import database.ConexionDB;
import java.sql.Connection;
import model.HistorialClinico;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HistorialDAO {

    /**
     * Crea la entrada inicial del historial clínico para una nueva mascota.
     * @param idMascota El ID de la mascota recién registrada.
     * @return true si el registro fue exitoso.
     */
    public boolean crearHistorialInicial(int idMascota) {
        // Campos iniciales vacíos para el nuevo paciente
        String sql = "INSERT INTO historiales (id_mascota, vacunas_aplicadas, vacunas_pendientes, ultima_desparasitacion, ultima_visita, registro_vacunas_ruta) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMascota);
            pstmt.setString(2, "Ninguna");
            pstmt.setString(3, "Pendientes: Revisiones de cachorro");
            pstmt.setString(4, "N/A"); // No aplica
            pstmt.setString(5, "N/A"); // No aplica
            pstmt.setString(6, null); // Ruta de registro vacunas
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear historial inicial para Mascota ID: " + idMascota);
            e.printStackTrace();
            return false;
        }
    }
    // --- R E A D por Mascota ---
    public HistorialClinico obtenerPorIdMascota(int idMascota) {
        // Obtenemos el registro de historial y el nombre de la mascota en un JOIN
        String sql = "SELECT h.*, m.nombre FROM historiales h JOIN mascotas m ON h.id_mascota = m.id WHERE h.id_mascota = ?";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMascota);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new HistorialClinico(
                    rs.getInt("id"),
                    rs.getInt("id_mascota"),
                    rs.getString("nombre"), // nombreMascota
                    rs.getString("vacunas_aplicadas"),
                    rs.getString("vacunas_pendientes"),
                    rs.getString("ultima_desparasitacion"),
                    rs.getString("ultima_visita"),
                    rs.getString("registro_vacunas_ruta")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial: " + e.getMessage());
        }
        return null;
    }

    // --- U P S E R T (Actualizar o Insertar) ---
    public boolean guardar(HistorialClinico historial) {
        if (historial.getId() > 0) {
            return actualizar(historial);
        } else {
            return insertar(historial);
        }
    }
    
    // --- C R E A T E ---
    private boolean insertar(HistorialClinico historial) {
        String sql = "INSERT INTO historiales (id_mascota, vacunas_aplicadas, vacunas_pendientes, ultima_desparasitacion, ultima_visita, registro_vacunas_ruta) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, historial.getIdMascota());
            pstmt.setString(2, historial.getVacunasAplicadas());
            pstmt.setString(3, historial.getVacunasPendientes());
            pstmt.setString(4, historial.getUltimaDesparasitacion());
            pstmt.setString(5, historial.getUltimaVisita());
            pstmt.setString(6, historial.getRegistroVacunasRuta());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar historial: " + e.getMessage());
            return false;
        }
    }

    // --- U P D A T E ---
    private boolean actualizar(HistorialClinico historial) {
        String sql = "UPDATE historiales SET vacunas_aplicadas = ?, vacunas_pendientes = ?, ultima_desparasitacion = ?, ultima_visita = ?, registro_vacunas_ruta = ? WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, historial.getVacunasAplicadas());
            pstmt.setString(2, historial.getVacunasPendientes());
            pstmt.setString(3, historial.getUltimaDesparasitacion());
            pstmt.setString(4, historial.getUltimaVisita());
            pstmt.setString(5, historial.getRegistroVacunasRuta());
            pstmt.setInt(6, historial.getId()); // WHERE clause
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar historial: " + e.getMessage());
            return false;
        }
    }

    // --- D E L E T E (Opcional, pero implementado por la necesidad de CRUD) ---
    public boolean eliminar(int idHistorial) {
        String sql = "DELETE FROM historiales WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idHistorial);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar historial: " + e.getMessage());
            return false;
        }
    }
}