package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    UserData getUserData(String username);
    void addUser(UserData userData);
    AuthData getAuthData(UserData userData);
    void addAuthData(AuthData authData);
    GameData getGameData(String gameName);
    ArrayList<GameData> getAllGameData();
    void clearEntireDatabase(); // Yes this is sketchy
}
