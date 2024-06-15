module org.umcs.chat.application {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    exports org.umcs.chat;
    opens org.umcs.chat to javafx.fxml;
}