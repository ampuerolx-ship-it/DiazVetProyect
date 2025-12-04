package utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class GestorArchivos {
    
    public static String guardarImagen(File archivoOrigen, String carpetaDestino) {
        if (archivoOrigen == null) return null;
        
        try {
            // Crear directorio si no existe (ej: "uploads/pets")
            File directorio = new File(carpetaDestino);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // Generar nombre único: tiempo_nombreOriginal
            String nuevoNombre = System.currentTimeMillis() + "_" + archivoOrigen.getName();
            File archivoDestino = new File(directorio, nuevoNombre);

            // Copiar
            Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            return archivoDestino.getPath(); // Retornamos la ruta relativa/absoluta para guardar en BD
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}