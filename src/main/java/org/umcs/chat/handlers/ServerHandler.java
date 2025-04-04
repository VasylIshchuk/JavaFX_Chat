package org.umcs.chat.handlers;

import org.umcs.chat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private final Server server;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Socket clientSocket;


    public ServerHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        parseClientMessage();
        try {
            clientSocket.close();
            System.out.println("Client disconnected");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseClientMessage() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                System.out.println(message);
                if (message.startsWith("/singUp ")) {
                    String[] splitMessage = message.split(" ", 3);
                    if(validateUniqueUsername(splitMessage[1])){
                        server.clientsRegistered.put(splitMessage[1],splitMessage[2]);
                        server.clientsOnline.put(this,splitMessage[1]);
                        sendMessageUserRegistered(true);
                        announceNewConnection();
                    }
                    sendMessageUserRegistered(false);
                } else if (message.startsWith("/logIn ")) {
                    String[] splitMessage = message.split(" ", 3);
                    if(verifyUserRegistration(splitMessage[1], splitMessage[2])){
                        server.clientsOnline.put(this,splitMessage[1]);
                        sendMessageUserExists(true);
                        announceNewConnection();
                    }
                    sendMessageUserExists(false);
                } else if (message.equals("/exit")) {
                    server.disconnect("[SERVER]: " + server.clientsOnline.get(this) + " left the chat", this);
                    break;
                } else if (message.equals("/online")) {
                    //      This command is automatically sent after connecting and disconnecting the user to update
                    //      the list of online members of the chat on the "listView" in the "ChatController" class,
                    //      the "updateListOfMembers" method.
                    StringBuilder onlineUsers = new StringBuilder();
                    for (String username : server.clientsOnline.values())
                        onlineUsers.append(username).append(",");
                    server.broadcast(message + " " + onlineUsers, this);
                } else if (message.startsWith("/pm ")) {
                    String[] splitMessage = message.split(" ", 3);
                    for(ServerHandler receiver : server.clientsOnline.keySet()){
                        if(server.clientsOnline.get(receiver).equals(splitMessage[1])){
                            server.privateMessage(splitMessage[2], this, receiver );
                        }
                    }
                } else if (message.startsWith("/command")) {
                    StringBuffer text = new StringBuffer();
                    text.append("\t• \"/exit\" - to exit the server;\n").append("\t• \"/pm username_receiver message\" - to send a private message;");
                    writer.println(text);
                } else server.broadcast(message, this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageUserRegistered(boolean isRegistered) {
        writer.println("/singUp " + isRegistered);
    }

    public void sendMessageUserExists(boolean isRegistered) {
        writer.println("/logIn " + isRegistered);
    }

    public void error() {
        writer.println("This user is not connected -_-");
    }

    public void sendListMembersOnline(String message) {
        writer.println(message);
    }

    public void sendServerMessage(String message) {
        writer.println(message);
    }

    public void sendMessage(String message, ServerHandler sender) {
        writer.println(server.clientsOnline.get(sender) + ": " + message);
    }

    public void sendMessageToYourself(String message) {
        writer.println("You: " + message);
    }

    public void sendPrivateMessageToYourself(String message, ServerHandler receiver) {
        writer.println("You (PM for " + server.clientsOnline.get(receiver) + "): " + message);
    }

    public void sendPrivateMessage(String message, ServerHandler sender) {
        writer.println(server.clientsOnline.get(sender) + " (PM): " + message);
    }

    private boolean verifyUserRegistration(String username, String password) {
        for (String key :  server.clientsRegistered.keySet()) {
            if (username.equals(key) && server.clientsRegistered.get(key).equals(password)) {
                return true;
            }
        }
        return false;
    }

    private void announceNewConnection() {
        server.broadcast("[SERVER]: " + server.clientsOnline.get(this) + " joined", this);
    }

    private boolean validateUniqueUsername(String username) {
        for (String key :  server.clientsRegistered.keySet()) {
            if (username.equals(key)) {
                return false;
            }
        }
        return true;
    }
}
