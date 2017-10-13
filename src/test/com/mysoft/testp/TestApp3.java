package com.mysoft.testp;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class TestApp3 extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().add(new Tab("Tab1"));
        tabPane.getTabs().add(new Tab("Tab2"));


        Scene scene = new Scene(tabPane);
        stage.setScene(scene);
        scene.getStylesheets().addAll("/com/mysoft/testp/styles.css");
        stage.show();
    }
}