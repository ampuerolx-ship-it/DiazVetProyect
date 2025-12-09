package model;

import javafx.beans.property.*;

// Debe implementar Comparable para poder ser usada en el Árbol AVL (se comparará por ID)
public class Mascotas implements Comparable<Mascotas> {
    private final IntegerProperty id;
    private final StringProperty nombre;
    private final StringProperty especie;
    private final StringProperty raza;
    private final IntegerProperty edad;
    private final DoubleProperty peso;
    private final StringProperty dniCliente;
    private final StringProperty nombreDueno; // Campo para mostrar en UI
    private final StringProperty fotoMascotaRuta;
    private final StringProperty fechaRegistro;

    public Mascotas() {
        this(0, null, null, null, 0, 0.0, null, null, null, null);
    }

    public Mascotas(int id, String nombre, String especie, String raza, int edad, double peso, String dniCliente, String nombreDueno, String fotoRuta, String fechaRegistro) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.especie = new SimpleStringProperty(especie);
        this.raza = new SimpleStringProperty(raza);
        this.edad = new SimpleIntegerProperty(edad);
        this.peso = new SimpleDoubleProperty(peso);
        this.dniCliente = new SimpleStringProperty(dniCliente);
        this.nombreDueno = new SimpleStringProperty(nombreDueno);
        this.fotoMascotaRuta = new SimpleStringProperty(fotoRuta);
        this.fechaRegistro = new SimpleStringProperty(fechaRegistro);
    }

    // --- Properties ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty especieProperty() { return especie; }
    public StringProperty razaProperty() { return raza; }
    public IntegerProperty edadProperty() { return edad; }
    public DoubleProperty pesoProperty() { return peso; }
    public StringProperty dniClienteProperty() { return dniCliente; }
    public StringProperty nombreDuenoProperty() { return nombreDueno; }
    public StringProperty fotoMascotaRutaProperty() { return fotoMascotaRuta; }
    public StringProperty fechaRegistroProperty() { return fechaRegistro; }

    // --- Getters & Setters ---
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    // ... (otros getters y setters) ...
    public String getNombreDueno() { return nombreDueno.get(); }
    public void setNombreDueno(String nombreDueno) { this.nombreDueno.set(nombreDueno); }
    public String getDniCliente() { return dniCliente.get(); }
    public void setDniCliente(String dniCliente) { this.dniCliente.set(dniCliente); }
    public String getEspecie() { return especie.get(); }
    public void setEspecie(String especie) { this.especie.set(especie); }
    public String getRaza() { return raza.get(); }
    public void setRaza(String raza) { this.raza.set(raza); }
    public int getEdad() { return edad.get(); }
    public void setEdad(int edad) { this.edad.set(edad); }
    public double getPeso() { return peso.get(); }
    public void setPeso(double peso) { this.peso.set(peso); }
    public String getFotoMascotaRuta() { return fotoMascotaRuta.get(); }
    public void setFotoMascotaRuta(String fotoMascotaRuta) { this.fotoMascotaRuta.set(fotoMascotaRuta); }
    public String getFechaRegistro() { return fechaRegistro.get(); }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro.set(fechaRegistro); }

    // Implementación de Comparable (CRUCIAL para el AVL, usamos ID)
    @Override
    public int compareTo(Mascotas otra) {
        // Comparación por ID, que es la clave primaria
        return Integer.compare(this.getId(), otra.getId());
    }
    
    // Método para la búsqueda en el AVL por nombre
    public boolean nombreContiene(String filtro) {
        return this.nombre.get().toLowerCase().contains(filtro.toLowerCase());
    }
}