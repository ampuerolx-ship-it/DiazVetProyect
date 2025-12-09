package controller;

import database.dao.DashboardDAO;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.control.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Optional;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class DashboardAdminController {
    
    @FXML private BorderPane mainLayout;

    // Inyección de las tarjetas de estadísticas para animarlas
    @FXML private VBox cardCitas;
    @FXML private VBox cardPacientes;
    @FXML private VBox cardVentas;
    @FXML private VBox cardInventario;

    // Labels de datos
    @FXML private Label lblCitasHoy;
    @FXML private Label lblPacientesNuevos;
    @FXML private Label lblVentasDia;
    @FXML private Label lblAlertasStock;

    // Listas
    @FXML private ListView<String> listProximasCitas;
    @FXML private ListView<String> listActividadReciente;
    
    // Contenedor de accesos rápidos para buscar sus botones hijos
    @FXML private GridPane gridAccesosRapidos; // Necesitarías añadir este fx:id al GridPane en el FXML si quieres animar los botones de acceso rápido automáticamente
    
    private Timeline refreshTimeline;
    // DAO
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    
    @FXML
    public void initialize() {
        // 1. Aplicar Animaciones 3D Hover a las tarjetas
        aplicarEfectoHover3D(cardCitas);
        aplicarEfectoHover3D(cardPacientes);
        aplicarEfectoHover3D(cardVentas);
        aplicarEfectoHover3D(cardInventario);
        
        // 2. Configurar las Listas para que se vean bien
        configurarListas();
        startRefresh();
        // 3. Cargar Datos Reales
        cargarDatosReales();
    }
    
    private void cargarDatosReales() {
        // Usamos un Task de JavaFX para hacer las consultas a BD en otro hilo
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Consultas a BD (son rápidas en SQLite, pero es buena práctica)
                int citasHoy = dashboardDAO.contarCitasHoy();
                int pacientesMes = dashboardDAO.contarPacientesNuevosMes();
                double ventasDia = dashboardDAO.sumarVentasDia();
                int stockBajo = dashboardDAO.contarAlertasStock();
                ObservableList<String> proximas = dashboardDAO.obtenerProximasCitas();

                // Actualizar UI en el hilo principal
                javafx.application.Platform.runLater(() -> {
                    lblCitasHoy.setText(String.valueOf(citasHoy));
                    lblPacientesNuevos.setText(String.valueOf(pacientesMes));
                    lblVentasDia.setText(String.format("S/. %.2f", ventasDia));
                    lblAlertasStock.setText(String.valueOf(stockBajo));
                    
                    // Alertas visuales de color
                    if (stockBajo > 0) lblAlertasStock.setStyle("-fx-text-fill: #EF5350;"); 
                    else lblAlertasStock.setStyle("-fx-text-fill: #66BB6A;");

                    listProximasCitas.setItems(proximas);
                    
                    // Nota: Para actividad reciente, podrías crear una consulta similar
                    // o mantener mensajes estáticos del sistema por ahora.
                    listActividadReciente.getItems().add("✅ Sistema sincronizado con BD");
                });
                return null;
            }
        };
        
        new Thread(task).start();
    }

    private void startRefresh() {
        // Ejecutar inmediatamente al inicio
        cargarDatosReales(); 

        // Configurar el Timeline para refrescar cada 30 segundos
        if (refreshTimeline != null) {
            refreshTimeline.stop(); // Prevenir duplicados si se llama de nuevo
        }

        refreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(30), e -> cargarDatosReales())
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE); // Ciclo infinito
        refreshTimeline.play();
    }
    
    private void cambiarVistaCentral(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent nuevaVista = loader.load();
            mainLayout.setCenter(nuevaVista);
        } catch (IOException e) {
            e.printStackTrace();
            mainLayout.setCenter(new javafx.scene.control.Label("ERROR: No se pudo cargar la vista " + fxmlPath));
        }
    }
    
    @FXML
    private void mostrarVistaPetShop(ActionEvent event) {
        cambiarVistaCentral("/view/panels/PanelPetShop.fxml");
    }

    private void aplicarEfectoHover3D(Node node) {
        DropShadow normalShadow = new DropShadow(30, 0, 10, Color.rgb(144, 202, 249, 0.25));
        DropShadow hoverShadow = new DropShadow(40, 0, 15, Color.rgb(144, 202, 249, 0.4));
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), node);
        scaleIn.setToX(1.03);
        scaleIn.setToY(1.03);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), node);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        node.setOnMouseEntered(e -> {
            node.setEffect(hoverShadow);
            scaleIn.playFromStart(); 
            node.setStyle("-fx-cursor: hand;");
        });

        node.setOnMouseExited(e -> {
            node.setEffect(normalShadow); 
            scaleOut.playFromStart(); 
        });
    }
    
    private void configurarListas() {
        // Clase CSS personalizada para las celdas
        listProximasCitas.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().add("activity-list-cell");
                    setStyle("-fx-font-size: 14px; -fx-padding: 10;");
                }
            }
        });

         listActividadReciente.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                     setStyle("-fx-font-size: 13px; -fx-padding: 8; -fx-text-fill: #64748B;");
                     if(item.contains("✅")) setStyle(getStyle() + "-fx-text-fill: #66BB6A;");
                     if(item.contains("📦")) setStyle(getStyle() + "-fx-text-fill: #FFA726;");
                }
            }
        });
    }
    
    @FXML
    private void cerrarSesionClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cierre de Sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que quieres cerrar la sesión?");

        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (refreshTimeline != null) {
                refreshTimeline.stop(); // ⬅️ Detener el refresh al salir
            }
            try {
                Stage currentStage = (Stage) mainLayout.getScene().getWindow();
                currentStage.close();
                Class<?> loginClass = Class.forName("LoginAppMain");
                javafx.application.Application loginApp = (javafx.application.Application) loginClass.getDeclaredConstructor().newInstance();
                Stage newLoginStage = new Stage();
                loginApp.start(newLoginStage);

            } catch (Exception e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setContentText("No se pudo reiniciar la sesión: " + e.getMessage());
                errorAlert.show();
            }
        }
    }
}