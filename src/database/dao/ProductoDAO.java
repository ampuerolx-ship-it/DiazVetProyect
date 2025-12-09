package database.dao;

import database.ConexionDB;
import model.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductoDAO {

    public List<Producto> listarTodo() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(new Producto(
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("categoria")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean actualizarStock(String codigo, int cantidadRestar) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE codigo = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidadRestar);
            pstmt.setString(2, codigo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // --- C R E A T E ---
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO productos (codigo, nombre, descripcion, precio, stock, categoria) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setString(3, producto.getDescripcion());
            pstmt.setDouble(4, producto.getPrecio());
            pstmt.setInt(5, producto.getStock());
            pstmt.setString(6, producto.getCategoria());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    // --- R E A D A L L ---
    public ObservableList<Producto> obtenerTodos() {
        ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
        String sql = "SELECT codigo, nombre, descripcion, precio, stock, categoria FROM productos";
        
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Producto producto = new Producto(
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("categoria")
                );
                listaProductos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return listaProductos;
    }
    
    // --- U P D A T E ---
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ?, categoria = ? WHERE codigo = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setInt(4, producto.getStock());
            pstmt.setString(5, producto.getCategoria());
            pstmt.setString(6, producto.getCodigo()); // WHERE clause
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    // --- D E L E T E ---
    public boolean eliminar(String codigo) {
        String sql = "DELETE FROM productos WHERE codigo = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
    
    // Método auxiliar para el Controller (usado en validación)
    public Producto obtenerPorCodigo(String codigo) {
        String sql = "SELECT codigo, nombre, descripcion, precio, stock, categoria FROM productos WHERE codigo = ?";
        // ... (código similar a obtenerTodos, pero solo recupera un Producto) ...
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Producto(rs.getString("codigo"), rs.getString("nombre"), rs.getString("descripcion"),
                                    rs.getDouble("precio"), rs.getInt("stock"), rs.getString("categoria"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método especializado para alertas de stock (usado en la vista de Inventario)
    public ObservableList<Producto> obtenerStockBajo(int limite) {
        ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
        String sql = "SELECT codigo, nombre, descripcion, precio, stock, categoria FROM productos WHERE stock <= ?";
        
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                listaProductos.add(new Producto(rs.getString("codigo"), rs.getString("nombre"), rs.getString("descripcion"),
                                                rs.getDouble("precio"), rs.getInt("stock"), rs.getString("categoria")));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos con stock bajo: " + e.getMessage());
        }
        return listaProductos;
    }
}
