package org.umcs.chat.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.umcs.chat.ClientReceiver;
import org.umcs.chat.handlers.ConnectionHandler;


public class RegistrationController extends Controller {
    public TextField textField;
    public PasswordField passwordField;
    public Text text;
    public Stage stage;
    public ConnectionHandler client;
    public Hyperlink hyperLink;

    public void setClient(ConnectionHandler client) {
        this.client = client;
    }

    public void initialize() {
        ClientReceiver.registrationController = this;
        text.setText("Please enter your credentials to sing up your account ^_^ ");
    }

    public void initializeStage(Stage stage, Scene scene, String title,
                                double maxHeight, double minHeight, double maxWidth, double minWidth) {
        Platform.runLater(() -> {
            addTitle(stage, title);
            setMaxAndMinSizeStage(stage, maxHeight, minHeight, maxWidth, minWidth);
            stage.setScene(scene);
            stage.show();
        });
    }

    public void onPressHyperLink(ActionEvent actionEvent) {
        stage = getStage(actionEvent);
        startLogin(stage,client);
    }

    public void onPressButton(ActionEvent actionEvent) {
        stage = getStage(actionEvent);
        String username = textField.getText();
        String password = passwordField.getText();
        client.send("/singUp " + username + " " + password);
        textField.clear();
        passwordField.clear();
    }

    public void userRegistered(boolean isRegistered) {
        if (isRegistered) {
            startChat(stage,client);
        } else {
            text.setText("Sorry, but this username is already taken \n" + "Please choose another one o.o");
        }
    }

}
