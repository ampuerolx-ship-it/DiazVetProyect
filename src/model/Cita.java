package model;

import java.time.LocalDateTime;

// Implementamos Comparable para que el AVL sepa cómo ordenar las citas
public class Cita implements Comparable<Cita> {
    
    private String idCita; // Usaremos un ID único
    private Paciente paciente;
    private LocalDateTime fechaHora;
    private String tipoCita; // vacuna, revisión, urgente, etc.

    // Constructor
    public Cita(String idCita, Paciente paciente, LocalDateTime fechaHora, String tipoCita) {
        this.idCita = idCita;
        this.paciente = paciente;
        this.fechaHora = fechaHora;
        this.tipoCita = tipoCita;
    }

    // -----------------------------------------------------------------
    // GETTERS AGREGADOS (Acceso de lectura a la Vista y Controlador)
    // -----------------------------------------------------------------
    
    public String getIdCita() {
        return idCita;
    }
    public Paciente getPaciente() {
        return paciente;
    }
    // Crucial para la visualización de colores en el PanelCitas
    public String getTipoCita() {
        return tipoCita;
    }
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    // --- MÉTODO CLAVE: COMPARETO ---
    /**
     * Define el orden de las citas en el Arbol AVL.
     * Orden primario: fechaHora.
     * Orden secundario (en caso de empate): idCita.
     */
    @Override
    public int compareTo(Cita otraCita) {
        // 1. Comparar por Fecha y Hora
        int resultadoFecha = this.fechaHora.compareTo(otraCita.fechaHora);
        
        // 2. Si las fechas son diferentes, retornar el resultado
        if (resultadoFecha != 0) {
            return resultadoFecha;
        }
        
        // 3. Si las fechas son iguales, desempatar por ID de Cita
        return this.idCita.compareTo(otraCita.idCita);
    }
}