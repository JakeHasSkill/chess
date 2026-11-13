package client;

import ui.CommandLineUI;
import chess.*;
import model.*;

import java.util.ArrayList;

public class ClientLogic {
    private AuthData currentAuthData;
    private GameData currentGame;
    public ClientLogic() {
        CommandLineUI ui = new CommandLineUI();
    }

    public void login(String username, String password) {

    }

    public void register(String username, String password, String email) {

    }

    public void logout() {

    }

    public boolean createGame(String name) {

    }

    public ArrayList<GameData> listGames() {

    }

    public void joinGame(int gameID, ChessGame.TeamColor color) {

    }

    public void observeGame(int gameID) {

    }
}
