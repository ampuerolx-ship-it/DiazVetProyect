package controller;

import database.dao.DashboardDAO;
import javafx.animation.ScaleTransition;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class DashboardAdminController {
    
    @FXML private BorderPane mainLayout;

    // Tarjetas (Cards)
    @FXML private VBox cardCitas, cardPacientes, cardVentas, cardInventario;

    // Labels Stats
    @FXML private Label lblCitasHoy, lblPacientesNuevos, lblVentasDia, lblAlertasStock;

    // Listas
    @FXML private ListView<String> listProximasCitas;
    @FXML private ListView<String> listActividadReciente;
    
    private Timeline refreshTimeline;
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    
    @FXML
    public void initialize() {
        aplicarEfectoHover3D(cardCitas);
        aplicarEfectoHover3D(cardPacientes);
        aplicarEfectoHover3D(cardVentas);
        aplicarEfectoHover3D(cardInventario);
        
        configurarListas();
        startRefresh();
    }
    
    private void cargarDatosReales() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Consultas a BD
                int citasHoy = dashboardDAO.contarCitasHoy();
                int pacientesMes = dashboardDAO.contarPacientesNuevosMes();
                double ventasDia = dashboardDAO.sumarVentasDia();
                int stockBajo = dashboardDAO.contarAlertasStock();
                
                ObservableList<String> proximas = dashboardDAO.obtenerProximasCitas();
                ObservableList<String> actividad = dashboardDAO.obtenerActividadReciente(); // ¡Nuevo método!

                // Actualizar UI en hilo principal
                javafx.application.Platform.runLater(() -> {
                    lblCitasHoy.setText(String.valueOf(citasHoy));
                    lblPacientesNuevos.setText(String.valueOf(pacientesMes));
                    lblVentasDia.setText(String.format("S/. %.2f", ventasDia));
                    
                    lblAlertasStock.setText(String.valueOf(stockBajo));
                    if (stockBajo > 0) lblAlertasStock.setStyle("-fx-text-fill: #EF5350;"); 
                    else lblAlertasStock.setStyle("-fx-text-fill: #66BB6A;");

                    listProximasCitas.setItems(proximas);
                    listActividadReciente.setItems(actividad); // Asignamos la actividad real
                });
                return null;
            }
        };
        
        Thread thread = new Thread(task);
        thread.setDaemon(true); // Importante: que el hilo muera si se cierra la app
        thread.start();
    }

    private void startRefresh() {
        cargarDatosReales(); 
        if (refreshTimeline != null) refreshTimeline.stop();

        // Refrescar cada 15 segundos para dar sensación de tiempo real
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(15), e -> cargarDatosReales()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }
    
    // --- GESTIÓN DE NAVEGACIÓN CENTRAL ---
    
    private void cambiarVistaCentral(String fxmlPath) {
        // 1. Validar que la ruta no sea nula o vacía
        if (fxmlPath == null || fxmlPath.isEmpty()) {
            System.err.println("⚠️ Advertencia: Se intentó cargar una vista con ruta vacía.");
            return;
        }

        // 2. Intentar obtener el recurso (archivo FXML)
        java.net.URL resource = getClass().getResource(fxmlPath);

        // 3. Verificar si el archivo realmente existe
        if (resource == null) {
            System.err.println("❌ ERROR FATAL: No se encontró el archivo FXML en la ruta: " + fxmlPath);
            System.err.println("   -> Verifica que el archivo exista en 'src" + fxmlPath + "'");
            System.err.println("   -> Verifica que hayas hecho 'Clean & Build' en NetBeans.");
            
            // Mostrar mensaje visual en el Dashboard en lugar de romper la app
            Label errorLabel = new Label("⚠️ Vista no encontrada:\n" + fxmlPath);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: CENTER;");
            mainLayout.setCenter(errorLabel);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath)); // ✅ Pasamos el URL (Location) aquí
            Parent nuevaVista = loader.load();
            mainLayout.setCenter(nuevaVista);
        } catch (IOException e) {
            e.printStackTrace();
            
            // Mensaje de error amigable en la interfaz
            Label errorLabel = new Label("❌ Error cargando la vista:\n" + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            mainLayout.setCenter(errorLabel);
        }
    }
    
    // Métodos para el Menú Lateral (Sidebar)
    @FXML private void mostrarDashboard(ActionEvent event) {
        // Recargar el dashboard es básicamente restaurar la vista inicial o resetear el centro
        // En este diseño, el dashboard ES el contenedor padre, pero si tuviéramos un PanelDashboard.fxml separado:
        // cambiarVistaCentral("/view/panels/PanelDashboard.fxml");
        // Como estamos EN el dashboard, podemos recargar los datos:
        cargarDatosReales();
    }

    @FXML
    private void mostrarVistaCitas(ActionEvent event) {
        cambiarVistaCentral("/view/panels/PanelCitas.fxml"); 
    }

    @FXML 
    private void mostrarVistaPacientes(ActionEvent event) {
        // Carga el nuevo FXML
        cambiarVistaCentral("/view/panels/PanelPacientes.fxml");
    }
    
    @FXML 
    private void mostrarVistaClientes(ActionEvent event) {
        // Carga el nuevo FXML
        cambiarVistaCentral("/view/panels/PanelClientes.fxml");
    }

    @FXML 
    private void mostrarVistaInventario(ActionEvent event) {
        // Usa el método seguro que creamos
        cambiarVistaCentral("/view/panels/PanelInventario.fxml"); 
    }

    @FXML private void mostrarVistaPetShop(ActionEvent event) {
        cambiarVistaCentral("/view/panels/PanelPetShop.fxml");
    }
    
    @FXML 
    private void mostrarVistaHistorial(ActionEvent event) {
        // Carga el nuevo FXML
        cambiarVistaCentral("/view/panels/PanelHistorial.fxml");
    }

    @FXML private void mostrarReportes(ActionEvent event) {
        cambiarVistaCentral("/view/panels/PanelReportes.fxml");
    }

    // --- EFECTOS VISUALES Y UTILIDADES ---

    private void aplicarEfectoHover3D(Node node) {
        DropShadow normalShadow = new DropShadow(30, 0, 10, Color.rgb(144, 202, 249, 0.25));
        DropShadow hoverShadow = new DropShadow(40, 0, 15, Color.rgb(144, 202, 249, 0.4));
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), node);
        scaleIn.setToX(1.03); scaleIn.setToY(1.03);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), node);
        scaleOut.setToX(1.0); scaleOut.setToY(1.0);

        node.setOnMouseEntered(e -> { node.setEffect(hoverShadow); scaleIn.playFromStart(); node.setStyle("-fx-cursor: hand;"); });
        node.setOnMouseExited(e -> { node.setEffect(normalShadow); scaleOut.playFromStart(); });
    }
    
    private void configurarListas() {
        listProximasCitas.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); } 
                else { 
                    setText(item); 
                    setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-text-fill: #455A64;"); 
                }
            }
        });

         listActividadReciente.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); } 
                else {
                    setText(item);
                    String color = "#64748B"; // Default gris
                    if(item.contains("📅")) color = "#42A5F5"; // Azul citas
                    if(item.contains("💰")) color = "#66BB6A"; // Verde dinero
                    if(item.contains("🐾")) color = "#EC407A"; // Rosa mascotas
                    
                    setStyle("-fx-font-size: 13px; -fx-padding: 8; -fx-text-fill: " + color + ";");
                }
            }
        });
    }
    
    @FXML
    private void cerrarSesionClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas salir del sistema?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (refreshTimeline != null) refreshTimeline.stop();
            try {
                Stage currentStage = (Stage) mainLayout.getScene().getWindow();
                currentStage.close();
                // Reiniciar Login (Ajustar nombre de clase si es necesario)
                // new LoginAppMain().start(new Stage()); 
                // O usar reflexión como tenías antes si prefieres no importar la clase principal directamente
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}