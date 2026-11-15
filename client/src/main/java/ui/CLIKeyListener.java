package ui;

import java.io.IOException;
import java.io.InputStream;

public class CLIKeyListener{
    public enum Key {
        UP, DOWN, LEFT, RIGHT, ENTER, ESCAPE, CHAR
    }

    public record KeyPress(Key key, char character) {}

    /**
     * Waits until a key is pressed and returns the key press
     */
    public KeyPress getNextKeyPress() {
        InputStream in = System.in;
        try {
            while (true) {
                int b = in.read();
                if (b == -1) {new KeyPress(Key.ESCAPE, '\0');} // EOF
                if (b == '\r' || b == '\n') {return new KeyPress(Key.ENTER, '\0');}
                if (b == 27) { // Escape character
                    if (in.available() == 0) {return new KeyPress(Key.ESCAPE, '\0');}
                    // Escape code
                    b = in.read();
                    if (b != '[') {continue;}
                    b = in.read();
                    if (b == 'A') {return new KeyPress(Key.UP, '\0');}
                    if (b == 'B') {return new KeyPress(Key.DOWN, '\0');}
                    if (b == 'C') {return new KeyPress(Key.RIGHT, '\0');}
                    if (b == 'D') {return new KeyPress(Key.LEFT, '\0');}
                    continue;
                }
                return new KeyPress(Key.CHAR, (char) b);
            }
        } catch (IOException e) {
            System.err.println("Input stream closed");
        }
        return new KeyPress(Key.ESCAPE, '\0');
    }
}
