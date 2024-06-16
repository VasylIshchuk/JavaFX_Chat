package org.umcs.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements  Runnable{
    BufferedReader reader;
    PrintWriter writer;

    public ConnectionHandler(Socket clientSocket) throws IOException {
        reader = new BufferedReader(
                new InputStreamReader(
                        clientSocket.getInputStream()
                ));
        writer = new PrintWriter(clientSocket.getOutputStream(),true);
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                if(message.startsWith("/online")){
                    String[] splitMessage = message.split(" ", 2);
                    ClientReceiver.receiveList(splitMessage[1]);
                } else ClientReceiver.receive(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void send(String message){
        writer.println(message);
    }
}
