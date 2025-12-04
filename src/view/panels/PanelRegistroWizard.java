package view.panels;

import controller.RegistroTransaccionalController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Cliente;
import model.Mascotas; // Asegúrate de usar Mascota (singular)
import model.Usuario;
import utilidades.GestorArchivos;

import java.io.File;

public class PanelRegistroWizard extends StackPane {

    private Runnable accionVolver;
    private RegistroTransaccionalController controller;

    // Contenedor de pasos
    private StackPane contenedorPasos;
    private VBox paso1Cuenta, paso2Datos, paso3Mascota;

    // DATOS TEMPORALES (Campos del Formulario)
    // Paso 1
    private TextField txtUser;
    private PasswordField txtPass, txtPassConfirm;
    // Paso 2
    private TextField txtDni, txtNombre, txtApellido, txtTelefono, txtDireccion, txtCorreo;
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
        this.getStyleClass().add("gradient-background");
        setPadding(new Insets(20));

        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMaxWidth(700);

        // Barra Superior
        HBox topBar = new HBox(10);
        Button btnVolver = new Button("← Cancelar");
        btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> accionVolver.run());
        topBar.getChildren().add(btnVolver);

        // Contenedor de "Tarjetas" (Pasos)
        contenedorPasos = new StackPane();
        
        paso1Cuenta = crearPaso1();
        paso2Datos = crearPaso2();
        paso3Mascota = crearPaso3();

        // Inicialmente solo mostramos el paso 1
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
        
        txtPassConfirm = new PasswordField();
        txtPassConfirm.setPromptText("Confirmar Contraseña");
        txtPassConfirm.getStyleClass().add("input-modern");

        Button btnSiguiente = new Button("Siguiente >");
        btnSiguiente.getStyleClass().add("btn-primary");
        btnSiguiente.setOnAction(e -> {
            if(validarPaso1()) {
                cambiarPaso(paso1Cuenta, paso2Datos);
            }
        });

        card.getChildren().addAll(
            new Label("Usuario:"), txtUser, 
            new Label("Contraseña:"), txtPass, 
            new Label("Confirmar:"), txtPassConfirm,
            btnSiguiente
        );
        return card;
    }

    // --- PASO 2: DATOS PERSONALES ---
    private VBox crearPaso2() {
        VBox card = crearTarjetaBase("Paso 2: Datos Personales");

        txtDni = crearInput("DNI");
        txtNombre = crearInput("Nombres");
        txtApellido = crearInput("Apellidos");
        txtCorreo = crearInput("Correo Electrónico");
        txtTelefono = crearInput("Teléfono");
        txtDireccion = crearInput("Dirección");
        
        // Selector de Foto
        Button btnFoto = new Button("Subir Foto Perfil");
        imgPreviewPerfil = new ImageView();
        imgPreviewPerfil.setFitHeight(60); 
        imgPreviewPerfil.setFitWidth(60);
        imgPreviewPerfil.setPreserveRatio(true);
        
        btnFoto.setOnAction(e -> seleccionarImagen(f -> {
            archivoFotoPerfil = f;
            imgPreviewPerfil.setImage(new Image(f.toURI().toString()));
        }));

        HBox boxFoto = new HBox(10, btnFoto, imgPreviewPerfil);
        boxFoto.setAlignment(Pos.CENTER_LEFT);

        // Botones de Navegación
        HBox boxBotones = new HBox(10);
        boxBotones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnAtras = new Button("< Atrás");
        btnAtras.setOnAction(e -> cambiarPaso(paso2Datos, paso1Cuenta));
        
        Button btnSiguiente = new Button("Siguiente >");
        btnSiguiente.getStyleClass().add("btn-primary");
        btnSiguiente.setOnAction(e -> {
            if(validarPaso2()) {
                cambiarPaso(paso2Datos, paso3Mascota);
            }
        });
        boxBotones.getChildren().addAll(btnAtras, btnSiguiente);

        card.getChildren().addAll(
            new Label("DNI:"), txtDni,
            new Label("Nombres:"), txtNombre,
            new Label("Apellidos:"), txtApellido,
            new Label("Correo:"), txtCorreo,
            new Label("Teléfono:"), txtTelefono,
            new Label("Dirección:"), txtDireccion,
            new Label("Foto (Opcional):"), boxFoto,
            boxBotones
        );
        return card;
    }

    // --- PASO 3: MASCOTA Y FINALIZAR ---
    private VBox crearPaso3() {
        VBox card = crearTarjetaBase("Paso 3: Datos de la Mascota");

        txtNombreMascota = crearInput("Nombre Mascota");
        
        comboEspecie = new ComboBox<>();
        comboEspecie.getItems().addAll("Perro", "Gato", "Ave", "Roedor", "Otro");
        comboEspecie.getSelectionModel().selectFirst();
        comboEspecie.setMaxWidth(Double.MAX_VALUE); // Llenar ancho
        
        txtRaza = crearInput("Raza");
        txtEdad = crearInput("Edad (Años)");
        txtPeso = crearInput("Peso (Kg)");

        // Selector de Foto Mascota
        Button btnFoto = new Button("Foto Mascota");
        imgPreviewMascota = new ImageView();
        imgPreviewMascota.setFitHeight(60); 
        imgPreviewMascota.setFitWidth(60);
        imgPreviewMascota.setPreserveRatio(true);
        
        btnFoto.setOnAction(e -> seleccionarImagen(f -> {
            archivoFotoMascota = f;
            imgPreviewMascota.setImage(new Image(f.toURI().toString()));
        }));
        HBox boxFoto = new HBox(10, btnFoto, imgPreviewMascota);
        boxFoto.setAlignment(Pos.CENTER_LEFT);

        // Botones Finales
        HBox boxBotones = new HBox(10);
        boxBotones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnAtras = new Button("< Atrás");
        btnAtras.setOnAction(e -> cambiarPaso(paso3Mascota, paso2Datos));
        
        Button btnFinalizar = new Button("FINALIZAR REGISTRO");
        btnFinalizar.getStyleClass().add("btn-save"); 
        btnFinalizar.setOnAction(e -> procesarRegistroCompleto());
        
        boxBotones.getChildren().addAll(btnAtras, btnFinalizar);

        card.getChildren().addAll(
            new Label("Nombre Mascota:"), txtNombreMascota,
            new Label("Especie:"), comboEspecie,
            new Label("Raza:"), txtRaza,
            new Label("Edad:"), txtEdad,
            new Label("Peso:"), txtPeso,
            new Label("Foto (Opcional):"), boxFoto,
            boxBotones
        );
        return card;
    }

    // --- LÓGICA DE NEGOCIO Y GUARDADO ---
    private void procesarRegistroCompleto() {
        if (!validarPaso3()) return;

        // 1. Guardar archivos (Si se seleccionaron)
        String rutaPerfil = (archivoFotoPerfil != null) ? 
            GestorArchivos.guardarImagen(archivoFotoPerfil, "uploads/profiles") : null;
            
        String rutaMascotaImg = (archivoFotoMascota != null) ? 
            GestorArchivos.guardarImagen(archivoFotoMascota, "uploads/pets") : null;

        // 2. Crear Objeto Cliente
        // Nota: Asegúrate que tu modelo Cliente tenga este constructor (DNI, Nombres, Apellidos, Correo, Tel, Dir)
        Cliente cliente = new Cliente(
            txtDni.getText(), 
            txtNombre.getText(), 
            txtApellido.getText(), 
            txtCorreo.getText(),
            txtTelefono.getText(), 
            txtDireccion.getText()
        );

        // 3. Crear Objeto Usuario
        Usuario usuario = new Usuario(
            txtUser.getText(), 
            txtPass.getText(), 
            txtDni.getText(), 
            "cliente", // Rol por defecto
            rutaPerfil
        );

        // 4. Crear Objeto Mascota e intentar guardar todo
        try {
            int edad = Integer.parseInt(txtEdad.getText());
            double peso = Double.parseDouble(txtPeso.getText());
            
            // Constructor: nombre, especie, raza, edad, peso, dniCliente
            Mascotas mascota = new Mascotas(
                txtNombreMascota.getText(),
                comboEspecie.getValue(),
                txtRaza.getText(),
                edad,
                peso,
                txtDni.getText()
            );
            mascota.setFotoMascotaRuta(rutaMascotaImg);

            // LLAMADA A CONTROLADOR TRANSACCIONAL
            boolean exito = controller.registrarCompleto(usuario, cliente, mascota);

            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Exitoso", 
                    "¡Bienvenido! Su cuenta y su mascota han sido registradas correctamente.");
                accionVolver.run(); // Volver al Login
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", 
                    "No se pudo completar el registro.\nEs posible que el DNI o el Usuario ya existan.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Datos Inválidos", "La Edad y el Peso deben ser números válidos.");
        }
    }

    // --- VALIDACIONES ---
    
    private boolean validarPaso1() {
        if (txtUser.getText().isEmpty() || txtPass.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor ingrese usuario y contraseña.");
            return false;
        }
        if (!txtPass.getText().equals(txtPassConfirm.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Contraseña", "Las contraseñas no coinciden.");
            return false;
        }
        return true;
    }

    private boolean validarPaso2() {
        if (txtDni.getText().isEmpty() || txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos Incompletos", "DNI, Nombre y Apellido son obligatorios.");
            return false;
        }
        if (!txtDni.getText().matches("\\d+")) { // Solo números
             mostrarAlerta(Alert.AlertType.WARNING, "DNI Inválido", "El DNI debe contener solo números.");
             return false;
        }
        return true;
    }
    
    private boolean validarPaso3() {
        if (txtNombreMascota.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan Datos", "El nombre de la mascota es obligatorio.");
            return false;
        }
        return true;
    }

    // --- UTILIDADES VISUALES ---
    
    private VBox crearTarjetaBase(String titulo) {
        VBox card = new VBox(15);
        card.getStyleClass().add("register-card"); // Estilo definido en style.css
        card.setMaxWidth(500);
        card.setPadding(new Insets(30));
        
        Text t = new Text(titulo);
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
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
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png", "*.jpeg"));
        File f = fc.showOpenDialog(this.getScene().getWindow());
        if (f != null) onSelect.accept(f);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msj) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msj);
        alert.showAndWait();
    }
}