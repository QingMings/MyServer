import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TestListVIewAddItemOther extends Application {

    private int newBookIndex = 2;
    public final ObservableList<Book> data = FXCollections.observableArrayList(
            new Book("123", "Hugo"), new Book("456", "Harry Potter"));
    private final ListView<Book> lv = new ListView<>();

    @Override
    public void start(Stage primaryStage) {
        Button addButton = new Button("Add Book");

        lv.setCellFactory(param -> new BookCell());
        lv.setItems(data);
        Scene myScene = new Scene(new VBox(10, lv, addButton), 200, 200);
//        myScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(myScene);
        primaryStage.show();

        addButton.setOnAction((event) -> {
            addButton.setDisable(true);
            data.add(0, new Book(String.valueOf(newBookIndex), "test"));

            VirtualFlow vf = (VirtualFlow) lv.lookup(".virtual-flow");
            if (!lv.lookup(".scroll-bar").isVisible()) {
                vf.show(0);

                Timeline timeline = new Timeline();
                timeline.getKeyFrames().addAll(getFlatternOut(vf.getCell(0)));
                timeline.setOnFinished(t -> {
                    newBookIndex++;
                    addButton.setDisable(false);
                });
                timeline.playFromStart();
//                FadeTransition f = new FadeTransition();
//                f.setDuration(Duration.seconds(1));
//                f.setFromValue(0);
//                f.setToValue(1);
//                f.setNode(vf.getCell(0));
//                f.setOnFinished(t->{
//                    newBookIndex++;
//                    addButton.setDisable(false);
//                });
//                f.play();
            } else {
                PauseTransition p = new PauseTransition(Duration.millis(20));
                p.setOnFinished(e -> {
//                    vf.getCell(lv.getItems().size() - 1).setOpacity(0);
                    vf.show(0);
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().addAll(getFlatternOut(vf.getCell(0)));
                    timeline.setOnFinished(t -> {
                        newBookIndex++;
                        addButton.setDisable(false);
                    });
                    timeline.playFromStart();
                    FadeTransition f = new FadeTransition();
                    f.setDuration(Duration.seconds(1));
                    f.setFromValue(0);
                    f.setToValue(1);
                    f.setNode(vf.getCell(0));
                    f.setOnFinished(t->{
                        newBookIndex++;
                        addButton.setDisable(false);
                    });
                    f.play();
                });
                p.play();
            }
        });
    }

    class BookCell extends ListCell<Book> {
        private final Text text = new Text();
        private final HBox h = new HBox(text);

        {
            getStyleClass().add("book-list-cell");
        }

        @Override
        protected void updateItem(Book item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                text.setText(item.getIsbn());
                setGraphic(h);
            } else {
                setGraphic(null);
                setText(null);
            }
        }
    }

    class Book {
        private Book(String isbn, String name) {
            this.isbn.set(isbn);
            this.name.set(name);
        }

        private final StringProperty name = new SimpleStringProperty();

        public String getName() {
            return name.get();
        }

        public void setName(String value) {
            name.set(value);
        }

        public StringProperty nameProperty() {
            return name;
        }

        private final StringProperty isbn = new SimpleStringProperty();

        public String getIsbn() {
            return isbn.get();
        }

        public void setIsbn(String value) {
            isbn.set(value);
        }

        public StringProperty isbnProperty() {
            return isbn;
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    public KeyFrame[] getFlatternOut(Node node) {
        return new KeyFrame[]{
                new KeyFrame(Duration.millis(0), new KeyValue(node.scaleXProperty(), 0)),
                new KeyFrame(Duration.millis(0), new KeyValue(node.scaleYProperty(), 0.9)),
                new KeyFrame ( Duration.millis(600 * 0.4),
                        new KeyValue(node.scaleXProperty(), 0.001)),
                new KeyFrame(Duration.millis(600 * 0.6),
                        new KeyValue(node.scaleXProperty(), 1.2, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(600), new KeyValue(node.scaleYProperty(), 1)),
                new KeyFrame(Duration.millis(600), new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH))};

    }

}