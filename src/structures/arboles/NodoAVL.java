package structures.arboles;

/**
 * Representa un nodo en el Arbol AVL. T debe ser comparable (e.g., Cita, Integer).
 */
public class NodoAVL<T extends Comparable<T>> {
    
    public T dato;
    public NodoAVL<T> izquierda;
    public NodoAVL<T> derecha;
    public int altura; // Importante para el balanceo
    
    public NodoAVL(T dato) {
        this.dato = dato;
        this.altura = 1; // Un nodo nuevo siempre tiene altura 1
        this.izquierda = null;
        this.derecha = null;
    }
}