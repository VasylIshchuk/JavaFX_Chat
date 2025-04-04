package org.umcs.chat;


import org.umcs.chat.controllers.ChatController;
import org.umcs.chat.controllers.LoginController;
import org.umcs.chat.controllers.RegistrationController;

public class ClientReceiver {
    public static ChatController chatController;
    public static LoginController loginController;
    public static RegistrationController registrationController;

    public static void receive(String message) {
        chatController.onReceiveMessage(message);
    }

    public static void receiveList(String message) {
        chatController.takeListOfMembers(message);
    }

    public static void verifyUserRegistration(boolean isRegistered) {
        loginController.verifyUserRegistration(isRegistered);
    }

    public static void userRegistered(boolean isRegistered) {
        registrationController.userRegistered(isRegistered);
    }
//    Use it, so that the controller that is created after the load, can be used wherever
//    it is needed and not use a different controller every time

}
