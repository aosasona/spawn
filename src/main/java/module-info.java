module com.trulyao.spawn {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires org.commonmark;
    requires org.commonmark.ext.front.matter;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.ionicons4;

    exports com.trulyao.spawn.application;
}
