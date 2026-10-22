package dataaccess;

import model.AuthData;
import model.GameData;
import model.LoginData;
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
    public ArrayList<AuthData> getAuthData(LoginData loginData) {
        ArrayList<AuthData> output = new ArrayList<>();
        for (AuthData data : this.authData) {
            if (data.username().equals(loginData.username())) {
                output.add(data);
            }
        }
        return output;
    }

    @Override
    public ArrayList<AuthData> getAuthData() {
        return new ArrayList<>(authData);
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
    public GameData getGameData(String gameName) throws DataAccessException {
        for (GameData game : gameData) {
            if (game.gameName().equals(gameName)) {
                return game;
            }
        }
        throw new DataAccessException("Game doesn't exist with that name");
    }

    @Override
    public int addGameData(GameData gameData) {
        this.gameData.add(new GameData(nextGameID, gameData));
        nextGameID++;
        return nextGameID - 1;
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
