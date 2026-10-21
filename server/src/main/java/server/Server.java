package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.AlreadyTakenException;
import service.GameLogicService;

public class Server {

    private final Javalin javalin;
    private GameLogicService service;

    public Server() {
        this.service = new GameLogicService();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.delete("/db", this::clearEntireDatabase);
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
            service.registerUser(userData);
            context.status(200); // This should really return 201, but the passoff tests require 200
        } catch (AlreadyTakenException e) {
            context.status(400);
            context.result("{\"message\": \"Error: Username already taken\"}");
        }
    }

    private void clearEntireDatabase(Context context) {
        service.clearEntireDatabase();
    }
}
