package controller;

import database.dao.UsuarioDAO;
import javafx.scene.control.Alert;
import model.Usuario;

public class ClienteController {

    private UsuarioDAO usuarioDAO;

    public ClienteController() {
        this.usuarioDAO = new UsuarioDAO();
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