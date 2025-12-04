package model;

import java.time.LocalDate;

public class HistorialClinico implements Comparable<HistorialClinico> {
    
    private String idHistorial;
    private String idPaciente; // Enlace lógico con Paciente
    private LocalDate fecha;
    private String diagnostico;
    private String tratamiento;
    private String veterinarioCargo;

    public HistorialClinico(String id, String idPaciente, LocalDate fecha, String diagnostico, String tratamiento, String vet) {
        this.idHistorial = id;
        this.idPaciente = idPaciente;
        this.fecha = fecha;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.veterinarioCargo = vet;
    }

    // --- GETTERS ---
    public String getIdHistorial() { return idHistorial; }
    public String getIdPaciente() { return idPaciente; }
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
        return fecha + " - " + diagnostico + " (Dr. " + veterinarioCargo + ")";
    }
}