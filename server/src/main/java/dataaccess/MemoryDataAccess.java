package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Map;

public class MemoryDataAccess implements DataAccess{
    private ArrayList<AuthData> authData;
    private ArrayList<GameData> gameData;
    private ArrayList<UserData> userData;

    public MemoryDataAccess() {

    }

    @Override
    public UserData getUserData(String username) {
        return null;
    }

    @Override
    public void addUser(UserData userData) {

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

    }
}
