package org.umcs.chat.server;

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
    String username;

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
        clientRegistration();
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
                if (message.equals("/exit")) {
                    server.disconnect("[SERVER]: " + username + " left the chat", this);
                    break;
                } else if (message.equals("/online")) {
                    //      This command is automatically sent after connecting and disconnecting the user to update
                    //      the list of online members of the chat on the "listView" in the "ChatController" class,
                    //      the "updateListOfMembers" method.
                    StringBuilder onlineUsers = new StringBuilder();
                    for (String username : server.clientsOnline.keySet())
                        onlineUsers.append(username).append(",");
                    server.broadcast(message + " " + onlineUsers, this);
                } else if (message.startsWith("/pm ")) {
                    String[] splitMessage = message.split(" ", 3);
                    server.privateMessage(splitMessage[2], this, server.clientsOnline.get(splitMessage[1]));
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
        writer.println(sender.username + ": " + message);
    }

    public void sendMessageToYourself(String message) {
        writer.println("You: " + message);
    }

    public void sendPrivateMessageToYourself(String message, ServerHandler receiver) {
        writer.println("You (PM for " + receiver.username + "): " + message);
    }

    public void sendPrivateMessage(String message, ServerHandler sender) {
        writer.println(sender.username + " (PM): " + message);
    }

    private void clientRegistration() {
        displayWelcomeMessage();
        promptForUsernameMessage();
        askUsername();
        addNewClient();
        showFinishRegistrationMessage();
        showChatInstructions();
        announceNewConnection();
    }

    private void displayWelcomeMessage() {
        writer.println("Welcome to the server ^_^");
    }

    private void promptForUsernameMessage() {
        writer.println("Enter your username so other users know who is connecting to the chat o_o");
    }

    private void addNewClient() {
        server.clientsOnline.put(username, this);
    }

    private void announceNewConnection() {
        server.broadcast("[SERVER]: " + username + " joined", this);
    }

    private void showFinishRegistrationMessage() {
        writer.println("Thanks ^-^\n" + "Now you can chat with other users ^.^\n");
    }

    private void showChatInstructions() {
        StringBuffer text = new StringBuffer();
        text.append("Additional commands:\n").append("\t• \"/exit\" - to exit the server;\n").append("\t• \"/pm username_receiver message\" - to send a private message;\n").append("\t• \"/command\" - to show all commands\n").append("Let's go!\n");
        writer.println(text);
    }

    private void askUsername() {
        try {
            username = reader.readLine();
            writer.println(username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!validateUniqueUsername(username)) askUsername();
    }

    private boolean validateUniqueUsername(String username) {
        for (String key : server.clientsOnline.keySet()) {
            if (username.equals(key)) {
                notifyUsernameInUse();
                return false;
            }
        }
        return true;
    }

    private void notifyUsernameInUse() {
        writer.println("Sorry, this username is already in use. \n" + "Please try again o.o");
    }
}
