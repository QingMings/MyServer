import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vbox =new  VBox();
        vbox.getChildren().add(new SliderTextField(0,100,10));
        primaryStage.setScene(new Scene(vbox));
        primaryStage.show();
    }
}
