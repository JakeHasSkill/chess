package ui;

import static ui.EscapeSequences.*;

public class ChessTile extends Option {
    private final String text;
    private final boolean whiteTile;

    public ChessTile(String text, boolean whiteTile) {
        this.text = text;
        this.whiteTile = whiteTile;
    }

    @Override
    public String toString() {
        if (whiteTile) {return SET_BG_COLOR_LIGHT_GREY + text + RESET_ALL_FORMATTING;}
        return text;
    }

    @Override
    public String toStringHighlighted() {
        if (whiteTile) {return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + text + RESET_ALL_FORMATTING;}
        return SET_TEXT_BOLD + text + RESET_ALL_FORMATTING;
    }
}
