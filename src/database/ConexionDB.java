package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {

    // Nombre del archivo de base de datos (se creará en la raíz del proyecto)
    private static final String URL = "jdbc:sqlite:diasvet.db";
    private static Connection conexion = null;

    /**
     * Obtiene la conexión única (Patrón Singleton).
     * Si no existe o está cerrada, crea una nueva.
     */
    public static Connection conectar() {
        try {
            if (conexion == null || conexion.isClosed()) {
                // Asegúrate de tener la librería sqlite-jdbc en tu proyecto
                conexion = DriverManager.getConnection(URL);
                // System.out.println("Conexión a SQLite establecida."); // Descomentar para depuración
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar con la BD: " + e.getMessage());
        }
        return conexion;
    }

    /**
     * Cierra la conexión explícitamente si es necesario.
     */
    public static void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void inicializarBaseDeDatos() {
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            // ---------------------------------------------------------
            // A. TABLA USUARIOS (Dueños y Admin)
            // PK es el DNI para búsquedas rápidas y unicidad lógica.
            // ---------------------------------------------------------
            String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "dni TEXT PRIMARY KEY, " +     // ID solicitado como DNI
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "rol TEXT NOT NULL, " +        // 'ADMIN' o 'CLIENTE'
                    "nombres TEXT, " +
                    "apellidos TEXT, " +
                    "correo TEXT, " +
                    "fecha_nacimiento TEXT, " +
                    "telefono TEXT, " +
                    "direccion TEXT, " +
                    "foto_perfil TEXT" +           // Ruta del archivo de imagen
                    ");";

            // ---------------------------------------------------------
            // B. TABLA MASCOTAS (Pacientes)
            // Contiene datos filiatorios básicos.
            // ---------------------------------------------------------
            String sqlMascotas = "CREATE TABLE IF NOT EXISTS mascotas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_dueno TEXT NOT NULL, " +   // FK -> usuarios.dni
                    "nombre TEXT NOT NULL, " +
                    "especie TEXT NOT NULL, " +
                    "raza TEXT, " +
                    "sexo TEXT, " +                // Macho/Hembra (Visto en img Historia)
                    "color TEXT, " +               // Visto en img Historia
                    "fecha_nacimiento TEXT, " +    // Para calcular edad exacta
                    "caracteristicas TEXT, " +     // Señas particulares
                    "foto_mascota TEXT, " +
                    "foto_vacunas TEXT, " +        // Ruta de imagen de carnet
                    "FOREIGN KEY(id_dueno) REFERENCES usuarios(dni)" +
                    ");";

            // ---------------------------------------------------------
            // B.2 TABLA ANAMNESIS (Extensión de Mascota)
            // Soporta los datos fijos de la hoja clínica (Img Anamnesis)
            // ---------------------------------------------------------
            String sqlAnamnesis = "CREATE TABLE IF NOT EXISTS anamnesis (" +
                    "id_mascota INTEGER PRIMARY KEY, " + // 1 a 1 con Mascota
                    "dieta TEXT, " +
                    "esterilizado TEXT, " +        // SI/NO
                    "numero_partos INTEGER, " +
                    "enfermedades_previas TEXT, " +
                    "cirugias_previas TEXT, " +
                    "vacunas_info TEXT, " +
                    "convive_otros_animales TEXT, " + // SI/NO
                    "origen_procedencia TEXT, " +
                    "FOREIGN KEY(id_mascota) REFERENCES mascotas(id)" +
                    ");";

            // ---------------------------------------------------------
            // C. TABLA CITAS
            // Gestión de agenda y estados.
            // ---------------------------------------------------------
            String sqlCitas = "CREATE TABLE IF NOT EXISTS citas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_mascota INTEGER NOT NULL, " +
                    "fecha_hora TEXT NOT NULL, " + // Formato ISO8601
                    "motivo TEXT, " +
                    "estado TEXT DEFAULT 'PENDIENTE', " + // PENDIENTE, ATENDIDO, CANCELADO
                    "prioridad INTEGER DEFAULT 3, " +     // 1=Alta, 2=Media, 3=Baja
                    "FOREIGN KEY(id_mascota) REFERENCES mascotas(id)" +
                    ");";

            // ---------------------------------------------------------
            // D. TABLA HISTORIAL_MEDICO (Evolución)
            // Cada visita o consulta individual.
            // ---------------------------------------------------------
            String sqlHistorial = "CREATE TABLE IF NOT EXISTS historial_medico (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_mascota INTEGER NOT NULL, " +
                    "fecha TEXT NOT NULL, " +
                    "hora TEXT, " +
                    "motivo_consulta TEXT, " +
                    "sintomas TEXT, " +
                    "diagnostico TEXT, " +
                    "tratamiento TEXT, " +
                    "examenes_auxiliares TEXT, " + // Labs, Rayos X
                    "veterinario_cargo TEXT, " +
                    "FOREIGN KEY(id_mascota) REFERENCES mascotas(id)" +
                    ");";

            // Ejecutar creación
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlMascotas);
            stmt.execute(sqlAnamnesis); // Nueva tabla vital para la ficha
            stmt.execute(sqlCitas);
            stmt.execute(sqlHistorial);

            // Crear ADMIN por defecto (Clave: admin123)
            // Nota: Insertamos DNI ficticio '00000000' para el admin sistema
            String sqlAdmin = "INSERT OR IGNORE INTO usuarios (dni, username, password, rol, nombres, apellidos) " +
                    "VALUES ('00000000', 'admin', 'admin123', 'ADMIN', 'Super', 'Administrador');";
            stmt.execute(sqlAdmin);

            System.out.println("Base de datos 'diasvet.db' estructurada correctamente.");

        } catch (SQLException e) {
            System.err.println("Error crítico en BD: " + e.getMessage());
            e.printStackTrace();
        }
    }
}