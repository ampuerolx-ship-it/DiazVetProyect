package controller;

import database.ConexionDB;
import model.Cliente;
import model.Mascotas;
import model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegistroTransaccionalController {

    /**
     * Realiza el registro completo de las 3 entidades en una sola transacción.
     */
    public boolean registrarCompleto(Usuario usuario, Cliente cliente, Mascotas mascota) {
        Connection conn = null;
        
        try {
            conn = ConexionDB.conectar();
            conn.setAutoCommit(false); // INICIO TRANSACCIÓN

            // 1. REGISTRAR CLIENTE
            String sqlCliente = "INSERT INTO clientes(dni, nombres, apellidos, telefono, direccion) VALUES(?,?,?,?)";
            try (PreparedStatement pstC = conn.prepareStatement(sqlCliente)) {
                pstC.setString(1, cliente.getDni());
                pstC.setString(2, cliente.getNombres());
                pstC.setString(3, cliente.getApellidos());
                pstC.setString(4, cliente.getTelefono());
                pstC.setString(5, cliente.getDireccion());
                pstC.executeUpdate();
            }

            // 2. REGISTRAR USUARIO
            // Asumiendo que Usuario.java tiene un método hashPassword o ya viene hasheada
            String sqlUsuario = "INSERT INTO usuarios(nickname, password_hash, dni_cliente, rol, foto_perfil_ruta) VALUES(?,?,?,?,?)";
            try (PreparedStatement pstU = conn.prepareStatement(sqlUsuario)) {
                pstU.setString(1, usuario.getNickname());
                pstU.setString(2, usuario.getPassword()); 
                pstU.setString(3, cliente.getDni()); // FK
                pstU.setString(4, "cliente");
                pstU.setString(5, usuario.getFotoPerfilRuta());
                pstU.executeUpdate();
            }

            // 3. REGISTRAR MASCOTA
            String sqlMascota = "INSERT INTO mascotas(nombre, especie, raza, edad, peso, dni_cliente, foto_mascota_ruta) VALUES(?,?,?,?,?,?,?)";
            int idMascotaGenerado = -1;
            
            try (PreparedStatement pstM = conn.prepareStatement(sqlMascota, Statement.RETURN_GENERATED_KEYS)) {
                pstM.setString(1, mascota.getNombre());
                pstM.setString(2, mascota.getEspecie());
                pstM.setString(3, mascota.getRaza());
                pstM.setInt(4, mascota.getEdad());
                pstM.setDouble(5, mascota.getPeso());
                pstM.setString(6, cliente.getDni()); // FK
                pstM.setString(7, mascota.getFotoMascotaRuta());
                pstM.executeUpdate();
                
                ResultSet rs = pstM.getGeneratedKeys();
                if (rs.next()) {
                    idMascotaGenerado = rs.getInt(1);
                }
            }

            // 4. CREAR HISTORIAL VACÍO (Requisito del sistema antiguo que mantenemos)
            if (idMascotaGenerado != -1) {
                String sqlHist = "INSERT INTO historiales (id_mascota, vacunas_aplicadas, vacunas_pendientes, ultima_desparasitacion, ultima_visita) VALUES (?, 'Ninguna', 'Ninguna', 'No registrada', 'No registrada')";
                try (PreparedStatement pstH = conn.prepareStatement(sqlHist)) {
                    pstH.setInt(1, idMascotaGenerado);
                    pstH.executeUpdate();
                }
            }

            conn.commit(); // CONFIRMAR TRANSACCIÓN
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("Error en registro. Haciendo Rollback.");
                    conn.rollback(); // DESHACER TODO SI FALLA ALGO
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
}