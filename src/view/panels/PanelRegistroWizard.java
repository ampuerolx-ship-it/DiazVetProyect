package view.panels;

import controller.RegistroTransaccionalController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.Cliente;
import model.Mascotas;
import model.Usuario;
import utilidades.GestorArchivos;

import java.io.File;

public class PanelRegistroWizard extends StackPane {

    private final Runnable accionVolver;
    private final RegistroTransaccionalController controller;

    // Contenedor de pasos
    private StackPane contenedorPasos;
    private VBox paso1Cuenta, paso2Datos, paso3Mascota;

    // DATOS TEMPORALES
    private TextField txtUser;
    private PasswordField txtPass, txtPassConfirm;
    
    private TextField txtDni, txtNombre, txtApellido, txtTelefono, txtDireccion, txtCorreo;
    private File archivoFotoPerfil;
    private ImageView imgPreviewPerfil;
    
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
        // Fondo con gradiente (Estilo definido en CSS)
        this.getStyleClass().add("gradient-background");
        setPadding(new Insets(30));

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMaxWidth(700); // Ancho máximo de la tarjeta

        // --- Barra Superior (Navegación) ---
        HBox topBar = new HBox(10);
        Button btnVolver = new Button("← Cancelar Registro");
        btnVolver.getStyleClass().add("btn-secondary"); // Texto blanco/rosa según tu CSS
        btnVolver.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-color: transparent;");
        btnVolver.setOnAction(e -> accionVolver.run());
        topBar.getChildren().add(btnVolver);

        // --- Contenedor de Tarjetas ---
        contenedorPasos = new StackPane();
        
        paso1Cuenta = crearPaso1();
        paso2Datos = crearPaso2();
        paso3Mascota = crearPaso3();

        // Lógica de visibilidad inicial
        mostrarPaso(paso1Cuenta);

        contenedorPasos.getChildren().addAll(paso1Cuenta, paso2Datos, paso3Mascota);
        
        mainLayout.getChildren().addAll(topBar, contenedorPasos);
        getChildren().add(mainLayout);
    }

    // =================================================================================
    // PASO 1: CUENTA DE USUARIO
    // =================================================================================
    private VBox crearPaso1() {
        VBox card = crearTarjetaBase("Paso 1 de 3", "Credenciales de Acceso");
        
        txtUser = crearInput("Nombre de Usuario", "Ej: juan.perez");
        txtPass = new PasswordField(); 
        txtPass.setPromptText("Contraseña");
        txtPass.getStyleClass().add("input-modern");
        
        txtPassConfirm = new PasswordField();
        txtPassConfirm.setPromptText("Repetir Contraseña");
        txtPassConfirm.getStyleClass().add("input-modern");

        // Layout Vertical simple para el login
        VBox form = new VBox(15);
        form.getChildren().addAll(
            crearCampoLabel("Usuario:", txtUser),
            crearCampoLabel("Contraseña:", txtPass),
            crearCampoLabel("Confirmar:", txtPassConfirm)
        );

        Button btnSiguiente = crearBotonAccion("Siguiente >", "btn-primary");
        btnSiguiente.setOnAction(e -> {
            if(validarPaso1()) mostrarPaso(paso2Datos);
        });

        HBox buttonBar = new HBox(btnSiguiente);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(20, 0, 0, 0));

        card.getChildren().addAll(form, new Separator(), buttonBar);
        return card;
    }

    // =================================================================================
    // PASO 2: DATOS DEL CLIENTE (DISEÑO EN FILAS)
    // =================================================================================
    private VBox crearPaso2() {
        VBox card = crearTarjetaBase("Paso 2 de 3", "Información del Dueño");

        txtDni = crearInput("DNI", "8 dígitos");
        txtTelefono = crearInput("Teléfono", "Ej: 999...");
        txtNombre = crearInput("Nombres", "");
        txtApellido = crearInput("Apellidos", "");
        txtCorreo = crearInput("Correo Electrónico", "ejemplo@mail.com");
        txtDireccion = crearInput("Dirección", "Av. Principal 123");

        // --- FILA 1: DNI y Teléfono ---
        HBox row1 = new HBox(15);
        row1.getChildren().addAll(
            crearCampoLabel("DNI:", txtDni, 0.4), // 40% del ancho
            crearCampoLabel("Teléfono:", txtTelefono, 0.6) // 60% del ancho
        );

        // --- FILA 2: Nombres y Apellidos ---
        HBox row2 = new HBox(15);
        row2.getChildren().addAll(
            crearCampoLabel("Nombres:", txtNombre, 0.5),
            crearCampoLabel("Apellidos:", txtApellido, 0.5)
        );

        // --- SECCIÓN FOTO ---
        HBox photoBox = crearSeccionFoto("Foto de Perfil:", 
            f -> {
                archivoFotoPerfil = f;
                actualizarPreview(imgPreviewPerfil, f);
            }
        );
        imgPreviewPerfil = (ImageView) photoBox.getChildren().get(2); // Guardamos referencia

        // --- BOTONERA ---
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(20, 0, 0, 0));
        
        Button btnAtras = crearBotonAccion("< Atrás", "btn-secondary");
        btnAtras.setOnAction(e -> mostrarPaso(paso1Cuenta));
        
        Button btnSiguiente = crearBotonAccion("Siguiente >", "btn-primary");
        btnSiguiente.setOnAction(e -> {
            if(validarPaso2()) mostrarPaso(paso3Mascota);
        });
        
        buttonBar.getChildren().addAll(btnAtras, btnSiguiente);

        // Agregamos todo a la tarjeta
        card.getChildren().addAll(
            row1, 
            row2, 
            crearCampoLabel("Correo:", txtCorreo),
            crearCampoLabel("Dirección:", txtDireccion),
            new Separator(),
            photoBox,
            buttonBar
        );
        return card;
    }

    // =================================================================================
    // PASO 3: MASCOTA Y FINALIZAR
    // =================================================================================
    private VBox crearPaso3() {
        VBox card = crearTarjetaBase("Paso 3 de 3", "Datos de la Mascota");

        txtNombreMascota = crearInput("Nombre", "Ej: Firulais");
        
        comboEspecie = new ComboBox<>();
        comboEspecie.getItems().addAll("Perro", "Gato", "Ave", "Roedor", "Otro");
        comboEspecie.getSelectionModel().selectFirst();
        comboEspecie.setMaxWidth(Double.MAX_VALUE);
        comboEspecie.getStyleClass().add("input-modern");
        
        txtRaza = crearInput("Raza", "Ej: Mestizo");
        txtEdad = crearInput("Edad", "Años");
        txtPeso = crearInput("Peso", "Kg");

        // --- FILA 1: Nombre y Especie ---
        HBox row1 = new HBox(15);
        row1.getChildren().addAll(
            crearCampoLabel("Nombre:", txtNombreMascota, 0.6),
            crearCampoLabel("Especie:", comboEspecie, 0.4)
        );

        // --- FILA 2: Raza, Edad y Peso ---
        HBox row2 = new HBox(15);
        row2.getChildren().addAll(
            crearCampoLabel("Raza:", txtRaza, 0.5),
            crearCampoLabel("Edad:", txtEdad, 0.25),
            crearCampoLabel("Peso:", txtPeso, 0.25)
        );

        // --- SECCIÓN FOTO ---
        HBox photoBox = crearSeccionFoto("Foto de Mascota:", 
            f -> {
                archivoFotoMascota = f;
                actualizarPreview(imgPreviewMascota, f);
            }
        );
        imgPreviewMascota = (ImageView) photoBox.getChildren().get(2);

        // --- BOTONERA FINAL ---
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(20, 0, 0, 0));
        
        Button btnAtras = crearBotonAccion("< Atrás", "btn-secondary");
        btnAtras.setOnAction(e -> mostrarPaso(paso2Datos));
        
        Button btnFinalizar = crearBotonAccion("FINALIZAR REGISTRO", "btn-save"); // Estilo destacado
        btnFinalizar.setStyle("-fx-background-color: linear-gradient(to right, #66BB6A, #43A047); -fx-text-fill: white; -fx-font-weight: bold;"); // Verde éxito
        btnFinalizar.setOnAction(e -> procesarRegistroCompleto());
        
        buttonBar.getChildren().addAll(btnAtras, btnFinalizar);

        card.getChildren().addAll(row1, row2, new Separator(), photoBox, buttonBar);
        return card;
    }

    // =================================================================================
    // LÓGICA DE NEGOCIO (TRANSACCIONAL)
    // =================================================================================
    private void procesarRegistroCompleto() {
        if (!validarPaso3()) return;

        // 1. Guardar archivos
        String rutaPerfil = (archivoFotoPerfil != null) ? 
            GestorArchivos.guardarImagen(archivoFotoPerfil, "uploads/profiles") : null;
        String rutaMascota = (archivoFotoMascota != null) ? 
            GestorArchivos.guardarImagen(archivoFotoMascota, "uploads/pets") : null;

        // 2. Construir Modelos (OJO: El orden de Cliente puede tener un error posicional en el original)
        String nombreCompleto = txtNombre.getText() + " " + txtApellido.getText();
        
        Cliente cliente = new Cliente(
            txtDni.getText(), 
            txtNombre.getText(), 
            txtApellido.getText(), 
            txtTelefono.getText(),  
            txtDireccion.getText(), 
            txtCorreo.getText()
        );

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
                0, // ID: 0 para inserción (auto-incremento)
                txtNombreMascota.getText(),
                comboEspecie.getValue(),
                txtRaza.getText(),
                edad,
                peso,
                txtDni.getText(), // DNI del Dueño (Cliente)
                null, // nombreDueno (Se obtiene en el DAO al leer)
                rutaMascota, // rutaFotoMascota
                null // fechaRegistro (Se establece en la BD)
            );
            // mascota.setFotoMascotaRuta(rutaMascota); // Línea eliminada, ya se pasa en el constructor

            // 3. Transacción
            boolean exito = controller.registrarCompleto(usuario, cliente, mascota);

            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "¡Registro Exitoso!", 
                    "Bienvenido a Días Vet.\nSu cuenta ha sido creada correctamente.");
                accionVolver.run();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "El DNI o Usuario ya existen.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "Edad y Peso deben ser numéricos.");
        }
    }
    /*private void procesarRegistroCompleto() {
        if (!validarPaso3()) return;

        // 1. Guardar archivos
        String rutaPerfil = (archivoFotoPerfil != null) ? 
            GestorArchivos.guardarImagen(archivoFotoPerfil, "uploads/profiles") : null;
        String rutaMascota = (archivoFotoMascota != null) ? 
            GestorArchivos.guardarImagen(archivoFotoMascota, "uploads/pets") : null;

        // 2. Construir Modelos (Concatenando Nombre+Apellido para la BD)
        String nombreCompleto = txtNombre.getText() + " " + txtApellido.getText();
        
        Cliente cliente = new Cliente(
            txtDni.getText(), 
            txtNombre.getText(), 
            txtApellido.getText(), 
            txtCorreo.getText(),
            txtTelefono.getText(), 
            txtDireccion.getText()
        );

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
                txtNombreMascota.getText(),
                comboEspecie.getValue(),
                txtRaza.getText(),
                edad,
                peso,
                txtDni.getText()
            );
            mascota.setFotoMascotaRuta(rutaMascota);

            // 3. Transacción
            boolean exito = controller.registrarCompleto(usuario, cliente, mascota);

            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "¡Registro Exitoso!", 
                    "Bienvenido a Días Vet.\nSu cuenta ha sido creada correctamente.");
                accionVolver.run();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "El DNI o Usuario ya existen.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "Edad y Peso deben ser numéricos.");
        }
    }*/

    // =================================================================================
    // UTILIDADES DE UI & DISEÑO
    // =================================================================================
    
    private VBox crearTarjetaBase(String paso, String titulo) {
        VBox card = new VBox(15);
        card.getStyleClass().add("register-card"); // Tarjeta blanca con sombra
        card.setPadding(new Insets(30));
        
        Label lblPaso = new Label(paso);
        lblPaso.setTextFill(Color.web("#90CAF9")); // Azul suave
        lblPaso.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        
        Text txtTitulo = new Text(titulo);
        txtTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        txtTitulo.setFill(Color.web("#546E7A")); // Gris azulado oscuro
        
        card.getChildren().addAll(lblPaso, txtTitulo, new Separator());
        return card;
    }

    // Crea un grupo vertical de Label + Input, con ancho opcional
    private VBox crearCampoLabel(String texto, Control input) {
        return crearCampoLabel(texto, input, 1.0);
    }

    private VBox crearCampoLabel(String texto, Control input, double porcentajeAncho) {
        VBox box = new VBox(5);
        Label lbl = new Label(texto);
        lbl.getStyleClass().add("label-modern"); // Estilo definido en CSS
        
        input.setMaxWidth(Double.MAX_VALUE); // Llenar el contenedor
        box.getChildren().addAll(lbl, input);
        
        HBox.setHgrow(box, Priority.ALWAYS); // Crecer en HBox
        box.prefWidthProperty().bind(this.widthProperty().multiply(porcentajeAncho)); // Responsive
        return box;
    }

    private HBox crearSeccionFoto(String titulo, java.util.function.Consumer<File> onSelect) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label lbl = new Label(titulo);
        lbl.getStyleClass().add("label-modern");
        
        Button btnUpload = new Button("Seleccionar Imagen");
        btnUpload.getStyleClass().add("btn-clean"); // Estilo outline
        
        ImageView preview = new ImageView();
        preview.setFitHeight(60);
        preview.setFitWidth(60);
        preview.setPreserveRatio(true);
        // Hacemos que la imagen sea circular
        Circle clip = new Circle(30, 30, 30);
        preview.setClip(clip);
        
        btnUpload.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png", "*.jpeg"));
            File f = fc.showOpenDialog(this.getScene().getWindow());
            if (f != null) onSelect.accept(f);
        });
        
        box.getChildren().addAll(lbl, btnUpload, preview);
        return box;
    }

    private void actualizarPreview(ImageView view, File file) {
        if (file != null) {
            view.setImage(new Image(file.toURI().toString()));
        }
    }

    private TextField crearInput(String prompt, String tooltip) {
        TextField t = new TextField();
        t.setPromptText(prompt);
        t.setTooltip(new Tooltip(tooltip));
        t.getStyleClass().add("input-modern");
        return t;
    }

    private Button crearBotonAccion(String texto, String claseCss) {
        Button b = new Button(texto);
        b.getStyleClass().add(claseCss);
        b.setPrefHeight(40);
        b.setMinWidth(120);
        return b;
    }

    private void mostrarPaso(VBox pasoMostrar) {
        paso1Cuenta.setVisible(false);
        paso2Datos.setVisible(false);
        paso3Mascota.setVisible(false);
        pasoMostrar.setVisible(true);
    }

    // --- VALIDACIONES SIMPLES ---
    private boolean validarPaso1() {
        if (txtUser.getText().isEmpty() || txtPass.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Ingrese usuario y contraseña.");
            return false;
        }
        if (!txtPass.getText().equals(txtPassConfirm.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Las contraseñas no coinciden.");
            return false;
        }
        return true;
    }

    private boolean validarPaso2() {
        if (txtDni.getText().isEmpty() || txtNombre.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan Datos", "DNI y Nombre son obligatorios.");
            return false;
        }
        if (!txtDni.getText().matches("\\d{8}")) {
             mostrarAlerta(Alert.AlertType.WARNING, "DNI Inválido", "El DNI debe tener 8 dígitos.");
             return false;
        }
        return true;
    }
    
    private boolean validarPaso3() {
        if (txtNombreMascota.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan Datos", "Nombre de mascota es obligatorio.");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msj) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msj);
        alert.showAndWait();
    }
}

/*package view.panels;

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
import model.Mascotas; // Asegúrate de usar Mascotas (singular)
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
        VBox card = crearTarjetaBase("Paso 3: Datos de la Mascotas");

        txtNombreMascota = crearInput("Nombre Mascotas");
        
        comboEspecie = new ComboBox<>();
        comboEspecie.getItems().addAll("Perro", "Gato", "Ave", "Roedor", "Otro");
        comboEspecie.getSelectionModel().selectFirst();
        comboEspecie.setMaxWidth(Double.MAX_VALUE); // Llenar ancho
        
        txtRaza = crearInput("Raza");
        txtEdad = crearInput("Edad (Años)");
        txtPeso = crearInput("Peso (Kg)");

        // Selector de Foto Mascotas
        Button btnFoto = new Button("Foto Mascotas");
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
            new Label("Nombre Mascotas:"), txtNombreMascota,
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

        // 4. Crear Objeto Mascotas e intentar guardar todo
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
}*/