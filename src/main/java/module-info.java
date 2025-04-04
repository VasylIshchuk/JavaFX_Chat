module org.umcs.chat.application {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    exports org.umcs.chat;
    opens org.umcs.chat to javafx.fxml;
    exports org.umcs.chat.controllers;
    opens org.umcs.chat.controllers to javafx.fxml;
    exports org.umcs.chat.handlers;
    opens org.umcs.chat.handlers to javafx.fxml;
}