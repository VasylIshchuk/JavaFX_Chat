package org.umcs.chat;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.umcs.chat.controllers.LoginController;
import org.umcs.chat.handlers.ConnectionHandler;


import java.io.IOException;
import java.net.Socket;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
       startLogin(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    public void startLogin(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("log-in-view.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ConnectionHandler connectionHandler = connectionWithServer();
        LoginController loginController = fxmlLoader.getController();
        loginController.setClient(connectionHandler);
        loginController.onClose(stage);

        loginController.initializeStage(stage, scene,"LOGIN",476, 476, 579, 579 );
    }

    private ConnectionHandler connectionWithServer() {
        try {
            Socket clientSocket = new Socket("localhost", 1973);
            ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
            Thread thread = new Thread(connectionHandler);
            thread.start();
            return connectionHandler;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private void onCloseApplication(Stage stage) {
//        stage.setOnCloseRequest(event -> {
//            Platform.exit();
//            System.exit(0);
//        });
//    }
/*
This code is for the window close event (Stage) in JavaFX. When the user tries to close the window,
the setOnCloseRequest method is called, which performs two operations when the window is closed:

Platform.exit(); - This call closes the JavaFX Application Thread, which stops the execution of the JavaFX application
System.exit(0); - This call terminates the execution of the entire Java Virtual Machine (JVM).
 */
}