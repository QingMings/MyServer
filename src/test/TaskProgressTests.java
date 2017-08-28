


import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.TaskProgressView;
import org.controlsfx.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TaskProgressTests extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Hello World!");

        Button btn = new Button();
        btn.setText("Run singleTaskService()");
        btn.setOnAction(event -> singleTaskService());

        Button btn2 = new Button();
        btn2.setText("Run multipleTasksExecutorOnStage()");
        btn2.setOnAction(event -> multipleTasksExecutorOnStage());

        Button btn3 = new Button();
        btn3.setText("Run multipleTasksExecutorPopup()");
        btn3.setOnAction(event -> multipleTasksExecutorPopup());


        VBox root = new VBox();
        root.setSpacing(5.0);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15.0));
        root.getChildren().addAll(btn, btn2, btn3);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public Task<Void> task() {
        return new Task<Void>() {
            @Override
            protected Void call()
                    throws InterruptedException {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(200, 2000));
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                updateMessage("Finding friends . . .");
                updateProgress(0, 100);
                for (int i = 0; i < 100; i++) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        break;
                    }

                    updateProgress(i + 1, 100);
                    updateMessage("Found " + (i + 1) + " friends!");

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException interrupted) {
                        if (isCancelled()) {
                            updateMessage("Cancelled");
                            break;
                        }
                    }
                }
                updateMessage("Found all.");
                done();
                return null;
            }
        };
    }

    public void multipleTasksExecutorPopup() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Task<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Task task = task();
            executorService.submit(task);
            tasks.add(task);
        }
        TaskProgressView<Task<Void>> view = new TaskProgressView<>();
        //view.setGraphicFactory(t -> new ImageView(new Image(getClass().getResourceAsStream("/icon.png"))));
        view.getTasks().addAll(tasks);


        final Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        popup.getContent().add(view);
        popup.show(primaryStage);

        executorService.shutdown();
        new Thread(() -> {
            try {
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Platform.runLater(popup::hide);
            }
        }).start();
    }

    public void multipleTasksExecutorOnStage() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Task<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Task task = task();
            executorService.submit(task);
            tasks.add(task);
        }
        TaskProgressView<Task<Void>> view = new TaskProgressView<>();
        //view.setGraphicFactory(t -> new ImageView(new Image(getClass().getResourceAsStream("/icon.png"))));
        view.getTasks().addAll(tasks);


        Stage dialogStage = new Stage();
        dialogStage.setTitle("Tasks");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(view);
        dialogStage.setScene(scene);
        dialogStage.setOnCloseRequest(event -> {
            executorService.shutdownNow();
            dialogStage.hide();
        });

        dialogStage.show();
        executorService.shutdown();
        new Thread(() -> {
            try {
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Platform.runLater(dialogStage::hide);
            }
        }).start();
    }

    public void singleTaskService() {
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return task();
            }
        };

        ProgressDialog progDiag = new ProgressDialog(service);
        progDiag.setTitle("Progress Dialog Title");
        progDiag.initOwner(primaryStage);
        progDiag.setHeaderText("Header Text");
        progDiag.initModality(Modality.WINDOW_MODAL);

        progDiag.setOnCloseRequest(event -> {
            Platform.runLater(() -> {
                Notifications.create()
                        .title("Information")
                        .text("Task done")
                        .showInformation();
            });
        });

        service.start();

    }

    public static void main(String[] args) {
        launch(args);
    }

}


