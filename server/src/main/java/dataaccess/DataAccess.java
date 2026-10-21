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
    void addAuthData(AuthData authData);
    GameData getGameData(String gameName);
    ArrayList<GameData> getAllGameData();
    void clearEntireDatabase(); // Yes this is sketchy
}
