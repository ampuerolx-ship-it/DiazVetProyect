package database.dao;

import database.ConexionDB;
import java.sql.Connection;
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
}