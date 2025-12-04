package utilidades;
import java.util.regex.Pattern;

/**
 * Clase utilitaria para validaciones comunes en el sistema
 */
public class Validador {

    // Patrones para validaciones
    private static final Pattern PATRON_DNI = Pattern.compile("^\\d{8}$");
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\d{9}$");

    /**
     * Valida que un DNI tenga exactamente 8 dígitos
     * @param dni El DNI a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean validarDNI(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return false;}
        return PATRON_DNI.matcher(dni.trim()).matches();
    }

    /**
     * Valida que un teléfono tenga exactamente 9 dígitos
     * @param telefono El teléfono a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;}
        return PATRON_TELEFONO.matcher(telefono.trim()).matches();
    }

    /**
     * Obtiene un mensaje de error para DNI inválido
     * @return Mensaje de error formateado
     */
    public static String getMensajeErrorDNI() {
        return "El DNI debe tener exactamente 8 dígitos numéricos";}

    /**
     * Obtiene un mensaje de error para teléfono inválido
     * @return Mensaje de error formateado
     */
    public static String getMensajeErrorTelefono() {
        return "El teléfono debe tener exactamente 9 dígitos numéricos";}
}