package database.dao;

import database.ConexionDB;
import model.Cita;
import model.Mascotas; // Usamos el modelo Mascota para obtener la lista de pacientes
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CitaDAO {

    // Formato de fecha y hora para la BD SQLite
    private static final DateTimeFormatter DB_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00");
    private static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // --- C R E A T E ---
    public boolean insertar(Cita cita) {
        // Asegúrate que tu tabla 'citas' incluya la columna 'nivel_prioridad'
        String sql = "INSERT INTO citas (fecha_hora, motivo, dni_cliente, id_mascota, nivel_prioridad) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Reemplazamos espacio por 'T' para formato ISO de SQLite
            String fechaHora = cita.getFechaHora().format(DB_DATE_TIME_FORMATTER).replace(' ', 'T'); 
            
            pstmt.setString(1, fechaHora);
            pstmt.setString(2, cita.getMotivo());
            pstmt.setString(3, cita.getDniCliente());
            pstmt.setInt(4, cita.getIdMascota());
            pstmt.setInt(5, cita.getNivelPrioridad());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar cita: " + e.getMessage());
            return false;
        }
    }

    // --- R E A D A L L (para un día específico) ---
    public ObservableList<Cita> obtenerPorFecha(LocalDate fecha) {
        ObservableList<Cita> listaCitas = FXCollections.observableArrayList();
        String fechaStr = fecha.format(DB_DATE_FORMATTER);
        
        String sql = "SELECT c.*, m.nombre AS nombre_mascota, cl.nombres || ' ' || cl.apellidos AS nombre_cliente " +
                     "FROM citas c " +
                     "JOIN mascotas m ON c.id_mascota = m.id " +
                     "JOIN clientes cl ON c.dni_cliente = cl.dni " +
                     "WHERE date(c.fecha_hora) = date(?) ";
                     // Nota: El orden lo maneja la ColaPrioridad en el Controller.

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fechaStr);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Cita cita = new Cita(
                    rs.getInt("id"),
                    rs.getString("fecha_hora"),
                    rs.getString("motivo"),
                    rs.getString("dni_cliente"),
                    rs.getInt("id_mascota"),
                    rs.getString("nombre_mascota"),
                    rs.getString("nombre_cliente"),
                    rs.getInt("nivel_prioridad")
                );
                listaCitas.add(cita);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener citas por fecha: " + e.getMessage());
        }
        return listaCitas;
    }
    
    // --- U P D A T E ---
    public boolean actualizar(Cita cita) {
        String sql = "UPDATE citas SET fecha_hora = ?, motivo = ?, dni_cliente = ?, id_mascota = ?, nivel_prioridad = ? WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String fechaHora = cita.getFechaHora().format(DB_DATE_TIME_FORMATTER).replace(' ', 'T');
            
            pstmt.setString(1, fechaHora);
            pstmt.setString(2, cita.getMotivo());
            pstmt.setString(3, cita.getDniCliente());
            pstmt.setInt(4, cita.getIdMascota());
            pstmt.setInt(5, cita.getNivelPrioridad());
            pstmt.setInt(6, cita.getId()); // WHERE clause
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cita: " + e.getMessage());
            return false;
        }
    }

    // --- D E L E T E ---
    public boolean eliminar(int id) {
        String sql = "DELETE FROM citas WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar cita: " + e.getMessage());
            return false;
        }
    }
    
    // Método auxiliar para obtener lista completa de mascotas (para el ComboBox del controller)
    public ObservableList<Mascotas> obtenerTodasMascotas() {
        ObservableList<Mascotas> mascotas = FXCollections.observableArrayList();
        // Solo traemos los campos necesarios para el ComboBox de agendamiento
        String sql = "SELECT m.id, m.nombre, m.dni_cliente, c.nombres || ' ' || c.apellidos AS nombre_dueno " + 
                     "FROM mascotas m JOIN clientes c ON m.dni_cliente = c.dni";
        
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // Creamos un objeto Mascota simple, reusando el modelo Mascota
                Mascotas m = new Mascotas(); 
                m.setId(rs.getInt("id"));
                m.setNombre(rs.getString("nombre"));
                m.setDniCliente(rs.getString("dni_cliente"));
                m.setNombreDueno(rs.getString("nombre_dueno")); 
                mascotas.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mascotas;
    }
}