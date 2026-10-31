package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MariaDBDataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.UUID;

public class GameLogicService {
    private DataAccess database;

    public GameLogicService() {
        try {
            this.database = new MariaDBDataAccess();
        }
        catch (DataAccessException e) {
            this.database = new MemoryDataAccess();
            System.err.println("Creating SQL Database Access failed, falling back to in-memory database");
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        return database.getUserData(username);
    }

    public AuthData registerUser(UserData userData) throws DataAccessException {
        if (userData == null) {
            throw new InvalidRequestException("UserData cannot be null");
        }
        if (userData.password() == null || userData.username() == null) {
            throw new InvalidRequestException("Password/Username cannot be null");
        }
        if (getUser(userData.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        if (userData.password().length() < 7) {
            throw new InvalidRequestException("Password must be 8 characters or longer");
        }
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        UserData userDataHashed = new UserData(userData.username(), hashedPassword, userData.email());
        database.addUser(userDataHashed);
        return generateAuthData(userDataHashed);
    }

    private AuthData generateAuthData(UserData userData) {
        UUID uuid = UUID.randomUUID();
        String authToken = uuid.toString();
        AuthData authData = new AuthData(authToken, userData.username());
        database.addAuthData(authData);
        return authData;
    }

    public AuthData login(LoginData loginData) throws DataAccessException {
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
        if (!BCrypt.checkpw(loginData.password(), userData.password())) {
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

    public void joinGame(JoinData joinData, String authToken) {
        AuthData authData = validateAuthToken(authToken);
        if (joinData == null) {
            throw new InvalidRequestException("JoinData cannot be null");
        }
        if (joinData.playerColor() == null) {
            throw new InvalidRequestException("Player color cannot be null");
        }
        try {
            GameData game = database.getGameData(joinData.gameID());
            if (joinData.playerColor().equals(ChessGame.TeamColor.BLACK)) {
                if (game.blackUsername() != null) {
                    throw new AlreadyTakenException("Black player is already assigned");
                }
                game = new GameData(game.gameID(), game.whiteUsername(), authData.username(), game.gameName(), game.game());
            } else {
                if (game.whiteUsername() != null) {
                    throw new AlreadyTakenException("White player is already assigned");
                }
                game = new GameData(game.gameID(), authData.username(), game.blackUsername(), game.gameName(), game.game());
            }
            database.editGameData(game);
        } catch (DataAccessException e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    public void clearEntireDatabase() throws DataAccessException {
        database.clearEntireDatabase();
        /* TODO: add a command line flag that enables this, have it be disabled by default
         *  If it is enabled give a warning in std.err
         *  Might just be better to remove clearEntireDatabase entirely
         */
    }
}
