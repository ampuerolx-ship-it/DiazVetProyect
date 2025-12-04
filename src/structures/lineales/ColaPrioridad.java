package structures.lineales;

// Usamos un tipo genérico <T> para que sirva para Pacientes o cualquier otra cosa en el futuro
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
        // (Ej: Llega un perro atropellado (1) y el primero es una vacuna (3))
        if (estaVacia() || prioridad < frente.prioridad) {
            nuevo.siguiente = frente;
            frente = nuevo;
        } 
        else {
            // CASO 2: Recorrer para encontrar su lugar
            Nodo actual = frente;
            
            // Avanzamos mientras haya nodos y el nodo siguiente tenga 
            // igual o mayor urgencia (menor o igual número) que el nuevo.
            // Esto garantiza estabilidad: si tienen misma prioridad, el nuevo va detrás.
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

    public boolean estaVacia() {
        return frente == null;
    }

    public int getLongitud() {
        return longitud;
    }
}