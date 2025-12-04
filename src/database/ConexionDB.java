package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {

    private static final String URL = "jdbc:sqlite:diasvet.db";

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Error en la conexión a SQLite: " + e.getMessage());
            return null;
        }
    }

    public static void inicializarBaseDeDatos() {
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            // 1. Tabla CLIENTES (Incluye 'correo')
            String sqlClientes = "CREATE TABLE IF NOT EXISTS clientes (" +
                    "dni TEXT PRIMARY KEY, " +
                    "nombre TEXT NOT NULL, " +
                    "telefono TEXT, " +
                    "direccion TEXT, " +
                    "correo TEXT" + 
                    ");";

            // 2. Tabla USUARIOS (Adaptada al nuevo modelo)
            String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "nickname TEXT PRIMARY KEY, " +
                    "password_hash TEXT NOT NULL, " +
                    "dni_cliente TEXT NOT NULL, " +
                    "rol TEXT NOT NULL, " +
                    "foto_perfil_ruta TEXT, " +
                    "FOREIGN KEY(dni_cliente) REFERENCES clientes(dni)" +
                    ");";

            // 3. Tabla MASCOTAS (Incluye ruta foto)
            String sqlMascotas = "CREATE TABLE IF NOT EXISTS mascotas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "especie TEXT, " +
                    "raza TEXT, " +
                    "edad INTEGER, " +
                    "peso REAL, " +
                    "dni_cliente TEXT, " +
                    "foto_mascota_ruta TEXT, " +
                    "FOREIGN KEY(dni_cliente) REFERENCES clientes(dni)" +
                    ");";

            // 4. Tabla HISTORIALES (Incluye ruta registro vacunas)
            String sqlHistoriales = "CREATE TABLE IF NOT EXISTS historiales (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_mascota INTEGER NOT NULL, " +
                    "vacunas_aplicadas TEXT, " +
                    "vacunas_pendientes TEXT, " +
                    "ultima_desparasitacion TEXT, " +
                    "ultima_visita TEXT, " +
                    "registro_vacunas_ruta TEXT, " +
                    "FOREIGN KEY(id_mascota) REFERENCES mascotas(id)" +
                    ");";
            
            // 5. Tabla CITAS (Estructura estándar)
            String sqlCitas = "CREATE TABLE IF NOT EXISTS citas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "fecha_hora TEXT NOT NULL, " +
                    "motivo TEXT, " +
                    "dni_cliente TEXT, " +
                    "id_mascota INTEGER, " +
                    "FOREIGN KEY (dni_cliente) REFERENCES clientes(dni), " +
                    "FOREIGN KEY (id_mascota) REFERENCES mascotas(id)" +
                    ");";
            
            // 6. Tabla PRODUCTOS (Para el PetShop)
            String sqlProductos = "CREATE TABLE IF NOT EXISTS productos (" +
                    "codigo TEXT PRIMARY KEY, " +
                    "nombre TEXT NOT NULL, " +
                    "descripcion TEXT, " +
                    "precio REAL, " +
                    "stock INTEGER" +
                    ");";

            // --- EJECUTAR CREACIÓN DE TABLAS ---
            stmt.execute(sqlClientes);
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlMascotas);
            stmt.execute(sqlHistoriales);
            stmt.execute(sqlCitas);
            stmt.execute(sqlProductos);
            
            // --- ACTUALIZACIONES PARA BASES DE DATOS EXISTENTES (Migraciones) ---
            // Si el archivo .db ya existía sin estas columnas, esto las agrega sin borrar datos
            try { stmt.execute("ALTER TABLE clientes ADD COLUMN correo TEXT;"); } catch (SQLException e) {}
            try { stmt.execute("ALTER TABLE mascotas ADD COLUMN foto_mascota_ruta TEXT;"); } catch (SQLException e) {}
            try { stmt.execute("ALTER TABLE historiales ADD COLUMN registro_vacunas_ruta TEXT;"); } catch (SQLException e) {}

            // --- CREACIÓN DEL ADMINISTRADOR POR DEFECTO ---
            // 1. Primero creamos el "Cliente" ficticio para el admin (por la llave foránea)
            String sqlAdminCliente = "INSERT OR IGNORE INTO clientes (dni, nombre, telefono, direccion, correo) " +
                    "VALUES ('00000000', 'Super Administrador', '000000000', 'Sistema', 'admin@diasvet.com');";
            stmt.execute(sqlAdminCliente);

            // 2. Luego creamos el "Usuario" admin vinculado a ese cliente
            String sqlAdminUsuario = "INSERT OR IGNORE INTO usuarios (nickname, password_hash, dni_cliente, rol, foto_perfil_ruta) " +
                    "VALUES ('admin', 'admin123', '00000000', 'admin', null);";
            stmt.execute(sqlAdminUsuario);

            System.out.println("Base de datos inicializada y verificada correctamente.");

        } catch (SQLException e) {
            System.err.println("Error inicializando BD: " + e.getMessage());
        }
    }
}