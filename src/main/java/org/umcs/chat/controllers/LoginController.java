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


public class LoginController extends Controller {
    public ConnectionHandler client;
    public TextField textField;
    public PasswordField passwordField;
    public Text text;
    public Hyperlink hyperLink;
    public Stage stage;

    public void setClient(ConnectionHandler client) {
        this.client = client;
    }

    public void initialize() {
        ClientReceiver.loginController = this;
        text.setText("Welcome to the server ^_^ \n" + "Please enter your credentials to log into your account o_o");
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
        startRegistration(stage,client);
    }

    public void onPressButton(ActionEvent actionEvent) {
        stage = getStage(actionEvent);
        String username = textField.getText();
        String password = passwordField.getText();
        client.send("/logIn " + username + " " + password);
        textField.clear();
        passwordField.clear();
    }

    public void verifyUserRegistration(boolean isRegistered) {
        if (isRegistered) {
            startChat(stage,client);
        } else {
            text.setText("Sorry, but the username or password is incorrect \n" + "Please try again o.o");
        }
    }
}
