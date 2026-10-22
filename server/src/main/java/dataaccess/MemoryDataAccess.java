package dataaccess;

import model.AuthData;
import model.GameData;
import model.LoginData;
import model.UserData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class MemoryDataAccess implements DataAccess{
    private ArrayList<AuthData> authData;
    private Map<String, GameData> gameData;
    private Map<String, UserData> userData;

    public MemoryDataAccess() {
        authData = new ArrayList<>();
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
    public GameData getGameData(String gameName) {
        return null;
    }

    @Override
    public ArrayList<GameData> getAllGameData() {
        return null;
    }

    @Override
    public void clearEntireDatabase() {
        authData = new ArrayList<>();
        gameData = new TreeMap<>();
        userData = new TreeMap<>();
    }
}
