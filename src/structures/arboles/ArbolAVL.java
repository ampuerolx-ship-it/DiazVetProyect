package structures.arboles;

import java.util.ArrayList;
import java.util.List;

public class ArbolAVL<T extends Comparable<T>> {
    private NodoAVL<T> raiz;

    // Método para obtener la altura de un nodo (cuidado con null)
    private int altura(NodoAVL<T> n) {
        return n == null ? 0 : n.altura;
    }

    // Método para obtener el factor de equilibrio
    private int obtenerFE(NodoAVL<T> n) {
        return n == null ? 0 : altura(n.izquierdo) - altura(n.derecho);
    }
    
    // Método para actualizar la altura de un nodo
    private void actualizarAltura(NodoAVL<T> n) {
        n.altura = 1 + Math.max(altura(n.izquierdo), altura(n.derecho));
    }

    // Rotación Simple Derecha (LL) - Rotación clave para balanceo
    private NodoAVL<T> rotacionSimpleDerecha(NodoAVL<T> c) {
        NodoAVL<T> b = c.izquierdo;
        c.izquierdo = b.derecho;
        b.derecho = c;
        
        actualizarAltura(c);
        actualizarAltura(b);
        
        return b; // Nueva raíz del subárbol
    }

    // Rotación Simple Izquierda (RR)
    private NodoAVL<T> rotacionSimpleIzquierda(NodoAVL<T> c) {
        NodoAVL<T> b = c.derecho;
        c.derecho = b.izquierdo;
        b.izquierdo = c;
        
        actualizarAltura(c);
        actualizarAltura(b);
        
        return b; // Nueva raíz del subárbol
    }

    // Rotación Doble Derecha-Izquierda (RL)
    private NodoAVL<T> rotacionDobleDerechaIzquierda(NodoAVL<T> c) {
        c.derecho = rotacionSimpleDerecha(c.derecho);
        return rotacionSimpleIzquierda(c);
    }

    // Rotación Doble Izquierda-Derecha (LR)
    private NodoAVL<T> rotacionDobleIzquierdaDerecha(NodoAVL<T> c) {
        c.izquierdo = rotacionSimpleIzquierda(c.izquierdo);
        return rotacionSimpleDerecha(c);
    }
    
    // Método recursivo de inserción y rebalanceo
    private NodoAVL<T> insertar(NodoAVL<T> nodo, T dato) {
        if (nodo == null) return new NodoAVL<>(dato);

        if (dato.compareTo(nodo.dato) < 0) {
            nodo.izquierdo = insertar(nodo.izquierdo, dato);
        } else if (dato.compareTo(nodo.dato) > 0) {
            nodo.derecho = insertar(nodo.derecho, dato);
        } else {
            return nodo; // No se permiten duplicados
        }

        // 1. Actualizar altura del nodo actual
        actualizarAltura(nodo);

        // 2. Obtener Factor de Equilibrio (FE)
        int fe = obtenerFE(nodo);

        // 3. Casos de Desbalance (Aplicar Rotaciones)
        
        // Caso I: Desbalance Izquierda-Izquierda (LL)
        if (fe > 1 && dato.compareTo(nodo.izquierdo.dato) < 0) {
            return rotacionSimpleDerecha(nodo);
        }

        // Caso II: Desbalance Derecha-Derecha (RR)
        if (fe < -1 && dato.compareTo(nodo.derecho.dato) > 0) {
            return rotacionSimpleIzquierda(nodo);
        }

        // Caso III: Desbalance Izquierda-Derecha (LR)
        if (fe > 1 && dato.compareTo(nodo.izquierdo.dato) > 0) {
            return rotacionDobleIzquierdaDerecha(nodo);
        }

        // Caso IV: Desbalance Derecha-Izquierda (RL)
        if (fe < -1 && dato.compareTo(nodo.derecho.dato) < 0) {
            return rotacionDobleDerechaIzquierda(nodo);
        }

        return nodo; // Si está balanceado o no hay rotación
    }

    /** MÉTODOS PÚBLICOS **/

    public void insertar(T dato) {
        this.raiz = insertar(this.raiz, dato);
    }

    public T buscar(T dato) {
        return buscar(this.raiz, dato);
    }
    
    // Método auxiliar para la búsqueda
    private T buscar(NodoAVL<T> nodo, T dato) {
        if (nodo == null) return null;

        if (dato.compareTo(nodo.dato) == 0) {
            return nodo.dato;
        } else if (dato.compareTo(nodo.dato) < 0) {
            return buscar(nodo.izquierdo, dato);
        } else {
            return buscar(nodo.derecho, dato);
        }
    }
    
    // Método para obtener una lista InOrden (útil para la tabla)
    public List<T> obtenerInOrden() {
        List<T> lista = new ArrayList<>();
        obtenerInOrden(raiz, lista);
        return lista;
    }

    private void obtenerInOrden(NodoAVL<T> nodo, List<T> lista) {
        if (nodo != null) {
            obtenerInOrden(nodo.izquierdo, lista);
            lista.add(nodo.dato);
            obtenerInOrden(nodo.derecho, lista);
        }
    }
    
    public boolean estaVacio() {
        return raiz == null;
    }
}

/*package structures.arboles;

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
}*/