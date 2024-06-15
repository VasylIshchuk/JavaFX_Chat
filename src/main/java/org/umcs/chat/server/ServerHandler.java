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
                switch (message){
                    case "EXIT" :
                        server.disconnect("left a chat", this);
                        break;
                    case "/online" :
                        for(String username :server.clients.keySet())
                            writer.println(username);
                    case "/w " :
                        String[] splitMessage = message.split(" ",3);
                        server.privateMessage(splitMessage[2],this,server.clients.get(splitMessage[1]));
                    default:
                        server.broadcast(message,this);
                }
            }
            clientSocket.close();
            System.out.println(ColorANSI.MAGENTA + "Client disconnected" + ColorANSI.RESET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void error(){
        writer.println(ColorANSI.MAGENTA +
                "This user is not connected -_-" +
                ColorANSI.RESET);
    }

    public void send(String message, ServerHandler sender){
        writer.println(ColorANSI.BLUE +
                sender.username + " " +
                ColorANSI.RESET +
                message);
    }


    private void clientRegistration() throws IOException {
        writer.println();
        writer.println(ColorANSI.BLUE + "Welcome to the server ^_^" );
        getUsername();
        writer.println(ColorANSI.BLUE + "Thanks ^-^" );
        writer.println("Now you can chat with other users ^.^");
        writer.println("Additionally, if you want to leave the server, write \"EXIT\"");
        writer.println("Let's go!" + ColorANSI.RESET);
        writer.println();
    }

    private void getUsername() throws IOException {
        writer.println("Write your username so that other users know that you have connected to the server o_o"
                + ColorANSI.RESET);
        username = reader.readLine();

        boolean uniqueUsername = false;
        while(!uniqueUsername) {
            boolean isUnique = true;
            for (String key : server.clients.keySet() ) {
                if (username.equals(key)) {
                    writer.println(ColorANSI.MAGENTA + "Sorry, this username is already in use. " +
                            "Please try again o.o" + ColorANSI.RESET );
                    username = reader.readLine();
                    isUnique = false;
                    break;
                }
            }
            if(isUnique) uniqueUsername = true;
        }
        server.clients.put(username, this);

        server.broadcast(ColorANSI.RESET +
                        ColorANSI.GREEN +
                        "joined" +
                        ColorANSI.RESET,
                this);
    }

}
