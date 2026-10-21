package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.UserData;

public class GameLogicService {
    private DataAccess database;

    public GameLogicService() {
        this.database = new MemoryDataAccess();
    }

    public UserData getUser(String username) {
        return database.getUserData(username);
    }

    public void registerUser(UserData userData) {
        if (getUser(userData.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        database.addUser(userData);
    }

    public void clearEntireDatabase() {
        database.clearEntireDatabase();
    }
}
