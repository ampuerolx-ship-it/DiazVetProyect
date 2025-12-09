package database.dao;

import database.ConexionDB;
import model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class ClienteDAO {

    // --- C R E A T E ---
    public boolean insertar(Cliente cliente) {
        // Asegúrate que la tabla clientes tiene 6 columnas, incluyendo 'correo'
        String sql = "INSERT INTO clientes (dni, nombres, apellidos, telefono, direccion, correo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente.getDni());
            pstmt.setString(2, cliente.getNombres());
            pstmt.setString(3, cliente.getApellidos());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getDireccion());
            pstmt.setString(6, cliente.getCorreo());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    // --- R E A D A L L ---
    public ObservableList<Cliente> obtenerTodos() {
        ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
        String sql = "SELECT dni, nombres, apellidos, telefono, direccion, correo FROM clientes";
        
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getString("dni"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("telefono"),
                    rs.getString("direccion"),
                    rs.getString("correo")
                );
                listaClientes.add(cliente);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }
        return listaClientes;
    }
    
    // --- U P D A T E ---
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombres = ?, apellidos = ?, telefono = ?, direccion = ?, correo = ? WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente.getNombres());
            pstmt.setString(2, cliente.getApellidos());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getCorreo());
            pstmt.setString(6, cliente.getDni()); // WHERE clause
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    // --- D E L E T E ---
    public boolean eliminar(String dni) {
        String sql = "DELETE FROM clientes WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dni);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
    
    // Método auxiliar para buscar por DNI
    public Cliente obtenerPorDni(String dni) {
        String sql = "SELECT dni, nombres, apellidos, telefono, direccion, correo FROM clientes WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dni);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Cliente(rs.getString("dni"), rs.getString("nombres"), rs.getString("apellidos"),
                                    rs.getString("telefono"), rs.getString("direccion"), rs.getString("correo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}