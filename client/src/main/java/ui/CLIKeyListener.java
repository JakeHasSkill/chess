package ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CLIKeyListener implements Runnable{
    public enum Key {
        UP, DOWN, LEFT, RIGHT, ENTER, ESCAPE, CHAR
    }

    public record KeyPressEvent(Key key, char character) {}

    private final BlockingQueue<KeyPressEvent> inputQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    public BlockingQueue<KeyPressEvent> getInputQueue() {
        return inputQueue;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        InputStream in = System.in;
        try {
            while (running) {
                int b = in.read();
                if (b == -1) {break;} // EOF
                if (b == '\r' || b == '\n') {inputQueue.offer(new KeyPressEvent(Key.ENTER, '\0'));}
                if (b == 27) { // Escape character
                    if (in.available() == 0) {
                        inputQueue.offer(new KeyPressEvent(Key.ESCAPE, '\0'));
                    }
                    // Escape code
                    b = in.read();
                    if (b != '[') {continue;}
                    b = in.read();
                    if (b == 'A') {inputQueue.offer(new KeyPressEvent(Key.UP, '\0'));}
                    if (b == 'B') {inputQueue.offer(new KeyPressEvent(Key.DOWN, '\0'));}
                    if (b == 'C') {inputQueue.offer(new KeyPressEvent(Key.RIGHT, '\0'));}
                    if (b == 'D') {inputQueue.offer(new KeyPressEvent(Key.LEFT, '\0'));}
                    continue;
                }
                inputQueue.offer(new KeyPressEvent(Key.CHAR, (char) b));
            }
        } catch (IOException e) {
            System.err.println("Input stream closed");
        }
    }
}
