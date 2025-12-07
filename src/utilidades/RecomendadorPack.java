package utilidades;

import model.Producto;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del problema de la Mochila (Knapsack 0/1) 
 * para sugerir la mejor combinación de productos dado un presupuesto.
 */
public class RecomendadorPack {

    public static List<Producto> sugerirMejorCompra(double presupuesto, List<Producto> inventario) {
        int n = inventario.size();
        int w = (int) Math.floor(presupuesto); // Presupuesto entero para la matriz
        
        // dp[i][j] almacenará el "valor" máximo con i productos y j presupuesto.
        // Asumimos que el "valor" es el precio para maximizar el gasto del presupuesto exacto,
        // o podrías agregar un atributo "calidad" al producto.
        double[][] dp = new double[n + 1][w + 1];

        for (int i = 1; i <= n; i++) {
            Producto p = inventario.get(i - 1);
            int costo = (int) Math.ceil(p.getPrecio());
            
            for (int j = 0; j <= w; j++) {
                if (costo <= j) {
                    // Maximizamos el precio total acumulado
                    dp[i][j] = Math.max(dp[i - 1][j], p.getPrecio() + dp[i - 1][j - costo]);
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        // Recuperar los items seleccionados (Backtracking)
        List<Producto> seleccion = new ArrayList<>();
        int res = (int) dp[n][w]; // Valor máximo conseguido
        int j = w;
        
        for (int i = n; i > 0 && res > 0; i--) {
            // Si el valor viene de arriba, no se incluyó este item
            if (res == (int) dp[i - 1][j]) continue;
            else {
                // Se incluyó el item
                Producto p = inventario.get(i - 1);
                seleccion.add(p);
                res -= (int) p.getPrecio();
                j -= (int) Math.ceil(p.getPrecio());
            }
        }
        return seleccion;
    }
}