package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    UserData getUserData(String username) throws DataAccessException;
    void addUser(UserData userData) throws DataAccessException;
    ArrayList<AuthData> getAuthData();
    void deleteAuthData(AuthData authData) throws DataAccessException;
    void addAuthData(AuthData authData) throws DataAccessException;
    GameData getGameData(int gameID) throws DataAccessException;
    int addGameData(GameData gameData);
    void editGameData(GameData gameData);
    ArrayList<GameData> getAllGameData();
    void clearEntireDatabase() throws DataAccessException; // Yes this is sketchy
}
