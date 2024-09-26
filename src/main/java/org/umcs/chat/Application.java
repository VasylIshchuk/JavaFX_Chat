package org.umcs.chat;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
//import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.Socket;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ConnectionHandler connectionHandler = connectionWithServer();
        onCloseApplication(stage, connectionHandler);
//        addIconAndTitle(stage);
//        addStyleCss(scene);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private ConnectionHandler connectionWithServer() {
        try {
            Socket clientSocket = new Socket("localhost", 1973);
            ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
            ClientReceiver.controller.client = connectionHandler;
//            used to initialize ConnectionHandler client in ChatController
            Thread thread = new Thread(connectionHandler);
            thread.start();
            return connectionHandler;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onCloseApplication(Stage stage, ConnectionHandler connectionHandler) {
        stage.setOnCloseRequest(event -> {
            connectionHandler.send("/exit");
            Platform.exit();
            System.exit(0);
        });
    }
/*
This code is for the window close event (Stage) in JavaFX. When the user tries to close the window,
the setOnCloseRequest method is called, which performs two operations when the window is closed:

Platform.exit(); - This call closes the JavaFX Application Thread, which stops the execution of the JavaFX application
System.exit(0); - This call terminates the execution of the entire Java Virtual Machine (JVM).
 */
//    private void addIconAndTitle(Stage stage){
//        Image icon = new Image("icon.png");
//        stage.getIcons().add(icon);
//        stage.setTitle("CHAT");
//    }
//
//    private void addStyleCss(Scene scene){
//        String css = this.getClass().getResource("stuyle.css").toExternalForm();
//        scene.getStylesheets().add(css);
//    }
}