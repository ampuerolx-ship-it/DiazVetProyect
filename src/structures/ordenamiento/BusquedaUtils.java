package structures.ordenamiento;

import java.util.List;

public class BusquedaUtils {

    /**
     * Búsqueda Binaria Genérica.
     * @param lista Lista PREVIAMENTE ORDENADA.
     * @param valorBuscado Objeto a buscar (debe implementar equals/compareTo).
     * @return Índice del objeto o -1 si no existe.
     */
    public static <T extends Comparable<T>> int busquedaBinaria(List<T> lista, T valorBuscado) {
        int inicio = 0;
        int fin = lista.size() - 1;

        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            T valorMedio = lista.get(medio);

            int comparacion = valorMedio.compareTo(valorBuscado);

            if (comparacion == 0) {
                return medio; // ¡Encontrado!
            }
            if (comparacion > 0) { // Si es "mayor", buscamos en la mitad izquierda (depende del ordenamiento)
                 // Nota: Si ordenamos descendente (fechas), la lógica > o < se invierte.
                 // Asumiremos orden ascendente estándar para la utilidad genérica.
                 fin = medio - 1;
            } else {
                inicio = medio + 1;
            }
        }
        return -1; // No encontrado
    }

    /**
     * Búsqueda Secuencial (Por si la lista está desordenada o es pequeña)
     */
    public static <T> int busquedaSecuencial(List<T> lista, T valorBuscado) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).equals(valorBuscado)) {
                return i;
            }
        }
        return -1;
    }
}