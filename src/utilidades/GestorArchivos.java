package utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GestorArchivos {
    
    private static final String BASE_DIR = "uploads";
    
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
    //O TAL VEZ:
    /*public static String guardarImagen(File sourceFile, String targetSubDir) {
        return guardarArchivo(sourceFile, targetSubDir);
    }*/
    
    public static String guardarArchivo(File sourceFile, String targetSubDir) {
        if (sourceFile == null || !sourceFile.exists()) {
            return null;
        }

        try {
            // 1. Crear el directorio de destino si no existe
            Path targetPath = Path.of(BASE_DIR, targetSubDir);
            Files.createDirectories(targetPath);

            // 2. Generar un nombre único basado en timestamp y nombre original
            String extension = "";
            String originalName = sourceFile.getName();
            int i = originalName.lastIndexOf('.');
            if (i > 0) {
                extension = originalName.substring(i);
                originalName = originalName.substring(0, i);
            }
            
            String uniqueName = originalName.replaceAll("[^a-zA-Z0-9.-]", "_") // Limpiar el nombre de caracteres extraños
                                    + "_" + System.currentTimeMillis() 
                                    + extension;

            Path finalFilePath = targetPath.resolve(uniqueName);

            // 3. Copiar el archivo
            Files.copy(sourceFile.toPath(), finalFilePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Retornar la ruta relativa para la base de datos
            return BASE_DIR + "/" + targetSubDir + "/" + uniqueName;

        } catch (IOException e) {
            System.err.println("Error al guardar el archivo en " + targetSubDir + ": " + e.getMessage());
            return null;
        }
    }
    
}