package ui;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class CommandLineUI {
    public CommandLineUI() throws IOException {
        try (Terminal terminal = new DefaultTerminalFactory()
                .setForceTextTerminal(true).createTerminal()){
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            BasicWindow mainMenuWindow = new BasicWindow("Main Menu");
            gui.addWindowAndWait(mainMenuWindow);
        }
        catch (IOException e) {
            System.err.println("Unable to start command line UI");
        }
    }
}
