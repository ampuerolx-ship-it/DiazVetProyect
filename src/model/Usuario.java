package model;

public class Usuario {
    private String nickname;
    private String password;
    private String dniCliente;
    private String rol;
    private String fotoPerfilRuta;

    public Usuario(String nickname, String password, String dniCliente, String rol, String fotoPerfilRuta) {
        this.nickname = nickname;
        this.password = password;
        this.dniCliente = dniCliente;
        this.rol = rol;
        this.fotoPerfilRuta = fotoPerfilRuta;
    }

    // Getters y Setters
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDniCliente() { return dniCliente; }
    public void setDniCliente(String dniCliente) { this.dniCliente = dniCliente; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getFotoPerfilRuta() { return fotoPerfilRuta; }
    public void setFotoPerfilRuta(String fotoPerfilRuta) { this.fotoPerfilRuta = fotoPerfilRuta; }
}
/*package model;

public class Usuario {
    private String dni; // PK
    private String username;
    private String password;
    private String rol; // "ADMIN" o "CLIENTE"
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;

    // Constructor completo
    public Usuario(String dni, String username, String password, String rol, 
                   String nombres, String apellidos, String correo, String telefono) {
        this.dni = dni;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
    }

    // Constructor vacío (útil para mapeo)
    public Usuario() {}

    // --- GETTERS (Necesarios para que la UI lea los datos) ---
    public String getDni() { return dni; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getNombreCompleto() { return nombres + " " + apellidos; }

    // --- SETTERS (Necesarios para llenar el objeto desde la BD) ---
    public void setDni(String dni) { this.dni = dni; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRol(String rol) { this.rol = rol; }
    // ... agrega el resto de setters según necesites
}*/