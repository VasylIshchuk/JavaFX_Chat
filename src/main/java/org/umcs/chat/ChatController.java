package org.umcs.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;

    public ConnectionHandler client;
//  This field is need to send a message to the server
    public void initialize() {
        ClientReceiver.controller = this;
    }
//    This method is automatically called after the FXML file is loaded(Scene scene = new Scene(fxmlLoader.load());).
//    We use it to set the controller in the ClientReceiver.
    @FXML
    public void onSendMessage(){
        String message = textField.getText();
        textField.clear();
        onReceiveMessage(message);
        client.send(message);
        if (message.equals("EXIT")){
            Platform.exit();
            System.exit(0);
        }
    }
    public void onReceiveMessage(String message){
        textArea.appendText(message + "\n");
    }
}
