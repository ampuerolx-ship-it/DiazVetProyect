package model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Implementa Comparable para ordenar por fecha/hora y prioridad (para la Cola de Prioridad)
public class Cita implements Comparable<Cita> {
    
    private final IntegerProperty id;
    private final StringProperty fechaHoraRaw; // String en BD (YYYY-MM-DDTHH:MM)
    private final StringProperty motivo;
    private final StringProperty dniCliente;
    private final IntegerProperty idMascota;
    private final StringProperty nombrePaciente; // Join field
    private final StringProperty nombreCliente; // Join field
    private final IntegerProperty nivelPrioridad; // 1: Alta, 2: Media, 3: Baja (Crucial para la Cola)

    // Formato de la BD
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Cita() {
        this(0, null, null, null, 0, null, null, 3);
    }

    public Cita(int id, String fechaHoraRaw, String motivo, String dniCliente, int idMascota, 
                String nombrePaciente, String nombreCliente, int nivelPrioridad) {
        this.id = new SimpleIntegerProperty(id);
        this.fechaHoraRaw = new SimpleStringProperty(fechaHoraRaw);
        this.motivo = new SimpleStringProperty(motivo);
        this.dniCliente = new SimpleStringProperty(dniCliente);
        this.idMascota = new SimpleIntegerProperty(idMascota);
        this.nombrePaciente = new SimpleStringProperty(nombrePaciente);
        this.nombreCliente = new SimpleStringProperty(nombreCliente);
        this.nivelPrioridad = new SimpleIntegerProperty(nivelPrioridad);
    }

    // --- Properties ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty fechaHoraRawProperty() { return fechaHoraRaw; }
    public StringProperty motivoProperty() { return motivo; }
    public IntegerProperty nivelPrioridadProperty() { return nivelPrioridad; }
    public StringProperty nombrePacienteProperty() { return nombrePaciente; }
    public StringProperty nombreClienteProperty() { return nombreCliente; }
    
    // --- Getters & Setters ---
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public String getMotivo() { return motivo.get(); }
    public void setMotivo(String motivo) { this.motivo.set(motivo); }
    public int getNivelPrioridad() { return nivelPrioridad.get(); }
    public void setNivelPrioridad(int nivelPrioridad) { this.nivelPrioridad.set(nivelPrioridad); }
    public int getIdMascota() { return idMascota.get(); }
    public String getDniCliente() { return dniCliente.get(); }
    public String getNombrePaciente() { return nombrePaciente.get(); }
    public String getNombreCliente() { return nombreCliente.get(); }

    public LocalDateTime getFechaHora() {
        try {
            String raw = fechaHoraRaw.get().replace('T', ' ');
            return LocalDateTime.parse(raw.substring(0, 16), FORMATTER); 
        } catch (Exception e) {
            System.err.println("Error parseando fecha/hora: " + fechaHoraRaw.get());
            return LocalDateTime.MIN;
        }
    }

    /**
     * Lógica de Prioridad para la Cola:
     * 1. Citas más tempranas (menor fecha/hora) tienen mayor prioridad (salen primero).
     * 2. Si son a la misma hora, menor nivel (1) tiene mayor prioridad.
     */
    @Override
    public int compareTo(Cita otra) {
        int fechaComparacion = this.getFechaHora().compareTo(otra.getFechaHora());
        
        if (fechaComparacion != 0) {
            return fechaComparacion; // Ordena por hora (ascendente)
        } else {
            // Si horas son iguales, prioriza el nivel (1 es más prioritario que 3)
            return Integer.compare(this.getNivelPrioridad(), otra.getNivelPrioridad());
        }
    }
}