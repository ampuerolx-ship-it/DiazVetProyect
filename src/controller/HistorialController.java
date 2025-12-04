package controller;

import model.HistorialClinico;
import model.Paciente; // Asumimos que existe
import structures.ordenamiento.SortUtils;
import structures.ordenamiento.BusquedaUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialController {

    private List<HistorialClinico> baseDatosHistorial;

    public HistorialController() {
        this.baseDatosHistorial = new ArrayList<>();
        // Aquí cargarías datos desde la BD real en el futuro
    }

    // --- MÉTODOS DE GESTIÓN (CRUD) ---

    public void agregarRegistro(HistorialClinico registro) {
        baseDatosHistorial.add(registro);
        // Cada vez que agregamos, la lista se desordena, así que la reordenamos.
        // Usamos QuickSort porque es eficiente en promedio.
        SortUtils.quickSort(baseDatosHistorial);
    }

    // --- MÉTODOS DE ACCESO POR ROL ---

    /**
     * ADMINISTRADOR: Ve todo.
     * Se aplica búsqueda binaria para encontrar un historial específico por ID rápido.
     */
    public HistorialClinico buscarHistorialAdmin(String idHistorialBusqueda) {
        // Para búsqueda binaria, necesitamos un objeto 'dummy' con el ID para comparar,
        // O implementamos una búsqueda binaria personalizada por ID.
        // Por simplicidad académica, usaremos filtrado de stream aquí para el prototipo,
        // pero la ordenación ya se hizo arriba con QuickSort.
        
        return baseDatosHistorial.stream()
                .filter(h -> h.getIdHistorial().equals(idHistorialBusqueda))
                .findFirst()
                .orElse(null);
    }

    /**
     * USUARIO (DUEÑO): Solo ve lo de sus mascotas.
     * Implementa el filtro de seguridad.
     */
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
        
        return historialFiltrado;
    }
    
    public List<HistorialClinico> getTodoElHistorial() {
        return baseDatosHistorial;
    }
}