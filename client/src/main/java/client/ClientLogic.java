package client;

import ui.CommandLineUI;
import chess.*;
import model.*;

import java.util.ArrayList;

public class ClientLogic {
    private ServerFacade serverFacade;
    private AuthData currentAuthData;
    private GameData currentGame;
    private ChessGame.TeamColor myColor;
    private ArrayList<GameData> gameList;
    public ClientLogic() {
        this.serverFacade = new ServerFacade("http://localhost:8080");
        CommandLineUI ui = new CommandLineUI(this);
        gameList = new ArrayList<>();
    }

    public GameData getCurrentGame() {
        return currentGame;
    }

    public enum LoginResult {
        OK, INCORRECT_CREDENTIALS, SERVER_ERROR
    }

    public LoginResult login(String username, String password) {
        try {currentAuthData = serverFacade.login(username, password);}
        catch (InvalidCredentials e) {return LoginResult.INCORRECT_CREDENTIALS;}
        catch (ServerError e) {return LoginResult.SERVER_ERROR;}
        return LoginResult.OK;
    }

    public enum RegisterResult {
        OK, USERNAME_TAKEN, BAD_PASSWORD, SERVER_ERROR
    }

    public RegisterResult register(String username, String password, String email) {
        if (password.length() < 8) {return RegisterResult.BAD_PASSWORD;}
        try {currentAuthData = serverFacade.register(username, password, email);}
        catch (ServerError e) {return RegisterResult.SERVER_ERROR;}
        catch (UsernameTaken e) {return RegisterResult.USERNAME_TAKEN;}
        return RegisterResult.OK;
    }

    public enum LogoutResult {
        OK, SERVER_ERROR, NOT_LOGGED_IN
    }

    public LogoutResult logout() {
        try {serverFacade.logout(currentAuthData.authToken());}
        catch (NotLoggedIn e) {return LogoutResult.NOT_LOGGED_IN;}
        catch (ServerError e) {return LogoutResult.SERVER_ERROR;}
        this.currentAuthData = null;
        return LogoutResult.OK;
    }

    public enum CreateGameResult {
        OK, NOT_LOGGED_IN, SERVER_ERROR
    }

    public CreateGameResult createGame(String name) {
        try {serverFacade.createGame(currentAuthData.authToken(), new GameData(0, null, null, name, new ChessGame()));}
        catch (NotLoggedIn e) {return CreateGameResult.NOT_LOGGED_IN;}
        catch (ServerError e) {return CreateGameResult.SERVER_ERROR;}
        return CreateGameResult.OK;
    }

    public enum ListGamesResult {
        OK, NOT_LOGGED_IN, SERVER_ERROR
    }

    public ListGamesResult requestGames() {
        try {gameList = serverFacade.listGames(currentAuthData.authToken());}
        catch (NotLoggedIn e) {return ListGamesResult.NOT_LOGGED_IN;}
        catch (ServerError e) {return ListGamesResult.SERVER_ERROR;}
        return ListGamesResult.OK;
    }

    public ArrayList<GameData> getGames() {
        return gameList;
    }

    public GameData getGameData(String gameName) {
        for (GameData game : gameList) {
            if (gameName.equals(game.gameName())) {
                return game;
            }
        }
        return null;
    }

    public GameData getGameData(int gameID) {
        for (GameData game : gameList) {
            if (gameID == game.gameID()) {
                return game;
            }
        }
        return null;
    }

    public enum JoinGameResult {
        OK, TEAM_COLOR_TAKEN, SERVER_ERROR, NOT_LOGGED_IN
    }

    public JoinGameResult joinGame(int gameID, ChessGame.TeamColor color) {
        try {serverFacade.joinGame(currentAuthData.authToken(), new JoinData(color, gameID));}
        catch (TeamColorTaken e) {return JoinGameResult.TEAM_COLOR_TAKEN;}
        catch (ServerError e) {return JoinGameResult.SERVER_ERROR;}
        catch (NotLoggedIn e) {return JoinGameResult.NOT_LOGGED_IN;}
        myColor = color;
        requestGames();
        currentGame = getGameData(gameID);
        return JoinGameResult.OK;
    }

    public ChessGame.TeamColor getMyColor() {
        return myColor;
    }

    public void observeGame(int gameID) {
        requestGames();
        currentGame = getGameData(gameID);
        myColor = ChessGame.TeamColor.WHITE;
    }
}
