package controller;

import database.dao.MascotaDAO;
import database.dao.ClienteDAO;
import model.Cliente;
import structures.arboles.ArbolAVL; // Importamos el AVL
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;
import java.util.List;
import model.Mascotas;

public class MascotaController {

    // Tabla
    @FXML private TableView<Mascotas> tableMascotas;
    @FXML private TableColumn<Mascotas, Integer> colId;
    @FXML private TableColumn<Mascotas, String> colNombre;
    @FXML private TableColumn<Mascotas, String> colEspecie;
    @FXML private TableColumn<Mascotas, String> colRaza;
    @FXML private TableColumn<Mascotas, String> colDueno;

    // Formulario
    @FXML private TextField txtId; // Deshabilitado para auto-incremento
    @FXML private TextField txtNombre;
    @FXML private TextField txtEdad;
    @FXML private TextField txtPeso;
    @FXML private ComboBox<String> cmbEspecie;
    @FXML private TextField txtRaza;
    
    // Dueño
    @FXML private ComboBox<Cliente> cmbDueno; // Usamos objeto Cliente
    @FXML private TextField txtBuscarDueno;
    
    // Búsqueda en tabla
    @FXML private TextField txtBuscarMascota; 
    
    // Botones
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    
    // DAOs
    private final MascotaDAO mascotaDAO = new MascotaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO(); // Para el ComboBox de dueños

    // Estructuras de Datos
    private ObservableList<Mascotas> masterData = FXCollections.observableArrayList();
    private ArbolAVL<Mascotas> arbolPacientes = new ArbolAVL<>(); // 🎯 AVL para búsqueda rápida por ID
    
    @FXML
    public void initialize() {
        // 1. Configuración de columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colDueno.setCellValueFactory(new PropertyValueFactory<>("nombreDueno"));

        // 2. Configurar ComboBox
        cmbEspecie.getItems().addAll("Perro", "Gato", "Ave", "Roedor", "Otro");
        cmbDueno.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Cliente cliente) {
                return (cliente != null) ? cliente.getNombres() + " " + cliente.getApellidos() + " (" + cliente.getDni() + ")" : "";
            }
            @Override
            public Cliente fromString(String string) { return null; }
        });
        
        // 3. Cargar datos
        cargarDatosMascotas();
        cargarDatosClientes();
        
        // 4. Listener de selección y búsqueda
        tableMascotas.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> mostrarDetallesMascota(newValue)
        );
        implementarBusqueda();
        
        // 5. Bloquear ID y limpiar
        txtId.setDisable(true); 
        limpiarFormulario();
        
        // 6. Validaciones numéricas (peso y edad)
        configurarValidacionNumerica(txtEdad, false); // Edad: entero
        configurarValidacionNumerica(txtPeso, true); // Peso: decimal
    }
    
    private void cargarDatosMascotas() {
        masterData.clear();
        arbolPacientes = new ArbolAVL<>(); // Resetear el AVL
        
        ObservableList<Mascotas> mascotasDB = mascotaDAO.obtenerTodos();
        masterData.addAll(mascotasDB);
        
        // 🎯 Llenar el Árbol AVL con los pacientes por ID (clave única)
        for(Mascotas m : mascotasDB) {
            arbolPacientes.insertar(m); 
        }
    }
    
    private void cargarDatosClientes() {
        ObservableList<Cliente> clientes = clienteDAO.obtenerTodos();
        cmbDueno.setItems(clientes);
    }
    
    private void implementarBusqueda() {
        // Filtro estándar de ObservableList para búsqueda por Nombre/Especie (Texto)
        FilteredList<Mascotas> filteredData = new FilteredList<>(masterData, p -> true);

        txtBuscarMascota.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mascota -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                
                // Búsqueda por ID (O(log n) si usáramos AVL, pero aquí mantenemos el filtro de UI)
                try {
                    int idFilter = Integer.parseInt(newValue);
                    if (mascota.getId() == idFilter) return true;
                } catch (NumberFormatException ignored) {}
                
                // Búsqueda por Nombre, Especie o Dueño
                if (mascota.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } else if (mascota.getEspecie().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (mascota.getNombreDueno().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        // Envolver en SortedList y aplicar a la tabla
        SortedList<Mascotas> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableMascotas.comparatorProperty());
        tableMascotas.setItems(sortedData);
    }

    private void mostrarDetallesMascota(Mascotas mascota) {
        if (mascota != null) {
            txtId.setText(String.valueOf(mascota.getId()));
            txtNombre.setText(mascota.getNombre());
            cmbEspecie.setValue(mascota.getEspecie());
            txtRaza.setText(mascota.getRaza());
            txtEdad.setText(String.valueOf(mascota.getEdad()));
            txtPeso.setText(String.format("%.2f", mascota.getPeso()));
            
            // Seleccionar el dueño correcto en el ComboBox
            cmbDueno.getSelectionModel().select(
                cmbDueno.getItems().stream()
                        .filter(c -> c.getDni().equals(mascota.getDniCliente()))
                        .findFirst()
                        .orElse(null)
            );
            
            // txtId.setDisable(true) ya se hace en initialize, no se puede cambiar ID
            btnGuardar.setText("Actualizar");
            btnEliminar.setDisable(false);
        } else {
            limpiarFormulario();
        }
    }
    
    private void limpiarFormulario() {
        txtId.setText("Nuevo (Auto)");
        txtNombre.setText("");
        cmbEspecie.setValue(null);
        txtRaza.setText("");
        txtEdad.setText("");
        txtPeso.setText("");
        cmbDueno.setValue(null);
        
        btnGuardar.setText("Guardar");
        btnEliminar.setDisable(true);
        tableMascotas.getSelectionModel().clearSelection();
    }
    
    private Mascotas getMascotaDelFormulario() {
        if (!validarCampos()) return null;
        
        try {
            int id = txtId.getText().contains("Nuevo") ? 0 : Integer.parseInt(txtId.getText());
            String nombre = txtNombre.getText().trim();
            String especie = cmbEspecie.getValue();
            String raza = txtRaza.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());
            double peso = Double.parseDouble(txtPeso.getText().trim().replace(',', '.'));
            Cliente dueno = cmbDueno.getValue();
            
            // Asumiendo que el campo fotoMascotaRuta se manejará aparte o se dejará en null por ahora
            return new Mascotas(id, nombre, especie, raza, edad, peso, dueno.getDni(), dueno.getNombres() + " " + dueno.getApellidos(), null, null);
            
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "Edad y Peso deben ser números válidos.");
            return null;
        }
    }
    
    private boolean validarCampos() {
        String mensajeError = "";
        if (txtNombre.getText().trim().isEmpty()) mensajeError += "Nombre no puede estar vacío.\n";
        if (cmbEspecie.getValue() == null) mensajeError += "Debe seleccionar una Especie.\n";
        if (txtEdad.getText().trim().isEmpty() || !txtEdad.getText().trim().matches("\\d+")) mensajeError += "Edad debe ser un número entero.\n";
        if (txtPeso.getText().trim().isEmpty()) mensajeError += "Peso no puede estar vacío.\n";
        if (cmbDueno.getValue() == null) mensajeError += "Debe seleccionar un Dueño.\n";
        
        if (mensajeError.isEmpty()) return true;
        
        mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos/Inválidos", "Por favor, corrige los siguientes errores:\n" + mensajeError);
        return false;
    }
    
    // --- ACCIONES CRUD ---
    
    @FXML private void handleNuevo() { limpiarFormulario(); }
    
    @FXML
    private void handleGuardar() {
        Mascotas mascota = getMascotaDelFormulario();
        if (mascota == null) return;
        
        boolean exito;
        if (mascota.getId() > 0) {
            // Actualizar
            exito = mascotaDAO.actualizar(mascota);
        } else {
            // Insertar (ID es 0 o "Nuevo")
            exito = mascotaDAO.insertar(mascota);
        }
        
        mostrarAlerta(exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, 
                     exito ? "Éxito" : "Error de BD", 
                     exito ? "Paciente guardado correctamente." : "No se pudo guardar el paciente.");
        
        cargarDatosMascotas(); // Recargar el AVL y la tabla
        limpiarFormulario();
    }
    
    @FXML
    private void handleEliminar() {
        Mascotas seleccionado = tableMascotas.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("Eliminar Paciente: " + seleccionado.getNombre());
        alert.setContentText("¿Está seguro de que desea eliminar permanentemente a este paciente? Esto podría afectar a sus historiales asociados.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (mascotaDAO.eliminar(seleccionado.getId())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Paciente eliminado correctamente.");
                cargarDatosMascotas(); // Recargar el AVL y la tabla
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo eliminar el paciente.");
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
    
    private void configurarValidacionNumerica(TextField textField, boolean permitirDecimal) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) return;
            String cleanValue = newValue.replace(',', '.');
            String regex = permitirDecimal ? "^\\d*\\.?\\d*$" : "^\\d*$";
            
            if (!cleanValue.matches(regex)) {
                textField.setText(oldValue);
            }
        });
    }
}