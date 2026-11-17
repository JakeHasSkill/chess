package client;

public class NotLoggedIn extends RuntimeException {
    public NotLoggedIn(String message) {
        super(message);
    }
}
