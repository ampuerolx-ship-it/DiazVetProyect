package model;

import java.time.LocalDate;
import javafx.beans.property.*;

public class HistorialClinico implements Comparable<HistorialClinico> {
    
    // NUEVO
    private final IntegerProperty id;
    private final IntegerProperty idMascota;
    private final StringProperty nombreMascota;
    private final StringProperty vacunasAplicadas;
    private final StringProperty vacunasPendientes;
    private final StringProperty ultimaDesparasitacion;
    private final StringProperty ultimaVisita;
    private final StringProperty registroVacunasRuta;
    private LocalDate fecha; // AÑADIDO DEL VIEJO
    private String diagnostico; // AÑADIDO DEL VIEJO
    
    /*VIEJO
    private String idHistorial;
    private String idPaciente; // Enlace lógico con Paciente
    private LocalDate fecha;
    private String diagnostico;
    private String tratamiento;
    private String veterinarioCargo;*/
    
    // NUEVO
    public HistorialClinico() {
        this(0, 0, null, null, null, null, null, null);
    }

    //NUEVO
    public HistorialClinico(int id, int idMascota, String nombreMascota, String vacunasAplicadas, String vacunasPendientes, 
                            String ultimaDesparasitacion, String ultimaVisita, String registroVacunasRuta) {
        this.id = new SimpleIntegerProperty(id);
        this.idMascota = new SimpleIntegerProperty(idMascota);
        this.nombreMascota = new SimpleStringProperty(nombreMascota);
        this.vacunasAplicadas = new SimpleStringProperty(vacunasAplicadas);
        this.vacunasPendientes = new SimpleStringProperty(vacunasPendientes);
        this.ultimaDesparasitacion = new SimpleStringProperty(ultimaDesparasitacion);
        this.ultimaVisita = new SimpleStringProperty(ultimaVisita);
        this.registroVacunasRuta = new SimpleStringProperty(registroVacunasRuta);
        this.fecha = fecha; // AÑADIDO DEL VIEJO
        this.diagnostico = diagnostico; // AÑADIDO DEL VIEJO
    }
    
    /* VIEJO
    public HistorialClinico(String id, String idPaciente, LocalDate fecha, String diagnostico, String tratamiento, String vet) {
        this.idHistorial = id;
        this.idPaciente = idPaciente;
        this.fecha = fecha;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.veterinarioCargo = vet;
    }*/
    
    //NUEVO
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty idMascotaProperty() { return idMascota; }
    public StringProperty nombreMascotaProperty() { return nombreMascota; }
    public StringProperty vacunasAplicadasProperty() { return vacunasAplicadas; }
    public StringProperty vacunasPendientesProperty() { return vacunasPendientes; }
    public StringProperty ultimaDesparasitacionProperty() { return ultimaDesparasitacion; }
    public StringProperty ultimaVisitaProperty() { return ultimaVisita; }
    public StringProperty registroVacunasRutaProperty() { return registroVacunasRuta; }

    // --- Getters & Setters ---
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public int getIdMascota() { return idMascota.get(); }
    public void setIdMascota(int idMascota) { this.idMascota.set(idMascota); }
    public String getNombreMascota() { return nombreMascota.get(); }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota.set(nombreMascota); }
    // ... (otros getters y setters) ...
    public String getVacunasAplicadas() { return vacunasAplicadas.get(); }
    public void setVacunasAplicadas(String vacunasAplicadas) { this.vacunasAplicadas.set(vacunasAplicadas); }
    public String getVacunasPendientes() { return vacunasPendientes.get(); }
    public void setVacunasPendientes(String vacunasPendientes) { this.vacunasPendientes.set(vacunasPendientes); }
    public String getUltimaDesparasitacion() { return ultimaDesparasitacion.get(); }
    public void setUltimaDesparasitacion(String ultimaDesparasitacion) { this.ultimaDesparasitacion.set(ultimaDesparasitacion); }
    public String getUltimaVisita() { return ultimaVisita.get(); }
    public void setUltimaVisita(String ultimaVisita) { this.ultimaVisita.set(ultimaVisita); }
    public String getRegistroVacunasRuta() { return registroVacunasRuta.get(); }
    public void setRegistroVacunasRuta(String registroVacunasRuta) { this.registroVacunasRuta.set(registroVacunasRuta); }

    /* VIEJOS GETTERS
    public String getIdHistorial() { return idHistorial; }
    public String getIdPaciente() { return idPaciente; }*/
    public LocalDate getFecha() { return fecha; }
    
    // --- LÓGICA DE ORDENAMIENTO (Comparable) ---
    @Override
    public int compareTo(HistorialClinico otro) {
        // Ordenamos por FECHA (De más reciente a más antiguo)
        // Multiplicamos por -1 para invertir el orden natural (descendente)
        return this.fecha.compareTo(otro.fecha) * -1;
    }

    @Override
    public String toString() {
        return fecha + " - " + diagnostico + ")";
    }
}