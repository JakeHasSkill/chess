package ui;

import static ui.EscapeSequences.*;

public class Button extends Option{
    private final String text;
    private final Screen destination;

    public Button(String text, Screen destination) {
        this.text = text;
        this.destination = destination;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public String toStringHighlighted() {
        return SET_TEXT_BOLD + text + RESET_ALL_FORMATTING;
    }

    public Screen getDestination() {
        return destination;
    }
}
