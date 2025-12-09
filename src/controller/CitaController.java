package controller;

import database.dao.CitaDAO;
import model.Cita;
import model.Mascotas;
import structures.lineales.ColaPrioridad; 
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.TableView;

public class CitaController {

    // Formulario
    @FXML private Label lblFormTitle;
    @FXML private ComboBox<Mascotas> cmbMascota;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtMinuto;
    @FXML private ComboBox<String> cmbPrioridad;
    @FXML private TextArea txtMotivo;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardar;
    
    // Lista de Citas
    @FXML private DatePicker dateSelector;
    @FXML private VBox listCitasDia; 

    private final CitaDAO citaDAO = new CitaDAO();
    private Cita citaSeleccionada;
    private ObservableList<Mascotas> mascotasDisponibles;
    
    // 🎯 ESTRUCTURA DE DATOS PRINCIPAL: Cola de Prioridad
    private ColaPrioridad<Cita> colaCitasDia; 

    @FXML
    public void initialize() {
        // 1. Inicialización de datos
        // NOTA: Se mantiene model.Mascotas para compatibilidad con la estructura de tu proyecto.
        mascotasDisponibles = citaDAO.obtenerTodasMascotas();
        
        // 2. Configuración de ComboBox de Mascota
        cmbMascota.setItems(mascotasDisponibles);
        cmbMascota.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Mascotas m) {
                return (m != null) ? m.getNombre() + " (Dueño: " + m.getNombreDueno() + ")" : "";
            }
            @Override
            public Mascotas fromString(String string) { return null; }
        });
        
        // 3. Configuración de Prioridad
        cmbPrioridad.getItems().addAll("1 - Urgente", "2 - Media", "3 - Baja");
        cmbPrioridad.getSelectionModel().selectFirst();
        
        // 4. Inicialización de Fecha y Hora
        dateSelector.setValue(LocalDate.now());
        dateFecha.setValue(LocalDate.now());
        
        // 5. Configuración de validación numérica de hora/minuto
        configurarValidacionNumerica(txtHora, 23);
        configurarValidacionNumerica(txtMinuto, 59);

        // 6. Carga inicial de citas del día
        cargarCitasDia(LocalDate.now());
        handleLimpiar();
    }
    
    // --- LÓGICA DE CARGA Y VISUALIZACIÓN DE LA COLA ---
    
    @FXML
    private void handleFechaSeleccionada() {
        cargarCitasDia(dateSelector.getValue());
        handleLimpiar();
    }
    
    private void cargarCitasDia(LocalDate fecha) {
        // 1. Obtener la lista de citas del día desde la BD
        ObservableList<Cita> citasDB = citaDAO.obtenerPorFecha(fecha);
        
        // 2. Llenar la Cola de Prioridad
        colaCitasDia = new ColaPrioridad<>(); 
        for (Cita cita : citasDB) {
            colaCitasDia.encolar(cita, cita.getNivelPrioridad()); 
        }
        
        // 3. Renderizar la cola en el VBox
        listCitasDia.getChildren().clear();
        
        if (citasDB.isEmpty()) {
            listCitasDia.getChildren().add(new Label("No hay citas programadas para este día."));
            return;
        }

        List<Cita> citasPriorizadas = colaCitasDia.obtenerContenidoOrdenado(); 
        
        for (Cita cita : citasPriorizadas) {
            listCitasDia.getChildren().add(crearCardCita(cita));
        }
    }
    
    private HBox crearCardCita(Cita cita) {
        // Implementación de tarjeta visual de cita
        HBox card = new HBox(5);
        card.getStyleClass().add("card-3d");
        card.setStyle("-fx-cursor: hand; -fx-padding: 10; -fx-background-color: white;");
        
        // Barra de prioridad 
        VBox barraColor = new VBox();
        barraColor.setPrefWidth(5);
        String colorHex;
        switch (cita.getNivelPrioridad()) {
            case 1: colorHex = "#EF5350"; break; // Alta (Rojo)
            case 2: colorHex = "#FFA726"; break; // Media (Naranja)
            default: colorHex = "#66BB6A"; break; // Baja (Verde)
        }
        barraColor.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 5 0 0 5;");
        
        // Contenido
        VBox contenido = new VBox(3);
        contenido.setPadding(new Insets(0, 5, 0, 5));
        
        Label lblHora = new Label(cita.getFechaHora().toLocalTime().toString().substring(0, 5));
        lblHora.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #42A5F5;");
        
        Label lblPaciente = new Label(cita.getNombrePaciente() + " (Dueño: " + cita.getNombreCliente() + ")");
        Label lblMotivo = new Label("Motivo: " + cita.getMotivo());
        lblMotivo.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        contenido.getChildren().addAll(lblHora, lblPaciente, lblMotivo);
        
        card.getChildren().addAll(barraColor, contenido);
        
        // Listener para editar
        card.setOnMouseClicked(e -> seleccionarCitaParaEditar(cita));
        
        return card;
    }
    
    private void seleccionarCitaParaEditar(Cita cita) {
        this.citaSeleccionada = cita;
        lblFormTitle.setText("Editar Cita ID: " + cita.getId());
        
        // Llenar formulario
        cmbMascota.getSelectionModel().select(
            mascotasDisponibles.stream()
                .filter(m -> m.getId() == cita.getIdMascota())
                .findFirst().orElse(null)
        );
        dateFecha.setValue(cita.getFechaHora().toLocalDate());
        txtHora.setText(String.format("%02d", cita.getFechaHora().getHour()));
        txtMinuto.setText(String.format("%02d", cita.getFechaHora().getMinute()));
        cmbPrioridad.getSelectionModel().select(cita.getNivelPrioridad() - 1); // 1-Alta -> índice 0
        txtMotivo.setText(cita.getMotivo());
        
        btnEliminar.setDisable(false);
        btnGuardar.setText("Actualizar Cita");
    }
    
    // --- ACCIONES CRUD ---
    
    @FXML
    private void handleLimpiar() {
        citaSeleccionada = null;
        lblFormTitle.setText("Agendar Nueva Cita");
        cmbMascota.getSelectionModel().clearSelection();
        dateFecha.setValue(LocalDate.now());
        txtHora.setText("");
        txtMinuto.setText("");
        cmbPrioridad.getSelectionModel().selectFirst();
        txtMotivo.setText("");
        btnEliminar.setDisable(true);
        btnGuardar.setText("Agendar Cita");
    }
    
    @FXML
    private void handleGuardar() {
        Cita nuevaCita = getCitaDelFormulario();
        if (nuevaCita == null) return;
        
        boolean exito;
        if (citaSeleccionada != null) {
            nuevaCita.setId(citaSeleccionada.getId());
            exito = citaDAO.actualizar(nuevaCita);
        } else {
            exito = citaDAO.insertar(nuevaCita);
        }
        
        mostrarAlerta(exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, 
                     exito ? "Éxito" : "Error de BD", 
                     exito ? "Cita guardada correctamente." : "No se pudo guardar la cita.");
        
        cargarCitasDia(dateSelector.getValue());
        handleLimpiar();
    }
    
    @FXML
    private void handleEliminar() {
        if (citaSeleccionada == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("Eliminar Cita de " + citaSeleccionada.getNombrePaciente());
        alert.setContentText("¿Está seguro de que desea eliminar esta cita?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (citaDAO.eliminar(citaSeleccionada.getId())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cita eliminada.");
                cargarCitasDia(dateSelector.getValue());
                handleLimpiar();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo eliminar la cita.");
            }
        }
    }
    
    // --- UTILIDADES Y VALIDACIÓN ---
    
    private Cita getCitaDelFormulario() {
        if (!validarCampos()) return null;
        
        Mascotas mascota = cmbMascota.getValue();
        LocalDate fecha = dateFecha.getValue();
        int hora = Integer.parseInt(txtHora.getText());
        int minuto = Integer.parseInt(txtMinuto.getText());
        String motivo = txtMotivo.getText().trim();
        int prioridad = cmbPrioridad.getSelectionModel().getSelectedIndex() + 1;

        LocalDateTime fechaHora = LocalDateTime.of(fecha, LocalTime.of(hora, minuto));
        
        // Construcción de la Cita (ID es 0 si es nueva)
        return new Cita(
            0,
            fechaHora.toString(), 
            motivo,
            mascota.getDniCliente(), 
            mascota.getId(),
            mascota.getNombre(),
            mascota.getNombreDueno(),
            prioridad
        );
    }
    
    /**
     * ⭐ MÉTODO CORREGIDO: Incluye validación de rango numérico para la hora y minuto.
     */
    private boolean validarCampos() {
        String mensajeError = "";
        
        if (cmbMascota.getValue() == null) mensajeError += "Debe seleccionar un paciente.\n";
        
        // Chequeo de fecha
        LocalDate fechaSeleccionada = dateFecha.getValue();
        if (fechaSeleccionada == null) {
            mensajeError += "Debe seleccionar una fecha.\n";
        } else if (fechaSeleccionada.isBefore(LocalDate.now())) {
            mensajeError += "La fecha no puede ser pasada.\n";
        }

        if (txtMotivo.getText().trim().isEmpty()) mensajeError += "El motivo no puede estar vacío.\n";
        
        // Validación de Hora y Minuto: Verificación de formato y rango
        if (txtHora.getText().isEmpty() || txtMinuto.getText().isEmpty()) {
            mensajeError += "Debe especificar la hora y minutos.\n";
        } else {
            try {
                int hora = Integer.parseInt(txtHora.getText());
                int minuto = Integer.parseInt(txtMinuto.getText());
                
                if (hora < 0 || hora > 23 || minuto < 0 || minuto > 59) {
                    mensajeError += "La hora o minuto están fuera del rango válido (00:00 - 23:59).\n";
                }
            } catch (NumberFormatException e) {
                mensajeError += "La hora y minuto deben ser números enteros válidos.\n";
            }
        }
        
        if (mensajeError.isEmpty()) return true;
        
        mostrarAlerta(Alert.AlertType.WARNING, "Campos Inválidos", "Por favor, corrige los siguientes errores:\n" + mensajeError);
        return false;
    }
    
    private void configurarValidacionNumerica(TextField textField, int max) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) return;
            try {
                int value = Integer.parseInt(newValue);
                if (value < 0 || value > max) {
                    textField.setText(oldValue);
                }
            } catch (NumberFormatException e) {
                textField.setText(oldValue);
            }
        });
    }
    
    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}