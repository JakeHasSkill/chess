package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.LoginData;
import model.UserData;

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

    public void clearEntireDatabase() {
        database.clearEntireDatabase();
        /* TODO: add a command line flag that enables this, have it be disabled by default
         *  If it is enabled give a warning in std.err
         *  Might just be better to remove clearEntireDatabase entirely
         */
    }
}
