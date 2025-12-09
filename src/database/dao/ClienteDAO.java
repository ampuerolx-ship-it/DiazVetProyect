package database.dao;

import database.ConexionDB;
import model.Cliente;
import model.Mascotas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    /**
     * Registra un nuevo cliente y su mascota inicial en una transacción.
     * @param cliente Objeto Cliente a registrar.
     * @param mascota Objeto Mascota a registrar.
     * @return El ID de la mascota registrada (clave para el historial), o -1 si falla.
     */
    public int registrarClienteYMascota(Cliente cliente, Mascotas mascota) {
        Connection conn = null;
        int idMascotaGenerado = -1;
        
        // SQL para Clientes
        String sqlCliente = "INSERT INTO clientes (dni, nombres, apellidos, telefono, direccion, correo) VALUES (?, ?, ?, ?, ?, ?)";
        
        // SQL para Mascotas. Usamos 'date('now', 'localtime')' para el contador del Dashboard "Pacientes Nuevos"
        String sqlMascota = "INSERT INTO mascotas (nombre, especie, raza, edad, peso, dni_cliente, foto_mascota_ruta, fecha_registro) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, date('now', 'localtime'))"; 

        try {
            conn = ConexionDB.conectar();
            if (conn == null) return -1;
            
            // 1. Iniciar Transacción (Desactivar auto-commit)
            conn.setAutoCommit(false); 

            // =======================================================
            // A. Registrar Cliente
            // =======================================================
            try (PreparedStatement pstmtCliente = conn.prepareStatement(sqlCliente)) {
                pstmtCliente.setString(1, cliente.getDni());
                pstmtCliente.setString(2, cliente.getNombres());
                pstmtCliente.setString(3, cliente.getApellidos());
                pstmtCliente.setString(4, cliente.getTelefono());
                pstmtCliente.setString(5, cliente.getDireccion());
                pstmtCliente.setString(6, cliente.getCorreo());
                pstmtCliente.executeUpdate();
            }
            
            // =======================================================
            // B. Registrar Mascota (Obtener ID generado)
            // =======================================================
            // Statement.RETURN_GENERATED_KEYS es crucial para SQLite
            try (PreparedStatement pstmtMascota = conn.prepareStatement(sqlMascota, Statement.RETURN_GENERATED_KEYS)) {
                pstmtMascota.setString(1, mascota.getNombre());
                pstmtMascota.setString(2, mascota.getEspecie());
                pstmtMascota.setString(3, mascota.getRaza());
                pstmtMascota.setInt(4, mascota.getEdad());
                pstmtMascota.setDouble(5, mascota.getPeso());
                pstmtMascota.setString(6, cliente.getDni()); // Clave Foránea
                pstmtMascota.setString(7, mascota.getFotoMascotaRuta());
                
                int affectedRows = pstmtMascota.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmtMascota.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            idMascotaGenerado = generatedKeys.getInt(1); // Este es el ID de la nueva mascota
                        }
                    }
                }
            }
            
            // 3. Commit de la Transacción: Si todo fue bien
            conn.commit();
            
        } catch (SQLException e) {
            System.err.println("Error transaccional al registrar Cliente/Mascota. Iniciando Rollback...");
            try {
                if (conn != null) conn.rollback(); // Deshacer si algo falló
            } catch (SQLException ex) {
                System.err.println("Error durante el rollback: " + ex.getMessage());
            }
            e.printStackTrace();
            return -1;
            
        } finally {
            // 4. Restaurar y cerrar conexión
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return idMascotaGenerado;
    }
    
    /**
     * Busca un cliente por DNI para verificar si ya existe.
     */
    public boolean existeCliente(String dni) {
        String sql = "SELECT dni FROM clientes WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dni);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}