package database.dao;

import database.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Cliente;
import model.Mascotas;
import model.Paciente;
import model.Usuario;


public class UsuarioDAO {

    /**
     * Valida si el usuario y contraseña existen.
     * @return Objeto Usuario si es correcto, null si falla.
     */
    public Usuario login(String user, String pass) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user);
            pstmt.setString(2, pass); // En producción, aquí compararías HASHES, no texto plano.
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Si encontramos coincidencia, construimos el objeto
                return new Usuario(
                    rs.getString("dni"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("rol"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("correo"),
                    rs.getString("telefono")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error en Login: " + e.getMessage());
        }
        return null; // Credenciales incorrectas
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     */
    public boolean registrar(Usuario u) {
        String sql = "INSERT INTO usuarios (dni, username, password, rol, nombres, apellidos, correo, telefono) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, u.getDni());
            pstmt.setString(2, u.getUsername());
            pstmt.setString(3, u.getPassword());
            pstmt.setString(4, u.getRol());
            pstmt.setString(5, u.getNombres());
            pstmt.setString(6, u.getApellidos());
            pstmt.setString(7, "correo@ejemplo.com"); // Placeholder o u.getCorreo()
            pstmt.setString(8, "000-000"); // Placeholder o u.getTelefono()
            
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si un DNI ya existe (Para evitar duplicados)
     */
    public boolean existeUsuario(String dni) {
        String sql = "SELECT dni FROM usuarios WHERE dni = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dni);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}