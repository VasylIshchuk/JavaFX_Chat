package org.umcs.chat.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.umcs.chat.Application;
import org.umcs.chat.handlers.ConnectionHandler;

import java.io.IOException;

public class Controller {
    public void startChat(Stage stage, ConnectionHandler client) {
        FXMLLoader fxmlLoader = setupChatInterface("chat-view.fxml");
        Scene scene = initializeScene(fxmlLoader);

        ChatController chatController = fxmlLoader.getController();
        chatController.setClient(client);

        chatController.initializeStage(stage, scene, "CHAT", "/org/umcs/chat/style.css", 780, 684, 738, 453);
    }

    public void startLogin(Stage stage, ConnectionHandler client) {
        FXMLLoader fxmlLoader = setupChatInterface("log-in-view.fxml");
        Scene scene = initializeScene(fxmlLoader);

        LoginController loginController = fxmlLoader.getController();
        loginController.setClient(client);

        loginController.initializeStage(stage, scene,"LOG IN",476, 476, 579, 579 );
    }

    public void startRegistration(Stage stage, ConnectionHandler client) {
        FXMLLoader fxmlLoader = setupChatInterface("sing-up-view.fxml");
        Scene scene = initializeScene(fxmlLoader);

        RegistrationController registrationController = fxmlLoader.getController();
        registrationController.setClient(client);

        registrationController.initializeStage(stage, scene, "SING UP", 476, 476, 579, 579);
    }

    private FXMLLoader setupChatInterface(String nameFileXML){
        return new FXMLLoader(Application.class.getResource(nameFileXML));
    }

    private Scene initializeScene(FXMLLoader fxmlLoader){
        try {
            return new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stage getStage(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        return (Stage) node.getScene().getWindow();
    }

    public void setPositionStageChat(Stage stage){
        stage.setX(480);
        stage.setY(80);
    }

    public  void addStageSize(Stage stage, double width,double height){
        stage.setWidth(width);
        stage.setHeight(height);
    }

    public void addTitle(Stage stage, String title) {
        stage.setTitle(title);
    }

    public void setMaxAndMinSizeStage(Stage stage, double maxHeight, double minHeight, double maxWidth, double minWidth) {
        stage.setMaxHeight(maxHeight);
        stage.setMinHeight(minHeight);
        stage.setMaxWidth(maxWidth);
        stage.setMinWidth(minWidth);
    }

    public void addStyleCss(Scene scene, String fileCss) {
        if (!fileCss.isEmpty()) {
            String css = this.getClass().getResource(fileCss).toExternalForm();
            scene.getStylesheets().add(css);
        }
    }

    public void onClose(Stage stage) {
        stage.setOnCloseRequest(event -> {
            closeAplication();
        });
    }

    public void onCloseChat(Stage stage, ConnectionHandler connectionHandler) {
        stage.setOnCloseRequest(event -> {
            connectionHandler.send("/exit");
            closeAplication();
        });
    }

    private void closeAplication(){
        Platform.exit();
        System.exit(0);
    }
    /*
    This code is for the window close event (Stage) in JavaFX. When the user tries to close the window,
    the setOnCloseRequest method is called, which performs two operations when the window is closed:

    Platform.exit(); - This call closes the JavaFX Application Thread, which stops the execution of the JavaFX application
    System.exit(0); - This call terminates the execution of the entire Java Virtual Machine (JVM).
     */
}
