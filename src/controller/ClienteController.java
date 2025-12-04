package controller;

import database.ConexionDB;
import database.dao.UsuarioDAO;
import javafx.scene.control.Alert;
import model.Usuario;

public class ClienteController {

    private UsuarioDAO usuarioDAO;

    public ClienteController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean registrarCliente(Cliente cliente) {
        // Validaciones...

        // CORRECCIÓN: El SQL debe coincidir con tu tabla 'clientes' y el modelo 'Cliente'
        // IMPORTANTE: Si tu tabla 'clientes' tiene columna 'nombre' (singular), concatenamos.
        // Si tiene 'nombres' y 'apellidos', usamos los dos. 
        // Asumiré por tu esquema anterior que la tabla tiene 'nombre' completo.

        String sql = "INSERT INTO clientes(dni, nombres, apellidos, telefono, direccion, correo) VALUES(?,?,?,?,?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getDni());
            // Concatenamos para guardar en la BD si la columna es única
            pstmt.setString(2, cliente.getNombres() + " " + cliente.getApellidos()); 
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getCorreo()); // ¡Asegúrate de tener esta columna en la BD!

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Método principal para procesar el registro de un cliente.
     * Retorna TRUE si se guardó correctamente, FALSE si hubo error.
     */
    public boolean guardarNuevoCliente(String dni, String nombre, String apellido, 
                                       String telefono, String correo, String direccion) {
        
        // 1. VALIDACIONES BÁSICAS (Lógica de Negocio)
        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos Incompletos", 
                          "El DNI, Nombre y Apellido son obligatorios.");
            return false;
        }

        // 2. VERIFICAR DUPLICADOS
        if (usuarioDAO.existeUsuario(dni)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Usuario Existente", 
                          "Ya existe un cliente registrado con el DNI: " + dni);
            return false;
        }

        // 3. CREAR EL MODELO (OBJETO)
        // Nota: Para clientes nuevos, el usuario y contraseña son el DNI por defecto.
        Usuario nuevoCliente = new Usuario(
            dni,            // DNI
            dni,            // Username (Automático)
            dni,            // Password (Automático)
            "CLIENTE",      // Rol fijo
            nombre,
            apellido,
            correo,
            telefono
        );
        // Nota: Si tu constructor de Usuario no tiene 'direccion', puedes agregarla o manejarla aparte.

        // 4. GUARDAR EN BASE DE DATOS USANDO DAO
        if (usuarioDAO.registrar(nuevoCliente)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", 
                          "Cliente " + nombre + " " + apellido + " registrado correctamente.");
            return true;
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", 
                          "No se pudo guardar el cliente. Intente nuevamente.");
            return false;
        }
    }

    // Método auxiliar para mostrar alertas
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}