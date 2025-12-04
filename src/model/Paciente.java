package model;

import java.time.LocalDateTime;

public class Paciente {
    // Atributos del paciente
    private String nombre;
    private String especie; // Perro, Gato, etc.
    private String raza;
    private int edad;
    
    // Atributos del dueño
    private String nombreDueno;
    private String dniDueno;
    
    // Atributos de gestión (Estructuras de Datos)
    private int nivelPrioridad; // 1 = Emergencia (Máxima), 2 = Urgencia, 3 = Normal
    private LocalDateTime horaLlegada;
    private String motivoConsulta;

    // Constructor
    public Paciente(String nombre, String especie, String nombreDueno, String dniDueno, String motivo, int nivelPrioridad) {
        this.nombre = nombre;
        this.especie = especie;
        this.nombreDueno = nombreDueno;
        this.dniDueno = dniDueno;
        this.motivoConsulta = motivo;
        this.nivelPrioridad = nivelPrioridad;
        this.horaLlegada = LocalDateTime.now(); // Se marca la hora exacta al crearse
    }

    // --- GETTERS Y SETTERS NECESARIOS ---

    public String getNombre() { return nombre; }
    public String getEspecie() { return especie; }
    public String getDniDueno() { return dniDueno; }
    public int getNivelPrioridad() { return nivelPrioridad; }
    public LocalDateTime getHoraLlegada() { return horaLlegada; }
    
    // Método para mostrar información legible (útil para pruebas en consola)
    @Override
    public String toString() {
        String tipo = "";
        switch (nivelPrioridad) {
            case 1: tipo = "[EMERGENCIA]"; break;
            case 2: tipo = "[URGENCIA]"; break;
            default: tipo = "[NORMAL]"; break;
        }
        return tipo + " " + nombre + " (" + especie + ") - Dueño: " + dniDueno;
    }
}