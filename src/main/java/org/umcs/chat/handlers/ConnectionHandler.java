package org.umcs.chat.handlers;

import org.umcs.chat.ClientReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    BufferedReader reader;
    PrintWriter writer;

    public ConnectionHandler(Socket clientSocket) throws IOException {
        reader = new BufferedReader(
                new InputStreamReader(
                        clientSocket.getInputStream()
                ));
        writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                 if (message.contains("/singUp")) {
                    ClientReceiver.userRegistered(Boolean.parseBoolean(getMessage(message)));
                }else if (message.contains("/logIn")) {
                    ClientReceiver.verifyUserRegistration(Boolean.parseBoolean(getMessage(message)));
                }else if (message.contains("/online")) {
                    ClientReceiver.receiveList(getMessage(message));
                }else ClientReceiver.receive(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMessage(String message){
        String[] splitMessage = message.split(" ", 2);
        return splitMessage[1];
    }

    public void send(String message) {
        writer.println(message);
    }
}
