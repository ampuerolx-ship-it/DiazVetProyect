package database.dao;

import database.ConexionDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import model.Mascotas;

public class MascotaDAO {

    // --- C R E A T E ---
    public boolean insertar(Mascotas mascota) {
        String sql = "INSERT INTO mascotas (nombre, especie, raza, edad, peso, dni_cliente, foto_mascota_ruta, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?, date('now', 'localtime'))";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, mascota.getNombre());
            pstmt.setString(2, mascota.getEspecie());
            pstmt.setString(3, mascota.getRaza());
            pstmt.setInt(4, mascota.getEdad());
            pstmt.setDouble(5, mascota.getPeso());
            pstmt.setString(6, mascota.getDniCliente());
            pstmt.setString(7, mascota.getFotoMascotaRuta());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar mascota: " + e.getMessage());
            return false;
        }
    }

    // --- R E A D A L L (CON JOIN) ---
    public ObservableList<Mascotas> obtenerTodos() {
        ObservableList<Mascotas> listaMascotas = FXCollections.observableArrayList();
        // Usamos JOIN para obtener el nombre completo del dueño
        String sql = "SELECT m.*, c.nombres, c.apellidos " +
                     "FROM mascotas m " +
                     "JOIN clientes c ON m.dni_cliente = c.dni " +
                     "ORDER BY m.id ASC";
        
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String nombreCompletoDueno = rs.getString("nombres") + " " + rs.getString("apellidos");
                
                Mascotas mascota = new Mascotas(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("especie"),
                    rs.getString("raza"),
                    rs.getInt("edad"),
                    rs.getDouble("peso"),
                    rs.getString("dni_cliente"),
                    nombreCompletoDueno, // Nombre del dueño
                    rs.getString("foto_mascota_ruta"),
                    rs.getString("fecha_registro")
                );
                listaMascotas.add(mascota);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener mascotas: " + e.getMessage());
        }
        return listaMascotas;
    }
    
    // --- U P D A T E ---
    public boolean actualizar(Mascotas mascota) {
        String sql = "UPDATE mascotas SET nombre = ?, especie = ?, raza = ?, edad = ?, peso = ?, dni_cliente = ?, foto_mascota_ruta = ? WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, mascota.getNombre());
            pstmt.setString(2, mascota.getEspecie());
            pstmt.setString(3, mascota.getRaza());
            pstmt.setInt(4, mascota.getEdad());
            pstmt.setDouble(5, mascota.getPeso());
            pstmt.setString(6, mascota.getDniCliente());
            pstmt.setString(7, mascota.getFotoMascotaRuta());
            pstmt.setInt(8, mascota.getId()); // WHERE clause
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar mascota: " + e.getMessage());
            return false;
        }
    }

    // --- D E L E T E ---
    public boolean eliminar(int id) {
        String sql = "DELETE FROM mascotas WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar mascota: " + e.getMessage());
            return false;
        }
    }
}