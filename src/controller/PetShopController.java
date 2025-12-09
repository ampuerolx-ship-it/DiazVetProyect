package controller;

import database.dao.ProductoDAO;
import database.ConexionDB;
import database.dao.ProductoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Producto;
import utilidades.RecomendadorPack;
import java.util.Stack;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class PetShopController {

    // --- FXML UI Components ---
    @FXML private TableView<Producto> tablaStock;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colCategoria;

    @FXML private ListView<String> listaCarrito; // Vista simple del carrito
    @FXML private Label lblTotal;
    @FXML private TextField txtPresupuesto; // Para el algoritmo de recomendación

    // --- Estructuras de Datos ---
    private ProductoDAO productoDAO;
    private ObservableList<Producto> listaProductosObs;
    
    // PILA: Para el historial del carrito (Permite Undo/Deshacer)
    private Stack<Producto> pilaCarrito; 
    private double totalVenta = 0.0;

    @FXML
    public void initialize() {
        productoDAO = new ProductoDAO();
        pilaCarrito = new Stack<>();
        listaProductosObs = FXCollections.observableArrayList();

        // Configurar Columnas Tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        cargarProductos();
        actualizarTotal();
    }

    private void cargarProductos() {
        listaProductosObs.setAll(productoDAO.listarTodo());
        tablaStock.setItems(listaProductosObs);
    }

    // --- ACCIONES DE USUARIO ---

    @FXML
    private void agregarAlCarrito() {
        Producto seleccionado = tablaStock.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        if (seleccionado.getStock() > 0) {
            // 1. Lógica de Negocio
            pilaCarrito.push(seleccionado); // PUSH a la Pila
            totalVenta += seleccionado.getPrecio();
            
            // 2. Actualizar UI (Visualmente restamos 1, pero no en BD aún)
            seleccionado.setStock(seleccionado.getStock() - 1);
            tablaStock.refresh();
            actualizarVistaCarrito();
        } else {
            mostrarAlerta("Sin Stock", "No quedan unidades de este producto.");
        }
    }

    @FXML
    private void deshacerAccion() {
        if (!pilaCarrito.isEmpty()) {
            // POP de la Pila (Sacar el último elemento agregado)
            Producto ultimo = pilaCarrito.pop();
            
            // Revertir cambios
            totalVenta -= ultimo.getPrecio();
            ultimo.setStock(ultimo.getStock() + 1); // Devolvemos el stock virtual
            
            tablaStock.refresh();
            actualizarVistaCarrito();
        } else {
            mostrarAlerta("Carrito Vacío", "No hay acciones para deshacer.");
        }
    }

    @FXML
    private void sugerirPack() {
        try {
            double presupuesto = Double.parseDouble(txtPresupuesto.getText());
            // Llamada al Algoritmo de la Mochila
            List<Producto> sugeridos = RecomendadorPack.sugerirMejorCompra(presupuesto, listaProductosObs);
            
            StringBuilder mensaje = new StringBuilder("Te recomendamos llevar:\n");
            double totalSug = 0;
            for(Producto p : sugeridos) {
                mensaje.append("- ").append(p.getNombre()).append(" (S/ ").append(p.getPrecio()).append(")\n");
                totalSug += p.getPrecio();
            }
            mensaje.append("\nTotal: S/ ").append(String.format("%.2f", totalSug));
            
            mostrarAlerta("Sugerencia IA", mensaje.toString());
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingresa un presupuesto válido.");
        }
    }

    @FXML
    private void finalizarCompra() {
        if (pilaCarrito.isEmpty()) return;
        
        boolean exito = true;
        double montoFinalVenta = totalVenta;
        
        while(!pilaCarrito.isEmpty()){
            Producto p = pilaCarrito.pop();
            if(!productoDAO.actualizarStock(p.getCodigo(), 1)){
                exito = false;
            }
        }
        
        if(exito){
            String sqlVenta = "INSERT INTO ventas (total) VALUES (?)";
            try (Connection conn = ConexionDB.conectar(); 
                PreparedStatement pst = conn.prepareStatement(sqlVenta)) {

                pst.setDouble(1, montoFinalVenta); 
                pst.executeUpdate();

                mostrarAlerta("Venta Exitosa", "La venta se ha registrado por S/ " + montoFinalVenta);
                totalVenta = 0;
                actualizarVistaCarrito();
                cargarProductos();

            } catch (SQLException e) { 
                e.printStackTrace();
                mostrarAlerta("Error Crítico", "Se actualizó el stock pero falló al guardar el historial de venta.");
            }
            
            /*
            mostrarAlerta("Venta Exitosa", "La venta se ha registrado y el stock actualizado.");
            totalVenta = 0;
            actualizarVistaCarrito();
            cargarProductos(); // Recargar desde BD para asegurar consistencia*/
        } else {
            mostrarAlerta("Error", "Hubo un problema al actualizar el stock.");
        }
    }

    // --- UTILITARIOS UI ---
    
    private void actualizarVistaCarrito() {
        listaCarrito.getItems().clear();
        // Recorremos la pila sin vaciarla para mostrar el contenido
        for (Producto p : pilaCarrito) {
            listaCarrito.getItems().add(p.getNombre() + " - S/ " + p.getPrecio());
        }
        actualizarTotal();
    }

    private void actualizarTotal() {
        lblTotal.setText("S/ " + String.format("%.2f", totalVenta));
    }

    private void mostrarAlerta(String titulo, String msj) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msj);
        alert.showAndWait();
    }
}