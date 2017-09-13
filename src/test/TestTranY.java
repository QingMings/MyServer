


import javafx.application.Application;
        import javafx.scene.Group;
        import javafx.scene.Scene;
        import javafx.scene.control.Label;
        import javafx.scene.layout.HBox;
        import javafx.scene.text.TextAlignment;
        import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class TestTranY extends Application {
    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Label Sample");
        stage.setWidth(400);
        stage.setHeight(180);

        HBox hbox = new HBox();

        Label label1 = new Label("Search");
        label1.setTranslateY(50);

        hbox.setSpacing(10);
        hbox.getChildren().add((label1));
        ((Group) scene.getRoot()).getChildren().add(hbox);

        stage.setScene(scene);
        stage.show();
    }
}
