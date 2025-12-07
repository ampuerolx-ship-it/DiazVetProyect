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
                    "nombres TEXT NOT NULL, " +
                    "apellidos TEXT NOT NULL, " +
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
                    "stock INTEGER, " +
                    "categoria TEXT" + // Ej: Alimento, Juguete, Medicina
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

            try { stmt.execute("ALTER TABLE productos ADD COLUMN categoria TEXT;"); } catch (SQLException e) { 
                // Esto es normal si la columna ya existe, por eso usamos el try-catch.
            }
            
            // Insertar productos de prueba si la tabla está vacía
            String sqlDataPrueba = "INSERT OR IGNORE INTO productos (codigo, nombre, descripcion, precio, stock, categoria) VALUES " +
                    "('P001', 'Croquetas Premium Dog', 'Saco 15kg Adulto', 120.00, 20, 'Alimento')," +
                    "('P002', 'Juguete Hueso Goma', 'Indestructible', 25.50, 50, 'Juguete')," +
                    "('P003', 'Pipeta Antipulgas', 'Para perros medianos', 45.00, 100, 'Medicina')," +
                    "('P004', 'Collar Reflectivo', 'Ajustable rojo', 30.00, 15, 'Accesorio')," +
                    "('P005', 'Shampoo Hipoalergénico', 'Piel sensible', 35.00, 30, 'Higiene');";
            stmt.execute(sqlDataPrueba);
            
            // --- ADMIN POR DEFECTO ---
            // Creamos el cliente admin ficticio con las nuevas columnas
            String sqlAdminCliente = "INSERT OR IGNORE INTO clientes (dni, nombres, apellidos, telefono, direccion, correo) " +
                    "VALUES ('00000000', 'Super', 'Administrador', '000000000', 'Sistema', 'admin@diasvet.com');";
            stmt.execute(sqlAdminCliente);

            // Creamos el usuario admin
            String sqlAdminUsuario = "INSERT OR IGNORE INTO usuarios (nickname, password_hash, dni_cliente, rol, foto_perfil_ruta) " +
                    "VALUES ('admin', 'admin123', '00000000', 'admin', null);";
            stmt.execute(sqlAdminUsuario);

            System.out.println("Base de datos inicializada correctamente.");

        } catch (SQLException e) {
            System.err.println("Error inicializando BD: " + e.getMessage());
        }
    }
}