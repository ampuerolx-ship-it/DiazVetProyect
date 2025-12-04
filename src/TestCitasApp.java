import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.panels.PanelCitas;
import java.util.Objects;

public class TestCitasApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        PanelCitas panelCitas = new PanelCitas();
        
        Scene scene = new Scene(panelCitas, 1000, 700);
        
        // ¡IMPORTANTE! Carga el CSS
        try {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("resources/style.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("No se encontró CSS");
        }

        primaryStage.setTitle("Días Vet - Gestión de Citas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}