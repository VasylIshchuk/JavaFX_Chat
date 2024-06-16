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
        try {
            clientRegistration();
            String message;
            while((message = reader.readLine())!= null){
                if (message.equals("EXIT")) {
                    server.disconnect("left a chat", this);
                    break;
                }else if(message.equals("/online")){
                    StringBuilder onlineUsers = new StringBuilder();
                    for(String username : server.clients.keySet())
                        onlineUsers.append(username).append(",");
                    writer.println("/online " + onlineUsers);
                    server.broadcast("/online " + onlineUsers,this);
                }else if(message.startsWith("/w ")){
                    String[] splitMessage = message.split(" ",3);
                    server.privateMessage(splitMessage[2],this,server.clients.get(splitMessage[1]));
                }
                else server.broadcast(message,this);
            }
            clientSocket.close();
            System.out.println("Client disconnected");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void error(){
        writer.println("This user is not connected -_-" );
    }

    public void send(String message, ServerHandler sender){
        if(message.startsWith("/online ")) writer.println(message);
         else writer.println(sender.username + ": " + message);
    }


    private void clientRegistration() throws IOException {
        writer.println();
        writer.println("Welcome to the server ^_^");
        getUsername();
        writer.println("Thanks ^-^");
        writer.println("Now you can chat with other users ^.^");
        writer.println("Additionally, if you want to leave the server, write \"EXIT\"");
        writer.println("Let's go!");
        writer.println();
    }

    private void getUsername() throws IOException {
        writer.println("Write your username so that other users know that you have connected to the server o_o");
        username = reader.readLine();

        boolean uniqueUsername = false;
        while(!uniqueUsername) {
            boolean isUnique = true;
            for (String key : server.clients.keySet() ) {
                if (username.equals(key)) {
                    writer.println( "Sorry, this username is already in use. \n" +
                            "Please try again o.o" );
                    username = reader.readLine();
                    isUnique = false;
                    break;
                }
            }
            if(isUnique) uniqueUsername = true;
        }
        server.clients.put(username, this);

        server.broadcast("joined" , this);
    }

}
