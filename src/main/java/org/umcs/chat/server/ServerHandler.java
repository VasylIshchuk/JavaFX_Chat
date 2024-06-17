package org.umcs.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler implements  Runnable{
    private final Server server;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Socket clientSocket;
    String username;

    public ServerHandler(Socket clientSocket, Server server)  {
        this.clientSocket = clientSocket;
        this.server = server;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()
                    ));
            writer = new PrintWriter(clientSocket.getOutputStream(),true);
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

    private void parseClientMessage(){
        String message;
        try {
            while(( message = reader.readLine())!= null){
                if (message.equals("/exit")) {
                    server.disconnect("[SERVER]: " + username +  " left a chat", this);
                    break;
                }else if(message.equals("/online")){
                    StringBuilder onlineUsers = new StringBuilder();
                    for(String username : server.clients.keySet())
                        onlineUsers.append(username).append(",");
                    server.broadcast(message + " " +  onlineUsers,this);
                }else if(message.startsWith("/pm ")){
                    String[] splitMessage = message.split(" ",3);
                    server.privateMessage(splitMessage[2],this,server.clients.get(splitMessage[1]));
                }else if(message.startsWith("/command")){
                    StringBuffer text = new StringBuffer();
                    text.append("\t• \"/exit\" - to exit the server;\n")
                            .append("\t• \"/pm\" - to send a private message;");
                    writer.println(text);
                } else server.broadcast(message,this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void error(){
        writer.println("This user is not connected -_-" );
    }
    public void sendListMembersOnline(String message){
         writer.println(message);
    }
    public void sendServerMessage(String message){
        writer.println(message);
    }
    public void sendMessage(String message, ServerHandler sender){
        writer.println(sender.username + ": " + message);
    }
    public void sendPrivateMessage(String message, ServerHandler sender){
        writer.println(sender.username + " (PM): " + message);
    }

    private void clientRegistration()  {
        writer.println("Welcome to the server ^_^");
        getUsername();
        StringBuffer text = new StringBuffer();
        text.append("Thanks ^-^\n")
                .append("Now you can chat with other users ^.^\n")
                .append("Additional commands:\n")
                .append("\t• \"/exit\" - to exit the server;\n")
                .append("\t• \"/pm\" - to send a private message;\n")
                .append("\t • \"/command\" - to show all commands\n")
                .append("Let's go!\n");
        writer.println(text);
    }

    private void getUsername()  {
        writer.println("Write your username so that other users know that you have connected to the server o_o");
        try {
            username = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        validateUniqueUsername();
        server.clients.put(username, this);
        server.broadcast("[SERVER]: " + username + " joined" , this);
    }

    private void validateUniqueUsername(){
        boolean uniqueUsername = false;
        while(!uniqueUsername) {
            boolean isUnique = true;
            for (String key : server.clients.keySet() ) {
                if (username.equals(key)) {
                    try {
                        writer.println( "Sorry, this username is already in use. \n" +
                                "Please try again o.o" );
                        username = reader.readLine();
                        isUnique = false;
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if(isUnique) uniqueUsername = true;
        }
    }
}
