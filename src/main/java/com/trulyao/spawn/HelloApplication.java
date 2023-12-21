package com.trulyao.spawn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelloApplication extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        this.handleStageSettings();
        stage.show();
    }

    private void handleStageSettings() {
        this.primaryStage.initStyle(StageStyle.UNDECORATED);
        this.primaryStage.centerOnScreen();
        this.primaryStage.setResizable(false);
        this.primaryStage.setWidth(1200);
        this.primaryStage.setHeight(768);
        this.primaryStage.setTitle("Spawn");

        this.primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
