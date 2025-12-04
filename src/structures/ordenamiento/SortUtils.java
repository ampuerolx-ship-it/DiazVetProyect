package structures.ordenamiento;

import java.util.List;
import java.util.ArrayList;

public class SortUtils {

    // ==========================================
    // QUICKSORT (Rápido, in-place, ideal para Arrays)
    // ==========================================
    public static <T extends Comparable<T>> void quickSort(List<T> lista) {
        if (lista == null || lista.isEmpty()) return;
        quickSortRec(lista, 0, lista.size() - 1);
    }

    private static <T extends Comparable<T>> void quickSortRec(List<T> lista, int bajo, int alto) {
        if (bajo < alto) {
            int indiceParticion = particion(lista, bajo, alto);
            quickSortRec(lista, bajo, indiceParticion - 1);
            quickSortRec(lista, indiceParticion + 1, alto);
        }
    }

    private static <T extends Comparable<T>> int particion(List<T> lista, int bajo, int alto) {
        T pivote = lista.get(alto);
        int i = (bajo - 1);

        for (int j = bajo; j < alto; j++) {
            // Si el elemento actual es menor o igual al pivote
            if (lista.get(j).compareTo(pivote) <= 0) {
                i++;
                intercambiar(lista, i, j);
            }
        }
        intercambiar(lista, i + 1, alto);
        return i + 1;
    }

    private static <T> void intercambiar(List<T> lista, int i, int j) {
        T temp = lista.get(i);
        lista.set(i, lista.get(j));
        lista.set(j, temp);
    }

    // ==========================================
    // MERGESORT (Estable, ideal para listas grandes enlazadas)
    // ==========================================
    public static <T extends Comparable<T>> void mergeSort(List<T> lista) {
        if (lista.size() < 2) return;
        
        int medio = lista.size() / 2;
        List<T> izquierda = new ArrayList<>(lista.subList(0, medio));
        List<T> derecha = new ArrayList<>(lista.subList(medio, lista.size()));

        mergeSort(izquierda);
        mergeSort(derecha);

        mezclar(lista, izquierda, derecha);
    }

    private static <T extends Comparable<T>> void mezclar(List<T> lista, List<T> izq, List<T> der) {
        int i = 0, j = 0, k = 0;
        
        while (i < izq.size() && j < der.size()) {
            if (izq.get(i).compareTo(der.get(j)) <= 0) {
                lista.set(k++, izq.get(i++));
            } else {
                lista.set(k++, der.get(j++));
            }
        }
        while (i < izq.size()) lista.set(k++, izq.get(i++));
        while (j < der.size()) lista.set(k++, der.get(j++));
    }
}