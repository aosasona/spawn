module com.trulyao.spawn {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;

    opens com.trulyao.spawn.application to javafx.fxml;
    exports com.trulyao.spawn.application;
}
