package controller;

import database.dao.ClienteDAO;
import model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;

public class ClienteController {

    // Tabla
    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colNombres;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;

    // Formulario
    @FXML private TextField txtDni;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
    
    // Búsqueda
    @FXML private TextField txtBuscar;
    
    // Botones
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private ObservableList<Cliente> masterData = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // 1. Configuración de columnas
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        
        // 2. Cargar datos iniciales
        cargarDatosTabla();
        
        // 3. Implementar Listener para selección
        tableClientes.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> mostrarDetallesCliente(newValue)
        );
        
        // 4. Implementar Búsqueda Dinámica (Filtrado)
        implementarBusqueda();
        
        // 5. Limpiar formulario al inicio
        limpiarFormulario();
    }
    
    private void cargarDatosTabla() {
        masterData.clear();
        masterData.addAll(clienteDAO.obtenerTodos());
    }
    
    private void implementarBusqueda() {
        FilteredList<Cliente> filteredData = new FilteredList<>(masterData, p -> true);

        // 1. Asignar listener de búsqueda al campo de texto
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(cliente -> {
                // Si el campo de búsqueda está vacío, muestra todos los clientes.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                
                // 2. Lógica de Filtrado (DNI, Nombre o Apellido)
                if (cliente.getDni().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } else if (cliente.getNombres().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (cliente.getApellidos().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // No hay match.
            });
        });

        // 3. Envolver la lista filtrada en una lista ordenada (para permitir ordenamiento de columna)
        SortedList<Cliente> sortedData = new SortedList<>(filteredData);

        // 4. Vincular el comparador SortedList con el TableView
        sortedData.comparatorProperty().bind(tableClientes.comparatorProperty());

        // 5. Aplicar los datos a la tabla
        tableClientes.setItems(sortedData);
    }
    
    private void mostrarDetallesCliente(Cliente cliente) {
        if (cliente != null) {
            txtDni.setText(cliente.getDni());
            txtNombres.setText(cliente.getNombres());
            txtApellidos.setText(cliente.getApellidos());
            txtTelefono.setText(cliente.getTelefono());
            txtCorreo.setText(cliente.getCorreo());
            txtDireccion.setText(cliente.getDireccion());
            
            txtDni.setDisable(true); // Bloquear DNI en edición
            btnGuardar.setText("Actualizar");
            btnEliminar.setDisable(false);
        } else {
            limpiarFormulario();
        }
    }
    
    private void limpiarFormulario() {
        txtDni.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        
        txtDni.setDisable(false); // Habilitar DNI para nuevo registro
        btnGuardar.setText("Guardar");
        btnEliminar.setDisable(true);
        tableClientes.getSelectionModel().clearSelection();
    }
    
    private Cliente getClienteDelFormulario() {
        if (!validarCampos()) return null;
        
        String dni = txtDni.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String direccion = txtDireccion.getText().trim();
        
        return new Cliente(dni, nombres, apellidos, telefono, direccion, correo);
    }
    
    private boolean validarCampos() {
        String mensajeError = "";
        
        // Validación de campos obligatorios
        if (txtDni.getText() == null || txtDni.getText().trim().isEmpty() || txtDni.getText().trim().length() != 8) mensajeError += "DNI debe tener 8 dígitos.\n";
        if (txtNombres.getText() == null || txtNombres.getText().trim().isEmpty()) mensajeError += "Nombres no puede estar vacío.\n";
        if (txtApellidos.getText() == null || txtApellidos.getText().trim().isEmpty()) mensajeError += "Apellidos no puede estar vacío.\n";
        if (txtTelefono.getText() == null || txtTelefono.getText().trim().isEmpty() || txtTelefono.getText().trim().length() != 9) mensajeError += "Teléfono debe tener 9 dígitos.\n";
        if (txtCorreo.getText() == null || txtCorreo.getText().trim().isEmpty() || !txtCorreo.getText().trim().contains("@")) mensajeError += "Correo es inválido.\n";
        
        if (mensajeError.isEmpty()) return true;
        
        mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos/Inválidos", "Por favor, corrige los siguientes errores:\n" + mensajeError);
        return false;
    }
    
    // --- ACCIONES CRUD ---
    
    @FXML private void handleNuevo() { limpiarFormulario(); }
    
    @FXML
    private void handleGuardar() {
        Cliente cliente = getClienteDelFormulario();
        if (cliente == null) return;
        
        boolean exito;
        if (txtDni.isDisable()) {
            // Actualizar (DNI deshabilitado significa edición)
            exito = clienteDAO.actualizar(cliente);
        } else {
            // Insertar (DNI habilitado significa nuevo)
            if (clienteDAO.obtenerPorDni(cliente.getDni()) != null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "El DNI del cliente ya existe.");
                return;
            }
            exito = clienteDAO.insertar(cliente);
        }
        
        mostrarAlerta(exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, 
                     exito ? "Éxito" : "Error de BD", 
                     exito ? "Cliente guardado correctamente." : "No se pudo guardar el cliente.");
        
        cargarDatosTabla(); 
        limpiarFormulario();
    }
    
    @FXML
    private void handleEliminar() {
        Cliente seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("Eliminar Cliente: " + seleccionado.getNombres() + " " + seleccionado.getApellidos());
        alert.setContentText("¿Está seguro de que desea eliminar permanentemente a este cliente? Esto podría afectar a sus mascotas y citas asociadas.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (clienteDAO.eliminar(seleccionado.getDni())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente eliminado correctamente.");
                cargarDatosTabla();
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo eliminar el cliente. Verifique que no tenga dependencias activas.");
            }
        }
    }
    
    // --- UTILIDADES ---
    
    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}