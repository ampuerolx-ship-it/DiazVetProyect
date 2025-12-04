package view.panels;

import controller.RegistroTransaccionalController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Cliente;
import model.Mascotas;
import model.Usuario;
import utilidades.GestorArchivos;

import java.io.File;
import javafx.scene.paint.Color;

public class PanelRegistroWizard extends StackPane {

    private Runnable accionVolver;
    private RegistroTransaccionalController controller;

    // Contenedor de pasos (CardLayout behavior)
    private StackPane contenedorPasos;
    private VBox paso1Cuenta, paso2Datos, paso3Mascota;

    // DATOS TEMPORALES
    // Paso 1
    private TextField txtUser, txtPass;
    // Paso 2
    private TextField txtDni, txtNombre, txtApellido, txtTelefono, txtDireccion;
    private File archivoFotoPerfil;
    private ImageView imgPreviewPerfil;
    // Paso 3
    private TextField txtNombreMascota, txtRaza, txtEdad, txtPeso;
    private ComboBox<String> comboEspecie;
    private File archivoFotoMascota;
    private ImageView imgPreviewMascota;

    public PanelRegistroWizard(Runnable accionVolver) {
        this.accionVolver = accionVolver;
        this.controller = new RegistroTransaccionalController();
        initUI();
    }

    private void initUI() {
        this.getStyleClass().add("gradient-background"); // Usamos tu estilo CSS
        setPadding(new Insets(20));

        // Layout Principal
        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMaxWidth(700);

        // Barra Superior
        HBox topBar = new HBox(10);
        Button btnVolver = new Button("← Cancelar");
        btnVolver.getStyleClass().add("btn-secondary"); // Estilo CSS
        btnVolver.setOnAction(e -> accionVolver.run());
        topBar.getChildren().add(btnVolver);

        // Contenedor de "Tarjetas"
        contenedorPasos = new StackPane();
        
        paso1Cuenta = crearPaso1();
        paso2Datos = crearPaso2();
        paso3Mascota = crearPaso3();

        // Mostrar solo el primero
        paso2Datos.setVisible(false);
        paso3Mascota.setVisible(false);

        contenedorPasos.getChildren().addAll(paso1Cuenta, paso2Datos, paso3Mascota);
        mainLayout.getChildren().addAll(topBar, contenedorPasos);
        getChildren().add(mainLayout);
    }

    // --- PASO 1: CUENTA ---
    private VBox crearPaso1() {
        VBox card = crearTarjetaBase("Paso 1: Datos de Cuenta");
        
        txtUser = crearInput("Nombre de Usuario (Login)");
        txtPass = new PasswordField(); 
        txtPass.setPromptText("Contraseña");
        txtPass.getStyleClass().add("input-modern");

        Button btnSiguiente = new Button("Siguiente >");
        btnSiguiente.getStyleClass().add("btn-primary");
        btnSiguiente.setOnAction(e -> {
            if(validarPaso1()) cambiarPaso(paso1Cuenta, paso2Datos);
        });

        card.getChildren().addAll(new Label("Usuario:"), txtUser, new Label("Contraseña:"), txtPass, btnSiguiente);
        return card;
    }

    // --- PASO 2: DATOS PERSONALES ---
    private VBox crearPaso2() {
        VBox card = crearTarjetaBase("Paso 2: Datos Personales");

        txtDni = crearInput("DNI");
        txtNombre = crearInput("Nombres");
        txtApellido = crearInput("Apellidos");
        txtTelefono = crearInput("Teléfono");
        txtDireccion = crearInput("Dirección");
        
        // Subida de Foto
        Button btnFoto = new Button("Subir Foto Perfil");
        imgPreviewPerfil = new ImageView();
        imgPreviewPerfil.setFitHeight(50); imgPreviewPerfil.setFitWidth(50);
        
        btnFoto.setOnAction(e -> seleccionarImagen(f -> {
            archivoFotoPerfil = f;
            imgPreviewPerfil.setImage(new Image(f.toURI().toString()));
        }));

        HBox boxFoto = new HBox(10, btnFoto, imgPreviewPerfil);

        HBox boxBotones = new HBox(10);
        Button btnAtras = new Button("< Atrás");
        btnAtras.setOnAction(e -> cambiarPaso(paso2Datos, paso1Cuenta));
        Button btnSiguiente = new Button("Siguiente >");
        btnSiguiente.getStyleClass().add("btn-primary");
        btnSiguiente.setOnAction(e -> {
            if(validarPaso2()) cambiarPaso(paso2Datos, paso3Mascota);
        });
        boxBotones.getChildren().addAll(btnAtras, btnSiguiente);

        card.getChildren().addAll(
            new Label("DNI:"), txtDni,
            new Label("Nombre:"), txtNombre,
            new Label("Apellido:"), txtApellido,
            new Label("Teléfono:"), txtTelefono,
            new Label("Dirección:"), txtDireccion,
            new Label("Foto:"), boxFoto,
            boxBotones
        );
        return card;
    }

    // --- PASO 3: MASCOTA Y FINALIZAR ---
    private VBox crearPaso3() {
        VBox card = crearTarjetaBase("Paso 3: Datos de la Mascota");

        txtNombreMascota = crearInput("Nombre Mascota");
        comboEspecie = new ComboBox<>();
        comboEspecie.getItems().addAll("Perro", "Gato", "Otro");
        comboEspecie.getSelectionModel().selectFirst();
        txtRaza = crearInput("Raza");
        txtEdad = crearInput("Edad (Años)");
        txtPeso = crearInput("Peso (Kg)");

        // Foto Mascota
        Button btnFoto = new Button("Foto Mascota");
        imgPreviewMascota = new ImageView();
        imgPreviewMascota.setFitHeight(50); imgPreviewMascota.setFitWidth(50);
        
        btnFoto.setOnAction(e -> seleccionarImagen(f -> {
            archivoFotoMascota = f;
            imgPreviewMascota.setImage(new Image(f.toURI().toString()));
        }));
        HBox boxFoto = new HBox(10, btnFoto, imgPreviewMascota);

        HBox boxBotones = new HBox(10);
        Button btnAtras = new Button("< Atrás");
        btnAtras.setOnAction(e -> cambiarPaso(paso3Mascota, paso2Datos));
        
        Button btnFinalizar = new Button("FINALIZAR REGISTRO");
        btnFinalizar.getStyleClass().add("btn-save"); // Estilo verde o destacado
        btnFinalizar.setOnAction(e -> procesarRegistroCompleto());
        
        boxBotones.getChildren().addAll(btnAtras, btnFinalizar);

        card.getChildren().addAll(
            new Label("Nombre Mascota:"), txtNombreMascota,
            new Label("Especie:"), comboEspecie,
            new Label("Raza:"), txtRaza,
            new Label("Edad:"), txtEdad,
            new Label("Peso:"), txtPeso,
            new Label("Foto:"), boxFoto,
            boxBotones
        );
        return card;
    }

    // --- LÓGICA DE NEGOCIO ---
    private void procesarRegistroCompleto() {
        // 1. Guardar archivos físicamente
        String rutaPerfil = GestorArchivos.guardarImagen(archivoFotoPerfil, "uploads/profiles");
        String rutaMascota = GestorArchivos.guardarImagen(archivoFotoMascota, "uploads/pets");

        // 2. Crear Modelos
        Cliente cliente = new Cliente(
            txtDni.getText(), 
            txtNombre.getText() + " " + txtApellido.getText(), 
            txtTelefono.getText(), 
            txtDireccion.getText()
        );

        // Nota: En un sistema real, hashea la password aquí
        Usuario usuario = new Usuario(
            txtUser.getText(), 
            txtPass.getText(), 
            txtDni.getText(), 
            "cliente", 
            rutaPerfil
        );

        try {
            int edad = Integer.parseInt(txtEdad.getText());
            double peso = Double.parseDouble(txtPeso.getText());
            
            Mascotas mascota = new Mascotas(
                0, // ID autogenerado
                txtNombreMascota.getText(),
                comboEspecie.getValue(),
                txtRaza.getText(),
                edad,
                peso,
                txtDni.getText()
            );
            mascota.setFotoMascotaRuta(rutaMascota);

            // 3. Llamar al Controlador Transaccional
            boolean exito = controller.registrarCompleto(usuario, cliente, mascota);

            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Registro completado. Ahora puede iniciar sesión.");
                accionVolver.run();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo completar el registro. Verifique que el DNI o Usuario no existan.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Edad y Peso deben ser números.");
        }
    }

    // --- UTILIDADES VISUALES ---
    private VBox crearTarjetaBase(String titulo) {
        VBox card = new VBox(10);
        card.getStyleClass().add("register-card"); // Tu estilo CSS de tarjeta blanca
        card.setMaxWidth(500);
        
        Text t = new Text(titulo);
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        t.setFill(Color.web("#546E7A"));
        
        card.getChildren().add(t);
        return card;
    }

    private void cambiarPaso(VBox actual, VBox siguiente) {
        actual.setVisible(false);
        siguiente.setVisible(true);
    }

    private TextField crearInput(String prompt) {
        TextField t = new TextField();
        t.setPromptText(prompt);
        t.getStyleClass().add("input-modern");
        return t;
    }

    private void seleccionarImagen(java.util.function.Consumer<File> onSelect) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png"));
        File f = fc.showOpenDialog(this.getScene().getWindow());
        if (f != null) onSelect.accept(f);
    }
    
    private boolean validarPaso1() {
        return !txtUser.getText().isEmpty() && !txtPass.getText().isEmpty();
    }
    private boolean validarPaso2() {
        return !txtDni.getText().isEmpty(); // Agregar más validaciones aquí
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msj) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(titulo);
        alert.setContentText(msj);
        alert.showAndWait();
    }
}