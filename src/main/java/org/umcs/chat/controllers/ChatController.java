package org.umcs.chat.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.umcs.chat.ClientReceiver;
import org.umcs.chat.handlers.ConnectionHandler;


public class ChatController extends Controller {
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public ListView listView;

    @FXML
    public Label counter;
    //  This field is need to send a message to the server
    public ConnectionHandler client;

    public void setClient(ConnectionHandler client) {
        this.client = client;
    }

    //    This method is automatically called after the FXML file is loaded (Scene scene = new Scene(fxmlLoader.load());).
    //    We use it to set the controller in the ClientReceiver.
    public void initialize() {
        ClientReceiver.chatController = this;
        textArea.setEditable(false); // forbidden to write text in the  TextArea
        textArea.setWrapText(true); // to transfer text in new line auto
        showFinishRegistrationMessage();
        showChatInstructions();
    }

    private void showFinishRegistrationMessage() {
        textArea.appendText("Thanks ^-^\n" + "Now you can chat with other users ^.^\n");
    }

    private void showChatInstructions() {
        StringBuffer text = new StringBuffer();
        text.append("Additional commands:\n").append("\t• \"/exit\" - to exit the server;\n").append("\t• \"/pm username_receiver message\" - to send a private message;\n").append("\t• \"/command\" - to show all commands\n").append("Let's go!\n");
        textArea.appendText(String.valueOf(text));
    }

    public void initializeStage(Stage stage, Scene scene, String title, String fileCss,
                                double maxHeight, double minHeight, double maxWidth, double minWidth) {
        Platform.runLater(() -> {
            setPositionStageChat(stage);
            addStageSize(stage, maxWidth,maxHeight);
            addStyleCss(scene, fileCss);
            addTitle(stage, title);
            setMaxAndMinSizeStage(stage, maxHeight, minHeight, maxWidth, minWidth);
            onCloseChat(stage,client);
            stage.setScene(scene);
            stage.show();
        });
    }

    //    The method is needed to send messages to other users (to the server).
    @FXML
    public void onSendMessage() {
        String message = textField.getText();
        textField.clear();
        client.send(message);
        checkExitCommand(message);
    }

    private void checkExitCommand(String message) {
        if (message.equals("/exit")) {
            Platform.exit();
            System.exit(0);
        }
    }

    //  The method is needed to show the messages you send and receive from other users on your screen.
    public void onReceiveMessage(String message) {
        Platform.runLater(() -> {
            textArea.appendText(message + "\n");
            updateListOfMembers(message);
        });
    }

    private void updateListOfMembers(String message) {
        if (message.contains("[SERVER]")) client.send("/online");
    }

    //  Method is needed to show which users are online on "listView".
    public void takeListOfMembers(String message) {
        Platform.runLater(() -> {
            listView.getItems().clear();
            listView.getItems().addAll(message.split(","));
            onCountMembers(message);
        });
    }
    /*
    Platform.runLater() is used in JavaFX to execute code that changes the user interface (UI) on a dedicated thread
    responsible for the UI. This is because all changes to the UI must be made on this thread to avoid errors.

    When you're working with JavaFX and want to change something in the UI from another thread
    (not the main JavaFX thread), you need to use Platform.runLater().
     */

    public void onCountMembers(String message) {
        int countMembers = message.split(",").length;
        counter.setText(String.valueOf(countMembers));
    }
}
