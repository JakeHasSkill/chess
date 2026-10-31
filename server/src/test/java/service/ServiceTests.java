package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ServiceTests {
    private static GameLogicService service;
    @BeforeAll
    public static void instantiateService() {
        service = new GameLogicService();
    }

    @AfterEach
    public void clear() throws DataAccessException {
        service.clearEntireDatabase();
    }

    @Test
    public void createUser() throws DataAccessException {
        UserData userData = new UserData("player1", "player1_secure", "player1_email");
        UserData nullUser = new UserData(null, "password", "email");
        UserData nullPassword = new UserData("username", null, "email");
        service.registerUser(userData);
        Assertions.assertEquals(userData, service.getUser("player1"));
        Assertions.assertThrows(AlreadyTakenException.class, () -> service.registerUser(userData));
        Assertions.assertThrows(InvalidRequestException.class, () -> service.registerUser(nullUser));
        Assertions.assertThrows(InvalidRequestException.class, () -> service.registerUser(nullPassword));
        Assertions.assertThrows(InvalidRequestException.class, () -> service.registerUser(null));
    }

    @Test
    public void login() throws DataAccessException {
        UserData userData = new UserData("player1", "player1_secure", "player1_email");
        AuthData authData = service.registerUser(userData);
        Assertions.assertEquals("player1", authData.username());
        LoginData loginData = new LoginData("player1", "player1_secure");
        LoginData nullUsername = new LoginData(null, "password");
        LoginData nullPassword = new LoginData("player1", null);
        LoginData wrongUsername = new LoginData("otherPlayer", "password");
        LoginData wrongPassword = new LoginData("player1", "wrongpassword");
        AuthData authDataLogin = service.login(loginData);
        Assertions.assertEquals("player1", authDataLogin.username());
        Assertions.assertThrows(InvalidRequestException.class, () -> service.login(nullUsername));
        Assertions.assertThrows(InvalidRequestException.class, () -> service.login(nullPassword));
        Assertions.assertThrows(InvalidLoginException.class, () -> service.login(wrongUsername));
        Assertions.assertThrows(InvalidLoginException.class, () -> service.login(wrongPassword));
    }

    @Test
    public void logout() throws DataAccessException {
        UserData userData = new UserData("player1", "player1_secure", "player1_email");
        AuthData authData = service.registerUser(userData);
        Assertions.assertThrows(InvalidAuthToken.class, () -> service.logout("not_a_token"));
        service.logout(authData.authToken());
        Assertions.assertThrows(InvalidAuthToken.class, () -> service.logout(authData.authToken()));
        Assertions.assertThrows(InvalidAuthToken.class, () -> service.logout(null));
    }

    @Test
    public void createGame() throws DataAccessException {
        UserData userData = new UserData("player1", "player1_secure", "player1_email");
        AuthData authData = service.registerUser(userData);
        GameData gameData = new GameData(0, null, null, "chess", new ChessGame());
        GameData nullGameName = new GameData(0, null, null, null, new ChessGame());
        int gameID = service.createGame(gameData, authData.authToken());
        Assertions.assertNotEquals(0, gameID);
        GameData getGame = service.listGames(authData.authToken()).getFirst();
        Assertions.assertEquals(gameData.gameName(), getGame.gameName());

        Assertions.assertThrows(InvalidAuthToken.class, () -> service.createGame(gameData, "wrong_token"));
        Assertions.assertThrows(InvalidAuthToken.class, () -> service.createGame(gameData, null));
        Assertions.assertThrows(InvalidRequestException.class, () -> service.createGame(null, authData.authToken()));
        Assertions.assertThrows(InvalidRequestException.class, () -> service.createGame(nullGameName, authData.authToken()));
    }

    @Test
    public void joinGame() throws DataAccessException {
        UserData user1 = new UserData("player1", "player1_secure", "player1_email");
        UserData user2 = new UserData("player2", "player2_secure", "player2_email");
        AuthData auth1 = service.registerUser(user1);
        AuthData auth2 = service.registerUser(user2);
        GameData gameData = new GameData(0, null, null, "chess", new ChessGame());
        int gameID = service.createGame(gameData, auth1.authToken());
        JoinData joinWhite = new JoinData(ChessGame.TeamColor.WHITE, gameID);
        JoinData joinBlack = new JoinData(ChessGame.TeamColor.BLACK, gameID);
        JoinData nullColor = new JoinData(null, gameID);
        JoinData wrongID = new JoinData(ChessGame.TeamColor.WHITE, 3);
        service.joinGame(joinWhite, auth1.authToken());
        Assertions.assertEquals("player1", service.listGames(auth1.authToken()).getFirst().whiteUsername());
        Assertions.assertThrows(AlreadyTakenException.class, () -> service.joinGame(joinWhite, auth2.authToken()));
        service.joinGame(joinBlack, auth2.authToken());
        Assertions.assertThrows(AlreadyTakenException.class, () -> service.joinGame(joinBlack, auth1.authToken()));
        Assertions.assertEquals("player2", service.listGames(auth2.authToken()).getFirst().blackUsername());
        Assertions.assertThrows(InvalidRequestException.class, () -> service.joinGame(null, auth2.authToken()));
        Assertions.assertThrows(InvalidRequestException.class, () -> service.joinGame(nullColor, auth2.authToken()));
        Assertions.assertThrows(InvalidRequestException.class, () -> service.joinGame(wrongID, auth2.authToken()));
    }
}
