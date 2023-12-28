package com.trulyao.spawn.application;

import com.trulyao.spawn.utils.AppConstants;
import com.trulyao.spawn.utils.Logger;
import com.trulyao.spawn.utils.exceptions.ExceptionHandler;
import com.trulyao.spawn.views.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage mainStage;

    @Override
    public void start(Stage stage) {
        try {
            this.mainStage = stage;
            Pane root = new MainView(this.mainStage).render();
            stage.setScene(new Scene(root));
            this.setStageProperties();
            this.mainStage.show();
        } catch (Exception e) {
            ExceptionHandler.handle(mainStage, e);
        }
    }

    private void setStageProperties() {
        this.mainStage.setTitle("Spawn");
        this.mainStage.centerOnScreen();
        this.mainStage.setHeight(AppConstants.HEIGHT);
        this.mainStage.setWidth(AppConstants.WIDTH);
        this.mainStage.setResizable(false);
        Logger.getSharedInstance().debug("Set stage properties.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
