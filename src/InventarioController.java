package controller;

import database.dao.ProductoDAO;
import model.Producto;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;

public class InventarioController {

    // Tabla Principal
    @FXML private TableView<Producto> tableProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colCategoria;
    
    // Tabla de Stock Bajo
    @FXML private TableView<Producto> tableStockBajo;
    @FXML private TableColumn<Producto, String> colBajoCodigo;
    @FXML private TableColumn<Producto, String> colBajoNombre;
    @FXML private TableColumn<Producto, Integer> colBajoStock;
    @FXML private TableColumn<Producto, String> colBajoCategoria;

    // Formulario de Detalle
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private ComboBox<String> cmbCategoria;
    
    // Botones
    @FXML private Button btnGuardar;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    
    private final ProductoDAO productoDAO = new ProductoDAO();
    
    @FXML
    public void initialize() {
        // 1. Configuración de columnas
        configurarColumnas(tableProductos, colCodigo, colNombre, colPrecio, colStock, colCategoria);
        configurarColumnasAlerta();
        
        // 2. Configurar ComboBox de categorías
        cmbCategoria.getItems().addAll("Alimento", "Juguete", "Medicina", "Accesorio", "Higiene", "Otro");
        
        // 3. Cargar datos
        cargarDatosTabla();
        
        // 4. Listener de selección
        tableProductos.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> mostrarDetallesProducto(newValue)
        );
        
        // 5. Inicializar/Limpiar
        limpiarFormulario();
        
        // 6. Aplicar validación de números para Precio y Stock
        configurarValidacionNumerica(txtPrecio, true); 
        configurarValidacionNumerica(txtStock, false);
    }
    
    private void configurarColumnas(TableView<Producto> tabla, TableColumn<Producto, String> cod, 
                                    TableColumn<Producto, String> nom, TableColumn<?, ?> pre, 
                                    TableColumn<Producto, Integer> sto, TableColumn<Producto, String> cat) {
        cod.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        nom.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        ((TableColumn<Producto, Double>) pre).setCellValueFactory(new PropertyValueFactory<>("precio"));
        sto.setCellValueFactory(new PropertyValueFactory<>("stock"));
        cat.setCellValueFactory(new PropertyValueFactory<>("categoria"));
    }
    
    private void configurarColumnasAlerta() {
        colBajoCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colBajoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colBajoStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colBajoCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
    }
    
    // Se ejecuta al inicio y al guardar/eliminar
    private void cargarDatosTabla() {
        ObservableList<Producto> listaProductos = productoDAO.obtenerTodos();
        tableProductos.setItems(listaProductos);
        cargarAlertasStock(); // Sincronizar alertas después de cargar la tabla principal
    }
    
    // Muestra los productos con stock bajo (limite 5, definido en DAO o DashboardDAO)
    private void cargarAlertasStock() {
        ObservableList<Producto> stockBajo = productoDAO.obtenerStockBajo(5); 
        tableStockBajo.setItems(stockBajo);
    }
    
    private void mostrarDetallesProducto(Producto producto) {
        if (producto != null) {
            txtCodigo.setText(producto.getCodigo());
            txtNombre.setText(producto.getNombre());
            txtDescripcion.setText(producto.getDescripcion());
            txtPrecio.setText(String.format("%.2f", producto.getPrecio()));
            txtStock.setText(String.valueOf(producto.getStock()));
            cmbCategoria.setValue(producto.getCategoria());
            
            txtCodigo.setDisable(true); 
            btnGuardar.setText("Actualizar");
            btnEliminar.setDisable(false);
        } else {
            limpiarFormulario();
        }
    }
    
    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        cmbCategoria.setValue(null); 
        
        txtCodigo.setDisable(false); // Habilitar para nuevo registro
        btnGuardar.setText("Guardar");
        btnEliminar.setDisable(true);
        tableProductos.getSelectionModel().clearSelection();
    }
    
    private Producto getProductoDelFormulario() {
        if (validarCampos()) {
            try {
                // ... (Parsing and validation logic) ...
                String codigo = txtCodigo.getText().trim();
                String nombre = txtNombre.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                // Importante: usar replace(',', '.') para compatibilidad decimal
                double precio = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.')); 
                int stock = Integer.parseInt(txtStock.getText().trim());
                String categoria = cmbCategoria.getValue();
                
                return new Producto(codigo, nombre, descripcion, precio, stock, categoria);
                
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "Asegúrate de que Precio y Stock sean números válidos.");
                return null;
            }
        }
        return null;
    }
    
    private boolean validarCampos() {
        String mensajeError = "";
        // ... (Validation checks) ...
        if (txtCodigo.getText() == null || txtCodigo.getText().trim().isEmpty()) mensajeError += "Código no puede estar vacío.\n";
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) mensajeError += "Nombre no puede estar vacío.\n";
        if (txtPrecio.getText() == null || txtPrecio.getText().trim().isEmpty()) mensajeError += "Precio no puede estar vacío.\n";
        if (txtStock.getText() == null || txtStock.getText().trim().isEmpty()) mensajeError += "Stock no puede estar vacío.\n";
        if (cmbCategoria.getValue() == null) mensajeError += "Debes seleccionar una categoría.\n";
        
        if (mensajeError.isEmpty()) return true;
        
        mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, corrige los siguientes errores:\n" + mensajeError);
        return false;
    }
    
    // --- ACCIONES CRUD ---
    
    @FXML private void handleNuevo() { limpiarFormulario(); }
    
    @FXML
    private void handleGuardar() {
        Producto producto = getProductoDelFormulario();
        if (producto == null) return;
        
        boolean exito;
        if (txtCodigo.isDisable()) {
            // Actualizar (Código deshabilitado significa edición)
            exito = productoDAO.actualizar(producto);
            mostrarAlerta(exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, 
                         exito ? "Éxito" : "Error de BD", 
                         exito ? "Producto actualizado correctamente." : "No se pudo actualizar el producto.");
        } else {
            // Insertar (Código habilitado significa nuevo)
            if (productoDAO.obtenerPorCodigo(producto.getCodigo()) != null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "El código de producto ya existe.");
                return;
            }
            exito = productoDAO.insertar(producto);
            mostrarAlerta(exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, 
                         exito ? "Éxito" : "Error de BD", 
                         exito ? "Producto registrado correctamente." : "No se pudo registrar el producto.");
        }
        
        cargarDatosTabla(); 
        limpiarFormulario();
    }
    
    @FXML
    private void handleEliminar() {
        Producto seleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("Eliminar Producto: " + seleccionado.getNombre());
        alert.setContentText("¿Está seguro de que desea eliminar permanentemente este producto del inventario?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (productoDAO.eliminar(seleccionado.getCodigo())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto eliminado correctamente.");
                cargarDatosTabla();
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo eliminar el producto.");
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
    
    // Implementación de la validación numérica robusta
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