/*package view.panels;

import controller.ClienteController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class PanelRegistroCliente extends StackPane {

    private TextField txtDni, txtNombre, txtApellido, txtTelefono, txtCorreo, txtDireccion;
    private ClienteController controller;
    private Runnable accionVolver;

    public PanelRegistroCliente(Runnable accionVolver) {
        this.controller = new ClienteController();
        this.accionVolver = accionVolver;
        initUI();
    }

    private void initUI() {
        // 1. FONDO DEGRADADO (Clase CSS .gradient-background)
        this.getStyleClass().add("gradient-background");
        this.setPadding(new Insets(20));

        // 2. CONTENEDOR PRINCIPAL (Layout Vertical)
        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setMaxWidth(600); // Ancho máximo para que se vea elegante

        // --- BARRA DE NAVEGACIÓN (Fuera de la tarjeta) ---
        HBox navBar = new HBox();
        navBar.setAlignment(Pos.CENTER_LEFT);
        navBar.setPadding(new Insets(0, 0, 10, 0));
        
        Button btnVolver = new Button("← Volver");
        btnVolver.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        btnVolver.setOnAction(e -> {
            if (accionVolver != null) accionVolver.run();
        });
        navBar.getChildren().add(btnVolver);

        // --- TARJETA BLANCA (La Card) ---
        VBox card = new VBox(20);
        card.getStyleClass().add("register-card"); // Clase CSS definida arriba
        
        // Encabezado dentro de la tarjeta
        VBox headerBox = new VBox(5);
        Text titulo = new Text("Registrar Nuevo Cliente");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titulo.setFill(Color.web("#37474F"));
        
        Text subtitulo = new Text("Ingrese los datos del dueño de la mascota");
        subtitulo.setFont(Font.font("Segoe UI", 14));
        subtitulo.setFill(Color.GRAY);
        
        headerBox.getChildren().addAll(titulo, subtitulo);

        // --- FORMULARIO ---
        // Usamos VBox para apilar los campos verticalmente como en la imagen
        VBox formContainer = new VBox(15); 

        txtDni = crearCampo(formContainer, "📄 DNI:", "DNI / Documento");
        txtNombre = crearCampo(formContainer, "👤 Nombres:", "Nombres");
        txtApellido = crearCampo(formContainer, "👤 Apellidos:", "Apellidos");
        txtTelefono = crearCampo(formContainer, "📞 Teléfono:", "Teléfono / Celular");
        txtCorreo = crearCampo(formContainer, "✉ Correo:", "Correo Electrónico");
        txtDireccion = crearCampo(formContainer, "📍 Dirección:", "Dirección de Residencia");

        // --- BOTONES DE ACCIÓN ---
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnLimpiar = new Button("♻ Limpiar");
        btnLimpiar.getStyleClass().add("btn-clean");
        btnLimpiar.setPrefHeight(40);
        btnLimpiar.setPrefWidth(120);
        btnLimpiar.setOnAction(e -> limpiarCampos());

        Button btnGuardar = new Button("💾 GUARDAR CLIENTE");
        btnGuardar.getStyleClass().add("btn-save");
        btnGuardar.setPrefHeight(40);
        btnGuardar.setPrefWidth(200);
        HBox.setHgrow(btnGuardar, Priority.ALWAYS); // Que ocupe espacio
        
        btnGuardar.setOnAction(e -> {
            boolean exito = controller.registrarCliente()
                txtDni.getText(), txtNombre.getText(), txtApellido.getText(),
                txtTelefono.getText(), txtCorreo.getText(), txtDireccion.getText()
            );
            if (exito) limpiarCampos();
        });

        buttonBox.getChildren().addAll(btnLimpiar, btnGuardar);

        // Armar la tarjeta
        card.getChildren().addAll(headerBox, formContainer, buttonBox);

        // Añadimos scroll por si la pantalla es pequeña
        ScrollPane scroll = new ScrollPane(card);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Armar layout final
        mainLayout.getChildren().addAll(navBar, scroll);
        
        // Centrar todo en el StackPane
        this.getChildren().add(mainLayout);
    }

    // --- MÉTODO AUXILIAR PARA CREAR CAMPOS (ESTILO STACKED) ---
    private TextField crearCampo(VBox container, String labelText, String placeholder) {
        VBox fieldGroup = new VBox(5);
        
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("label-modern");
        
        TextField txt = new TextField();
        txt.setPromptText(placeholder);
        txt.getStyleClass().add("input-modern");
        
        fieldGroup.getChildren().addAll(lbl, txt);
        container.getChildren().add(fieldGroup);
        
        return txt;
    }

    private void limpiarCampos() {
        txtDni.clear(); txtNombre.clear(); txtApellido.clear();
        txtTelefono.clear(); txtCorreo.clear(); txtDireccion.clear();
    }
}*/