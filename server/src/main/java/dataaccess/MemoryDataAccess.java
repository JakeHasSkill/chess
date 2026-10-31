package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MemoryDataAccess implements DataAccess{
    private ArrayList<AuthData> authData;
    private ArrayList<GameData> gameData;
    private int nextGameID;
    private Map<String, UserData> userData;

    public MemoryDataAccess() {
        authData = new ArrayList<>();
        gameData = new ArrayList<>();
        userData = new TreeMap<>();
        nextGameID = 1;
    }

    @Override
    public UserData getUserData(String username) {
        return userData.get(username);
    }

    @Override
    public void addUser(UserData userData) {
        this.userData.put(userData.username(), userData);
    }

    @Override
    public AuthData getAuthData(String authToken) {
        for (AuthData data : authData) {
            if (authToken.equals(data.authToken())) {
                return new AuthData(data.authToken(), data.username());
            }
        }
        return null;
    }

    @Override
    public void deleteAuthData(AuthData authData) {
        this.authData.remove(authData);
    }

    @Override
    public void addAuthData(AuthData authData) {
        this.authData.add(authData);
    }

    @Override
    public GameData getGameData(int gameID) throws DataAccessException {
        for (GameData game : gameData) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Game doesn't exist with that ID");
    }

    @Override
    public int addGameData(GameData gameData) {
        this.gameData.add(new GameData(nextGameID, gameData));
        nextGameID++;
        return nextGameID - 1;
    }

    @Override
    public void editGameData(GameData gameData) {
        for (GameData game : this.gameData) {
            if (game.gameID() == gameData.gameID()) {
                int index = this.gameData.indexOf(game);
                this.gameData.set(index, gameData);
            }
        }
    }

    @Override
    public ArrayList<GameData> getAllGameData() {
        return new ArrayList<>(gameData);
    }

    @Override
    public void clearEntireDatabase() {
        authData = new ArrayList<>();
        gameData = new ArrayList<>();
        userData = new TreeMap<>();
    }
}
