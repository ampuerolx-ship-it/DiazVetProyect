package structures.lineales;

// ⭐ NUEVOS IMPORTS
import java.util.ArrayList;
import java.util.List;

public class ColaPrioridad<T> {

    // Clase interna Nodo (La "cajita" que guarda el dato)
    private class Nodo {
        T dato;
        int prioridad; // 1 es más urgente que 10
        Nodo siguiente;

        public Nodo(T dato, int prioridad) {
            this.dato = dato;
            this.prioridad = prioridad;
            this.siguiente = null;
        }
    }

    private Nodo frente; // El inicio de la cola (quien será atendido)
    private int longitud;

    public ColaPrioridad() {
        this.frente = null;
        this.longitud = 0;
    }

    // --- MÉTODO CLAVE: ENCOLAR CON PRIORIDAD ---
    public void encolar(T dato, int prioridad) {
        Nodo nuevo = new Nodo(dato, prioridad);

        // CASO 1: Cola vacía o el nuevo es más urgente que el primero
        if (estaVacia() || prioridad < frente.prioridad) {
            nuevo.siguiente = frente;
            frente = nuevo;
        } 
        else {
            // CASO 2: Recorrer para encontrar su lugar
            Nodo actual = frente;
            
            // Avanzamos mientras haya nodos y el nodo siguiente tenga 
            // igual o mayor urgencia (menor o igual número) que el nuevo.
            while (actual.siguiente != null && actual.siguiente.prioridad <= prioridad) {
                actual = actual.siguiente;
            }
            
            // Insertamos el nodo
            nuevo.siguiente = actual.siguiente;
            actual.siguiente = nuevo;
        }
        longitud++;
    }

    // Extraer el primero (Atender paciente)
    public T desencolar() {
        if (estaVacia()) return null;
        
        T dato = frente.dato;
        frente = frente.siguiente;
        longitud--;
        return dato;
    }

    // Ver quién sigue sin sacarlo
    public T verFrente() {
        return estaVacia() ? null : frente.dato;
    }
    
    // Ver si está vacía
    public boolean estaVacia() {
        return frente == null;
    }
    
    // Ver el tamaño de la cola
    public int longitud() {
        return longitud;
    }
    
    // ⭐ MÉTODO AÑADIDO: NECESARIO PARA LA UI
    /**
     * Devuelve una lista de los elementos en la cola en su orden de prioridad.
     * Importante: No consume los elementos (no llama a desencolar).
     * @return List<T> lista de elementos ordenados por prioridad (frente a final).
     */
    public List<T> obtenerContenidoOrdenado() {
        List<T> lista = new ArrayList<>();
        Nodo actual = frente;
        
        while(actual != null) {
            lista.add(actual.dato);
            actual = actual.siguiente;
        }
        return lista;
    }
}