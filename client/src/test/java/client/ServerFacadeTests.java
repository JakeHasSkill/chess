package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.JoinData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.ArrayList;


public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade serverFacade;
    private static AuthData auth1;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        serverFacade = new ServerFacade("http://localhost:" + port);
        serverFacade.clearDatabase();
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void reset() {
        serverFacade.clearDatabase();
    }

    @Test
    @Order(1)
    public void register() {
        auth1 = serverFacade.register("user1", "longpassword", "myemail");
        Assertions.assertNotNull(auth1);
        Assertions.assertNotNull(auth1.authToken());
        Assertions.assertEquals("user1", auth1.username());
        Assertions.assertThrows(UsernameTaken.class, () -> serverFacade.register("user1", "password", "email"));
    }

    @Test
    @Order(2)
    public void logout() {
        register();
        serverFacade.logout(auth1.authToken());
        Assertions.assertThrows(NotLoggedIn.class, () -> serverFacade.logout(auth1.authToken()));
    }

    @Test
    @Order(3)
    public void login() {
        register();
        serverFacade.logout(auth1.authToken());
        Assertions.assertThrows(InvalidCredentials.class, () -> serverFacade.login("user1", "wrongpassword"));
        auth1 = serverFacade.login("user1", "longpassword");
    }

    @Test
    @Order(4)
    public void createGame() {
        register();
        GameData game = new GameData(0, null, null, "game1", new ChessGame());
        Assertions.assertThrows(NotLoggedIn.class, () -> serverFacade.createGame("notAnAuthToken", game));
        serverFacade.createGame(auth1.authToken(), game);
        Assertions.assertEquals(1, serverFacade.listGames(auth1.authToken()).size());
    }

    @Test
    @Order(5)
    public void listGames() {
        createGame();
        Assertions.assertThrows(NotLoggedIn.class, () -> serverFacade.listGames("NotLoggedIn"));
        ArrayList<GameData> games = serverFacade.listGames(auth1.authToken());
        GameData expectedGame = new GameData(1, null, null, "game1", new ChessGame());
        Assertions.assertEquals(1, games.size());
        Assertions.assertEquals(expectedGame, games.getFirst());
    }

    @Test
    @Order(6)
    public void joinGame() {
        createGame();
        JoinData joinData = new JoinData(ChessGame.TeamColor.WHITE, 1);
        Assertions.assertThrows(NotLoggedIn.class, () -> serverFacade.joinGame("RandomString", joinData));
        serverFacade.joinGame(auth1.authToken(), joinData);
        ArrayList<GameData> games = serverFacade.listGames(auth1.authToken());
        GameData expectedGame = new GameData(1, "user1", null, "game1", new ChessGame());
        Assertions.assertEquals(expectedGame, games.getFirst());
        Assertions.assertThrows(TeamColorTaken.class, () -> serverFacade.joinGame(auth1.authToken(), joinData));
    }

    @AfterAll
    static void stopServer() {
        serverFacade.clearDatabase();
        server.stop();
    }
}
