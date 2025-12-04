package structures.arboles;

public class ArbolAVL<T extends Comparable<T>> {

    private NodoAVL<T> raiz;

    // -----------------------------------------------------------------
    // MÉTODOS AUXILIARES
    // -----------------------------------------------------------------

    // 1. Obtiene la altura del nodo (0 si es null)
    private int altura(NodoAVL<T> N) {
        return (N == null) ? 0 : N.altura;
    }

    // 2. Obtiene el factor de balance (diferencia de alturas entre subárboles)
    private int obtenerBalance(NodoAVL<T> N) {
        return (N == null) ? 0 : altura(N.izquierda) - altura(N.derecha);
    }

    // 3. Actualiza la altura del nodo después de una rotación o inserción
    private void actualizarAltura(NodoAVL<T> N) {
        N.altura = 1 + Math.max(altura(N.izquierda), altura(N.derecha));
    }

    // -----------------------------------------------------------------
    // MÉTODOS DE ROTACIÓN (El corazón del AVL)
    // -----------------------------------------------------------------

    // Rotación Simple Derecha (Caso Izquierda-Izquierda)
    private NodoAVL<T> rotarDerecha(NodoAVL<T> y) {
        NodoAVL<T> x = y.izquierda;
        NodoAVL<T> T2 = x.derecha;

        // Realizar rotación
        x.derecha = y;
        y.izquierda = T2;

        // Actualizar alturas
        actualizarAltura(y);
        actualizarAltura(x);

        return x; // Nueva raíz del subárbol
    }

    // Rotación Simple Izquierda (Caso Derecha-Derecha)
    private NodoAVL<T> rotarIzquierda(NodoAVL<T> x) {
        NodoAVL<T> y = x.derecha;
        NodoAVL<T> T2 = y.izquierda;

        // Realizar rotación
        y.izquierda = x;
        x.derecha = T2;

        // Actualizar alturas
        actualizarAltura(x);
        actualizarAltura(y);

        return y; // Nueva raíz del subárbol
    }

    // -----------------------------------------------------------------
    // MÉTODO DE INSERCIÓN Y BALANCEO
    // -----------------------------------------------------------------

    public void insertar(T dato) {
        raiz = insertarRec(raiz, dato);
    }

    private NodoAVL<T> insertarRec(NodoAVL<T> nodo, T dato) {
        // 1. Realizar inserción estándar de ABB
        if (nodo == null) return new NodoAVL<>(dato);

        if (dato.compareTo(nodo.dato) < 0) {
            nodo.izquierda = insertarRec(nodo.izquierda, dato);
        } else if (dato.compareTo(nodo.dato) > 0) {
            nodo.derecha = insertarRec(nodo.derecha, dato);
        } else {
            return nodo; // Datos duplicados no permitidos
        }

        // 2. Actualizar altura del nodo actual
        actualizarAltura(nodo);

        // 3. Obtener el factor de balance
        int balance = obtenerBalance(nodo);

        // 4. Realizar las ROTACIONES necesarias:
        
        // Caso Izquierda-Izquierda (LL)
        if (balance > 1 && dato.compareTo(nodo.izquierda.dato) < 0) {
            return rotarDerecha(nodo);
        }

        // Caso Derecha-Derecha (RR)
        if (balance < -1 && dato.compareTo(nodo.derecha.dato) > 0) {
            return rotarIzquierda(nodo);
        }

        // Caso Izquierda-Derecha (LR)
        if (balance > 1 && dato.compareTo(nodo.izquierda.dato) > 0) {
            nodo.izquierda = rotarIzquierda(nodo.izquierda);
            return rotarDerecha(nodo);
        }

        // Caso Derecha-Izquierda (RL)
        if (balance < -1 && dato.compareTo(nodo.derecha.dato) < 0) {
            nodo.derecha = rotarDerecha(nodo.derecha);
            return rotarIzquierda(nodo);
        }

        // Si está balanceado o fue caso duplicado
        return nodo;
    }

    // -----------------------------------------------------------------
    // MÉTODO DE BÚSQUEDA
    // -----------------------------------------------------------------
    public T buscar(T dato) {
        return buscarRec(raiz, dato);
    }
    
    private T buscarRec(NodoAVL<T> nodo, T dato) {
        if (nodo == null) return null; // No encontrado

        if (dato.compareTo(nodo.dato) < 0) {
            return buscarRec(nodo.izquierda, dato);
        } else if (dato.compareTo(nodo.dato) > 0) {
            return buscarRec(nodo.derecha, dato);
        } else {
            return nodo.dato; // Encontrado
        }
    }
}