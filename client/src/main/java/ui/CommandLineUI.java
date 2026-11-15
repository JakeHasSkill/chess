package ui;


import java.io.IOException;

import static ui.EscapeSequences.*;

public class CommandLineUI {
    private final CLIKeyListener cliKeyListener = new CLIKeyListener();
    private int terminalWidth, terminalHeight;
    private StringBuilder screenBufferCenter, screenBufferTop, screenBufferBottom;
    private enum RowAlign {TOP, CENTER, BOTTOM}

    public CommandLineUI() {
        try {
            runBashCommand("stty raw -echo < /dev/tty");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Failed to switch terminal to raw mode");
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(CommandLineUI::revertTerminal));
        screenBufferCenter = new StringBuilder();
        screenBufferTop = new StringBuilder();
        screenBufferBottom = new StringBuilder();

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
            switch (highlightedOption) {
                case LOGIN -> {drawLine(SET_TEXT_BOLD + "LOGIN" + RESET_ALL_FORMATTING + " | REGISTER | QUIT", RowAlign.CENTER);}
                case REGISTER -> {drawLine("LOGIN | " + SET_TEXT_BOLD + "REGISTER" + RESET_ALL_FORMATTING + " | QUIT", RowAlign.CENTER);}
                case QUIT -> {drawLine("LOGIN | REGISTER | " + SET_TEXT_BOLD + "QUIT" + RESET_ALL_FORMATTING, RowAlign.CENTER);}
            }
            drawLine("———CHESS———", RowAlign.TOP);
            drawLine("Arrow keys to navigate; Enter to select", RowAlign.BOTTOM);
            drawScreen();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                System.err.println("Got interrupted in pre login loop");
            }
        }
    }

    /**
     *  Draws the string centered to the middle of the line
     */
    private void drawLine(String contents, RowAlign rowAlign) {
        StringBuilder buffer = screenBufferCenter;
        switch (rowAlign) {
            case TOP -> buffer = screenBufferTop;
            case BOTTOM -> buffer = screenBufferBottom;
        }
        buffer.repeat(" ", (terminalWidth - contents.replaceAll("\\e\\[[0-9;]*[a-zA-Z]|\\p{Cntrl}", "").length()) / 2);
        buffer.append(contents);
        buffer.append("\r\n");
    }

    private void drawScreen() {
        StringBuilder entireBuffer = new StringBuilder();
        System.out.print(ERASE_SCREEN);
        int linesCenter = (int) screenBufferCenter.toString().lines().count();
        int linesTop = (int) screenBufferTop.toString().lines().count();
        int linesBottom = (int) screenBufferBottom.toString().lines().count();
        entireBuffer.append(screenBufferTop);
        entireBuffer.repeat("\r\n", ((terminalHeight - linesCenter) / 2) - linesTop);
        entireBuffer.append(screenBufferCenter);
        entireBuffer.repeat("\r\n", ((terminalHeight - linesCenter) / 2) - linesBottom);
        entireBuffer.append(screenBufferBottom);
        System.out.print(entireBuffer);
        System.out.flush();
        screenBufferTop = new StringBuilder();
        screenBufferCenter = new StringBuilder();
        screenBufferBottom = new StringBuilder();
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
        System.out.print(ERASE_SCREEN + EXIT_ALT_SCREEN + RESET_ALL_FORMATTING + SHOW_CURSOR + "Goodbye!\r\n");
        System.out.flush();
    }
}
