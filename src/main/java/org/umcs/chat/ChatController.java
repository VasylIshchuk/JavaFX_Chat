package org.umcs.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public ListView listView;

    public ConnectionHandler client;
//  This field is need to send a message to the server
    public void initialize() {
        ClientReceiver.controller = this;
    }
//    This method is automatically called after the FXML file is loaded (Scene scene = new Scene(fxmlLoader.load());).
//    We use it to set the controller in the ClientReceiver.
    @FXML
    public void onSendMessage(){
        String message = textField.getText();
        textField.clear();
        client.send(message);
        client.send("/online");
//        in order to update the list of members after each message
        onReceiveMessage(message);
        if (message.equals("EXIT")){
            Platform.exit();
            System.exit(0);
        }

    }
    public void onReceiveMessage(String message){
        Platform.runLater(() -> textArea.appendText(message + "\n"));
    }

    public void takeListOfMembers(String message) {
        Platform.runLater(() -> {
            listView.getItems().clear();
            listView.getItems().addAll(message.split(","));
        });
    }
    /*
    Platform.runLater() is used in JavaFX to execute code that changes the user interface (UI) on a dedicated thread
    responsible for the UI. This is because all changes to the UI must be made on this thread to avoid errors.

    When you're working with JavaFX and want to change something in the UI from another thread
    (not the main JavaFX thread), you need to use Platform.runLater().
     */
}
