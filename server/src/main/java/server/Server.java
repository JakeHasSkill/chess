package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private GameLogicService service;

    public Server() {
        this.service = new GameLogicService();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.delete("/db", this::clearEntireDatabase);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context context) {
        Gson gson = new Gson();
        UserData userData = gson.fromJson(context.body(), UserData.class);
        try {
            AuthData authData = service.registerUser(userData);
            context.status(200); // This would be better as 201 created
            context.result("{\"username\":\"" + authData.username() + "\",\"authToken\": \"" + authData.authToken() + "\"}");
        } catch (AlreadyTakenException e) {
            context.status(403); // This would be better as 409 conflict
            context.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        } catch (InvalidRequestException e) {
            context.status(400);
            context.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    private void login(Context context) {
        Gson gson = new Gson();
        LoginData loginData = gson.fromJson(context.body(), LoginData.class);
        try {
            AuthData authData = service.login(loginData);
            context.status(200);
            context.result("{\"username\":\"" + authData.username() + "\",\"authToken\": \"" + authData.authToken() + "\"}");
        } catch (InvalidLoginException e) {
            context.status(401);
            context.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        } catch (InvalidRequestException e) {
            context.status(400);
            context.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    private void logout(Context context) {
        String authToken = context.header("authorization");
        try {
            service.logout(authToken);
            context.status(200);
        } catch (InvalidAuthToken e) {
            context.status(401);
            context.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }

    private void clearEntireDatabase(Context context) {
        service.clearEntireDatabase();
    }
}
