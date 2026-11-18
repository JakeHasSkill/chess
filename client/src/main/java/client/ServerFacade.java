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
        HttpResponse<String> response = makeHttpRequest("/db", null, null, HttpMethod.DELETE);
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return;
        }
        else {
            throw new ServerError("Unable to connect to server");
        }
    }

    public enum HttpMethod {
        POST, GET, DELETE, PUT
    }

    private HttpResponse<String> makeHttpRequest(String path, String jsonBody, String authToken, HttpMethod type) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder();
            builder.uri(new URI(url + path));
            builder.timeout(java.time.Duration.ofMillis(1500)); // TODO: increase timeout for a production build
            HttpRequest.BodyPublisher body;
            if (jsonBody != null) {body = HttpRequest.BodyPublishers.ofString(jsonBody);}
            else {body = HttpRequest.BodyPublishers.noBody();}
            switch (type) {
                case POST -> builder.POST(body);
                case DELETE -> builder.DELETE();
                case GET -> builder.GET();
                case PUT -> builder.PUT(body);
            }
            if (authToken != null) {builder.header("authorization", authToken);}
            HttpRequest request = builder.build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            throw new ServerError("URL syntax incorrect");
        } catch (IOException e) {
            throw new ServerError("Internal error");
        } catch (InterruptedException e) {
            throw new ServerError("Interrupted");
        }
    }

    public AuthData register(String username, String password, String email) throws ServerError, UsernameTaken {
        UserData userData = new UserData(username, password, email);
        HttpResponse<String> response = makeHttpRequest("/user", gson.toJson(userData), null, HttpMethod.POST);
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), AuthData.class);
        }
        else if (response.statusCode() == 403) {
            throw new UsernameTaken("Username taken");
        }
        else {
            throw new ServerError("Server Error");
        }
    }

    public AuthData login(String username, String password) throws InvalidCredentials, ServerError {
        LoginData loginData = new LoginData(username, password);
        HttpResponse<String> response = makeHttpRequest("/session", gson.toJson(loginData), null, HttpMethod.POST);
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), AuthData.class);
        }
        else if (response.statusCode() == 401) {throw new InvalidCredentials("Incorrect username or password");}
        else {throw new ServerError("Server Error");}
    }

    public void logout(String authToken) throws NotLoggedIn, ServerError {
        HttpResponse<String> response = makeHttpRequest("/session", null, authToken, HttpMethod.DELETE);
        if (response.statusCode() >= 200 && response.statusCode() < 300) {return;}
        else if (response.statusCode() == 401) {throw new NotLoggedIn("Invalid AuthToken");}
        else {throw new ServerError("Server Error");}
    }

    public ArrayList<GameData> listGames(String authToken) {
        record GameList(ArrayList<GameData> games) {}
        HttpResponse<String> response = makeHttpRequest("/game", null, authToken, HttpMethod.GET);
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            GameList games = gson.fromJson(response.body(), GameList.class);
            return games.games;
        }
        else if (response.statusCode() == 401) {throw new NotLoggedIn("Invalid AuthToken");}
        else {throw new ServerError("Server Error");}
    }

    public void createGame(String authToken, GameData game) throws NotLoggedIn, ServerError {
        HttpResponse<String> response = makeHttpRequest("/game", gson.toJson(game), authToken, HttpMethod.POST);
        if (response.statusCode() >= 200 && response.statusCode() < 300) {return;}
        else if (response.statusCode() == 401) {throw new NotLoggedIn("Invalid AuthToken");}
        else {throw new ServerError("Server Error");}
    }

    public void joinGame(String authToken, JoinData joinData) throws TeamColorTaken, NotLoggedIn, ServerError {
        HttpResponse<String> response = makeHttpRequest("/game", gson.toJson(joinData), authToken, HttpMethod.PUT);
        if (response.statusCode() >= 200 && response.statusCode() < 300) {return;}
        else if (response.statusCode() == 403) {throw new TeamColorTaken("Team not available");}
        else if (response.statusCode() == 401) {throw new NotLoggedIn("Invalid AuthToken");}
        else {throw new ServerError("Server Error");}
    }
}
