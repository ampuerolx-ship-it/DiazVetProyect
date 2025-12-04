package view.panels;

import controller.HistorialController;
import model.HistorialClinico;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelHistorial extends BorderPane {

    private HistorialController controller;
    private VBox listaContenedor; // Aquí van las tarjetas
    private TextField campoBusqueda;

    public PanelHistorial() {
        this.controller = new HistorialController();
        cargarDatosFicticios(); // Solo para probar visualmente ahora
        initUI();
        refrescarLista(controller.getTodoElHistorial());
    }

    // Método temporal para tener algo que mostrar
    private void cargarDatosFicticios() {
        controller.agregarRegistro(new HistorialClinico("H001", "Bobby", LocalDate.now(), "Gastroenteritis", "Suero + Antibiótico", "Perez"));
        controller.agregarRegistro(new HistorialClinico("H002", "Michi", LocalDate.now().minusDays(2), "Control Sano", "Vacuna Triple", "Gomez"));
        controller.agregarRegistro(new HistorialClinico("H003", "Rex", LocalDate.now().minusDays(10), "Fractura Pata", "Cirugía + Yeso", "Perez"));
        controller.agregarRegistro(new HistorialClinico("H004", "Luna", LocalDate.now().minusDays(1), "Alergia Piel", "Corticoides", "Gomez"));
    }

    private void initUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #F5F7FA;");

        // 1. ENCABEZADO (Título + Buscador)
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        VBox titulos = new VBox(5);
        Text titulo = new Text("Historial Clínico");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titulo.setFill(Color.web("#546E7A"));
        
        Text subTitulo = new Text("Gestión de expedientes y diagnósticos");
        subTitulo.setFill(Color.GRAY);
        titulos.getChildren().addAll(titulo, subTitulo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Barra de Búsqueda
        campoBusqueda = new TextField();
        campoBusqueda.setPromptText("Buscar por ID, Mascota o Diagnóstico...");
        campoBusqueda.setPrefWidth(300);
        campoBusqueda.getStyleClass().add("search-bar"); // Estilo redondeado
        
        // Lógica de filtrado en tiempo real
        campoBusqueda.setOnKeyReleased(e -> filtrarLista(campoBusqueda.getText()));

        Button btnNuevo = new Button("+ Nuevo Registro");
        btnNuevo.getStyleClass().add("btn-primary");
        // Acción: Aquí abrirías un diálogo para agregar (usando controller.agregarRegistro)

        header.getChildren().addAll(titulos, spacer, campoBusqueda, btnNuevo);
        setTop(header);

        // 2. LISTA DE TARJETAS (Centro)
        listaContenedor = new VBox(10); // 10px de separación entre tarjetas
        listaContenedor.setPadding(new Insets(10));
        
        ScrollPane scrollPane = new ScrollPane(listaContenedor);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        setCenter(scrollPane);
    }

    /**
     * Convierte la lista de objetos en Tarjetas visuales
     */
    private void refrescarLista(List<HistorialClinico> lista) {
        listaContenedor.getChildren().clear();

        if (lista.isEmpty()) {
            Label empty = new Label("No se encontraron registros.");
            empty.setTextFill(Color.GRAY);
            empty.setPadding(new Insets(20));
            listaContenedor.getChildren().add(empty);
            return;
        }

        for (HistorialClinico h : lista) {
            listaContenedor.getChildren().add(crearTarjeta(h));
        }
    }

    private HBox crearTarjeta(HistorialClinico h) {
        HBox card = new HBox(15);
        card.getStyleClass().add("history-card");
        card.setAlignment(Pos.CENTER_LEFT);

        // Icono o Avatar (Círculo con inicial)
        StackPane avatar = new StackPane();
        javafx.scene.shape.Circle circulo = new javafx.scene.shape.Circle(25, Color.web("#E3F2FD"));
        Text inicial = new Text(h.getIdPaciente().substring(0, 1)); // Primera letra nombre mascota
        inicial.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        inicial.setFill(Color.web("#1976D2"));
        avatar.getChildren().addAll(circulo, inicial);

        // Información Central
        VBox info = new VBox(5);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        Text nombreMascota = new Text(h.getIdPaciente());
        nombreMascota.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        nombreMascota.setFill(Color.web("#37474F"));

        Text diagnostico = new Text(h.toString()); // Usamos el toString del modelo
        diagnostico.setFill(Color.GRAY);

        info.getChildren().addAll(nombreMascota, diagnostico);

        // Fecha y Estado (Derecha)
        VBox metaData = new VBox(5);
        metaData.setAlignment(Pos.CENTER_RIGHT);

        Label lblFecha = new Label(h.getFecha().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        lblFecha.setTextFill(Color.GRAY);
        lblFecha.setStyle("-fx-font-size: 12px;");

        Label badge = new Label("Completado");
        badge.getStyleClass().addAll("badge", "badge-completed"); // CSS definido arriba
        
        // Lógica visual simple para simular estados
        if (h.toString().contains("Urgente") || h.toString().contains("Fractura")) {
            badge.setText("Crítico");
            badge.getStyleClass().remove("badge-completed");
            badge.getStyleClass().add("badge-urgent");
        }

        metaData.getChildren().addAll(lblFecha, badge);

        card.getChildren().addAll(avatar, info, metaData);
        return card;
    }

    /**
     * Filtro simple para la búsqueda
     */
    private void filtrarLista(String texto) {
        if (texto == null || texto.isEmpty()) {
            refrescarLista(controller.getTodoElHistorial());
            return;
        }

        // Usamos streams para filtrar visualmente (Búsqueda Secuencial visual)
        // En un sistema real gigante, aquí llamarías a BusquedaUtils.busquedaBinaria si buscaras por ID exacto.
        List<HistorialClinico> filtrada = controller.getTodoElHistorial().stream()
                .filter(h -> h.getIdPaciente().toLowerCase().contains(texto.toLowerCase()) || 
                             h.toString().toLowerCase().contains(texto.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
        
        refrescarLista(filtrada);
    }
}