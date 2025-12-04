import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.animation.FadeTransition;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Objects;
import java.net.URL;

// IMPORTACIONES LÓGICAS (No tocamos nada aquí)
import database.ConexionDB;
import database.dao.UsuarioDAO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Usuario;
import view.panels.PanelRegistroWizard;

public class LoginAppMain extends Application {
    
    // Colores base para usar donde CSS no llega fácil
    private static final String COLOR_FONDO_IZQ = "#90CAF9"; 

    @Override
    public void start(Stage primaryStage) {
        
        // Estructura Principal Dividida
        VBox leftPane = crearPanelIzquierdo();
        VBox rightPane = crearPanelDerecho(primaryStage);

        HBox mainLayout = new HBox(leftPane, rightPane);
        
        // Ajuste de proporciones: 50% y 50% aprox para verse equilibrado como la imagen
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        leftPane.setPrefWidth(450);
        rightPane.setPrefWidth(450);

        Scene scene = new Scene(mainLayout, 900, 600);
        cargarCSS(scene); // Tu método seguro de carga

        primaryStage.setTitle("Días Vet - Acceso al Sistema");
        primaryStage.setScene(scene);
        
        // Animación suave de entrada
        FadeTransition ft = new FadeTransition(Duration.millis(800), mainLayout);
        ft.setFromValue(0); ft.setToValue(1); ft.play();

        primaryStage.show();
    }

    // --- PANEL IZQUIERDO (El lado Azul con el Logo en Tarjeta) ---
    // --- PANEL IZQUIERDO (Con Logo Real) ---
    private VBox crearPanelIzquierdo() {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("left-panel-blue");
        box.setSpacing(20);

        // 1. LA TARJETA DEL LOGO
        VBox logoCard = new VBox(10);
        logoCard.getStyleClass().add("logo-card");
        logoCard.setMaxWidth(320); // Un poco más ancho para que quepa bien
        // logoCard.setMaxHeight(320); // Dejamos que la altura sea automática según la imagen
        logoCard.setAlignment(Pos.CENTER);

        // --- CARGA DE IMAGEN ---
        ImageView logoView = new ImageView();
        try {
            // Buscamos la imagen en la carpeta resources/images
            String imagePath = "/resources/img/logo_diasvet.jpg"; // Asegúrate de que el nombre coincida
            
            // Intentamos cargarla. Usamos getResourceAsStream es más seguro para archivos dentro de JARs
            java.io.InputStream is = getClass().getResourceAsStream(imagePath);
            
            // Fallback: Si no está en /resources/images, buscamos en /images (por si la estructura varía)
            if (is == null) {
                is = getClass().getResourceAsStream("/img/logo_diasvet.jpg");
            }

            if (is != null) {
                Image image = new Image(is);
                logoView.setImage(image);
                
                // Ajustes de tamaño
                logoView.setFitWidth(220);  // Ancho deseado
                logoView.setPreserveRatio(true); // Mantener proporciones para no deformar al perrito
                logoView.setSmooth(true); // Suavizado para mejor calidad
            } else {
                System.err.println("No se encontró la imagen del logo.");
                logoView.setImage(null); // O poner un placeholder texto
            }

        } catch (Exception e) {
            System.err.println("Error cargando logo: " + e.getMessage());
        }

        // Agregamos la imagen a la tarjeta
        logoCard.getChildren().add(logoView);

        // 2. TEXTOS INFERIORES
        Text titleApp = new Text("Sistema de Gestión Veterinaria");
        titleApp.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleApp.setFill(Color.WHITE);

        Text subTitleApp = new Text("Cuidando a tus mascotas con amor y profesionalismo");
        subTitleApp.setFont(Font.font("Segoe UI", 12));
        subTitleApp.setFill(Color.WHITE);

        box.getChildren().addAll(logoCard, titleApp, subTitleApp);
        return box;
    }

    // --- PANEL DERECHO (El lado Blanco con el Formulario en Tarjeta) ---
    private VBox crearPanelDerecho(Stage stage) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #FAFAFA;"); // Blanco hueso muy suave
        box.setPadding(new Insets(40));
        box.setSpacing(10);

        // Encabezados
        Text welcomeText = new Text("Bienvenido");
        welcomeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        welcomeText.setFill(Color.web("#37474F"));

        Text instructionText = new Text("Ingresa tus credenciales para continuar");
        instructionText.setFill(Color.GRAY);
        instructionText.setFont(Font.font(13));

        // --- TARJETA DE LOGIN (Aquí agrupamos los inputs) ---
        VBox loginCard = new VBox(15);
        loginCard.getStyleClass().add("login-form-card"); // Estilo CSS de tarjeta
        loginCard.setMaxWidth(350); // Ancho fijo para que se vea elegante

        // Input Usuario
        VBox userGroup = new VBox(5);
        Label lblUser = new Label("👤 Usuario / DNI");
        lblUser.getStyleClass().add("label-small");
        TextField userField = new TextField();
        userField.setPromptText("Ingresa tu usuario o DNI");
        userField.getStyleClass().add("input-round"); // Estilo redondeado
        userGroup.getChildren().addAll(lblUser, userField);

        // Input Contraseña
        VBox passGroup = new VBox(5);
        Label lblPass = new Label("🔒 Contraseña");
        lblPass.getStyleClass().add("label-small");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Ingresa tu contraseña");
        passField.getStyleClass().add("input-round"); // Estilo redondeado
        passGroup.getChildren().addAll(lblPass, passField);

        // Botón INGRESAR (Rosa)
        Button btnLogin = new Button("INGRESAR");
        btnLogin.getStyleClass().add("btn-login-gradient");
        btnLogin.setMaxWidth(Double.MAX_VALUE); // Expandir a todo el ancho

        // Botón CREAR CUENTA (Blanco borde rosa)
        Button btnRegistro = new Button("CREAR CUENTA");
        btnRegistro.getStyleClass().add("btn-create-account");
        btnRegistro.setMaxWidth(Double.MAX_VALUE); // Expandir a todo el ancho

        // Link Olvidé contraseña
        Hyperlink forgotPass = new Hyperlink("¿Olvidaste tu contraseña?");
        forgotPass.setStyle("-fx-text-fill: #546E7A; -fx-font-size: 12px;");
        forgotPass.setAlignment(Pos.CENTER);
        
        // Agregamos todo a la tarjeta
        loginCard.getChildren().addAll(userGroup, passGroup, new Region(), btnLogin, btnRegistro, forgotPass);
        
        // --- LOGICA (Intacta) ---
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        btnLogin.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor ingrese usuario y contraseña.");
                return;
            }
            Usuario usuarioLogueado = usuarioDAO.login(user, pass);
            if (usuarioLogueado != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Acceso Correcto", 
                              "Bienvenido " + usuarioLogueado.getNickname() + "\nRol: " + usuarioLogueado.getRol());
                // TODO: Aquí abrirías el Dashboard (PanelClienteUI o PanelAdminUI)
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Acceso Denegado", "Usuario o contraseña incorrectos.");
            }
        });

        // Lógica Registro
        btnRegistro.setOnAction(e -> {
            // 1. Definimos la acción "Volver" (Recargar esta pantalla de Login)
            Runnable logicaVolver = () -> this.start(stage);
            
            // 2. Instanciamos el NUEVO Wizard (No el panel viejo)
            PanelRegistroWizard panelWizard = new PanelRegistroWizard(logicaVolver);
            
            // 3. Cambiamos la escena
            Scene registerScene = new Scene(panelWizard, 900, 600);
            cargarCSS(registerScene); // Importante: cargar estilos
            stage.setScene(registerScene);
        });

        // Copyright Footer
        Label footer = new Label("Díaz Vet © 2025");
        footer.setTextFill(Color.web("#B0BEC5"));
        footer.setPadding(new Insets(20, 0, 0, 0));

        box.getChildren().addAll(welcomeText, instructionText, new Region(), loginCard, footer);
        return box;
    }

    // --- UTILITARIOS ---
    private void cargarCSS(Scene scene) {
        try {
            URL cssUrl = getClass().getResource("/resources/style.css");
            if (cssUrl == null) cssUrl = getClass().getResource("/style.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
        } catch (Exception e) { System.err.println("Error CSS: " + e.getMessage()); }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo); alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(mensaje); alert.showAndWait();
    }

    public static void main(String[] args) {
        ConexionDB.inicializarBaseDeDatos();
        launch(args);
    }
}