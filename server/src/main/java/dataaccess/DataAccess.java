package dataaccess;

import model.AuthData;
import model.GameData;
import model.LoginData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    UserData getUserData(String username);
    void addUser(UserData userData);
    ArrayList<AuthData> getAuthData(LoginData loginData);
    ArrayList<AuthData> getAuthData();
    void deleteAuthData(AuthData authData);
    void addAuthData(AuthData authData);
    GameData getGameData(int gameID) throws DataAccessException;
    int addGameData(GameData gameData);
    void editGameData(GameData gameData);
    ArrayList<GameData> getAllGameData();
    void clearEntireDatabase(); // Yes this is sketchy
}
