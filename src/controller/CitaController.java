package controller;

import database.dao.CitaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Paciente;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Cita;

public class CitaController {

    private final CitaDAO citaDAO;
    // Lista temporal para simular base de datos
    private List<Cita> cacheCitas;

    public CitaController() {
        this.citaDAO = new CitaDAO();
        refrescarDatos();
    }

    public void refrescarDatos() {
        this.cacheCitas = citaDAO.listarCitasFuturas();
    }

    /**
     * Filtra las citas cargadas por mes y año.
     */
    public List<Cita> obtenerCitasDelMes(int year, int month) {
        if (cacheCitas == null) refrescarDatos();
        
        return cacheCitas.stream()
                .filter(c -> c.getFechaHora().getYear() == year && 
                             c.getFechaHora().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    /**
     * Filtra las citas cargadas por un día específico.
     */
    public List<Cita> obtenerCitasDelDia(LocalDate fecha) {
        if (cacheCitas == null) refrescarDatos();

        return cacheCitas.stream()
                .filter(c -> c.getFechaHora().toLocalDate().equals(fecha))
                .collect(Collectors.toList());
    }
    
    /**
     * Método para registrar nueva cita desde la UI.
     */
    public boolean agendarCita(Cita nuevaCita, int idMascotaReal) {
        boolean exito = citaDAO.registrarCita(nuevaCita, idMascotaReal);
        if (exito) {
            refrescarDatos(); // Actualizar caché inmediatamente
        }
        return exito;
    }
}