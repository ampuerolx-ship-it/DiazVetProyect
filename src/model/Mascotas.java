package model;

public class Mascotas {
    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private double peso;
    private String dniCliente;  
    private String fotoMascotaRuta;

    // Constructor completo para recuperación de BD
    public Mascotas(int id, String nombre, String especie, String raza, int edad, double peso, String dniCliente) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.peso = peso;
        this.dniCliente = dniCliente;
        this.fotoMascotaRuta = fotoMascotaRuta;
    }

    // Constructor simple para registro nuevo (sin ID aún)
    public Mascotas(String nombre, String especie, String raza, int edad, double peso, String dniCliente) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.peso = peso;
        this.dniCliente = dniCliente;
        this.fotoMascotaRuta = fotoMascotaRuta;
    }

    // --- Getters y Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getDniCliente() { return dniCliente; }
    public void setDniCliente(String dniCliente) { this.dniCliente = dniCliente; }

    public String getFotoMascotaRuta() { return fotoMascotaRuta; }
    public void setFotoMascotaRuta(String fotoMascotaRuta) { this.fotoMascotaRuta = fotoMascotaRuta; }
    
    @Override
    public String toString() {
        return nombre + " (" + especie + ")";
    }
}