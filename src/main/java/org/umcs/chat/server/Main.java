package org.umcs.chat.server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start(1599);
    }
}
