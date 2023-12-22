package com.trulyao.spawn.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private final static String FXML_DIR = "/fxml/";
    private Stage mainStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_DIR + "main.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        this.setStageProperties();
        this.mainStage.show();
    }

    private void setStageProperties() {
        this.mainStage.setTitle("Spawn");
        this.mainStage.centerOnScreen();
        this.mainStage.setHeight(768);
        this.mainStage.setWidth(1200);
        this.mainStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
