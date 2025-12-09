package view.panels;

import controller.CitaController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Cita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PanelCitas extends BorderPane {

    private final CitaController controller;
    private LocalDate fechaSeleccionada;
    private Label lblFechaTitulo;
    private VBox contenedorHorario; 

    public PanelCitas() {
        this.controller = new CitaController();
        this.fechaSeleccionada = LocalDate.now();
        initUI();
        cargarCitas();
    }

    private void initUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #F5F7FA;");
        setTop(crearBarraSuperior());
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        contenedorHorario = new VBox(10);
        contenedorHorario.setPadding(new Insets(10, 0, 10, 0));
        scrollPane.setContent(contenedorHorario);
        setCenter(scrollPane);
    }

    private HBox crearBarraSuperior() {
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 20, 0));

        Button btnHoy = new Button("Hoy");
        btnHoy.setStyle("-fx-background-color: white; -fx-text-fill: #546E7A; -fx-border-color: #CFD8DC; -fx-cursor: hand;");
        btnHoy.setOnAction(e -> { fechaSeleccionada = LocalDate.now(); actualizarTitulo(); cargarCitas(); });

        Button btnAnterior = new Button("◀");
        btnAnterior.setOnAction(e -> { fechaSeleccionada = fechaSeleccionada.minusDays(1); actualizarTitulo(); cargarCitas(); });
        
        Button btnSiguiente = new Button("▶");
        btnSiguiente.setOnAction(e -> { fechaSeleccionada = fechaSeleccionada.plusDays(1); actualizarTitulo(); cargarCitas(); });

        lblFechaTitulo = new Label();
        lblFechaTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #37474F;");
        actualizarTitulo();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnNuevaCita = new Button("+ Nueva Cita");
        btnNuevaCita.setStyle("-fx-background-color: #42A5F5; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        topBar.getChildren().addAll(btnHoy, btnAnterior, lblFechaTitulo, btnSiguiente, spacer, btnNuevaCita);
        return topBar;
    }

    private void actualizarTitulo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", new Locale("es", "ES"));
        String texto = fechaSeleccionada.format(formatter);
        lblFechaTitulo.setText(texto.substring(0, 1).toUpperCase() + texto.substring(1));
    }

    public void cargarCitas() {
        contenedorHorario.getChildren().clear();
        List<Cita> citasDelDia = controller.obtenerCitasDelDia(fechaSeleccionada);

        LocalTime horaInicio = LocalTime.of(8, 0);
        LocalTime horaFin = LocalTime.of(20, 0);

        while (horaInicio.isBefore(horaFin)) {
            HBox filaHora = new HBox(15);
            filaHora.setAlignment(Pos.TOP_LEFT);
            filaHora.setMinHeight(70); 
            
            Label lblHora = new Label(horaInicio.format(DateTimeFormatter.ofPattern("hh:mm a")));
            lblHora.setMinWidth(70);
            lblHora.setAlignment(Pos.TOP_RIGHT);
            lblHora.setStyle("-fx-text-fill: #90A4AE; -fx-font-size: 12px; -fx-padding: 5 0 0 0;");

            VBox contenedorEventos = new VBox(5);
            HBox.setHgrow(contenedorEventos, Priority.ALWAYS);
            final int horaActual = horaInicio.getHour();
            
            citasDelDia.stream()
                .filter(c -> c.getFechaHora().getHour() == horaActual)
                .forEach(cita -> contenedorEventos.getChildren().add(crearTarjetaCita(cita)));

            if (contenedorEventos.getChildren().isEmpty()) {
                Region linea = new Region();
                linea.setMaxHeight(1);
                linea.setStyle("-fx-background-color: #ECEFF1;");
                HBox.setHgrow(linea, Priority.ALWAYS);
                VBox ph = new VBox(linea);
                ph.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(ph, Priority.ALWAYS);
                filaHora.getChildren().addAll(lblHora, ph);
            } else {
                filaHora.getChildren().addAll(lblHora, contenedorEventos);
            }
            contenedorHorario.getChildren().add(filaHora);
            horaInicio = horaInicio.plusHours(1);
        }
    }

    private HBox crearTarjetaCita(Cita cita) {
        HBox tarjeta = new HBox(0);
        tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 3, 0, 0, 1);");
        
        // 1. BARRA DE PRIORIDAD (Usando el nuevo método getNivelPrioridad)
        Region barraColor = new Region();
        barraColor.setMinWidth(5);
        
        int nivel = cita.getNivelPrioridad(); // ⬅️ AQUÍ USAMOS TU REQUERIMIENTO
        String colorHex;
        
        switch (nivel) {
            case 1: colorHex = "#EF5350"; break; // Alta (Rojo)
            case 2: colorHex = "#FFA726"; break; // Media (Naranja)
            default: colorHex = "#66BB6A"; break; // Baja (Verde)
        }
        barraColor.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 5 0 0 5;");

        // 2. Contenido
        VBox contenido = new VBox(3);
        contenido.setPadding(new Insets(8, 12, 8, 12));
        
        Text titulo = new Text(cita.getPaciente().getNombre() + " (" + cita.getPaciente().getEspecie() + ")");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        titulo.setFill(Color.web("#37474F"));
        
        // Usamos getDuenio() (o getDniDueno() si lo cambiaste en Paciente)
        // Nota: Si en Paciente el método es getDniDueno(), cámbialo aquí.
        // Asumo getDuenio() porque en el DAO estábamos guardando el nombre ahí.
        String textoDetalle = "Dueño: " + cita.getPaciente().getDniDueno() + " • " + cita.getTipoCita();
        
        Label detalle = new Label(textoDetalle);
        detalle.setTextFill(Color.web("#78909C"));
        detalle.setFont(Font.font("System", 12));
        
        contenido.getChildren().addAll(titulo, detalle);
        tarjeta.getChildren().addAll(barraColor, contenido);
        return tarjeta;
    }
}