module com.bkk.selectorchatgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires gson;
    requires java.sql;

    opens com.bkk.selectorchatgui to javafx.fxml, gson;

    exports com.bkk.selectorchatgui;
}