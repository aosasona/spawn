package com.trulyao.spawn.application;

import com.trulyao.spawn.utils.Logger;
import com.trulyao.spawn.utils.exceptions.ExceptionHandler;
import com.trulyao.spawn.views.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage mainStage;

    @Override
    public void start(Stage stage) {
        try {
            Logger.getSharedInstance().debug("Starting Spawn...");
            this.mainStage = stage;
            SplitPane root = new MainView(this.mainStage).buildView();
            stage.setScene(new Scene(root));
            this.setStageProperties();
            this.mainStage.show();
        } catch (Exception e) {
            ExceptionHandler.handle(mainStage, e);
        }
    }

    private void setStageProperties() {
        mainStage.setTitle("Spawn");
        mainStage.centerOnScreen();
        mainStage.setHeight(768);
        mainStage.setWidth(1280);
        mainStage.setMinHeight(640);
        mainStage.setMinWidth(1024);
        Logger.getSharedInstance().debug("Set stage properties.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

