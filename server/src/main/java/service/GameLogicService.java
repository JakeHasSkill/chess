package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.LoginData;
import model.UserData;

import java.util.ArrayList;
import java.util.UUID;

public class GameLogicService {
    private DataAccess database;

    public GameLogicService() {
        this.database = new MemoryDataAccess();
    }

    public UserData getUser(String username) {
        return database.getUserData(username);
    }

    public AuthData registerUser(UserData userData) {
        if (getUser(userData.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        if (userData.password() == null) {
            throw new InvalidRequestException("Password cannot be null");
        }
        // TODO: check password length (We only see the hash, is that possible here?)
        database.addUser(userData);
        return generateAuthData(userData);
    }

    private AuthData generateAuthData(UserData userData) {
        UUID uuid = UUID.randomUUID();
        String authToken = uuid.toString();
        AuthData authData = new AuthData(authToken, userData.username());
        database.addAuthData(authData);
        return authData;
    }

    public AuthData login(LoginData loginData) {
        if (loginData.password() == null) {
            throw new InvalidRequestException("Password cannot be null");
        }
        if (loginData.username() == null) {
            throw new InvalidRequestException("Username cannot be null");
        }
        UserData userData = database.getUserData(loginData.username());
        if (userData == null) {
            throw new InvalidLoginException("Incorrect username or password");
        }
        if (!userData.password().equals(loginData.password())) {
            throw new InvalidLoginException("Incorrect username or password");
        }
        return generateAuthData(userData);
    }

    public void logout(String authToken) {
        if (authToken == null) {
            throw new InvalidAuthToken("AuthToken cannot be null");
        }
        ArrayList<AuthData> authData = database.getAuthData();
        for (AuthData data : authData) {
            if (data.authToken().equals(authToken)) {
                database.deleteAuthData(data);
                return;
            }
        }
        throw new InvalidAuthToken("Invalid AuthToken");
    }

    private AuthData validateAuthToken(String authToken) {
        if (authToken == null) {
            throw new InvalidAuthToken("AuthToken cannot be null");
        }
        ArrayList<AuthData> authData = database.getAuthData();
        for (AuthData data : authData) {
            if (data.authToken().equals(authToken)) {
                return data;
            }
        }
        throw new InvalidAuthToken("Invalid AuthToken");
    }

    public ArrayList<GameData> listGames(String authToken) {
        validateAuthToken(authToken);
        return database.getAllGameData();
    }

    public int createGame(GameData gameData, String authToken) {
        validateAuthToken(authToken);
        if (gameData == null) {
            throw new InvalidRequestException("GameData cannot be null");
        }
        if (gameData.gameName() == null) {
            throw new InvalidRequestException("Game Name cannot be null");
        }
        return database.addGameData(gameData);
    }

    public void clearEntireDatabase() {
        database.clearEntireDatabase();
        /* TODO: add a command line flag that enables this, have it be disabled by default
         *  If it is enabled give a warning in std.err
         *  Might just be better to remove clearEntireDatabase entirely
         */
    }
}
