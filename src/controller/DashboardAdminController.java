package controller;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    @FXML
    public void initialize() {
        // 1. Aplicar Animaciones 3D Hover a las tarjetas
        aplicarEfectoHover3D(cardCitas);
        aplicarEfectoHover3D(cardPacientes);
        aplicarEfectoHover3D(cardVentas);
        aplicarEfectoHover3D(cardInventario);

        // 2. Cargar Datos Simulados (Mock Data)
        cargarDatosSimulados();
        
        // 3. Configurar las Listas para que se vean bien
        configurarListas();
    }

    // Método reutilizable para cambiar vistas
    private void cambiarVistaCentral(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent nuevaVista = loader.load();
            mainLayout.setCenter(nuevaVista); // Reemplaza el contenido del centro (el Dashboard) por la nueva vista
        } catch (IOException e) {
            e.printStackTrace();
            // System.err.println("Error cargando la vista: " + fxmlPath);
            mainLayout.setCenter(new javafx.scene.control.Label("ERROR: No se pudo cargar la vista " + fxmlPath));
        }
    }
    
    // Acción para el botón Pet Shop
    @FXML
    private void mostrarVistaPetShop(ActionEvent event) {
        cambiarVistaCentral("/view/panels/PanelPetShop.fxml");
    }

    /**
     * Aplica una transición de escala y eleva la sombra al pasar el mouse.
     */
    private void aplicarEfectoHover3D(Node node) {
        // Sombra original más suave
        DropShadow normalShadow = new DropShadow(30, 0, 10, Color.rgb(144, 202, 249, 0.25));
        // Sombra al hacer hover: más intensa y desplazada hacia abajo (efecto de elevación)
        DropShadow hoverShadow = new DropShadow(40, 0, 15, Color.rgb(144, 202, 249, 0.4));

        // Transición para agrandar ligeramente el nodo
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), node);
        scaleIn.setToX(1.03); // Escalar al 103%
        scaleIn.setToY(1.03);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), node);
        scaleOut.setToX(1.0); // Volver al 100%
        scaleOut.setToY(1.0);

        // Eventos del Mouse
        node.setOnMouseEntered(e -> {
            node.setEffect(hoverShadow); // Cambiar sombra
            scaleIn.playFromStart(); // Reproducir animación de entrada
            node.setStyle("-fx-cursor: hand;"); // Cambiar cursor
        });

        node.setOnMouseExited(e -> {
            node.setEffect(normalShadow); // Restaurar sombra
            scaleOut.playFromStart(); // Reproducir animación de salida
        });
    }

    private void cargarDatosSimulados() {
        // Aquí conectarías con tus DAOs reales más adelante
        lblCitasHoy.setText("24");
        lblPacientesNuevos.setText("52");
        lblVentasDia.setText("S/. 1,850.00");
        lblAlertasStock.setText("5");

        // Datos para próximas citas
        ObservableList<String> citas = FXCollections.observableArrayList(
            "10:30 AM - Firulais (Vacuna) - Dra. Ana",
            "11:00 AM - Michi (Revisión) - Dr. Juan",
            "11:45 AM - Rocco (Rayos X) - Dra. Ana",
            "01:00 PM - Luna (Emergencia) - Dr. Juan"
        );
        listProximasCitas.setItems(citas);

        // Datos para actividad reciente
        ObservableList<String> actividad = FXCollections.observableArrayList(
            "✅ Venta #1024 registrada por Laura (S/ 150.00)",
            "🐾 Nuevo paciente 'Thor' registrado",
            "📦 Stock de 'Croquetas Dog Chow' actualizado (Bajo)",
            "📅 Cita para 'Lola' reagendada para mañana"
        );
        listActividadReciente.setItems(actividad);
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
                     // Podrías añadir iconos aquí dependiendo del texto
                }
            }
        });
        
        // Repetir para la otra lista o crear una factory compartida
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
            
            // 1. Obtener la Stage actual (la ventana del Dashboard)
            Stage dashboardStage = (Stage) mainLayout.getScene().getWindow();
            dashboardStage.close(); // Cerrar la ventana del Dashboard

            // 2. Opcional: Abrir la ventana de LoginAppMain de nuevo
            try {
                // Instanciar la aplicación principal y ejecutarla para iniciar la ventana de login
                // NOTA: Esto requiere que tu LoginAppMain tenga un método estático o público para iniciar la UI.
                // Como práctica estándar, recargaremos el FXML del Login.
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/LoginApp.fxml")); // Usar la ruta de tu FXML de Login
                Parent loginRoot = loader.load();
                
                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(loginRoot));
                loginStage.setTitle("Días Vet - Login");
                loginStage.show();
                
            } catch (IOException e) {
                e.printStackTrace();
                // Si falla la recarga, al menos la sesión se cerró.
            }
        }
    }
}