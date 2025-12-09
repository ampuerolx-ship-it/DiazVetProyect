package controller;

import database.dao.HistorialDAO;
import database.dao.MascotaDAO;
import model.HistorialClinico;
import model.Mascotas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import utilidades.GestorArchivos;
import model.Paciente;
import structures.ordenamiento.SortUtils;
import structures.ordenamiento.BusquedaUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javafx.collections.transformation.SortedList;

public class HistorialController {
    
    @FXML private TableView<Mascotas> tablePacientes;
    @FXML private TableColumn<Mascotas, Integer> colIdMascota;
    @FXML private TableColumn<Mascotas, String> colNombreMascota;
    @FXML private TableColumn<Mascotas, String> colDuenoMascota;
    @FXML private TextField txtBuscarPaciente;
    
    @FXML private Label lblTituloHistorial;
    @FXML private DatePicker dateUltimaVisita;
    @FXML private DatePicker dateUltimaDesparasitacion;
    @FXML private TextArea txtVacunasAplicadas;
    @FXML private TextArea txtVacunasPendientes;
    @FXML private Label lblArchivoRuta;
    @FXML private Button btnVerArchivo;
    @FXML private Button btnGuardarHistorial;
    @FXML private Button btnSeleccionarArchivo;
    
    private final HistorialDAO historialDAO = new HistorialDAO();
    private final MascotaDAO mascotaDAO = new MascotaDAO();
    private ObservableList<Mascotas> masterDataPacientes = FXCollections.observableArrayList();
    private HistorialClinico historialActual;
    private File archivoTemporal; 

    private final DateTimeFormatter DB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // private List<HistorialClinico> baseDatosHistorial;

    @FXML
    public void initialize() {
        // 1. Configuración de columnas de pacientes (Usando Mascota.java)
        colIdMascota.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombreMascota.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDuenoMascota.setCellValueFactory(new PropertyValueFactory<>("nombreDueno"));

        // 2. Cargar datos
        cargarDatosPacientes();
        
        // 3. Listener de selección de paciente (Carga el historial al seleccionar)
        tablePacientes.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    cargarHistorial(newValue.getId());
                    btnGuardarHistorial.setDisable(false);
                } else {
                    limpiarFormularioHistorial();
                    btnGuardarHistorial.setDisable(true);
                }
            }
        );
        implementarBusqueda();
        limpiarFormularioHistorial();
    }
    
    private void cargarDatosPacientes() {
        masterDataPacientes.clear();
        masterDataPacientes.addAll(mascotaDAO.obtenerTodos());
    }
    
    private void implementarBusqueda() {
        FilteredList<Mascotas> filteredData = new FilteredList<>(masterDataPacientes, p -> true);

        txtBuscarPaciente.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(mascota -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                try {
                    int idFilter = Integer.parseInt(newValue);
                    if (mascota.getId() == idFilter) return true;
                } catch (NumberFormatException ignored) {}
                
                if (mascota.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                if (mascota.getNombreDueno().toLowerCase().contains(lowerCaseFilter)) return true;
                
                return false;
            });
        });
        SortedList<Mascotas> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablePacientes.comparatorProperty());
        tablePacientes.setItems(sortedData);
    }
        
    private void cargarHistorial(int idMascota) {
        Mascotas paciente = tablePacientes.getSelectionModel().getSelectedItem();
        lblTituloHistorial.setText("Historial de: " + paciente.getNombre() + " (ID: " + idMascota + ")");
        
        historialActual = historialDAO.obtenerPorIdMascota(idMascota);

        if (historialActual == null) {
            historialActual = new HistorialClinico();
            historialActual.setIdMascota(idMascota);
            historialActual.setNombreMascota(paciente.getNombre());
            limpiarCamposFormulario(); 
            // Opcional: mostrarAlerta(Alert.AlertType.INFORMATION, "Nuevo Historial", "No existe un historial...");
        } else {
            // Llenar campos con datos existentes
            try {
                if(historialActual.getUltimaVisita() != null) dateUltimaVisita.setValue(LocalDate.parse(historialActual.getUltimaVisita(), DB_DATE_FORMAT));
            } catch (Exception e) { dateUltimaVisita.setValue(null); }
            
            try {
                if(historialActual.getUltimaDesparasitacion() != null) dateUltimaDesparasitacion.setValue(LocalDate.parse(historialActual.getUltimaDesparasitacion(), DB_DATE_FORMAT));
            } catch (Exception e) { dateUltimaDesparasitacion.setValue(null); }
            
            txtVacunasAplicadas.setText(historialActual.getVacunasAplicadas());
            txtVacunasPendientes.setText(historialActual.getVacunasPendientes());
            
            String ruta = historialActual.getRegistroVacunasRuta();
            if (ruta != null && !ruta.isEmpty()) {
                lblArchivoRuta.setText(ruta.substring(ruta.lastIndexOf('/') + 1));
                btnVerArchivo.setDisable(false);
            } else {
                lblArchivoRuta.setText("Ningún archivo cargado.");
                btnVerArchivo.setDisable(true);
            }
        }
        archivoTemporal = null; 
    }
    
    private void limpiarFormularioHistorial() {
        lblTituloHistorial.setText("Historial de: [Seleccione un paciente]");
        historialActual = null;
        limpiarCamposFormulario();
    }
    
    private void limpiarCamposFormulario() {
        dateUltimaVisita.setValue(null);
        dateUltimaDesparasitacion.setValue(null);
        txtVacunasAplicadas.setText("");
        txtVacunasPendientes.setText("");
        lblArchivoRuta.setText("Ningún archivo seleccionado.");
        btnVerArchivo.setDisable(true);
    }
    
    // --- MANEJO DE ARCHIVOS ---
    @FXML
    private void handleSeleccionarArchivo() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.jpg", "*.png", "*.jpeg"));
        File file = fc.showOpenDialog(btnSeleccionarArchivo.getScene().getWindow());
        
        if (file != null) {
            archivoTemporal = file;
            lblArchivoRuta.setText(file.getName() + " (Pendiente de guardar)");
            btnVerArchivo.setDisable(true); 
        }
    }
    
    @FXML
    private void handleVerArchivo() {
        if (historialActual == null) return;
        
        String ruta = historialActual.getRegistroVacunasRuta();
        if (ruta != null && !ruta.isEmpty()) {
            // Lógica robusta para abrir el archivo en el sistema operativo
            try {
                File fileToOpen = new File(ruta);
                if (fileToOpen.exists() && java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(fileToOpen);
                } else {
                     mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se encontró el archivo o la función de abrir no está soportada.");
                }
            } catch (IOException e) {
                 mostrarAlerta(Alert.AlertType.ERROR, "Error de Sistema", "No se pudo abrir el archivo: " + e.getMessage());
            }
        }
    }

    // --- ACCIÓN CRUD (Upsert) ---
    @FXML
    private void handleGuardar() {
        if (historialActual == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un paciente.");
            return;
        }

        // 1. Manejo del Archivo (Guardar si hay un archivo temporal)
        String nuevaRutaArchivo = historialActual.getRegistroVacunasRuta();
        if (archivoTemporal != null) {
            nuevaRutaArchivo = GestorArchivos.guardarArchivo(archivoTemporal, "vacunas");
            if (nuevaRutaArchivo == null) {
                 mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo guardar el archivo.");
                 return;
            }
        }
        
        // 2. Construir/Actualizar Historial
        historialActual.setVacunasAplicadas(txtVacunasAplicadas.getText());
        historialActual.setVacunasPendientes(txtVacunasPendientes.getText());
        historialActual.setRegistroVacunasRuta(nuevaRutaArchivo);
        
        // Conversión de fechas a String (YYYY-MM-DD)
        historialActual.setUltimaVisita(dateUltimaVisita.getValue() != null ? dateUltimaVisita.getValue().format(DB_DATE_FORMAT) : null);
        historialActual.setUltimaDesparasitacion(dateUltimaDesparasitacion.getValue() != null ? dateUltimaDesparasitacion.getValue().format(DB_DATE_FORMAT) : null);

        // 3. Guardar en BD
        boolean exito = historialDAO.guardar(historialActual);

        if (exito) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Historial guardado correctamente.");
            cargarHistorial(historialActual.getIdMascota()); 
            archivoTemporal = null;
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudo guardar el historial.");
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

    /**
     * USUARIO (DUEÑO): Solo ve lo de sus mascotas.
    public List<HistorialClinico> obtenerHistorialUsuario(String dniDueno, List<Paciente> listaPacientes) {
        // 1. Identificar IDs de mascotas de este dueño
        List<String> idsMascotasDueno = listaPacientes.stream()
                .filter(p -> p.getDniDueno().equals(dniDueno))
                .map(p -> p.getNombre()) // O getId() si Paciente tiene ID
                .collect(Collectors.toList());

        // 2. Filtrar historiales que coincidan con esas mascotas
        List<HistorialClinico> historialFiltrado = new ArrayList<>();
        for (HistorialClinico h : baseDatosHistorial) {
            if (idsMascotasDueno.contains(h.getIdPaciente())) {
                historialFiltrado.add(h);
            }
        }

        // 3. Ordenar para presentación visual (MergeSort es muy estable para reportes)
        SortUtils.mergeSort(historialFiltrado);
        
        return historialFiltrado;*/
}