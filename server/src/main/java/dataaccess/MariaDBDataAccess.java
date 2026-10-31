package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;

public class MariaDBDataAccess implements DataAccess {
    public MariaDBDataAccess() throws DataAccessException{
        DatabaseManager.createDatabase();
        String databaseSetupCommands = """
                CREATE TABLE IF NOT EXISTS Users (
                `Username` varchar(256) NOT NULL,
                `PasswordHash` TEXT NOT NULL,
                `Email` varchar(256) DEFAULT NULL,
                PRIMARY KEY (`Username`)
                ) CHARSET=utf8mb4
                """;
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(databaseSetupCommands)) {
                preparedStatement.executeUpdate();
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
    public ArrayList<AuthData> getAuthData() {
        return null;
    }

    @Override
    public void deleteAuthData(AuthData authData) {

    }

    @Override
    public void addAuthData(AuthData authData) {

    }

    @Override
    public GameData getGameData(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public int addGameData(GameData gameData) {
        return 0;
    }

    @Override
    public void editGameData(GameData gameData) {

    }

    @Override
    public ArrayList<GameData> getAllGameData() {
        return null;
    }

    @Override
    public void clearEntireDatabase() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String command  = "TRUNCATE Users";
            try (PreparedStatement preparedStatement = connection.prepareStatement(command)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear database");
        }
    }
}
