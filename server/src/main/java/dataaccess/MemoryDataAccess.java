package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MemoryDataAccess implements DataAccess{
    private Map<Integer, AuthData> authData;
    private Map<String, GameData> gameData;
    private Map<String, UserData> userData;

    public MemoryDataAccess() {
        authData = new TreeMap<>();
        gameData = new TreeMap<>();
        userData = new TreeMap<>();
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
    public AuthData getAuthData(UserData userData) {
        return null;
    }

    @Override
    public void addAuthData(AuthData authData) {

    }

    @Override
    public GameData getGameData(String gameName) {
        return null;
    }

    @Override
    public ArrayList<GameData> getAllGameData() {
        return null;
    }

    @Override
    public void clearEntireDatabase() {
        authData = new TreeMap<>();
        gameData = new TreeMap<>();
        userData = new TreeMap<>();
    }
}
