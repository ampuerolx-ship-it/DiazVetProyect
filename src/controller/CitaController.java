package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Paciente;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CitaController {

    // Lista temporal para simular base de datos
    private List<CitaSimulada> baseDatosCitas;

    public CitaController() {
        baseDatosCitas = new ArrayList<>();
        cargarDatosPrueba();
    }

    // Clase auxiliar interna solo para visualización
    public static class CitaSimulada {
        public Paciente paciente;
        public LocalDate fecha;
        public LocalTime hora;

        public CitaSimulada(Paciente p, LocalDate f, LocalTime h) {
            this.paciente = p; this.fecha = f; this.hora = h;
        }
    }

    private void cargarDatosPrueba() {
        // Creamos citas para HOY y MAÑANA para probar las vistas
        LocalDate hoy = LocalDate.now();
        
        baseDatosCitas.add(new CitaSimulada(new Paciente("Bobby", "Perro", "Juan", "123", "Vacuna", 3), hoy, LocalTime.of(9, 0)));
        baseDatosCitas.add(new CitaSimulada(new Paciente("Michi", "Gato", "Ana", "456", "Emergencia", 1), hoy, LocalTime.of(10, 30)));
        baseDatosCitas.add(new CitaSimulada(new Paciente("Rex", "Perro", "Luis", "789", "Revisión", 2), hoy.plusDays(1), LocalTime.of(15, 0)));
        baseDatosCitas.add(new CitaSimulada(new Paciente("Nemo", "Pez", "Dory", "000", "Control", 3), hoy.minusDays(2), LocalTime.of(11, 0)));
    }

    // Método que usará la UI para pedir citas de un mes específico
    public List<CitaSimulada> obtenerCitasDelMes(int year, int month) {
        return baseDatosCitas.stream()
                .filter(c -> c.fecha.getYear() == year && c.fecha.getMonthValue() == month)
                .collect(Collectors.toList());
    }

    // Método para citas de un día específico
    public List<CitaSimulada> obtenerCitasDelDia(LocalDate fecha) {
        return baseDatosCitas.stream()
                .filter(c -> c.fecha.equals(fecha))
                .collect(Collectors.toList());
    }
}