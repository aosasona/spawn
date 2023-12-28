package com.trulyao.spawn.utils.exceptions;

import com.trulyao.spawn.utils.Logger;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

final public class ExceptionHandler {
    private static final Logger log = Logger.getSharedInstance();
    public static void handle(Stage stage, Exception e) {
        if (e instanceof FatalException) {
            log.fatal(e.getMessage());
            stage.close();
            ExceptionHandler.showErrorAlert("Fatal Error", "Fatal Error", e.getMessage());
            System.exit(1);
        } else if (e instanceof AppException) {
            log.error(e.getMessage());
            ExceptionHandler.showErrorAlert("Error", "Error", e.getMessage());
        } else {
            log.fatal(e.getMessage());
            stage.close();
            ExceptionHandler.showErrorAlert("Unknown error", "Oops!", "Something went horribly wrong! Please report this to the developer.");
            System.exit(1);
        }
    }

    private static void showErrorAlert(String title, String header, String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
