package database.dao;

import database.ConexionDB;
import model.Cita;
import model.Paciente;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    // Formateador para guardar fechas en SQLite (Texto ISO 8601)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra una nueva cita en la base de datos.
     * @param cita Objeto Cita con los datos.
     * @param idMascota ID real de la mascota (necesario para la FK).
     * @return true si se guardó correctamente.
     */
    public boolean registrarCita(Cita cita, int idMascota) {
        String sql = "INSERT INTO citas (fecha_hora, motivo, dni_cliente, id_mascota) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cita.getFechaHora().format(FORMATTER));
            pstmt.setString(2, cita.getTipoCita()); // Usamos 'tipoCita' como 'motivo'
            pstmt.setString(3, cita.getPaciente().getDniDueno());
            pstmt.setInt(4, idMascota);
            pstmt.setString(5, cita.getIdCita());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar cita: " + e.getMessage());
            return false;
        }
    }

    public List<Cita> listarCitasFuturas() {
        List<Cita> lista = new ArrayList<>();
        // Hacemos JOIN con mascotas para poder reconstruir el objeto Paciente básico
        String sql = "SELECT c.id, c.fecha_hora, c.motivo, m.nombre as nombre_mascota, m.especie, c.dni_cliente " +
                     "FROM citas c " +
                     "JOIN mascotas m ON c.id_mascota = m.id " +
                     "WHERE datetime(c.fecha_hora) >= datetime('now', 'localtime') " +
                     "ORDER BY c.fecha_hora ASC";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // 1. Reconstruir Fecha
                String fechaStr = rs.getString("fecha_hora");
                // Manejo robusto de fechas (por si vienen con T o espacio)
                fechaStr = fechaStr.replace("T", " ");
                LocalDateTime fecha = LocalDateTime.parse(fechaStr, FORMATTER);
                
                // 2. Reconstruir Paciente (Objeto mínimo para visualización)
                // Nota: Ajusta los parámetros del constructor de Paciente según tu modelo real
                Paciente p = new Paciente(
                    rs.getString("nombre_mascota"),
                    rs.getString("especie"),
                    rs.getString("dni_cliente"), // Usamos dni como dueño temporalmente
                    "000", // Teléfono dummy si no lo trae la query
                    rs.getString("motivo"),
                    0 // Edad dummy
                );
                
                // 3. Crear Objeto Cita
                lista.add(new Cita(
                    String.valueOf(rs.getInt("id")), // ID Cita
                    p,
                    fecha,
                    rs.getString("motivo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}