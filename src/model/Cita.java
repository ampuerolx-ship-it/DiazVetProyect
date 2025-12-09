package model;

import java.time.LocalDateTime;

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

    /**
     * Devuelve la prioridad de la cita para el Triage visual.
     * 1 = Alta (Emergencia) - Rojo
     * 2 = Media (Urgencia/Cirugía) - Naranja/Amarillo
     * 3 = Baja (Control/Vacuna/Normal) - Azul/Verde
     */
    public int getNivelPrioridad() {
        if (tipoCita == null) return 3;
        
        String tipo = tipoCita.toLowerCase();
        
        if (tipo.contains("emergencia") || tipo.contains("atropello") || tipo.contains("grave")) {
            return 1; // Prioridad Alta
        } else if (tipo.contains("urgente") || tipo.contains("cirugía") || tipo.contains("gestación")) {
            return 2; // Prioridad Media
        } else {
            return 3; // Prioridad Baja (Vacunas, Baños, Controles)
        }
    }
    
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
        int resultadoFecha = this.fechaHora.compareTo(otraCita.fechaHora);
        if (resultadoFecha != 0) {
            return resultadoFecha;
        }
        return this.idCita.compareTo(otraCita.idCita);
    }
}