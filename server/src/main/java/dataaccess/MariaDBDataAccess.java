package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;

public class MariaDBDataAccess implements DataAccess {
    public MariaDBDataAccess() throws DataAccessException{
        DatabaseManager.createDatabase();
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS Users (
                `Username` varchar(256) NOT NULL,
                `PasswordHash` TEXT NOT NULL,
                `Email` varchar(256) DEFAULT NULL,
                PRIMARY KEY (`Username`)
                ) CHARSET=utf8mb4;
                """;
        String createAuthDataTable = """
                CREATE TABLE IF NOT EXISTS AuthData (
                `AuthToken` CHAR(60) NOT NULL,
                `Username` varchar(256) NOT NULL,
                PRIMARY KEY (`AuthToken`)
                ) CHARSET=utf8mb4;
                """;
        String createGameDataTable = """
                CREATE TABLE IF NOT EXISTS GameData (
                `GameID` int AUTO_INCREMENT PRIMARY KEY,
                `WhiteUsername` varchar(256),
                `BlackUsername` varchar(256),
                `GameName` varchar(256) NOT NULL,
                `ChessGame` TEXT NOT NULL
                ) CHARSET=utf8mb4;
                """;
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(createUsersTable)) {
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(createAuthDataTable)) {
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(createGameDataTable)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to setup database");
        }
    }

    @Override
    public UserData getUserData(String username) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String command = "SELECT Username, PasswordHash, Email FROM Users WHERE Username=?";
            try (PreparedStatement statement = connection.prepareStatement(command)) {
                statement.setString(1, username);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {return null;}
                    String passwordHash = result.getString("PasswordHash");
                    String email = result.getString("Email");
                    return new UserData(username, passwordHash, email);
                }
            }
        }
        catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error getting user data");
        }
    }

    @Override
    public void addUser(UserData userData) throws DataAccessException {
        try (Connection sql = DatabaseManager.getConnection()) {
            String command = "INSERT INTO Users (Username, PasswordHash, Email) VALUES (?, ?, ?)";
            try (PreparedStatement statement = sql.prepareStatement(command)) {
                statement.setString(1, userData.username());
                statement.setString(2, userData.password());
                statement.setString(3, userData.email());
                statement.executeUpdate();
            }
        }
        catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error adding user");
        }
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String command = "SELECT AuthToken, Username FROM AuthData WHERE AuthToken=?";
            try (PreparedStatement statement = connection.prepareStatement(command)) {
                statement.setString(1, authToken);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {return null;}
                    String username = result.getString("Username");
                    return new AuthData(authToken, username);
                }
            }
        }
        catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error getting auth data");
        }
    }

    @Override
    public void deleteAuthData(AuthData authData) throws DataAccessException {
        try (Connection sql = DatabaseManager.getConnection()) {
            String command = "DELETE FROM AuthData WHERE AuthToken=?";
            try (PreparedStatement statement = sql.prepareStatement(command)) {
                statement.setString(1, authData.authToken());
                statement.executeUpdate();
            }
        }
        catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error deleting auth data");
        }
    }

    @Override
    public void addAuthData(AuthData authData) throws DataAccessException {
        try (Connection sql = DatabaseManager.getConnection()) {
            String command = "INSERT INTO AuthData (AuthToken, Username) VALUES (?, ?)";
            try (PreparedStatement statement = sql.prepareStatement(command)) {
                statement.setString(1, authData.authToken());
                statement.setString(2, authData.username());
                statement.executeUpdate();
            }
        }
        catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error adding auth data");
        }
    }

    @Override
    public GameData getGameData(int gameID) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String command = "SELECT * FROM GameData WHERE GameID=?";
            try (PreparedStatement statement = connection.prepareStatement(command)) {
                statement.setInt(1, gameID);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {return null;}
                    String whiteUsername = result.getString("WhiteUsername");
                    String blackUsername = result.getString("BlackUsername");
                    String gameName = result.getString("GameName");
                    String chessGame = result.getString("ChessGame");
                    Gson gson = new Gson();
                    ChessGame game = gson.fromJson(chessGame, ChessGame.class);
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                }
            }
        }
        catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error getting auth data");
        }
    }

    @Override
    public int addGameData(GameData gameData) throws DataAccessException {
        try (Connection sql = DatabaseManager.getConnection()) {
            String command = "INSERT INTO GameData (WhiteUsername, BlackUsername, GameName, ChessGame) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = sql.prepareStatement(command, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, gameData.whiteUsername());
                statement.setString(2, gameData.blackUsername());
                statement.setString(3, gameData.gameName());
                Gson gson = new Gson();
                statement.setString(4, gson.toJson(gameData.game()));
                statement.executeUpdate();
                ResultSet result = statement.getGeneratedKeys();
                if (!result.next()) {throw new DataAccessException("Error adding game data");}
                return result.getInt(1);
            }
        }
        catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error adding game data");
        }
    }

    @Override
    public void editGameData(GameData gameData) throws DataAccessException {
        try (Connection sql = DatabaseManager.getConnection()) {
            String command = "UPDATE GameData SET WhiteUsername=COALESCE(?, WhiteUsername), " +
                    "BlackUsername=COALESCE(?, BlackUsername), ChessGame=? WHERE GameID=?";
            try (PreparedStatement statement = sql.prepareStatement(command)) {
                statement.setString(1, gameData.whiteUsername());
                statement.setString(2, gameData.blackUsername());
                Gson gson = new Gson();
                statement.setString(3, gson.toJson(gameData.game()));
                statement.setInt(4, gameData.gameID());
                statement.executeUpdate();
            }
        }
        catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error adding game data");
        }
    }

    @Override
    public ArrayList<GameData> getAllGameData() throws DataAccessException {
        ArrayList<Integer> gameIDs = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection()) {
            String command = "SELECT GameID FROM GameData";
            try (PreparedStatement statement = connection.prepareStatement(command)) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        gameIDs.add(result.getInt(1));
                    }
                }
            }
        }
        catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error getting auth data");
        }
        ArrayList<GameData> allGames = new ArrayList<>();
        for (int i : gameIDs) {
            allGames.add(getGameData(i));
        }
        return allGames;
    }

    @Override
    public void clearEntireDatabase() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String clearUsers = "TRUNCATE Users";
            String clearAuthData = "TRUNCATE AuthData";
            String clearGameData = "TRUNCATE GameData";
            try (PreparedStatement preparedStatement = connection.prepareStatement(clearUsers)) {
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(clearAuthData)) {
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(clearGameData)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear database");
        }
    }
}
