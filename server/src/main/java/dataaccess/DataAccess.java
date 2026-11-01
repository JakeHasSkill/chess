package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    UserData getUserData(String username) throws DataAccessException;
    void addUser(UserData userData) throws DataAccessException;
    AuthData getAuthData(String authToken) throws DataAccessException;
    void deleteAuthData(AuthData authData) throws DataAccessException;
    void addAuthData(AuthData authData) throws DataAccessException;
    GameData getGameData(int gameID) throws DataAccessException;
    int addGameData(GameData gameData) throws DataAccessException;
    void editGameData(GameData gameData) throws DataAccessException;
    ArrayList<GameData> getAllGameData() throws DataAccessException;
    void clearEntireDatabase() throws DataAccessException; // Yes this is sketchy
}
