package client;

import ui.CommandLineUI;
import chess.*;
import model.*;

import java.util.ArrayList;

public class ClientLogic {
    private ServerFacade serverFacade;
    private AuthData currentAuthData;
    private GameData currentGame;
    public ClientLogic() {
        this.serverFacade = new ServerFacade("http://localhost:8080");
        CommandLineUI ui = new CommandLineUI(this);
    }

    public void login(String username, String password) {

    }

    public enum RegisterResult {
        OK, USERNAME_TAKEN, BAD_PASSWORD, SERVER_ERROR
    }

    public RegisterResult register(String username, String password, String email) {
        if (password.length() < 8) {return RegisterResult.BAD_PASSWORD;}
        try {
            currentAuthData = serverFacade.register(username, password, email);
            return RegisterResult.OK;
        }
        catch (ServerError e) {
            return RegisterResult.SERVER_ERROR;
        }
        catch (UsernameTaken e) {return RegisterResult.USERNAME_TAKEN;}
    }

    public void logout() {

    }

    public boolean createGame(String name) {
        return false;
    }

    public ArrayList<GameData> listGames() {
        return null;
    }

    public void joinGame(int gameID, ChessGame.TeamColor color) {

    }

    public void observeGame(int gameID) {

    }
}
