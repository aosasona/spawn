package com.trulyao.spawn.application;

import com.trulyao.spawn.utils.Logger;
import java.util.HashMap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private final static String FXML_DIR = "/fxml/";
    private Stage mainStage;

    @Override
    public void start(Stage stage) {
        try {
            this.mainStage = stage;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_DIR + "main.fxml"));
            Scene scene = new Scene(loader.load());

            stage.setScene(scene);
            this.setStageProperties();
            this.mainStage.show();
        } catch (Exception e) {
            var meta = new HashMap<String, String>();
            meta.put("originalError", e.getMessage());
            meta.put("originalStackTrace", e.getStackTrace().toString());

            Logger.getSharedInstance().fatal("Something went wrong while loading the main window.", meta);
        }
    }

    private void setStageProperties() {
        this.mainStage.setTitle("Spawn");
        this.mainStage.centerOnScreen();
        this.mainStage.setHeight(768);
        this.mainStage.setWidth(1200);
        this.mainStage.setResizable(false);
        Logger.getSharedInstance().debug("Set stage properties.");
        ;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
