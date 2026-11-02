package dataAccess;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MariaDBDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MariaDBDataAccessTests {
    private static DataAccess database;

    @BeforeAll
    public static void setupTables() throws DataAccessException {
        DatabaseManager.createDatabase();
        database = new MariaDBDataAccess();
        database.clearEntireDatabase();
    }

    @AfterEach
    public void clearDB() throws DataAccessException {
        database.clearEntireDatabase();
    }

    @Test
    public void createUser() throws DataAccessException {
        UserData user = new UserData("username", "secure_password", "email");
        database.addUser(user);
        Assertions.assertEquals(user, database.getUserData("username"));
        Assertions.assertNull(database.getUserData("bob"));
    }

    @Test
    public void AuthData() throws DataAccessException {
        UserData user = new UserData("username", "secure_password", "email");
        database.addUser(user);
        AuthData auth1 = new AuthData("111111111122222222223333333333444444444455555555556666666666", "username");
        database.addAuthData(auth1);
        AuthData auth2 = database.getAuthData(auth1.authToken());
        Assertions.assertEquals(auth1, auth2);
        database.deleteAuthData(auth1);
        Assertions.assertNull(database.getAuthData(auth1.authToken()));
    }

    @Test
    public void gameData() throws DataAccessException {
        GameData game = new GameData(0, null, null, "game1", new ChessGame());
        Assertions.assertEquals(1, database.addGameData(game));
        game = new GameData(1, "player1", "player2", "game1", game.game());
        database.editGameData(game);
        Assertions.assertNull(database.getGameData(2));
        Assertions.assertEquals(game, database.getGameData(1));
        database.addGameData(new GameData(0, null, null, "game2", new ChessGame()));
        Assertions.assertEquals(2, database.getAllGameData().size());
    }
}
