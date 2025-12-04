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

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelCitas extends BorderPane {

    private CitaController controller;
    private YearMonth currentYearMonth;
    private LocalDate selectedDate;
    
    // Contenedor central cambiante
    private StackPane contentArea;
    private ToggleGroup viewToggleGroup;

    public PanelCitas() {
        this.controller = new CitaController();
        this.currentYearMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();

        initUI();
    }

    private void initUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #F5F7FA;");

        // 1. TOP BAR (Título y Navegación)
        HBox topBar = crearBarraSuperior();
        setTop(topBar);

        // 2. AREA CENTRAL (Donde se renderiza Mes/Semana/Día)
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20, 0, 0, 0));
        setCenter(contentArea);

        // Por defecto mostramos MES
        mostrarVistaMes();
    }

    private HBox crearBarraSuperior() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        // Título
        VBox titleBox = new VBox(2);
        Text title = new Text("Calendario de Citas");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setFill(Color.web("#546E7A"));
        
        Text subtitle = new Text("Gestiona y visualiza la agenda de Días Vet");
        subtitle.setFill(Color.GRAY);
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Botones de cambio de vista (Mes / Día)
        viewToggleGroup = new ToggleGroup();
        
        ToggleButton btnMes = new ToggleButton("Mes");
        btnMes.getStyleClass().add("toggle-btn");
        btnMes.setToggleGroup(viewToggleGroup);
        btnMes.setSelected(true);
        btnMes.setOnAction(e -> mostrarVistaMes());

        ToggleButton btnDia = new ToggleButton("Día");
        btnDia.getStyleClass().add("toggle-btn");
        btnDia.setToggleGroup(viewToggleGroup);
        btnDia.setOnAction(e -> mostrarVistaDia());

        HBox controls = new HBox(10, btnMes, btnDia);
        controls.setAlignment(Pos.CENTER);

        header.getChildren().addAll(titleBox, spacer, controls);
        return header;
    }

    // ==========================================
    // LOGICA VISTA MES (Cuadrícula Clásica)
    // ==========================================
    private void mostrarVistaMes() {
        contentArea.getChildren().clear();

        VBox layout = new VBox(10);
        
        // Cabecera del Mes (Ej: "Diciembre 2025")
        HBox navBar = new HBox(15);
        navBar.setAlignment(Pos.CENTER_LEFT);
        Button btnPrev = new Button("<");
        Button btnNext = new Button(">");
        
        String fechaFormateada = currentYearMonth.format(
            DateTimeFormatter.ofPattern("MMMM yyyy")
                .withLocale(new java.util.Locale("es", "ES"))
        );
        Text monthTitle = new Text(fechaFormateada.toUpperCase());
        
        monthTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        btnPrev.setOnAction(e -> { currentYearMonth = currentYearMonth.minusMonths(1); mostrarVistaMes(); });
        btnNext.setOnAction(e -> { currentYearMonth = currentYearMonth.plusMonths(1); mostrarVistaMes(); });
        
        navBar.getChildren().addAll(btnPrev, monthTitle, btnNext);

        // Grilla de días
        GridPane calendarGrid = new GridPane();
        calendarGrid.getStyleClass().add("calendar-grid");
        calendarGrid.setPrefSize(800, 600);
        
        
        // Columnas (Lunes a Domingo)
        String[] diasSemana = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        for (int i = 0; i < 7; i++) {
            Label lbl = new Label(diasSemana[i]);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            lbl.setPadding(new Insets(5));
            calendarGrid.add(lbl, i, 0);
            
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 7);
            calendarGrid.getColumnConstraints().add(col);
        }

        // Llenar días
        LocalDate primerDiaDelMes = currentYearMonth.atDay(1);
        int dayOfWeek = primerDiaDelMes.getDayOfWeek().getValue(); // 1 = Lunes
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int row = 1;
        int col = dayOfWeek - 1; // Ajuste índice 0

        // Obtener citas del mes desde Controller
        List<CitaController.CitaSimulada> citasMes = controller.obtenerCitasDelMes(currentYearMonth.getYear(), currentYearMonth.getMonthValue());

        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = currentYearMonth.atDay(i);
            VBox cell = new VBox(2);
            cell.getStyleClass().add("calendar-cell");
            cell.setMinHeight(80); // Altura de celda

            Label dateLbl = new Label(String.valueOf(i));
            dateLbl.getStyleClass().add("date-label");
            if(date.equals(LocalDate.now())) dateLbl.getStyleClass().add("date-label-today");

            cell.getChildren().add(dateLbl);

            // Agregar citas a la celda visualmente
            for (CitaController.CitaSimulada cita : citasMes) {
                if (cita.fecha.getDayOfMonth() == i) {
                    Label eventTag = new Label(cita.hora + " " + cita.paciente.getNombre());
                    eventTag.getStyleClass().addAll("event-tag", "priority-" + cita.paciente.getNivelPrioridad());
                    eventTag.setMaxWidth(Double.MAX_VALUE);
                    cell.getChildren().add(eventTag);
                }
            }

            calendarGrid.add(cell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
        
        layout.getChildren().addAll(navBar, calendarGrid);
        contentArea.getChildren().add(layout);
    }

    // ==========================================
    // LOGICA VISTA DIA (Timeline Vertical)
    // ==========================================
    private void mostrarVistaDia() {
        contentArea.getChildren().clear();
        
        VBox layout = new VBox(10);
        
        // Navegación día
        HBox navBar = new HBox(15);
        navBar.setAlignment(Pos.CENTER_LEFT);
        Button btnPrev = new Button("<");
        Button btnNext = new Button(">");
        Text dayTitle = new Text(selectedDate.format(DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM").withLocale(new java.util.Locale("es", "ES"))));
        dayTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        btnPrev.setOnAction(e -> { selectedDate = selectedDate.minusDays(1); mostrarVistaDia(); });
        btnNext.setOnAction(e -> { selectedDate = selectedDate.plusDays(1); mostrarVistaDia(); });

        navBar.getChildren().addAll(btnPrev, dayTitle, btnNext);

        // ScrollPane para el Timeline
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        VBox timeline = new VBox(0); // Espacio entre horas
        
        List<CitaController.CitaSimulada> citasDia = controller.obtenerCitasDelDia(selectedDate);

        // Horas de 8:00 a 20:00
        for (int hour = 8; hour <= 20; hour++) {
            HBox hourRow = new HBox(10);
            hourRow.setPadding(new Insets(10));
            hourRow.setStyle("-fx-border-color: #EEEEEE; -fx-border-width: 0 0 1 0;"); // Línea divisoria
            hourRow.setMinHeight(60);

            Label timeLbl = new Label(String.format("%02d:00", hour));
            timeLbl.setMinWidth(50);
            timeLbl.setTextFill(Color.GRAY);

            VBox eventsContainer = new VBox(5);
            
            // Buscar citas en esta hora
            int currentHour = hour;
            citasDia.stream()
                .filter(c -> c.hora.getHour() == currentHour)
                .forEach(c -> {
                    HBox card = new HBox(10);
                    card.setPadding(new Insets(8));
                    card.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");
                    
                    // Indicador de color según prioridad
                    Region colorBar = new Region();
                    colorBar.setMinWidth(4);
                    String color = c.paciente.getNivelPrioridad() == 1 ? "#EF9A9A" : (c.paciente.getNivelPrioridad() == 2 ? "#FFCC80" : "#90CAF9");
                    colorBar.setStyle("-fx-background-color: " + color + ";");

                    VBox info = new VBox(2);
                    Text name = new Text(c.paciente.getNombre() + " (" + c.paciente.getEspecie() + ")");
                    name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                    Text owner = new Text("Dueño: " + c.paciente.getDniDueno());
                    owner.setFill(Color.GRAY);
                    
                    info.getChildren().addAll(name, owner);
                    card.getChildren().addAll(colorBar, info);
                    eventsContainer.getChildren().add(card);
                });

            hourRow.getChildren().addAll(timeLbl, eventsContainer);
            timeline.getChildren().add(hourRow);
        }

        scroll.setContent(timeline);
        layout.getChildren().addAll(navBar, scroll);
        contentArea.getChildren().add(layout);
    }
}