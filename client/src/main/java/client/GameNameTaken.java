package client;

public class GameNameTaken extends RuntimeException {
    public GameNameTaken(String message) {
        super(message);
    }
}
