package org.umcs.chat.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    ServerSocket serverSocket;
    Map<String, ServerHandler> clients = new HashMap<>();
    public static final String BLUE = "\u001B[34m";
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";

    public void start(int port)  {
        try {
            serverSocket = new ServerSocket(port); //ServerSocket creates a socket to be attached to a port.
            System.out.println(BLUE + "Server started" + "\n" + RESET );
            while (true) {
                Socket clientSocket = serverSocket.accept();
//            ".accept()" is used to wait. Stops program until it detects a connection from the client.
                System.out.println(CYAN + "Client connected" + RESET);
                ServerHandler serverHandler = new ServerHandler(clientSocket, this);
                Thread thread = new Thread(serverHandler);
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcast(String message, ServerHandler sender){
        for(ServerHandler receiver : clients.values()){
            if(message.contains("/online")) receiver.sendListMembersOnline(message);
            else if(message.startsWith("[SERVER]: ") && !receiver.equals(sender)) receiver.sendServerMessage(message);
            else if(!receiver.equals(sender)) receiver.sendMessage(message, sender);
        }
    }

    public void disconnect(String message, ServerHandler serverHandler){
        clients.remove(serverHandler.username);
        for(ServerHandler receiver : clients.values()){
            if(!receiver.equals(serverHandler)) receiver.sendServerMessage(message);
        }
    }
    public void privateMessage(String message, ServerHandler sender, ServerHandler receiver){
        boolean isConnected = false;
        for(ServerHandler client : clients.values()){
            if(client.equals(receiver)){
                receiver.sendPrivateMessage(message,sender);
                isConnected = true;
                break;
            }
        }
        if(!isConnected) sender.error();
    }
}
