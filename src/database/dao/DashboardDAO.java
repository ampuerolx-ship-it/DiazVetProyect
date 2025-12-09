package database.dao;

import database.ConexionDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class DashboardDAO {

    // 1. Obtener Citas de Hoy
    public int contarCitasHoy() {
        String sql = "SELECT COUNT(*) FROM citas WHERE date(fecha_hora) = date('now', 'localtime')";
        return ejecutarConteo(sql);
    }

    // 2. Obtener Pacientes Nuevos (Mes Actual)
    public int contarPacientesNuevosMes() {
        String sql = "SELECT COUNT(*) FROM mascotas WHERE strftime('%Y-%m', fecha_registro) = strftime('%Y-%m', 'now', 'localtime')";
        return ejecutarConteo(sql);
    }

    // 3. Calcular Ventas del Día
    public double sumarVentasDia() {
        String sql = "SELECT SUM(total) FROM ventas WHERE date(fecha_hora) = date('now', 'localtime')";
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getDouble(1); // Retorna 0.0 si es null
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // 4. Alertas de Stock Bajo (menor a 5 unidades)
    public int contarAlertasStock() {
        String sql = "SELECT COUNT(*) FROM productos WHERE stock <= 5";
        return ejecutarConteo(sql);
    }

    // 5. Lista de Próximas Citas (Datos reales para la lista)
    public ObservableList<String> obtenerProximasCitas() {
        ObservableList<String> lista = FXCollections.observableArrayList();
        // Traemos fecha, nombre mascota y motivo. Ordenamos por fecha más próxima.
        String sql = "SELECT c.fecha_hora, m.nombre, c.motivo " +
                     "FROM citas c " +
                     "JOIN mascotas m ON c.id_mascota = m.id " +
                     "WHERE datetime(c.fecha_hora) >= datetime('now', 'localtime') " +
                     "ORDER BY c.fecha_hora ASC LIMIT 10";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String fechaRaw = rs.getString("fecha_hora"); // Ej: 2023-10-25T14:30
                String nombre = rs.getString("nombre");
                String motivo = rs.getString("motivo");
                
                // Formateo simple para la lista
                String[] partesFecha = fechaRaw.split("T");
                String hora = partesFecha.length > 1 ? partesFecha[1] : partesFecha[0];
                
                lista.add("🕒 " + hora + " - " + nombre + " (" + motivo + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Método auxiliar para evitar repetir try-catch en conteos
    private int ejecutarConteo(String sql) {
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}