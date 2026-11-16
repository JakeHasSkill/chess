package client;

import com.google.gson.*;
import model.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class ServerFacade {
    private final String url;
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson;

    public ServerFacade(String url) {
        this.url = url;
        gson = new Gson();
    }

    public void clearDatabase() {

    }

    public AuthData register(String username, String password, String email) throws ServerError, UsernameTaken {
        try {
            UserData userData = new UserData(username, password, email);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/user"))
                    .timeout(java.time.Duration.ofMillis(1500)) // TODO: increase timeout for a production build
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(userData)))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return gson.fromJson(response.body(), AuthData.class);
            }
            else if (response.statusCode() == 403) {
                throw new UsernameTaken("Username taken");
            }
            else {
                throw new ServerError("Server Error");
            }
        } catch (URISyntaxException e) {
            throw new ServerError("URL syntax incorrect");
        } catch (IOException e) {
            throw new ServerError("Internal error");
        } catch (InterruptedException e) {
            throw new ServerError("Interrupted");
        }
    }

    public AuthData login(String username, String password) {
        try {
            LoginData loginData = new LoginData(username, password);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url + "/session"))
                    .timeout(java.time.Duration.ofMillis(1500))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(loginData)))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return gson.fromJson(response.body(), AuthData.class);
            }
            else if (response.statusCode() == 401) {
                throw new InvalidCredentials("Incorrect username or password");
            }
            else {
                throw new ServerError("Server Error");
            }
        } catch (URISyntaxException e) {
            throw new ServerError("URL syntax incorrect");
        } catch (IOException e) {
            throw new ServerError("Internal Error");
        } catch (InterruptedException e) {
            throw new ServerError("Interrupted");
        }
    }

    public void logout(String authToken) {

    }

    public ArrayList<GameData> listGames(String authToken) {
        return null;
    }

    public void createGame(String authToken, GameData game) {

    }

    public void joinGame(String authToken, LoginData loginData) {

    }
}
