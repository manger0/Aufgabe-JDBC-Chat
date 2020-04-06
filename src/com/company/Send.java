package com.company;

import java.util.Scanner;
import java.util.TimerTask;

public class Send extends TimerTask {

    // Variables
    private String myCurrentMessage = "";
    private int messageCount = 0;
    private int messageCountBefore = 0;

    // Methods
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("write message or write exit");
        myCurrentMessage = scanner.nextLine();
        messageCount++;
    }

    public String getCurrentMessage() {
        messageCountBefore = messageCount;
        return myCurrentMessage;
    }

    public boolean newMessage() {
        return (messageCount > messageCountBefore);
    }

    public boolean exitChat() {
        return (myCurrentMessage.equalsIgnoreCase("exit"));
    }
}
