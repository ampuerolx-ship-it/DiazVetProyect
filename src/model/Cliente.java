package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cliente {
    private final StringProperty dni;
    private final StringProperty nombres;
    private final StringProperty apellidos;
    private final StringProperty telefono;
    private final StringProperty direccion;
    private final StringProperty correo; // Nueva columna en ConexionDB

    public Cliente() {
        this(null, null, null, null, null, null);
    }

    public Cliente(String dni, String nombres, String apellidos, String telefono, String direccion, String correo) {
        this.dni = new SimpleStringProperty(dni);
        this.nombres = new SimpleStringProperty(nombres);
        this.apellidos = new SimpleStringProperty(apellidos);
        this.telefono = new SimpleStringProperty(telefono);
        this.direccion = new SimpleStringProperty(direccion);
        this.correo = new SimpleStringProperty(correo);
    }

    // --- Properties (Para TableView) ---
    public StringProperty dniProperty() { return dni; }
    public StringProperty nombresProperty() { return nombres; }
    public StringProperty apellidosProperty() { return apellidos; }
    public StringProperty telefonoProperty() { return telefono; }
    public StringProperty direccionProperty() { return direccion; }
    public StringProperty correoProperty() { return correo; }

    // --- Getters and Setters ---
    public String getDni() { return dni.get(); }
    public void setDni(String dni) { this.dni.set(dni); }
    public String getNombres() { return nombres.get(); }
    public void setNombres(String nombres) { this.nombres.set(nombres); }
    public String getApellidos() { return apellidos.get(); }
    public void setApellidos(String apellidos) { this.apellidos.set(apellidos); }
    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String telefono) { this.telefono.set(telefono); }
    public String getDireccion() { return direccion.get(); }
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
    public String getCorreo() { return correo.get(); }
    public void setCorreo(String correo) { this.correo.set(correo); }
}