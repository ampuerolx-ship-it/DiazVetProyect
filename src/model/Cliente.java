package model;

public class Cliente {
    private String dni; // PK
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String direccion;

    // Constructor completo
    public Cliente(String dni, String nombres, String apellidos, String correo, String telefono, String direccion) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Constructor vacío (útil para mapeo)
    public Cliente() {}

    // --- GETTERS ---
    public String getDni() { return dni; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getNombreCompleto() { return nombres + " " + apellidos; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; } // <--- GETTER AGREGADO

    // --- SETTERS ---
    public void setDni(String dni) { this.dni = dni; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDireccion(String direccion) { this.direccion = direccion; } // <--- SETTER AGREGADO
    
    @Override
    public String toString() {
        return getNombreCompleto();
    }
}