package client;

import chess.*;
import ui.CommandLineUI;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) {
        try {
            CommandLineUI ui = new CommandLineUI();
        }
        catch (IOException e) {
            System.err.println("Unable to start command line UI");
        }
    }
}
