package ui;


import java.io.IOException;
import java.io.InputStream;

import static ui.EscapeSequences.*;

public class CommandLineUI {
    private final CLIKeyListener cliKeyListener = new CLIKeyListener();
    private int terminalWidth, terminalHeight;

    public CommandLineUI() {
        try {
            runBashCommand("stty raw -echo < /dev/tty");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Failed to switch terminal to raw mode");
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(CommandLineUI::revertTerminal));

        setDimensions();

        System.out.print(ENTER_ALT_SCREEN + ERASE_SCREEN + "Chess UI :)");
        System.out.flush();

        Thread keyListenerThread = new Thread(cliKeyListener);
        keyListenerThread.setDaemon(true);
        keyListenerThread.start();

        preLoginLoop();

        revertTerminal();
    }

    private void setDimensions() {
        try {
            String result = runBashCommand("stty size < /dev/tty").trim();
            String[] results = result.split("\\s+");
            terminalHeight = Integer.parseInt(results[0]);
            terminalWidth = Integer.parseInt(results[1]);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error setting terminal dimensions");
        }
    }

    enum PreLoginOptions {
        LOGIN, REGISTER, QUIT;
        static PreLoginOptions goLeft(PreLoginOptions selected) {
            switch (selected) {
                case QUIT -> {return REGISTER;}
                default -> {return LOGIN;}
            }
        }
        static PreLoginOptions goRight(PreLoginOptions selected) {
            switch (selected) {
                case LOGIN -> {return REGISTER;}
                default -> {return QUIT;}
            }
        }
    }

    private PreLoginOptions preLoginLoop() {
        PreLoginOptions highlightedOption = PreLoginOptions.LOGIN;
        while (true) {
            var queue = cliKeyListener.getInputQueue();
            CLIKeyListener.KeyPressEvent keyPress;
            while ((keyPress = queue.poll()) != null) {
                if (keyPress.key() == CLIKeyListener.Key.LEFT) {
                    highlightedOption = PreLoginOptions.goLeft(highlightedOption);
                }
                else if (keyPress.key() == CLIKeyListener.Key.RIGHT) {
                    highlightedOption = PreLoginOptions.goRight(highlightedOption);
                }
                else if (keyPress.key() == CLIKeyListener.Key.ENTER) {
                    return highlightedOption;
                }
                else if (keyPress.key() == CLIKeyListener.Key.ESCAPE) {
                    return PreLoginOptions.QUIT;
                }
            }
            drawLine("LOGIN | REGISTER | QUIT\nCurrent selection:" + highlightedOption.name(), 4);
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                System.err.println("Got interrupted in pre login loop");
            }
        }
    }

    /**
     *  Draws the string centered on a given line number
     */
    private void drawLine(String contents, int lineNumber) {
        System.out.print(ERASE_SCREEN + contents + "\n" + terminalWidth + " | " + terminalHeight);
        System.out.flush();
    }

    private static String runBashCommand(String command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("sh", "-c", command).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes());
        process.waitFor();
        return output;
    }

    private static void revertTerminal() {
        try {
            runBashCommand("stty echo icanon < /dev/tty");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Error restoring terminal to previous state");
        }
        System.out.println(EXIT_ALT_SCREEN + RESET_ALL_FORMATTING + SHOW_CURSOR);
        System.out.flush();
    }
}
