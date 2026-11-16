package client;

public class UsernameTaken extends RuntimeException {
    public UsernameTaken(String message) {
        super(message);
    }
}
