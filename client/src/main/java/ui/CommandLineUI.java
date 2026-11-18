package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import chess.*;
import client.ClientLogic;
import model.GameData;

import static ui.EscapeSequences.*;

public class CommandLineUI {
    private ClientLogic registerLoopKeypress;
    private final CLIKeyListener cliKeyListener = new CLIKeyListener();
    private int terminalWidth, terminalHeight;
    private StringBuilder screenBufferCenter, screenBufferTop, screenBufferBottom;
    private enum RowAlign {TOP, CENTER, BOTTOM}
    private Destination currentScreen;
    private GameData gameToJoin;

    public CommandLineUI(ClientLogic clientLogic) {
        this.registerLoopKeypress = clientLogic;
        this.currentScreen = Destination.PRE_LOGIN;
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

        System.out.print(moveCursor(1, 1)); // this can be taken out later
        System.out.print(ENTER_ALT_SCREEN + ERASE_SCREEN + HIDE_CURSOR + "Chess UI :)");
        System.out.flush();

        ui: while (true) {
            switch (currentScreen) {
                case PRE_LOGIN -> currentScreen = preLoginLoop();
                case REGISTER -> currentScreen = registerLoop();
                case LOGIN -> currentScreen = loginLoop();
                case LIST_GAMES -> currentScreen = listGamesLoop();
                case CREATE_GAME -> currentScreen = createGameLoop();
                case JOIN_GAME -> currentScreen = joinGameLoop();
                case IN_GAME -> currentScreen = inGameLoop();
                case IN_GAME_OBSERVE -> currentScreen = observeGameLoop();
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

    private Destination observeGameLoop() {
        ChessGrid chessGrid = new ChessGrid(
                Destination.IN_GAME_OBSERVE, registerLoopKeypress.getCurrentGame().game().getBoard().getBoard(), ChessGame.TeamColor.WHITE
        );
        while (true) {
            drawMenu(chessGrid);
            switch (chessGrid.sendKeyPress(cliKeyListener.getNextKeyPress())) {
                case LIST_GAMES -> {return Destination.LIST_GAMES;}
                case IN_GAME_OBSERVE -> {continue;}
            }
        }
    }

    private Destination inGameLoop() {
        ChessGrid chessGrid = new ChessGrid(
                Destination.IN_GAME, registerLoopKeypress.getCurrentGame().game().getBoard().getBoard(), registerLoopKeypress.getMyColor()
        );
        while (true) {
            drawMenu(chessGrid);
            switch (chessGrid.sendKeyPress(cliKeyListener.getNextKeyPress())) {
                case LIST_GAMES -> {return Destination.LIST_GAMES;}
                case IN_GAME -> {continue;}
            }
        }
    }

    private Destination joinGameLoop() {
        ArrayList<Option> options = new ArrayList<>();
        if (gameToJoin.blackUsername() == null) {
            options.add(new Button("Join as Black team", Destination.IN_GAME));
        } else {
            options.add(new Button("Player " + gameToJoin.blackUsername() + " is on the Black team", Destination.JOIN_GAME));
        }
        if (gameToJoin.whiteUsername() == null) {
            options.add(new Button("Join as White team", Destination.IN_GAME));
        } else {
            options.add(new Button("Player " + gameToJoin.whiteUsername() + " is on the White team", Destination.JOIN_GAME));
        }
        options.add(new Button("Join as observer", Destination.IN_GAME_OBSERVE));
        options.add(new Button("Back", Destination.BACK));
        OptionGrid grid = new OptionGrid(Destination.JOIN_GAME, new Option[][]{Stream.of(
            options.stream()
        ).flatMap(s -> s).toArray(Option[]::new)});
        while (true) {
            drawMenu(grid);
            Destination preLogin = joinGameLoopKeypress(grid);
            if (preLogin != null) {
                return preLogin;
            }
        }
    }

    private Destination joinGameLoopKeypress(OptionGrid grid) {
        switch (grid.sendKeyPress(cliKeyListener.getNextKeyPress())) {
            case IN_GAME -> {
                ChessGame.TeamColor color;
                if (grid.getValue(grid.getSelectedRow(), grid.getSelectedColumn()).equals("Join as Black team")) {
                    color = ChessGame.TeamColor.BLACK;
                } else {color = ChessGame.TeamColor.WHITE;}
                switch (registerLoopKeypress.joinGame(gameToJoin.gameID(), color)) {
                    case NOT_LOGGED_IN -> {
                        return Destination.PRE_LOGIN;
                    }
                    case TEAM_COLOR_TAKEN -> {
                        registerLoopKeypress.requestGames();
                        gameToJoin = registerLoopKeypress.getGameData(gameToJoin.gameID());
                        return Destination.JOIN_GAME;
                    }
                    case SERVER_ERROR -> {drawLines("Unable to connect to server", RowAlign.BOTTOM);}
                    case OK -> {
                        return Destination.IN_GAME;
                    }
                }
            }
            case IN_GAME_OBSERVE -> {
                registerLoopKeypress.observeGame(gameToJoin.gameID());
                return Destination.IN_GAME_OBSERVE;
            }
            case BACK -> {
                return Destination.LIST_GAMES;
            }
            case JOIN_GAME -> {
                return null;
            }
        }
        return null;
    }

    private Destination createGameLoop() {
        OptionGrid grid = new OptionGrid(Destination.CREATE_GAME, new Option[][]{{
            new TextField("Game name"),
            new Button("Create Game", Destination.SUBMIT_CREATE_GAME),
            new Button("Back", Destination.BACK)
        }});
        while (true) {
            drawMenu(grid);
            Destination preLogin = createGameLoopKeypress(grid);
            if (preLogin != null) {
                return preLogin;
            }
        }
    }

    private Destination createGameLoopKeypress(OptionGrid grid) {
        switch (grid.sendKeyPress(cliKeyListener.getNextKeyPress())) {
            case SUBMIT_CREATE_GAME -> {
                String gameName = grid.getValue(0, 0);
                switch (registerLoopKeypress.createGame(gameName)) {
                    case NOT_LOGGED_IN -> {
                        drawLines("Not logged in", RowAlign.BOTTOM);
                        return Destination.PRE_LOGIN;
                    }
                    case SERVER_ERROR -> drawLines("Unable to connect to server", RowAlign.BOTTOM);
                    case OK -> {}
                }
                registerLoopKeypress.requestGames();
                gameToJoin = registerLoopKeypress.getGameData(gameName);
                if (gameToJoin == null) {
                    return Destination.LIST_GAMES;
                }
                return Destination.JOIN_GAME;
            }
            case BACK -> {
                return Destination.LIST_GAMES;
            }
            case CREATE_GAME -> {
                return null;
            }
        }
        return null;
    }

    private Destination listGamesLoop() {
        ArrayList<GameData> gameData = new ArrayList<>();
        switch (registerLoopKeypress.requestGames()) {
            case OK -> gameData = registerLoopKeypress.getGames();
            case NOT_LOGGED_IN -> {return Destination.PRE_LOGIN;}
            case SERVER_ERROR -> drawLines("Server Error; Unable to list games.", RowAlign.BOTTOM);
        }
        OptionGrid grid = new OptionGrid(Destination.LIST_GAMES, new Option[][]{Stream.of(
            Stream.of(new Button("Create New Game", Destination.CREATE_GAME),
            new Button("Join Game:", Destination.LIST_GAMES)),
            gameData.stream().map(game -> new Button(game.gameName(), Destination.JOIN_GAME)),
            Stream.of(new Button("Logout", Destination.BACK))
        ).flatMap(s -> s).toArray(Option[]::new)});
        while (true) {
            drawMenu(grid);
            Destination createGame = listGamesLoopKeypress(grid);
            if (createGame != null) {
                return createGame;
            }
        }
    }

    private Destination listGamesLoopKeypress(OptionGrid grid) {
        switch (grid.sendKeyPress(cliKeyListener.getNextKeyPress())) {
            case CREATE_GAME -> {
                return Destination.CREATE_GAME;
            }
            case BACK -> {
                switch (registerLoopKeypress.logout()) {
                    case OK -> {
                        return Destination.PRE_LOGIN;
                    }
                    case SERVER_ERROR -> {drawLines("Unable to connect to server", RowAlign.BOTTOM);}
                }
            }
            case JOIN_GAME -> {
                gameToJoin = registerLoopKeypress.getGameData(grid.getValue(grid.getSelectedRow(), grid.getSelectedColumn()));
                if (gameToJoin == null) {
                    return Destination.LIST_GAMES;
                }
                return Destination.JOIN_GAME;
            }
            case LIST_GAMES -> {
                return null;
            }
        }
        return null;
    }

    private Destination loginLoop() {
        OptionGrid grid = new OptionGrid(Destination.LOGIN, new Option[][]{{
            new TextField("Username"),
            new TextField("Password", true),
            new Button("Submit", Destination.SUBMIT_LOGIN),
            new Button("Back", Destination.BACK)
        }});
        while (true) {
            drawMenu(grid);
            Destination listGames = loginLoopKeypress(grid);
            if (listGames != null) {
                return listGames;
            }
        }
    }

    private Destination loginLoopKeypress(OptionGrid grid) {
        switch (grid.sendKeyPress(cliKeyListener.getNextKeyPress())) {
            case SUBMIT_LOGIN -> {
                String username = grid.getValue(0, 0);
                String password = grid.getValue(1, 0);
                switch (registerLoopKeypress.login(username, password)) {
                    case OK -> {
                        return Destination.LIST_GAMES;
                    }
                    case INCORRECT_CREDENTIALS -> drawLines("Incorrect username or password", RowAlign.BOTTOM);
                    case SERVER_ERROR -> drawLines("Unable to connect to server", RowAlign.BOTTOM);
                }
            }
            case BACK -> {
                return Destination.PRE_LOGIN;
            }
            case LOGIN -> {
                return null;
            }
        }
        return null;
    }

    private Destination registerLoop() {
        OptionGrid grid = new OptionGrid(Destination.REGISTER, new Option[][]{{
            new TextField("Username"),
            new TextField("Password", true),
            new TextField("Email"),
            new Button("Submit", Destination.SUBMIT_REGISTER),
            new Button("Back", Destination.PRE_LOGIN)
        }});
        while (true) {
            drawMenu(grid);
            Destination preLogin = registerLoopKeypress(grid);
            if (preLogin != null) {
                return preLogin;
            }
        }
    }

    private Destination registerLoopKeypress(OptionGrid grid) {
        switch (grid.sendKeyPress(cliKeyListener.getNextKeyPress())) {
            case PRE_LOGIN -> {
                return Destination.PRE_LOGIN;
            }
            case SUBMIT_REGISTER -> {
                String username = grid.getValue(0, 0);
                String password = grid.getValue(1, 0);
                String email = grid.getValue(2, 0);
                switch (registerLoopKeypress.register(username, password, email)) {
                    case OK -> {
                        return Destination.LIST_GAMES;
                    }
                    case BAD_PASSWORD -> {
                        drawLines("Password must be at least 8 characters long", RowAlign.BOTTOM);
                    }
                    case USERNAME_TAKEN -> {
                        drawLines("Username already taken", RowAlign.BOTTOM);
                    }
                    case SERVER_ERROR -> {
                        drawLines("Unable to connect to server", RowAlign.BOTTOM);
                    }
                }
            }
            case REGISTER -> {
                return null;
            }
        }
        return null;
    }

    private Destination preLoginLoop() {
        OptionGrid grid = new OptionGrid(Destination.PRE_LOGIN, new Option[][]{{
            new Button("Login", Destination.LOGIN),
            new Button("Register", Destination.REGISTER),
            new Button("Quit", Destination.BACK)
        }});
        while (true) {
            drawMenu(grid);
            switch (grid.sendKeyPress(cliKeyListener.getNextKeyPress())) {
                case LOGIN -> {return Destination.LOGIN;}
                case REGISTER -> {return Destination.REGISTER;}
                case BACK -> {return Destination.QUIT;}
                case PRE_LOGIN -> {continue;}
            }
        }
    }

    private void drawMenu(Grid grid) {
        setDimensions();
        drawLines(grid.toString(), RowAlign.CENTER);
        drawLines("———CHESS———", RowAlign.TOP);
        drawLines("Arrow keys to navigate; Enter to select", RowAlign.BOTTOM);
        drawScreen();
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
