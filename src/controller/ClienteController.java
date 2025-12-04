package controller;

import database.ConexionDB;
import model.Cliente;
import utilidades.Validador; // Asegúrate de tener esta clase

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteController {

    // Constructor vacío (Ya no necesitamos UsuarioDAO aquí)
    public ClienteController() {
    }

    /**
     * Registra un nuevo cliente en la base de datos.
     * @param cliente Objeto Cliente con los datos.
     * @return true si se guardó correctamente.
     */
    public boolean registrarCliente(Cliente cliente) {
        
        // 1. Validaciones previas
        if (!Validador.validarDNI(cliente.getDni())) {
            System.err.println("Error: " + Validador.getMensajeErrorDNI());
            return false;
        }
        if (!Validador.validarTelefono(cliente.getTelefono())) {
            System.err.println("Error: " + Validador.getMensajeErrorTelefono());
            return false;
        }

        // 2. Consulta SQL (Ajustada a tu base de datos real)
        String sql = "INSERT INTO clientes(dni, nombre, telefono, direccion, correo) VALUES(?,?,?,?,?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getDni());
            
            // Concatenamos Nombres y Apellidos para guardarlos en la columna 'nombre'
            String nombreCompleto = cliente.getNombres() + " " + cliente.getApellidos();
            pstmt.setString(2, nombreCompleto);
            
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getCorreo()); 

            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar cliente en BD: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un cliente existente.
     */
    public boolean actualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, telefono = ?, direccion = ?, correo = ? WHERE dni = ?";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String nombreCompleto = cliente.getNombres() + " " + cliente.getApellidos();
            
            pstmt.setString(1, nombreCompleto);
            pstmt.setString(2, cliente.getTelefono());
            pstmt.setString(3, cliente.getDireccion());
            pstmt.setString(4, cliente.getCorreo());
            pstmt.setString(5, cliente.getDni()); // El DNI es la condición WHERE
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un cliente por su DNI.
     */
    public Cliente buscarClientePorDni(String dni) {
        String sql = "SELECT * FROM clientes WHERE dni = ?";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dni);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // NOTA: Como en la BD guardamos el nombre completo junto, 
                // aquí lo recuperamos en "nombres" y dejamos "apellidos" vacío 
                // o intentamos separarlo si es estrictamente necesario.
                // Para simplificar, pondremos todo en 'nombres'.
                
                return new Cliente(
                    rs.getString("dni"),
                    rs.getString("nombre"), // Ponemos todo el nombre aquí
                    "",                     // Apellido vacío (ya está incluido en nombre)
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("direccion")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene todos los clientes (Para tablas de administración).
     */
    public List<Cliente> obtenerTodosLosClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(new Cliente(
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    "", 
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("direccion")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }
    
    /**
     * Elimina un cliente por su DNI.
     */
    public boolean eliminarCliente(String dni) {
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
}