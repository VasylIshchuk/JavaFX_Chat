package org.umcs.chat;

public class ClientReceiver {
    public static ChatController controller;

    public static void receive(String message) {
        controller.onReceiveMessage(message);
    }

    public static void receiveList(String message) {
        controller.takeListOfMembers(message);
    }
//    Use it, so that the controller that is created after the load, can be used wherever
//    it is needed and not use a different controller every time
}
