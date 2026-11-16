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

        System.out.print(ENTER_ALT_SCREEN + ERASE_SCREEN + HIDE_CURSOR + "Chess UI :)");
        System.out.flush();

        ui: while (true) {
            switch (preLoginLoop()) {
                case REGISTER -> {
                    registerLoop();
                }
                case LOGIN -> {
                }
                case QUIT -> {break ui;}
            }
        }

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

    private Destination registerLoop() {
        OptionGrid grid = new OptionGrid(Destination.LOGIN, new Option[][]{{
            new Button("Back", Destination.PRE_LOGIN),
            new TextField("Username"),
            new TextField("Password"),
            new TextField("Email"),
            new Button("Submit", Destination.SUBMIT_LOGIN)
        }});
        while (true) {
            drawLines(grid.toString(), RowAlign.CENTER);
            drawLines("———CHESS———", RowAlign.TOP);
            drawLines("Arrow keys to navigate; Enter to select", RowAlign.BOTTOM);
            drawScreen();
            CLIKeyListener.KeyPress keyPress = cliKeyListener.getNextKeyPress();
            if (keyPress.key() == CLIKeyListener.Key.UP) {grid.goUp();}
            else if (keyPress.key() == CLIKeyListener.Key.DOWN) {grid.goDown();}
            else if (keyPress.key() == CLIKeyListener.Key.ENTER) {
                switch (grid.select()) {
                    case PRE_LOGIN -> {return Destination.PRE_LOGIN;}
                    case SUBMIT_LOGIN -> {
                        // TODO: call game logic and register the user
                        return Destination.PRE_LOGIN;
                    }
                    default -> {continue;}
                }
            }
            else if (keyPress.key() == CLIKeyListener.Key.ESCAPE) {return Destination.PRE_LOGIN;}
            else if (keyPress.key() == CLIKeyListener.Key.BACKSPACE) {grid.backspace();}
            else if (keyPress.key() == CLIKeyListener.Key.CHAR) {grid.sendLetter(keyPress.character());}
        }
    }

    private Destination preLoginLoop() {
        OptionGrid grid = new OptionGrid(Destination.PRE_LOGIN, new Option[][]{{
            new Button("Login", Destination.LOGIN),
            new Button("Register", Destination.REGISTER),
            new Button("Quit", Destination.QUIT)
        }});
        while (true) {
            drawLines(grid.toString(), RowAlign.CENTER);
            drawLines("———CHESS———", RowAlign.TOP);
            drawLines("Arrow keys to navigate; Enter to select", RowAlign.BOTTOM);
            drawScreen();
            CLIKeyListener.KeyPress keyPress = cliKeyListener.getNextKeyPress();
            if (keyPress.key() == CLIKeyListener.Key.UP) {grid.goUp();}
            else if (keyPress.key() == CLIKeyListener.Key.DOWN) {grid.goDown();}
            else if (keyPress.key() == CLIKeyListener.Key.ENTER) {
                switch (grid.select()) {
                    case LOGIN -> {return Destination.LOGIN;}
                    case REGISTER -> {return Destination.REGISTER;}
                    case QUIT -> {return Destination.QUIT;}
                }
            }
            else if (keyPress.key() == CLIKeyListener.Key.ESCAPE) {return Destination.QUIT;}
        }
    }

    /**
     *  Draws each line in the string centered to the middle of that line
     */
    private void drawLines(String contents, RowAlign rowAlign) {
        int width = 0;
        for (String line : contents.lines().toList()) {
            int lineWidth = line.replaceAll("\\e\\[[0-9;]*[a-zA-Z]|\\p{Cntrl}", "").length();
            if (lineWidth > width) {width = lineWidth;}
        }
        for (String line : contents.lines().toList()) {
            StringBuilder buffer = screenBufferCenter;
            switch (rowAlign) {
                case TOP -> buffer = screenBufferTop;
                case BOTTOM -> buffer = screenBufferBottom;
            }
            buffer.repeat(" ", (terminalWidth - width) / 2);
            buffer.append(line);
            buffer.append("\r\n");
        }
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
        entireBuffer.repeat("\r\n", ((terminalHeight - linesCenter + 1) / 2) - linesBottom);
        entireBuffer.append(screenBufferBottom.toString().stripTrailing());
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
