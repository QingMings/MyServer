import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestWhileButton extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        WhileButton btn = new WhileButton();
        btn.setText("按下一直执行");
        btn.setOnAction(event -> System.out.println("hehe"));
        Scene scene = new Scene(new StackPane(btn), 300, 250);
        stage.setTitle("Hello World!");
        stage.setScene(scene);
        stage.show();
    }
}
